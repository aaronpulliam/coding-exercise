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
    * **Register as a buyer:**   
    A buyer can register on the site. The buyer provides a first name and last name and receives an id. 
    * **Register as a seller:**   
    A seller can register on the site. The seller provides a first name, and last name and receives an id. 
    * **Submit a project:**  
    A buyer can submit a project. The project must have a description, a maximum budget, and bid deadline. 
    The bid deadline must be in the future. After submitting, the buyer receives a project ID. The current 
    project details can be retrieved using the project ID.
    * **View projects:**  
    A seller or buyer can view a list of projects. For each project, the description, maximum budget, 
    and bid deadline must be returned.
    * **Submit a bid:**  
    A buyer can submit a bid for a project. The bid must include an amount. The bid amount must be less 
    than the maximum budget, less than the lowest bid for the project, and greater than zero. 
    When viewing projects, the lowest bid for a project must be shown.
    * **Winning a bid:**  
    When the bid deadline is reached, the lowest bid for the project wins and further bidding is not allowed.
    
* Entities:
    * Sellers
    * Buyers
    * Projects
    * Bids
