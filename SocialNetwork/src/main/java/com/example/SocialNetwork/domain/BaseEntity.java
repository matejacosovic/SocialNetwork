package com.example.SocialNetwork.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Where;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Where(clause = "deleted = false")
@Getter
@Setter
@FilterDef(name = "tenantFilter", parameters = {@ParamDef(name = "tenantId", type = "string")})
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class BaseEntity implements TenantSupport, Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    protected String id;

    @Column(columnDefinition = "boolean default false", nullable = false)
    protected boolean deleted;

    @Column(name = "tenant_id")
    private String tenantId;
}
