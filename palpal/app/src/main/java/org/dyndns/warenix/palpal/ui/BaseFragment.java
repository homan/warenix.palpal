package org.dyndns.warenix.palpal.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by warenix on 9/16/14.
 */
public class BaseFragment extends Fragment {

    private boolean mHasSubscribedEventBus;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        subscribeEventBus();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (!mHasSubscribedEventBus) {
            subscribeEventBus();

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mHasSubscribedEventBus) {
            subscribeEventBus();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mHasSubscribedEventBus) {
            unsubscribeEventBus();

        }
    }

    protected void subscribeEventBus() {
        mHasSubscribedEventBus = true;
    }

    protected void unsubscribeEventBus() {
        mHasSubscribedEventBus = false;
    }
}
