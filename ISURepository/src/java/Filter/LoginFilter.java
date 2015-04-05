package Filter;

import Controller.UserController;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter("/secure/*")
public class LoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        UserController loginBean = (UserController) ((HttpServletRequest) request).getSession().getAttribute("userController");

        if (loginBean == null || !loginBean.isLogged()) {
            String contextPath = ((HttpServletRequest) request).getContextPath();
            ((HttpServletResponse) response).sendRedirect(contextPath + "/login.xhtml");
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig config) throws ServletException {
        // Nothing to do here!
    }

    @Override
    public void destroy() {
        // Nothing to do here!
    }

}
