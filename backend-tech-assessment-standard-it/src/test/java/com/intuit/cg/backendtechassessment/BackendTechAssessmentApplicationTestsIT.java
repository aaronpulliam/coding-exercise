package com.intuit.cg.backendtechassessment;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

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

import com.intuit.cg.backendtechassessment.controller.requestmappings.RequestMappings;
import com.intuit.cg.backendtechassessment.dto.BuyerDTO;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BackendTechAssessmentApplicationTestsIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getUriForPath(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    public void buyerCanRegister() {
        BuyerDTO buyerDTO = new BuyerDTO();
        buyerDTO.setFirstName("John");
        buyerDTO.setLastName("Smith");
        ResponseEntity<BuyerDTO> responseEntity = restTemplate.postForEntity(getUriForPath(RequestMappings.BUYERS),
                buyerDTO, BuyerDTO.class);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
        BuyerDTO createdBuyer = responseEntity.getBody();
        assertThat(createdBuyer.getFirstName(), is("John"));
        assertThat(createdBuyer.getLastName(), is("Smith"));
        assertThat(createdBuyer.getId(), is(notNullValue()));
    }

    @Test
    public void sellerCanRegister() {
        BuyerDTO buyerDTO = new BuyerDTO();
        buyerDTO.setFirstName("Jane");
        buyerDTO.setLastName("Doe");
        ResponseEntity<BuyerDTO> responseEntity = restTemplate.postForEntity(getUriForPath(RequestMappings.SELLERS),
                buyerDTO, BuyerDTO.class);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
        BuyerDTO createdBuyer = responseEntity.getBody();
        assertThat(createdBuyer.getFirstName(), is("Jane"));
        assertThat(createdBuyer.getLastName(), is("Doe"));
        assertThat(createdBuyer.getId(), is(notNullValue()));
    }

}
