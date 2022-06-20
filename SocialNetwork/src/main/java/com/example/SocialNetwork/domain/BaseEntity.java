package com.example.SocialNetwork.domain;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Where(clause = "deleted = false")
@Getter
@Setter
public class BaseEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    protected String id;

    @Column(columnDefinition = "boolean default false", nullable = false)
    protected boolean deleted;

}
