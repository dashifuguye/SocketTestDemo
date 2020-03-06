package com.clobotics.sockettest.event;

/**
 * @author: Aya
 * @date: 2019/12/26
 * @decription:
 */
public class ReceivedMsgEvent {
    public String msg;

    public ReceivedMsgEvent(String msg) {
        this.msg = msg;
    }
}
