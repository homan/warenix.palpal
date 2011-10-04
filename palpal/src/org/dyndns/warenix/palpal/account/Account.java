package org.dyndns.warenix.palpal.account;

import org.dyndns.warenix.palpaltwitter.R;
import org.dyndns.warenix.pattern.baseListView.ListViewItem;

import android.content.Context;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

/**
 * Account is a social network account.
 * 
 * @author warenix
 * 
 */
public class Account extends ListViewItem {

	String socialNetworkName;
	String nick;

	public Account(String socialNetworkName, String nick) {
		this.socialNetworkName = socialNetworkName;
		this.nick = nick;
	}

	static class ViewHolder {
		TextView socialNetworkName;
		TextView nick;
	}

	@Override
	protected View createEmptyView(Context context) {
		View view = inflater.inflate(R.layout.account, null);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.socialNetworkName = (TextView) view
				.findViewById(R.id.socialNetworkName);
		viewHolder.nick = (TextView) view.findViewById(R.id.nick);
		view.setTag(viewHolder);
		return view;
	}

	@Override
	protected View fillViewWithContent(Context context, View view) {
		ViewHolder viewHolder = (ViewHolder) view.getTag();
		viewHolder.socialNetworkName.setText(socialNetworkName);
		viewHolder.nick.setText(nick);
		return view;
	}

	@Override
	public void showContextMenu(ContextMenu menu) {

	}

}
