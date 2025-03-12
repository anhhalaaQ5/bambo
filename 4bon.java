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
        
        // Kiểm tra validation trước
        if (userid == null || userid.trim().isEmpty()) {
            messages.add("login", new ActionMessage("error.userid.required"));
            return messages;
        }
        
        if (pass == null || pass.trim().isEmpty()) {
            messages.add("login", new ActionMessage("error.password.required"));
            return messages;
        }
        
        // Sau khi validate OK mới kiểm tra đăng nhập
        List<UserDTO> users = userDAO.validateAndGetUser(userid, pass);
        if (users.isEmpty()) {
            messages.add("login", new ActionMessage("error.login.invalid"));
            return messages;
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
}
\\\\\\\\\\\\\\\\\\\\\\\\\\\\
package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;

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

        // Chỉ xử lý login khi có action=login
        if ("login".equals(action)) {
            // Xử lý login và lấy messages
            ActionMessages messages = loginFactory.processLogin(loginForm.getUserid(), loginForm.getPass());
            if (!messages.isEmpty()) {
                saveErrors(request, messages);
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
        
        // Trường hợp mới vào trang
        return mapping.findForward("failure");
    }
}
