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
} \\\\\\\\\\\\\\\\\\\\\\\\\\\\
package form;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;

import dto.CustomerDTO;

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
    private int maxPage = 1;
    
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
    
    // Thêm getter/setter cho maxPage
    public int getMaxPage() { return maxPage; }
    public void setMaxPage(int maxPage) { this.maxPage = maxPage; }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        String action = getAction();
        
        // Chỉ validate khi action là search
        if ("search".equals(action)) {
            String birthdayFrom = getBirthdayFrom();
            String birthdayTo = getBirthdayTo();
            
            // Validate birthdayFrom nếu có giá trị
            if (birthdayFrom != null && !birthdayFrom.trim().isEmpty()) {
                if (!isValidDate(birthdayFrom)) {
                    errors.add("alert", new ActionMessage("error.date.from.format"));
                    return errors;
                }
            }
            
            // Validate birthdayTo nếu có giá trị
            if (birthdayTo != null && !birthdayTo.trim().isEmpty()) {
                if (!isValidDate(birthdayTo)) {
                    errors.add("alert", new ActionMessage("error.date.to.format"));
                    return errors;
                }
            }
            
            // Validate khoảng thời gian nếu cả hai trường đều có giá trị
            if (birthdayFrom != null && !birthdayFrom.trim().isEmpty() 
                && birthdayTo != null && !birthdayTo.trim().isEmpty()) {
                if (isValidDate(birthdayFrom) && isValidDate(birthdayTo)) {
                    if (!isValidDateRange(birthdayFrom, birthdayTo)) {
                        errors.add("alert", new ActionMessage("error.date.range"));
                        return errors;
                    }
                }
            }
        }
        
        return errors;
    }

    private boolean isValidDate(String dateStr) {
        // Kiểm tra định dạng YYYY/MM/DD
        if (!dateStr.matches("^\\d{4}/\\d{2}/\\d{2}$")) {
            return false;
        }

        String[] parts = dateStr.split("/");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);

        // Kiểm tra năm
        if (year < 1 || year > 9999) {
            return false;
        }

        // Kiểm tra tháng
        if (month < 1 || month > 12) {
            return false;
        }

        // Kiểm tra ngày
        int[] daysInMonth = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) {
            daysInMonth[2] = 29;
        }
        
        return day >= 1 && day <= daysInMonth[month];
    }

    private boolean isValidDateRange(String fromDate, String toDate) {
        String[] fromParts = fromDate.split("/");
        String[] toParts = toDate.split("/");
        
        int fromYear = Integer.parseInt(fromParts[0]);
        int fromMonth = Integer.parseInt(fromParts[1]);
        int fromDay = Integer.parseInt(fromParts[2]);
        
        int toYear = Integer.parseInt(toParts[0]);
        int toMonth = Integer.parseInt(toParts[1]);
        int toDay = Integer.parseInt(toParts[2]);
        
        if (fromYear > toYear) return false;
        if (fromYear == toYear && fromMonth > toMonth) return false;
        if (fromYear == toYear && fromMonth == toMonth && fromDay > toDay) return false;
        
        return true;
    }
}\\\\\\\\\\\\\\\\\\\\\\\\\\\
package form;

import org.apache.struts.action.ActionForm;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;

public class CustomerAddForm extends ActionForm {
    private String customerId;
    private String customerName;
    private String sex;
    private String birthday;
    private String email;
    private String address;

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }
    
    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        customerId = null;
        customerName = "";
        sex = "-1";
        birthday = "";
        email = "";
        address = "";
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        String action = request.getParameter("action");
        
        // Chỉ validate khi action là save
        if ("save".equals(action)) {
            // Validate customerName
            if (customerName == null || customerName.trim().isEmpty()) {
                errors.add("error", new ActionMessage("error.customerName.required"));
                return errors;
            }
            
            // Validate sex
            if (sex == null || sex.equals("-1")) {
                errors.add("error", new ActionMessage("error.sex.required"));
                return errors;
            }
            
            // Validate birthday
            if (birthday == null || birthday.trim().isEmpty()) {
                errors.add("error", new ActionMessage("error.birthday.required"));
                return errors;
            } else if (!isValidDate(birthday)) {
                errors.add("error", new ActionMessage("error.birthday.format"));
                return errors;
            }
            
            // Validate email
            if (email == null || email.trim().isEmpty()) {
                errors.add("error", new ActionMessage("error.email.required"));
                return errors;
            } else if (!isValidEmail(email)) {
                errors.add("error", new ActionMessage("error.email.format"));
                return errors;
            }
        }
        
        return errors;
    }

    private boolean isValidDate(String dateStr) {
        // Kiểm tra định dạng YYYY/MM/DD
        if (!dateStr.matches("^\\d{4}/\\d{2}/\\d{2}$")) {
            return false;
        }

        String[] parts = dateStr.split("/");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);

        // Kiểm tra năm
        if (year < 1 || year > 9999) {
            return false;
        }

        // Kiểm tra tháng
        if (month < 1 || month > 12) {
            return false;
        }

        // Kiểm tra ngày
        int[] daysInMonth = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) {
            daysInMonth[2] = 29;
        }
        return day >= 1 && day <= daysInMonth[month];
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
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
} \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
\package action;

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
}\\\\\\\\\\\\\\\\\\
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
    private CustomerDAO customerDAO;

    public void setCustomerDAO(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        HttpSession session = request.getSession();
        LoginForm user = (LoginForm) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect("login.do");
            return null;
        }

        CustomerForm customerForm = (CustomerForm) form;
        String action = customerForm.getAction();

        // Kiểm tra lỗi validate
        ActionErrors errors = customerForm.validate(mapping, request);
        if (!errors.isEmpty()) {
            saveErrors(request, errors);
                    return mapping.findForward("success");

        }

        // Xử lý action search
        if ("search".equals(action)) {
            // Lưu điều kiện tìm kiếm vào session
            session.setAttribute("searchName", customerForm.getCustomerName());
            session.setAttribute("searchSex", customerForm.getSex());
            session.setAttribute("searchBirthdayFrom", customerForm.getBirthdayFrom());
            session.setAttribute("searchBirthdayTo", customerForm.getBirthdayTo());
            customerForm.setCurrentPage(1); // Reset về trang 1 khi tìm kiếm mới
        } 
        // Xử lý action pagination
        else if ("pagination".equals(action)) {
            // Lấy điều kiện tìm kiếm từ session
            customerForm.setCustomerName((String)session.getAttribute("searchName"));
            customerForm.setSex((String)session.getAttribute("searchSex"));
            customerForm.setBirthdayFrom((String)session.getAttribute("searchBirthdayFrom"));
            customerForm.setBirthdayTo((String)session.getAttribute("searchBirthdayTo"));
        }
        // Các action khác
        else {
            // Lấy điều kiện tìm kiếm từ session như cũ
            customerForm.setCustomerName((String)session.getAttribute("searchName"));
            customerForm.setSex((String)session.getAttribute("searchSex"));
            customerForm.setBirthdayFrom((String)session.getAttribute("searchBirthdayFrom"));
            customerForm.setBirthdayTo((String)session.getAttribute("searchBirthdayTo"));
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

        // Xử lý phân trang
        Boolean returnFromEdit = (Boolean) session.getAttribute("returnFromEdit");
        if (returnFromEdit != null && returnFromEdit) {
            // Nếu quay về từ edit, lấy trang từ session
            Integer savedPage = (Integer) session.getAttribute("currentPage");
            if (savedPage != null) {
                customerForm.setCurrentPage(savedPage);
            }
            // Xóa flag sau khi sử dụng
            session.removeAttribute("returnFromEdit");
        } else {
            // Xử lý page từ form submit như bình thường
            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.isEmpty()) {
                try {
                    int page = Integer.parseInt(pageParam);
                    customerForm.setCurrentPage(page);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        // Lưu trang hiện tại vào session
        session.setAttribute("currentPage", customerForm.getCurrentPage());

        // Xử lý phân trang và lấy dữ liệu
        int totalRecords = customerDAO.getTotalRecords(
            customerForm.getCustomerName(), 
            customerForm.getSex(), 
            customerForm.getBirthdayFrom(), 
            customerForm.getBirthdayTo()
        );
        
        int maxPage = customerDAO.getMaxPage(totalRecords);
        customerForm.setMaxPage(maxPage); // Set maxPage vào form
        
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
            currentPage
        );
        customerForm.setSearchResults(customers);

        return mapping.findForward("success");
    }
}\\\\\\\\\\\\\\\\\\\\\
package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.*;
import form.LoginForm;
import form.CustomerAddForm;
import dto.CustomerDTO;
import dao.CustomerDAO;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CustomerAddAction extends Action {
    private CustomerDAO customerDAO;

    // Thêm setter cho Spring DI
    public void setCustomerDAO(CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        HttpSession session = request.getSession();
        LoginForm user = (LoginForm) session.getAttribute("user");
        
        if (user == null) {
            return mapping.findForward("login");
        }

        CustomerAddForm customerForm = (CustomerAddForm) form;
        String action = request.getParameter("action");

        // Xử lý hiển thị form add/edit
        if ("add".equals(action)) {
            customerForm.reset(mapping, request);
            return mapping.findForward("add");
        } 
        else if ("edit".equals(action)) {
            String customerId = request.getParameter("id");
            if (customerId == null || customerId.isEmpty()) {
                return mapping.findForward("search");
            }
            
            CustomerDTO customer = getCustomerById(customerId);
            if (customer != null) {
                customerForm.setCustomerId(String.valueOf(customer.getId()));
                customerForm.setCustomerName(customer.getName());
                customerForm.setSex(customer.getSex());
                customerForm.setBirthday(customer.getBirthday());
                customerForm.setEmail(customer.getEmail());
                customerForm.setAddress(customer.getAddress());
                
                request.setAttribute("customer", customer);
                request.setAttribute("isEdit", true);
            }
            return mapping.findForward("edit");
        }
        // Xử lý lưu dữ liệu
        else if ("save".equals(action)) {
            try {
                boolean isEdit = customerForm.getCustomerId() != null && !customerForm.getCustomerId().isEmpty();
                
                if (isEdit) {
                    customerDAO.updateCustomer(
                        Integer.parseInt(customerForm.getCustomerId()),
                        customerForm.getCustomerName(),
                        customerForm.getSex(),
                        customerForm.getBirthday(),
                        customerForm.getEmail(),
                        customerForm.getAddress(),
                        user.getPsn()
                    );
                    
                    // Khi edit, lấy trang hiện tại từ session và redirect về trang đó
                    Integer currentPage = (Integer) session.getAttribute("currentPage");
                    if (currentPage != null) {
                        // Lưu action vào session để CustomerAction biết không phải reset về trang 1
                        session.setAttribute("returnFromEdit", true);
                        response.sendRedirect("search.do");
                    } else {
                        response.sendRedirect("search.do");
                    }
                } else {
                    // Khi thêm mới
                    customerDAO.addCustomer(
                        customerForm.getCustomerName(),
                        customerForm.getSex(),
                        customerForm.getBirthday(),
                        customerForm.getEmail(),
                        customerForm.getAddress(),
                        user.getPsn()
                    );
                    // Khi add new, chỉ quay về search không có tham số
                    response.sendRedirect("search.do");
                }
                return null;
            } catch (Exception e) {
                request.setAttribute("error", e.getMessage());
                return mapping.findForward("add");
            }
        }

        return mapping.findForward("search");
    }

    private CustomerDTO getCustomerById(String customerId) {
        if (customerId == null || customerId.isEmpty()) {
            return null;
        }
        return customerDAO.getCustomerById(Integer.parseInt(customerId));
    }
} \\\\\\\\\\\\\\\\\\\\\
error.userid.required=vui lòng nhập id
error.password.required=うわーん
error.login={0}
error.login.invalid=Sai thông tin đăng nhập 
error.date.format=Ngày sinh không hợp lệ. Vui lòng nhập theo định dạng YYYY/MM/DD và đảm bảo ngày tháng hợp lệ
error.date.range=Khoảng thời gian không hợp lệ (Từ ngày phải nhỏ hơn hoặc bằng Đến ngày)
error.date.from.format=Ngày sinh từ không hợp lệ. Vui lòng nhập theo định dạng YYYY/MM/DD và đảm bảo ngày tháng hợp lệ
error.date.to.format=Ngày sinh đến không hợp lệ. Vui lòng nhập theo định dạng YYYY/MM/DD và đảm bảo ngày tháng hợp lệ
error.customerName.required=Vui lòng nhập tên khách hàng
error.sex.required=Vui lòng chọn giới tính
error.birthday.required=Vui lòng nhập ngày sinh
error.birthday.format=Ngày sinh không hợp lệ. Vui lòng nhập theo định dạng YYYY/MM/DD
error.email.required=Vui lòng nhập email
error.email.format=Email không đúng định dạng \\\\\\\\\\\\\\\\\\\\
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE struts-config PUBLIC
        "-//Apache Software Foundation//DTD Struts Configuration 1.3//EN"
        "http://struts.apache.org/dtds/struts-config_1_3.dtd">

<struts-config>
    <!-- Form Beans Configuration -->
    <form-beans>
        <form-bean name="loginForm" type="form.LoginForm"/>
        <form-bean name="customerForm" type="form.CustomerForm"/>
        <form-bean name="customerAddForm" type="form.CustomerAddForm"/>
    </form-beans>

    <!-- Global Forwards Configuration -->
    <global-forwards>
        <forward name="welcome" path="/login.do"/>
    </global-forwards>

    <!-- Action Mappings Configuration -->
    <action-mappings>
        <action path="/login"
                type="org.springframework.web.struts.DelegatingActionProxy"
                name="loginForm"
                scope="request"
                validate="false"
                input="/login.jsp">
            <forward name="success" path="/search.do"/>
            <forward name="failure" path="/login.jsp"/>
        </action>

        <action path="/search"
                type="org.springframework.web.struts.DelegatingActionProxy"
                name="customerForm"
                scope="request"
                validate="true"
                input="/WEB-INF/views/search.jsp">
            <forward name="success" path="/WEB-INF/views/search.jsp"/>
            <forward name="failure" path="/login.jsp"/>
        </action>

        <action path="/add"
                type="org.springframework.web.struts.DelegatingActionProxy"
                name="customerAddForm"
                scope="request"
                validate="true"
                input="/WEB-INF/views/customerAdd.jsp">
            <forward name="add" path="/WEB-INF/views/customerAdd.jsp"/>
            <forward name="edit" path="/WEB-INF/views/customerAdd.jsp"/>
            <forward name="search" path="/search.do"/>
            <forward name="login" path="/login.do"/>
        </action>
    </action-mappings>

    <!-- Message Resources Configuration -->
    <message-resources parameter="resources.ApplicationResources" 
        null="false" 
        escape="false"
        encoding="UTF-8"/>

    <!-- Plugins Configuration -->
    <plug-in className="org.springframework.web.struts.ContextLoaderPlugIn">
        <set-property property="contextConfigLocation"
                     value="/WEB-INF/applicationContext.xml"/>
    </plug-in>

</struts-config>\\\\\\\\\\\\\\\\\\\\\\\\\\\
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee 
                             http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">

    <!-- Thêm vào phần đầu của web.xml -->
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>/WEB-INF/applicationContext.xml</param-value>
    </context-param>

    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>

    <!-- Struts Tag Library Descriptors -->
    <jsp-config>
        <taglib>
            <taglib-uri>http://struts.apache.org/tags-bean</taglib-uri>
            <taglib-location>/WEB-INF/struts-bean.tld</taglib-location>
        </taglib>
        <taglib>
            <taglib-uri>http://struts.apache.org/tags-html</taglib-uri>
            <taglib-location>/WEB-INF/struts-html.tld</taglib-location>
        </taglib>
        <taglib>
            <taglib-uri>http://struts.apache.org/tags-logic</taglib-uri>
            <taglib-location>/WEB-INF/struts-logic.tld</taglib-location>
        </taglib>
    </jsp-config>

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

    <welcome-file-list>
        <welcome-file>index.jsp</welcome-file>
    </welcome-file-list>
</web-app>
\\\\\\\\\\\\\\\\\\\\\\\\\\\
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta charset="UTF-8">
    <%
        response.setHeader("Content-Language", "ja");
        response.setCharacterEncoding("UTF-8");
    %>
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
        
        <div id="errorLabel" class="error-label">
            <logic:messagesPresent property="login">
                <html:messages id="error" property="login">
                    <bean:write name="error"/><br/>
                </html:messages>
            </logic:messagesPresent>
            <logic:messagesNotPresent property="login">
                &nbsp;
            </logic:messagesNotPresent>
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
</html>\\\\\\\\\\\\\\\\\\\\\\\\\
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
    <script type="text/javascript">
        window.onload = function() {
            <html:messages id="msg" property="alert">
                alert("<bean:write name="msg"/>");
            </html:messages>
        }
    </script>
</head>
<body>
    <div class="user-info">
        <bean:define id="user" name="user" scope="session"/>
        <label>Chào, <bean:write name="user" property="username"/></label> | 
        <html:link action="/login?action=logout">Đăng xuất</html:link>
    </div>

    <!-- Form tìm kiếm -->
    <html:form action="/search" method="post" styleId="searchForm">
        <html:hidden property="action" value="search"/>
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
    </html:form>

    <!-- Form phân trang -->
    <html:form action="/search" method="post" styleId="paginationForm">
        <html:hidden property="action" value="pagination"/>
        <html:hidden property="customerName"/>
        <html:hidden property="sex"/>
        <html:hidden property="birthdayFrom"/>
        <html:hidden property="birthdayTo"/>
        <html:hidden property="currentPage" styleId="currentPage"/>
        
        <div style="text-align: center; margin-top: 20px;">
            <bean:define id="currentPage">
                <bean:write name="customerForm" property="currentPage"/>
            </bean:define>
            <html:button property="firstPage" styleId="firstPage" 
                         onclick="goToPage(1)" 
                         disabled='<%= "1".equals(pageContext.getAttribute("currentPage")) %>'>
                First
            </html:button>

            <html:button property="prevPage" styleId="prevPage"
                         onclick='<%= "goToPage(" + (Integer.parseInt((String)pageContext.getAttribute("currentPage")) - 1) + ")" %>'
                         disabled='<%= "1".equals(pageContext.getAttribute("currentPage")) %>'>
                Previous
            </html:button>
            
            <span>
                Trang <bean:write name="customerForm" property="currentPage"/> / 
                <bean:write name="customerForm" property="maxPage"/>
            </span>
            
            <bean:define id="maxPage">
                <bean:write name="customerForm" property="maxPage"/>
            </bean:define>
            <html:button property="nextPage" styleId="nextPage"
                         onclick='<%= "goToPage(" + (Integer.parseInt((String)pageContext.getAttribute("currentPage")) + 1) + ")" %>'
                         disabled='<%= pageContext.getAttribute("currentPage").equals(pageContext.getAttribute("maxPage")) %>'>
                Next
            </html:button>

            <html:button property="lastPage" styleId="lastPage"
                         onclick='<%= "goToPage(" + pageContext.getAttribute("maxPage") + ")" %>'
                         disabled='<%= pageContext.getAttribute("currentPage").equals(pageContext.getAttribute("maxPage")) %>'>
                Last
            </html:button>
        </div>
    </html:form>

    <script type="text/javascript">
    function goToPage(pageNumber) {
        document.getElementById('currentPage').value = pageNumber;
        document.getElementById('paginationForm').submit();
    }
    </script>

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
                            <html:link action="/add?action=edit" paramId="id" paramName="customer" paramProperty="id">
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
            <html:link action="/add?action=add" styleClass="button">Add New</html:link>
            <html:button property="" onclick="deleteSelected()" styleId="deleteButton" disabled="${empty customerForm.searchResults}">Delete</html:button>
        </div>
    </html:form>
</body>
</html>\\\\\\\\\\\\\\\\\\\\\\\\\\\\
 <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
    <%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
    <%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
    <%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="UTF-8">
        <title>Thêm mới khách hàng</title>
        <style>
            .container {
                width: 600px;
                margin: 20px auto;
                padding: 20px;
                border: 1px solid #ddd;
            }
            .form-group {
                margin-bottom: 15px;
            }
            label {
                display: inline-block;
                width: 120px;
                margin-right: 10px;
            }
            input[type="text"], select, textarea {
                width: 300px;
                padding: 5px;
            }
            .buttons {
                margin-top: 20px;
                text-align: center;
            }
            button {
                padding: 5px 20px;
                margin: 0 10px;
            }
            .error {
                color: red;
                margin-bottom: 10px;
            }
            .user-info {
                text-align: right;
                margin-bottom: 20px;
                padding-right: 20px;
            }
        </style>
        <script>
            function isValidDate(dateStr) {
                // Kiểm tra định dạng YYYY/MM/DD
                var dateFormat = /^\d{4}\/\d{2}\/\d{2}$/;
                if (!dateFormat.test(dateStr)) {
                    return false;
                }

                // Tách ngày tháng năm
                var parts = dateStr.split('/');
                var year = parseInt(parts[0], 10);
                var month = parseInt(parts[1], 10);
                var day = parseInt(parts[2], 10);

                // Kiểm tra năm hợp lệ (từ năm 1 đến năm hiện tại)
                var currentYear = new Date().getFullYear();
                if (year < 1 || year > currentYear) {
                    return false;
                }

                // Kiểm tra tháng
                if (month < 1 || month > 12) {
                    return false;
                }

                // Kiểm tra ngày trong tháng
                var daysInMonth = new Date(year, month, 0).getDate();
                if (day < 1 || day > daysInMonth) {
                    return false;
                }

                return true;
            }

            function validateForm() {
                var name = document.getElementById("customerName").value;
                var birthday = document.getElementById("birthday").value;
                var email = document.getElementById("email").value;
                var sex = document.getElementsByName("sex")[0].value;
                
                // Reset error message
                document.getElementById("errorMessage").innerHTML = "";
                
                // Kiểm tra giới tính
                if (sex === "-1" || sex === "" || (sex !== "0" && sex !== "1")) {
                    document.getElementById("errorMessage").innerHTML = "Vui lòng chọn giới tính (Nam hoặc Nữ)";
                    return false;
                }
                
                if (!birthday.trim()) {
                    document.getElementById("errorMessage").innerHTML = "Vui lòng nhập ngày sinh";
                    return false;
                }
                
                if (!isValidDate(birthday)) {
                    document.getElementById("errorMessage").innerHTML = "Birthday wrong";
                    return false;
                }
                
                if (!email.trim()) {
                    document.getElementById("errorMessage").innerHTML = "Vui lòng nhập email";
                    return false;
                }
                
                // Kiểm tra định dạng email
                var emailFormat = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
                if (!emailFormat.test(email)) {
                    document.getElementById("errorMessage").innerHTML = "Email không đúng định dạng";
                    return false;
                }
                
                return true;
            }
            
            function clearForm() {
                // Xóa giá trị các trường thông tin
                document.getElementById("customerName").value = "";
                document.getElementById("email").value = "";
                document.getElementById("birthday").value = "";
                document.getElementsByName("address")[0].value = "";
                
                // Reset giới tính về giá trị mặc định
                document.getElementsByName("sex")[0].value = "-1";
                
                // Focus vào trường đầu tiên
                document.getElementById("customerName").focus();
            }
        </script>
    </head>
    <body>
        <div class="user-info">
            <logic:present name="user">
                Chào, <bean:write name="user" property="username"/> | 
                <html:link action="/login?action=logout">Đăng xuất</html:link>
            </logic:present>
        </div>

        <div class="container">
            <h2>
                <logic:present name="isEdit">
                    Chỉnh sửa khách hàng
                </logic:present>
                <logic:notPresent name="isEdit">
                    Thêm mới khách hàng
                </logic:notPresent>
            </h2>
            
            <!-- Một label lỗi chung -->
            <div class="error">
                <html:errors property="error"/>
            </div>
            
            <html:form action="/add" method="post">
                <html:hidden property="action" value="save"/>
                <html:hidden property="customerId"/>
                
                <div class="form-group">
                    <label>Mã khách hàng:</label>
                    <label style="font-weight: normal;">
                        <logic:present name="customer">
                            <bean:write name="customer" property="id"/>
                        </logic:present>
                        <logic:notPresent name="customer">[Tự động]</logic:notPresent>
                    </label>
                </div>
                
                <div class="form-group">
                    <label>Tên khách hàng:</label>
                    <html:text property="customerName" styleId="customerName"/>
                </div>
                
                <div class="form-group">
                    <label>Giới tính:</label>
                    <html:select property="sex">
                        <html:option value="-1">Chọn giới tính</html:option>
                        <html:option value="0">Nam</html:option>
                        <html:option value="1">Nữ</html:option>
                    </html:select>
                </div>
                
                <div class="form-group">
                    <label>Ngày sinh:</label>
                    <html:text property="birthday" styleId="birthday"/>
                </div>
                
                <div class="form-group">
                    <label>Email:</label>
                    <html:text property="email" styleId="email"/>
                </div>
                
                <div class="form-group">
                    <label>Địa chỉ:</label>
                    <html:textarea property="address" rows="3"/>
                </div>
                
                <div class="buttons">
                    <html:submit value="Lưu"/>
                    <html:button property="" onclick="clearForm()" value="Xóa"/>
                </div>
            </html:form>
        </div>
    </body>
    </html>
