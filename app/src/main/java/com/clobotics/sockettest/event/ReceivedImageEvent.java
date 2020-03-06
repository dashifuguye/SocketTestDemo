package com.clobotics.sockettest.event;

/**
 * @author: Aya
 * @date: 2019/12/26
 * @decription:
 */
public class ReceivedImageEvent {
    public String name;
    public String data;

    public ReceivedImageEvent(String name, String data) {
        this.name = name;
        this.data = data;
    }
}
