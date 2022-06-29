package com.example.SocialNetwork.repository;

import java.util.Optional;

import com.example.SocialNetwork.domain.Tenant;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, Long>{
        Optional<Tenant> findBySchema(String schema);
}
