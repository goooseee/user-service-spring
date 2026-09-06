package com.example.user_service_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.user_service_spring.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{
	
}
