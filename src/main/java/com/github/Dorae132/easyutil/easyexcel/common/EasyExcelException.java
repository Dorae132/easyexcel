package com.github.Dorae132.easyutil.easyexcel.common;

public class EasyExcelException extends RuntimeException {
    private static final long serialVersionUID = -20778884061304669L;

    public EasyExcelException() {
        super();
    }

    public EasyExcelException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public EasyExcelException(String message, Throwable cause) {
        super(message, cause);
    }

    public EasyExcelException(String message) {
        super(message);
    }

    public EasyExcelException(Throwable cause) {
        super(cause);
    }

}
