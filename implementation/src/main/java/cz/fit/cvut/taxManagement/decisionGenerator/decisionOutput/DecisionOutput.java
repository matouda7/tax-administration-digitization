package cz.fit.cvut.taxManagement.decisionGenerator.decisionOutput;

import java.io.IOException;
import java.io.OutputStream;

public interface DecisionOutput {
    OutputStream getOutputStream(String streamName) throws IOException;
}
