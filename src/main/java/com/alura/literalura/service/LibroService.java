package com.alura.literalura.service;

import com.alura.literalura.model.Libro;
import com.alura.literalura.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LibroService {

    @Autowired
    private LibroRepository libroRepository;

    public List<Libro> listarLibros() {
        List<Libro> libros = libroRepository.findAll();  // Recuperar todos los libros
        System.out.println("Libros encontrados: " + libros.size());  // Verificar la cantidad de libros
        return libros;
    }

    public List<Libro> listarLibrosPorIdioma(String idiomas) {
        return libroRepository.findByIdiomas(idiomas);
    }

    public Libro crearLibro(Libro libro) {
        return libroRepository.save(libro);
    }

    public Optional<Libro> obtenerLibroPorId(Long id) {
        return libroRepository.findById(id);
    }

    public Optional<Libro> obtenerLibroPorTitulo(String titulo) {
        return libroRepository.findByTituloIgnoreCase(titulo);
    }

    public Libro actualizarLibro(Long id, Libro libroDetalles) {
        Libro libro = libroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

        // Solo actualiza el campo si el valor proporcionado no es nulo o vacío
        if (libroDetalles.getTitulo() != null && !libroDetalles.getTitulo().isEmpty()) {
            libro.setTitulo(libroDetalles.getTitulo());
        }
        if (libroDetalles.getIdiomas() != null && !libroDetalles.getIdiomas().isEmpty()) {
            libro.setIdiomas(libroDetalles.getIdiomas());
        }
        if (libroDetalles.getNumeroDescargas() > 0) {  // Asumiendo que el número de descargas no puede ser negativo
            libro.setNumeroDescargas(libroDetalles.getNumeroDescargas());
        }
        if (libroDetalles.getAutor() != null && libroDetalles.getAutor().getNombre() != null) {
            libro.setAutor(libroDetalles.getAutor());
        }

        return libroRepository.save(libro);
    }


    public void eliminarLibro(Long id) {
        libroRepository.deleteById(id);
    }
}
