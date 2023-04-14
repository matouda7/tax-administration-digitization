package cz.fit.cvut.taxManagement.decisionGenerator;

import cz.fit.cvut.taxManagement.process.TaxManagementVariables;

public interface DecisionGenerator {
    void generate(String filename, TaxManagementVariables variables);
}
