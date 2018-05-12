# backend-tech-assessment

Backend Technical Assessment project

Includes
--------
1. Two Maven modules:
    - backend-tech-assessment-standard: Main application module - [pom.xml](backend-tech-assessment-standard/pom.xml)
    - backend-tech-assessment-standard-it: Integration test module - [pom.xml](backend-tech-assessment-standard/pom.xml)
1. Runnable Spring Boot Application: [BackendTechAssessmentApplication](backend-tech-assessment-standard/src/main/java/com/intuit/cg/backendtechassessment/BackendTechAssessmentApplication.java)
1. Application properties: [application.yml](backend-tech-assessment-standard/src/main/resources/application.yml)
1. REST endpoint definitions - [RequestMappings.java](backend-tech-assessment-standard/src/main/java/com/intuit/cg/backendtechassessment/controller/requestmappings/RequestMappings.java)
1. Controller classes: [controllers](backend-tech-assessment-standard/src/main/java/com/intuit/cg/backendtechassessment/controller)
1. Model classes: [models](backend-tech-assessment-standard/src/main/java/com/intuit/cg/backendtechassessment/model)
1. Data Transfer Object classes: [DTOs](backend-tech-assessment-standard/src/main/java/com/intuit/cg/backendtechassessment/dto)
1. Repository definitions: [repositories](backend-tech-assessment-standard/src/main/java/com/intuit/cg/backendtechassessment/repository)
1. Service classes: [services](backend-tech-assessment-standard/src/main/java/com/intuit/cg/backendtechassessment/service)
1. Converter class for converting to/from DTOs: [ModelConverter.java](backend-tech-assessment-standard/src/main/java/com/intuit/cg/backendtechassessment/service/ModelConverter.java)

Requirements
------------
See Backend Technical Assessment document for detailed requirements.

Required Software
------------
- [Java SDKv1.8](https://www.java.com/) or higher
- [Apache Maven 3.2](https://maven.apache.org/) or above

Installation Instructions
------------
- Verify the version of Java installed (must be Java 1.8 or higher)
```
    $ java -version
    java version "1.8.0_161"
    Java(TM) SE Runtime Environment (build 1.8.0_161-b12)
    Java HotSpot(TM) 64-Bit Server VM (build 25.161-b12, mixed mode)
```
- Verify the version of Maven installed (must be Maven 3.2 or higher)
```
    $ mvn -v
    Apache Maven 3.5.3 (3383c37e1f9e9b3bc3df5050c29c8aff9f295297; 2018-02-24T11:49:05-08:00)
    ...
```
- Download a zip file of the application to a local directory: [master.zip](https://github.com/aaronpulliam/coding-exercise/archive/v0.0.1.zip)

- Extract the files in the archive

Running the application
------------

- From the command-line, change your current working directory to the backend-tech-assessment-standard subdirectory of the extracted files

- Build and launch the application from the command-line using the following command:
```
    ./mvnw spring-boot:run
```
- As the application is building and starting up, it will output a lot of information to the command window. After the web application has finished starting up, the output should stop and you should see a "Started BackendTechAssesmentAppliation" message similar to the following: 

    2018-05-11 16:03:45.676  INFO 19824 --- [  restartedMain] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
    2018-05-11 16:03:45.685  INFO 19824 --- [  restartedMain] c.i.c.b.BackendTechAssessmentApplication : Started BackendTechAssessmentApplication in 11.09 seconds (JVM running for 11.876)

- With the web application running, go to Swagger UI page with your browser: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html). Swagger UI allows you to view the API definitions and make calls to it.

- The following steps can be followed to walk-through the process of creating a project, submitting bids, and viewing the project status:
    1. [Register a seller](http://localhost:8080/swagger-ui.html#!/seller45controller/registerSellerUsingPOST)
    2. [Create a project](http://localhost:8080/swagger-ui.html#!/project45controller/createProjectUsingPOST)
    3. [Register one or more buyers](http://localhost:8080/swagger-ui.html#!/buyer45controller/registerBuyerUsingPOST)
    4. [Submit one or more bids](http://localhost:8080/swagger-ui.html#!/bid45controller/submitBidUsingPOST)
    5. [Add/update an autobid amount for one or more buyers](http://localhost:8080/swagger-ui.html#!/auto45bid45controller/submitBidUsingPUT)
    6. [View the current status of the project](http://localhost:8080/swagger-ui.html#!/project45controller/getProjectByIdUsingGET)

- When the deadline for accepting bids for a project has passed, no further bids can be made on the project and the project information will 
  show the id of the winning buyer in the winningBidderId parameter (assuming at least one buyer has bid on the project) 

- The application stores all data in an in-memory database so all information is lost when you stop the application. The database console can be accessed at [http://localhost:8080/console](http://localhost:8080/console). To connect to the database from the database console, enter "jdbc:h2:mem:testdb" for the JDBC URL and click on the "Connect" button. From the database console, you can view the database definitions, run queries, etc.

- The application can be stopped by pressing CTRL-C.


Design Notes
------------
* Below is a sketch of the user stories for building the application. The database is not being pre-populated
with any data, so APIs were added to allow buyers and sellers to be added. No authentication or security is in 
place.
    * **Register as a buyer:**   
    A buyer can register on the site. The buyer provides a first name and last name and receives an id. 
    * **Register as a seller:**   
    A seller can register on the site. The seller provides a first name, and last name and receives an id. 
    * **Submit a project:**  
    A buyer can submit a project. The project must have a description, a maximum budget, and bid deadline. 
    The bid deadline must be in the future. After submitting, the buyer receives a project ID. The current 
    project details can be retrieved using the project ID.
    * **Submit a bid:**  
    A buyer can submit a bid for a project as long as the deadline has not passed. The bid must include an 
    amount. The bid amount must be less than the maximum budget, less than the lowest bid for the project,
    and greater than zero. When viewing projects, the lowest bid amount for a project must be shown and
    if the deadline has passed then the winning buyer id (if any) must be given.
    * **Auto bidding :**
    A buyer can submit an autobid for a project as long as the deadline has not passed. The autobid must
    include the minimum amount the buyer is willing to bid. The minimum bid amount must be less than the
    maximum budget and greater than zero. Whenever an autobid or bid for a project is submitted/updated,
    a new bid is made for the project if:
        * the earliest autobid submitted with the least minimum amount is less than the current lowest bid. 
          In this case, if the autobid buyer is not already the lower bidder than a new bid is made for 
          the buyer of the autobid at the maximum amount that is lower than all other minimum autobids
        * the earliest autobid submitted has a minimum amount equal to the current lowest bid and the autobid
          was submitted earlier. Assuming the buyers aren't the same then a new bid is made for the autobid
          buyer at the current lowest bid amount.

* Application follows a standard layered architecture:
    * Controller --> Services --> Repository
    * DTOs were written for the Service Layer to hide the database specifics 

* The application uses the following entities for representing/storing the data
    * Buyer (buyer_id, first_name, last_name)
    * Seller (seller_id, first_name, last_name)
    * Project (project_id, description, maximum_budget, deadline, seller_id, lowest_bid_id)
    * Bid (bid_id, amount, bid_time, buyer_id, project_id)
    * AutoBid (autobid_id, minimum_amount, bid_time, buyer_id, project_id)

* The lowest bid for a project is calculated/maintained with each bid that is made.
* Auto bidding is implemented by using a database query after each bid is made in order to determine the two auto bids with the lowest minimums. 
  It might be more efficient to maintain this information for each project in a separate table and avoid the query, since it only changes when 
  auto bids are made/update.

Implementation Notes
------------
* The following technologies/frameworks were used in building the application:
    * [Spring Boot](https://projects.spring.io/spring-boot/)
        * Spring Data JPA project using Hibernate and H2 in-memory database
        * Spring MVC using Tomcat embedded container
    * [SpringFox](https://springfox.github.io/springfox/) and [Swagger UI](https://swagger.io/swagger-ui/) for documenting the APIs, etc.
    * [ModelMapper](http://modelmapper.org/) for converting between entity objects and data transfer objects
* Integration tests were written to verify each set of functionality as it was being designed and coded. See  [BackendTechAssessmentApplicationTestsIT.java](backend-tech-assessment-standard-it/src/test/java/com/intuit/cg/backendtechassessment/BackendTechAssessmentApplicationTestsIT.java)
* To further flesh out the application, the following should be considered:
    * Adding authentication/security
    * Writing unit tests so that exception cases can be verified and code refactored
    * Refactoring the existing integration tests by moving code into shared functions and eliminate duplicated code
    * Adding more validation on data such as maximum field sizes, etc.
    * Improving error handling such as adding finer grain exceptions to the Service layer and making sure exception
      scenarios result in a proper error message.
    * Fleshing out the API to support additional operations such as updating data, querying/listing data, etc.
    * Enabling handling of larger amounts of data with pagination capability on queries.
    * Enabling caching of requests by adding support for caching using E-Tags, for example.
    * Configuring/enabling Spring Actuator to support health checks and application metrics.          

Feedback Notes
------------

* Exercise Difficulty: Moderate
* How did you feel about the exercise itself? 9
* How do you feel about coding an exercise as a step in the interview process? 9
* What would you change in the exercise and/or process?
    * Clarifying whether information can be pre-populated in the database in order to minimize
      the scope of the project. For example, having a fixed set of sellers and buyers. 
    * Also, the Seller and Buyer terminology used in the instructions are confusing, since 
      the Seller is actually looking to buy the services which Buyers are providing.
