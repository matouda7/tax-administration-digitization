package cz.fit.cvut.taxManagement.process;

import cz.fit.cvut.taxManagement.decisionGenerator.DecisionGenerator;
import cz.fit.cvut.taxManagement.resolvers.participantContributionResolver.ParticipantContributionResolver;
import cz.fit.cvut.taxManagement.resolvers.testimonyResolver.TestimonyResolver;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;

import io.camunda.zeebe.spring.client.annotation.Variable;
import io.camunda.zeebe.spring.client.annotation.VariablesAsType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class TaxAdministrationJobWorkers {

    private static final Logger log = LoggerFactory.getLogger(TaxAdministrationJobWorkers.class);

    @Autowired
    DecisionGenerator decisionGenerator;

    @Autowired
    TestimonyResolver testimonyResolver;

    @Autowired
    ParticipantContributionResolver participantContributionResolver;

    @JobWorker(type = "initialization")
    public Map<String, Object> handleInitializationJob(final ActivatedJob job, @Variable String personName) {
        long time = System.currentTimeMillis();

        //change the name to the ID form
        String[] splitName = Normalizer.normalize(personName, Normalizer.Form.NFKD).
                replaceAll("[^\\p{ASCII}]", "").toLowerCase().split(" ");
        List<String> listName = Arrays.asList(splitName);
        Collections.reverse(listName);
        String[] reversedSplitName = listName.toArray(splitName);

        //append with timestamp
        String decisionId = String.join("_", reversedSplitName)
                + "_" + time;

        log.info("Initialized tax administration process with id:" + decisionId);

        return Collections.singletonMap("decisionId", decisionId);
    }


    @JobWorker(type = "generatingDecision")
    public Map<String, Object> handleGeneratingDecisionJob(final ActivatedJob job, @VariablesAsType TaxManagementVariables variables) {
        String fileName = "results/TMD_" + variables.getDecisionId() + ".pdf";

        //generate pdf file
        decisionGenerator.generate(fileName, variables);
        log.info("Generated official decision, saved with filename: " + fileName);

        return Collections.singletonMap("decisionFileName", fileName);
    }

    @JobWorker(type = "resolveTestimonies")
    public Map<String, Object> resolveTestimoniesJob(final ActivatedJob job, @Variable String[] testimonyList) {
        log.info("Resolved tax management process testimonies");
        return Collections.singletonMap("testimonies", testimonyResolver.resolveTestimony(testimonyList));
    }

    @JobWorker(type = "resolveParticipantContributions")
    public Map<String, Object> resolveParticipantContributionsJob(
            final ActivatedJob job,
            @Variable String[] participantContributionList,
            @Variable String[] participantNameList) {
        log.info("Resolved tax management process participant contributions");
        return Collections.singletonMap("participantContributions",
                participantContributionResolver.resolveParticipantContribution(participantContributionList, participantNameList));
    }
}
