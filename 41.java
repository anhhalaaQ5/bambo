package form;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionForm;

public class LoginForm extends ActionForm {
    private int psn;
    private String userid;
    private String pass;
    private String username;
    private String errorMessage;

    // Các getter/setter giữ nguyên

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        
        // Bỏ qua validate cho request logout và GET
        String action = request.getParameter("action");
        if ("logout".equals(action) || "GET".equals(request.getMethod())) {
            return errors;
        }

        // Validate userid
        if (userid == null || userid.trim().isEmpty()) {
            errors.add("userid", new ActionMessage("error.userid.required"));
            this.errorMessage = "User ID không được để trống";
        }

        // Validate password
        if (pass == null || pass.trim().isEmpty()) {
            errors.add("pass", new ActionMessage("error.password.required"));
            this.errorMessage = "Password không được để trống";
        }

        return errors;
    }

    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        psn = 0;
        userid = "";
        pass = "";
        username = "";
        errorMessage = "";
    }
}\\\\\\\\\\\\\\\\\\\\\\\
package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.*;
import factory.LoginFactory;
import form.LoginForm;
import dao.UserDAO;
import java.util.List;

public class LoginAction extends Action {
    private final LoginFactory loginFactory = new LoginFactory();
    private final UserDAO userDAO = new UserDAO();

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

        // Kiểm tra validate - Struts sẽ tự động gọi validate() trong LoginForm
        if (!loginForm.getErrorMessage().isEmpty()) {
            return mapping.findForward("failure");
        }

        // Kiểm tra đăng nhập từ database
        List<LoginForm> users = userDAO.validateAndGetUser(loginForm.getUserid(), loginForm.getPass());
        
        if (users.isEmpty()) {
            loginForm.setErrorMessage("User ID hoặc Password không đúng");
            return mapping.findForward("failure");
        }

        // Login thành công
        LoginForm userInfo = users.get(0);
        loginForm.setPsn(userInfo.getPsn());
        loginForm.setUsername(userInfo.getUsername());
        
        HttpSession session = request.getSession();
        session.setAttribute("user", loginForm);
        return mapping.findForward("success");
    }
}\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
<!-- Giữ nguyên code cũ của login.jsp -->
<label id="errorLabel" class="error-label">
    <logic:notEmpty name="loginForm" property="errorMessage">
        <bean:write name="loginForm" property="errorMessage"/>
    </logic:notEmpty>
    <logic:empty name="loginForm" property="errorMessage">
        &nbsp;
    </logic:empty>
</label>
