package com.example.CarGo.Services;

import com.example.CarGo.DB.UserRepository;
import com.example.CarGo.models.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    public String registerUser(User user) {
        if (userRepository.existsByLogin(user.getLogin())) {
            return "Login already exists";
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return "Email already exists";
        }

        userRepository.save(user);
        return "User registered successfully";
    }

    public void sendPasswordResetLink(String email) throws MessagingException {

        String resetLink = "http://localhost:8080/reset-password?email=" + email;

        MimeMessage message = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom("19pawel970415@gmail.com");
        helper.setTo(email);
        helper.setSubject("Password Reset Request");

        String htmlContent = "<html><body>"
                + "<p>Click below to reset your password:</p>"
                + "<p><br></p>"
                + "<a href=\"" + resetLink + "\" style=\""
                + "background-color: #4CAF50; "
                + "color: white; "
                + "padding: 15px 32px; "
                + "text-align: center; "
                + "text-decoration: none; "
                + "display: inline-block; "
                + "font-size: 16px; "
                + "border-radius: 5px; "
                + "border: none; "
                + "cursor: pointer;\">Reset my password</a>"
                + "</body></html>";

        helper.setText(htmlContent, true);

        javaMailSender.send(message);
    }

    @Transactional
    public void updatePassword(String userEmail, String newPassword) {
        userRepository.updatePassword(userEmail, newPassword);
    }
}
