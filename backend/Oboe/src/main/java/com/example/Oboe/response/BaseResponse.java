package com.example.Oboe.response;

public class BaseResponse<T> {
    private String message;
    private String code;
    private T data;

    public BaseResponse() {}

    public BaseResponse(String message, String code, T data) {
        this.message = message;
        this.code = code;
        this.data = data;
    }

    // Getter & Setter
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
