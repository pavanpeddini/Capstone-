package AppWeb.AppWeb.controller;

import AppWeb.AppWeb.model.User;
import AppWeb.AppWeb.service.SubscriptionService;
import AppWeb.AppWeb.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String redirectToLoginPage() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("errorMessage", null);
        return "login";
    }

@PostMapping("/login")
public String login(@RequestParam String username, @RequestParam String password, Model model, HttpServletRequest request) {
    User user = userService.findByUsername(username);
    if (user == null) {
        // User is not registered
        model.addAttribute("errorMessage", "You are not registered, please register");
        return "login";
    } else if (!user.getPassword().equals(password)) {
        // Invalid credentials
        model.addAttribute("errorMessage", "Invalid credentials");
        return "login";
    } else {
        // Successful login
        HttpSession session = request.getSession();
        session.setAttribute("loggedInUser", user);
        return "redirect:/dashboard";
    }
}

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {
        // Check if the email already exists in the database
        if (userService.findByEmail(user.getEmail()) != null) {
            model.addAttribute("errorMessage", "Email already exists, please use a different email");
            return "register";
        }

        userService.save(user);
        return "redirect:/login";
    }


    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login";
    }
}
