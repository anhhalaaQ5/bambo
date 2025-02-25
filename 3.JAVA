package form;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import javax.servlet.http.HttpServletRequest;

public class LoginForm extends ActionForm {
    private String userid;
    private String pass;

    public String getUserid() { return userid; }
    public void setUserid(String userid) { this.userid = userid; }
    
    public String getPass() { return pass; }
    public void setPass(String pass) { this.pass = pass; }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        
        if (userid == null || userid.trim().isEmpty()) {
            errors.add("userid", new ActionMessage("error.userid.required"));
        }
        if (pass == null || pass.trim().isEmpty()) {
            errors.add("pass", new ActionMessage("error.password.required"));
        }
        
        return errors;
    }
} 

\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
package action;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import dto.UserDTO;
import factory.LoginFactory;
import form.LoginForm;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginAction extends Action {
    private final LoginFactory loginFactory = new LoginFactory();

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        
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
} \\\\\\\\\\\\\\\\\\\\
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import dto.UserDTO;
import db.DBConnection;

public class UserDAO {
    public List<UserDTO> validateAndGetUser(String userid, String pass) {
        List<UserDTO> users = new ArrayList<>();
        
        String countSql = "SELECT COUNT(*) AS cnt FROM MSTUSER WHERE Userid = ? AND Pass = ? AND Delete_ymd IS NULL";
        String userSql = "SELECT Psn, Userid, Pass, Username FROM MSTUSER WHERE Userid = ? AND Delete_ymd IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement countPs = conn.prepareStatement(countSql);
             PreparedStatement userPs = conn.prepareStatement(userSql)) {

            // Thực hiện query đếm số lượng user
            countPs.setString(1, userid);
            countPs.setString(2, pass);
            ResultSet countRs = countPs.executeQuery();

            if (countRs.next() && countRs.getInt("cnt") == 1) {
                // Nếu tìm thấy đúng 1 user, thực hiện query lấy thông tin
                userPs.setString(1, userid);
                ResultSet userRs = userPs.executeQuery();

                if (userRs.next()) {
                    users.add(new UserDTO(
                        userRs.getInt("Psn"),
                        userRs.getString("Userid"),
                        userRs.getString("Pass"),
                        userRs.getString("Username")
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

        public static void main(String[] args) {

            UserDAO userDAO = new UserDAO();
            List<UserDTO> users = userDAO.validateAndGetUser("user1", "password123");
            for (UserDTO user : users) {
                System.out.println(user.getUsername());
            }
        }

}
\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
package factory;

import dao.UserDAO;
import dto.UserDTO;
import javax.servlet.http.HttpSession;
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
} \\\\\\\\\\\\\\\\\\\\\\\\\\\\\
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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
        <html:form action="/loginSubmit">
            <label for="userid">User ID:</label>
            <html:text property="userid" styleId="userid"/>
            <html:errors property="userid"/>
            
            <label for="pass">Password:</label>
            <html:password property="pass" styleId="pass"/>
            <html:errors property="pass"/>
            
            <p class="error">
                ${requestScope.error}
            </p>
            
            <html:submit value="Login"/>
            <html:button property="clear" onclick="clearForm()">Clear</html:button>
        </html:form>
    </div>

    <script>
        function clearForm() {
            document.getElementById("userid").value = "";
            document.getElementById("pass").value = "";
            document.querySelector(".error").innerText = "";
        }
    </script>
</body>
</html>
\\\\\\\\\\\\\\\\\\\\\\
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE struts-config PUBLIC
    "-//Apache Software Foundation//DTD Struts Configuration 1.3//EN"
    "http://jakarta.apache.org/struts/dtds/struts-config_1_3.dtd">
<struts-config>
    <form-beans>
        <form-bean name="loginForm" type="form.LoginForm"/>
    </form-beans>

    <action-mappings>
        <!-- Action mapping cho trang login ban đầu -->
        <action path="/login"
                type="org.apache.struts.actions.ForwardAction"
                parameter="/login.jsp"/>
                
        <!-- Action mapping cho xử lý form login -->
        <action path="/loginSubmit"
                type="action.LoginAction"
                name="loginForm"
                scope="request">
            <forward name="success" path="/WEB-INF/views/search.jsp"/>
            <forward name="failure" path="/login.jsp"/>
        </action>
        
        <action path="/logout"
                type="action.LogoutAction">
            <forward name="success" path="/login.jsp"/>
        </action>
    </action-mappings>
</struts-config> 
