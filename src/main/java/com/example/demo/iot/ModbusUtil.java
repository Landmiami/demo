package com.example.demo.iot;

import io.netty.util.internal.StringUtil;

/**
 * Modbus工具类
 */
public class ModbusUtil {

    /**
     * String 转 byte[]
     *
     * @param hexString
     * @return
     */
    public static byte[] hexStringToByte(String hexString) {
        if (hexString == null || StringUtil.isNullOrEmpty(hexString)) {
            return null;
        }
        //转大写，去空格
        hexString = hexString.toUpperCase().replace(" ", "");
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * byte[]转String
     * @param hexByte
     * @return
     */
    public static String hexByteToString(byte[] hexByte){
        if(hexByte == null){
            return null;
        }
        StringBuilder hex = new StringBuilder();

        for (byte b : hexByte) {
            hex.append(HEXES[(b >> 4) & 0x0F]);
            hex.append(HEXES[b & 0x0F]);
        }
        return hex.toString();
    }
    private static final char[] HEXES = {
            '0', '1', '2', '3',
            '4', '5', '6', '7',
            '8', '9', 'A', 'B',
            'C', 'D', 'E', 'F'
    };
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 校验，生成CRC16-Modbus校验码，判断
     * @param
     * @return
     */
    public static boolean getCRC16Modbus(byte[] bytes){
        int CRC = 0x0000FFFF;
        int POLYNOMIAL = 0x0000A001;
        //长度-2，去除校验为
        int i, j;
        for (i = 0; i < bytes.length-2 ; i++) {
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
//        CRC = ((CRC & 0x0000FF00) >> 8) | ((CRC & 0x000000FF) << 8);
        String crc1 = String.format("%04X",CRC);
        String crc2 =  String.format("%X",bytes[bytes.length-1])+String.format("%X",bytes[bytes.length-2]);
        if(crc1.equals(crc2)){
            return true;
        }
        return false;
    }

    /**
     * 在原有的byte上生成校验
     * @param bytes
     * @return
     */
    public static byte[] setCRC16Modbus(byte[] bytes){
        int CRC = 0x0000FFFF;
        int POLYNOMIAL = 0x0000A001;
        byte[] bt = new byte[2];
        //长度-2，去除校验为
        int i, j;
        for (i = 0; i < bytes.length ; i++) {
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
        CRC = ((CRC & 0x0000FF00) >> 8) | ((CRC & 0x000000FF) << 8);

        bt = hexStringToByte(String.format("%04X",CRC));

        bytes[bt.length]=bt[0];
        bytes[bt.length+1]=bt[1];
        return bytes;
    }

    /**
     * 计算数值
     * @param b
     * @param index
     * @return
     */
    public static float byteToFloat(byte[] b, int index) {
        int l;
        l = b[index + 3];
        l &= 0xff;
        l |= ((long) b[index + 2] << 8);
        l &= 0xffff;
        l |= ((long) b[index + 1] << 16);
        l &= 0xffffff;
        l |= ((long) b[index + 0] << 24);
        return Float.intBitsToFloat(l);
    }


    /**
     * 16进制转ASCII
     *
     * @param hex
     * @return
     */
    public static String hex2Str(String hex) {
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 0; i < hex.length() - 1; i += 2) {
            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char) decimal);
            temp.append(decimal);
        }
        return sb.toString();
    }

}
