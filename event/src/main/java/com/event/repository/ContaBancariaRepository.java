package com.event.repository;

import com.event.model.ContaBancariaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContaBancariaRepository extends JpaRepository<ContaBancariaEntity, Long> {}