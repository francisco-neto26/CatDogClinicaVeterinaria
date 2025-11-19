package br.com.catdogclinicavet.backend_api.repositories;

import br.com.catdogclinicavet.backend_api.models.Agendamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    Page<Agendamento> findByClienteId(Long clienteId, Pageable pageable);

    Page<Agendamento> findByFuncionarioId(Long funcionarioId, Pageable pageable);

    Page<Agendamento> findByAnimalId(Long animalId, Pageable pageable);
}