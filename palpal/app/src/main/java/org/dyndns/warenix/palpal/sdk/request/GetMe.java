package org.dyndns.warenix.palpal.sdk.request;

import org.dyndns.warenix.palpal.sdk.model.BaseResponse;
import org.dyndns.warenix.palpal.sdk.model.Picture;

/**
 * Created by warenix on 9/17/14.
 */
public class GetMe {

    public static class Request extends BaseRequest {

        public Request() {
            super(Verb.GET, "me", "id,name,picture.width(500).height(500)", Response.class);
        }
    }

    public static class Response extends BaseResponse {

        private String id;
        private String name;
        private Picture picture;

        public Response() {
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Picture getPicture() {
            return picture;
        }

    }
}
