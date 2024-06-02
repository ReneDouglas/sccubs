package br.com.tecsus.sccubs.repositories.Impl;

import br.com.tecsus.sccubs.entities.SystemUser;
import br.com.tecsus.sccubs.repositories.SystemUserRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class SystemUserRepositoryImpl implements SystemUserRepositoryCustom {

    private final EntityManager em;

    @Autowired
    public SystemUserRepositoryImpl(JpaContext jpaContext) {
        this.em = jpaContext.getEntityManagerByManagedType(SystemUser.class);
    }

    @Override
    public Page<SystemUser> findSystemUsersPaginated(SystemUser systemUser, Pageable page) {

        StringBuilder jpql = new StringBuilder();

        jpql.append("SELECT su.id FROM SystemUser su ");
        jpql.append("WHERE su.creationUser = :creationUser ");

        if (attrIsNotNull(systemUser.getUsername())) {
            jpql.append("AND su.username = :username ");
        }
        if (attrIsNotNull(systemUser.getName())) {
            jpql.append("AND su.name = :name ");
        }
        if (attrIsNotNull(systemUser.getBasicHealthUnit())) {
            jpql.append("AND su.basicHealthUnit.id = :ubsId ");
        }
        if (attrIsNotNull(systemUser.getSelectedRoleId())) {
            jpql.append("AND su.roles.id = :roleId ");
        }
        if (attrIsNotNull(systemUser.getActive())) {
            jpql.append("AND su.active = :active ");
        }

        TypedQuery<Long> systemUsersIdQuery = em.createQuery(jpql.toString(), Long.class);
        systemUsersIdQuery.setParameter("creationUser", systemUser.getCreationUser());

        if (attrIsNotNull(systemUser.getUsername())) {
            systemUsersIdQuery.setParameter("username", systemUser.getUsername());
        }
        if (attrIsNotNull(systemUser.getName())) {
            systemUsersIdQuery.setParameter("name", systemUser.getName());
        }
        if (attrIsNotNull(systemUser.getBasicHealthUnit())) {
            systemUsersIdQuery.setParameter("ubsId", systemUser.getBasicHealthUnit().getId());
        }
        if (attrIsNotNull(systemUser.getSelectedRoleId())) {
            systemUsersIdQuery.setParameter("roleId", systemUser.getSelectedRoleId());
        }
        if (attrIsNotNull(systemUser.getActive())) {
            systemUsersIdQuery.setParameter("active", systemUser.getActive());
        }

        systemUsersIdQuery.setFirstResult(page.getPageNumber() * page.getPageSize());
        systemUsersIdQuery.setMaxResults(page.getPageSize());

        var systemUsersIds = systemUsersIdQuery.getResultList();

        long totalCountSystemUsers = systemUsersIds.size() < page.getPageSize()
                ? page.getPageSize()
                : (long) em.createQuery(
                        jpql.toString().replace("su.id", "count(su.id)"))
                            .setParameter("creationUser", systemUser.getCreationUser())
                        .getSingleResult();

        TypedQuery<SystemUser> systemUsersQuery = em.createQuery("""
                                         SELECT su FROM SystemUser su
                                             LEFT JOIN FETCH su.roles
                                             LEFT JOIN FETCH su.basicHealthUnit
                                         WHERE su.id IN :ids
                """, SystemUser.class);

        systemUsersQuery.setParameter("ids", systemUsersIds);
        List<SystemUser> systemUsers = systemUsersQuery.getResultList();

        return new PageImpl<>(systemUsers, page, totalCountSystemUsers);
    }

    private Boolean attrIsNotNull(Object attr){

        if (attr instanceof String) {
            return attr != null && !((String) attr).isEmpty();
        }
        return attr != null;
    }
}
