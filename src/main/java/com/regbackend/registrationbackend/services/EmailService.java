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

            // ✅ Plain text (fallback)
            String plainText = String.format(
                    "Hi %s,\n\nYour registration has been approved!\n\nCNIC: %s\nEvent Type: %s\n\nRegards,\nGDG Kolachi Team",
                    fullName, cnic, eventType
            );

            // ✅ Simple HTML email
            String htmlContent = String.format(
                    """
                    <html>
                      <body style="font-family: 'Segoe UI', Arial, sans-serif; background-color: #f9f9f9; padding: 20px;">
                        <div style="max-width: 600px; margin: auto; background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 6px rgba(0,0,0,0.1);">
                          <h2 style="color: #2563eb;">🎉 Congratulations, %s!</h2>
                          <p>Your registration has been <strong style="color: green;">approved</strong>.</p>
                          <table style="width: 100%%; margin-top: 15px; border-collapse: collapse;">
                            <tr>
                              <td style="padding: 8px; border-bottom: 1px solid #eee;"><strong>CNIC:</strong></td>
                              <td style="padding: 8px; border-bottom: 1px solid #eee;">%s</td>
                            </tr>
                            <tr>
                              <td style="padding: 8px; border-bottom: 1px solid #eee;"><strong>Event Type:</strong></td>
                              <td style="padding: 8px; border-bottom: 1px solid #eee;">%s</td>
                            </tr>
                          </table>
                          <p style="margin-top: 20px;">We're excited to see you at the event!</p>
                          <p style="color: #555;">Best Regards,<br><strong>GDG Kolachi Team</strong></p>
                        </div>
                      </body>
                    </html>
                    """,
                    fullName, cnic, eventType
            );

            email.setPlain(plainText);
            email.setHtml(htmlContent);

            mailerSend.emails().send(email);
            System.out.println("✅ Test email sent successfully to: " + toEmail);
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }
}
