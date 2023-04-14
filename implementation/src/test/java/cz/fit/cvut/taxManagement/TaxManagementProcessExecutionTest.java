package cz.fit.cvut.taxManagement;

import cz.fit.cvut.taxManagement.decisionGenerator.DecisionGenerator;
import cz.fit.cvut.taxManagement.process.TaxAdministrationJobWorkers;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.process.test.api.ZeebeTestEngine;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeoutException;

import static io.camunda.zeebe.process.test.assertions.BpmnAssert.assertThat;
import static io.camunda.zeebe.protocol.Protocol.USER_TASK_JOB_TYPE;
import static io.camunda.zeebe.spring.test.ZeebeTestThreadSupport.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
@ZeebeSpringTest
public class TaxManagementProcessExecutionTest {

    @Autowired
    private ZeebeClient zeebe;

    @Autowired
    private ZeebeTestEngine zeebeTestEngine;

    @MockBean
    private TaxAdministrationJobWorkers jobWorkers;

    @Test
    @DisplayName("Test happy path execution")
    public void testHappyPath() throws Exception {
        Mockito.mock(DecisionGenerator.class).generate(anyString(), any());

        ProcessInstanceEvent processInstance = zeebe.newCreateInstanceCommand()
                .bpmnProcessId("tax_management").latestVersion()
                .send().join();

        //Complete opening user tasks - Start official decision creation, Save additional information, Get facts from tax subject
        waitForAndCompleteOpeningUserTasks();

        //Complete Validate presented facts with result referring that facts are valid
        waitForUserTaskAndComplete("user_task_validate_facts", Collections.singletonMap("correctPresentedFacts", true));

        //Complete Check facts sufficiency with results referring that no additional steps are needed
        waitForUserTaskAndComplete("user_task_check_facts_sufficiency", new HashMap<>() {
            {
                put("requiredTestimony", false);
                put("requiredExpertReport", false);
                put("requiredSpecialRecords", false);
                put("requiredThirdPersonParticipation", false);
                put("requiredTaxNegotiation", false);
                put("witnessNames", "");
                put("participantNames", "");
            }
        });

        //Complete Check final decision values with result referring that update is not needed
        waitForUserTaskAndComplete("user_task_check_final_decision_values", Collections.singletonMap("requiredDecisionValuesUpdate", false));

        //Complete final user tasks - Create official decision, Publish official decision, Get proceedings cost paid
        waitForAndCompleteFinalUserTasks();

        //Wait for the process completion
        waitForProcessInstanceCompleted(processInstance);

        //Assert process execution
        assertThat(processInstance)
                .hasPassedElement("end_event_decision_created")
                .hasNoIncidents()
                .isCompleted();
    }

    @Test
    @DisplayName("Test invalid presented facts path execution")
    public void testInvalidPresentedFactsPath() throws Exception {
        Mockito.mock(DecisionGenerator.class).generate(anyString(), any());

        ProcessInstanceEvent processInstance = zeebe.newCreateInstanceCommand()
                .bpmnProcessId("tax_management").latestVersion()
                .send().join();

        //Complete opening user tasks - Start official decision creation, Save additional information, Get facts from tax subject
        waitForAndCompleteOpeningUserTasks();

        //Complete Validate presented facts with result referring that facts are invalid
        waitForUserTaskAndComplete("user_task_validate_facts", Collections.singletonMap("correctPresentedFacts", false));

        //Update the presented facts
        waitForUserTaskAndComplete("user_task_get_facts", Collections.singletonMap("presentedFacts", "Presented facts version 2"));

        //Complete Validate presented facts with result referring that facts are valid
        waitForUserTaskAndComplete("user_task_validate_facts", Collections.singletonMap("correctPresentedFacts", true));

        //Complete Check facts sufficiency with results referring that no additional steps are needed
        waitForUserTaskAndComplete("user_task_check_facts_sufficiency", new HashMap<>() {
            {
                put("requiredTestimony", false);
                put("requiredExpertReport", false);
                put("requiredSpecialRecords", false);
                put("requiredThirdPersonParticipation", false);
                put("requiredTaxNegotiation", false);
                put("witnessNames", "");
                put("participantNames", "");
            }
        });

        //Complete Check final decision values with result referring that update is not needed
        waitForUserTaskAndComplete("user_task_check_final_decision_values", Collections.singletonMap("requiredDecisionValuesUpdate", false));

        //Complete final user tasks - Create official decision, Publish official decision, Get proceedings cost paid
        waitForAndCompleteFinalUserTasks();

        //Wait for the process completion
        waitForProcessInstanceCompleted(processInstance);

        //Assert process execution
        assertThat(processInstance)
                .hasPassedElement("end_event_decision_created")
                .hasNoIncidents()
                .isCompleted();
    }

    @Test
    @DisplayName("Test incorrect decision values path execution")
    public void testIncorrectDecisionValuesPath() throws Exception {
        Mockito.mock(DecisionGenerator.class).generate(anyString(), any());

        ProcessInstanceEvent processInstance = zeebe.newCreateInstanceCommand()
                .bpmnProcessId("tax_management").latestVersion()
                .send().join();

        //Complete opening user tasks - Start official decision creation, Save additional information, Get facts from tax subject
        waitForAndCompleteOpeningUserTasks();

        //Complete Validate presented facts with result referring that facts are valid
        waitForUserTaskAndComplete("user_task_validate_facts", Collections.singletonMap("correctPresentedFacts", true));

        //Complete Check facts sufficiency with results referring that no additional steps are needed
        waitForUserTaskAndComplete("user_task_check_facts_sufficiency", new HashMap<>() {
            {
                put("requiredTestimony", false);
                put("requiredExpertReport", false);
                put("requiredSpecialRecords", false);
                put("requiredThirdPersonParticipation", false);
                put("requiredTaxNegotiation", false);
                put("witnessNames", "");
                put("participantNames", "");
            }
        });

        //Complete Check final decision values with result referring that update is needed
        waitForUserTaskAndComplete("user_task_check_final_decision_values", Collections.singletonMap("requiredDecisionValuesUpdate", true));

        //Update some final decision values
        waitForUserTaskAndComplete("user_task_update_final_decision_values", new HashMap<>() {
            {
                put("personName", "New name");
                put("personPhone", "+420132456789");
                put("additionalInformation", "Additional information - new version");
            }
        });

        //Complete Check final decision values with result referring that update is not needed
        waitForUserTaskAndComplete("user_task_check_final_decision_values", Collections.singletonMap("requiredDecisionValuesUpdate", false));

        //Complete final user tasks - Create official decision, Publish official decision, Get proceedings cost paid
        waitForAndCompleteFinalUserTasks();

        //Wait for the process completion
        waitForProcessInstanceCompleted(processInstance);

        //Assert process execution
        assertThat(processInstance)
                .hasPassedElement("end_event_decision_created")
                .hasNoIncidents()
                .isCompleted();
    }

    @Test
    @DisplayName("Test testimony creation path execution")
    public void testTestimonyCreationPath() throws Exception {
        Mockito.mock(DecisionGenerator.class).generate(anyString(), any());

        ProcessInstanceEvent processInstance = zeebe.newCreateInstanceCommand()
                .bpmnProcessId("tax_management").latestVersion()
                .send().join();

        //Complete opening user tasks - Start official decision creation, Save additional information, Get facts from tax subject
        waitForAndCompleteOpeningUserTasks();

        //Complete Validate presented facts with result referring that facts are valid
        waitForUserTaskAndComplete("user_task_validate_facts", Collections.singletonMap("correctPresentedFacts", true));

        //Complete Check facts sufficiency with results referring that no additional steps are needed
        waitForUserTaskAndComplete("user_task_check_facts_sufficiency", new HashMap<>() {
            {
                put("requiredTestimony", true);
                put("requiredExpertReport", false);
                put("requiredSpecialRecords", false);
                put("requiredThirdPersonParticipation", false);
                put("requiredTaxNegotiation", false);
                put("witnessNames", "Daniel Matousek");
                put("participantNames", "");
            }
        });

        //Complete testimony tasks - Save testimony information, Notify tax subject about testimony, Get testimony
        waitForAndCompleteTestimonyUserTasks();

        //Complete Check added information with results referring that tax negotiation is not needed
        waitForUserTaskAndComplete("user_task_check_added_information", Collections.singletonMap("requiredTaxNegotiation", false));

        //Complete Check final decision values with result referring that update is not needed
        waitForUserTaskAndComplete("user_task_check_final_decision_values", Collections.singletonMap("requiredDecisionValuesUpdate", false));

        //Complete final user tasks - Create official decision, Publish official decision, Get proceedings cost paid
        waitForAndCompleteFinalUserTasks();

        //Wait for the process completion
        waitForProcessInstanceCompleted(processInstance);

        //Assert process execution
        assertThat(processInstance)
                .hasPassedElement("end_event_decision_created")
                .hasNoIncidents()
                .isCompleted();
    }

    @Test
    @DisplayName("Test multiple testimonies creation path execution")
    public void testMultipleTestimoniesCreationPath() throws Exception {
        Mockito.mock(DecisionGenerator.class).generate(anyString(), any());

        ProcessInstanceEvent processInstance = zeebe.newCreateInstanceCommand()
                .bpmnProcessId("tax_management").latestVersion()
                .send().join();

        //Complete opening user tasks - Start official decision creation, Save additional information, Get facts from tax subject
        waitForAndCompleteOpeningUserTasks();

        //Complete Validate presented facts with result referring that facts are valid
        waitForUserTaskAndComplete("user_task_validate_facts", Collections.singletonMap("correctPresentedFacts", true));

        //Complete Check facts sufficiency with results referring that no additional steps are needed
        waitForUserTaskAndComplete("user_task_check_facts_sufficiency", new HashMap<>() {
            {
                put("requiredTestimony", true);
                put("requiredExpertReport", false);
                put("requiredSpecialRecords", false);
                put("requiredThirdPersonParticipation", false);
                put("requiredTaxNegotiation", false);
                put("witnessNames", "Daniel Matousek, Jan Dietz");
                put("participantNames", "");
            }
        });

        //Complete testimony tasks for first witness - Save testimony information, Notify tax subject about testimony, Get testimony
        waitForAndCompleteTestimonyUserTasks();

        //Complete testimony tasks for second witness - Save testimony information, Notify tax subject about testimony, Get testimony
        waitForAndCompleteTestimonyUserTasks();

        //Complete Check added information with results referring that tax negotiation is not needed
        waitForUserTaskAndComplete("user_task_check_added_information", Collections.singletonMap("requiredTaxNegotiation", false));

        //Complete Check final decision values with result referring that update is not needed
        waitForUserTaskAndComplete("user_task_check_final_decision_values", Collections.singletonMap("requiredDecisionValuesUpdate", false));

        //Complete final user tasks - Create official decision, Publish official decision, Get proceedings cost paid
        waitForAndCompleteFinalUserTasks();

        //Wait for the process completion
        waitForProcessInstanceCompleted(processInstance);

        //Assert process execution
        assertThat(processInstance)
                .hasPassedElement("end_event_decision_created")
                .hasNoIncidents()
                .isCompleted();
    }

    @Test
    @DisplayName("Test expert report creation path execution")
    public void testExpertReportCreationPath() throws Exception {
        Mockito.mock(DecisionGenerator.class).generate(anyString(), any());

        ProcessInstanceEvent processInstance = zeebe.newCreateInstanceCommand()
                .bpmnProcessId("tax_management").latestVersion()
                .send().join();

        //Complete opening user tasks - Start official decision creation, Save additional information, Get facts from tax subject
        waitForAndCompleteOpeningUserTasks();

        //Complete Validate presented facts with result referring that facts are valid
        waitForUserTaskAndComplete("user_task_validate_facts", Collections.singletonMap("correctPresentedFacts", true));

        //Complete Check facts sufficiency with results referring that no additional steps are needed
        waitForUserTaskAndComplete("user_task_check_facts_sufficiency", new HashMap<>() {
            {
                put("requiredTestimony", false);
                put("requiredExpertReport", true);
                put("requiredSpecialRecords", false);
                put("requiredThirdPersonParticipation", false);
                put("requiredTaxNegotiation", false);
                put("witnessNames", "");
                put("participantNames", "");
            }
        });

        //Complete expert report tasks - Create expert report
        waitForAndCompleteExpertReportUserTasks();

        //Complete Check added information with results referring that tax negotiation is not needed
        waitForUserTaskAndComplete("user_task_check_added_information", Collections.singletonMap("requiredTaxNegotiation", false));

        //Complete Check final decision values with result referring that update is not needed
        waitForUserTaskAndComplete("user_task_check_final_decision_values", Collections.singletonMap("requiredDecisionValuesUpdate", false));

        //Complete final user tasks - Create official decision, Publish official decision, Get proceedings cost paid
        waitForAndCompleteFinalUserTasks();

        //Wait for the process completion
        waitForProcessInstanceCompleted(processInstance);

        //Assert process execution
        assertThat(processInstance)
                .hasPassedElement("end_event_decision_created")
                .hasNoIncidents()
                .isCompleted();
    }

    @Test
    @DisplayName("Test special records creation main path execution")
    public void testSpecialRecordsCreationPath() throws Exception {
        Mockito.mock(DecisionGenerator.class).generate(anyString(), any());

        ProcessInstanceEvent processInstance = zeebe.newCreateInstanceCommand()
                .bpmnProcessId("tax_management").latestVersion()
                .send().join();

        //Complete opening user tasks - Start official decision creation, Save additional information, Get facts from tax subject
        waitForAndCompleteOpeningUserTasks();

        //Complete Validate presented facts with result referring that facts are valid
        waitForUserTaskAndComplete("user_task_validate_facts", Collections.singletonMap("correctPresentedFacts", true));

        //Complete Check facts sufficiency with results referring that no additional steps are needed
        waitForUserTaskAndComplete("user_task_check_facts_sufficiency", new HashMap<>() {
            {
                put("requiredTestimony", false);
                put("requiredExpertReport", false);
                put("requiredSpecialRecords", true);
                put("requiredThirdPersonParticipation", false);
                put("requiredTaxNegotiation", false);
                put("witnessNames", "");
                put("participantNames", "");
            }
        });

        //Complete special records tasks - Save special records information, Notify tax subject about special records creation, Collect special records
        waitForAndCompleteSpecialRecordsUserTasks();

        //Complete Check added information with results referring that tax negotiation is not needed
        waitForUserTaskAndComplete("user_task_check_added_information", Collections.singletonMap("requiredTaxNegotiation", false));

        //Complete Check final decision values with result referring that update is not needed
        waitForUserTaskAndComplete("user_task_check_final_decision_values", Collections.singletonMap("requiredDecisionValuesUpdate", false));

        //Complete final user tasks - Create official decision, Publish official decision, Get proceedings cost paid
        waitForAndCompleteFinalUserTasks();

        //Wait for the process completion
        waitForProcessInstanceCompleted(processInstance);

        //Assert process execution
        assertThat(processInstance)
                .hasPassedElement("end_event_decision_created")
                .hasNoIncidents()
                .isCompleted();
    }

    @Test
    @DisplayName("Test special records creation path with incorrect special records execution")
    public void testSpecialRecordsCreationPathWithIncorrectRecords() throws Exception {
        Mockito.mock(DecisionGenerator.class).generate(anyString(), any());

        ProcessInstanceEvent processInstance = zeebe.newCreateInstanceCommand()
                .bpmnProcessId("tax_management").latestVersion()
                .send().join();

        //Complete opening user tasks - Start official decision creation, Save additional information, Get facts from tax subject
        waitForAndCompleteOpeningUserTasks();

        //Complete Validate presented facts with result referring that facts are valid
        waitForUserTaskAndComplete("user_task_validate_facts", Collections.singletonMap("correctPresentedFacts", true));

        //Complete Check facts sufficiency with results referring that no additional steps are needed
        waitForUserTaskAndComplete("user_task_check_facts_sufficiency", new HashMap<>() {
            {
                put("requiredTestimony", false);
                put("requiredExpertReport", false);
                put("requiredSpecialRecords", true);
                put("requiredThirdPersonParticipation", false);
                put("requiredTaxNegotiation", false);
                put("witnessNames", "");
                put("participantNames", "");
            }
        });

        //Complete Save special records information with corresponding variables
        waitForUserTaskAndComplete("user_task_save_special_records_information", new HashMap<>() {
            {
                put("specialRecordsMatter", "Matter of special records");
                put("specialRecordsDuration", "5");     //Five seconds to conform the waitForIdleState timeout
            }
        });

        //Complete Notify tax subject about special records creation
        waitForUserTaskAndComplete("user_task_notify_tax_subject_about_special_records_creation", Collections.EMPTY_MAP);

        //Complete Collect special records with corresponding variable
        waitForUserTaskAndComplete("user_task_collect_special_records", new HashMap<>() {
            {
                put("specialRecords", "Special records");
                put("correctSpecialRecords", false);
            }
        });

        //Complete Notify tax subject about special records creation
        waitForUserTaskAndComplete("user_task_enlighten_special_records_creator", Collections.EMPTY_MAP);

        //Complete Collect special records with corresponding variable now with corrected special records
        waitForUserTaskAndComplete("user_task_collect_special_records", new HashMap<>() {
            {
                put("specialRecords", "Corrected special records");
                put("correctSpecialRecords", true);
            }
        });

        //Complete Check added information with results referring that tax negotiation is not needed
        waitForUserTaskAndComplete("user_task_check_added_information", Collections.singletonMap("requiredTaxNegotiation", false));

        //Complete Check final decision values with result referring that update is not needed
        waitForUserTaskAndComplete("user_task_check_final_decision_values", Collections.singletonMap("requiredDecisionValuesUpdate", false));

        //Complete final user tasks - Create official decision, Publish official decision, Get proceedings cost paid
        waitForAndCompleteFinalUserTasks();

        //Wait for the process completion
        waitForProcessInstanceCompleted(processInstance);

        //Assert process execution
        assertThat(processInstance)
                .hasPassedElement("end_event_decision_created")
                .hasNoIncidents()
                .isCompleted();
    }

    @Test
    @DisplayName("Test proceedings participation main path execution")
    public void testProceedingsParticipationPath() throws Exception {
        Mockito.mock(DecisionGenerator.class).generate(anyString(), any());

        ProcessInstanceEvent processInstance = zeebe.newCreateInstanceCommand()
                .bpmnProcessId("tax_management").latestVersion()
                .send().join();

        //Complete opening user tasks - Start official decision creation, Save additional information, Get facts from tax subject
        waitForAndCompleteOpeningUserTasks();

        //Complete Validate presented facts with result referring that facts are valid
        waitForUserTaskAndComplete("user_task_validate_facts", Collections.singletonMap("correctPresentedFacts", true));

        //Complete Check facts sufficiency with results referring that no additional steps are needed
        waitForUserTaskAndComplete("user_task_check_facts_sufficiency", new HashMap<>() {
            {
                put("requiredTestimony", false);
                put("requiredExpertReport", false);
                put("requiredSpecialRecords", false);
                put("requiredThirdPersonParticipation", true);
                put("requiredTaxNegotiation", false);
                put("witnessNames", "");
                put("participantNames", "Daniel Matousek");
            }
        });

        //Complete proceedings participation tasks - Fill in proceedings participant information, Summon participant, Get participant contribution,
        // Assess entitlement to compensation
        waitForAndCompleteProceedingsParticipationUserTasks();

        //Complete Check added information with results referring that tax negotiation is not needed
        waitForUserTaskAndComplete("user_task_check_added_information", Collections.singletonMap("requiredTaxNegotiation", false));

        //Complete Check final decision values with result referring that update is not needed
        waitForUserTaskAndComplete("user_task_check_final_decision_values", Collections.singletonMap("requiredDecisionValuesUpdate", false));

        //Complete final user tasks - Create official decision, Publish official decision, Get proceedings cost paid
        waitForAndCompleteFinalUserTasks();

        //Wait for the process completion
        waitForProcessInstanceCompleted(processInstance);

        //Assert process execution
        assertThat(processInstance)
                .hasPassedElement("end_event_decision_created")
                .hasNoIncidents()
                .isCompleted();
    }

    @Test
    @DisplayName("Test multiple proceedings participation path execution")
    public void testMultipleProceedingsParticipationPath() throws Exception {
        Mockito.mock(DecisionGenerator.class).generate(anyString(), any());

        ProcessInstanceEvent processInstance = zeebe.newCreateInstanceCommand()
                .bpmnProcessId("tax_management").latestVersion()
                .send().join();

        //Complete opening user tasks - Start official decision creation, Save additional information, Get facts from tax subject
        waitForAndCompleteOpeningUserTasks();

        //Complete Validate presented facts with result referring that facts are valid
        waitForUserTaskAndComplete("user_task_validate_facts", Collections.singletonMap("correctPresentedFacts", true));

        //Complete Check facts sufficiency with results referring that no additional steps are needed
        waitForUserTaskAndComplete("user_task_check_facts_sufficiency", new HashMap<>() {
            {
                put("requiredTestimony", false);
                put("requiredExpertReport", false);
                put("requiredSpecialRecords", false);
                put("requiredThirdPersonParticipation", true);
                put("requiredTaxNegotiation", false);
                put("witnessNames", "");
                put("participantNames", "Daniel Matousek, Jan Dietz");
            }
        });

        //Complete proceedings participation tasks for first participant - Fill in proceedings participant information, Summon participant, Get participant contribution,
        // Assess entitlement to compensation
        waitForAndCompleteProceedingsParticipationUserTasks();

        //Complete proceedings participation tasks for second participant - Fill in proceedings participant information, Summon participant, Get participant contribution,
        // Assess entitlement to compensation
        waitForAndCompleteProceedingsParticipationUserTasks();

        //Complete Check added information with results referring that tax negotiation is not needed
        waitForUserTaskAndComplete("user_task_check_added_information", Collections.singletonMap("requiredTaxNegotiation", false));

        //Complete Check final decision values with result referring that update is not needed
        waitForUserTaskAndComplete("user_task_check_final_decision_values", Collections.singletonMap("requiredDecisionValuesUpdate", false));

        //Complete final user tasks - Create official decision, Publish official decision, Get proceedings cost paid
        waitForAndCompleteFinalUserTasks();

        //Wait for the process completion
        waitForProcessInstanceCompleted(processInstance);

        //Assert process execution
        assertThat(processInstance)
                .hasPassedElement("end_event_decision_created")
                .hasNoIncidents()
                .isCompleted();
    }

    @Test
    @DisplayName("Test proceedings participation path with attachment execution")
    public void testProceedingsParticipationPathWithAttachment() throws Exception {
        Mockito.mock(DecisionGenerator.class).generate(anyString(), any());

        ProcessInstanceEvent processInstance = zeebe.newCreateInstanceCommand()
                .bpmnProcessId("tax_management").latestVersion()
                .send().join();

        //Complete opening user tasks - Start official decision creation, Save additional information, Get facts from tax subject
        waitForAndCompleteOpeningUserTasks();

        //Complete Validate presented facts with result referring that facts are valid
        waitForUserTaskAndComplete("user_task_validate_facts", Collections.singletonMap("correctPresentedFacts", true));

        //Complete Check facts sufficiency with results referring that no additional steps are needed
        waitForUserTaskAndComplete("user_task_check_facts_sufficiency", new HashMap<>() {
            {
                put("requiredTestimony", false);
                put("requiredExpertReport", false);
                put("requiredSpecialRecords", false);
                put("requiredThirdPersonParticipation", true);
                put("requiredTaxNegotiation", false);
                put("witnessNames", "");
                put("participantNames", "Daniel Matousek");
            }
        });

        //Complete Fill in proceedings participant information with corresponding variables
        waitForUserTaskAndComplete("user_task_fill_in_proceedings_participant_information", new HashMap<>() {
            {
                put("participantAddress", "Some Address, 123, Some City");
                put("participantEmail", "participant@someemail.cz");
                put("participantPhone", "+420123456000");
                put("matterOfParticipation", "Matter of participation");
            }
        });

        //Complete Summon participant with result referring that attachment is needed
        waitForUserTaskAndComplete("user_task_summon_participant", Collections.singletonMap("requiredParticipantAttachment", true));

        //Complete Attach participant
        waitForUserTaskAndComplete("user_task_attach_participant", Collections.EMPTY_MAP);

        //Complete Get participant contribution with corresponding variable
        waitForUserTaskAndComplete("user_task_get_participant_contribution", Collections.singletonMap("participantContribution", "Participant contribution"));

        //Complete Assess entitlement to compensation with corresponding variable
        waitForUserTaskAndComplete("user_task_assess_entitlement_to_compensation", Collections.singletonMap("requiredCompensationPayment", false));

        //Complete Check added information with results referring that tax negotiation is not needed
        waitForUserTaskAndComplete("user_task_check_added_information", Collections.singletonMap("requiredTaxNegotiation", false));

        //Complete Check final decision values with result referring that update is not needed
        waitForUserTaskAndComplete("user_task_check_final_decision_values", Collections.singletonMap("requiredDecisionValuesUpdate", false));

        //Complete final user tasks - Create official decision, Publish official decision, Get proceedings cost paid
        waitForAndCompleteFinalUserTasks();

        //Wait for the process completion
        waitForProcessInstanceCompleted(processInstance);

        //Assert process execution
        assertThat(processInstance)
                .hasPassedElement("end_event_decision_created")
                .hasNoIncidents()
                .isCompleted();
    }

    @Test
    @DisplayName("Test proceedings participation path with compensation payment execution")
    public void testProceedingsParticipationPathWithCompensationPayment() throws Exception {
        Mockito.mock(DecisionGenerator.class).generate(anyString(), any());

        ProcessInstanceEvent processInstance = zeebe.newCreateInstanceCommand()
                .bpmnProcessId("tax_management").latestVersion()
                .send().join();

        //Complete opening user tasks - Start official decision creation, Save additional information, Get facts from tax subject
        waitForAndCompleteOpeningUserTasks();

        //Complete Validate presented facts with result referring that facts are valid
        waitForUserTaskAndComplete("user_task_validate_facts", Collections.singletonMap("correctPresentedFacts", true));

        //Complete Check facts sufficiency with results referring that no additional steps are needed
        waitForUserTaskAndComplete("user_task_check_facts_sufficiency", new HashMap<>() {
            {
                put("requiredTestimony", false);
                put("requiredExpertReport", false);
                put("requiredSpecialRecords", false);
                put("requiredThirdPersonParticipation", true);
                put("requiredTaxNegotiation", false);
                put("witnessNames", "");
                put("participantNames", "Daniel Matousek");
            }
        });

        //Complete Fill in proceedings participant information with corresponding variables
        waitForUserTaskAndComplete("user_task_fill_in_proceedings_participant_information", new HashMap<>() {
            {
                put("participantAddress", "Some Address, 123, Some City");
                put("participantEmail", "participant@someemail.cz");
                put("participantPhone", "+420123456000");
                put("matterOfParticipation", "Matter of participation");
            }
        });

        //Complete Summon participant with result referring that attachment is not needed
        waitForUserTaskAndComplete("user_task_summon_participant", Collections.singletonMap("requiredParticipantAttachment", false));

        //Complete Get participant contribution with corresponding variable
        waitForUserTaskAndComplete("user_task_get_participant_contribution", Collections.singletonMap("participantContribution", "Participant contribution"));

        //Complete Assess entitlement to compensation with corresponding variables
        waitForUserTaskAndComplete("user_task_assess_entitlement_to_compensation", new HashMap<>() {
            {
                put("requiredCompensationPayment", true);
                put("compensationPaymentAmount", 12345);
            }
        });

        //Complete Pay compensation with corresponding variable
        waitForUserTaskAndComplete("user_task_pay_compensation", Collections.singletonMap("participantCompensationPaymentId", "ID_1235464"));

        //Complete Check added information with results referring that tax negotiation is not needed
        waitForUserTaskAndComplete("user_task_check_added_information", Collections.singletonMap("requiredTaxNegotiation", false));

        //Complete Check final decision values with result referring that update is not needed
        waitForUserTaskAndComplete("user_task_check_final_decision_values", Collections.singletonMap("requiredDecisionValuesUpdate", false));

        //Complete final user tasks - Create official decision, Publish official decision, Get proceedings cost paid
        waitForAndCompleteFinalUserTasks();

        //Wait for the process completion
        waitForProcessInstanceCompleted(processInstance);

        //Assert process execution
        assertThat(processInstance)
                .hasPassedElement("end_event_decision_created")
                .hasNoIncidents()
                .isCompleted();
    }

    @Test
    @DisplayName("Test tax negotiation with testimony path execution")
    public void testTestimonyAndTaxNegotiationPath() throws Exception {
        Mockito.mock(DecisionGenerator.class).generate(anyString(), any());

        ProcessInstanceEvent processInstance = zeebe.newCreateInstanceCommand()
                .bpmnProcessId("tax_management").latestVersion()
                .send().join();

        //Complete opening user tasks - Start official decision creation, Save additional information, Get facts from tax subject
        waitForAndCompleteOpeningUserTasks();

        //Complete Validate presented facts with result referring that facts are valid
        waitForUserTaskAndComplete("user_task_validate_facts", Collections.singletonMap("correctPresentedFacts", true));

        //Complete Check facts sufficiency with results referring that no additional steps are needed
        waitForUserTaskAndComplete("user_task_check_facts_sufficiency", new HashMap<>() {
            {
                put("requiredTestimony", true);
                put("requiredExpertReport", false);
                put("requiredSpecialRecords", false);
                put("requiredThirdPersonParticipation", false);
                put("requiredTaxNegotiation", false);
                put("witnessNames", "Daniel Matousek");
                put("participantNames", "");
            }
        });

        //Complete testimony tasks - Save testimony information, Notify tax subject about testimony, Get testimony
        waitForAndCompleteTestimonyUserTasks();

        //Complete Check added information with results referring that tax negotiation is needed
        waitForUserTaskAndComplete("user_task_check_added_information", Collections.singletonMap("requiredTaxNegotiation", true));

        //Complete Get negotiated tax with corresponding variables
        waitForUserTaskAndComplete("user_task_get_negotiated_tax", new HashMap<>() {
            {
                put("negotiatedTaxValue", "12345");
                put("negotiatedTaxJustification", "Negotiated tax justification");
            }
        });

        //Complete Check final decision values with result referring that update is not needed
        waitForUserTaskAndComplete("user_task_check_final_decision_values", Collections.singletonMap("requiredDecisionValuesUpdate", false));

        //Complete final user tasks - Create official decision, Publish official decision, Get proceedings cost paid
        waitForAndCompleteFinalUserTasks();

        //Wait for the process completion
        waitForProcessInstanceCompleted(processInstance);

        //Assert process execution
        assertThat(processInstance)
                .hasPassedElement("end_event_decision_created")
                .hasNoIncidents()
                .isCompleted();
    }

    @Test
    @DisplayName("Test tax negotiation with expert report path execution")
    public void testExpertReportAndTaxNegotiationPath() throws Exception {
        Mockito.mock(DecisionGenerator.class).generate(anyString(), any());

        ProcessInstanceEvent processInstance = zeebe.newCreateInstanceCommand()
                .bpmnProcessId("tax_management").latestVersion()
                .send().join();

        //Complete opening user tasks - Start official decision creation, Save additional information, Get facts from tax subject
        waitForAndCompleteOpeningUserTasks();

        //Complete Validate presented facts with result referring that facts are valid
        waitForUserTaskAndComplete("user_task_validate_facts", Collections.singletonMap("correctPresentedFacts", true));

        //Complete Check facts sufficiency with results referring that no additional steps are needed
        waitForUserTaskAndComplete("user_task_check_facts_sufficiency", new HashMap<>() {
            {
                put("requiredTestimony", false);
                put("requiredExpertReport", true);
                put("requiredSpecialRecords", false);
                put("requiredThirdPersonParticipation", false);
                put("requiredTaxNegotiation", false);
                put("witnessNames", "Daniel Matousek");
                put("participantNames", "");
            }
        });

        //Complete testimony tasks - Save testimony information, Notify tax subject about testimony, Get testimony
        waitForAndCompleteExpertReportUserTasks();

        //Complete Check added information with results referring that tax negotiation is needed
        waitForUserTaskAndComplete("user_task_check_added_information", Collections.singletonMap("requiredTaxNegotiation", true));

        //Complete Get negotiated tax with corresponding variables
        waitForUserTaskAndComplete("user_task_get_negotiated_tax", new HashMap<>() {
            {
                put("negotiatedTaxValue", "12345");
                put("negotiatedTaxJustification", "Negotiated tax justification");
            }
        });

        //Complete Check final decision values with result referring that update is not needed
        waitForUserTaskAndComplete("user_task_check_final_decision_values", Collections.singletonMap("requiredDecisionValuesUpdate", false));

        //Complete final user tasks - Create official decision, Publish official decision, Get proceedings cost paid
        waitForAndCompleteFinalUserTasks();

        //Wait for the process completion
        waitForProcessInstanceCompleted(processInstance);

        //Assert process execution
        assertThat(processInstance)
                .hasPassedElement("end_event_decision_created")
                .hasNoIncidents()
                .isCompleted();
    }

    @Test
    @DisplayName("Test tax negotiation with special records path execution")
    public void testSpecialRecordsAndTaxNegotiationPath() throws Exception {
        Mockito.mock(DecisionGenerator.class).generate(anyString(), any());

        ProcessInstanceEvent processInstance = zeebe.newCreateInstanceCommand()
                .bpmnProcessId("tax_management").latestVersion()
                .send().join();

        //Complete opening user tasks - Start official decision creation, Save additional information, Get facts from tax subject
        waitForAndCompleteOpeningUserTasks();

        //Complete Validate presented facts with result referring that facts are valid
        waitForUserTaskAndComplete("user_task_validate_facts", Collections.singletonMap("correctPresentedFacts", true));

        //Complete Check facts sufficiency with results referring that no additional steps are needed
        waitForUserTaskAndComplete("user_task_check_facts_sufficiency", new HashMap<>() {
            {
                put("requiredTestimony", false);
                put("requiredExpertReport", false);
                put("requiredSpecialRecords", true);
                put("requiredThirdPersonParticipation", false);
                put("requiredTaxNegotiation", false);
                put("witnessNames", "Daniel Matousek");
                put("participantNames", "");
            }
        });

        //Complete testimony tasks - Save testimony information, Notify tax subject about testimony, Get testimony
        waitForAndCompleteSpecialRecordsUserTasks();

        //Complete Check added information with results referring that tax negotiation is needed
        waitForUserTaskAndComplete("user_task_check_added_information", Collections.singletonMap("requiredTaxNegotiation", true));

        //Complete Get negotiated tax with corresponding variables
        waitForUserTaskAndComplete("user_task_get_negotiated_tax", new HashMap<>() {
            {
                put("negotiatedTaxValue", "12345");
                put("negotiatedTaxJustification", "Negotiated tax justification");
            }
        });

        //Complete Check final decision values with result referring that update is not needed
        waitForUserTaskAndComplete("user_task_check_final_decision_values", Collections.singletonMap("requiredDecisionValuesUpdate", false));

        //Complete final user tasks - Create official decision, Publish official decision, Get proceedings cost paid
        waitForAndCompleteFinalUserTasks();

        //Wait for the process completion
        waitForProcessInstanceCompleted(processInstance);

        //Assert process execution
        assertThat(processInstance)
                .hasPassedElement("end_event_decision_created")
                .hasNoIncidents()
                .isCompleted();
    }

    @Test
    @DisplayName("Test tax negotiation with proceedings participation path execution")
    public void testProceedingsParticipationAndTaxNegotiationPath() throws Exception {
        Mockito.mock(DecisionGenerator.class).generate(anyString(), any());

        ProcessInstanceEvent processInstance = zeebe.newCreateInstanceCommand()
                .bpmnProcessId("tax_management").latestVersion()
                .send().join();

        //Complete opening user tasks - Start official decision creation, Save additional information, Get facts from tax subject
        waitForAndCompleteOpeningUserTasks();

        //Complete Validate presented facts with result referring that facts are valid
        waitForUserTaskAndComplete("user_task_validate_facts", Collections.singletonMap("correctPresentedFacts", true));

        //Complete Check facts sufficiency with results referring that no additional steps are needed
        waitForUserTaskAndComplete("user_task_check_facts_sufficiency", new HashMap<>() {
            {
                put("requiredTestimony", false);
                put("requiredExpertReport", false);
                put("requiredSpecialRecords", false);
                put("requiredThirdPersonParticipation", true);
                put("requiredTaxNegotiation", false);
                put("witnessNames", "");
                put("participantNames", "Daniel Matousek");
            }
        });

        //Complete testimony tasks - Save testimony information, Notify tax subject about testimony, Get testimony
        waitForAndCompleteProceedingsParticipationUserTasks();

        //Complete Check added information with results referring that tax negotiation is needed
        waitForUserTaskAndComplete("user_task_check_added_information", Collections.singletonMap("requiredTaxNegotiation", true));

        //Complete Get negotiated tax with corresponding variables
        waitForUserTaskAndComplete("user_task_get_negotiated_tax", new HashMap<>() {
            {
                put("negotiatedTaxValue", "12345");
                put("negotiatedTaxJustification", "Negotiated tax justification");
            }
        });

        //Complete Check final decision values with result referring that update is not needed
        waitForUserTaskAndComplete("user_task_check_final_decision_values", Collections.singletonMap("requiredDecisionValuesUpdate", false));

        //Complete final user tasks - Create official decision, Publish official decision, Get proceedings cost paid
        waitForAndCompleteFinalUserTasks();

        //Wait for the process completion
        waitForProcessInstanceCompleted(processInstance);

        //Assert process execution
        assertThat(processInstance)
                .hasPassedElement("end_event_decision_created")
                .hasNoIncidents()
                .isCompleted();
    }

    private void waitForAndCompleteOpeningUserTasks() throws Exception {
        //Complete Start official decision creation with corresponding variables
        waitForUserTaskAndComplete("user_task_start_official_decision_creation", new HashMap<>() {
            {
                put("personName", "Daniel Matousek");
                put("personAddress", "Thakurova 25, Praha 6");
                put("personEmail", "matousek@someemail.com");
                put("personPhone", "+420123456789");
            }
        });

        //Complete Save additional information with corresponding variable
        waitForUserTaskAndComplete("user_task_save_additional_information", Collections.singletonMap("additionalInformation", "Additional information"));

        //Complete Get facts from tax subject with corresponding variable
        waitForUserTaskAndComplete("user_task_get_facts", Collections.singletonMap("presentedFacts", "Presented facts"));
    }

    private void waitForAndCompleteTestimonyUserTasks() throws Exception {
        //Complete Save testimony information with corresponding variables
        waitForUserTaskAndComplete("user_task_save_testimony_information", new HashMap<>() {
            {
                put("witnessAddress", "Some Address, 123, Some City");
                put("witnessEmail", "witness@someemail.cz");
                put("witnessPhone", "+420123456987");
                put("matterOfTestimony", "Matter of testimony");
            }
        });

        //Complete Notify tax subject about testimony
        waitForUserTaskAndComplete("user_task_notify_tax_subject_about_testimony", Collections.EMPTY_MAP);

        //Complete Get facts from tax subject with corresponding variable
        waitForUserTaskAndComplete("user_task_get_testimony", Collections.singletonMap("testimony", "Testimony"));
    }

    private void waitForAndCompleteExpertReportUserTasks() throws Exception {
        //Complete Create expert report with corresponding variable
        waitForUserTaskAndComplete("user_task_create_expert_report", Collections.singletonMap("expertReport", "Expert report"));
    }

    private void waitForAndCompleteSpecialRecordsUserTasks() throws Exception {
        //Complete Save special records information with corresponding variables
        waitForUserTaskAndComplete("user_task_save_special_records_information", new HashMap<>() {
            {
                put("specialRecordsMatter", "Matter of special records");
                put("specialRecordsDuration", "5");     //Five seconds to conform the waitForIdleState timeout
            }
        });

        //Complete Notify tax subject about special records creation
        waitForUserTaskAndComplete("user_task_notify_tax_subject_about_special_records_creation", Collections.EMPTY_MAP);

        //Complete Collect special records with corresponding variable
        waitForUserTaskAndComplete("user_task_collect_special_records", new HashMap<>() {
            {
                put("specialRecords", "Special records");
                put("correctSpecialRecords", true);
            }
        });
    }

    private void waitForAndCompleteProceedingsParticipationUserTasks() throws Exception {
        //Complete Fill in proceedings participant information with corresponding variables
        waitForUserTaskAndComplete("user_task_fill_in_proceedings_participant_information", new HashMap<>() {
            {
                put("participantAddress", "Some Address, 123, Some City");
                put("participantEmail", "participant@someemail.cz");
                put("participantPhone", "+420123456000");
                put("matterOfParticipation", "Matter of participation");
            }
        });

        //Complete Summon participant with result referring that attachment is not needed
        waitForUserTaskAndComplete("user_task_summon_participant", Collections.singletonMap("requiredParticipantAttachment", false));

        //Complete Get participant contribution with corresponding variable
        waitForUserTaskAndComplete("user_task_get_participant_contribution", Collections.singletonMap("participantContribution", "Participant contribution"));

        //Complete Assess entitlement to compensation with corresponding variable
        waitForUserTaskAndComplete("user_task_assess_entitlement_to_compensation", Collections.singletonMap("requiredCompensationPayment", false));
    }

    private void waitForAndCompleteFinalUserTasks() throws Exception {
        //Complete Create official decision with corresponding variables
        waitForUserTaskAndComplete("user_task_create_official_decision", new HashMap<>() {
            {
                put("taxAmount", 12345);
                put("taxJustification", "Tax justification");
            }
        });

        //Complete Publish official decision
        waitForUserTaskAndComplete("user_task_publish_official_decision", Collections.EMPTY_MAP);

        //Complete Get proceedings cost paid with corresponding variable
        waitForUserTaskAndComplete("user_task_get_proceedings_cost_paid", Collections.singletonMap("proceedingCostPaymentId", "ID_payment"));
    }

    private void waitForUserTaskAndComplete(String userTaskId, Map<String, Object> variables) throws InterruptedException, TimeoutException {
        //Let the workflow engine complete the tasks on background
        zeebeTestEngine.waitForIdleState(Duration.ofSeconds(10));

        //Now get all user tasks
        List<ActivatedJob> jobs = zeebe.newActivateJobsCommand().jobType(USER_TASK_JOB_TYPE).maxJobsToActivate(1).workerName("waitForUserTaskAndComplete").send().join().getJobs();

        assertTrue(jobs.size() > 0, "Job for user task '" + userTaskId + "' does not exist");
        ActivatedJob userTaskJob = jobs.get(0);
        //Assert it is the right one
        if (userTaskId != null) {
            assertEquals(userTaskId, userTaskJob.getElementId());
        }

        //Complete it passing the variables
        if (variables != null && variables.size() > 0) {
            zeebe.newCompleteCommand(userTaskJob.getKey()).variables(variables).send().join();
        } else {
            zeebe.newCompleteCommand(userTaskJob.getKey()).send().join();
        }
    }
}
