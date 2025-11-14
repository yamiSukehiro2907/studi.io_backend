package io.studi.backend.services.others;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${mail.from.email}")
    private String fromEmail;

    @Value("${mail.from.name}")
    private String fromName;

    public boolean sendOtpMailVerification(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String title = "Welcome to Studi.io!";

            helper.setFrom(String.format("%s <%s>", fromName, fromEmail));
            helper.setTo(toEmail);
            helper.setSubject("Verify Your Studi.io Account");
            helper.setText(createEmailTemplate(title, buildOtpMailVerificationBody(otp)), true);

            mailSender.send(message);
            log.info("OTP email sent successfully to {}", toEmail);
            return true;

        } catch (MessagingException ex) {
            log.error("Failed to send OTP for email verification to {}: {}", toEmail, ex.getMessage());
            return false;
        }
    }

    private String createEmailTemplate(String title, String body) {
        return "<div style=\"font-family: 'Inter', Arial, sans-serif; max-width: 600px; "
                + "margin: auto; background-color: #f9fafb; border-radius: 12px; overflow: hidden; "
                + "border: 1px solid #e5e7eb;\">"
                + "    <div style=\"background-color: #10B981; color: white; text-align: center; padding: 20px 0;\">"
                + "      <h2 style=\"margin: 0; font-size: 24px; letter-spacing: 0.5px;\">" + title + "</h2>"
                + "    </div>"
                + "    <div style=\"padding: 24px; color: #374151; font-size: 16px; line-height: 1.6;\">"
                + body
                + "      <p style=\"font-size: 14px; color: #6b7280; margin-top: 24px;\">"
                + "        Thanks,<br><strong>The Studi.io Team</strong>"
                + "      </p>"
                + "    </div>"
                + "    <div style=\"background-color: #ecfdf5; text-align: center; padding: 12px; "
                + "color: #065f46; font-size: 13px;\">"
                + "      <p style=\"margin: 0;\">© 2025 Studi.io — Learn. Build. Grow.</p>"
                + "    </div>"
                + "  </div>";
    }

    private String buildOtpMailVerificationBody(String otp) {
        return "<p>We're thrilled to have you join Studi.io! Please verify your email by using the OTP below:</p>"
                + "<h1 style=\"color: #10B981; font-size: 36px; letter-spacing: 5px; text-align: center; margin: 24px 0;\">"
                + otp
                + "</h1>"
                + "<p>This OTP will expire in <strong>10 minutes</strong>.</p>"
                + "<p>If you didn’t sign up for Studi.io, please ignore this email.</p>";
    }
}
