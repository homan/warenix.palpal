package org.dyndns.warenix.palpal.sdk.model;

/**
 * Created by warenix on 9/17/14.
 */
public class Picture {

    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data {

        private int height;
        private int width;
        private String url;

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }

        public String getUrl() {
            return url;
        }
    }
}
