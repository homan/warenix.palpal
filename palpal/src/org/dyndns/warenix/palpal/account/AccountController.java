package org.dyndns.warenix.palpal.account;

import org.dyndns.warenix.pattern.baseListView.ListViewAdapter;
import org.dyndns.warenix.pattern.baseListView.ListViewController;

import android.app.Activity;
import android.content.Context;

public class AccountController extends ListViewController {

	public AccountController(Activity context, int resourceId) {
		super(context, resourceId);
	}

	@Override
	public ListViewAdapter setupListViewAdapter(Context context) {
		AccountListAdapter adapter = new AccountListAdapter(context);
		return adapter;
	}

	public void addAccount(Account account) {
		((AccountListAdapter) listAdapter).addAccount(account);
	}

}
