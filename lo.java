<action path="/login"
        type="action.LoginAction"
        name="loginForm"
        input="/login.jsp"
        scope="request"
        validate="true">
    <forward name="success" path="/search.jsp"/>
    <forward name="failure" path="/login.jsp"/>
</action>


  \\\\\\\\\\\\\\\
  package form;

import org.apache.struts.action.ActionForm;

public class LoginForm extends ActionForm {
    private String userid;
    private String pass;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
\\\\\\\\\\\\\\\\\\
package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import dto.UserDTO;
import factory.LoginFactory;
import form.LoginForm;

public class LoginAction extends Action {
    private final LoginFactory loginFactory = new LoginFactory();

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response) {
        LoginForm loginForm = (LoginForm) form;
        String userid = loginForm.getUserid();
        String pass = loginForm.getPass();

        String errorMsg = loginFactory.processLogin(userid, pass);

        if (!errorMsg.isEmpty()) {
            request.setAttribute("error", errorMsg);
            return mapping.findForward("failure");
        }

        UserDTO user = loginFactory.getUserInfo(userid, pass);
        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
        }

        return mapping.findForward("success");
    }
}
\\\\\\\\\\\\\\\\
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đăng nhập</title>
    <style>
        body { text-align: center; font-family: Arial, sans-serif; }
        .container { width: 300px; margin: 100px auto; padding: 20px; border: 1px solid black; border-radius: 5px; }
        input { width: 100%; padding: 8px; margin: 5px 0; }
        .error { color: red; }
    </style>
</head>
<body>
    <div class="container">
        <h2>Đăng nhập</h2>
        <html:form action="/login" method="post">
            <label for="userid">User ID:</label>
            <html:text property="userid" style="width: 100%;" />

            <label for="pass">Password:</label>
            <html:password property="pass" style="width: 100%;" />

            <p class="error">
                <bean:write name="error" />
            </p>

            <html:submit>Login</html:submit>
            <html:reset>Clear</html:reset>
        </html:form>
    </div>
</body>
</html>
\\\\\\\\\\\\\\\\\\
      <servlet>
    <servlet-name>action</servlet-name>
    <servlet-class>org.apache.struts.action.ActionServlet</servlet-class>
    <init-param>
        <param-name>config</param-name>
        <param-value>/WEB-INF/struts-config.xml</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
</servlet>

<servlet-mapping>
    <servlet-name>action</servlet-name>
    <url-pattern>*.do</url-pattern>
</servlet-mapping>
\\\\\\\\\\\\\\

      package factory;

import dao.UserDAO;
import dto.UserDTO;
import jakarta.servlet.http.HttpSession;
import java.util.List;

public class LoginFactory {
    private final UserDAO userDAO = new UserDAO();
    
    public void processLogout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }
    
    public String processLogin(String userid, String pass) {
        if (userid == null || userid.trim().isEmpty()) {
            return "Vui lòng nhập userid";
        }
        if (pass == null || pass.trim().isEmpty()) {
            return "Vui lòng nhập password";
        }

        List<UserDTO> users = userDAO.validateAndGetUser(userid, pass);
        return users.isEmpty() ? "Userid hoặc password không đúng" : "";
    }
    
    public UserDTO getUserInfo(String userid, String pass) {
        List<UserDTO> users = userDAO.validateAndGetUser(userid, pass);
        return users.isEmpty() ? null : users.get(0);
    }
} 
