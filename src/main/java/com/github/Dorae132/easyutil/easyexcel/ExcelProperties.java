package com.github.Dorae132.easyutil.easyexcel;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.Dorae132.easyutil.easyexcel.export.ExcelCol;
import com.github.Dorae132.easyutil.easyexcel.export.IDataSupplier;
import com.github.Dorae132.easyutil.easyexcel.export.IWorkbookProcessor;
import com.github.Dorae132.easyutil.easyexcel.read.IReadDoneCallBackProcessor;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * preperties if the excel which would to be extported
 * 
 * @author Dorae
 *
 * @param <T> the data type
 * @param <R> the type of the return value that excepted
 */
public class ExcelProperties<T, R> {
	
	// sheet名称和表头值
	private String sheetName = "sheet1";

	// 表头List
	private List<String> titles;
	
	// 对象集合
	private List<T> dataList;
	
	// data类的field
	private List<Field> fields;
	
	// fieldName -> field
	private Map<String, Field> fieldNameMap;
	
	// 文件地址
	private String filePath = "./easyexcel/";

	// 文件名
	private String fileName = new StringBuilder(UUID.randomUUID().toString()).append(".xlsx").toString().replace("-", "");

	// 行偏移
	private int rowOffset = 0;

	// 列偏移
	private int colOffset = 0;

	// 缓冲
	private int rowAccessWindowsize = 100;
	
	// 读模式，线程等待最大时长/秒
	private int readThreadWaitTime = 300;
	
	// 并行读线程池，默认实现异步ExcelUtils
	private ThreadPoolExecutor readThreadPool = null;
	
	// 并行写线程池
	private ThreadPoolExecutor writeThreadPool = null;
	
	private IWorkbookProcessor<R> wbProcessor;
	
	// 读模式结束回调
	private IReadDoneCallBackProcessor readDoneCallBackProcessor;

	private IDataSupplier<T> dataSupplier;
	
	private Class dataClazz;
	
	public static ExcelProperties produceCommonProperties(String sheetName, List<?> dataList, String filePath,
			String fileName, int colOffset, Class dataClazz, int rowAccessWindowsize, IWorkbookProcessor wbProcessor) throws Exception {
		return new ExcelProperties<>(sheetName, dataList, filePath, fileName, 0, colOffset, dataClazz, rowAccessWindowsize, wbProcessor, null);
	}
	
	public static ExcelProperties produceAppendProperties(String sheetName, String filePath, String fileName,
			int colOffset, Class dataClazz, int rowAccessWindowsize, IWorkbookProcessor wbProcessor, IDataSupplier<?> dataSupplier)
			throws Exception {
		return new ExcelProperties<>(sheetName, null, filePath, fileName, 0, colOffset, dataClazz, rowAccessWindowsize, wbProcessor, dataSupplier);
	}
	
	public static ExcelProperties produceReadProperties(String filePath, String fileName) {
		ExcelProperties excelProperties = new ExcelProperties();
		excelProperties.setFilePath(filePath);
		excelProperties.setFileName(fileName);
		return excelProperties;
	}
	/**
	 * 
	 * @param sheetName
	 * @param titleToFieldObjs 表头与字段的对应关系
	 * @param dataList 数据集
	 * @param filePath 文件路径（默认./liveunionExcels/）
	 * @param fileName 文件名（默认随机）
	 * @param excludeFields 要排除的字段名
	 * @param rowOffset 行偏移
	 * @param colOffset 列偏移
	 * @param processor 结果processor
	 * @param dataSupplier 数据源
	 * @param dataClazz 资源类型
	 * @param rowAccessWindowsize 缓冲大小
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private ExcelProperties(String sheetName, List<T> dataList, String filePath, String fileName, int rowOffset,
			int colOffset, Class dataClazz, int rowAccessWindowsize, IWorkbookProcessor wbProcessor,
			IDataSupplier<T> dataSupplier) throws Exception {
		super();
		// 1.check
		if (CollectionUtils.isEmpty(dataList) && dataClazz == null) {
			// 保障能获取到资源类型
			throw new RuntimeException("dataList和资源数据类型不能同时为空！");
		}
		// 2.common field
		if (StringUtils.isNotBlank(sheetName)) {
			this.sheetName = sheetName;
		}
		this.dataList = dataList;
		if (StringUtils.isNotBlank(filePath)) {
			this.filePath = filePath;
		}
		if (StringUtils.isNotBlank(fileName)) {
			int sufixIndex = fileName.lastIndexOf(".");
			sufixIndex = sufixIndex == -1 ? fileName.length() : sufixIndex;
			this.fileName = new StringBuilder(fileName)
					.insert(sufixIndex, "_" + UUID.randomUUID().toString().replace("-", "")).toString();
		}
		this.rowOffset = rowOffset;
		this.colOffset = colOffset;
		this.wbProcessor = wbProcessor;
		this.dataSupplier = dataSupplier;
		this.rowAccessWindowsize = (rowAccessWindowsize > 100 ? rowAccessWindowsize : 100);
		// 3.special field
		this.dataClazz = dataClazz == null ? dataList.get(0).getClass() : dataClazz;
		Field[] declaredFields = this.dataClazz.getDeclaredFields();
		Arrays.sort(declaredFields, new Comparator<Field>() {

			@Override
			public int compare(Field o1, Field o2) {
				ExcelCol o1Annotation = o1.getAnnotation(ExcelCol.class);
				ExcelCol o2Annotation = o2.getAnnotation(ExcelCol.class);
				if (o1Annotation == null && o2Annotation == null) {
					return 0;
				} else if (o1Annotation != null
						&& o2Annotation != null) {
					if (o1Annotation.order() == o2Annotation.order()) {
						return 0;
					} else {
						return o1Annotation.order() > o2Annotation.order() ? 1 : -1;
					}
				} else if (o1Annotation != null
						&& o2Annotation == null) {
					return 1;
				} else {
					return -1;
				}
			}
		});
		this.fields = Lists.newArrayListWithCapacity(declaredFields.length);
		this.fieldNameMap = Maps.newHashMapWithExpectedSize(declaredFields.length);
		this.titles = Lists.newArrayListWithCapacity(declaredFields.length);
		for (Field declaredField : declaredFields) {
			ExcelCol excelCol = declaredField.getAnnotation(ExcelCol.class);
			if (excelCol == null) {
				continue;
			}
			this.fieldNameMap.put(declaredField.getName(), declaredField);
			titles.add(excelCol.title());
			this.fields.add(declaredField);
		}
		this.fieldNameMap = Stream.of(declaredFields).collect(Collectors.toMap(Field::getName, e -> e));
	}

	private ExcelProperties() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getRowAccessWindowsize() {
		return rowAccessWindowsize;
	}

	public void setRowAccessWindowsize(int rowAccessWindowsize) {
		this.rowAccessWindowsize = rowAccessWindowsize;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public List<String> getTitles() {
		return titles;
	}

	public void setTitles(List<String> titles) {
		this.titles = titles;
	}

	public List<T> getDataList() {
		return dataList;
	}

	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public Map<String, Field> getFieldNameMap() {
		return fieldNameMap;
	}

	public void setFieldNameMap(Map<String, Field> fieldNameMap) {
		this.fieldNameMap = fieldNameMap;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getRowOffset() {
		return rowOffset;
	}

	public void setRowOffset(int rowOffset) {
		this.rowOffset = rowOffset;
	}

	public int getColOffset() {
		return colOffset;
	}

	public void setColOffset(int colOffset) {
		this.colOffset = colOffset;
	}

	public IWorkbookProcessor<R> getWbProcessor() {
        return wbProcessor;
    }

    public void setWbProcessor(IWorkbookProcessor<R> wbProcessor) {
        this.wbProcessor = wbProcessor;
    }

    public IDataSupplier<T> getDataSupplier() {
		return dataSupplier;
	}

	public void setDataSupplier(IDataSupplier<T> dataSupplier) {
		this.dataSupplier = dataSupplier;
	}

	public Class getDataClazz() {
		return dataClazz;
	}

	public void setDataClazz(Class dataClazz) {
		this.dataClazz = dataClazz;
	}

    public int getReadThreadWaitTime() {
        return readThreadWaitTime;
    }

    public void setReadThreadWaitTime(int readThreadWaitTime) {
        this.readThreadWaitTime = readThreadWaitTime;
    }

    public ThreadPoolExecutor getReadThreadPool() {
        return readThreadPool;
    }

    public void setReadThreadPool(ThreadPoolExecutor readThreadPool) {
        this.readThreadPool = readThreadPool;
    }

    public ThreadPoolExecutor getWriteThreadPool() {
        return writeThreadPool;
    }

    public void setWriteThreadPool(ThreadPoolExecutor writeThreadPool) {
        this.writeThreadPool = writeThreadPool;
    }

    public IReadDoneCallBackProcessor getReadDoneCallBackProcessor() {
        return readDoneCallBackProcessor;
    }

    public void setReadDoneCallBackProcessor(IReadDoneCallBackProcessor readDoneCallBackProcessor) {
        this.readDoneCallBackProcessor = readDoneCallBackProcessor;
    }
	
}
