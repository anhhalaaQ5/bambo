package factory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import form.CustomerForm;
import form.LoginForm;
import dao.CustomerDAO;
import dto.CustomerDTO;
import java.util.List;

public class CustomerFactory {
    private final CustomerDAO customerDAO;
    
    public CustomerFactory() {
        this.customerDAO = new CustomerDAO();
    }

    public void processSearch(CustomerForm customerForm, HttpSession session) {
        session.setAttribute("searchName", customerForm.getCustomerName());
        session.setAttribute("searchSex", customerForm.getSex());
        session.setAttribute("searchBirthdayFrom", customerForm.getBirthdayFrom());
        session.setAttribute("searchBirthdayTo", customerForm.getBirthdayTo());
        customerForm.setCurrentPage(1);
    }

    public void loadSearchConditions(CustomerForm customerForm, HttpSession session) {
        customerForm.setCustomerName((String)session.getAttribute("searchName"));
        customerForm.setSex((String)session.getAttribute("searchSex"));
        customerForm.setBirthdayFrom((String)session.getAttribute("searchBirthdayFrom"));
        customerForm.setBirthdayTo((String)session.getAttribute("searchBirthdayTo"));
    }

    public void processDelete(String[] customerIds) {
        if (customerIds != null && customerIds.length > 0) {
            try {
                customerDAO.deleteCustomers(customerIds);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void processPagination(CustomerForm customerForm, HttpSession session, HttpServletRequest request) {
        Boolean returnFromEdit = (Boolean) session.getAttribute("returnFromEdit");
        if (returnFromEdit != null && returnFromEdit) {
            Integer savedPage = (Integer) session.getAttribute("currentPage");
            if (savedPage != null) {
                customerForm.setCurrentPage(savedPage);
            }
            session.removeAttribute("returnFromEdit");
        } else {
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
    }

    public void loadCustomerData(CustomerForm customerForm, HttpSession session, HttpServletRequest request) {
        // Xử lý phân trang và lấy dữ liệu
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
        
        // Lấy danh sách khách hàng
        List<CustomerDTO> customers = customerDAO.searchCustomers(
            customerForm.getCustomerName(),
            customerForm.getSex(),
            customerForm.getBirthdayFrom(),
            customerForm.getBirthdayTo(),
            currentPage
        );
        customerForm.setSearchResults(customers);

        // Set thông tin vào request/session
        session.setAttribute("maxPage", maxPage);
        session.setAttribute("currentPage", currentPage);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("maxPage", maxPage);
        request.setAttribute("customerForm", customerForm);
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
import factory.CustomerFactory;

public class CustomerAction extends Action {
    private final CustomerFactory customerFactory;

    public CustomerAction() {
        this.customerFactory = new CustomerFactory();
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

        // Xử lý các action
        if ("search".equals(action)) {
            customerFactory.processSearch(customerForm, session);
        } 
        else if ("pagination".equals(action)) {
            customerFactory.loadSearchConditions(customerForm, session);
        }
        else {
            customerFactory.loadSearchConditions(customerForm, session);
        }

        // Xử lý delete nếu cần
        if ("delete".equals(action)) {
            customerFactory.processDelete(customerForm.getCustomerIds());
        }

        // Xử lý phân trang
        customerFactory.processPagination(customerForm, session, request);

        // Load dữ liệu khách hàng
        customerFactory.loadCustomerData(customerForm, session, request);
        
        return mapping.findForward("success");
    }
}
\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
package factory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import form.CustomerAddForm;
import form.LoginForm;
import dto.CustomerDTO;
import dao.CustomerDAO;

public class CustomerAddFactory {
    private final CustomerDAO customerDAO;
    
    public CustomerAddFactory() {
        this.customerDAO = new CustomerDAO();
    }

    public void resetForm(CustomerAddForm customerForm, HttpServletRequest request) {
        customerForm.reset(null, request);
    }

    public CustomerDTO processEdit(String customerId) {
        if (customerId == null || customerId.isEmpty()) {
            return null;
        }
        return customerDAO.getCustomerById(Integer.parseInt(customerId));
    }

    public void setEditFormData(CustomerAddForm customerForm, CustomerDTO customer, HttpServletRequest request) {
        customerForm.setCustomerId(String.valueOf(customer.getId()));
        customerForm.setCustomerName(customer.getName());
        customerForm.setSex(String.valueOf(customer.getSex()));
        customerForm.setBirthday(customer.getBirthday());
        customerForm.setEmail(customer.getEmail());
        customerForm.setAddress(customer.getAddress());
        
        request.setAttribute("customer", customer);
        request.setAttribute("isEdit", true);
    }

    public void processSave(CustomerAddForm customerForm, LoginForm user, HttpSession session) throws Exception {
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
            
            // Đánh dấu là quay về từ edit để giữ trang
            session.setAttribute("returnFromEdit", true);
        } else {
            customerDAO.addCustomer(
                customerForm.getCustomerName(),
                customerForm.getSex(),
                customerForm.getBirthday(),
                customerForm.getEmail(),
                customerForm.getAddress(),
                user.getPsn()
            );
        }
    }
}\\\\\\\\\\\\\\\\\\\\\\\
package action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts.action.*;
import form.LoginForm;
import form.CustomerAddForm;
import dto.CustomerDTO;
import factory.CustomerAddFactory;

public class CustomerAddAction extends Action {
    private final CustomerAddFactory customerAddFactory;

    public CustomerAddAction() {
        this.customerAddFactory = new CustomerAddFactory();
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
            customerAddFactory.resetForm(customerForm, request);
            return mapping.findForward("add");
        } 
        else if ("edit".equals(action)) {
            String customerId = request.getParameter("id");
            if (customerId == null || customerId.isEmpty()) {
                return mapping.findForward("search");
            }
            
            CustomerDTO customer = customerAddFactory.processEdit(customerId);
            if (customer != null) {
                customerAddFactory.setEditFormData(customerForm, customer, request);
            }
            return mapping.findForward("edit");
        }
        // Xử lý lưu dữ liệu
        else if ("save".equals(action)) {
            try {
                customerAddFactory.processSave(customerForm, user, session);
                response.sendRedirect("search.do");
                return null;
            } catch (Exception e) {
                request.setAttribute("error", e.getMessage());
                return mapping.findForward("add");
            }
        }

        return mapping.findForward("search");
    }
}
