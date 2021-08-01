### How do I get set up? ###
* Git link 
    	 
      https://github.com/Malika-Arora/zendeskProj.git


* Technical stack
    
      Java8    
      Spring Boot    
      Maven


* Approach

	  Load static data from given json files and create im-memory data structures  
	  Implemented the search on fields using java reflection


* Improvement

  	  Search can be made case insensitive.
      Include search on other datatypes as well.    
      Rest endpoints can be exposed to return the data with proper UI.
	
	
* Assumptions

      Search is case sensitive 
      Data will e filtered in case of exact match
     
* How to Run the application
    
      Navigate to \zendesk-ticket-service project and run
         mvn clean install
      Navigate to to \zendesk-ticket-service\target folder and run 
        java -jar zendesk-ticket-service-0.0.1-SNAPSHOT.jar
