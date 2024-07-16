package br.com.alura.literalura.repository;

import br.com.alura.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {

    @Query("SELECT a FROM Autor a WHERE :ano > a.anoDeNascimento AND :ano < a.anoDeFalecimento")
    List<Autor> listarAutoresVivosEmDeterminadoAno(int ano);

    Optional<Autor> findByNome(String nome);
}
