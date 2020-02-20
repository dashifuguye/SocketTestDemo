package com.clobotics.sockettest;

import java.util.List;

/**
 * @author: Aya
 * @date: 2019/12/18
 * @decription:确认过的风机数据
 */
public class TurbineConfirmed extends TurbineInfo {
    private List<PathConfirmed> paths;

    public TurbineConfirmed() {

    }

    public static class PathConfirmed {
        private String pathName;
        private List<ImageInfo> images;

        public PathConfirmed(String pathName, List<ImageInfo> images) {
            this.pathName = pathName;
            this.images = images;
        }

        public String getPathName() {
            return pathName;
        }

        public void setPathName(String pathName) {
            this.pathName = pathName;
        }

        public List<ImageInfo> getImages() {
            return images;
        }

        public void setImages(List<ImageInfo> images) {
            this.images = images;
        }
    }

    public List<PathConfirmed> getPaths() {
        return paths;
    }

    public void setPaths(List<PathConfirmed> paths) {
        this.paths = paths;
    }

}
