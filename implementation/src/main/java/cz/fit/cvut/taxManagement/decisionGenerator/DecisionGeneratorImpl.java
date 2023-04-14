package cz.fit.cvut.taxManagement.decisionGenerator;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import cz.fit.cvut.taxManagement.decisionGenerator.decisionOutput.DecisionOutput;
import cz.fit.cvut.taxManagement.process.TaxManagementVariables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class DecisionGeneratorImpl implements DecisionGenerator {

    @Autowired
    private DecisionOutput output;

    private Document document = new Document(PageSize.A4);

    private final Font fontT1 = FontFactory.getFont(FontFactory.TIMES_ROMAN);
    private final Font fontT2 = FontFactory.getFont(FontFactory.TIMES_ROMAN);
    private final Font fontH1 = FontFactory.getFont(FontFactory.TIMES_ROMAN);
    private final Font fontH2 = FontFactory.getFont(FontFactory.TIMES_ROMAN);
    private final Font fontText = FontFactory.getFont(FontFactory.TIMES_ROMAN);
    private final Font fontTextBold = FontFactory.getFont(FontFactory.TIMES_ROMAN);

    public DecisionGeneratorImpl() {
        loadStyles();
    }

    @Override
    public void generate(String filename, TaxManagementVariables variables) {
        try {
            OutputStream pdfOutputFile = output.getOutputStream(filename);
            final PdfWriter pdfWriter = PdfWriter.getInstance(document, pdfOutputFile);

            document.open();

            addHeading(document);
            addTaxSubjectProperties(document, variables);
            document.add(Chunk.NEWLINE);
            document.add(new LineSeparator());
            document.add(Chunk.NEWLINE);
            addOfficialDecisionProperties(document, variables);
            document.newPage();
            addAttachments(document, variables);

            document.close();

            pdfWriter.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void loadStyles() {
        fontT1.setSize(20);
        fontT1.setStyle(Font.BOLD);
        fontT2.setSize(18);
        fontH1.setSize(14);
        fontH1.setStyle(Font.BOLD);
        fontH2.setSize(12);
        fontH2.setStyle(Font.BOLD);
        fontText.setSize(11);
        fontTextBold.setSize(11);
        fontTextBold.setStyle(Font.BOLD);
    }

    private void addHeading(Document document) {
        addParagraph(document, "Official decision", fontT1, Element.ALIGN_CENTER);
        addParagraph(document, "Tax administration process", fontT2, Element.ALIGN_CENTER);
        addParagraph(document, "Czech Republic", fontText, Element.ALIGN_RIGHT);

        try {
            Image coatOfArm = Image.getInstance("src/main/resources/images/czech_coat_of_arms.jpg");
            coatOfArm.setAbsolutePosition(499, 745);
            coatOfArm.scalePercent(50);
            document.add(coatOfArm);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void addTaxSubjectProperties(Document document, TaxManagementVariables variables) {
        addParagraph(document, "Tax Subject information:", fontH2);
        addParagraph(document, "Name: " + variables.getPersonName(), fontText);
        addParagraph(document, "Address: " + variables.getPersonAddress(), fontText);
        addParagraph(document, "E-mail: " + variables.getPersonEmail(), fontText);
        addParagraph(document, "Phone: " + variables.getPersonPhone(), fontText);
    }

    private void addOfficialDecisionProperties(Document document, TaxManagementVariables variables) {
        Paragraph par = new Paragraph();
        par.add(new Chunk("Based on Act No. 280/2009 Coll., Tax Administration, as currently applicable, the Tax Administrator imposes an obligation on Tax Subject to pay: ", fontText));
        par.add(new Chunk("CZK " + variables.getTaxAmount(), fontTextBold));
        document.add(par);

        document.add(Chunk.NEWLINE);

        addParagraph(document, "Reasoning:", fontH2);
        addParagraph(document, variables.getTaxJustification(), fontText);

        document.add(Chunk.NEWLINE);

        addParagraph(document, "Notice:", fontH2);
        addParagraph(document, "The decision is valid from the day of its adoption. This decision may be appealed within 30 days of its adoption by lodging a separate appeal with the Tax Administration Institution. An appeal can challenge the sentence part of the decision, an individual sentence or its subsidiary provisions. An appeal against only the justification of the decision is inadmissible. ", fontText);

        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        addParagraph(document, "In Prague " + getDate() + ".", fontText);
    }

    private void addAttachments(Document document, TaxManagementVariables variables) {
        addParagraph(document, "Attachments", fontH1);
        addParagraph(document, "The following sections covers the attachments of the Official decision based on which the tax was determined and the decision was created.", fontText);
        document.add(Chunk.NEWLINE);

        addParagraph(document, "Submitted additional information", fontH2);
        addParagraph(document, variables.getAdditionalInformation(), fontText);
        document.add(Chunk.NEWLINE);

        addParagraph(document, "Presented facts", fontH2);
        addParagraph(document, variables.getPresentedFacts(), fontText);
        document.add(Chunk.NEWLINE);

        addTestimonies(document, variables);

        addExpertReport(document, variables);

        addSpecialRecords(document, variables);

        addParticipantContributions(document, variables);

        addTaxNegotiation(document, variables);
    }

    private void addTestimonies(Document document, TaxManagementVariables variables) {
        if (variables.getTestimonies().equals("")) {
            return;
        }

        addParagraph(document, "Testimonies", fontH2);
        addParagraph(document, variables.getTestimonies(), fontText);
        document.add(Chunk.NEWLINE);
    }

    private void addExpertReport(Document document, TaxManagementVariables variables) {
        if (variables.getExpertReport().equals("")) {
            return;
        }

        addParagraph(document, "Expert report", fontH2);
        addParagraph(document, variables.getExpertReport(), fontText);
        document.add(Chunk.NEWLINE);
    }

    private void addSpecialRecords(Document document, TaxManagementVariables variables) {
        if (variables.getSpecialRecords().equals("")) {
            return;
        }

        addParagraph(document, "Special records", fontH2);
        addParagraph(document, variables.getSpecialRecords(), fontText);
        document.add(Chunk.NEWLINE);
    }

    private void addParticipantContributions(Document document, TaxManagementVariables variables) {
        if (variables.getParticipantContributions().equals("")) {
            return;
        }

        addParagraph(document, "Participant contributions", fontH2);
        addParagraph(document, variables.getParticipantContributions(), fontText);
        document.add(Chunk.NEWLINE);
    }

    private void addTaxNegotiation(Document document, TaxManagementVariables variables) {
        if (variables.getNegotiatedTaxJustification().equals("")) {
            return;
        }

        addParagraph(document, "Tax negotiation", fontH2);
        addParagraph(document, "During the tax negotiation the final amount of: CZK " + variables.getNegotiatedTaxValue() + " was determined.", fontText);
        document.add(Chunk.NEWLINE);

        addParagraph(document, "Negotiated tax justification", fontTextBold);
        addParagraph(document, variables.getNegotiatedTaxJustification(), fontText);
        document.add(Chunk.NEWLINE);
    }

    private void addParagraph(Document document, String text, Font font) {
        Paragraph par = new Paragraph(text, font);
        document.add(par);
    }

    private void addParagraph(Document document, String text, Font font, int alignment) {
        Paragraph par = new Paragraph(text, font);
        par.setAlignment(alignment);
        document.add(par);
    }

    private String getDate() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd. MM. yyyy");
        return dtf.format(now);
    }
}
