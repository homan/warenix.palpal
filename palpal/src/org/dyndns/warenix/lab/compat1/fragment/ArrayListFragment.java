package org.dyndns.warenix.lab.compat1.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ArrayListFragment extends ListFragment {
	int position;

	public ArrayListFragment() {

	}

	public ArrayListFragment(int position) {
		this.position = position;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, new String[] { "a", "b",
						"c", "a", "b", "c", "a", "b", "c", "a", "b", "c", "a",
						"b", "c", "a", "b", "c", "a", "b", "c", "a", "b", "c" }));
	}

	public static Fragment newInstance(int position) {
		return new ArrayListFragment(position);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.i("ArrayListFragment", "Item clicked: " + id);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d("warenix", "onDestroyView()" + position);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("warenix", "onDestroy()");
	}

}