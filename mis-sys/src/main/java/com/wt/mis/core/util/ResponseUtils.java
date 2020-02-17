package com.wt.mis.core.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * message 为弹窗提示输出的文字，谨慎填写
 * data 数据主体的字段
 * code 返回http状态码
 *
 * @author cl
 */
public class ResponseUtils {

    private ResponseUtils() {
    }


    public static String toJson(String message,HttpStatus status,Object obj){
        Map<String, Object> result = new HashMap<>();
        result.put("message", message);
        result.put("data", obj);
        result.put("code", status.value());
        return JsonUtils.objectToJson(result);
    }

    /**
     * 以json格式返回无权限信息
     * @param message
     * @param obj
     * @return
     */
    public static String forbiddenJson(String message,Object obj){
        return toJson(message,HttpStatus.FORBIDDEN,obj);
    }

    /**
     * 以JSON格式返回正确信息
     * @param message
     * @param data
     * @return
     */
    public static String okJson(String message, Object data){
        return toJson(message,HttpStatus.OK,data);
    }


    /**
     * 以JSON格式返回错误信息
     * @param message
     * @param data
     * @return
     */
    public static String errorJson(String message, Object data) {
        return toJson(message,HttpStatus.METHOD_NOT_ALLOWED,data);
    }




    public static ResponseEntity ok(String message, Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("message", message);
        result.put("data", data);
        result.put("code", HttpStatus.OK.value());
        return ResponseEntity.ok().body(result);
    }

    public static ResponseEntity ok(String message) {
        return ok(message, "");
    }

    public static ResponseEntity error(String message, Object data, HttpStatus status) {
        Map<String, Object> result = new HashMap<>();
        result.put("message", message);
        result.put("data", data);
        result.put("code", status.value());
        return ResponseEntity.status(status).body(result);
    }

    public static ResponseEntity error(String message, Object data) {
        return error(message, data, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static ResponseEntity error(String message) {
        return error(message, "", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
