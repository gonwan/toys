package com.gonwan.snippet.json.demo.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TTestRepository2 extends JpaRepository<TTest2, Long> {

}
