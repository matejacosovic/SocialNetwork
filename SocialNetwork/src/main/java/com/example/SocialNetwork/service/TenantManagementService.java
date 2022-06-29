package com.example.SocialNetwork.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import com.example.SocialNetwork.domain.Tenant;
import com.example.SocialNetwork.repository.TenantRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ResourceLoader;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.stereotype.Service;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;

@Service
@ConditionalOnProperty(name = "spring.liquibase.enabled", havingValue = "true", matchIfMissing = true)
public class TenantManagementService  {

    @Value("${tenant.liquibase.change-log}")
    private String changeLog;

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;
    private final ResourceLoader resourceLoader;
    private final TenantRepository tenantRepository;

    @Autowired
    public TenantManagementService(DataSource dataSource,
                                       JdbcTemplate jdbcTemplate,
                                       ResourceLoader resourceLoader,
                                       TenantRepository tenantRepository) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
        this.resourceLoader = resourceLoader;
        this.tenantRepository = tenantRepository;
    }

    private static final String VALID_SCHEMA_NAME_REGEXP = "[A-Za-z0-9_]*";

    @PostConstruct
    public void initTenants(){
        List<Tenant> tenants = tenantRepository.findAll();
        tenants.forEach(tenant -> {
            try {
                createSchema(tenant.getSchema());
                runLiquibase(dataSource, tenant.getSchema());
            } catch (LiquibaseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }

    public void createTenant(String schema) {

        // Verify schema string to prevent SQL injection
        if (!schema.matches(VALID_SCHEMA_NAME_REGEXP)) {
            throw new RuntimeException("Invalid schema name: " + schema);
        }

        try {
            createSchema(schema);
            runLiquibase(dataSource, schema);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error when creating schema: " + schema, e);
        } catch (LiquibaseException e) {
            throw new RuntimeException("Error when populating schema: ", e);
        }
        Tenant tenant = new Tenant();
        tenant.setSchema(schema);
        tenantRepository.save(tenant);
    }

    private void createSchema(String schema) {
        jdbcTemplate.execute((StatementCallback<Boolean>) stmt -> stmt.execute("CREATE SCHEMA IF NOT EXISTS " + schema));
    }

    private void runLiquibase(DataSource dataSource, String schema) throws LiquibaseException {
        SpringLiquibase liquibase = getSpringLiquibase(dataSource, schema);
        liquibase.afterPropertiesSet();
    }

    protected SpringLiquibase getSpringLiquibase(DataSource dataSource, String schema) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setResourceLoader(resourceLoader);
        liquibase.setDataSource(dataSource);
        liquibase.setDefaultSchema(schema);
        liquibase.setChangeLog(changeLog);
        return liquibase;
    }
}
