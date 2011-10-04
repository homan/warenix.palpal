package org.dyndns.warenix.palpal.account;

import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;

import android.content.Context;

public class AccountListAdapter extends ListViewAdapter {

	public AccountListAdapter(Context context) {
		super(context);
	}

	public void addAccount(Account account) {
		itemList.add(account);
		runNotifyDataSetInvalidated();
	}
}
