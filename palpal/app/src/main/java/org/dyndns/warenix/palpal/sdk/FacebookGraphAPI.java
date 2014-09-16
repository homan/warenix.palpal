package org.dyndns.warenix.palpal.sdk;

import android.content.Context;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.dyndns.warenix.palpal.sdk.model.BaseResponse;
import org.dyndns.warenix.palpal.sdk.request.BaseRequest;
import org.dyndns.warenix.palpal.util.GsonUtil;
import org.dyndns.warenix.palpal.util.SharedPreferenceUtil;

import java.io.IOException;

import static org.dyndns.warenix.palpal.util.LogUtil.makeLog;

/**
 * Created by warenix on 8/23/14.
 */
public class FacebookGraphAPI {

    private static final String TAG = FacebookGraphAPI.class.getSimpleName();

    private static final String BASE_API_URL = "https://graph.facebook.com/v2.1";

    private static FacebookGraphAPI sInstance;

    private CharSequence
        mAccessToken
        =
        "CAAVykGwIWZAcBAPp18jZApfLq8hWzKG3RbrhVLOZC6EAPpV4U7ttQfUTY13q7wlEPQC9ff2rVHvRUFZClNST2WqrJq6X1FjZCwoMYa2H620aLcMe0vkAlmAgFB5uOvmZA1FBm9VvZCKDQEDxckRf4MHzKyGzdzuMFtPwPi7gIZBBicKUXpnusvlZBA2r61EFtvYsWro0G0FBtj6D8gi6zUocRgi72PrvYfHsoQWoiJCdGFQZDZD";

    public static FacebookGraphAPI getInstance(Context context) {
        if (sInstance == null) {
            String accessToken = SharedPreferenceUtil
                .getKeyValue(context, Constant.KEY_ACCESS_TOKEN);
            sInstance = new FacebookGraphAPI();
            sInstance.setAccessToken(accessToken);
        }
        return sInstance;
    }

    private String makeCallByGet(String path, String field) {

        OkHttpClient client = new OkHttpClient();
        String url = String.format("%s/%s?access_token=%s", BASE_API_URL, path, mAccessToken);
        if (field != null) {
            url += String.format("&fields=%s", field);
        }
        Request request = new Request.Builder()
            .url(url)
            .build();

        try {
            makeLog(TAG, "makeCallByGet", "%d url[%s]", url.hashCode(), url);

            Response response = client.newCall(request).execute();
            String result = response.body().string();
            makeLog(TAG, "makeCallByGet", "%d result [%s]", url.hashCode(), result);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public BaseResponse makeCall(BaseRequest request) {
        String result = null;
        switch (request.getVerb()) {
            case GET: {
                result = makeCallByGet(request.getPath(), request.getField());

                break;
            }
            case POST: {
                break;
            }
        }

        BaseResponse model = (BaseResponse) GsonUtil.getGson()
            .fromJson(result, request.getClassName());
        return model;
    }

    public void setAccessToken(String accessToken) {
        mAccessToken = accessToken;
    }
}
