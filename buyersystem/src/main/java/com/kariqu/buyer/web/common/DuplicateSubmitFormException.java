package com.kariqu.buyer.web.common;

/**
 * User: kyle
 * Date: 13-2-20
 * Time: 下午2:56
 */
public class DuplicateSubmitFormException extends RuntimeException {

    public  DuplicateSubmitFormException(){
        super();

    }

    public DuplicateSubmitFormException(String msg){
        super(msg);
    }
}
