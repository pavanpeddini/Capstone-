package AppWeb.AppWeb.service;

import AppWeb.AppWeb.model.User;
import AppWeb.AppWeb.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    public void save(User user) {
        userRepository.save(user);
    }

    private String extractUserIdFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("userId")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

//    public User getCurrentUser(HttpServletRequest request) {
//        String userId = extractUserIdFromRequest(request);
//        if (userId != null) {
//            User user = userRepository.findById(Long.parseLong(userId)).orElse(null);
//            return user;
//        }
//        return null;
//    }

    public User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return (User) session.getAttribute("loggedInUser");
    }
}
