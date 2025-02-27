<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE struts-config PUBLIC
        "-//Apache Software Foundation//DTD Struts Configuration 1.2//EN"
        "http://struts.apache.org/dtds/struts-config_1_2.dtd">

<struts-config>
    <form-beans>
        <form-bean name="loginForm" type="form.LoginForm"/>
        <form-bean name="customerForm" type="form.CustomerForm"/>
    </form-beans>

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

    <global-forwards>
        <forward name="welcome" path="/login.do"/>
    </global-forwards>

    <message-resources parameter="resources.ApplicationResources"/>
</struts-config>

\\\\\\\\\\\\
package form;

import org.apache.struts.action.ActionForm;
import dto.CustomerDTO;
import java.util.List;

public class CustomerForm extends ActionForm {
    // Các trường điều kiện tìm kiếm
    private String customerName;
    private String sex;
    private String birthdayFrom;
    private String birthdayTo;
    
    // Các trường xử lý form và hiển thị
    private String[] customerIds;
    private String action;
    private int currentPage = 1;
    
    // Kết quả tìm kiếm
    private List<CustomerDTO> searchResults;
    
    // Constructor
    public CustomerForm() {
        super();
    }
    
    // Getters và Setters cho điều kiện tìm kiếm
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }
    
    public String getBirthdayFrom() { return birthdayFrom; }
    public void setBirthdayFrom(String birthdayFrom) { this.birthdayFrom = birthdayFrom; }
    
    public String getBirthdayTo() { return birthdayTo; }
    public void setBirthdayTo(String birthdayTo) { this.birthdayTo = birthdayTo; }
    
    // Getters và Setters cho xử lý form
    public String[] getCustomerIds() { return customerIds; }
    public void setCustomerIds(String[] customerIds) { this.customerIds = customerIds; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }
    
    // Getter và Setter cho kết quả tìm kiếm
    public List<CustomerDTO> getSearchResults() { return searchResults; }
    public void setSearchResults(List<CustomerDTO> searchResults) { this.searchResults = searchResults; }
}
\\\\\\\\\\\\\\\\\\\\\\
package dto;

public class CustomerDTO {
    private int id;
    private String name;
    private int sex;
    private String birthday;
    private String email;
    private String address;

    public CustomerDTO(int id, String name, int sex, String birthday, String email, String address) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.birthday = birthday;
        this.email = email;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSex() {
        return sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }
}
\\\\\\\\\\\\\\\\\
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
\\\\\\\\\\\\\\\\\\\\\\\\\\
package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.*;
import form.CustomerForm;
import form.LoginForm;
import dao.CustomerDAO;
import java.util.List;
import dto.CustomerDTO;

public class CustomerAction extends Action {
    private final CustomerDAO customerDAO = new CustomerDAO();

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        HttpSession session = request.getSession();
        LoginForm user = (LoginForm) session.getAttribute("user");
        
        // Kiểm tra session, nếu không có thì redirect về login.do
        if (user == null) {
            response.sendRedirect("login.do");
            return null;
        }

        CustomerForm customerForm = (CustomerForm) form;
        String action = customerForm.getAction();

        // Xử lý page parameter từ request
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                customerForm.setCurrentPage(Integer.parseInt(pageParam));
            } catch (NumberFormatException e) {
                customerForm.setCurrentPage(1);
            }
        }

        // Xử lý delete
        if ("delete".equals(action)) {
            String[] customerIds = customerForm.getCustomerIds();
            if (customerIds != null && customerIds.length > 0) {
                try {
                    customerDAO.deleteCustomers(customerIds);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Lưu điều kiện tìm kiếm vào session
        session.setAttribute("searchName", customerForm.getCustomerName());
        session.setAttribute("searchSex", customerForm.getSex());
        session.setAttribute("searchBirthdayFrom", customerForm.getBirthdayFrom());
        session.setAttribute("searchBirthdayTo", customerForm.getBirthdayTo());
        
        // Xử lý phân trang
        int totalRecords = customerDAO.getTotalRecords(
            customerForm.getCustomerName(), 
            customerForm.getSex(), 
            customerForm.getBirthdayFrom(), 
            customerForm.getBirthdayTo()
        );
        
        int maxPage = customerDAO.getMaxPage(totalRecords);
        
        // Kiểm tra và điều chỉnh trang hiện tại
        int currentPage = customerForm.getCurrentPage();
        if (currentPage < 1) currentPage = 1;
        if (currentPage > maxPage) currentPage = maxPage;
        customerForm.setCurrentPage(currentPage);
        
        // Lấy danh sách khách hàng theo điều kiện tìm kiếm và phân trang
        List<CustomerDTO> customers = customerDAO.searchCustomers(
            customerForm.getCustomerName(),
            customerForm.getSex(),
            customerForm.getBirthdayFrom(),
            customerForm.getBirthdayTo(),
            customerForm.getCurrentPage()
        );
        customerForm.setSearchResults(customers);

        // Set thông tin vào request/session
        session.setAttribute("maxPage", maxPage);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("maxPage", maxPage);
        request.setAttribute("customerForm", customerForm);
        
        return mapping.findForward("success");
    }
} \\\\\\\\\\\\\\\\\\\\\\\\\\\\
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
    <html:form action="/search" method="post">
        <div class="search-form">
            <label>Tên khách hàng:</label>
            <html:text property="customerName"/>
            
            <label>Giới tính:</label>
            <html:select property="sex">
                <html:option value="-1">Tất cả</html:option>
                <html:option value="0">Nam</html:option>
                <html:option value="1">Nữ</html:option>
            </html:select>
            
            <label>Ngày sinh từ:</label>
            <html:text property="birthdayFrom" styleId="birthdayFrom"/>
            
            <label>Đến:</label>
            <html:text property="birthdayTo" styleId="birthdayTo"/>
            
            <html:submit value="Tìm kiếm"/>
        </div>

        <!-- Form phân trang -->
        <html:form action="/search" method="post">
            <html:hidden property="customerName"/>
            <html:hidden property="sex"/>
            <html:hidden property="birthdayFrom"/>
            <html:hidden property="birthdayTo"/>
            
            <div style="text-align: center; margin-top: 20px;">
                <button type="submit" name="page" value="1" ${customerForm.currentPage == 1 ? 'disabled' : ''}>&lt;&lt;</button>
                <button type="submit" name="page" value="${customerForm.currentPage - 1}" ${customerForm.currentPage == 1 ? 'disabled' : ''}>&lt;</button>
                
                <span>Trang ${customerForm.currentPage} / ${maxPage}</span>
                
                <button type="submit" name="page" value="${customerForm.currentPage + 1}" ${customerForm.currentPage == maxPage ? 'disabled' : ''}>&gt;</button>
                <button type="submit" name="page" value="${maxPage}" ${customerForm.currentPage == maxPage ? 'disabled' : ''}>&gt;&gt;</button>
            </div>
        </html:form>
    </html:form>

    <!-- Form xử lý delete -->
    <html:form action="/search" method="post" styleId="customerForm">
        <html:hidden property="action" value="delete"/>
        <html:hidden property="customerName"/>
        <html:hidden property="sex"/>
        <html:hidden property="birthdayFrom"/>
        <html:hidden property="birthdayTo"/>
        <html:hidden property="currentPage"/>
        
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
            <logic:present name="customerForm" property="searchResults">
                <logic:iterate id="customer" name="customerForm" property="searchResults">
                    <tr>
                        <td><html:checkbox property="customerIds" value="${customer.id}"/></td>
                        <td>
                            <span class="triangle">▶</span>
                            <html:link action="/customer" paramId="id" paramName="customer" paramProperty="id">
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
            <logic:notPresent name="customerForm" property="searchResults">
                <tr>
                    <td colspan="6" style="text-align: center;">Không có dữ liệu</td>
                </tr>
            </logic:notPresent>
        </table>

        <!-- Các nút chức năng -->
        <div style="text-align: right; margin: 20px 10% 0 0;">
            <html:button property="" onclick="window.location.href='customer.do?action=add'" styleClass="button">Add New</html:button>
            <html:button property="" onclick="deleteSelected()" styleId="deleteButton" disabled="${empty customerForm.searchResults}">Delete</html:button>
        </div>
    </html:form>
</body>
</html>
