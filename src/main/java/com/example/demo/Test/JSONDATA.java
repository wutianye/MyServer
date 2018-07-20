package com.example.demo.Test;

/**
 * Created with IntelliJ IDEA.
 * User: yunxi
 * Date: 2018/7/18
 * Time: 19:04
 * Description: No Description
 */
public class JSONDATA {

    private  Data wind;
    private  Data temperature;
    private  Data humidity;
    private  Data gas;

    public JSONDATA(Data wind, Data temperature, Data humidity, Data gas) {
        this.wind = wind;
        this.temperature = temperature;
        this.humidity = humidity;
        this.gas = gas;
    }

    public Data getWind() {
        return wind;
    }

    public void setWind(Data wind) {
        this.wind = wind;
    }

    public Data getTemperature() {
        return temperature;
    }

    public void setTemperature(Data temperature) {
        this.temperature = temperature;
    }

    public Data getHumidity() {
        return humidity;
    }

    public void setHumidity(Data humidity) {
        this.humidity = humidity;
    }

    public Data getGas() {
        return gas;
    }

    public void setGas(Data gas) {
        this.gas = gas;
    }
}
