package br.com.catdogclinicavet.backend_api.repositories;

import br.com.catdogclinicavet.backend_api.models.TituloReceber;
import br.com.catdogclinicavet.backend_api.models.enums.TituloStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TituloReceberRepository extends JpaRepository<TituloReceber, Long> {

    List<TituloReceber> findByContaId(Long contaId);

    List<TituloReceber> findByClienteId(Long clienteId);

    List<TituloReceber> findByStatus(TituloStatus status);
}