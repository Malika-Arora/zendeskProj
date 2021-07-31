package com.zendesk.application;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.zendesk.dto.Tickets;
import com.zendesk.dto.User;
import com.zendesk.service.UserTicketBuilderService;

@SpringBootApplication(scanBasePackages = { "com.zendesk" })
public class ZendeskTicketServiceApplication {

	@Autowired
	UserTicketBuilderService userTicketBuilderService;

	public static void main(String[] args) {
		SpringApplication.run(ZendeskTicketServiceApplication.class, args);
		String userInput;
		String nextInput;
		String searchCoulmn;
		String searchValue;
		Scanner sn = new Scanner(System.in);
		UserTicketBuilderService userTicketBuilderService = new UserTicketBuilderService();

		// loop the utility in loop until the user makes the choice to exit
		while (true) {
			// Print the options for the user to choose from
			System.out.println("\n\nWelcome to Zendesk Search");
			System.out.println("Type 'quit' to exit at any time, Press 'Enter' to continue");
			System.out.println("\n");
			System.out.println("    Select search options:");
			System.out.println("     * Press 1 to search Zendesk");
			System.out.println("     * Press 2 to view a list of searchable fields");
			System.out.println("     * Type 'quit' to exit");

			userInput = sn.next();

			switch (userInput) {
			case "1":
				System.out.println("Select  1) Users or 2) Tickets");
				nextInput = sn.next();
				switch (nextInput) {
				case "1":
					System.out.println("Enter search term");
					searchCoulmn = sn.next();
					System.out.println("Enter search value");
					sn.nextLine();
					searchValue = sn.nextLine();
					System.out.println("Searching users for " + searchCoulmn + " with a value of " + searchValue);
					Map<User, List<String>> filteredData = userTicketBuilderService.filterUsers(searchCoulmn,
							searchValue);
					if (filteredData != null) {
						filteredData.entrySet().forEach(fd -> {
							System.out.println("_id                        " + fd.getKey().get_id());
							System.out.println("name                       " + fd.getKey().getName());
							System.out.println("created_at                 " + fd.getKey().getCreated_at());
							System.out.println("verifiled                  " + fd.getKey().isVerified());
							System.out.println("tickets                    " + fd.getValue());
							System.out.println("\n\n");
						});
					} else {
						System.out.println("No results found");
					}
					System.out.println("----------------------------------");
					break;
				case "2":
					System.out.println("Enter search term");
					searchCoulmn = sn.next();
					System.out.println("Enter search value");
					sn.nextLine();
					searchValue = sn.nextLine();
					System.out.println("Searching tickets for " + searchCoulmn + " with a value of " + searchValue);
					List<Tickets> filteredTicketData = userTicketBuilderService.filterTickets(searchCoulmn,
							searchValue);
					if (filteredTicketData != null && !filteredTicketData.isEmpty()) {
						filteredTicketData.forEach(ft -> {
							System.out.println("_id                        " + ft.get_id());
							System.out.println("created_at                 " + ft.getCreated_at());
							System.out.println("type                  	   " + ft.getType());
							System.out.println("subject                    " + ft.getSubject());
							System.out.println("assignee_id                " + ft.getAssignee_id());
							System.out.println("tags                       " + ft.getTags());
							List<User> user = UserTicketBuilderService.userList.stream()
									.filter(u -> u.get_id() == ft.getAssignee_id()).collect(Collectors.toList());
							if (user != null && !user.isEmpty()) {
								System.out.println("assignee_name              " + user.get(0).getName());
							}
							System.out.println("\n\n");
						});

					} else {
						System.out.println("No results found");
					}
					System.out.println("----------------------------------");
					break;
				}

				break;
			case "2":
				List<String> userFields = userTicketBuilderService.getUserSearchableFields();
				List<String> ticketFields = userTicketBuilderService.getTicketSearchableFields();
				System.out.println("----------------------------------");
				System.out.println("Search Users with");
				userFields.forEach(uf -> System.out.println(uf));
				System.out.println("----------------------------------");
				System.out.println("Search Tickets with");
				ticketFields.forEach(tf -> System.out.println(tf));
				System.out.println("----------------------------------");
				break;
			case "quit":
				// exit from the program
				System.out.println("Exiting...");
				System.exit(0);
			default:
				// inform user in case of invalid choice.
				System.out.println("Invalid choice. Read the options carefully...");
			}
		}
	}

	@Bean
	public void getUserTicketBuilderService() throws FileNotFoundException, IOException {
		userTicketBuilderService.buildUserTicketDetails();
	}

}
