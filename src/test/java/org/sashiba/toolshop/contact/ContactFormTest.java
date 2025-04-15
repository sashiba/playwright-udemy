package org.sashiba.toolshop.contact;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.sashiba.fixtures.PlaywrightTestCase;
import org.sashiba.toolshop.contact.pageobjects.ContactForm;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@DisplayName("Contact form")
@Feature("Contacts")
public class ContactFormTest extends PlaywrightTestCase {

    ContactForm contactForm;

    @DisplayName("Wen submitting a request")
    @BeforeEach
    void openContactPage() {
        contactForm = new ContactForm(page);

        page.navigate("https://practicesoftwaretesting.com/contact");
    }

    @DisplayName("Customers can use the contact form to contact us")
    @Story("Contact form")
    @Test
    void completeForm() throws URISyntaxException {
        Path fileToUpload = Paths.get(ClassLoader.getSystemResource("data/sample-data.txt").toURI());

        contactForm.setFirstName("Sarah-Jane");
        contactForm.setLastName("Smith");
        contactForm.setEmail("sarah@exmaple.com");
        contactForm.setMessage("HEllo, world! 50 characters, 50 characters ,50 characters ,50 characters, 50 characters" +
                "50 characters , 50 characters, 50 characters");
        contactForm.selectSubject("Return");
        contactForm.setAttachement(fileToUpload);

        contactForm.submitForm();

        org.assertj.core.api.Assertions.assertThat(contactForm.getAlertMessage())
                .contains("Thanks for your message! We will contact you shortly.");
    }

    @DisplayName("Mandatory fields")
    @Story("Contact form")
    @ParameterizedTest
    @ValueSource(strings = {"First name", "Last name", "Email", "Message"})
    void mandatoryFields(String fieldName) {
        contactForm.setFirstName("Sarah-Jane");
        contactForm.setLastName("Smith");
        contactForm.setEmail("sarah@exmaple.com");
        contactForm.setMessage("HEllo, world! 50 characters, 50 characters ,50 characters ,50 characters, 50 characters" +
                "50 characters , 50 characters, 50 characters");
        contactForm.selectSubject("Return");

        //clear one field
        contactForm.clearField(fieldName);
        //check message
        contactForm.submitForm();
        Locator errorMessage = page.getByRole(AriaRole.ALERT).getByText(fieldName + " is required");

        assertThat(errorMessage).isVisible();
    }

    @DisplayName("The message must be at least 50 characters long")
    @Story("Contact form")
    @Test
    void messageTooShort() {
        contactForm.setFirstName("Sarah-Jane");
        contactForm.setLastName("Smith");
        contactForm.setEmail("sarah@exmaple.com");
        contactForm.setMessage("HEllo, world!");
        contactForm.selectSubject("Return");
        contactForm.submitForm();

        assertThat(page.getByRole(AriaRole.ALERT)).hasText("Message must be minimal 50 characters");
    }

    @DisplayName("The email address must be correctly formatted")
    @ParameterizedTest(name = "'{arguments}' should be rejected")
    @ValueSource(strings = {"not-an-email", "not-an.email.com", "notanemail"})
    void invalidEmailField(String invalidEmail) {
        contactForm.setFirstName("Sarah-Jane");
        contactForm.setLastName("Smith");
        contactForm.setEmail(invalidEmail);
        contactForm.setMessage("A very long message to the warranty service about a warranty on a product!");
        contactForm.selectSubject("Warranty");

        contactForm.submitForm();

        assertThat(page.getByRole(AriaRole.ALERT)).hasText("Email format is invalid");
    }

    @DisplayName("Text fields")
    @Test
    void textFieldValues() {
        var messageField = page.getByLabel("Message");

        messageField.fill("This is my message");

        assertThat(messageField).hasValue("This is my message");
    }

    @DisplayName("Dropdown lists")
    @Test
    void dropdownFieldValues() {
        var subjectField = page.getByLabel("Subject");

        subjectField.selectOption("Warranty");

        assertThat(subjectField).hasValue("warranty");
    }

    @DisplayName("File uploads")
    @Test
    void fileUploads() throws URISyntaxException {
        var attachmentField = page.getByLabel("Attachment");

        Path attachment = Paths.get(ClassLoader.getSystemResource("data/sample-data.txt").toURI());

        page.setInputFiles("#attachment", attachment);

        String uploadedFile = attachmentField.inputValue();

        org.assertj.core.api.Assertions.assertThat(uploadedFile).endsWith("sample-data.txt");
    }


    @DisplayName("By CSS class")
    @Test
    void locateTheSendButtonByCssClass() {
        page.locator("#first_name").fill("Sarah-Jane");
        page.locator(".btnSubmit").click();
        List<String> alertMessages = page.locator(".alert").allTextContents();
        Assertions.assertTrue(!alertMessages.isEmpty());

    }

    @DisplayName("By attribute")
    @Test
    void locateTheSendButtonByAttribute() {
        page.locator("input[placeholder='Your last name *']").fill("Smith");
        assertThat(page.locator("#last_name")).hasValue("Smith");
    }
}

