package org.dyndns.warenix.palpal.sdk.request;

/**
 * Created by warenix on 8/23/14.
 */

import org.dyndns.warenix.palpal.sdk.Constant;
import org.dyndns.warenix.palpal.sdk.model.AppInfo;

/**
 * Get the base info of the facebook page
 */
public class GetAppInfo extends BaseRequest {

    public GetAppInfo() {
        super(Verb.GET, Constant.APP_ID,
              "id,name,picture.type(large),message,description,created_time,updated_time,properties",
              AppInfo.class);
    }
}
