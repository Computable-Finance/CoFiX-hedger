package io.cofix.hedging.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * HTTP request interception
 */
@Component
public class PermissionInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With,Access-Control-Allow-Headers, Content-Type, Accept, Connection, User-Agent, Cookie, Authorization");

        // OPTIONS requests direct release
        if (request.getMethod().toUpperCase().equals("OPTIONS")) {
            return true;
        }

        String uri = request.getRequestURI();
        // A URL with some extension
        if (uri.indexOf(".") > -1) {
            String extension = uri.substring(uri.lastIndexOf(".") + 1);
            return "css".equalsIgnoreCase(extension) || "js".equalsIgnoreCase(extension)
                    || "jpg".equalsIgnoreCase(extension) || "gif".equalsIgnoreCase(extension)
                    || "png".equalsIgnoreCase(extension) || "mp4".equalsIgnoreCase(extension)
                    || "swf".equalsIgnoreCase(extension) || "html".equalsIgnoreCase(extension)
                    || "htm".equalsIgnoreCase(extension) || "doc".equalsIgnoreCase(extension)
                    || "xls".equalsIgnoreCase(extension);
        }

        // The URI for direct release
        if ("/auth/login".equalsIgnoreCase(uri)||"/auth/verify".equalsIgnoreCase(uri)) {
            return true;
        }

        HttpSession session = request.getSession();
        Object user = session.getAttribute("user");
        // To get the user, I'm just going to do some simple authentication here
        if (user == null) {
            request.getRequestDispatcher("/auth/login").forward(request, response);
            return false;
        }

        return true;
    }
}
