package org.dyndns.warenix.lab.compat1.app.timeline;

import org.dyndns.warenix.mission.timeline.TimelineListFragment;

import android.os.Parcel;
import android.os.Parcelable;

public class TimelineFactory {
	public static class TimelineConfig implements Parcelable {
		public TimelineConfig(Type type, String title) {
			mType = type;
			mTitle = title;
		}

		public String mTitle;
		public Type mType;

		public static enum Type {
			Stream, Notifications, Messages,
			Photo
		}

		// + Parcelable
		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			out.writeString(mTitle);
			out.writeInt(mType.ordinal());
		}

		private TimelineConfig(Parcel in) {
			mType = Type.values()[in.readInt()];
			mTitle = in.readString();
		}

		public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
			public TimelineConfig createFromParcel(Parcel in) {
				return new TimelineConfig(in);
			}

			public TimelineConfig[] newArray(int size) {
				return new TimelineConfig[size];
			}
		};

	}

	public static TimelineListFragment factory(TimelineConfig config) {
		TimelineListFragment f = null;
		if (config != null) {
			switch (config.mType) {
			case Stream:
				f = StreamTimeline.newInstance(config);
				break;
			case Notifications:
				f = NotificationsTimeline.newInstance(config);
				break;
			case Messages:
				f = MessagesTimeline.newInstance(config);
				break;
			case Photo:
				f = PhotoTimeline.newInstance(config);
			}
		}
		return f;
	}
}
