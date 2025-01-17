package com.alura.literalura.model;

import com.alura.literalura.dto.AutorDTO;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "autores")
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private int anoNacimiento;
    private Integer anoFallecimiento;

    @OneToMany(mappedBy = "autor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Libro> libros;

    // Constructor vacío (obligatorio para JPA/Hibernate)
    public Autor() {
    }

    // Constructor con parámetros
    public Autor(String nombre, int anoNacimiento, Integer anoFallecimiento) {
        this.nombre = nombre;
        this.anoNacimiento = anoNacimiento;
        this.anoFallecimiento = anoFallecimiento;
    }

    // Constructor a partir de un AutorDTO record
    public Autor(AutorDTO autorDTO) {
        this.nombre = autorDTO.nombre();
        this.anoNacimiento = autorDTO.anoNacimiento() != null ? autorDTO.anoNacimiento() : 0;
        this.anoFallecimiento = autorDTO.anoFallecimiento();
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getAnoNacimiento() {
        return anoNacimiento;
    }

    public void setAnoNacimiento(int anoNacimiento) {
        this.anoNacimiento = anoNacimiento;
    }

    public Integer getAnoFallecimiento() {
        return anoFallecimiento;
    }

    public void setAnoFallecimiento(Integer anoFallecimiento) {
        this.anoFallecimiento = anoFallecimiento;
    }

    public List<Libro> getLibros() {
        return libros;
    }

    public void setLibros(List<Libro> libros) {
        this.libros = libros;
    }

    @Override
    public String toString() {
        return "Autor{" +
                "id=" + id +
                ", nombre='" + (nombre != null ? nombre : "No asignado") + '\'' +
                ", anoNacimiento=" + anoNacimiento +
                ", anoFallecimiento=" + (anoFallecimiento != null ? anoFallecimiento : "No asignado") +
                ", libros=" + (libros != null ? libros : "No asignados") +
                '}';
    }
}
