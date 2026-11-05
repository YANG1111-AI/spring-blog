package com.zcy.ResponseData;

/**
 * 文章响应数据封装类
 */
public class ArticleResponseData<T> {
    private boolean success;
    private String message;
    private T data;

    public ArticleResponseData() {}

    public ArticleResponseData(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> ArticleResponseData<T> ok(String message) {
        return new ArticleResponseData<>(true, message, null);
    }

    public static <T> ArticleResponseData<T> ok(String message, T data) {
        return new ArticleResponseData<>(true, message, data);
    }

    public static <T> ArticleResponseData<T> fail(String message) {
        return new ArticleResponseData<>(false, message, null);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ArticleResponseData{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}