package com.example.demo.Utils;

public class CRC16Modbus {
    /**
     *
     * @param bytes，待校验的bytes类型的数组
     * @return 生成的CRC校验码
     */
    private static String getCRC2(byte[] bytes) {
        int CRC = 0xffff;
        int POLYNOMIAL = 0xa001;
        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x0000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) == 1) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        //高低位转换，看情况使用（譬如本人这次对led彩屏的通讯开发就规定校验码高位在前低位在后，也就不需要转换高低位)
        CRC = ( (CRC & 0x0000FF00) >> 8) | ( (CRC & 0x000000FF ) << 8);
        String hexString = Integer.toHexString(CRC);
        int length = hexString.length();
        if (length < 4) {
            for (int k = 0; k < 4 - length; k++) {
                hexString = "0" + hexString;
            }
        }
        return hexString;
    }

    /**
     *
     * @param hexString,16进制的字符串
     * @return 转换成的字节数组
     */
    public static byte[] HexString2Bytes(String hexString){
        byte[]  destByte = new byte[hexString.length()/2];
        int j=0;
        for (int i=0; i<destByte.length;i++){
            byte high = (byte)(Character.digit(hexString.charAt(j),16)&0xff);
            byte low = (byte)(Character.digit(hexString.charAt(j+1),16)&0xff);
            destByte[i] = (byte)(high<<4|low);
            j = j+2;
        }
        return destByte;
    }

    /**
     *
     * @param hexString
     * @param CRC
     * @return 返回校验结果
     */
    public static boolean checkCRC16(String hexString, String CRC){
        byte[] bytes  =  HexString2Bytes(hexString);
        String  strCRC = getCRC2(bytes);
        //防止hex大小写问题
        return  (strCRC != null && strCRC.trim().toLowerCase().equals(CRC.toLowerCase().trim()));
    }

    /**
     * 生成CRC
     */
    public static String makeCRC(String hexString) {
        byte[] bytes = HexString2Bytes(hexString);
        String strCRC = getCRC2(bytes);
        if (strCRC != null) {
            return strCRC.toLowerCase();
        }
        return null;
    }
}
