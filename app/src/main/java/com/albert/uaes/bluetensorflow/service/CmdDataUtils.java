package com.albert.uaes.bluetensorflow.service;

import android.app.admin.DeviceAdminInfo;

public class CmdDataUtils {


    public static int getCmd(byte[] data){
        int cmdId = data[3];
        switch (cmdId){
            case 0x0101:
                //Connection confirm

                break;
            case 0x0102:
                //Return DK authorization status

                break;
            case 0x0103:
                //Exchange certificate from APP to Vehicle

                break;
            case 0x0104:
                //Exchange certificate from Vehicle to APP

                break;
            case 0x0105:
                //Return APP certificate verify result(failed)

                break;
            case 0x0106:
                //Exchange factor from APP to Vehicle

                break;
            case 0x0107:
                //Return parse result of factorA

                break;
            case 0x0108:
                //Exchange factor from Vehicle to APP

                break;
            case 0x0109:
                //Return parse result of factorV

                break;
            case 0x010A:
                //Send Digital key to Vehicle

                break;
            case 0x010B:
                //Return DK verify result

                break;
                default:
                    break;
        }
        return cmdId;
    }

    public static boolean checkDkAuthStatus(byte[] data){

        if (data[6] == 0x100A){
            //认证失败
            return false;
        }else if ((data[6] == 0x100B) ||(data[6] == 0x100C)){
            //认证成功
            return true;
        }
        return false;
    }

    /**
     * 证书验证
     * @param data
     * @return
     */
    public static boolean getCmdCertVerifyResult(byte[] data){
        if (data[6] == 0x100A){
            //认证失败
            return false;
        }else if ((data[6] == 0x100B) ||(data[6] == 0x100C)){
            //认证成功
            return true;
        }
        return false;
    }

    /**
     * 反馈App因子解析结果
     * @param data
     * @return
     */
    public static boolean getCmdParseResultA(byte[] data){
        if (data[6] == 0x100A){
            //认证失败
            return false;
        }else if ((data[6] == 0x100B) ||(data[6] == 0x100C)){
            //认证成功
            return true;
        }
        return false;
    }

    /**
     * 反馈车辆因子解析结果
     * @param data
     * @return
     */
    public static boolean getCmdParseResultV(byte[] data){
        if (data[6] == 0x100A){
            //认证失败
            return false;
        }else if ((data[6] == 0x100B) ||(data[6] == 0x100C)){
            //认证成功
            return true;
        }
        return false;
    }

    /**
     * 反馈数字钥匙验证结果
     * @param data
     * @return
     */
    public static boolean getCmdDkStatus(byte[] data){
        if (data[6] == 0x100A){
            //认证失败
            return false;
        }else if ((data[6] == 0x100B) ||(data[6] == 0x100C)){
            //认证成功
            return true;
        }
        return false;
    }

    /**
     * 发送指令
     * @param data
     */
    public static void sendCmd(byte[] data){

    }

    /**
     * 生成ConnectKey
     * @return
     */
    public static byte[] createConnectKey(){
        byte[] random = {(byte)0x4D,(byte)0x67,(byte)0x41,(byte)0x5C,(byte) 0x88,(byte)0xD3,(byte)0x31,(byte)0x8E};
        byte[] randomv2 = new byte[16];
        for (int i = 0;i<random.length;i++){
            int height;
            int low;
            height = ((random[i] & 0xf0) >> 4);
            low = (random[i] & 0x0f);
            randomv2[16-i] = Byte.parseByte(Integer.toHexString((char)height));
            randomv2[16-i-1] = Byte.parseByte(Integer.toHexString((char)low));
        }
        return randomv2;
    }

    public static byte[] createData(int cmdId){
        byte[] data = new byte[]{};

        data[0] = (byte)0xFE;
        data[1] = (byte)0x01;
        data[3] = Byte.parseByte(Integer.toHexString(cmdId));


        data[5] = Byte.parseByte(Long.toHexString(System.currentTimeMillis()));


        return data;
    }
}
