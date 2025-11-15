//package com.regbackend.registrationbackend.services;
//
//import com.google.zxing.BarcodeFormat;
//import com.google.zxing.WriterException;
//import com.google.zxing.client.j2se.MatrixToImageWriter;
//import com.google.zxing.common.BitMatrix;
//import com.google.zxing.qrcode.QRCodeWriter;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.FileSystems;
//import java.nio.file.Path;
//
//@Service
//public class QRCodeStorageService {
//
//    @Value("${qr.storage.path:/var/www/qrcodes}")
//    private String storagePath;
//
//    @Value("${qr.public.url:https://yourdomain.com/qrcodes}")
//    private String publicUrl;
//
//    // Generate and save QR code to file system
//    public String generateAndSaveQRCode(String publicId) {
//        try {
//            // Create directory if it doesn't exist
//            File directory = new File(storagePath);
//            if (!directory.exists()) {
//                directory.mkdirs();
//            }
//
//            // Generate QR code
//            QRCodeWriter qrCodeWriter = new QRCodeWriter();
//            BitMatrix bitMatrix = qrCodeWriter.encode(publicId, BarcodeFormat.QR_CODE, 250, 250);
//
//            // Save to file
//            String fileName = publicId + ".png";
//            Path path = FileSystems.getDefault().getPath(storagePath, fileName);
//            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
//
//            // Return public URL
//            return publicUrl + "/" + fileName;
//
//        } catch (WriterException | IOException e) {
//            throw new RuntimeException("Failed to generate and save QR Code", e);
//        }
//    }
//}
//
//// Then in your EmailService:
///*
//@Autowired
//private QRCodeStorageService qrCodeStorageService;
//
//public void sendApprovalEmail(String toEmail, String fullName, String cnic, String eventType, String publicId) {
//    // Generate QR and get URL
//    String qrCodeUrl = qrCodeStorageService.generateAndSaveQRCode(publicId);
//
//    // Use the URL in your email HTML
//    String qrImgTag = "<img src=\"" + qrCodeUrl + "\" width=\"220\" height=\"220\" style=\"display:block;margin:20px auto;\" />";
//
//    // Rest of your email code...
//}
//*/