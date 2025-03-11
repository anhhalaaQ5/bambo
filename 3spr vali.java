package form;

import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import javax.servlet.http.HttpServletRequest;

public class LoginForm extends ActionForm {
    private int psn;
    private String userid;
    private String pass;
    private String username;
    private String errorMessage;

    public int getPsn() { return psn; }
    public void setPsn(int psn) { this.psn = psn; }
    public String getUserid() { return userid; }
    public void setUserid(String userid) { this.userid = userid; }
    public String getPass() { return pass; }
    public void setPass(String pass) { this.pass = pass; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        psn = 0;
        userid = "";
        pass = "";
        username = "";
        errorMessage = "";
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        
        if (userid == null || userid.trim().isEmpty()) {
            errors.add("login", new ActionMessage("error.userid.required"));
            return errors;
        }
        
        if (pass == null || pass.trim().isEmpty()) {
            errors.add("login", new ActionMessage("error.password.required"));
            return errors;
        }
        
        return errors;
    }
} \\\\\\\\\\\\\\\\\\\\\\\\\
error.userid.required=Vui lòng nhập userid
error.password.required=Vui lòng nhập password
error.login.invalid=Userid hoặc password không đúng \\\\\\\\\\\\\\\\\\\\
  \\\\\\\\\\\\\\\\\\\\\\\\\\\
  <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Đăng nhập</title>
    <style>
        body { text-align: center; font-family: Arial, sans-serif; }
        .container { width: 300px; margin: 100px auto; padding: 20px; border: 1px solid black; border-radius: 5px; }
        input { width: 100%; padding: 8px; margin: 5px 0; }
        .error-label { 
            color: red; 
            margin: 10px 0;
            display: block;
            text-align: left;
            font-size: 14px;
            height: 20px; /* Thêm chiều cao cố định */
        }
        .form-group { margin-bottom: 15px; }
        .buttons { margin-top: 20px; }
        .buttons input { width: auto; margin: 0 5px; padding: 8px 20px; }
        label { display: block; text-align: left; margin-bottom: 5px; }
    </style>
    <script type="text/javascript">
        function clearForm() {
            document.getElementById('userid').value = '';
            document.getElementById('pass').value = '';
            document.getElementById('errorLabel').innerHTML = '&nbsp;';
            return false;
        }
    </script>
</head>
<body>
    <div class="container">
        <h2>Đăng nhập</h2>
        
        <!-- Chỉ hiển thị messages có key "login" -->
        <div id="errorLabel" class="error-label">
            <html:errors property="login"/>
        </div>
        
        <html:form action="/login" method="post">
            <div class="form-group">
                <label for="userid">User ID:</label>
                <html:text property="userid" styleId="userid"/>
            </div>
            
            <div class="form-group">
                <label for="pass">Password:</label>
                <html:password property="pass" styleId="pass"/>
            </div>
            
            <div class="buttons">
                <html:submit value="Login"/>
                <html:button property="clearButton" onclick="clearForm()" value="Clear"/>
            </div>
        </html:form>
    </div>
</body>
</html>
      \\\\\\\\\\\\\\\\\\\\\\\\
      package factory;

import java.util.List;
import dao.UserDAO;
import dto.UserDTO;
import form.LoginForm;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

public class LoginFactory {
    private UserDAO userDAO;
    
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    
    public void processLogout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }
    
    public ActionMessages processLogin(String userid, String pass) {
        ActionMessages messages = new ActionMessages();
        
        List<UserDTO> users = userDAO.validateAndGetUser(userid, pass);
        if (users.isEmpty()) {
            messages.add("login", new ActionMessage("error.login.invalid"));
        }
        
        return messages;
    }
    
    public LoginForm getUserInfo(String userid, String pass) {
        List<UserDTO> users = userDAO.validateAndGetUser(userid, pass);
        if (!users.isEmpty()) {
            UserDTO user = users.get(0);
            LoginForm loginForm = new LoginForm();
            loginForm.setPsn(user.getPsn());
            loginForm.setUserid(user.getUserid());
            loginForm.setUsername(user.getUsername());
            return loginForm;
        }
        return null;
    }
} \\\\\\\\\\\\\\\\\\\\\\\\\\
package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.action.ActionErrors;

import factory.LoginFactory;
import form.LoginForm;

public class LoginAction extends Action {
    private LoginFactory loginFactory;

    public void setLoginFactory(LoginFactory loginFactory) {
        this.loginFactory = loginFactory;
    }

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        LoginForm loginForm = (LoginForm) form;
        String action = request.getParameter("action");

        // Xử lý logout
        if ("logout".equals(action)) {
            HttpSession session = request.getSession(false);
            loginFactory.processLogout(session);
            response.sendRedirect("login.do");
            return null;
        }

        // Nếu là GET request, hiển thị trang login
        if (request.getMethod().equals("GET")) {
            return mapping.findForward("failure");
        }

        // Khởi tạo ActionErrors một lần
        ActionErrors errors = loginForm.validate(mapping, request);
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
            return mapping.findForward("failure");
        }

        // Xử lý login và kiểm tra messages
        ActionMessages messages = loginFactory.processLogin(loginForm.getUserid(), loginForm.getPass());
        if (!messages.isEmpty()) {
            // Thêm messages vào errors đã có
            errors.add(messages);
            saveErrors(request, errors);
            return mapping.findForward("failure");
        }

        // Login thành công, lấy thông tin user
        LoginForm userInfo = loginFactory.getUserInfo(loginForm.getUserid(), loginForm.getPass());
        if (userInfo != null) {
            loginForm.setPsn(userInfo.getPsn());
            loginForm.setUsername(userInfo.getUsername());
            
            HttpSession session = request.getSession();
            session.setAttribute("user", loginForm);
            return mapping.findForward("success");
        }
        
        return mapping.findForward("failure");
    }
}
