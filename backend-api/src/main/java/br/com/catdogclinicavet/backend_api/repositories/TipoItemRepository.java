package br.com.catdogclinicavet.backend_api.repositories;

import br.com.catdogclinicavet.backend_api.models.TipoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoItemRepository extends JpaRepository<TipoItem, Long> {

    Optional<TipoItem> findByNome(String nome);
}