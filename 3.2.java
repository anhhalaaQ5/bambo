<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee 
         http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">

    <display-name>Struts Application</display-name>

    <!-- Struts Action Servlet Configuration -->
    <servlet>
        <servlet-name>action</servlet-name>
        <servlet-class>org.apache.struts.action.ActionServlet</servlet-class>
        <init-param>
            <param-name>config</param-name>
            <param-value>/WEB-INF/struts-config.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <!-- Struts Action Servlet Mapping -->
    <servlet-mapping>
        <servlet-name>action</servlet-name>
        <url-pattern>*.do</url-pattern>
    </servlet-mapping>

    <!-- Welcome File List -->
    <welcome-file-list>
        <welcome-file>index.jsp</welcome-file>
    </welcome-file-list>

</web-app>
\\\\\\\\\\\\\\\\
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE struts-config PUBLIC
    "-//Apache Software Foundation//DTD Struts Configuration 1.3//EN"
    "http://jakarta.apache.org/struts/dtds/struts-config_1_3.dtd">
<struts-config>
    <form-beans>
        <form-bean name="loginForm" type="form.LoginForm"/>
        <form-bean name="searchForm" type="form.SearchForm"/>
    </form-beans>

    <global-forwards>
        <forward name="welcome" path="/login.do"/>
    </global-forwards>

    <action-mappings>
        <!-- Login page display -->
        <action path="/login"
                forward="/login.jsp"/>
        
        <!-- Login form submission -->
        <action path="/loginSubmit"
                type="action.LoginAction"
                name="loginForm"
                scope="request">
            <forward name="success" path="/search.do" redirect="true"/>
            <forward name="failure" path="/login.jsp"/>
        </action>
        
        <!-- Search actions -->
        <action path="/search"
                type="action.SearchAction"
                name="searchForm"
                scope="request">
            <forward name="success" path="/WEB-INF/views/search.jsp"/>
            <forward name="failure" path="/login.jsp"/>
        </action>
        
        <action path="/logout"
                type="action.LogoutAction">
            <forward name="success" path="/login.jsp"/>
        </action>
    </action-mappings>

    <!-- Message Resources -->
    <message-resources parameter="resources.ApplicationResources"/>
</struts-config> \\\\\\\\\\\\\\\\\\\\\\
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login Page</title>
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
\\\\\\\\\\\\\\\\\
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Search Customers</title>
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
    <div class="container">
        <h2>Search Customers</h2>
        
        <!-- Search Form -->
        <html:form action="/search">
            <div class="form-group">
                <label for="customerName">Customer Name:</label>
                <html:text property="customerName" styleId="customerName"/>
            </div>
            
            <div class="form-group">
                <label for="sex">Sex:</label>
                <html:select property="sex" styleId="sex">
                    <html:option value="-1">All</html:option>
                    <html:option value="0">Male</html:option>
                    <html:option value="1">Female</html:option>
                </html:select>
            </div>
            
            <div class="form-group">
                <label for="birthdayFrom">Birthday From:</label>
                <html:text property="birthdayFrom" styleId="birthdayFrom"/>
                
                <label for="birthdayTo">Birthday To:</label>
                <html:text property="birthdayTo" styleId="birthdayTo"/>
            </div>
            
            <html:submit value="Search"/>
            <html:button property="clear" onclick="clearForm()">Clear</html:button>
        </html:form>

        <!-- Results Table -->
        <logic:present name="customers">
            <table border="1">
                <tr>
                    <th>Select</th>
                    <th>Customer Name</th>
                    <th>Sex</th>
                    <th>Birthday</th>
                    <th>Email</th>
                    <th>Address</th>
                </tr>
                <logic:iterate id="customer" name="customers">
                    <tr>
                        <td><input type="checkbox" name="customerIds" value="${customer.id}"/></td>
                        <td><bean:write name="customer" property="name"/></td>
                        <td><bean:write name="customer" property="sex"/></td>
                        <td><bean:write name="customer" property="birthday"/></td>
                        <td><bean:write name="customer" property="email"/></td>
                        <td><bean:write name="customer" property="address"/></td>
                    </tr>
                </logic:iterate>
            </table>
            
            <!-- Pagination -->
            <div class="pagination">
                <logic:present name="maxPage">
                    <logic:iterate id="pageNum" name="pageNumbers">
                        <a href="search.do?page=<bean:write name="pageNum"/>">
                            <bean:write name="pageNum"/>
                        </a>
                    </logic:iterate>
                </logic:present>
            </div>
        </logic:present>
    </div>

    <script>
    function clearForm() {
        document.getElementById('customerName').value = '';
        document.getElementById('sex').value = '-1';
        document.getElementById('birthdayFrom').value = '';
        document.getElementById('birthdayTo').value = '';
    }
    </script>
</body>
</html>
\\\\\\\\\\\\\\\\\\\\
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
} \\\\\\\\\\\\\\\\\\\\\\\\\\
package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import dto.UserDTO;
import factory.CustomerFactory;
import form.SearchForm;

import java.util.ArrayList;
import java.util.List;

public class SearchAction extends Action {
    private final CustomerFactory customerFactory = new CustomerFactory();

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        HttpSession session = request.getSession();
        UserDTO user = (UserDTO) session.getAttribute("user");
        
        if (user == null) {
            return mapping.findForward("failure");
        }

        SearchForm searchForm = (SearchForm) form;
        String name = searchForm.getCustomerName();
        String sex = searchForm.getSex();
        String birthdayFrom = searchForm.getBirthdayFrom();
        String birthdayTo = searchForm.getBirthdayTo();
        String pageStr = searchForm.getPage();

        // Process search in factory
        String viewPath = customerFactory.processSearch(name, sex, birthdayFrom, birthdayTo, pageStr, session);
        
        // Get page number from session after processing
        int page = Integer.parseInt((String) session.getAttribute("currentPage"));
        
        // Get customers list and set to request
        request.setAttribute("customers", 
            customerFactory.getCustomers(name, sex, birthdayFrom, birthdayTo, page));

        // Get total records and max page
        int totalRecords = customerFactory.getTotalRecords(name, sex, birthdayFrom, birthdayTo);
        int maxPage = customerFactory.getMaxPage(totalRecords);
        
        // Create page numbers list
        List<Integer> pageNumbers = new ArrayList<>();
        for (int i = 1; i <= maxPage; i++) {
            pageNumbers.add(i);
        }
        request.setAttribute("pageNumbers", pageNumbers);
        request.setAttribute("maxPage", maxPage);
        request.setAttribute("currentPage", page);

        return mapping.findForward("success");
    }
} \\\\\\\\\\\\\\\\\\\\\\\\\
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
} 
\\\\\\\\\\\\\\\\\\\\
package factory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import dao.CustomerDAO;
import dto.CustomerDTO;
import dto.UserDTO;
import javax.servlet.http.HttpSession;

public class CustomerFactory {
    private final CustomerDAO customerDAO = new CustomerDAO();
    
    public String processSearch(String name, String sex, String birthdayFrom, String birthdayTo, 
                              String pageStr, HttpSession session) {
        // Lưu các điều kiện tìm kiếm vào session
        session.setAttribute("searchName", name);
        session.setAttribute("searchSex", sex);
        session.setAttribute("searchBirthdayFrom", birthdayFrom);
        session.setAttribute("searchBirthdayTo", birthdayTo);
        
        // Xử lý phân trang
        int page = 1;
        if (pageStr != null && !pageStr.isEmpty()) {
            page = Integer.parseInt(pageStr);
        }
        session.setAttribute("currentPage", String.valueOf(page));

        // Lấy tổng số bản ghi và tính số trang
        int totalRecords = customerDAO.getTotalRecords(name, sex, birthdayFrom, birthdayTo);
        int maxPage = customerDAO.getMaxPage(totalRecords);

        // Điều chỉnh page nếu vượt quá giới hạn
        if (page < 1) page = 1;
        if (page > maxPage) page = maxPage;

        return "/WEB-INF/views/search.jsp";
    }

    public List<CustomerDTO> getCustomers(String name, String sex, String birthdayFrom, 
                                        String birthdayTo, int page) {
        return customerDAO.searchCustomers(name, sex, birthdayFrom, birthdayTo, page);
    }

    public String processDelete(String[] customerIds, HttpSession session) {
        if (customerIds == null || customerIds.length == 0) {
            return buildRedirectURL(session);
        }

        try {
            customerDAO.deleteCustomers(customerIds);
            return buildRedirectURL(session);
        } catch (RuntimeException e) {
            return null; // Return null to indicate error
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

    public int getTotalRecords(String name, String sex, String birthdayFrom, String birthdayTo) {
        return customerDAO.getTotalRecords(name, sex, birthdayFrom, birthdayTo);
    }

    public int getMaxPage(int totalRecords) {
        return customerDAO.getMaxPage(totalRecords);
    }
} \
\\\\\\\\\\\\\\\\\\\
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
\\\\\\\\\\\\\\\\\\\\\\\
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import db.DBConnection;
import dto.CustomerDTO;

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

    public List<CustomerDTO> searchCustomers(String name, String sex, String birthdayFrom, String birthdayTo, int page) {
        List<CustomerDTO> customers = new ArrayList<>();
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
                    CustomerDTO customer = new CustomerDTO(
                        rs.getInt("Id"),
                        rs.getString("Name"),
                        rs.getString("Sex").equals("0") ? 0 : 1,
                        rs.getString("Birthday"),
                        rs.getString("Email"),
                        rs.getString("Address")
                    );
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
\\\\\\\\\\\\\\\\\\\\\\\
package form;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import javax.servlet.http.HttpServletRequest;

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
} \\\\\\\\\\\\\\\\\\\
package form;

import org.apache.struts.action.ActionForm;

public class SearchForm extends ActionForm {
    private String customerName;
    private String sex;
    private String birthdayFrom;
    private String birthdayTo;
    private String page;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthdayFrom() {
        return birthdayFrom;
    }

    public void setBirthdayFrom(String birthdayFrom) {
        this.birthdayFrom = birthdayFrom;
    }

    public String getBirthdayTo() {
        return birthdayTo;
    }

    public void setBirthdayTo(String birthdayTo) {
        this.birthdayTo = birthdayTo;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }
} 
