package org.dyndns.warenix.palpal.social.facebook;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dyndns.warenix.palpal.social.facebook.vo.FacebookPost;
import org.dyndns.warenix.palpal.social.facebook.vo.graph.UserGroupHeader;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

public class TimelineAdapter extends BaseExpandableListAdapter {
	Context context;
	ArrayList<String> usernameList;
	HashMap<String, ArrayList<FacebookPost>> timeline;
	HashMap<String, SoftReference<Bitmap>> imagePool;

	public TimelineAdapter(Context context, ArrayList<String> usernameList,
			HashMap<String, ArrayList<FacebookPost>> timeline,
			HashMap<String, SoftReference<Bitmap>> imagePool) {
		this.context = context;
		this.usernameList = usernameList;
		this.timeline = timeline;
		this.imagePool = imagePool;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		String createdBy = usernameList.get(groupPosition);
		List<FacebookPost> statusList = timeline.get(createdBy);
		if (childPosition >= statusList.size()) {
			return null;
		}
		return statusList.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return groupPosition * 10000 + childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		FacebookPost status = (FacebookPost) getChild(groupPosition,
				childPosition);
		if (status == null) {
			return null;
		}
		return status.getView(context, convertView, imagePool);
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		String createdBy = usernameList.get(groupPosition);
		List<FacebookPost> statusList = timeline.get(createdBy);
		return statusList.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		if (groupPosition >= usernameList.size()) {
			return null;
		}

		String createdBy = usernameList.get(groupPosition);
		return createdBy;
	}

	@Override
	public int getGroupCount() {
		return usernameList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (groupPosition >= usernameList.size()) {
			return null;
		}

		String createdById = (String) getGroup(groupPosition);
		List<FacebookPost> statusList = timeline.get(createdById);
		String createdByUserName = statusList.get(0).createdByName;
		UserGroupHeader user = new UserGroupHeader(createdById,
				createdByUserName, String.format("(%s)", statusList.size()));
		statusList = null;

		return user.getView(context, convertView, imagePool);
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
}