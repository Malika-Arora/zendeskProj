package com.zendesk.dto;

import java.util.List;

import lombok.Data;

@Data
public class Tickets {

	private String _id;
	private String created_at;
	private String type;
	private String subject;
	private int assignee_id;
	private List<String> tags;
}
