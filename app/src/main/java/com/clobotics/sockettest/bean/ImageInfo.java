package com.clobotics.sockettest.bean;

/**
 * @author: Aya
 * @date: 2019/12/18
 * @decription:照片信息用来获得原图
 */

public class ImageInfo extends TurbineInfo {
    private String inspectionTime;
    private int id;
    private String name;
    private String date;

    public ImageInfo() {
    }

    public String getInspectionTime() {
        return inspectionTime;
    }

    public void setInspectionTime(String inspectionTime) {
        this.inspectionTime = inspectionTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ImageInfo{" +
                "windFarmId='" + getWindFarmId() + '\'' +
                ", turbineName='" + getTurbineName() + '\'' +
                ", inspectionTime='" + inspectionTime + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
