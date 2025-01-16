package com.alura.literalura.repository;

import com.alura.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository <Libro, Long>{

    Optional <Libro> findByTituloIgnoreCase(String titulo);

    @Query("SELECT l FROM Libro l WHERE l.idiomas = :idiomas")
    List<Libro> findByIdiomas(String idiomas);  // Método adicional para filtrar por idioma
}
