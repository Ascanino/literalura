package com.alura.literalura.service;

import com.alura.literalura.model.Autor;
import com.alura.literalura.repository.AutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AutorService {

    @Autowired
    private AutorRepository autorRepository;

    public List<Autor> listarAutor() {
        List<Autor> autor = autorRepository.findAllConLibros();  // Recuperar todos los autores con sus libros
        System.out.println("Autores encontrados: " + autor.size());  // Verificar la cantidad de autores
        return autor;
    }

    public List<Autor> listarAutorVivosEnAno(int ano) {
        return autorRepository.findAutoresVivosEnAnoConLibros(ano);
    }

    public Autor crearAutor (Autor autor){
        return autorRepository.save(autor);
    }

    public Optional<Autor> obtenerAutorPorId(Long id){
        return autorRepository.findById(id);
    }

    public Optional<Autor> obtenerAutorPorNombre(String nombre){
        return autorRepository.findByNombre(nombre);
    }

    public Autor actualizarAutor(Long id , Autor autorDetalles) {
        Autor autor = autorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Oops, no logramos encontrar al autor que mencionaste"));
        autor.setNombre(autorDetalles.getNombre());
        autor.setAnoNacimiento(autorDetalles.getAnoNacimiento());
        autor.setAnoFallecimiento(autorDetalles.getAnoFallecimiento());
        return autorRepository.save(autor);
    }

    public void eliminarAutor(Long id) {
        autorRepository.deleteById(id);
    }


}
