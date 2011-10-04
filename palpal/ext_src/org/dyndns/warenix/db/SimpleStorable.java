package org.dyndns.warenix.db;

public class SimpleStorable {
	int id;
	protected String type;
	protected String key;
	protected String value;

	public SimpleStorable(String type, String key, String value) {
		this.type = type;
		this.key = key;
		this.value = value;
	}

	public SimpleStorable(int id, String type, String key, String value) {
		this(type, key, value);
		this.id = id;
	}

	@Override
	public String toString() {
		return String.format("id[%s] type[%s] key[%s] value[%s]", id, type,
				key, value);
	}

	public String getValue() {
		return value;
	}
}
