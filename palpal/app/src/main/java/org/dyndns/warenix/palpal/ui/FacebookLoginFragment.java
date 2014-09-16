package org.dyndns.warenix.palpal.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

/**
 * Created by warenix on 9/16/14.
 */
public abstract class FacebookLoginFragment extends BaseFragment {

    private static final String TAG = FacebookLoginFragment.class.getSimpleName();

    private UiLifecycleHelper mUIHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUIHelper = new UiLifecycleHelper(getActivity(), callback);
        mUIHelper.onCreate(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        Session session = Session.getActiveSession();
        if (session != null && session.getAccessToken() != null) {
            onSessionToken(session);
        }
        mUIHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mUIHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        mUIHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUIHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mUIHelper.onSaveInstanceState(outState);
    }

    protected void onSessionStateChange(Session session, SessionState state, Exception exception) {

        Log.d(TAG, "state:" + state);
        switch (state) {

            case OPENED:
                // after login
                onLoginOK(session);
                break;
            case CLOSED_LOGIN_FAILED:
                // user cancelled
                onLoginCancelled();
                break;
            case CLOSED:
                // user logout
                onLogout();
                break;
        }
    }

    protected void onLoginOK(Session session) {

    }

    protected void onLoginCancelled() {

    }

    protected void onLogout() {

    }

    protected void onSessionToken(Session session) {

    }
}
