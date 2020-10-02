package io.cofix.hedging.controller;

import io.cofix.hedging.annotation.TokenRequired;
import io.cofix.hedging.service.UserService;
import io.cofix.hedging.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    UserService userService;

    @RequestMapping(value = "/login")
    public ModelAndView login(ModelMap model) {

        return new ModelAndView("login", model);
    }

    @PostMapping(value = "/verify")
    public String verify(@RequestParam String userName, String passwd, HttpServletRequest request, ModelMap mmp) {
        if (userName.equals(userService.getUserName()) && passwd.equals(userService.getPasswd())) {
            String token = JwtUtil.sign(userName, passwd);

            HttpSession session = request.getSession();
            Map<String, Object> user = new HashMap<>();
            user.put("userName", userName);
            user.put("passwd", passwd);
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(15 * 60);

            return token;
        }

        return "";
    }

    @TokenRequired
    @PostMapping("update-passwd")
    public void updaePw(@RequestParam String passwd) {
        if (null == passwd || "".equals(passwd)) return;

        userService.setPassword(passwd);
    }
}
