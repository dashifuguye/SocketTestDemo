package com.clobotics.sockettest.bean;

/**
 * @author: Aya
 * @date: 2019/12/18
 * @decription:发送确认过的风机数据
 */
public class SendTurbineConfirmed extends Base {

    public TurbineConfirmed data;

    public TurbineConfirmed getData() {
        return data;
    }

    public void setData(TurbineConfirmed data) {
        this.data = data;
    }
}
