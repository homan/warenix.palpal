package org.dyndns.warenix.palpal.social.twitter;

public class Friend {
	public String username;
	public String profileImageUrl;
	public String relation;
	public int usageCount;

	public static final String FRIEND_RELATIONSHIP_FOLLOWER = "follower";
	public static final String FRIEND_RELATIONSHIP_FOLLOWING = "following";

	public Friend(String username, String profileImageUrl, String relation,
			int usageCount) {
		this.username = username;
		this.profileImageUrl = profileImageUrl.replace("_normal", "");
		this.relation = relation;
		this.usageCount = usageCount;
	}
}
