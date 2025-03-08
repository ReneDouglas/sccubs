package br.com.tecsus.sccubs.repositories.Impl;

import br.com.tecsus.sccubs.dtos.UBSsystemUserDTO;
import br.com.tecsus.sccubs.entities.SystemUser;
import br.com.tecsus.sccubs.repositories.SystemUserRepositoryCustom;
import br.com.tecsus.sccubs.utils.ValidationUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

//@Repository
public class SystemUserRepositoryImpl implements SystemUserRepositoryCustom {

    private final EntityManager em;
    private ValidationUtils validationUtils;

    @Autowired
    public SystemUserRepositoryImpl(JpaContext jpaContext) {
        this.em = jpaContext.getEntityManagerByManagedType(SystemUser.class);
    }

    @Autowired
    public void setValidationUtils(ValidationUtils validationUtils) {
        this.validationUtils = validationUtils;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UBSsystemUserDTO> findSystemUsersNameByNameContains(String name) {

        String jpql = """
                        SELECT su.id, su.name, 'null', 'null' FROM SystemUser su
                        WHERE su.name LIKE CONCAT('%', :name, '%')
                        AND su.basicHealthUnit IS NULL
                        AND su.active IS TRUE
               """;

        TypedQuery<UBSsystemUserDTO> usersQuery = em.createQuery(jpql, UBSsystemUserDTO.class);
        usersQuery.setParameter("name", name);
        usersQuery.setMaxResults(5);

        return usersQuery.getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SystemUser> findSystemUsersPaginated(SystemUser systemUser, Pageable page) {

        StringBuilder jpql = new StringBuilder();

        jpql.append("SELECT su.id FROM SystemUser su ");
        jpql.append("LEFT JOIN su.roles r ");
        jpql.append("WHERE su.creationUser = :creationUser ");

        if (validationUtils.attrIsNotNull(systemUser.getUsername())) {
            jpql.append("AND su.username = :username ");
        }
        if (validationUtils.attrIsNotNull(systemUser.getName())) {
            jpql.append("AND su.name = :name ");
        }
        if (validationUtils.attrIsNotNull(systemUser.getBasicHealthUnit())) {
            jpql.append("AND su.basicHealthUnit.id = :ubsId ");
        }
        if (validationUtils.attrIsNotNull(systemUser.getSelectedRoleId())) {
            jpql.append("AND r.id = :roleId ");
        }
        if (validationUtils.attrIsNotNull(systemUser.getActive())) {
            jpql.append("AND su.active = :active ");
        }

        jpql.append("ORDER BY su.creationDate DESC ");

        TypedQuery<Long> systemUsersIdQuery = em.createQuery(jpql.toString(), Long.class);
        attachParameters(systemUsersIdQuery, systemUser);

        systemUsersIdQuery.setFirstResult(page.getPageNumber() * page.getPageSize());
        systemUsersIdQuery.setMaxResults(page.getPageSize());
        var systemUsersIds = systemUsersIdQuery.getResultList();

        long totalCountSystemUsers = 0;

        if (systemUsersIds.size() < page.getPageSize()) {
            totalCountSystemUsers = systemUsersIds.size();
        } else {
            Query count = em.createQuery(jpql.toString().replace("su.id", "count(su.id)"));
            attachParameters(count, systemUser);
            totalCountSystemUsers = (long) count.getSingleResult();
        }

        TypedQuery<SystemUser> systemUsersQuery = em.createQuery("""
                                         SELECT su FROM SystemUser su
                                             LEFT JOIN FETCH su.roles
                                             LEFT JOIN FETCH su.basicHealthUnit
                                         WHERE su.id IN :ids
                                         ORDER BY su.creationDate DESC
                """, SystemUser.class);

        systemUsersQuery.setParameter("ids", systemUsersIds);
        List<SystemUser> systemUsers = systemUsersQuery.getResultList();

        return new PageImpl<>(systemUsers, page, totalCountSystemUsers);
    }

    private void attachParameters(Query query, SystemUser systemUser) {

        query.setParameter("creationUser", systemUser.getCreationUser());

        if (validationUtils.attrIsNotNull(systemUser.getUsername())) {
            query.setParameter("username", systemUser.getUsername());
        }
        if (validationUtils.attrIsNotNull(systemUser.getName())) {
            query.setParameter("name", systemUser.getName());
        }
        if (validationUtils.attrIsNotNull(systemUser.getBasicHealthUnit())) {
            query.setParameter("ubsId", systemUser.getBasicHealthUnit().getId());
        }
        if (validationUtils.attrIsNotNull(systemUser.getSelectedRoleId())) {
            query.setParameter("roleId", systemUser.getSelectedRoleId());
        }
        if (validationUtils.attrIsNotNull(systemUser.getActive())) {
            query.setParameter("active", systemUser.getActive());
        }
    }
}
