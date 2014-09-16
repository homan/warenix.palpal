package org.dyndns.warenix.palpal.sdk.request;


import org.dyndns.warenix.palpal.sdk.model.BaseResponse;
import org.dyndns.warenix.palpal.sdk.model.Post;

/**
 * Created by warenix on 8/23/14.
 */
public class GetPostList extends BaseRequest {

    public GetPostList(String path) {
        super(Verb.GET, String.format("%s/posts", path),
              "id,name,picture,message,description,created_time,updated_time,properties,status_type",
              Result.class);
    }

    public static class Result extends BaseResponse {

        private Post[] data;

        public Post[] getData() {
            return data;
        }
    }
}
