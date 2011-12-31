package org.dyndns.warenix.lab.compat1.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class BFragment extends ListFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, new String[] { "1", "2",
						"3", "1", "2", "3", "1", "2", "3" }));
	}

	public static Fragment newInstance(int position) {
		return new BFragment();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.i("ArrayListFragment", "Item clicked: " + id);
	}

}
