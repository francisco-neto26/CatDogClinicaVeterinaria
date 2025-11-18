package br.com.catdogclinicavet.backend_api.repositories;

import br.com.catdogclinicavet.backend_api.models.ItemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemServicoRepository extends JpaRepository<ItemServico, Long> {

    List<ItemServico> findByTipoItemId(Long tipoItemId);
}