package cz.fit.cvut.taxManagement.resolvers.participantContributionResolver;

import org.springframework.stereotype.Component;

@Component
public class ParticipantContributionResolverImpl implements ParticipantContributionResolver{

    @Override
    public String resolveParticipantContribution(String[] contributionList, String[] participantNameList) {
        StringBuilder contributions = new StringBuilder();

        if (contributionList.length != participantNameList.length) return "Invalid data";

        for (int i = 0; i < contributionList.length; i++) {
            contributions.append("Contribution ").append(i + 1)
                    .append(":\nAuthor: ").append(participantNameList[i]).append("\n")
                    .append(contributionList[i]);
            if (i!=contributionList.length-1) contributions.append("\n\n");
        }

        return contributions.toString();
    }
}
