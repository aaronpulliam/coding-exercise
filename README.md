# backend-tech-assessment

Skeleton project for Backend Technical Assessment.

Includes
--------
- Maven - [pom.xml](pom.xml)
- Application properties - [application.yml](src/main/resources/application.yml)
- Runnable Spring Boot Application - [BackendTechAssessmentApplication](src/main/java/com/intuit/cg/backendtechassessment/BackendTechAssessmentApplication.java)
- REST endpoints - [RequestMappings.java](src/main/java/com/intuit/cg/backendtechassessment/controller/requestmappings/RequestMappings.java)

Requirements
------------
See Backend Technical Assessment document for detailed requirements.



Design Notes
------------
* Sketch of User Stories:
    * **Register new user:**   
    A user can register to use the site. The user provides a username, first name, and last name. 
    Registration is not allowed if the username is already taken; otherwise, registration is successful.
    * **Submit a project:**  
    A user can submit a project. The project must have a 
    description, a maximum budget, and bid deadline. The bid deadline must 
    be in the future. After submitting the project, the user can retrieve the 
    project details.
    * **View projects:**  
    A user can view a list of projects. For each project, 
    the description, maximum budget, and bid deadline must be returned.
    * **Submit a bid:**  
    A user can submit a bid for a project. The bid must include
    an amount. The bid amount must be less than the maximum budget,
    less than any existing bid for the project, and greater than zero. When viewing 
    projects, the current lowest bid for a project must be shown.
    * **Winning a bid:**  
    When the bid deadline is reached, the lowest bid for the 
    project wins and further bidding is not allowed.
    * **View your projects:**   
    A user can retrieve a list of their projects.
    * **View your bids:**  
    A user can retrieve a list of bids they have made.
    
* Entities:
    * Users (Sellers and Buyers)
    * Projects
    * Bids
