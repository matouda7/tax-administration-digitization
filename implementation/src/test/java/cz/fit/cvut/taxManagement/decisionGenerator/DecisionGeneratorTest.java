package cz.fit.cvut.taxManagement.decisionGenerator;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import cz.fit.cvut.taxManagement.decisionGenerator.decisionOutput.FileDecisionOutputImpl;
import cz.fit.cvut.taxManagement.process.TaxManagementVariables;
import io.camunda.zeebe.spring.test.ZeebeSpringTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.*;

@SpringBootTest
@ZeebeSpringTest
public class DecisionGeneratorTest {

    @MockBean
    FileDecisionOutputImpl fileDecisionOutput;

    @Mock
    Document document;

    @InjectMocks
    DecisionGeneratorImpl decisionGenerator;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("Test decision creation without additional information")
    public void testDecisionGeneratingWithoutAdditionalInformation() throws IOException {
        //Prepare values and mocks
        FileOutputStream fileOutputStream = Mockito.mock(FileOutputStream.class);

        Mockito.when(fileDecisionOutput.getOutputStream(anyString())).thenReturn(fileOutputStream);

        TaxManagementVariables variables = initVariables();

        //Run generating
        decisionGenerator.generate("filename.pdf", variables);

        //Verify results with correct number of added Paragraphs
        Mockito.verify(fileDecisionOutput).getOutputStream("filename.pdf");
        Mockito.verify(document).open();
        Mockito.verify(document).close();
        Mockito.verify(document, Mockito.times(20)).add(any(Paragraph.class));
        Mockito.verify(document).add(any(Image.class));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Official decision]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Name: Daniel Matousek]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Address: Address of Daniel]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[E-mail: email@someemail.com]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Phone: +420123456789]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals(
                "[Based on Act No. 280/2009 Coll., Tax Administration, as currently applicable, the Tax Administrator imposes an obligation on Tax Subject to pay: , CZK 1234]"
        )));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some tax justification]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some additional information]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some presented facts]")));
    }

    @Test
    @DisplayName("Test decision creation with testimonies")
    public void testDecisionGeneratingWithTestimonies() throws IOException {
        //Prepare values and mocks
        FileOutputStream fileOutputStream = Mockito.mock(FileOutputStream.class);

        Mockito.when(fileDecisionOutput.getOutputStream(anyString())).thenReturn(fileOutputStream);

        TaxManagementVariables variables = initVariables();
        variables.setTestimonies("Some testimonies");

        //Run generating
        decisionGenerator.generate("filename.pdf", variables);

        //Verify results with correct number of added Paragraphs
        Mockito.verify(fileDecisionOutput).getOutputStream("filename.pdf");
        Mockito.verify(document).open();
        Mockito.verify(document).close();
        Mockito.verify(document, Mockito.times(22)).add(any(Paragraph.class));
        Mockito.verify(document).add(any(Image.class));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Official decision]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Name: Daniel Matousek]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Address: Address of Daniel]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[E-mail: email@someemail.com]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Phone: +420123456789]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals(
                "[Based on Act No. 280/2009 Coll., Tax Administration, as currently applicable, the Tax Administrator imposes an obligation on Tax Subject to pay: , CZK 1234]"
        )));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some tax justification]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some additional information]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some presented facts]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some testimonies]")));
    }

    @Test
    @DisplayName("Test decision creation with expert report")
    public void testDecisionGeneratingWithExpertReport() throws IOException {
        //Prepare values and mocks
        FileOutputStream fileOutputStream = Mockito.mock(FileOutputStream.class);

        Mockito.when(fileDecisionOutput.getOutputStream(anyString())).thenReturn(fileOutputStream);

        TaxManagementVariables variables = initVariables();
        variables.setExpertReport("Some expert report");

        //Run generating
        decisionGenerator.generate("filename.pdf", variables);

        //Verify results with correct number of added Paragraphs
        Mockito.verify(fileDecisionOutput).getOutputStream("filename.pdf");
        Mockito.verify(document).open();
        Mockito.verify(document).close();
        Mockito.verify(document, Mockito.times(22)).add(any(Paragraph.class));
        Mockito.verify(document).add(any(Image.class));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Official decision]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Name: Daniel Matousek]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Address: Address of Daniel]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[E-mail: email@someemail.com]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Phone: +420123456789]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals(
                "[Based on Act No. 280/2009 Coll., Tax Administration, as currently applicable, the Tax Administrator imposes an obligation on Tax Subject to pay: , CZK 1234]"
        )));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some tax justification]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some additional information]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some presented facts]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some expert report]")));
    }

    @Test
    @DisplayName("Test decision creation with special records")
    public void testDecisionGeneratingWithRecords() throws IOException {
        //Prepare values and mocks
        FileOutputStream fileOutputStream = Mockito.mock(FileOutputStream.class);

        Mockito.when(fileDecisionOutput.getOutputStream(anyString())).thenReturn(fileOutputStream);

        TaxManagementVariables variables = initVariables();
        variables.setSpecialRecords("Some special records");

        //Run generating
        decisionGenerator.generate("filename.pdf", variables);

        //Verify results with correct number of added Paragraphs
        Mockito.verify(fileDecisionOutput).getOutputStream("filename.pdf");
        Mockito.verify(document).open();
        Mockito.verify(document).close();
        Mockito.verify(document, Mockito.times(22)).add(any(Paragraph.class));
        Mockito.verify(document).add(any(Image.class));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Official decision]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Name: Daniel Matousek]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Address: Address of Daniel]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[E-mail: email@someemail.com]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Phone: +420123456789]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals(
                "[Based on Act No. 280/2009 Coll., Tax Administration, as currently applicable, the Tax Administrator imposes an obligation on Tax Subject to pay: , CZK 1234]"
        )));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some tax justification]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some additional information]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some presented facts]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some special records]")));
    }

    @Test
    @DisplayName("Test decision creation with participant contributions")
    public void testDecisionGeneratingWithParticipantContributions() throws IOException {
        //Prepare values and mocks
        FileOutputStream fileOutputStream = Mockito.mock(FileOutputStream.class);

        Mockito.when(fileDecisionOutput.getOutputStream(anyString())).thenReturn(fileOutputStream);

        TaxManagementVariables variables = initVariables();
        variables.setParticipantContributions("Some participant contributions");

        //Run generating
        decisionGenerator.generate("filename.pdf", variables);

        //Verify results with correct number of added Paragraphs
        Mockito.verify(fileDecisionOutput).getOutputStream("filename.pdf");
        Mockito.verify(document).open();
        Mockito.verify(document).close();
        Mockito.verify(document, Mockito.times(22)).add(any(Paragraph.class));
        Mockito.verify(document).add(any(Image.class));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Official decision]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Name: Daniel Matousek]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Address: Address of Daniel]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[E-mail: email@someemail.com]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Phone: +420123456789]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals(
                "[Based on Act No. 280/2009 Coll., Tax Administration, as currently applicable, the Tax Administrator imposes an obligation on Tax Subject to pay: , CZK 1234]"
        )));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some tax justification]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some additional information]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some presented facts]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some participant contributions]")));
    }

    @Test
    @DisplayName("Test decision creation with tax negotiation")
    public void testDecisionGeneratingWithTaxNegotiation() throws IOException {
        //Prepare values and mocks
        FileOutputStream fileOutputStream = Mockito.mock(FileOutputStream.class);

        Mockito.when(fileDecisionOutput.getOutputStream(anyString())).thenReturn(fileOutputStream);

        TaxManagementVariables variables = initVariables();
        variables.setNegotiatedTaxValue("4321");
        variables.setNegotiatedTaxJustification("Some tax negotiation justification");

        //Run generating
        decisionGenerator.generate("filename.pdf", variables);

        //Verify results with correct number of added Paragraphs
        Mockito.verify(fileDecisionOutput).getOutputStream("filename.pdf");
        Mockito.verify(document).open();
        Mockito.verify(document).close();
        Mockito.verify(document, Mockito.times(24)).add(any(Paragraph.class));
        Mockito.verify(document).add(any(Image.class));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Official decision]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Name: Daniel Matousek]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Address: Address of Daniel]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[E-mail: email@someemail.com]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Phone: +420123456789]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals(
                "[Based on Act No. 280/2009 Coll., Tax Administration, as currently applicable, the Tax Administrator imposes an obligation on Tax Subject to pay: , CZK 1234]"
        )));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some tax justification]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some additional information]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some presented facts]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals(
                "[During the tax negotiation the final amount of: CZK 4321 was determined.]"
        )));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some tax negotiation justification]")));

    }

    @Test
    @DisplayName("Test decision creation with all additional information")
    public void testDecisionGeneratingWithAdditionalInformation() throws IOException {
        //Prepare values and mocks
        FileOutputStream fileOutputStream = Mockito.mock(FileOutputStream.class);

        Mockito.when(fileDecisionOutput.getOutputStream(anyString())).thenReturn(fileOutputStream);

        TaxManagementVariables variables = initVariables();
        variables.setTestimonies("Some testimonies");
        variables.setExpertReport("Some expert report");
        variables.setSpecialRecords("Some special records");
        variables.setParticipantContributions("Some participant contributions");
        variables.setNegotiatedTaxValue("4321");
        variables.setNegotiatedTaxJustification("Some tax negotiation justification");

        //Run generating
        decisionGenerator.generate("filename.pdf", variables);

        //Verify results with correct number of added Paragraphs
        Mockito.verify(fileDecisionOutput).getOutputStream("filename.pdf");
        Mockito.verify(document).open();
        Mockito.verify(document).close();
        Mockito.verify(document, Mockito.times(32)).add(any(Paragraph.class));
        Mockito.verify(document).add(any(Image.class));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Official decision]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Name: Daniel Matousek]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Address: Address of Daniel]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[E-mail: email@someemail.com]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Phone: +420123456789]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals(
                "[Based on Act No. 280/2009 Coll., Tax Administration, as currently applicable, the Tax Administrator imposes an obligation on Tax Subject to pay: , CZK 1234]"
        )));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some tax justification]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some additional information]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some presented facts]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some testimonies]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some expert report]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some special records]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some participant contributions]")));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals(
                "[During the tax negotiation the final amount of: CZK 4321 was determined.]"
        )));
        Mockito.verify(document).add(argThat((Element element)-> element.toString().equals("[Some tax negotiation justification]")));
    }

    private TaxManagementVariables initVariables(){
        TaxManagementVariables variables = new TaxManagementVariables();
        variables.setPersonName("Daniel Matousek");
        variables.setPersonAddress("Address of Daniel");
        variables.setPersonPhone("+420123456789");
        variables.setPersonEmail("email@someemail.com");
        variables.setDecisionId("decisionId");
        variables.setAdditionalInformation("Some additional information");
        variables.setPresentedFacts("Some presented facts");
        variables.setTestimonies("");
        variables.setExpertReport("");
        variables.setSpecialRecords("");
        variables.setParticipantContributions("");
        variables.setNegotiatedTaxValue("");
        variables.setNegotiatedTaxJustification("");
        variables.setTaxAmount("1234");
        variables.setTaxJustification("Some tax justification");
        return variables;
    }
}
