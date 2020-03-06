package com.clobotics.sockettest.socket;

/**
 * Author: Aya
 * Date: 2020/2/10
 * Description:
 */
public class SocketConst {
    public static final int AUTO_GROUP = 1001; //自动分组
    public static final int PACK = 1002; //打包
    public static final int BACKUP = 1003; //备份至U盘
    public static final int GET_STATUS = 1004; //获取风机的状态
    public static final int GET_TURBINE = 1005; //获取某个风机的缩略图
    public static final int GET_IMAGE = 1006; //获取原图
    public static final int CONFIRM_TURBINE = 1007; //发送某个风机的分组确认结果
    public static final int POWER_OFF = 1008; //关闭机载电脑

    public static final int SOCKET_TIMEOUT = 30000; //Socket连接超时时间30s

}