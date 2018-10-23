# 你的ExcelUtil简单、高效、易扩展吗
> Author: Dorae  
> Date: 2018年10月23日12:30:15
> 转载请注明出处

----

## 一、背景

最近接到了和Excel导出相关的需求，但是：

1. 项目中的excelutil工具类会存在安全问题，而且无法扩展；
2. 一些开源的Excel工具太过重量级，并且不不能完全适合业务定制；
3. 于是乎产生了**easyExcel**（巧合，和阿里的easeExcel重名了😀）。

#### HSS、XSS

如果数据量较小的话，这种方式并不会存在问题，但是当数据量比较大时，存在非常严重的问题：

1. OOM（因为数据并没有被即使的写入磁盘）；
2. 耗时（串行化）；
3. HSS对单sheet的数据量限制（65535），XSS为100万+。

    
    HSSFWorkbook workbook = new HSSFWorkbook();
    HSSFSheet sheet = workbook.createSheet(sheetName);
    // 创建表头
    ...
    // 写入数据
    for (...) {
        HSSFRow textRow = sheet.createRow(rowIndex);
        for (...) {
            HSSFCell textcell = textRow.createCell(colIndex);
            textcell.setCellValue(value);
        }
    }

#### SXSS

为了改善上出方案的问题，SXSS采用了缓冲的方式，即按照某种策略定期刷盘。从而解决了OOM与耗时太长的问题。使用姿势：

	InputStream inputStream = new FileInputStream(file);
	XSSFWorkbook xssfWorkbook = new XSSFWorkbook(inputStream);
	SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(xssfWorkbook,properties.getRowAccessWindowsize());
	Sheet sheet = sxssfWorkbook.getSheet(properties.getSheetName());
	if (sheet == null) {
		sheet = sxssfWorkbook.createSheet(properties.getSheetName());
	}
	// 创建表头
	...
	// 写入数据
	for (...) {
	    Row row = sheet.createRow(row);
	    row.setValue(value);
	}
	FileOutputStream outputStream = new FileOutputStream(file);
	sxssfWorkbook.write(outputStream);

## 二、EasyUtil

虽然SXSS解决了OMM与耗时的问题，但是使用起来不太方便，会造成很多重复代码，于是产生了**EasyUtil**，在介绍之前我们先思考几个问题：

1. excel操作有哪些公用逻辑可以抽离；
2. 业务需求是什么，有哪些逻辑可以抽离；
3. 如何开放较好的扩展点。

#### 架构

按照上述的几个问题，EasyUtil-1.0的架构如图1-1所示，其中绿色部分为扩展点。

1. IFillSheet为填充excel的接口，可以扩展自定义格式，目前在FillSheetModeEnums中提供了两种（建议使用APPENDMODE）；
2. AbstractDataSupplier为需要根据业务实现的数据获取接口，类似于stream；
3. IExcelProcesor生成的file的后处理接口，比如可以将文件上传到某个公共空间。

<center>
    <img src="http://images.cnblogs.com/cnblogs_com/Dorae/1325782/o_easyexcel.jpg"/>
</center>
<center>
    图 1-1
</center>

#### 使用姿势

##### git

[戳这里](https://github.com/Dorae132/easyexcel)

##### pom

    <groupId>com.dorae132.easyutil</groupId>
	<artifactId>easyexcel</artifactId>
	<version>1.0.0</version>
	
##### test

    static class TestValue {
		@ExcelCol(title = "姓名")
		private String name;
		@ExcelCol(title = "年龄", order = 1)
		private String age;
		@ExcelCol(title = "学校", order = 3)
		private String school;
		@ExcelCol(title = "年级", order = 2)
		private String clazz;

		public TestValue(String name, String age, String school, String clazz) {
			super();
			this.name = name;
			this.age = age;
			this.school = school;
			this.clazz = clazz;
		}
	}

	private List<TestValue> getData(int count) {
		List<TestValue> dataList = Lists.newArrayListWithCapacity(10000);
		for (int i = 0; i < count; i++) {
			dataList.add(new TestValue("张三" + i, "age: " + i, null, "clazz: " + i));
		}
		return dataList;
	}
	
	@Test
	public void testAppend() throws Exception {
		List<TestValue> dataList = getData(10000);
		long start = System.currentTimeMillis();
		ExcelProperties<TestValue, File> properties = ExcelProperties.produceAppendProperties("",
				"C:\\Users\\Dorae\\Desktop\\ttt\\", "append.xlsx", 0, TestValue.class, 0, null, new AbstractDataSupplier<TestValue>() {
					private int i = 0;

					@Override
					public Pair<List<TestValue>, Boolean> getDatas() {
						boolean hasNext = i < 10;
						i++;
						return Pair.of(dataList, hasNext);
					}
				});
		File file = (File) ExcelUtils.excelExport(properties, FillSheetModeEnums.APPEND_MODE.getValue());
		System.out.println("apendMode: " + (System.currentTimeMillis() - start));
	}
	
#### 效果

1. 10万行4列数据耗时3s左右（我的渣渣笔记本）；
2. 对内存基本没消耗（当然需要合适的缓冲参数）。

## 三、To do

1. 导出excel和获取数据的接口并行化；
2. 完善utils。