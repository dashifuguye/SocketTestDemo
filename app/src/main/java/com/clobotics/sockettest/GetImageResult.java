package com.clobotics.sockettest;

/**
 * @author: Aya
 * @date: 2019/12/18
 * @decription:
 */
public class GetImageResult extends Base {

    public Image data;

    public class Image {
        private int id;
        private String name;
        private String data;
        private String base64;

        public Image(int id, String name, String data, String base64) {
            this.id = id;
            this.name = name;
            this.data = data;
            this.base64 = base64;
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

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getBase64() {
            return base64;
        }

        public void setBase64(String base64) {
            this.base64 = base64;
        }
    }

    public Image getData() {
        return data;
    }
}
