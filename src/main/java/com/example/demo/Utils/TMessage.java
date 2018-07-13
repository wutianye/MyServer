package com.example.demo.Utils;

import java.io.Serializable;

/**
 * auther: lyx
 * createtime : 7.13
 * function: return message
 */
public class TMessage implements Serializable {
    public static final int CODE_SUCCESS = 1, CODE_FAILURE = -1;
    private int code;
    private String info;
    private Object data;

    public TMessage(int code, String info, Object data) {
        this.code = code;
        this.info = info;
        this.data = data;
    }

    public TMessage(int code, String info) {
        this.code = code;
        this.info = info;
    }

    public TMessage(Object data) {
        this.data = CODE_SUCCESS;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "TMessage{" +
                "code=" + code +
                ", info='" + info + '\'' +
                ", data=" + data +
                '}';
    }
}
