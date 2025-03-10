<action-mappings>
    <!-- Mapping cho trang search -->
    <action path="/search"
            type="action.SearchAction"
            name="customerForm"
            scope="request">
        <forward name="success" path="/pages/search.jsp"/>
    </action>

    <!-- Mapping cho add/edit - giữ nguyên path="/add" -->
    <action path="/add"
            type="action.EditAction"
            name="customerForm" 
            scope="request">
        <forward name="success" path="/pages/edit.jsp"/>
        <forward name="error" path="/pages/search.jsp"/>
    </action>
</action-mappings>\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
  <!-- Link Add New -->
<html:link action="/add?action=add" styleClass="button">Add New</html:link>

<!-- Link Edit trong bảng -->
<td>
    <span class="triangle">▶</span>
    <html:link action="/add?action=edit" paramId="id" paramName="customer" paramProperty="id">
        <bean:write name="customer" property="id"/>
    </html:link>
</td>\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
  Kết quả:
URL sẽ là: http://localhost:8080/Thuctap/add.do?action=add
JSP vẫn nằm trong: /pages/edit.jsp
User không thấy thư mục pages trong URL
Lợi ích:
1. URL ngắn gọn, dễ nhớ
Ẩn cấu trúc thư mục thực của ứng dụng
Dễ dàng di chuyển JSP mà không ảnh hưởng đến URL\\\\\\\\\\\\\\\\\\\\\\\\\
  package dao;

import java.util.List;
import java.util.ArrayList;
import org.hibernate.Session;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.transform.Transformers;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import dto.CustomerDTO;
import java.util.Date;

public class CustomerDAO extends HibernateDaoSupport {
    private static final int RECORDS_PER_PAGE = 5;

    @SuppressWarnings("unchecked")
    public List<CustomerDTO> searchCustomers(String name, String sex, 
            String birthdayFrom, String birthdayTo, int page) {
        Session session = getSession();
        try {
            // Tạo câu query base
            StringBuilder hql = new StringBuilder();
            hql.append("select new CustomerDTO(c.id, c.name, c.sex, c.birthday, c.email, c.address) ")
               .append("from CustomerDTO c ")
               .append("where c.deleteYmd is null ");

            // Thêm các điều kiện tìm kiếm
            if (name != null && !name.trim().isEmpty()) {
                hql.append("and c.name like :name ");
            }
            if (sex != null && !sex.trim().isEmpty() && !"-1".equals(sex)) {
                hql.append("and c.sex = :sex ");
            }
            if (birthdayFrom != null && !birthdayFrom.isEmpty()) {
                hql.append("and c.birthday >= :birthdayFrom ");
            }
            if (birthdayTo != null && !birthdayTo.isEmpty()) {
                hql.append("and c.birthday <= :birthdayTo ");
            }

            // Set các tham số
            org.hibernate.Query query = session.createQuery(hql.toString());
            
            if (name != null && !name.trim().isEmpty()) {
                String escapedName = name.trim()
                    .replace("%", "\\%")
                    .replace("_", "\\_");
                query.setString("name", "%" + escapedName + "%");
            }
            if (sex != null && !sex.trim().isEmpty() && !"-1".equals(sex)) {
                query.setInteger("sex", Integer.parseInt(sex));
            }
            if (birthdayFrom != null && !birthdayFrom.isEmpty()) {
                query.setString("birthdayFrom", birthdayFrom);
            }
            if (birthdayTo != null && !birthdayTo.isEmpty()) {
                query.setString("birthdayTo", birthdayTo);
            }

            // Phân trang
            query.setFirstResult((page - 1) * RECORDS_PER_PAGE);
            query.setMaxResults(RECORDS_PER_PAGE);

            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<CustomerDTO>();
        } finally {
            releaseSession(session);
        }
    }

    public int getTotalRecords(String name, String sex, String birthdayFrom, String birthdayTo) {
        Session session = getSession();
        try {
            StringBuilder hql = new StringBuilder();
            hql.append("select count(*) from CustomerDTO c ")
               .append("where c.deleteYmd is null ");

            if (name != null && !name.trim().isEmpty()) {
                hql.append("and c.name like :name ");
            }
            if (sex != null && !sex.trim().isEmpty() && !"-1".equals(sex)) {
                hql.append("and c.sex = :sex ");
            }
            if (birthdayFrom != null && !birthdayFrom.isEmpty()) {
                hql.append("and c.birthday >= :birthdayFrom ");
            }
            if (birthdayTo != null && !birthdayTo.isEmpty()) {
                hql.append("and c.birthday <= :birthdayTo ");
            }

            org.hibernate.Query query = session.createQuery(hql.toString());

            if (name != null && !name.trim().isEmpty()) {
                String escapedName = name.trim()
                    .replace("%", "\\%")
                    .replace("_", "\\_");
                query.setString("name", "%" + escapedName + "%");
            }
            if (sex != null && !sex.trim().isEmpty() && !"-1".equals(sex)) {
                query.setInteger("sex", Integer.parseInt(sex));
            }
            if (birthdayFrom != null && !birthdayFrom.isEmpty()) {
                query.setString("birthdayFrom", birthdayFrom);
            }
            if (birthdayTo != null && !birthdayTo.isEmpty()) {
                query.setString("birthdayTo", birthdayTo);
            }

            Long count = (Long) query.uniqueResult();
            return count.intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            releaseSession(session);
        }
    }

    public int getMaxPage(int totalRecords) {
        if (totalRecords == 0) return 1;
        return (int) Math.ceil((double) totalRecords / RECORDS_PER_PAGE);
    }

    public boolean deleteCustomers(String[] customerIds) {
        Session session = getSession();
        try {
            session.beginTransaction();
            
            // Chuyển String[] thành Integer[]
            Integer[] ids = new Integer[customerIds.length];
            for (int i = 0; i < customerIds.length; i++) {
                ids[i] = Integer.parseInt(customerIds[i]);
            }
            
            // Update trực tiếp bằng HQL
            StringBuilder hql = new StringBuilder()
                .append("update CustomerDTO c ")
                .append("set c.deleteYmd = :now ")
                .append("where c.id in (:ids)");
                
            int updatedCount = session.createQuery(hql.toString())
                .setParameter("now", new Date())
                .setParameterList("ids", ids)
                .executeUpdate();
                
            session.getTransaction().commit();
            return updatedCount > 0;
        } catch (Exception e) {
            session.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally {
            releaseSession(session);
        }
    }

    public int getNextCustomerId() {
        Session session = getSession();
        try {
            String hql = "select max(c.id) from CustomerDTO c";
            Integer maxId = (Integer) session.createQuery(hql).uniqueResult();
            return maxId == null ? 1 : maxId + 1;
        } finally {
            releaseSession(session);
        }
    }

    public boolean addCustomer(String name, String sex, String birthday, 
                             String email, String address, int userPsn) {
        Session session = getSession();
        try {
            session.beginTransaction();
            
            // Lấy ID mới
            int newId = getNextCustomerId();
            Date now = new Date();
            
            // Tạo câu INSERT bằng HQL
            StringBuilder hql = new StringBuilder()
                .append("insert into CustomerDTO ")
                .append("(id, name, sex, birthday, email, address, ")
                .append("insertYmd, updateYmd, insertPsncd, updatePsncd) ")
                .append("select :id, :name, :sex, :birthday, :email, :address, ")
                .append(":now, :now, :userPsn, :userPsn ")
                .append("from CustomerDTO where 1=0");  // Dummy FROM clause vì HQL yêu cầu
                
            int result = session.createQuery(hql.toString())
                .setInteger("id", newId)
                .setString("name", name)
                .setInteger("sex", Integer.parseInt(sex))
                .setString("birthday", birthday)
                .setString("email", email)
                .setString("address", address)
                .setParameter("now", now)
                .setInteger("userPsn", userPsn)
                .executeUpdate();
                
            session.getTransaction().commit();
            return result > 0;
        } catch (Exception e) {
            session.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally {
            releaseSession(session);
        }
    }

    public CustomerDTO getCustomerById(int customerId) {
        Session session = getSession();
        try {
            StringBuilder hql = new StringBuilder()
                .append("select new CustomerDTO(")
                .append("c.id, c.name, c.sex, c.birthday, c.email, c.address) ")
                .append("from CustomerDTO c ")
                .append("where c.id = :id ")
                .append("and c.deleteYmd is null");
                
            return (CustomerDTO) session.createQuery(hql.toString())
                .setInteger("id", customerId)
                .uniqueResult();
        } finally {
            releaseSession(session);
        }
    }

    public boolean updateCustomer(int customerId, String name, String sex, 
                                String birthday, String email, String address, int userPsn) {
        Session session = getSession();
        try {
            session.beginTransaction();
            
            StringBuilder hql = new StringBuilder()
                .append("update CustomerDTO c ")
                .append("set c.name = :name, ")
                .append("c.sex = :sex, ")
                .append("c.birthday = :birthday, ")
                .append("c.email = :email, ")
                .append("c.address = :address, ")
                .append("c.updateYmd = :now, ")
                .append("c.updatePsncd = :userPsn ")
                .append("where c.id = :id");
                
            int updatedCount = session.createQuery(hql.toString())
                .setString("name", name)
                .setInteger("sex", Integer.parseInt(sex))
                .setString("birthday", birthday)
                .setString("email", email)
                .setString("address", address)
                .setParameter("now", new Date())
                .setInteger("userPsn", userPsn)
                .setInteger("id", customerId)
                .executeUpdate();
                
            session.getTransaction().commit();
            return updatedCount > 0;
        } catch (Exception e) {
            session.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally {
            releaseSession(session);
        }
    }
}
