package com.clobotics.sockettest.bean;

/**
 * @author: Aya
 * @date: 2019/12/18
 * @decription:用于获得某个风机缩略图的发送的类
 */
public class SendTurbineInfo extends Base {

    public TurbineInfo data;

    public TurbineInfo getData() {
        return data;
    }

    public void setData(TurbineInfo data) {
        this.data = data;
    }
}
