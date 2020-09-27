package io.cofix.hedging.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import io.cofix.hedging.annotation.TokenRequired;
import io.cofix.hedging.service.UserService;
import io.cofix.hedging.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class AuthenticationInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
        // Pull token from the HTTP request header
        String token = httpServletRequest.getHeader("token");
        // If the method is not mapped directly through
        if(!(object instanceof HandlerMethod)){
            return true;
        }
        HandlerMethod handlerMethod=(HandlerMethod)object;
        Method method=handlerMethod.getMethod();
        // Check if there are any annotations that require user permission
        if (method.isAnnotationPresent(TokenRequired.class)) {
            TokenRequired userLoginToken = method.getAnnotation(TokenRequired.class);
            if (userLoginToken.required()) {
                // Perform authentication
                if (token == null) {
                    throw new RuntimeException("No token, please log in again");
                }
                // Get the User ID in token
                String userName;
                try {
                    userName = JWT.decode(token).getClaim("username").asString();
                } catch (JWTDecodeException j) {
                    throw new RuntimeException("401");
                }
                String password = userService.findPasswordByUserName(userName);
                if (password == null) {
                    throw new RuntimeException("The user does not exist, please log in again");
                }
                // Authentication token
                try {
                    if(!JwtUtil.verity(token,password)){
                        throw new RuntimeException("Invalid token");
                    }
                } catch (JWTVerificationException e) {
                    throw new RuntimeException("401");
                }
                return true;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
