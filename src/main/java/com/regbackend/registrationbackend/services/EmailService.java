package com.regbackend.registrationbackend.services;

import com.mailersend.sdk.emails.Email;
import com.mailersend.sdk.MailerSend;
import com.mailersend.sdk.recipients.Recipient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${mailersend.api.key}")
    private String apiKey;

    @Value("${mailersend.from.email}")
    private String fromEmail;

    @Value("${mailersend.from.name}")
    private String fromName;

    public void sendApprovalEmail(String toEmail, String fullName, String cnic, String eventType) {
        try {
            MailerSend mailerSend = new MailerSend();
            mailerSend.setToken(apiKey);

            Email email = new Email();
            // setFrom expects (name, email)
            email.setFrom(fromName, fromEmail);

            // addRecipient expects (name, email)
            email.addRecipient(fullName, toEmail);
            // OR: email.addRecipient(new Recipient(fullName, toEmail));

            email.setSubject("🎉 Your Registration Has Been Approved!");

            String message = String.format(
                    "Hi %s,\n\n"
                            + "Good news! Your registration for GDG Kolachi has been approved. 🎉\n\n"
                            + "Here are your registration details:\n"
                            + "• CNIC: %s\n"
                            + "• Event Type: %s\n\n"
                            + "We’re excited to have you join us and can’t wait to see you at the event!\n\n"
                            + "Best regards,\n"
                            + "The GDG Kolachi Team"
                    , fullName, cnic, eventType
            );

            // set plain text content
            email.setPlain(message);

            mailerSend.emails().send(email);
            System.out.println("✅ Test email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }
}
