package br.com.catdogclinicavet.backend_api.repositories;

import br.com.catdogclinicavet.backend_api.models.ContaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContaItemRepository extends JpaRepository<ContaItem, Long> {

    List<ContaItem> findByContaId(Long contaId);
}