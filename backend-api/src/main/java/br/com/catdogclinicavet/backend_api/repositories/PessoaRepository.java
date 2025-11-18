package br.com.catdogclinicavet.backend_api.repositories;

import br.com.catdogclinicavet.backend_api.models.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {

    Optional<Pessoa> findByCpfcnpj(String cpfcnpj);

    Boolean existsByCpfcnpj(String cpfcnpj);
}