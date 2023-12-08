package si.fri.rso.simplsrecka.user.services.beans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;

import org.eclipse.microprofile.metrics.annotation.Metered;
import org.eclipse.microprofile.metrics.annotation.Timed;

import si.fri.rso.simplsrecka.user.lib.User;
import si.fri.rso.simplsrecka.user.models.converters.UserConverter;
import si.fri.rso.simplsrecka.user.models.entities.UserEntity;


@RequestScoped
public class UserBean {

    private Logger log = Logger.getLogger(UserBean.class.getName());

    @Inject
    private EntityManager em;


    @Timed(name = "get_all_users")
    public List<User> getUsersFilter(UriInfo uriInfo) {
        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0).build();
        return JPAUtils.queryEntities(em, UserEntity.class, queryParameters).stream()
                .map(UserConverter::toDto).collect(Collectors.toList());
    }

    public User getUser(Integer id) {
        UserEntity userEntity = em.find(UserEntity.class, id);
        if (userEntity == null) {
            throw new NotFoundException();
        }
        return UserConverter.toDto(userEntity);
    }

    public Integer login(String username, String password) {
        TypedQuery<UserEntity> query = em.createQuery(
                "SELECT u FROM UserEntity u WHERE u.username = :username AND u.password = :password",
                UserEntity.class);
        query.setParameter("username", username);
        query.setParameter("password", password);
        List<UserEntity> users = query.getResultList();

        if (!users.isEmpty()) {
            return users.get(0).getId();
        } else {
            throw new NotFoundException("Invalid username or password.");
        }
    }

    @Metered(name = "create_user")
    public User createUser(User user) {
        UserEntity userEntity = UserConverter.toEntity(user);
        try {
            beginTx();
            em.persist(userEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }
        if (userEntity.getId() == null) {
            throw new RuntimeException("Entity was not persisted");
        }
        return UserConverter.toDto(userEntity);
    }

    public User updateUser(Integer id, User user) {
        UserEntity entity = em.find(UserEntity.class, id);
        if (entity == null) {
            return null;
        }
        UserEntity updatedEntity = UserConverter.toEntity(user);
        try {
            beginTx();
            updatedEntity.setId(entity.getId());
            updatedEntity = em.merge(updatedEntity);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }
        return UserConverter.toDto(updatedEntity);
    }

    public boolean deleteUser(Integer id) {
        UserEntity userEntity = em.find(UserEntity.class, id);
        if (userEntity != null) {
            try {
                beginTx();
                em.remove(userEntity);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else {
            return false;
        }
        return true;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}