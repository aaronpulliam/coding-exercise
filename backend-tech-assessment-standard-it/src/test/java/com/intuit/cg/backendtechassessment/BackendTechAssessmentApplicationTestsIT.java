package com.intuit.cg.backendtechassessment;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.intuit.cg.backendtechassessment.controller.ControllerExceptionHandler.ErrorDetails;
import com.intuit.cg.backendtechassessment.controller.requestmappings.RequestMappings;
import com.intuit.cg.backendtechassessment.dto.AutoBidDTO;
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

    private BuyerDTO firstSampleBuyerDTO = new BuyerDTO();
    private BuyerDTO secondSampleBuyerDTO = new BuyerDTO();
    private BuyerDTO thirdSampleBuyerDTO = new BuyerDTO();

    @Before
    public void setUp() {
        // setup sample data
        firstSampleBuyerDTO.setFirstName("John");
        firstSampleBuyerDTO.setLastName("Smith");

        secondSampleBuyerDTO.setFirstName("Jack");
        secondSampleBuyerDTO.setLastName("Smith");

        thirdSampleBuyerDTO.setFirstName("Jim");
        thirdSampleBuyerDTO.setLastName("Smith");
    }

    @Test
    public void postForBuyerReturnsCreated() {
        postBuyerAndVerifyResponse(firstSampleBuyerDTO);
    }

    @Test
    public void getForBuyerByIdReturnsBuyerInformation() {
        BuyerDTO buyerDTO = firstSampleBuyerDTO;
        BuyerDTO createdBuyer = postBuyerAndVerifyResponse(buyerDTO);

        getBuyerAndVerifyResponse(createdBuyer.getId(), buyerDTO);
    }

    @Test
    public void postForSellerReturnsCreated() {
        postSellerAndVerifyResponse(getSampleSeller());
    }

    @Test
    public void postForProjectReturnsCreated() {
        SellerDTO createdSeller = postSellerAndVerifyResponse(getSampleSeller());

        OffsetDateTime deadline = OffsetDateTime.now().plusDays(5);
        ProjectDTO projectDTO = getSampleProjectForSeller(createdSeller, deadline);

        postProjectAndVerifyResponse(projectDTO);
    }

    @Test
    public void postForProjectWithoutSellerFails() {

        OffsetDateTime deadline = OffsetDateTime.now().plusDays(5);
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setDescription("description");
        projectDTO.setDeadline(deadline);
        projectDTO.setMaximumBudget(MAX_BUDGET_AMOUNT);

        ResponseEntity<ErrorDetails> responseEntity = restTemplate.postForEntity(getUriForPath(
                RequestMappings.PROJECTS), projectDTO, ErrorDetails.class);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.UNPROCESSABLE_ENTITY));
        ErrorDetails errorDetails = responseEntity.getBody();
        assertThat(errorDetails.getMessage(), is("Project must have a seller"));
    }

    @Test
    public void getForProjectByIdReturnsProjectInformation() {
        SellerDTO createdSeller = postSellerAndVerifyResponse(getSampleSeller());

        OffsetDateTime deadline = OffsetDateTime.now().plusDays(5);
        ProjectDTO projectDTO = getSampleProjectForSeller(createdSeller, deadline);

        ProjectDTO createdProject = postProjectAndVerifyResponse(projectDTO);

        getProjectAndVerifyResponse(createdProject, null, null);
    }

    @Test
    public void submitBid() throws InterruptedException {
        // create buyer and seller
        BuyerDTO createdBuyer = postBuyerAndVerifyResponse(firstSampleBuyerDTO);
        SellerDTO createdSeller = postSellerAndVerifyResponse(getSampleSeller());

        // create project
        OffsetDateTime deadline = OffsetDateTime.now().plusNanos(TimeUnit.NANOSECONDS.convert(1000,
                TimeUnit.MILLISECONDS));
        ProjectDTO createdProject = postProjectAndVerifyResponse(getSampleProjectForSeller(createdSeller, deadline));

        // submit bid
        Long bidAmount = MAX_BUDGET_AMOUNT - 10;
        postBidForProjectAndVerifyResponse(createdProject.getId(), createdBuyer.getId(), bidAmount);

        // verify amount of lowestBid on project
        getProjectAndVerifyResponse(createdProject, MAX_BUDGET_AMOUNT - 10, null);

        // submit a higher bid and verify it fails
        postBidForProjectAndVerifyFailure(createdProject.getId(), createdBuyer.getId(), MAX_BUDGET_AMOUNT - 5,
                HttpStatus.UNPROCESSABLE_ENTITY, "Bid must be lower than " + bidAmount);

        // wait for project deadline to pass
        while (deadline.isAfter(OffsetDateTime.now())) {
            Thread.sleep(10);
        }

        // submit a bid and verify it fails
        bidAmount = MAX_BUDGET_AMOUNT - 20;
        postBidForProjectAndVerifyFailure(createdProject.getId(), createdBuyer.getId(), bidAmount,
                HttpStatus.UNPROCESSABLE_ENTITY, "Bid deadline has passed");

        // checking for winning buyer id
        getProjectAndVerifyResponse(createdProject, MAX_BUDGET_AMOUNT - 10, createdBuyer.getId());
    }

    @Test
    public void verifyAutoBid() throws InterruptedException {

        // create three buyers:
        BuyerDTO firstCreatedBuyer = postBuyerAndVerifyResponse(firstSampleBuyerDTO);
        BuyerDTO secondCreatedBuyer = postBuyerAndVerifyResponse(secondSampleBuyerDTO);
        BuyerDTO thirdCreatedBuyer = postBuyerAndVerifyResponse(thirdSampleBuyerDTO);

        // create seller
        SellerDTO createdSeller = postSellerAndVerifyResponse(getSampleSeller());

        // create project
        OffsetDateTime deadline = OffsetDateTime.now().plusDays(5);
        ProjectDTO createdProject = postProjectAndVerifyResponse(getSampleProjectForSeller(createdSeller, deadline));

        // add autobid for firstCreatedBuyer with minimum amount of 20, resulting in bid
        // of 20
        Long autoBidAmount = 20l;
        postAutoBidForProjectAndVerifyResponse(createdProject.getId(), firstCreatedBuyer.getId(), autoBidAmount);
        getProjectAndVerifyResponse(createdProject, MAX_BUDGET_AMOUNT, null);

        // update existing autobid for firstCreatedBuyer to minimum of 25 (from 20),
        // result is no change in bids
        postAutoBidForProjectAndVerifyResponse(createdProject.getId(), firstCreatedBuyer.getId(), 25l);

        // verify lowest bid is for 20
        getProjectAndVerifyResponse(createdProject, MAX_BUDGET_AMOUNT, null);

        // add autobid for secondCreatedBuyer with minimum amount of 15 and verify
        // lowest bid is 24
        autoBidAmount = 15l;
        postAutoBidForProjectAndVerifyResponse(createdProject.getId(), secondCreatedBuyer.getId(), autoBidAmount);
        getProjectAndVerifyResponse(createdProject, 24l, null);

        // add regular bid for thirdCreatedBuyer with minimum amount of 23 and verify
        // lowest bid is 22
        Long bidAmount = 23l;
        postBidForProjectAndVerifyResponse(createdProject.getId(), thirdCreatedBuyer.getId(), bidAmount);
        getProjectAndVerifyResponse(createdProject, 22l, null);

        // add regular bid for thirdCreatedBuyer with minimum amount of 15 and verify
        // lowest bid is 15
        bidAmount = 15l;
        postBidForProjectAndVerifyResponse(createdProject.getId(), thirdCreatedBuyer.getId(), bidAmount);
        getProjectAndVerifyResponse(createdProject, 15l, null);

        // add regular bid for thirdCreatedBuyer with minimum amount of 15 and verify
        // lowest bid is 15
        bidAmount = 14l;
        postBidForProjectAndVerifyResponse(createdProject.getId(), thirdCreatedBuyer.getId(), bidAmount);
        getProjectAndVerifyResponse(createdProject, 14l, null);
    }

    private AutoBidDTO postAutoBidForProjectAndVerifyResponse(Long projectId, Long buyerId, Long autoBidAmount) {
        AutoBidDTO autoBidDTO = new AutoBidDTO();
        autoBidDTO.setBuyerId(buyerId);
        autoBidDTO.setProjectId(projectId);
        autoBidDTO.setMinimumAmount(autoBidAmount);

        ResponseEntity<AutoBidDTO> responseEntity = restTemplate.exchange(getUriForPath(RequestMappings.AUTOBIDS),
                HttpMethod.PUT, new HttpEntity<AutoBidDTO>(autoBidDTO), AutoBidDTO.class);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
        AutoBidDTO createdBid = responseEntity.getBody();

        assertThat(createdBid.getId(), is(notNullValue()));
        assertThat(createdBid.getMinimumAmount(), is(autoBidAmount));
        assertThat(createdBid.getProjectId(), is(projectId));
        assertThat(createdBid.getBuyerId(), is(buyerId));
        return createdBid;
    }

    private void postBidForProjectAndVerifyFailure(Long projectId, Long buyerId, Long amount, HttpStatus httpStatus,
            String errorMessage) {
        BidDTO bidDTO = new BidDTO();
        bidDTO.setBuyerId(buyerId);
        bidDTO.setProjectId(projectId);
        bidDTO.setAmount(amount);

        ResponseEntity<ErrorDetails> errorResponseEntity = restTemplate.postForEntity(getUriForPath(
                RequestMappings.BIDS), bidDTO, ErrorDetails.class);

        assertThat(errorResponseEntity.getStatusCode(), is(httpStatus));
        ErrorDetails errorDetails = errorResponseEntity.getBody();
        assertThat(errorDetails.getMessage(), is(errorMessage));
    }

    private BidDTO postBidForProjectAndVerifyResponse(Long projectId, Long buyerId, Long amount) {
        BidDTO bidDTO = new BidDTO();
        bidDTO.setBuyerId(buyerId);
        bidDTO.setProjectId(projectId);
        bidDTO.setAmount(amount);

        ResponseEntity<BidDTO> responseEntity = restTemplate.postForEntity(getUriForPath(RequestMappings.BIDS), bidDTO,
                BidDTO.class);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
        BidDTO createdBid = responseEntity.getBody();

        assertThat(createdBid.getId(), is(notNullValue()));
        assertThat(createdBid.getAmount(), is(amount));
        assertThat(createdBid.getProjectId(), is(projectId));
        assertThat(createdBid.getBuyerId(), is(buyerId));
        return bidDTO;
    }

    private ProjectDTO getProjectAndVerifyResponse(ProjectDTO createdProject, Long lowestBidAmount,
            Long winningBidderId) {
        ProjectDTO projectDTO = restTemplate.getForObject(getUriForPath(RequestMappings.PROJECTS + "/" + createdProject
                .getId()), ProjectDTO.class);

        assertThat(projectDTO.getId(), is(createdProject.getId()));
        assertThat(projectDTO.getDescription(), is(createdProject.getDescription()));
        assertThat(projectDTO.getDeadline(), is(createdProject.getDeadline()));
        assertThat(projectDTO.getMaximumBudget(), is(createdProject.getMaximumBudget()));
        assertThat(projectDTO.getSellerId(), is(createdProject.getSellerId()));
        assertThat(projectDTO.getLowestBidAmount(), is(lowestBidAmount));
        assertThat(projectDTO.getWinningBidderId(), is(winningBidderId));
        return projectDTO;
    }

    private SellerDTO postSellerAndVerifyResponse(SellerDTO sellerDTO) {
        ResponseEntity<SellerDTO> responseEntity = restTemplate.postForEntity(getUriForPath(RequestMappings.SELLERS),
                sellerDTO, SellerDTO.class);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
        SellerDTO createdSeller = responseEntity.getBody();

        assertThat(createdSeller.getFirstName(), is(sellerDTO.getFirstName()));
        assertThat(createdSeller.getLastName(), is(sellerDTO.getLastName()));
        assertThat(createdSeller.getId(), is(notNullValue()));
        return createdSeller;
    }

    private void getBuyerAndVerifyResponse(Long buyerId, BuyerDTO buyerDTO) {
        BuyerDTO retrievedBuyer = restTemplate.getForObject(getUriForPath(RequestMappings.BUYERS + "/" + buyerId),
                BuyerDTO.class);

        assertThat(retrievedBuyer.getId(), is(buyerId));
        assertThat(retrievedBuyer.getFirstName(), is(buyerDTO.getFirstName()));
        assertThat(retrievedBuyer.getLastName(), is(buyerDTO.getLastName()));
    }

    private BuyerDTO postBuyerAndVerifyResponse(BuyerDTO buyerDTO) {
        ResponseEntity<BuyerDTO> responseEntity = restTemplate.postForEntity(getUriForPath(RequestMappings.BUYERS),
                buyerDTO, BuyerDTO.class);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
        BuyerDTO createdBuyer = responseEntity.getBody();

        assertThat(createdBuyer.getFirstName(), is(buyerDTO.getFirstName()));
        assertThat(createdBuyer.getLastName(), is(buyerDTO.getLastName()));
        assertThat(createdBuyer.getId(), is(notNullValue()));
        return createdBuyer;
    }

    private ProjectDTO getSampleProjectForSeller(SellerDTO createdSeller, OffsetDateTime deadline) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setDescription("description");
        projectDTO.setDeadline(deadline);
        projectDTO.setMaximumBudget(MAX_BUDGET_AMOUNT);
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

    private ProjectDTO postProjectAndVerifyResponse(ProjectDTO projectDTO) {
        ResponseEntity<ProjectDTO> responseEntity = restTemplate.postForEntity(getUriForPath(RequestMappings.PROJECTS),
                projectDTO, ProjectDTO.class);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
        ProjectDTO createdProject = responseEntity.getBody();

        assertThat(createdProject.getDescription(), is(projectDTO.getDescription()));
        assertThat(createdProject.getDeadline().toEpochSecond(), is(projectDTO.getDeadline().toEpochSecond()));
        assertThat(createdProject.getMaximumBudget(), is(projectDTO.getMaximumBudget()));
        assertThat(createdProject.getSellerId(), is(projectDTO.getSellerId()));
        assertThat(createdProject.getLowestBidAmount(), is(nullValue()));
        assertThat(createdProject.getWinningBidderId(), is(nullValue()));
        return createdProject;
    }

}
