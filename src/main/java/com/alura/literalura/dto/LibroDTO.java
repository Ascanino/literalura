package com.alura.literalura.dto;

import com.alura.literalura.model.Libro;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LibroDTO(

        @JsonProperty("id")
        int id,

        @JsonProperty("title")
        String titulo,

        @JsonProperty("authors")
        List<AutorDTO> autores,

        @JsonProperty("languages")
        List<String> idiomas,

        @JsonProperty("download_count")
        int numeroDescargas
) {

    // Método para obtener el primer autor de manera segura
    public String getPrimerAutor() {
        return (autores != null && !autores.isEmpty()) ? autores.get(0).nombre() : "Autor desconocido";
    }

    // Método para obtener el primer idioma de manera segura
    public String getPrimerIdioma() {
        return (idiomas != null && !idiomas.isEmpty()) ? idiomas.get(0) : "Idioma desconocido";
    }

    // Método para convertir el DTO al modelo principal
    public Libro toLibro() {
        Libro libro = new Libro();
        libro.setTitulo(titulo);
        libro.setNumeroDescargas(numeroDescargas);

        // Asignar el primer autor o null si no hay autores
        libro.setAutor(autores != null && !autores.isEmpty() ? autores.get(0).toAutor() : null);

        // Guardar solo el primer idioma
        libro.setIdiomas(getPrimerIdioma());

        return libro;
    }
}
