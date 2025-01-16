package com.alura.literalura.dto;

import com.alura.literalura.model.Autor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AutorDTO(
        @JsonProperty("name") String nombre,
        @JsonProperty("birth_year") Integer anoNacimiento,
        @JsonProperty("death_year") Integer anoFallecimiento
) {

    // Método para convertir DTO a entidad Autor
    public Autor toAutor() {
        return new Autor(
                this.nombre,
                this.anoNacimiento != null ? this.anoNacimiento : 0,
                this.anoFallecimiento
        );
    }
}
