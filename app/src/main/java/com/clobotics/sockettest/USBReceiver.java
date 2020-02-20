package com.clobotics.sockettest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

/**
 * Author: Aya
 * Date: 2020/2/18
 * Description:
 */
public class USBReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 这里可以拿到插入的USB设备对象
        UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        switch (intent.getAction()) {
            case UsbManager.ACTION_USB_DEVICE_ATTACHED: // 插入USB设备
                EventBus.getDefault().post(new ReceivedMsgEvent("ACTION_USB_DEVICE_ATTACHED：" + usbDevice.getDeviceName() + "," + usbDevice.getManufacturerName() + "," + usbDevice.getProductName() + "," + usbDevice.getSerialNumber() + "," + usbDevice.getVersion()
                        + "," + usbDevice.getDeviceId() + "," + usbDevice.getVendorId()));
                break;
            case UsbManager.ACTION_USB_DEVICE_DETACHED: // 拔出USB设备
                EventBus.getDefault().post(new ReceivedMsgEvent("ACTION_USB_DEVICE_DETACHED 拔出usb：" + usbDevice.getDeviceName()));
                break;
            case MyUsbManager.ACTION_USB_STATE:
                if (intent.getExtras().getBoolean(MyUsbManager.USB_CONNECTED)) {
                    // usb 插入
                    if (intent.getExtras().getBoolean(MyUsbManager.USB_FUNCTION_ADB)){
                        EventBus.getDefault().post(new UsbConnectStateEvent(true, true));
                    }else {
                        EventBus.getDefault().post(new UsbConnectStateEvent(true, false));
                    }
                    Toast.makeText(context, "usb共享网络"+ intent.getExtras().getBoolean(MyUsbManager.USB_FUNCTION_RNDIS), Toast.LENGTH_LONG).show();
                } else {
                    //   usb 拔出
                    EventBus.getDefault().post(new UsbConnectStateEvent(false, false));
                }
                break;
            case UsbManager.ACTION_USB_ACCESSORY_ATTACHED:
                EventBus.getDefault().post(new ReceivedMsgEvent("ACCESSORY插入"));
                break;
            case UsbManager.ACTION_USB_ACCESSORY_DETACHED:
                EventBus.getDefault().post(new ReceivedMsgEvent("ACCESSORY拔出"));
                break;
            default:
                break;
        }
    }
}

