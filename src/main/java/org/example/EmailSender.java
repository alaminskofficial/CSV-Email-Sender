package org.example;

import com.opencsv.CSVReader;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.FileReader;
import java.util.Properties;

public class EmailSender {

    public static void main(String[] args) {
        // CSV file path containing email IDs
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
        String fromEmail = "<<your_email_id>>";
         String password = "<<password>>";

        // Reading email IDs from the CSV
        try (CSVReader csvReader = new CSVReader(new FileReader(csvFile))) {
            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                String toEmail = nextLine[0]; // Assuming the email is in the first column

                // Send email to each address
                sendEmail(fromEmail, password, toEmail, subject, body);
                System.out.println("Email sent to: " + toEmail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendEmail(String fromEmail, String password, String toEmail, String subject, String body) {
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
            // Create a message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);

            // Send the message
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}

