package com.regbackend.registrationbackend.services;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${resend.api.key}")
    private String apiKey;

    @Value("${resend.from.email}")
    private String fromEmail;

    @Value("${resend.from.name}")
    private String fromName;

    public void sendApprovalEmail(String toEmail, String fullName, String cnic, String eventType) {
        Resend resend = new Resend(apiKey);

        // ✅ Build the HTML email message
        String htmlTemplate = String.format("""
            <html>
              <body style="font-family:Arial, sans-serif; background-color:#f4f6f8; padding:20px;">
                <div style="max-width:600px; margin:auto; background:white; padding:25px; border-radius:10px; box-shadow:0 2px 10px rgba(0,0,0,0.05);">

                  <h2 style="color:#2563eb; margin-bottom:10px;">
                    🎉 Congratulations, %s!
                  </h2>

                  <p style="font-size:15px; color:#444;">
                    Your registration has been <b style="color:green;">approved</b>.
                  </p>

                  <table style="width:100%%; margin-top:20px; border-collapse:collapse;">
                    <tr>
                      <td style="padding:8px; border-bottom:1px solid #eee;"><strong>CNIC:</strong></td>
                      <td style="padding:8px; border-bottom:1px solid #eee;">%s</td>
                    </tr>
                    <tr>
                      <td style="padding:8px; border-bottom:1px solid #eee;"><strong>Event Type:</strong></td>
                      <td style="padding:8px; border-bottom:1px solid #eee;">%s</td>
                    </tr>
                  </table>

                  <p style="margin-top:20px; color:#333;">
                    We look forward to seeing you at the event!
                    <br/><br/>
                    Best Regards,<br/>
                    <strong>GDG Kolachi Team</strong>
                  </p>
                </div>
              </body>
            </html>
        """, fullName, cnic, eventType);

        // ✅ Build email options EXACTLY like the Resend documentation
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(fromName + " <" + fromEmail + ">")
                .to(toEmail)
                .subject("✅ Your Registration is Approved!")
                .html(htmlTemplate)
                .build();

        try {
            CreateEmailResponse data = resend.emails().send(params);
            System.out.println("✅ Email sent successfully. ID = " + data.getId());
        } catch (ResendException e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }
}
