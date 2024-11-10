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
        helper.setFrom("cargomailboxpl@gmail.com");
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

    public void sendSubscriptionConfirmation(String email) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("cargomailboxpl@gmail.com");
        helper.setTo(email);
        helper.setSubject("Welcome to CarGo!");

        String content = "<h3><strong>Welcome to the CarGo family!</strong></h3>"
                + "<p><br></p>"
                + "<p>We’re thrilled to have you on board. As a subscriber, you’ll enjoy a range of exclusive benefits, promotions, and tips designed to make your car rental experience smoother, more enjoyable, and full of perks. Here’s a glimpse of what you can look forward to:</p>"
                + "<p><br></p>"
                + "<ul>"
                + "<li><strong>🚙 Exclusive Subscriber Discounts:</strong> Every month, we offer special discounts just for subscribers like you! Enjoy savings of up to 20% on rentals, along with seasonal promotions throughout the year.</li>"
                + "<li><strong>🚗 Early Access to New Cars and Services:</strong> Be among the first to reserve new models and premium vehicles, plus stay updated on any new services or locations as they’re added to CarGo.</li>"
                + "<li><strong>🎁 Loyalty Rewards & Upgrades:</strong> Earn points with every rental that can be redeemed for future rentals, upgrades, or special add-ons. Plus, the more you rent, the more you save with priority upgrades and loyalty benefits!</li>"
                + "<li><strong>🔧 Personalized Recommendations:</strong> Based on your rental history, we’ll send personalized recommendations for vehicles and package deals—whether it’s a family trip or a luxury ride for a business occasion.</li>"
                + "<li><strong>🛣 Priority Service & Flexible Benefits:</strong> As a subscriber, you can look forward to priority pickup/drop-off services, waived fees on additional drivers, and flexible cancellation options to make your rental experience hassle-free.</li>"
                + "<li><strong>✈️ Travel Tips, Guides, and More:</strong> Get access to guides for popular destinations, scenic road trips, and practical driving tips to help you get the most out of every journey.</li>"
                + "<li><strong>🎂 Special Occasion Surprises:</strong> On your birthday and other milestones with us, enjoy exclusive discounts, free upgrades, or other small gifts to show our appreciation.</li>"
                + "</ul>"
                + "<p><br></p>"
                + "<p>Thank you again for choosing CarGo! Look out for your first discount and insider tips in the next few days. If you have any questions or need assistance, just reply to this email or reach out to us anytime.</p>"
                + "<p><br></p>"
                + "<p>Happy driving,</p>"
                + "<p><br></p>"
                + "<p>The CarGo Team</p>";

        helper.setText(content, true);

        javaMailSender.send(message);
    }

    public void sendContactFormMessage(String mail, String subject, String content) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(mail);
        helper.setTo(mail);
        helper.setSubject(subject);
        helper.setText(content, false);
        javaMailSender.send(message);
    }
}
