package com.klm.persist;

/**
 * Created by IntelliJ IDEA.
 * User: gang-liu
 * Date: 12/1/11
 * Time: 10:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class CSPersistException extends Exception{
    public CSPersistException() {
    }

    public CSPersistException(String message) {
        super(message);
    }

    public CSPersistException(String message, Throwable cause) {
        super(message, cause);
    }

    public CSPersistException(Throwable cause) {
        super(cause);
    }

//    public CSPersistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
//        super(message, cause, enableSuppression, writableStackTrace);
//    }
}
