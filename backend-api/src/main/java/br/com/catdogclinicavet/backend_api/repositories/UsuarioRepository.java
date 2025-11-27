package br.com.catdogclinicavet.backend_api.repositories;

import br.com.catdogclinicavet.backend_api.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    Boolean existsByEmail(String email);

    @Query("SELECT u FROM Usuario u JOIN FETCH u.role JOIN FETCH u.pessoa WHERE u.role.nome IN :roleNames")
    List<Usuario> findByRoleNames(@Param("roleNames") List<String> roleNames);

    @Query("SELECT u FROM Usuario u JOIN FETCH u.role JOIN FETCH u.pessoa")
    List<Usuario> findAll();
}