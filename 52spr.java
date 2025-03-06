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

    <welcome-file-list>
        <welcome-file>login.jsp</welcome-file>
    </welcome-file-list>
</web-app>

\\\\\\\\\\\\\\\\\\
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
    <bean name="/login" class="action.LoginAction">
        <property name="loginFactory" ref="loginFactory"/>
    </bean>
</beans> \\\\\\\\\\\\\\\\\\\\\\\\
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
                <value>dto/User.hbm.xml</value>
                <!-- <value>hibernate/Customer.hbm.xml</value> -->
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
</beans> \\\\\\\\\\\\\\\\\\\\\\\\\
<project xmlns="http://maven.apache.org/POM/4.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>Thuctap</groupId>
	<artifactId>Thuctap</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>war</packaging>

	<dependencies>
		<!-- Struts 1.2 dependencies -->
		<dependency>
			<groupId>org.apache.struts</groupId>
			<artifactId>struts-core</artifactId>
			<version>1.3.10</version>
		</dependency>
		<dependency>
			<groupId>org.apache.struts</groupId>
			<artifactId>struts-taglib</artifactId>
			<version>1.3.10</version>
		</dependency>
		<dependency>
			<groupId>org.apache.struts</groupId>
			<artifactId>struts-extras</artifactId>
			<version>1.3.10</version>
		</dependency>
		
		<!-- Spring Framework -->
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-core</artifactId>
			<version>2.5.6</version>
		</dependency>
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-web</artifactId>
			<version>2.5.6</version>
		</dependency>
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-orm</artifactId>
			<version>2.5.6</version>
		</dependency>
		
		<!-- Hibernate -->
		<dependency>
			<groupId>org.hibernate</groupId>
			<artifactId>hibernate-core</artifactId>
			<version>3.6.10.Final</version>
		</dependency>
		
		<!-- Database -->
		<dependency>
			<groupId>com.microsoft.sqlserver</groupId>
			<artifactId>mssql-jdbc</artifactId>
			<version>9.4.0.jre8</version>
		</dependency>
		
		<!-- Servlet & JSP -->
		<dependency>
			<groupId>javax.servlet</groupId>
			<artifactId>javax.servlet-api</artifactId>
			<version>3.1.0</version>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>javax.servlet</groupId>
			<artifactId>jstl</artifactId>
			<version>1.2</version>
		</dependency>
		
		<!-- Thêm JTA dependency -->
		<dependency>
			<groupId>javax.transaction</groupId>
			<artifactId>jta</artifactId>
			<version>1.1</version>
		</dependency>
		
		<!-- Thêm dependency cho connection pool -->
		<dependency>
			<groupId>commons-dbcp</groupId>
			<artifactId>commons-dbcp</artifactId>
			<version>1.4</version>
		</dependency>
		
		<!-- Thêm Spring JDBC -->
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-jdbc</artifactId>
			<version>2.5.6</version>
		</dependency>
		
		<!-- Thêm Spring TX -->
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-tx</artifactId>
			<version>2.5.6</version>
		</dependency>
		
		<!-- Thêm Spring Struts integration -->
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-struts</artifactId>
			<version>2.0.8</version>
		</dependency>
		
		<!-- Thêm ANTLR dependency -->
		<dependency>
			<groupId>antlr</groupId>
			<artifactId>antlr</artifactId>
			<version>2.7.7</version>
		</dependency>
		
		<!-- Thêm Javassist -->
		<dependency>
			<groupId>org.javassist</groupId>
			<artifactId>javassist</artifactId>
			<version>3.18.1-GA</version>
		</dependency>
		
		<!-- Cập nhật version của cglib -->
		<dependency>
			<groupId>cglib</groupId>
			<artifactId>cglib</artifactId>
			<version>2.2.2</version>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<artifactId>maven-compiler-plugin</artifactId>
				<version>3.13.0</version>
				<configuration>
					<release>21</release>
				</configuration>
			</plugin>
			<plugin>
				<artifactId>maven-war-plugin</artifactId>
				<version>3.2.3</version>
			</plugin>
		</plugins>
	</build>
</project>
\\\\\\\\\\\\\\\\\\\\
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

    // Getters và Setters
    public int getPsn() { return psn; }
    public void setPsn(int psn) { this.psn = psn; }

    public String getUserid() { return userid; }
    public void setUserid(String userid) { this.userid = userid; }

    public String getPass() { return pass; }
    public void setPass(String pass) { this.pass = pass; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Date getDeleteYmd() { return deleteYmd; }
    public void setDeleteYmd(Date deleteYmd) { this.deleteYmd = deleteYmd; }

    public Date getInsertYmd() { return insertYmd; }
    public void setInsertYmd(Date insertYmd) { this.insertYmd = insertYmd; }

    public int getInsertPsncd() { return insertPsncd; }
    public void setInsertPsncd(int insertPsncd) { this.insertPsncd = insertPsncd; }

    public Date getUpdateYmd() { return updateYmd; }
    public void setUpdateYmd(Date updateYmd) { this.updateYmd = updateYmd; }

    public int getUpdatePsncd() { return updatePsncd; }
    public void setUpdatePsncd(int updatePsncd) { this.updatePsncd = updatePsncd; }
}\\\\\\\\\\\\\\\\
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-mapping PUBLIC 
    "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
    "http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd">

<hibernate-mapping>
    <class name="dto.UserDTO" table="MSTUSER">
        <id name="psn" type="int">
            <column name="Psn" />
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
            <column name="Insert_ymd" />
        </property>
        
        <property name="insertPsncd" type="int">
            <column name="Insertpsncd" not-null="true" />
        </property>
        
        <property name="updateYmd" type="date">
            <column name="Updateymd" />
        </property>
        
        <property name="updatePsncd" type="int">
            <column name="Updatepsncd" not-null="true" />
        </property>
    </class>
</hibernate-mapping> \\\\\\\\\\\\\\\\\
package dao;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import dto.UserDTO;
import java.util.List;

public class UserDAO extends HibernateDaoSupport {
    
    @SuppressWarnings("unchecked")
    public List<UserDTO> validateAndGetUser(String userid, String pass) {
        String hql = "FROM UserDTO WHERE userid = ? AND pass = ? AND deleteYmd IS NULL";
        return getHibernateTemplate().find(hql, new Object[]{userid, pass});
    }

    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();
        List<UserDTO> users = userDAO.validateAndGetUser("user1", "password123");
        for (UserDTO user : users) {
            System.out.println(user.getUsername());
        }
    }
}
\\\\\\\\\\\\\\\\\\
package factory;

import dao.UserDAO;
import dto.UserDTO;
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
    
    public UserDTO processLogin(LoginForm form) {
        List<UserDTO> users = userDAO.validateAndGetUser(form.getUserid(), form.getPass());
        if (users != null && !users.isEmpty()) {
            return users.get(0);
        }
        return null;
    }
    
    public LoginForm getUserInfo(String userid, String pass) {
        List<LoginForm> users = userDAO.validateAndGetUser(userid, pass);
        return !users.isEmpty() ? users.get(0) : null;
    }
} \\\\\\\\\\\\\\\\\\\\\\
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
import dto.UserDTO;

public class LoginAction extends Action {
    private LoginFactory loginFactory;
    
    public void setLoginFactory(LoginFactory loginFactory) {
        this.loginFactory = loginFactory;
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        LoginForm loginForm = (LoginForm) form;
        UserDTO user = loginFactory.processLogin(loginForm);
        
        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            return mapping.findForward("success");
        } else {
            return mapping.findForward("failure");
        }
    }
}\\\\\\\\\\\\\\\\
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE struts-config PUBLIC
        "-//Apache Software Foundation//DTD Struts Configuration 1.2//EN"
        "http://struts.apache.org/dtds/struts-config_1_2.dtd">

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
                validate="true"
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

    <!-- Message Resources Configuration -->
    <message-resources parameter="resources.ApplicationResources"/>

    <!-- Plugins Configuration -->
    <plug-in className="org.springframework.web.struts.ContextLoaderPlugIn">
        <set-property property="contextConfigLocation"
                     value="/WEB-INF/applicationContext.xml,/WEB-INF/hibernateContext.xml"/>
    </plug-in>
</struts-config>
