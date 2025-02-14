package org.example;


import com.opencsv.CSVReader;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;
import java.util.List;
import java.util.ArrayList;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;

public class EmailSenderWithAttach {

    public static void main(String[] args) {
        // CSV file path containing email IDs
        String attachmentPath = "C:\\Users\\INDIA\\My Projects\\Backend\\email-sender-standalone\\AlaminResume-SDE.pdf";  // Path to the file you want to attach

        String csvFile = "C:\\Users\\INDIA\\My Projects\\Backend\\email-sender-standalone\\EmailList_Sheet1.csv";
        String subject = "Job Application";
        String body = "Hi, \n" +
                "\n" +
                "I am writing to express my interest in the Software Developer position at your company. With a Bachelor's degree and around 3.5 years of experience in Java development, I believe I would be an excellent fit for the role.\n" +
                "\n" +
                "In my current company , I am responsible for developing and maintaining several Java applications. I am proficient in using various frameworks such as Spring ,Spring boot and Hibernate and have experience with database management using MySQL and Postgresql. Additionally, I have experience in agile development methodologies and am familiar with version control tools such as Git.\n" +
                "\n" +
                "I am excited about the opportunity to join your company and contribute my skills to the development of innovative and cutting-edge software. My experience and skills make me confident that I can make a significant contribution to your team.\n" +
                "\n" +
                "Thank you for considering my application. I have attached my resume for your review. Please let me know if you require any further information or have any questions. I look forward to hearing from you. \n" +
                "\n" +
                "\n" +
                "Thanks & Regards\n" +
                "Sk Alamin\n" +
                "6294090242";

        // Sender email details
        String fromEmail = "alamin695sk@gmail.com";
        String password = "yfzr ahpd owyd ursh";

        // Reading email IDs from the CSV
        try (CSVReader csvReader = new CSVReader(new FileReader(csvFile))) {
            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                String toEmail = nextLine[0]; // Assuming the email is in the first column

                // Send email to each address
                sendEmail(fromEmail, password, toEmail, subject, body, attachmentPath);
                System.out.println("Email sent to: " + toEmail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendEmail(String fromEmail, String password, String toEmail, String subject, String body, String attachmentPath) {
        // Set email server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        // Create a session with authentication
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            // Create a new MimeMessage
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);

            // Create a multipart message for body + attachment
            Multipart multipart = new MimeMultipart();

            // Part 1: Set the body of the email
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(body);
            multipart.addBodyPart(textPart);

            // Part 2: Set the attachment
            if (attachmentPath != null && !attachmentPath.isEmpty()) {
                MimeBodyPart attachmentPart = new MimeBodyPart();
                FileDataSource source = new FileDataSource(attachmentPath);
                attachmentPart.setDataHandler(new DataHandler(source));
                attachmentPart.setFileName(new File(attachmentPath).getName());
                multipart.addBodyPart(attachmentPart);
            }

            // Set the complete message
            message.setContent(multipart);

            // Send the message
            Transport.send(message);
            System.out.println("Email sent successfully with attachment to: " + toEmail);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}

