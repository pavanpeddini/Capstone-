package AppWeb.AppWeb.service;

import AppWeb.AppWeb.model.Subscription;
import AppWeb.AppWeb.model.Update;
import AppWeb.AppWeb.model.User;
import AppWeb.AppWeb.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService {
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private JavaMailSender javaMailSender;


    public List<Subscription> findByUser(User user) {
        return subscriptionRepository.findByUser(user);
    }

    public void save(Subscription subscription) {
        subscriptionRepository.save(subscription);
    }

    public void sendEmailNotification(User user, List<Update> updates) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your_email@example.com");
        message.setTo(user.getEmail());
        message.setSubject("New Updates Available");
        StringBuilder sb = new StringBuilder();
        sb.append("Dear ").append(user.getUsername()).append(",\n\n");
        sb.append("New updates are available:\n");
        for (Update update : updates) {
            sb.append("- ").append(update.getDescription()).append("\n");
        }
        message.setText(sb.toString());
        javaMailSender.send(message);
    }
}
