package com.example.demo.Test;

/**
 * Created with IntelliJ IDEA.
 * User: yunxi
 * Date: 2018/7/18
 * date: 19:04
 * Description: No Description
 */
public class Data {

     private double value; // value
     private String date;  // data

    public Data(double value, String date) {
        this.value = value;
        this.date = date;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
