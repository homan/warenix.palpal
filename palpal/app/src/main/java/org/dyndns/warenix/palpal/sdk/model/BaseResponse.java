package org.dyndns.warenix.palpal.sdk.model;

/**
 * Created by warenix on 8/23/14.
 */
public class BaseResponse {

    private Error error;

    /**
     * check this response has any error
     *
     * @return null when no error. otherwise there's an error
     */
    public Error getError() {
        return error;
    }

    public boolean isSuccess() {
        return error == null;
    }

    public static class Error {

        private String message;

        private String type;

        private String code;

        public String getMessage() {
            return message;
        }

        public String getType() {
            return type;
        }

        public String getCode() {
            return code;
        }
    }

}
