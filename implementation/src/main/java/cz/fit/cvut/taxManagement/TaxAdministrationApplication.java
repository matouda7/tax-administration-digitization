package cz.fit.cvut.taxManagement;

import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.Deployment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableZeebeClient
@Deployment(resources = "classpath:tax_management.bpmn")
public class TaxAdministrationApplication {

    public static void main(final String... args) {
        SpringApplication.run(TaxAdministrationApplication.class, args);
    }

}
