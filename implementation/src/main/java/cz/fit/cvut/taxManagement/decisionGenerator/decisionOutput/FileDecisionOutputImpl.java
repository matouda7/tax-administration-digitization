package cz.fit.cvut.taxManagement.decisionGenerator.decisionOutput;

import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

@Component
public class FileDecisionOutputImpl implements DecisionOutput {

    @Override
    public OutputStream getOutputStream(String filename) throws FileNotFoundException {
        return new FileOutputStream(filename);
    }
}
