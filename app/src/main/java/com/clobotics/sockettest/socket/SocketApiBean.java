package com.clobotics.sockettest.socket;

/**
 * Author: Aya
 * Date: 2020/4/1
 * Description:
 */
public class SocketApiBean {
    private int sum;
    private int index;
    private StringBuilder data;

    public SocketApiBean(int sum, int index, StringBuilder data) {
        this.sum = sum;
        this.index = index;
        this.data = data;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public StringBuilder getData() {
        return data;
    }

    public void setData(StringBuilder data) {
        this.data = data;
    }
}
