package com.zendesk.service;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.zendesk.application.ZendeskTicketServiceApplication;
import com.zendesk.dto.Tickets;
import com.zendesk.dto.User;
@SpringBootTest(classes = {ZendeskTicketServiceApplication.class})
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
public class UserTicketBuilderServiceTest {
	
	@Autowired
	private UserTicketBuilderService userTicketBuilderService;
	
	@Test
	public void testUserSearchById() throws Exception {
		Map<User, List<String>> filteredUsers = userTicketBuilderService.filterUsers("_id", "1");
		validateUsersResults(filteredUsers);
	}
	
	@Test
	public void testUserSearchName() throws Exception {
		Map<User, List<String>> filteredUsers = userTicketBuilderService.filterUsers("name", "Francisca Rasmussen");
		validateUsersResults(filteredUsers);
	}
	
	@Test
	public void testUserSearchNameNotFound() throws Exception {
		Map<User, List<String>> filteredUsers = userTicketBuilderService.filterUsers("name", "Francisca");
		assertNull(filteredUsers);
	}
	
	
	private void validateUsersResults(Map<User, List<String>> filteredUsers) {
		assertEquals(1, filteredUsers.size());
		filteredUsers.keySet().forEach(u-> {
			assertEquals(1, u.get_id());
			assertEquals("Francisca Rasmussen", u.getName());
			assertEquals("2016-04-15T05:19:46-10:00", u.getCreated_at());
			assertTrue(u.isVerified());
		});
		User user = new User();
		user.set_id(1);
		assertEquals(Stream.of("A Problem in Russian Federation","A Problem in Malawi").collect(Collectors.toList()), filteredUsers.get(user));
	}
	@Test
	public void testTicketSearchById() throws Exception {
		List<Tickets> filteredTickets = userTicketBuilderService.filterTickets("_id", "436bf9b0-1147-4c0a-8439-6f79833bff5b");
		validateTicketResults(filteredTickets);
	}
	
	@Test
	public void testTicketSearchSubject() throws Exception {
		List<Tickets> filteredTickets = userTicketBuilderService.filterTickets("subject", "A Catastrophe in Korea (North)");
		validateTicketResults(filteredTickets);
	}
	
	@Test
	public void testTicketSearchCreatedAt() throws Exception {
		List<Tickets> filteredTickets = userTicketBuilderService.filterTickets("created_at", "2016-04-28T11:19:34-10:00");
		validateTicketResults(filteredTickets);
	}
	
	@Test
	public void testTicketSearchTag() throws Exception {
		List<Tickets> filteredTickets = userTicketBuilderService.filterTickets("tags", "Ohio");
		List<Tickets> tickets = filteredTickets.stream().filter(ft-> ft.get_id().equalsIgnoreCase("436bf9b0-1147-4c0a-8439-6f79833bff5b")).collect(Collectors.toList());
		validateTicketResults(tickets);
	}
	
	@Test
	public void testGetTicketSearchableFields() throws Exception {
		List<String> ticketSearchableFields = userTicketBuilderService.getTicketSearchableFields();
		assertTrue(ticketSearchableFields.containsAll(Stream.of("_id","created_at","type","subject","assignee_id","tags").collect(Collectors.toList())));
	}
	
	@Test
	public void testGetUserSearchableFields() throws Exception {
		List<String> userSearchableFields = userTicketBuilderService.getUserSearchableFields();
		assertTrue(userSearchableFields.containsAll(Stream.of("_id","created_at","verified","name").collect(Collectors.toList())));
	}
	
	public void validateTicketResults(List<Tickets> filteredTickets) {
		assertEquals(1, filteredTickets.size());
		Tickets ticket = filteredTickets.get(0);
		assertEquals("436bf9b0-1147-4c0a-8439-6f79833bff5b", ticket.get_id());
		assertEquals("2016-04-28T11:19:34-10:00", ticket.getCreated_at());
		assertEquals("incident", ticket.getType());
		assertEquals("A Catastrophe in Korea (North)", ticket.getSubject());
		assertEquals(24, ticket.getAssignee_id());
		assertEquals(4, ticket.getTags().size());
		List<String> tags = new ArrayList();
		tags.add("Ohio");
		tags.add("Pennsylvania");
		tags.add("American Samoa");
		tags.add("Northern Mariana Islands");
		assertTrue(ticket.getTags().containsAll(tags));
	}
	
}
