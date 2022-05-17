package com.example.SocialNetwork.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@MappedSuperclass
@Where(clause = "deleted = false")
@Getter
@Setter
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "boolean default false", nullable = false)
    protected boolean deleted;

    @Version
    private Integer version;
}
