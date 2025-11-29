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
        String qrImgTag = "<img src=\"" + qrCodeUrl + "\" width=\"160\" height=\"160\" style=\"display:block;margin:20px auto;border:1px solid #000000;padding:10px;background:white;\" />";

        // Check if event type is WORKSHOP
        boolean isWorkshop = "WORKSHOP".equalsIgnoreCase(eventType);

        // Build workshop row if applicable
        String workshopRow = "";
        if (isWorkshop && workshopName != null && !workshopName.isEmpty()) {
            workshopRow = "<tr><td><b>Workshop:</b></td><td><b>" + workshopName + "</b></td></tr>";
        }

        // Build workshop instruction if applicable
        String workshopInstruction = "";
        if (isWorkshop) {
            workshopInstruction = "<li><b>For the workshop please bring your laptop and internet devices or hotspot</b></li>";
        }

        // HTML template
        String htmlTemplate =
                "<html>" +
                        "<body style=\"font-family:'Google Sans', Arial, sans-serif; background:#f8f9fa; font-weight:bold;\">" +
                        "  <div style=\"max-width:600px; margin:auto; background:white; padding:20px; border-radius:10px; background-image:url('https://i.postimg.cc/dtgZsGQk/Grid.jpg'); background-size:cover; background-position:center; background-repeat:no-repeat;\">" +

                        "    <div style=\"text-align:center; margin-bottom:20px;\">" +
                        "      <img src=\"https://i.postimg.cc/44LkwJF3/Group-1306.png\" alt=\"Devfest Logo\" style=\"width:150px;\" />" +
                        "    </div>" +

                        "    <h2 style=\"color:#4285f4; text-align:center; font-weight:bold;\"><b>Congratulations, " + fullName + "!</b></h2>" +
                        "    <p style=\"text-align:center; color:#4285f4; font-weight:bold;\"><b>Your registration has been <span style='color:#0f9d58;'>approved</span>.</b></p>" +

                        "    <div style=\"text-align:center; margin-top:20px;\">" +
                        "      <h3 style=\"margin-bottom:10px; color:#4285f4; font-weight:bold;\"><b>🎟 Your Entry QR Code</b></h3>" +
                        qrImgTag +
                        "    </div>" +

                        "    <div style=\"background:white; border:2px solid rgba(0,0,0,0.2); border-radius:10px; padding:15px; margin:20px auto; width:250px;\">" +
                        "      <table style=\"width:250px; color:#4285f4; font-weight:bold;\">" +
                        "        <tr><td><b>CNIC:</b></td><td><b>" + cnic + "</b></td></tr>" +
                        "        <tr><td><b>Event Type:</b></td><td><b>" + eventType + "</b></td></tr>" +
                        workshopRow +
                        "      </table>" +
                        "    </div>" +

                        "    <div style=\"background:white; border:2px solid rgba(0,0,0,0.2); border-radius:10px; padding:15px; margin:20px auto; width:250px;\">" +
                        "      <h4 style=\"margin-top:0; color:#ea4335; font-weight:bold;\"><b>Important Instructions:</b></h4>" +
                        "      <ul style=\"color:#4285f4; font-weight:bold;\">" +
                        "        <li><b>Bring this ticket and CNIC for verification</b></li>" +
                        "        <li><b>Arrive sharp at 8:30 AM</b></li>" +
                        "        <li><b>Entry won't be allowed after 11:00 AM</b></li>" +
                        workshopInstruction +
                        "      </ul>" +
                        "    </div>" +

                        "    <p style=\"margin-top:20px; color:#4285f4; font-weight:bold;\"><b>We look forward to seeing you!</b><br/><br/>" +
                        "    <b>For more details, visit: <a href=\"http://devfest.gdgkolachi.com\" style=\"color:#0f9d58; text-decoration:none;\">devfest.gdgkolachi.com</a></b></p>" +

                        "    <div style=\"text-align:center; margin-top:10px;\">" +
                        "      <img src=\"https://i.postimg.cc/FKnmz3kx/GDG-LOGO.png\" alt=\"GDG Logo\" style=\"max-width:150px; height:auto;\" />" +
                        "    </div>" +

                        "  </div>" +
                        "</body>" +
                        "</html>";

        // Send email without attachment
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(fromName + " <" + fromEmail + ">")
                .to(toEmail)
                .subject("Devfest calling. You're shortlisted.")
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