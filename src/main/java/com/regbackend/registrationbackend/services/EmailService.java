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

    // Generate QR code URL using public API
    private String generateQRCodeUrl(String publicId) {
        try {
            // URL encode the public ID
            String encodedId = java.net.URLEncoder.encode(publicId, "UTF-8");
            // Using QuickChart.io free QR code API
            return "https://quickchart.io/qr?text=" + encodedId + "&size=250";
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate QR Code URL", e);
        }
    }

    public void sendApprovalEmail(String toEmail, String fullName, String cnic, String eventType, String publicId, String workshopName) {

        Resend resend = new Resend(apiKey);

        // Generate QR code URL
        String qrCodeUrl = generateQRCodeUrl(publicId);

        // Use the URL directly in the img tag
        String qrImgTag = "<img src=\"" + qrCodeUrl + "\" width=\"220\" height=\"220\" style=\"display:block;margin:20px auto;\" />";

        // Check if event type is WORKSHOP
        boolean isWorkshop = "WORKSHOP".equalsIgnoreCase(eventType);

        // Build workshop row if applicable
        String workshopRow = "";
        if (isWorkshop && workshopName != null && !workshopName.isEmpty()) {
            workshopRow = "<tr><td><b>Workshop:</b></td><td>" + workshopName + "</td></tr>";
        }

        // Build workshop instruction if applicable
        String workshopInstruction = "";
        if (isWorkshop) {
            workshopInstruction = "<li>For the workshop please bring your laptop and internet devices or hotspot</li>";
        }

        // HTML template
        String htmlTemplate =
                "<html>" +
                        "<body style=\"font-family:Arial, sans-serif; background:#ffffff; padding:20px;\">" +
                        "  <div style=\"max-width:600px; margin:auto; background:white; padding:25px; border-radius:10px;\">" +

                        "    <div style=\"text-align:center; margin-bottom:20px;\">" +
                        "      <img src=\"https://i.postimg.cc/44LkwJF3/Group-1306.png\" alt=\"Devfest Logo\" style=\"max-width:100%; height:auto;\" />" +
                        "    </div>" +

                        "    <h2 style=\"color:#2563eb; text-align:center;\">Congratulations, " + fullName + "!</h2>" +
                        "    <p style=\"text-align:center;\">Your registration has been <b style='color:green;'>approved</b>.</p>" +

                        "    <div style=\"text-align:center; margin-top:20px;\">" +
                        "      <h3 style=\"margin-bottom:10px;\">🎟 Your Entry QR Code</h3>" +
                        qrImgTag +
                        "    </div>" +

                        "    <table style=\"width:100%; margin-top:20px;\">" +
                        "      <tr><td><b>CNIC:</b></td><td>" + cnic + "</td></tr>" +
                        "      <tr><td><b>Event Type:</b></td><td>" + eventType + "</td></tr>" +
                        workshopRow +
                        "    </table>" +

                        "    <div style=\"margin-top:20px; background:#f3f4f6; padding:15px; border-radius:8px;\">" +
                        "      <h4 style=\"margin-top:0; color:#1f2937;\">Important Instructions:</h4>" +
                        "      <ul style=\"margin-left:10px; color:#374151;\">" +
                        "        <li>Bring this ticket and CNIC for verification</li>" +
                        "        <li>Arrive sharp at 8:30 AM</li>" +
                        "        <li>Entry won't be allowed after 10:00 AM</li>" +
                        workshopInstruction +
                        "      </ul>" +
                        "    </div>" +

                        "    <p style=\"margin-top:20px;\">We look forward to seeing you!<br/><br/>" +
                        "    Best Regards,<br/><b>GDG Kolachi Team</b></p>" +

                        "    <div style=\"text-align:center; margin-top:30px; border-top:1px solid #e5e7eb; padding-top:20px;\">" +
                        "      <img src=\"https://i.postimg.cc/FKnmz3kx/GDG-LOGO.png\" alt=\"GDG Logo\" style=\"max-width:150px; height:auto;\" />" +
                        "    </div>" +

                        "  </div>" +
                        "</body>" +
                        "</html>";

        // Send email without attachment
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