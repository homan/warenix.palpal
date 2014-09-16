package org.dyndns.warenix.palpal.sdk.request;

/**
 * Created by warenix on 8/23/14.
 */
public class BaseRequest {

    private final Class<?> mClassName;

    private final Verb mVerb;

    private final String mField;

    private String mPath;

    public BaseRequest(Verb verb, String path, String field, Class<?> className) {
        mVerb = verb;
        mPath = path;
        mField = field;
        mClassName = className;
    }

    public String getField() {
        return mField;
    }

    public Verb getVerb() {
        return mVerb;
    }

    public Class<?> getClassName() {
        return mClassName;
    }

    public String getPath() {
        return mPath;
    }

    public static enum Verb {
        GET, POST
    }
}
