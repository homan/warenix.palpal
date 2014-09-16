package org.dyndns.warenix.palpal.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.widget.LoginButton;
import com.squareup.picasso.Picasso;

import de.greenrobot.event.EventBus;

import org.dyndns.warenix.palpal.R;
import org.dyndns.warenix.palpal.sdk.FacebookGraphAPI;
import org.dyndns.warenix.palpal.sdk.request.GetMe;

import java.util.Arrays;
import java.util.List;

import static org.dyndns.warenix.palpal.util.LogUtil.makeLog;

/**
 * Created by warenix on 9/16/14.
 */
public class FacebookMembershipFragment extends FacebookLoginFragment {

    private static final String TAG = FacebookMembershipFragment.class.getSimpleName();
    private final List<String> mPermissions;

    private TextView mName;
    private ImageView mProfileImage;

    public FacebookMembershipFragment() {
        mPermissions = Arrays.asList("user_status");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facebook_membership, container, false);
        LoginButton mAuthButton = (LoginButton) view.findViewById(R.id.authButton);
        mAuthButton.setFragment(this);
        mAuthButton.setReadPermissions(mPermissions);

        mName = (TextView) view.findViewById(R.id.name);
        mProfileImage = (ImageView) view.findViewById(R.id.profile_image);
        return view;
    }


    protected void onLoginOK(Session session) {
        super.onLoginOK(session);
        Log.d(TAG, "login");
    }

    protected void onLoginCancelled() {
        super.onLoginCancelled();
        Log.d(TAG, "login is cancelled");
    }

    protected void onLogout() {
        super.onLogout();
        Log.d(TAG, "logout");
    }

    @Override
    protected void onSessionToken(Session session) {
        Log.d(TAG, "access token:" + session.getAccessToken());

        FacebookGraphAPI.getInstance(getActivity()).setAccessToken(session.getAccessToken());

        EventBus.getDefault().post(new GetMe.Request());
    }

    @Override
    protected void subscribeEventBus() {
        super.subscribeEventBus();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void unsubscribeEventBus() {
        super.unsubscribeEventBus();
        EventBus.getDefault().unregister(this);
    }

    public void onEventBackgroundThread(GetMe.Request requestEvent) {

        GetMe.Request request = new GetMe.Request();
        GetMe.Response response =
            (GetMe.Response) FacebookGraphAPI.getInstance(getActivity()).makeCall(request);
        makeLog(TAG, "GetMe.Request", "me id[%s]", response.getId());
        EventBus.getDefault().post(response);
    }

    public void onEventMainThread(GetMe.Response resultEvent) {
        Picasso.with(getActivity()).load(resultEvent.getPicture().getData().getUrl()).fit()
            .into(mProfileImage);
        mName.setText(resultEvent.getName());
    }

}
