package AppWeb.AppWeb.controller;

import AppWeb.AppWeb.model.SubscriptionUpdate;
import AppWeb.AppWeb.model.Update;
import AppWeb.AppWeb.model.User;
import AppWeb.AppWeb.model.Subscription;
import AppWeb.AppWeb.service.SubscriptionService;
import AppWeb.AppWeb.service.UserService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {
    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        User user = userService.getCurrentUser(request);
        if (user != null) {
            List<Subscription> subscriptions = subscriptionService.findByUser(user);
            List<Update> updates = fetchUpdates(subscriptions);
            model.addAttribute("updates", updates);
            try {
                sendEmailNotification(user, updates);
            } catch (Exception e) {
                e.printStackTrace(); // Print the stack trace for debugging
            }
            return "dashboard";
        } else {
            return "redirect:/login"; // Redirect to login page if user is not authenticated
        }
    }

    // Method to fetch updates based on user subscriptions
    private List<Update> fetchUpdates(List<Subscription> subscriptions) {
        List<Update> updates = new ArrayList<>();
        for (Subscription subscription : subscriptions) {
            List<SubscriptionUpdate> subscriptionUpdates = subscription.getSubscriptionUpdates();
            for (SubscriptionUpdate subscriptionUpdate : subscriptionUpdates) {
                updates.add(subscriptionUpdate.getUpdate());
            }
        }
        return updates;
    }

    public void sendEmailNotification(User user, List<Update> updates) throws MessagingException, jakarta.mail.MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(String.valueOf(new InternetAddress("your_email@example.com")));
        helper.setTo(user.getEmail());
        helper.setSubject("New Updates Available");

        // Process the email template
        Context context = new Context();
        context.setVariable("username", user.getUsername());
        context.setVariable("updates", updates);
        String emailContent = templateEngine.process("update_email", context);

        helper.setText(emailContent, true);
        javaMailSender.send(message);
    }

    @GetMapping("/update_email")
    public String updateEmail() {
        return "update_email";
    }
}
