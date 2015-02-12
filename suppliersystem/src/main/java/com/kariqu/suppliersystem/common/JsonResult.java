package com.kariqu.suppliersystem.common;

import com.kariqu.common.json.JsonUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * web层使用的Json结果集
 * User: Asion
 * Date: 12-6-1
 * Time: 上午9:46
 */
public class JsonResult {


    /**
     * 请求处理结果 ，成功
     */
    public static final boolean SUCCESS = true;
    /**
     * 请求处理结果 ，失败
     */
    public static final boolean FAILURE = false;




    /**
     * 返回结果集为数据的key
     */
    public static final String RESULT_TYPE_LIST = "list" ;
    /**
     * 返回结果集的类型（单个对象）的key.
     */
    public static final String RESULT_TYPE_SINGLE_OBJECT = "obj" ;
    /**
     *  返回结果集的类型（分页使用，数据总条数），即前端json数据的key.
     */
    public static final String RESULT_TYPE_TOTAL_COUNT = "totalCount" ;



    /**
     * 是否成功
     */
    private boolean success = false;

    /**
     * 消息
     */
    private String msg = "";


    /**
     * 数据
     */
    private Map<String, Object> data = new HashMap<String, Object>();


    public JsonResult() {
    }

    /**
     * 以成功标志来构造
     *
     * @param success
     */
    public JsonResult(boolean success) {
        this.success = success;
    }

    /**
     * 以消息来构造
     *
     * @param msg
     */
    public JsonResult(String msg) {
        this.msg = msg;
    }

    /**
     * 以成功标志和消息来构造
     *
     * @param success
     * @param msg
     */
    public JsonResult(boolean success, String msg) {
        this.success = success;
        this.msg = msg;
    }

    /**
     * 返回json对象
     *
     * @return
     */
    public String toJson() {
        return JsonUtil.objectToJson(this);
    }

    /**
     * 直接写入客户端
     *
     * @param response
     * @throws java.io.IOException
     */
    public void toJson(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(toJson());
    }


    /**
     * 加入数据项
     *
     * @param name
     * @param value
     */
    public JsonResult addData(String name, Object value) {
        data.put(name, value);
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public JsonResult setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
