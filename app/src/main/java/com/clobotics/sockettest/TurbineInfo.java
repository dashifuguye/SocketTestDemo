package com.clobotics.sockettest;

import android.widget.BaseExpandableListAdapter;

/**
 * @author: Aya
 * @date: 2019/12/18
 * @decription:用于获得某个风机缩略图的发送的类
 */

public class TurbineInfo{
    private String windFarmId;
    private String turbineName;

    public TurbineInfo(String windFarmId, String turbineName) {
        this.windFarmId = windFarmId;
        this.turbineName = turbineName;
    }

    public TurbineInfo() {
    }

    public String getWindFarmId() {
        return windFarmId;
    }

    public void setWindFarmId(String windFarmId) {
        this.windFarmId = windFarmId;
    }

    public String getTurbineName() {
        return turbineName;
    }

    public void setTurbineName(String turbineName) {
        this.turbineName = turbineName;
    }
}