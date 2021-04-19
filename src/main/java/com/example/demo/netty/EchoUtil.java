package com.example.demo.netty;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class EchoUtil {

    static Map<String, ChannelHandlerContext> map = new HashMap<>();
    static int num = 1;
    public void setMap(ChannelHandlerContext chc){
        map.put("code"+num,chc);
        System.out.println("++++++++++添加数据："+"code"+num++);

        //打印
        JSONArray array_test = new JSONArray();
        array_test.add(map);
        System.out.println("---------------------------------");
        System.out.println(array_test);

    }
    public void readDevData(String code,String data){
        if(code == null || data == null){
            return;
        }
        try {
            ChannelHandlerContext ctx = map.get(code);
            InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();

            System.out.println("发送数据："+ "  IP :" + insocket.getAddress().getHostAddress()  + " 端口:" + insocket.getPort() +"-----"+ data);

            ctx.writeAndFlush(Unpooled.copiedBuffer(hexStringToByte(data)));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static byte[] hexStringToByte(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }



}
