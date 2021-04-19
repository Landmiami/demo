package com.example.demo.netty;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.sql.Array;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.example.demo.iot.Modbus210101;
import com.example.demo.iot.ModbusUtil;
import com.example.demo.util.Map_ValueGetKey;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringEncoder;

public class EchoServer {



    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.option(ChannelOption.SO_BACKLOG, 1024);
            sb.group(group, bossGroup) // 绑定线程池
                    .channel(NioServerSocketChannel.class) // 指定使用的channel
                    .localAddress(this.port)// 绑定监听端口
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 绑定客户端连接时候触发操作

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            System.out.println("信息：有一客户端链接到本服务端");
//                            System.out.println("IP:" + ch.localAddress().getHostName() + "  ");
//                            System.out.print("Port:" + ch.localAddress().getPort());
//                            System.out.println("报告完毕");

                            ch.pipeline().addLast(new StringEncoder(Charset.forName("UTF-8")));
                            ch.pipeline().addLast(new EchoServerHandler()); // 客户端触发操作
                            ch.pipeline().addLast(new ByteArrayEncoder());
                        }
                    });
            ChannelFuture cf = sb.bind().sync(); // 服务器异步创建绑定
            System.out.println(EchoServer.class + " 启动正在监听： " + cf.channel().localAddress());
            cf.channel().closeFuture().sync(); // 关闭服务器通道
        } finally {
            group.shutdownGracefully().sync(); // 释放线程池资源
            bossGroup.shutdownGracefully().sync();
        }
    }



//    public static void main(String[] args) throws Exception {
//        new EchoServer(8888).start(); // 启动
//    }





    public void text1(){
        String s = "3E8C7FF8";
        System.out.println(s);
        byte[] bytes = hexStringToBytes(s);

        Float value = byte2float(bytes, 0);
        System.out.println("16进制浮点数转10进制=" + value);

        short shortAlt = (short) (Integer.valueOf("0002", 16) & 0xffff);
        System.out.println("有符号===" + shortAlt);

        int intDcmAlt = Integer.parseInt("003F", 16);
        System.out.println("无符号===" + intDcmAlt);

        long lvar = -24;
        String lhex = Long.toHexString(lvar);
        System.out.println("十进制===转16进制有符号" + lhex);

        String str16 = "03 04 30 00 03 42 7F 5A 0A 40 89 AC 0C 3F 7E DC E6 3E B2 E1 49 00 18 00 02 00 3F 43 6D 2B 0E 3E 8C 7F F8 00 00 00 00 3C 97 3A E2 00 00 00 00 3C C3 2E 31";
        str16 = str16.replace(" ", "");
        byte[] bts = hexStringToByte(str16);
        System.out.println("----------------" + getCRC(bts));

        Modbus210101 mdb = new Modbus210101();
        ModbusUtil modbusUtil = new ModbusUtil();

    }

    public static void main(String[] args) throws Exception {
        byte[] byte1 = new byte[]{(byte) 0x3F, (byte) 0xE9, (byte) 0x2B, (byte) 0xCD};
        //保留两位小数，四舍五入
        NumberFormat formatter = new DecimalFormat("0.00");
        String zydl = formatter.format(ModbusUtil.byteToFloat(byte1, 0));

        System.out.println(zydl+"-------------------------------------------");

        HashMap map = new HashMap();
        map.put("1", "a");
        map.put("2", "b");
        map.put("3", "c");
        map.put("4", "c");
        map.put("5", "e");
        Map_ValueGetKey mvg = new Map_ValueGetKey(map);
        System.out.println(mvg.getKey("c"));
        List<String> list = new ArrayList<>();
        list = (List<String>) mvg.getKey("c");
        for (String str:list ) {
            System.out.println(str);
        }

        String st = "12345678";
        System.out.println(st);

        System.out.println(st.substring(7,8));

    }



    public static String conver2HexStr(byte[] b) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            result.append(Long.toString(b[i] & 0xff, 2));
        }
        return result.toString().substring(0, result.length() - 1);
    }

    public static int byteGetInt(byte bt1,byte bt2){
        return (bt1 & 0xff) << 8 | (bt2 & 0xff);
    }


    public void mon01(){
        String bw = "01 01 01 7D 91 A9";    //7D   0111 1101
        Modbus210101 mdb = new Modbus210101();
        byte[] byt = ModbusUtil.hexStringToByte(bw);

        StringBuffer bin= new StringBuffer(Integer.toBinaryString(byt[3]));

        String binaryString = Integer.toBinaryString(byt[3]);//1111
        int binaryInt = Integer.parseInt(binaryString);//1111

        System.out.println(binaryInt);
        System.out.println("1---"+(byt[3]&0x01));
        System.out.println("2---"+(byt[3]&0x02));
        System.out.println("3---"+(byt[3]&0x04));
        System.out.println("4---"+(byt[3]&0x08));
        System.out.println("5---"+(byt[3]&0x10));
        System.out.println("6---"+(byt[3]&0x20));
        System.out.println("7---"+((byt[3]&0x40)==0?0:1));
        System.out.println("8---"+(byt[3]&0x80));
    }


    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
                    16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }



    public static String getCRC(byte[] bytes) {
        int CRC = 0x0000ffff;
        int POLYNOMIAL = 0x0000a001;

        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        CRC = ( (CRC & 0x0000FF00) >> 8) | ( (CRC & 0x000000FF ) << 8);
        return String.format("%04X", CRC);
    }


    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 倒序转byte
     *
     * @param hexString
     * @return
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[length - 1 - i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
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

    public static float byte2float(byte[] b, int index) {
        int l;
        l = b[index + 0];
        l &= 0xff;
        l |= ((long) b[index + 1] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 2] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 3] << 24);
        return Float.intBitsToFloat(l);
    }

}
