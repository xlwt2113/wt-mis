package com.wt.mis.core.exception;


/**
 * @author mac
 */
public class AppException extends  Exception {

    public AppException(){
        super();
    }

    public AppException(String message){
        super(message);
    }

    public AppException(String message, Throwable cause){
        super(message,cause);
    }

    public AppException(Throwable cause) {
        super(cause);
    }
}
