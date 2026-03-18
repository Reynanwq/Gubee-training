package com.event.repository;

import com.event.model.ContaPontoNoTempoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContaPontoNoTempoRepository extends JpaRepository<ContaPontoNoTempoEntity, Long> {

    // Busca todos os snapshots de uma conta, ordenados do mais recente para o mais antigo
    List<ContaPontoNoTempoEntity> findByContaIdOrderByPontoNoTempoDesc(Long contaId);
}