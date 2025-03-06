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

Cập nhật web.xml:
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee 
                             http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">

    <!-- Spring Context Loader -->
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>
    
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>
            /WEB-INF/applicationContext.xml
            /WEB-INF/hibernateContext.xml
        </param-value>
    </context-param>

    <!-- Struts Configuration -->
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

    <!-- JSP Config -->
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
</web-app>\\\\\\\\\\\\\\\\\\\\\<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee 
                             http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">

    <!-- Spring Context Loader -->
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>
    
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>
            /WEB-INF/applicationContext.xml
            /WEB-INF/hibernateContext.xml
        </param-value>
    </context-param>

    <!-- Struts Configuration -->
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

    <!-- JSP Config -->
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

    <welcome-file-list>
        <welcome-file>index.jsp</welcome-file>
    </welcome-file-list>
</web-app>
\\\\\\\\\\\\\\\\\\\\\\\\\\\
Tạo file applicationContext.xml:
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN//EN"
    "http://www.springframework.org/dtd/spring-beans.dtd">

<beans>
    <!-- DAO Beans -->
    <bean id="userDAO" class="dao.UserDAO">
        <property name="sessionFactory" ref="sessionFactory"/>
    </bean>

    <!-- Factory Beans -->
    <bean id="loginFactory" class="factory.LoginFactory">
        <property name="userDAO" ref="userDAO"/>
    </bean>

    <!-- Action Beans -->
    <bean id="loginAction" class="action.LoginAction">
        <property name="loginFactory" ref="loginFactory"/>
    </bean>
</beans>
\\\\\\\\\\\\\\\\\\\\\\\\Tạo file hibernateContext.xml:
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN//EN"
    "http://www.springframework.org/dtd/spring-beans.dtd">

<beans>
    <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName" value="com.microsoft.sqlserver.jdbc.SQLServerDriver"/>
        <property name="url" value="jdbc:sqlserver://MOI:1433;databaseName=CustomerSyste;encrypt=false;trustServerCertificate=true"/>
        <property name="username" value="sa"/>
        <property name="password" value="abcabc"/>
    </bean>

    <bean id="sessionFactory" class="org.springframework.orm.hibernate3.LocalSessionFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <property name="mappingResources">
            <list>
                <value>hibernate/User.hbm.xml</value>
            </list>
        </property>
        <property name="hibernateProperties">
            <props>
                <prop key="hibernate.dialect">org.hibernate.dialect.SQLServerDialect</prop>
                <prop key="hibernate.show_sql">true</prop>
            </props>
        </property>
    </bean>

    <bean id="transactionManager" class="org.springframework.orm.hibernate3.HibernateTransactionManager">
        <property name="sessionFactory" ref="sessionFactory"/>
    </bean>
</beans>\\\\\\\\\\\\\\\\\\\
Cập nhật struts-config.xml:
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE struts-config PUBLIC
        "-//Apache Software Foundation//DTD Struts Configuration 1.2//EN"
        "http://struts.apache.org/dtds/struts-config_1_2.dtd">

<struts-config>
    <form-beans>
        <form-bean name="loginForm" type="form.LoginForm"/>
    </form-beans>

    <action-mappings>
        <action path="/login"
                type="org.springframework.web.struts.DelegatingActionProxy"
                name="loginForm"
                scope="request"
                input="/login.jsp">
            <forward name="success" path="/search.do"/>
            <forward name="failure" path="/login.jsp"/>
        </action>
    </action-mappings>

    <message-resources parameter="resources.ApplicationResources"/>

    <plug-in className="org.springframework.web.struts.ContextLoaderPlugIn"/>
</struts-config>\\\\\\\\\\\\\\\\\\\
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE struts-config PUBLIC
        "-//Apache Software Foundation//DTD Struts Configuration 1.2//EN"
        "http://struts.apache.org/dtds/struts-config_1_2.dtd">

<struts-config>
    <form-beans>
        <form-bean name="loginForm" type="form.LoginForm"/>
        <form-bean name="customerForm" type="form.CustomerForm"/>
        <form-bean name="customerAddForm" type="form.CustomerAddForm"/>
    </form-beans>

    <action-mappings>
        <action path="/login"
                type="org.springframework.web.struts.DelegatingActionProxy"
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

        <action path="/add"
                type="action.CustomerAddAction"
                name="customerAddForm"
                scope="request">
            <forward name="add" path="/WEB-INF/views/customerAdd.jsp"/>
            <forward name="edit" path="/WEB-INF/views/customerAdd.jsp"/>
            <forward name="search" path="/search.do"/>
            <forward name="login" path="/login.do"/>
        </action>
    </action-mappings>

    <global-forwards>
        <forward name="welcome" path="/login.do"/>
    </global-forwards>

    <message-resources parameter="resources.ApplicationResources"/>

    <plug-in className="org.springframework.web.struts.ContextLoaderPlugIn"/>
</struts-config>\\\\\\\\\\\\\\\\\\
Cập nhật các class Java:
package dao;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import form.LoginForm;
import java.util.List;

public class UserDAO extends HibernateDaoSupport {
    
    @SuppressWarnings("unchecked")
    public List<LoginForm> validateAndGetUser(String userid, String pass) {
        String hql = "FROM User WHERE userid = ? AND pass = ? AND deleteYmd IS NULL";
        return getHibernateTemplate().find(hql, new Object[]{userid, pass});
    }
}\\\\\\\\\\\\\\\
package dao;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import form.LoginForm;
import java.util.List;

public class UserDAO extends HibernateDaoSupport {
    
    @SuppressWarnings("unchecked")
    public List<LoginForm> validateAndGetUser(String userid, String pass) {
        String hql = "FROM User WHERE userid = ? AND pass = ? AND deleteYmd IS NULL";
        return getHibernateTemplate().find(hql, new Object[]{userid, pass});
    }

    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();
        List<LoginForm> users = userDAO.validateAndGetUser("user1", "password123");
        for (LoginForm user : users) {
            System.out.println(user.getUsername());
        }
    }
}
\\\\\\\\\\\\\\\\\\\package factory;

import dao.UserDAO;
import form.LoginForm;
import javax.servlet.http.HttpSession;
import java.util.List;

public class LoginFactory {
    private UserDAO userDAO;
    
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    
    // ... các phương thức khác giữ nguyên ...
}\\\\\\\\\\\\\\\\\\\\\
package factory;

import dao.UserDAO;
import form.LoginForm;
import javax.servlet.http.HttpSession;
import java.util.List;

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
} \\\\\\\\\\\\\\\\\\\\package action;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import factory.LoginFactory;
import form.LoginForm;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginAction extends Action {
    private LoginFactory loginFactory;
    
    public void setLoginFactory(LoginFactory loginFactory) {
        this.loginFactory = loginFactory;
    }
    
    // ... phần còn lại của class giữ nguyên ...
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

import factory.LoginFactory;
import form.LoginForm;

public class LoginAction extends Action {
    private LoginFactory loginFactory;
    
    public void setLoginFactory(LoginFactory loginFactory) {
        this.loginFactory = loginFactory;
    }

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

        // Xử lý login
        String errorMsg = loginFactory.processLogin(loginForm.getUserid(), loginForm.getPass());
        
        if (!errorMsg.isEmpty()) {
            loginForm.setErrorMessage(errorMsg);
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
}\\\\\\\\\\\\\\\\\\\\
Có, chúng ta cần tạo các file mapping Hibernate cho các DTO và sửa lại các DTO để phù hợp với Hibernate:package dto;

import java.util.Date;

public class UserDTO {
    private int psn;
    private String userid;
    private String pass;
    private String username;
    private Date deleteYmd;
    private Date insertYmd;
    private int insertPsncd;
    private Date updateYmd;
    private int updatePsncd;

    // Constructor mặc định cho Hibernate
    public UserDTO() {}

    public UserDTO(int psn, String userid, String pass, String username, 
                  Date deleteYmd, Date insertYmd, int insertPsncd, 
                  Date updateYmd, int updatePsncd) {
        this.psn = psn;
        this.userid = userid;
        this.pass = pass;
        this.username = username;
        this.deleteYmd = deleteYmd;
        this.insertYmd = insertYmd;
        this.insertPsncd = insertPsncd;
        this.updateYmd = updateYmd;
        this.updatePsncd = updatePsncd;
    }

    // Getters và Setters giữ nguyên
}\\\\\\\\\\\\\\\\\\
package dto;

import java.util.Date;

public class UserDTO {
    private int psn;
    private String userid;
    private String pass;
    private String username;
    private Date deleteYmd;
    private Date insertYmd;
    private int insertPsncd;
    private Date updateYmd;
    private int updatePsncd;

    // Constructor mặc định cho Hibernate
    public UserDTO() {}

    public UserDTO(int psn, String userid, String pass, String username, 
                  Date deleteYmd, Date insertYmd, int insertPsncd, 
                  Date updateYmd, int updatePsncd) {
        this.psn = psn;
        this.userid = userid;
        this.pass = pass;
        this.username = username;
        this.deleteYmd = deleteYmd;
        this.insertYmd = insertYmd;
        this.insertPsncd = insertPsncd;
        this.updateYmd = updateYmd;
        this.updatePsncd = updatePsncd;
    }

    public int getPsn() {
        return psn;
    }

    public String getUserid() {
        return userid;
    }

    public String getPass() {
        return pass;
    }

    public String getUsername() {
        return username;
    }

    public Date getDeleteYmd() {
        return deleteYmd;
    }

    public Date getInsertYmd() {
        return insertYmd;
    }

    public int getInsertPsncd() {
        return insertPsncd;
    }

    public Date getUpdateYmd() {
        return updateYmd;
    }

    public int getUpdatePsncd() {
        return updatePsncd;
    }

    public void setDeleteYmd(Date deleteYmd) {
        this.deleteYmd = deleteYmd;
    }

    public void setUpdateYmd(Date updateYmd) {
        this.updateYmd = updateYmd;
    }

    public void setUpdatePsncd(int updatePsncd) {
        this.updatePsncd = updatePsncd;
    }
}\\\\\\\\\\\\\\\\\\Tạo file mapping cho UserDTO:
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-mapping PUBLIC 
    "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
    "http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd">

<hibernate-mapping>
    <class name="dto.UserDTO" table="MSTUSER">
        <id name="psn" type="int">
            <column name="Psn" />
            <generator class="native" />
        </id>
        
        <property name="userid" type="string">
            <column name="Userid" length="50" not-null="true" unique="true" />
        </property>
        
        <property name="pass" type="string">
            <column name="Pass" length="255" not-null="true" />
        </property>
        
        <property name="username" type="string">
            <column name="Username" length="100" not-null="true" />
        </property>
        
        <property name="deleteYmd" type="date">
            <column name="Delete_ymd" />
        </property>
        
        <property name="insertYmd" type="date">
            <column name="Insert_ymd" not-null="true" />
        </property>
        
        <property name="insertPsncd" type="int">
            <column name="Insertpsncd" not-null="true" />
        </property>
        
        <property name="updateYmd" type="date">
            <column name="Updateymd" not-null="true" />
        </property>
        
        <property name="updatePsncd" type="int">
            <column name="Updatepsncd" not-null="true" />
        </property>
    </class>
</hibernate-mapping>\\\\\\\\\\\\\\\\\\
Cập nhật hibernateContext.xml để thêm mapping cho Customer:
<bean id="sessionFactory" class="org.springframework.orm.hibernate3.LocalSessionFactoryBean">
    <property name="dataSource" ref="dataSource"/>
    <property name="mappingResources">
        <list>
            <value>hibernate/User.hbm.xml</value>
            <value>hibernate/Customer.hbm.xml</value>
        </list>
    </property>
    <!-- ... các cấu hình khác ... -->
</bean>\\\\\\\\\\\\\
Nếu ID không tự động tăng, chúng ta sẽ bỏ generator và để người dùng tự quản lý ID. Sửa lại mapping file như sau:
