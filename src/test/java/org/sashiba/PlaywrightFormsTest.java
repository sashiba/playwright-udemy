package org.sashiba;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.SelectOption;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@UsePlaywright(HeadlessChromeOptions.class)
public class PlaywrightFormsTest {

    @DisplayName("Interacting with text fields")
    @Nested
    class WhenInteractingWithTextFields {

        @BeforeEach
        void openContactPage(Page page) {
            page.navigate("https://practicesoftwaretesting.com/contact");
        }

        @DisplayName("Complete form")
        @Test
        void completeForm(Page page) throws URISyntaxException {
            Path fileToUpload = Paths.get(ClassLoader.getSystemResource("data/sample-data.txt").toURI());
            Locator firstName = page.getByLabel("First name");
            Locator lastName = page.getByLabel("Last name");
            Locator email = page.getByLabel("Email");
            Locator message = page.getByLabel("Message *");
            Locator subject = page.getByLabel("Subject");
            Locator attachment = page.getByLabel("Attachment");

            firstName.fill("Sarah-Jane");
            lastName.fill("Smith");
            email.fill("sarah@exmaple.com");
            message.fill("HEllo, world!");
//            subject.selectOption("Return");
            subject.selectOption(new SelectOption().setLabel("Return"));

            page.setInputFiles("#attachment", fileToUpload); //upload file

            assertThat(firstName).hasValue("Sarah-Jane");
            assertThat(lastName).hasValue("Smith");
            assertThat(email).hasValue("sarah@exmaple.com");
            assertThat(message).hasValue("HEllo, world!");
            assertThat(subject).hasValue("return");

            String uploadedFile = attachment.inputValue();
            org.assertj.core.api.Assertions.assertThat(uploadedFile).endsWith("sample-data.txt");
        }

        @DisplayName("Mandatory fields")
        @ParameterizedTest
        @ValueSource(strings = {"First name", "Last name", "Email", "Message"})
        void mandatoryFields(String fieldName, Page page) {
            Locator firstName = page.getByLabel("First name");
            Locator lastName = page.getByLabel("Last name");
            Locator email = page.getByLabel("Email");
            Locator message = page.getByLabel("Message *");
            Locator subject = page.getByLabel("Subject");
            Locator buttonSend = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Send"));

            // fill all fields
            firstName.fill("Sarah-Jane");
            lastName.fill("Smith");
            email.fill("sarah@exmaple.com");
            message.fill("HEllo, world!");
            subject.selectOption("Return");
            //clear one field
            page.getByLabel(fieldName).clear();
            //check message
            buttonSend.click();
            Locator errorMessage = page.getByRole(AriaRole.ALERT).getByText(fieldName + " is required");

            assertThat(errorMessage).isVisible();
        }

        @DisplayName("Text fields")
        @Test
        void textFieldValues(Page page) {
            var messageField = page.getByLabel("Message");

            messageField.fill("This is my message");

            assertThat(messageField).hasValue("This is my message");
        }

        @DisplayName("Dropdown lists")
        @Test
        void dropdownFieldValues(Page page) {
            var subjectField = page.getByLabel("Subject");

            subjectField.selectOption("Warranty");

            assertThat(subjectField).hasValue("warranty");
        }

        @DisplayName("File uploads")
        @Test
        void fileUploads(Page page) throws URISyntaxException {
            var attachmentField = page.getByLabel("Attachment");

            Path attachment = Paths.get(ClassLoader.getSystemResource("data/sample-data.txt").toURI());

            page.setInputFiles("#attachment", attachment);

            String uploadedFile = attachmentField.inputValue();

            org.assertj.core.api.Assertions.assertThat(uploadedFile).endsWith("sample-data.txt");
        }


        @DisplayName("By CSS class")
        @Test
        void locateTheSendButtonByCssClass(Page page) {
            page.locator("#first_name").fill("Sarah-Jane");
            page.locator(".btnSubmit").click();
            List<String> alertMessages = page.locator(".alert").allTextContents();
            Assertions.assertTrue(!alertMessages.isEmpty());

        }

        @DisplayName("By attribute")
        @Test
        void locateTheSendButtonByAttribute(Page page) {
            page.locator("input[placeholder='Your last name *']").fill("Smith");
            assertThat(page.locator("#last_name")).hasValue("Smith");
        }
    }
}

