package com.clobotics.sockettest;

import java.util.List;

/**
 * @author: Aya
 * @date: 2019/12/18
 * @decription:
 */
public class GetTurbineResult extends Base {

    private Turbine data;

    public static class Turbine {
        private String windFarmId;
        private String turbineName;
        private List<Path> paths;

        public static class Path {
            private String pathName;
            private boolean isLocked;
            private List<Inspection> inspections;

            public static class Inspection {
                private String inspectionTime;
                private List<Image> thumbnails;
                private boolean isSelected;

                public static class Image {
                    private int id;
                    private String name;
                    private String date;
                    private String base64;
                    private String inspectionTime;
                    private boolean isSelected;

                    public Image(int id, String name, String date, String base64, String inspectionTime, boolean isSelected) {
                        this.id = id;
                        this.name = name;
                        this.date = date;
                        this.base64 = base64;
                        this.inspectionTime = inspectionTime;
                        this.isSelected = isSelected;
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

                    public String getBase64() {
                        return base64;
                    }

                    public void setBase64(String base64) {
                        this.base64 = base64;
                    }

                    public String getInspectionTime() {
                        return inspectionTime;
                    }

                    public void setInspectionTime(String inspectionTime) {
                        this.inspectionTime = inspectionTime;
                    }

                    public boolean isSelected() {
                        return isSelected;
                    }

                    public void setSelected(boolean selected) {
                        isSelected = selected;
                    }
                }

                public Inspection(String inspectionTime, List<Image> thumbnails, boolean isSelected) {
                    this.inspectionTime = inspectionTime;
                    this.thumbnails = thumbnails;
                    this.isSelected = isSelected;
                }

                public void clearSelectedState() {
                    this.isSelected = false;
                    for (int i = 0; i < this.getThumbnails().size(); i++) {
                        this.getThumbnails().get(i).setSelected(false);
                    }
                }

                public String getInspectionTime() {
                    return inspectionTime;
                }

                public void setInspectionTime(String inspectionTime) {
                    this.inspectionTime = inspectionTime;
                }

                public List<Image> getThumbnails() {
                    return thumbnails;
                }

                public void setThumbnails(List<Image> thumbnails) {
                    this.thumbnails = thumbnails;
                }

                public boolean isSelected() {
                    return isSelected;
                }

                public void setSelected(boolean selected) {
                    isSelected = selected;
                }
            }

            public Path(String pathName, boolean isLocked, List<Inspection> inspections) {
                this.pathName = pathName;
                this.isLocked = isLocked;
                this.inspections = inspections;
            }

            public String getPathName() {
                return pathName;
            }

            public void setPathName(String pathName) {
                this.pathName = pathName;
            }

            public boolean isLocked() {
                return isLocked;
            }

            public void setLocked(boolean locked) {
                isLocked = locked;
            }

            public List<Inspection> getInspections() {
                return inspections;
            }

            public void setInspections(List<Inspection> inspections) {
                this.inspections = inspections;
            }
        }

        public Turbine(String windFarmId, String turbineName, List<Path> paths) {
            this.windFarmId = windFarmId;
            this.turbineName = turbineName;
            this.paths = paths;
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

        public List<Path> getPaths() {
            return paths;
        }

        public void setPaths(List<Path> paths) {
            this.paths = paths;
        }
    }

    public Turbine getData() {
        return data;
    }
}
