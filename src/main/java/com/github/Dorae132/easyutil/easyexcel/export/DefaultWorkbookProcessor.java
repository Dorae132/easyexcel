package com.github.Dorae132.easyutil.easyexcel.export;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.Workbook;

import com.github.Dorae132.easyutil.easyexcel.ExcelProperties;
import com.github.Dorae132.easyutil.easyexcel.common.EasyExcelException;

/**
 * the default impl of 
 * @author Dorae
 *
 */
public class DefaultWorkbookProcessor implements IWorkbookProcessor<File>{

    @Override
    public File process(Workbook wb, ExcelProperties<?, File> properties) throws Exception {
        // 1.创建目录
        validateFileDir(properties.getFilePath());
        File file = new File(new StringBuilder(properties.getFilePath()).append(properties.getFileName()).toString());
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            wb.write(out);
        } catch (Exception e) {
            // do nothing
            throw new EasyExcelException(e);
        } finally {
            if (out != null) {
                out.close();
            }
            if (wb != null) {
                wb.close();
            }
        }
        return file;
    }

    /**
     * 校验目录是否存在
     * 
     * @param filePath
     */
    private static synchronized void validateFileDir(String filePath) {
        File tempDir = new File(filePath);
        if (!tempDir.exists() && !tempDir.isDirectory()) {
            tempDir.mkdir();
        }
    }
}
