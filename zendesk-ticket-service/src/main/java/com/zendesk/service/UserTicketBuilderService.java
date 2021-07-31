package com.zendesk.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zendesk.dto.Tickets;
import com.zendesk.dto.User;
import com.zendesk.exception.CustomException;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserTicketBuilderService {
	private static SortedMap<User, List<Tickets>> userTicketMap = new TreeMap<>();
	private static SortedMap<Integer, List<Tickets>> userIdticketMap = new TreeMap<>();
	public static List<User> userList = new ArrayList<>();
	private static List<Tickets> ticketList = new ArrayList<>();

	public void buildUserTicketDetails() {
		JSONParser jsonParser = new JSONParser();
		JSONArray ticketArray = null;
		JSONArray userArray = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			InputStream is = getClass().getResourceAsStream("/tickets.json");
			ticketArray = (JSONArray) jsonParser.parse(new InputStreamReader(is));
			Iterator<?> iterator = ticketArray.iterator();
			while (iterator.hasNext()) {
				List<Tickets> tickets = null;
				Tickets ticket = mapper.readValue(iterator.next().toString(), Tickets.class);
				// Tickets ticket =(Tickets) iterator.next();
				ticketList.add(ticket);
				if (userIdticketMap.get(ticket.getAssignee_id()) == null) {
					tickets = new ArrayList<Tickets>();
				} else {
					tickets = userIdticketMap.get(ticket.getAssignee_id());
				}
				tickets.add(ticket);
				userIdticketMap.put(ticket.getAssignee_id(), tickets);
			}

			is = getClass().getResourceAsStream("/users.json");
			userArray = (JSONArray) jsonParser.parse(new InputStreamReader(is));
			Iterator<?> userIterator = userArray.iterator();
			while (userIterator.hasNext()) {
				User user = mapper.readValue(userIterator.next().toString(), User.class);
				userList.add(user);
				userTicketMap.put(user, userIdticketMap.get(user.get_id()));
			}

		} catch (FileNotFoundException e) {
			throw new CustomException("File not found ");
		} catch (IOException e) {
			throw new CustomException("Error reading the file ");
		} catch (ParseException e) {
			throw new CustomException("Error in parsing json to pojo ");
		}
	}

	public Map<User, List<String>> filterUsers(String searchCoulmn, String searchValue) {
		User user = new User();
		List<User> filteredUsers = null;
		Field[] allFields = user.getClass().getDeclaredFields();
		Optional<Field> fieldOpt = Arrays.asList(allFields).stream()
				.filter(feilds -> feilds.getName().equals(searchCoulmn)).findFirst();
		if (fieldOpt.isPresent()) {
			Field field = fieldOpt.get();
			try {
				filteredUsers = userList.stream().filter(u -> {
					try {
						field.setAccessible(true);
						return field.get(u).toString().equals(searchValue);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						throw new CustomException("Error in getting dynamic field's value ");
					}
				}).collect(Collectors.toList());
			} catch (IllegalArgumentException e) {
				throw new CustomException("Error in getting dynamic field's value ");
			}
		} else {
			System.out.println("Selected field does not exist");
		}
		if (filteredUsers != null && !filteredUsers.isEmpty()) {
			List<User> userTicketExists = filteredUsers.stream().filter(us -> userTicketMap.get(us) != null)
					.collect(Collectors.toList());
			return userTicketMap.entrySet().stream()
					.filter(userTicket -> userTicketExists.contains(userTicket.getKey()))
					.collect(Collectors.toMap(map -> (User) map.getKey(),
							map -> (List<String>) (((List<Tickets>) map.getValue()).stream().map(fd -> fd.getSubject())
									.collect(Collectors.toList()))));
		} else {
			return null;
		}
	}

	public List<Tickets> filterTickets(String searchCoulmn, String searchValue) {
		Tickets ticket = new Tickets();
		List<Tickets> filteredTickets = null;
		Field[] allFields = ticket.getClass().getDeclaredFields();
		Optional<Field> fieldOpt = Arrays.asList(allFields).stream()
				.filter(feilds -> feilds.getName().equals(searchCoulmn)).findFirst();
		if (fieldOpt.isPresent()) {
			Field field = fieldOpt.get();
			try {
				filteredTickets = ticketList.stream().filter(t -> {
					try {
						field.setAccessible(true);
						if (Collection.class.isAssignableFrom(field.getType())) {
							return field.get(t) != null && ((Collection<?>) field.get(t)).contains(searchValue);
						} else {
							return field.get(t) != null && field.get(t).equals(searchValue);
						}
					} catch (IllegalArgumentException | IllegalAccessException e) {
						throw new CustomException("Error in getting dynamic field's value ");
					}
				}).collect(Collectors.toList());
			} catch (IllegalArgumentException e) {
				throw new CustomException("Error in getting dynamic field's value ");
			}
		} else {
			System.out.println("Selected field does not exist");
		}
		return filteredTickets;

	}

	public List<String> getTicketSearchableFields() {
		Tickets ticket = new Tickets();
		return (Arrays.asList(ticket.getClass().getDeclaredFields())).stream().map(tf -> tf.getName())
				.collect(Collectors.toList());
	}

	public List<String> getUserSearchableFields() {
		User user = new User();
		return (Arrays.asList(user.getClass().getDeclaredFields())).stream().map(uf -> uf.getName())
				.collect(Collectors.toList());
	}

}
