package com.clobotics.sockettest.bean;

/**
 * @author: Aya
 * @date: 2019/12/18
 * @decription:发送照片信息用来获得原图
 */
public class SendImageInfo extends Base {

    public ImageInfo data;

    public ImageInfo getData() {
        return data;
    }

    public void setData(ImageInfo data) {
        this.data = data;
    }
}
