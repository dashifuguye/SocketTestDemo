package com.clobotics.sockettest.bean;

import java.util.List;

/**
 * @author: Aya
 * @date: 2019/12/18
 * @decription:
 */
public class GetStatusResult extends Base {

    private Status data;

    public class Status {
        private String hostname;
        private List<TurbineState> turbines;

        public class TurbineState {
            private String windFarmId;
            private String turbineName;
            private int state;
            private int progress;
            private List<LogError> logErrors;

            public TurbineState(String windFarmId, String turbineName, int state, int progress, List<LogError> logErrors) {
                this.windFarmId = windFarmId;
                this.turbineName = turbineName;
                this.state = state;
                this.progress = progress;
                this.logErrors = logErrors;
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

            public int getState() {
                return state;
            }

            public void setState(int state) {
                this.state = state;
            }

            public int getProgress() {
                return progress;
            }

            public void setProgress(int progress) {
                this.progress = progress;
            }

            public List<LogError> getLogErrors() {
                return logErrors;
            }

            public void setLogErrors(List<LogError> logErrors) {
                this.logErrors = logErrors;
            }

            public class LogError{
                private String time;
                private int code;

                public LogError(String time, int code) {
                    this.time = time;
                    this.code = code;
                }

                public String getTime() {
                    return time;
                }

                public void setTime(String time) {
                    this.time = time;
                }

                public int getCode() {
                    return code;
                }

                public void setCode(int code) {
                    this.code = code;
                }

            }
        }

        public Status(String hostname, List<TurbineState> turbines) {
            this.hostname = hostname;
            this.turbines = turbines;
        }

        public String getHostname() {
            return hostname;
        }

        public void setHostname(String hostname) {
            this.hostname = hostname;
        }

        public List<TurbineState> getTurbines() {
            return turbines;
        }

        public void setTurbines(List<TurbineState> turbines) {
            this.turbines = turbines;
        }
    }

    public void setData(Status data) {
        this.data = data;
    }

    public Status getData() {
        return data;
    }
}
