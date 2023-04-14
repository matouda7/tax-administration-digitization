package cz.fit.cvut.taxManagement.resolvers.testimonyResolver;

import org.springframework.stereotype.Component;

@Component
public class TestimonyResolverImpl implements TestimonyResolver {

    @Override
    public String resolveTestimony(String[] testimonyList) {
        StringBuilder testimonies = new StringBuilder();

        for (int i = 0; i < testimonyList.length; i++) {
            testimonies.append("Testimony ").append(i + 1).append(":\n").append(testimonyList[i]);
            if (i!=testimonyList.length-1) testimonies.append("\n\n");
        }

        return testimonies.toString();
    }
}
