package cz.fit.cvut.taxManagement;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.DeploymentEvent;
import io.camunda.zeebe.process.test.assertions.BpmnAssert;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ZeebeSpringTest
public class TaxManagementProcessDeploymentTest {

    @Autowired
    private ZeebeClient zeebe;

    @Test
    @DisplayName("Test process deployment")
    public void testProcessDeployment() {
        DeploymentEvent deploymentEvent = zeebe.newDeployCommand()
                .addResourceFromClasspath("tax_management.bpmn")
                .send()
                .join();

        BpmnAssert.assertThat(deploymentEvent);
    }
}
