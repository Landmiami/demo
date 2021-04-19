package com.example.demo.iot;

public class DeviceDataUtil {
    String deviceCode;
    int deviceStatic;
    String DeviceValue;

    public String getDeviceCode() {
        return deviceCode;
    }

    public int getDeviceStatic() {
        return deviceStatic;
    }

    public String getDeviceValue() {
        return DeviceValue;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public void setDeviceStatic(int deviceStatic) {
        this.deviceStatic = deviceStatic;
    }

    public void setDeviceValue(String deviceValue) {
        DeviceValue = deviceValue;
    }
}
