package org.dyndns.warenix.palpal.sdk.model;

/**
 * Created by warenix on 8/23/14.
 */
public class AppInfo extends BaseResponse {

    /**
     * graph id. this is the app id: 702309629818158
     */
    private String id;

    /**
     * about
     */
    private String about;

    /**
     * app name
     */
    private String name;

    public String getId() {
        return id;
    }

    public String getAbout() {
        return about;
    }

    public String getName() {
        return name;
    }
}
