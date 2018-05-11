package com.intuit.cg.backendtechassessment;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.intuit.cg.backendtechassessment.service.ModelConverter;

@SpringBootApplication
@EnableTransactionManagement
public class BackendTechAssessmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendTechAssessmentApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public ModelConverter modelConverter() {
        return new ModelConverter(modelMapper());
    }

}
