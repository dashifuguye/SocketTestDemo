package com.clobotics.sockettest;

/**
 * Author: Aya
 * Date: 2020/2/18
 * Description:
 */
public class UsbConnectStateEvent {
    public boolean isConnected;
    public boolean isAdbAvailable;

    public UsbConnectStateEvent(boolean isConnected, boolean isAdbAvailable) {
        this.isConnected = isConnected;
        this.isAdbAvailable = isAdbAvailable;
    }
}
