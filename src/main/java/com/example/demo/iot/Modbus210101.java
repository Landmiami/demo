package com.example.demo.iot;

import io.netty.util.internal.StringUtil;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 解析设备Modbus协议，1.0版
 */
public class Modbus210101 {


//    String str16 = "0304300003427F5A0A4089AC0C3F7EDCE63EB2E14900180002003F436D2B0E3E8C7FF8000000003C973AE2000000003CC32E31E836";

    //网关
    public String gateway = "A00001";

    List<DeviceDataUtil> devDataList = new ArrayList<>();

    //开始
    public String man(String str) {
        if (str == null || StringUtil.isNullOrEmpty(str)) {
            return "数据为null";
        }
        //String 转byte[]
        byte[] bytes = ModbusUtil.hexStringToByte(str);

        //校验
        if (!ModbusUtil.getCRC16Modbus(bytes)) {
            return "校验不正确";
        }

        //开始
        //从机地址
        String cjdz = String.format("%04X", bytes[0]);
        String ttt = "";
        //判断功能码
        switch (bytes[1]) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                function03(bytes);
                break;
            case 4:
                function04(bytes);
                break;
            case 5:
                break;
            case 6:
                break;
            case 10:
                break;
            default:
                return "错误功能码";
        }
//        for (DeviceDataUtil dev : devDataList ) {
//            System.out.println("设备号："+dev.getDeviceCode()+"---设备类型："+dev.getDeviceStatic()+"----值："+dev.getDeviceValue());
//        }

        return devDataList.toString();
    }

    /**
     * 功能码 03
     * @param bytes
     */
    public void function03(byte[] bytes){

        //判断单相还是三相
        int pdABC = bytes[2];

        ///解析电箱序列号，ASCII码
        //厂商代号
        String hy = ModbusUtil.hex2Str(String.format("%X",bytes[3]))+ModbusUtil.hex2Str(String.format("%X",bytes[4]));
//        System.out.println(hy);
        //电流类型
        String electricType = ModbusUtil.hex2Str(String.format("%X",bytes[5]));
//        System.out.println(electricType);
        //电流大小
        String electricSize = ModbusUtil.hex2Str(String.format("%X",bytes[6]))+ModbusUtil.hex2Str(String.format("%X",bytes[7]))+ModbusUtil.hex2Str(String.format("%X",bytes[8]));
//        System.out.println(electricSize);
        //极数
        String pole = ModbusUtil.hex2Str(String.format("%X",bytes[9]))+ModbusUtil.hex2Str(String.format("%X",bytes[10]));
//        System.out.println(pole);
        //漏电流类型
        String leakageType = ModbusUtil.hex2Str(String.format("%X",bytes[11]));
//        System.out.println(leakageType);
        //机器类型
        String machineType = ModbusUtil.hex2Str(String.format("%X",bytes[12]));
//        System.out.println(machineType);
        //硬件版本
        String hardwareVersion = ModbusUtil.hex2Str(String.format("%X",bytes[13]));
//        System.out.println(hardwareVersion);
        //软件版本
        String softwareVersion = ModbusUtil.hex2Str(String.format("%X",bytes[14]));
//        System.out.println(softwareVersion);
        //序列号
        String serialNumber= ModbusUtil.hex2Str(String.format("%X",bytes[15]))+ModbusUtil.hex2Str(String.format("%X",bytes[16]))
                +ModbusUtil.hex2Str(String.format("%X",bytes[17]))+ModbusUtil.hex2Str(String.format("%X",bytes[18]))
                +ModbusUtil.hex2Str(String.format("%X",bytes[19]))+ModbusUtil.hex2Str(String.format("%X",bytes[20]))
                +ModbusUtil.hex2Str(String.format("%X",bytes[21]))+ModbusUtil.hex2Str(String.format("%X",bytes[22]));
//        System.out.println(serialNumber);

        ///解析保持寄存器
        //远程，本地
        int ybPattern = (bytes[24]|bytes[23]<<8);
//        System.out.println(ybPattern);
        //过压保护
        int overVoltageValue = (bytes[26]|bytes[25]<<8);
//        System.out.println(overVoltageValue);
        //过压报警
        int overVoltageAlarm = (bytes[28]|bytes[27]<<8);
//        System.out.println(overVoltageAlarm);
        //上报过压报警
        int reportOverVoltageAlarm  = (bytes[30]|bytes[29]<<8);
//        System.out.println(reportOverVoltageAlarm);

        //欠压保护
        int underVoltageValue = Integer.parseInt(String.format("%X",bytes[31])+String.format("%X",bytes[32]),16);
//        System.out.println(underVoltageValue);
        //欠压报警
        int underVoltageAlarm = (bytes[34]|bytes[33]<<8);
//        System.out.println(underVoltageAlarm);
        //上报欠压报警
        int reportUnderVoltageAlarm  = (bytes[36]|bytes[35]<<8);
//        System.out.println(reportUnderVoltageAlarm);
        //过流保护
        int overCurrentValue = (bytes[38]|bytes[37]<<8);
//        System.out.println(overCurrentValue);
        //过流报警
        int overCurrentAlarm = (bytes[40]|bytes[39]<<8);
//        System.out.println(overCurrentAlarm);
        //上报过流报警
        int reportOverCurrentAlarm  = (bytes[42]|bytes[41]<<8);
//        System.out.println(reportOverCurrentAlarm);
        //过温保护
        int overTemperatureValue = (bytes[44]|bytes[43]<<8);
//        System.out.println(overTemperatureValue);
        //过温报警
        int overTemperatureAlarm = (bytes[46]|bytes[45]<<8);
//        System.out.println(overTemperatureAlarm);
        //上报过温报警
        int reportOverTemperatureAlarm  = (bytes[48]|bytes[47]<<8);
//        System.out.println(reportOverTemperatureAlarm);

        //故障码
        int fault = (bytes[50]|bytes[49]<<8);
//        System.out.println(fault);

        //判断是否三相
        if(bytes[2]==58){
            //上报三相不平衡报警
            int reportBalanceABC = (bytes[52]|bytes[51]<<8);
            //上报缺相报警
            int reportLackABC = (bytes[54]|bytes[53]<<8);
            //上报三相相序报警
            int reportOrderABC = (bytes[56]|bytes[55]<<8);

            //相不平衡报警(百分比，%)
            int balanceABCAlarm = (bytes[58]|bytes[57]<<8);
            //相不平衡保护(百分比，%)
            int balanceABCProtect = (bytes[60]|bytes[59]<<8);

        }



    }

    /**
     * 功能码04
     */
    public void function04(byte[] bytes) {
        List<String> list = new ArrayList<String>();

        //寄存器数量
        String zjs = String.format(String.valueOf(bytes[2]), 16);
        //从机地址
        String cjdz = String.format(String.valueOf(bytes[3] + bytes[4]), 16);

        //保留两位小数，四舍五入
        NumberFormat formatter = new DecimalFormat("0.00");
        //有功,保存两位小数
        String yggl = formatter.format(ModbusUtil.byteToFloat(bytes, 5));
        list.add(yggl);

        //无功,保存两位小数
        String wggl = formatter.format(ModbusUtil.byteToFloat(bytes, 9));
        list.add(wggl);

        //功率因数
        String hlys = formatter.format(ModbusUtil.byteToFloat(bytes, 13));
        list.add(wggl);

        //总用电量
        String zydl = formatter.format(ModbusUtil.byteToFloat(bytes, 17));
        list.add(wggl);

        //温度
        short wdd = (short) (((bytes[21] & 0x00FF) << 8) | (0x00FF & bytes[22]));
        list.add(wggl);

        //漏电流
        int ld = ((bytes[23] & 0xff) << 8 | (bytes[24] & 0xff));
        list.add(ld + "");

        //额定工作电流
        int edgzdl = ((bytes[25] & 0xff) << 8 | (bytes[26] & 0xff));
        list.add(edgzdl + "");

        //电压
        String dyaA = formatter.format(ModbusUtil.byteToFloat(bytes, 27));
        list.add(dyaA);

        //电流
        String dlA = formatter.format(ModbusUtil.byteToFloat(bytes, 31));
        list.add(dlA);

        //电流电压
        if(bytes[2]==48){
            System.out.println("三相");
            String dyaB = formatter.format(ModbusUtil.byteToFloat(bytes, 35));
            String dlB = formatter.format(ModbusUtil.byteToFloat(bytes, 39));
            String dyaC = formatter.format(ModbusUtil.byteToFloat(bytes, 43));
            String dlC = formatter.format(ModbusUtil.byteToFloat(bytes, 47));
            list.add(dyaB);
            list.add(dlB);
            list.add(dyaC);
            list.add(dlC);
        }

        DeviceDataUtil devData;
        for (int i = 0, length = list.size(); i < length; i++) {
            devData = new DeviceDataUtil();
            devData.setDeviceCode(gateway + cjdz);
            devData.setDeviceStatic(i + 1);
            devData.setDeviceValue(list.get(i));
            devDataList.add(devData);
        }


    }


}
