package com.zendesk.dto;

import java.util.Objects;

import lombok.Data;

@Data
public class User implements Comparable {

	private int _id;
	private String name;
	private String  created_at;
	private boolean verified;

	@Override
	public int compareTo(Object o) {
		return Integer.compare(get_id(), ((User)o).get_id());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return _id == other._id ;
	}

	@Override
	public int hashCode() {
		return Objects.hash(_id);
	}
	
	
	
}
