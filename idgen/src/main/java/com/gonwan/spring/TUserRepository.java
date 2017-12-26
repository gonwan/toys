package com.gonwan.spring;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gonwan.spring.generated.TUser;

public interface TUserRepository extends JpaRepository<TUser, Integer> {

}
