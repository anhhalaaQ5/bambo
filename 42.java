package form;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
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
} \\\\\\\\\\\\\\\\\\\\\\
package form;

import org.apache.struts.action.ActionForm;

public class CustomerForm extends ActionForm {
    // Các trường từ CSDL
    private int id;
    private String name;
    private int sex;
    private String birthday;
    private String email;
    private String address;
    
    // Các trường phục vụ tìm kiếm
    private String customerName;  // Tên dùng để tìm kiếm
    private String birthdayFrom; // Ngày sinh từ
    private String birthdayTo;   // Ngày sinh đến
    
    // Các trường phục vụ xử lý form và hiển thị
    private String[] customerIds;  // Danh sách ID để xóa
    private String action;         // Hành động (add/edit/delete)
    private int currentPage = 1;   // Trang hiện tại
    
    // Constructor mặc định
    public CustomerForm() {
        super();
    }
    
    // Getters và Setters cho các trường từ CSDL
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getSex() { return sex; }
    public void setSex(int sex) { this.sex = sex; }
    
    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    // Getters và Setters cho các trường tìm kiếm
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getBirthdayFrom() { return birthdayFrom; }
    public void setBirthdayFrom(String birthdayFrom) { this.birthdayFrom = birthdayFrom; }
    
    public String getBirthdayTo() { return birthdayTo; }
    public void setBirthdayTo(String birthdayTo) { this.birthdayTo = birthdayTo; }
    
    // Getters và Setters cho các trường xử lý form
    public String[] getCustomerIds() { return customerIds; }
    public void setCustomerIds(String[] customerIds) { this.customerIds = customerIds; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }
} \\\\\\\\\\\\\\\\\\\\\\\\\\
package factory;

import dao.UserDAO;
import form.LoginForm;
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

        List<LoginForm> users = userDAO.validateAndGetUser(userid, pass);
        if (!users.isEmpty()) {
            return "";
        }
        return "Userid hoặc password không đúng";
    }
    
    public LoginForm getUserInfo(String userid, String pass) {
        List<LoginForm> users = userDAO.validateAndGetUser(userid, pass);
        return !users.isEmpty() ? users.get(0) : null;
    }
} \\\\\\\\\\\\\\\\\\\\\\\\\
package factory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import dao.CustomerDAO;
import form.CustomerForm;
import javax.servlet.http.HttpSession;

public class CustomerFactory {
    private final CustomerDAO customerDAO = new CustomerDAO();
    
    public String processSearch(String name, String sex, String birthdayFrom, 
                              String birthdayTo, String pageStr, HttpSession session) {
        session.setAttribute("searchName", name);
        session.setAttribute("searchSex", sex);
        session.setAttribute("searchBirthdayFrom", birthdayFrom);
        session.setAttribute("searchBirthdayTo", birthdayTo);
        
        int page = 1;
        if (pageStr != null && !pageStr.isEmpty()) {
            page = Integer.parseInt(pageStr);
        }
        session.setAttribute("currentPage", String.valueOf(page));

        int totalRecords = customerDAO.getTotalRecords(name, sex, birthdayFrom, birthdayTo);
        int maxPage = customerDAO.getMaxPage(totalRecords);
        session.setAttribute("maxPage", maxPage);

        if (page < 1) page = 1;
        if (page > maxPage) page = maxPage;

        return "/WEB-INF/views/search.jsp";
    }

    public List<CustomerForm> getCustomers(String name, String sex, 
                                         String birthdayFrom, String birthdayTo, int page) {
        return customerDAO.searchCustomers(name, sex, birthdayFrom, birthdayTo, page);
    }

    public String processDelete(String[] customerIds, HttpSession session) {
        if (customerIds == null || customerIds.length == 0) {
            return buildRedirectURL(session);
        }

        try {
            customerDAO.deleteCustomers(customerIds);
            return buildRedirectURL(session);
        } catch (Exception e) {
            return null;
        }
    }

    private String buildRedirectURL(HttpSession session) {
        StringBuilder redirectURL = new StringBuilder("search.jsp?");
        
        String searchName = (String) session.getAttribute("searchName");
        String searchSex = (String) session.getAttribute("searchSex");
        String searchBirthdayFrom = (String) session.getAttribute("searchBirthdayFrom");
        String searchBirthdayTo = (String) session.getAttribute("searchBirthdayTo");
        String currentPage = (String) session.getAttribute("currentPage");

        if (searchName != null && !searchName.isEmpty()) {
            redirectURL.append("customerName=")
                      .append(URLEncoder.encode(searchName, StandardCharsets.UTF_8))
                      .append("&");
        }
        if (searchSex != null && !searchSex.isEmpty() && !"-1".equals(searchSex)) {
            redirectURL.append("sex=")
                      .append(URLEncoder.encode(searchSex, StandardCharsets.UTF_8))
                      .append("&");
        }
        if (searchBirthdayFrom != null && !searchBirthdayFrom.isEmpty()) {
            redirectURL.append("birthdayFrom=")
                      .append(URLEncoder.encode(searchBirthdayFrom, StandardCharsets.UTF_8))
                      .append("&");
        }
        if (searchBirthdayTo != null && !searchBirthdayTo.isEmpty()) {
            redirectURL.append("birthdayTo=")
                      .append(URLEncoder.encode(searchBirthdayTo, StandardCharsets.UTF_8))
                      .append("&");
        }
        if (currentPage != null && !currentPage.isEmpty()) {
            redirectURL.append("page=")
                      .append(URLEncoder.encode(currentPage, StandardCharsets.UTF_8));
        }

        return redirectURL.toString();
    }
} 
\\\\\\\\\\\\\\\\\\\\\\\\\\\\
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import form.LoginForm;
import db.DBConnection;

public class UserDAO {
    public List<LoginForm> validateAndGetUser(String userid, String pass) {
        List<LoginForm> users = new ArrayList<>();
        
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
                    LoginForm user = new LoginForm();
                    user.setPsn(userRs.getInt("Psn"));
                    user.setUserid(userRs.getString("Userid"));
                    user.setPass(userRs.getString("Pass"));
                    user.setUsername(userRs.getString("Username"));
                    users.add(user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }

        public static void main(String[] args) {

            UserDAO userDAO = new UserDAO();
            List<LoginForm> users = userDAO.validateAndGetUser("user1", "password123");
            for (LoginForm user : users) {
                System.out.println(user.getUsername());
            }
        }

}
\\\\\\\\\\\\\\\\\\\\\\
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import db.DBConnection;
import dto.CustomerDTO;
import form.CustomerForm;

public class CustomerDAO {
    private static final int RECORDS_PER_PAGE = 5;

    public int getTotalRecords(String name, String sex, String birthdayFrom, String birthdayTo) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM MSTCUSTOMER WHERE DeleteYMD IS NULL");
        int paramIndex = 1;

        if (name != null && !name.trim().isEmpty()) {
            sql.append(" AND Name LIKE ? ESCAPE '\\'");
        }

        if (sex != null && !sex.trim().isEmpty() && !"-1".equals(sex.trim())) {
            sql.append(" AND Sex = ?");
        }

        if (birthdayFrom != null && !birthdayFrom.trim().isEmpty()) {
            sql.append(" AND Birthday >= ?");
        }

        if (birthdayTo != null && !birthdayTo.trim().isEmpty()) {
            sql.append(" AND Birthday <= ?");
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            if (name != null && !name.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + escapeLikePattern(name.trim()) + "%");
            }

            if (sex != null && !sex.trim().isEmpty() && !"-1".equals(sex.trim())) {
                ps.setString(paramIndex++, sex.trim());
            }

            if (birthdayFrom != null && !birthdayFrom.trim().isEmpty()) {
                ps.setString(paramIndex++, birthdayFrom.trim());
            }

            if (birthdayTo != null && !birthdayTo.trim().isEmpty()) {
                ps.setString(paramIndex++, birthdayTo.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<CustomerForm> searchCustomers(String name, String sex, String birthdayFrom, String birthdayTo, int page) {
        List<CustomerForm> customers = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT Id, Name, Sex, Birthday, Email, Address FROM MSTCUSTOMER WHERE DeleteYMD IS NULL");
        int paramIndex = 1;

        if (name != null && !name.trim().isEmpty()) {
            sql.append(" AND Name LIKE ? ESCAPE '\\'");
        }

        if (sex != null && !sex.trim().isEmpty() && !"-1".equals(sex.trim())) {
            sql.append(" AND Sex = ?");
        }

        if (birthdayFrom != null && !birthdayFrom.trim().isEmpty()) {
            sql.append(" AND Birthday >= ?");
        }

        if (birthdayTo != null && !birthdayTo.trim().isEmpty()) {
            sql.append(" AND Birthday <= ?");
        }

        sql.append(" ORDER BY Id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            if (name != null && !name.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + escapeLikePattern(name.trim()) + "%");
            }

            if (sex != null && !sex.trim().isEmpty() && !"-1".equals(sex.trim())) {
                ps.setString(paramIndex++, sex.trim());
            }

            if (birthdayFrom != null && !birthdayFrom.trim().isEmpty()) {
                ps.setString(paramIndex++, birthdayFrom.trim());
            }

            if (birthdayTo != null && !birthdayTo.trim().isEmpty()) {
                ps.setString(paramIndex++, birthdayTo.trim());
            }

            ps.setInt(paramIndex++, (page - 1) * RECORDS_PER_PAGE);
            ps.setInt(paramIndex, RECORDS_PER_PAGE);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CustomerForm customer = new CustomerForm();
                    customer.setId(rs.getInt("Id"));
                    customer.setName(rs.getString("Name"));
                    customer.setSex(rs.getString("Sex").equals("0") ? 0 : 1);
                    customer.setBirthday(rs.getString("Birthday"));
                    customer.setEmail(rs.getString("Email"));
                    customer.setAddress(rs.getString("Address"));
                    customers.add(customer);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customers;
    }

    public int getMaxPage(int totalRecords) {
        if (totalRecords == 0) return 1;
        return (int) Math.ceil((double) totalRecords / RECORDS_PER_PAGE);
    }

    public boolean deleteCustomers(String[] customerIds) {
        // Tạo câu SQL base
        StringBuilder sql = new StringBuilder("UPDATE MSTCUSTOMER SET DeleteYMD = GETDATE() WHERE Id IN (");
        
        // Tạo chuỗi tham số (?,?,?) tương ứng với số lượng ID
        for (int i = 0; i < customerIds.length; i++) {
            if (i > 0) {
                sql.append(",");
            }
            sql.append("?");
        }
        sql.append(")");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            // Set giá trị cho từng tham số
            for (int i = 0; i < customerIds.length; i++) {
                ps.setInt(i + 1, Integer.parseInt(customerIds[i]));
            }
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static void main(String[] args) {
        // Khởi tạo đối tượng CustomerDAO
        CustomerDAO customerDAO = new CustomerDAO();
        
        boolean deleteResult = customerDAO.deleteCustomers(new String[]{"1"});
        System.out.println("Kết quả xóa: " + (deleteResult ? "Thành công" : "Thất bại"));
        
    }
    
    public int getNextCustomerId() {
        String sql = "SELECT MAX(Id) + 1 FROM MSTCUSTOMER";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                int nextId = rs.getInt(1);
                return rs.wasNull() ? 1 : nextId;
            }
            return 1;
            
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean addCustomer(String name, String sex, String birthday, 
                             String email, String address, int userPsn) {
        String sql = "INSERT INTO MSTCUSTOMER (Id, Name, Sex, Birthday, Email, Address, " +
                    "DeleteYMD, Insert_ymd, Insertpsncd, Updateymd, Updatepsncd) " +
                    "VALUES (?, ?, ?, ?, ?, ?, NULL, GETDATE(), ?, GETDATE(), ?)";

        try (Connection conn = DBConnection.getConnection()) {
            // Lấy ID mới
            int newId = getNextCustomerId();
            if (newId == -1) {
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, newId);
                ps.setString(2, name);
                ps.setString(3, sex);
                ps.setString(4, birthday);
                ps.setString(5, email);
                ps.setString(6, address);
                ps.setInt(7, userPsn);
                ps.setInt(8, userPsn);

                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public CustomerDTO getCustomerById(int customerId) {
        String sql = "SELECT Id, Name, Sex, Birthday, Email, Address FROM MSTCUSTOMER " +
                    "WHERE Id = ? AND DeleteYMD IS NULL";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, customerId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new CustomerDTO(
                        rs.getInt("Id"),
                        rs.getString("Name"),
                        rs.getString("Sex").equals("0") ? 0 : 1,
                        rs.getString("Birthday"),
                        rs.getString("Email"),
                        rs.getString("Address")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateCustomer(int customerId, String name, String sex, 
                                String birthday, String email, String address, int userPsn) {
        String sql = "UPDATE MSTCUSTOMER SET " +
                    "Name = ?, Sex = ?, Birthday = ?, Email = ?, Address = ?, " +
                    "DeleteYMD = NULL, Updateymd = GETDATE(), Updatepsncd = ? " +
                    "WHERE Id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, name);
            ps.setString(2, sex);
            ps.setString(3, birthday);
            ps.setString(4, email);
            ps.setString(5, address);
            ps.setInt(6, userPsn);
            ps.setInt(7, customerId);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String escapeLikePattern(String pattern) {
        if (pattern == null) return null;
        return pattern.replace("%", "\\%")
                     .replace("_", "\\_");
    }
}
\\\\\\\\\\\\\\\\\\\\\\\\\
package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import factory.LoginFactory;
import form.LoginForm;

public class LoginAction extends Action {
    private final LoginFactory loginFactory = new LoginFactory();

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        LoginForm loginForm = (LoginForm) form;
        String action = request.getParameter("action");

        if ("logout".equals(action)) {
            HttpSession session = request.getSession(false);
            loginFactory.processLogout(session);
            return mapping.findForward("login");
        }

        if (request.getMethod().equals("GET")) {
            return mapping.findForward("login");
        }

        // Xử lý login
        String errorMsg = loginFactory.processLogin(loginForm.getUserid(), loginForm.getPass());
        
        if (!errorMsg.isEmpty()) {
            loginForm.setErrorMessage(errorMsg);
            return mapping.findForward("failure");
        }

        // Login thành công, lấy thông tin user
        LoginForm userInfo = loginFactory.getUserInfo(loginForm.getUserid(), loginForm.getPass());
        if (userInfo != null) {
            // Cập nhật thông tin vào form hiện tại
            loginForm.setPsn(userInfo.getPsn());
            loginForm.setUsername(userInfo.getUsername());
            
            // Lưu form vào session
            HttpSession session = request.getSession();
            session.setAttribute("user", loginForm);
        }
        
        return mapping.findForward("success");
    }
} \\\\\\\\\\\\\\\\\\\\\\\\\\\\
package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.*;
import form.CustomerForm;
import form.LoginForm;
import factory.CustomerFactory;
import java.util.List;

public class CustomerAction extends Action {
    private final CustomerFactory customerFactory = new CustomerFactory();

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        HttpSession session = request.getSession();
        LoginForm user = (LoginForm) session.getAttribute("user");
        
        if (user == null) {
            return mapping.findForward("login");
        }

        CustomerForm customerForm = (CustomerForm) form;
        String action = customerForm.getAction();

        if ("delete".equals(action)) {
            String[] customerIds = customerForm.getCustomerIds();
            String redirectURL = customerFactory.processDelete(customerIds, session);
            if (redirectURL != null) {
                response.sendRedirect(redirectURL);
                return null;
            }
        }

        // Xử lý tìm kiếm
        String viewPath = customerFactory.processSearch(
            customerForm.getCustomerName(),
            String.valueOf(customerForm.getSex()),
            customerForm.getBirthdayFrom(),
            customerForm.getBirthdayTo(),
            String.valueOf(customerForm.getCurrentPage()),
            session
        );

        // Lấy danh sách khách hàng
        List<CustomerForm> customers = customerFactory.getCustomers(
            customerForm.getCustomerName(),
            String.valueOf(customerForm.getSex()),
            customerForm.getBirthdayFrom(),
            customerForm.getBirthdayTo(),
            customerForm.getCurrentPage()
        );

        request.setAttribute("customers", customers);
        return mapping.findForward("success");
    }
} \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
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
        <html:form action="/login" method="post">
            <div class="form-group">
                <label for="userid">User ID:</label>
                <html:text property="userid" styleId="userid"/>
            </div>
            
            <div class="form-group">
                <label for="pass">Password:</label>
                <html:password property="pass" styleId="pass"/>
            </div>
            
            <!-- Label error luôn hiển thị -->
            <label id="errorLabel" class="error-label">
                <logic:notEmpty name="loginForm" property="errorMessage">
                    <bean:write name="loginForm" property="errorMessage"/>
                </logic:notEmpty>
                <logic:empty name="loginForm" property="errorMessage">
                    &nbsp;
                </logic:empty>
            </label>
            
            <div class="buttons">
                <html:submit value="Login"/>
                <html:button property="clearButton" onclick="clearForm()" value="Clear"/>
            </div>
        </html:form>
    </div>
</body>
</html>

\\\\\\\\\\\\\\\\\\\\\\\
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Danh sách khách hàng</title>
    <style>
        table { width: 80%; margin: auto; border-collapse: collapse; }
        th, td { border: 1px solid black; padding: 8px; text-align: left; }
        th { background-color: #f2f2f2; }
        .user-info { text-align: right; margin-bottom: 20px; }
        .search-form { width: 80%; margin: 20px auto; padding: 15px; border: 1px solid #ddd; }
        .search-form input, .search-form select { margin: 5px; padding: 5px; }
        button {
            padding: 5px 15px;
            margin: 0 5px;
            cursor: pointer;
            background-color: #ffffff;
            border: 1px solid #ddd;
            border-radius: 3px;
        }
        
        button:hover:not(:disabled) {
            background-color: #e9ecef;
        }
        
        button:disabled {
            cursor: not-allowed;
            opacity: 0.6;
        }
        
        span {
            display: inline-block;
            vertical-align: middle;
        }
        
        /* Style cho link ID và nút Add New */
        td a {
            color: blue;
            text-decoration: none;
            display: inline-block;
            vertical-align: middle;
        }
        
        td a:hover {
            text-decoration: underline;
        }
        
        .triangle {
            color: blue;
            font-size: 10px;
            margin-right: 5px;
            display: inline-block;
            vertical-align: middle;
        }
        
        /* Đảm bảo cell chứa ID và tam giác được căn chỉnh đúng */
        td {
            vertical-align: middle;
        }
    </style>
    <script src="${pageContext.request.contextPath}/js/search.js"></script>
</head>
<body>
    <div class="user-info">
        <bean:define id="user" name="user" scope="session"/>
        <label>Chào, <bean:write name="user" property="username"/></label> | 
        <html:link action="/login?action=logout">Đăng xuất</html:link>
    </div>

    <!-- Form tìm kiếm -->
    <div class="search-form">
        <html:form action="/search" method="get" onsubmit="return validateDates()">
            <label>Tên khách hàng:</label>
            <html:text property="customerName" value="${searchName}"/>
            
            <label>Giới tính:</label>
            <html:select property="sex">
                <html:option value="-1">Tất cả</html:option>
                <html:option value="0">Nam</html:option>
                <html:option value="1">Nữ</html:option>
            </html:select>
            
            <label>Ngày sinh từ:</label>
            <html:text property="birthdayFrom" value="${searchBirthdayFrom}" styleId="birthdayFrom"/>
            
            <label>Đến:</label>
            <html:text property="birthdayTo" value="${searchBirthdayTo}" styleId="birthdayTo"/>
            
            <html:submit value="Tìm kiếm"/>
        </html:form>
    </div>

    <!-- Form xử lý delete -->
    <html:form action="/search" method="post" styleId="customerForm">
        <html:hidden property="action" value="delete"/>
        <html:hidden property="customerName" value="${searchName}"/>
        <html:hidden property="sex" value="${searchSex}"/>
        <html:hidden property="birthdayFrom" value="${searchBirthdayFrom}"/>
        <html:hidden property="birthdayTo" value="${searchBirthdayTo}"/>
        <html:hidden property="currentPage" value="${currentPage}"/>
        
        <h2 style="text-align: center;">Danh sách khách hàng</h2>
        <table>
            <tr>
                <th><input type="checkbox" onclick="toggleAll(this)" id="checkAll"/></th>
                <th>ID</th>
                <th>Tên</th>
                <th>Giới tính</th>
                <th>Ngày sinh</th>
                <th>Địa chỉ</th>
            </tr>
            <logic:present name="customers">
                <logic:iterate id="customer" name="customers">
                    <tr>
                        <td><html:checkbox property="customerIds" value="${customer.id}"/></td>
                        <td>
                            <span class="triangle">▶</span>
                            <html:link action="/customer?action=edit" paramId="id" paramName="customer" paramProperty="id">
                                <bean:write name="customer" property="id"/>
                            </html:link>
                        </td>
                        <td><bean:write name="customer" property="name"/></td>
                        <td>
                            <logic:equal name="customer" property="sex" value="0">Nam</logic:equal>
                            <logic:equal name="customer" property="sex" value="1">Nữ</logic:equal>
                        </td>
                        <td>
                            <logic:notEmpty name="customer" property="birthday">
                                <bean:write name="customer" property="birthday"/>
                            </logic:notEmpty>
                            <logic:empty name="customer" property="birthday">
                                Không có thông tin
                            </logic:empty>
                        </td>
                        <td><bean:write name="customer" property="address"/></td>
                    </tr>
                </logic:iterate>
            </logic:present>
            <logic:notPresent name="customers">
                <tr>
                    <td colspan="6" style="text-align: center;">Không có dữ liệu</td>
                </tr>
            </logic:notPresent>
        </table>

        <!-- Các nút chức năng -->
        <div style="text-align: right; margin: 20px 10% 0 0;">
            <html:button property="" onclick="window.location.href='customer.do?action=add'" styleClass="button">Add New</html:button>
            <html:button property="" onclick="deleteSelected()" styleId="deleteButton">Delete</html:button>
        </div>
    </html:form>

    <!-- Phân trang -->
    <div style="text-align: center; margin-top: 20px;">
        <html:form action="/search" method="get">
            <html:hidden property="customerName" value="${searchName}"/>
            <html:hidden property="sex" value="${searchSex}"/>
            <html:hidden property="birthdayFrom" value="${searchBirthdayFrom}"/>
            <html:hidden property="birthdayTo" value="${searchBirthdayTo}"/>
            
            <html:submit property="page" value="1" disabled="${currentPage == 1}">First</html:submit>
            <html:submit property="page" value="${currentPage - 1}" disabled="${currentPage == 1}">Previous</html:submit>
            
            <span>Trang ${currentPage} / ${maxPage}</span>
            
            <html:submit property="page" value="${currentPage + 1}" disabled="${currentPage == maxPage}">Next</html:submit>
            <html:submit property="page" value="${maxPage}" disabled="${currentPage == maxPage}">Last</html:submit>
        </html:form>
    </div>
</body>
</html>
\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE struts-config PUBLIC
        "-//Apache Software Foundation//DTD Struts Configuration 1.2//EN"
        "http://struts.apache.org/dtds/struts-config_1_2.dtd">

<struts-config>
    <form-beans>
        <form-bean name="loginForm" type="form.LoginForm"/>
        <form-bean name="customerForm" type="form.CustomerForm"/>
    </form-beans>

    <global-forwards>
        <forward name="login" path="/login.jsp"/>
    </global-forwards>

    <action-mappings>
        <action path="/login"
                type="action.LoginAction"
                name="loginForm"
                scope="request"
                input="/login.jsp">
            <forward name="success" path="/search.do"/>
            <forward name="failure" path="/login.jsp"/>
        </action>

        <action path="/search"
                type="action.CustomerAction"
                name="customerForm"
                scope="request">
            <forward name="success" path="/WEB-INF/views/search.jsp"/>
            <forward name="failure" path="/login.jsp"/>
        </action>
    </action-mappings>

    <message-resources parameter="resources.ApplicationResources"/>
</struts-config> 
