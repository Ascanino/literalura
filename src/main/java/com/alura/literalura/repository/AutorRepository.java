package com.alura.literalura.repository;

import com.alura.literalura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {
    @Query("SELECT a FROM Autor a JOIN FETCH a.libros")
    List<Autor> findAllConLibros();  // Recupera autores junto con sus libros

    @Query("SELECT a FROM Autor a JOIN FETCH a.libros WHERE a.anoNacimiento <= :ano AND (a.anoFallecimiento >= :ano OR a.anoFallecimiento IS NULL)")
    List<Autor> findAutoresVivosEnAnoConLibros(int ano);

    Optional<Autor> findByNombre(String nombre);  // Buscar autor por nombre
}



