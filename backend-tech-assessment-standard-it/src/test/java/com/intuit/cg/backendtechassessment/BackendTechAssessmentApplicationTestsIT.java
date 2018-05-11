package com.intuit.cg.backendtechassessment;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.time.OffsetDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.intuit.cg.backendtechassessment.controller.ControllerExceptionHandler.ErrorDetails;
import com.intuit.cg.backendtechassessment.controller.requestmappings.RequestMappings;
import com.intuit.cg.backendtechassessment.dto.BidDTO;
import com.intuit.cg.backendtechassessment.dto.BuyerDTO;
import com.intuit.cg.backendtechassessment.dto.ProjectDTO;
import com.intuit.cg.backendtechassessment.dto.SellerDTO;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BackendTechAssessmentApplicationTestsIT {

    private static final long MAX_BUDGET_AMOUNT = 10000l;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getUriForPath(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    public void buyerCanRegister() {
        BuyerDTO buyerDTO = getSampleBuyer();

        BuyerDTO createdBuyer = createBuyer(buyerDTO);

        assertThat(createdBuyer.getFirstName(), is("John"));
        assertThat(createdBuyer.getLastName(), is("Smith"));
        assertThat(createdBuyer.getId(), is(notNullValue()));
    }

    private BuyerDTO getSampleBuyer() {
        BuyerDTO buyerDTO = new BuyerDTO();
        buyerDTO.setFirstName("John");
        buyerDTO.setLastName("Smith");
        return buyerDTO;
    }

    private BuyerDTO createBuyer(BuyerDTO buyerDTO) {
        ResponseEntity<BuyerDTO> responseEntity = restTemplate.postForEntity(getUriForPath(RequestMappings.BUYERS),
                buyerDTO, BuyerDTO.class);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
        BuyerDTO createdBuyer = responseEntity.getBody();
        return createdBuyer;
    }

    @Test
    public void retrieveBuyer() {
        BuyerDTO buyerDTO = getSampleBuyer();

        BuyerDTO createdBuyer = createBuyer(buyerDTO);

        buyerDTO = restTemplate.getForObject(getUriForPath(RequestMappings.BUYERS + "/" + createdBuyer.getId()),
                BuyerDTO.class);

        assertThat(buyerDTO.getId(), is(createdBuyer.getId()));
        assertThat(buyerDTO.getFirstName(), is("John"));
        assertThat(buyerDTO.getLastName(), is("Smith"));
    }

    @Test
    public void sellerCanRegister() {
        SellerDTO sellerDTO = getSampleSeller();

        SellerDTO createdSeller = createSeller(sellerDTO);

        assertThat(createdSeller.getFirstName(), is("Jane"));
        assertThat(createdSeller.getLastName(), is("Doe"));
        assertThat(createdSeller.getId(), is(notNullValue()));
    }

    @Test
    public void sellerCanSubmitsProjects() {
        SellerDTO sellerDTO = getSampleSeller();
        SellerDTO createdSeller = createSeller(sellerDTO);

        OffsetDateTime deadline = OffsetDateTime.now().plusDays(5);
        ProjectDTO projectDTO = getSampleProjectForSeller(createdSeller, deadline);

        ProjectDTO createdProject = createProject(projectDTO);

        assertThat(createdProject.getDescription(), is("description"));
        assertThat(createdProject.getDeadline().toEpochSecond(), is(deadline.toEpochSecond()));
        assertThat(createdProject.getMaximumBudget(), is(MAX_BUDGET_AMOUNT));
        assertThat(createdProject.getSellerId(), is(notNullValue()));
    }

    private ProjectDTO getSampleProjectForSeller(SellerDTO createdSeller, OffsetDateTime deadline) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setDescription("description");
        projectDTO.setDeadline(deadline);
        projectDTO.setMaximumBudget(10000l);
        SellerDTO projectSeller = new SellerDTO();
        projectSeller.setId(createdSeller.getId());
        projectDTO.setSellerId(createdSeller.getId());
        return projectDTO;
    }

    private SellerDTO getSampleSeller() {
        SellerDTO sellerDTO = new SellerDTO();
        sellerDTO.setFirstName("Jane");
        sellerDTO.setLastName("Doe");
        return sellerDTO;
    }

    @Test
    public void submitProject() {
        SellerDTO sellerDTO = getSampleSeller();
        SellerDTO createdSeller = createSeller(sellerDTO);

        OffsetDateTime deadline = OffsetDateTime.now().plusDays(5);
        ProjectDTO projectDTO = getSampleProjectForSeller(createdSeller, deadline);

        ProjectDTO createdProject = createProject(projectDTO);

        assertThat(createdProject.getId(), is(notNullValue()));
        assertThat(createdProject.getDescription(), is(projectDTO.getDescription()));
        assertThat(createdProject.getDeadline().toEpochSecond(), is(projectDTO.getDeadline().toEpochSecond()));
        assertThat(createdProject.getMaximumBudget(), is(projectDTO.getMaximumBudget()));
        assertThat(createdProject.getSellerId(), is(projectDTO.getSellerId()));
    }

    @Test
    public void retrieveProject() {
        SellerDTO sellerDTO = getSampleSeller();
        SellerDTO createdSeller = createSeller(sellerDTO);

        OffsetDateTime deadline = OffsetDateTime.now().plusDays(5);
        ProjectDTO projectDTO = getSampleProjectForSeller(createdSeller, deadline);

        ProjectDTO createdProject = createProject(projectDTO);

        projectDTO = restTemplate.getForObject(getUriForPath(RequestMappings.PROJECTS + "/" + createdProject.getId()),
                ProjectDTO.class);

        assertThat(projectDTO.getId(), is(createdProject.getId()));
        assertThat(projectDTO.getDescription(), is(createdProject.getDescription()));
        assertThat(projectDTO.getDeadline(), is(createdProject.getDeadline()));
        assertThat(projectDTO.getMaximumBudget(), is(createdProject.getMaximumBudget()));
        assertThat(createdSeller.getId(), is(createdProject.getSellerId()));
    }

    private ProjectDTO createProject(ProjectDTO projectDTO) {
        ResponseEntity<ProjectDTO> responseEntity = restTemplate.postForEntity(getUriForPath(RequestMappings.PROJECTS),
                projectDTO, ProjectDTO.class);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
        return responseEntity.getBody();
    }

    private SellerDTO createSeller(SellerDTO sellerDTO) {
        ResponseEntity<SellerDTO> responseEntity = restTemplate.postForEntity(getUriForPath(RequestMappings.SELLERS),
                sellerDTO, SellerDTO.class);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
        return responseEntity.getBody();
    }

    @Test
    public void submitBid() {

        BuyerDTO createdBuyer = createBuyer(getSampleBuyer());
        SellerDTO createdSeller = createSeller(getSampleSeller());

        OffsetDateTime deadline = OffsetDateTime.now().plusDays(5);
        ProjectDTO createdProject = createProject(getSampleProjectForSeller(createdSeller, deadline));

        BidDTO bidDTO = new BidDTO();
        bidDTO.setBuyerId(createdBuyer.getId());
        bidDTO.setProjectId(createdProject.getId());
        bidDTO.setAmount(MAX_BUDGET_AMOUNT - 10);

        // post initial bid
        ResponseEntity<BidDTO> responseEntity = restTemplate.postForEntity(getUriForPath(RequestMappings.BIDS), bidDTO,
                BidDTO.class);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
        BidDTO createdBid = responseEntity.getBody();

        assertThat(createdBid.getId(), is(notNullValue()));
        assertThat(createdBid.getAmount(), is(MAX_BUDGET_AMOUNT - 10));
        assertThat(createdBid.getProjectId(), is(createdProject.getId()));
        assertThat(createdBid.getBuyerId(), is(createdBuyer.getId()));

        // verify that project lowestBid matches
        createdProject = restTemplate.getForObject(getUriForPath(RequestMappings.PROJECTS + "/" + createdProject
                .getId()), ProjectDTO.class);

        assertThat(createdProject.getLowestBidAmount(), is(MAX_BUDGET_AMOUNT - 10));

        // submit a higher bid (verify it fails)
        bidDTO.setAmount(MAX_BUDGET_AMOUNT - 5);
        ResponseEntity<ErrorDetails> errorResponseEntity = restTemplate.postForEntity(getUriForPath(
                RequestMappings.BIDS), bidDTO, ErrorDetails.class);
        assertThat(errorResponseEntity.getStatusCode(), is(HttpStatus.CONFLICT));
        ErrorDetails errorDetails = errorResponseEntity.getBody();
        assertThat(errorDetails.getMessage(), is("Bid must be lower than " + (MAX_BUDGET_AMOUNT - 10)));
    }

}
