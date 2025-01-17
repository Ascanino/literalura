package com.alura.literalura.principal;

import com.alura.literalura.dto.LibroDTO;
import com.alura.literalura.dto.RespuestaLibrosDTO;
import com.alura.literalura.model.Autor;
import com.alura.literalura.model.Libro;
import com.alura.literalura.service.AutorService;
import com.alura.literalura.service.ConsumoAPI;
import com.alura.literalura.service.ConvierteDatos;
import com.alura.literalura.service.LibroService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Component
public class Menu {

    private static final Logger logger = LoggerFactory.getLogger(Menu.class);
    private static final String BASE_URL = "https://gutendex.com/books/";
    private final LibroService libroService;
    private final AutorService autorService;
    private final ConsumoAPI consumoAPI;
    private final ConvierteDatos convierteDatos;

    public Menu(
            LibroService libroService,
            AutorService autorService,
            ConsumoAPI consumoAPI,
            ConvierteDatos convierteDatos
    ) {
        this.libroService = libroService;
        this.autorService = autorService;
        this.consumoAPI = consumoAPI;
        this.convierteDatos = convierteDatos;
    }

    public void mostrarMenu() {
        try (Scanner scanner = new Scanner(System.in)) {
            int opcion;
            do {
                System.out.println("""
                    --- LITERALURA ---
                    1 - Buscar libro por título
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un año
                    5 - Listar libros por idioma
                    0 - Salir
                    """);
                System.out.print("Seleccione una opción: ");
                opcion = scanner.nextInt();
                scanner.nextLine();

                switch (opcion) {
                    case 1 -> buscarLibroPorTitulo(scanner);
                    case 2 -> listarLibros();
                    case 3 -> listarAutores();
                    case 4 -> listarAutoresVivosEnAno(scanner);
                    case 5 -> listarLibrosPorIdioma(scanner);
                    case 0 -> System.out.println("Saliendo...");
                    default -> System.out.println("Opción no válida.");
                }
            } while (opcion != 0);
        }
    }

    private void buscarLibroPorTitulo(Scanner sc) {
        System.out.print("Ingrese el título del libro: ");
        String titulo = sc.nextLine().trim();

        List<LibroDTO> librosDTO = obtenerLibrosDeAPI(titulo);

        if (!librosDTO.isEmpty()) {
            librosDTO.forEach(dto -> {
                Optional<Libro> libroExistenteOpt = libroService.obtenerLibroPorTitulo(dto.titulo());
                if (libroExistenteOpt.isPresent()) {
                    Libro libroExistente = libroExistenteOpt.get();
                    Libro libroNuevo = dto.toLibro();
                    libroNuevo.setId(libroExistente.getId());
                    libroNuevo.setIdiomas(libroNuevo.getIdiomas().replace("[", "").replace("]", ""));
                    libroNuevo.setAutor(getOrCreateAutores(dto));

                    if (debeActualizar(libroExistente, libroNuevo)) {
                        libroService.actualizarLibro(libroNuevo.getId(), libroNuevo);
                        System.out.println("Se actualizó el libro con nueva información:");
                        mostrarDetallesLibro(libroNuevo);
                    } else {
                        System.out.println("El libro ya existe y no hay más información para actualizar.");
                    }
                } else {
                    registrarLibro(dto);
                    mostrarDetallesLibro(dto);
                }
            });
        } else {
            System.out.println("No se encontraron libros que coincidan con el título: '" + titulo + "'");
        }
    }

    private List<LibroDTO> obtenerLibrosDeAPI(String titulo) {
        try {
            String url = BASE_URL + "?search=" + URLEncoder.encode(titulo, StandardCharsets.UTF_8);
            String json = consumoAPI.obtenerDatos(url);
            RespuestaLibrosDTO respuesta = convierteDatos.obtenerDatos(json, RespuestaLibrosDTO.class);

            // Filtrar libros que contengan al menos una palabra del título
            List<String> palabrasClave = List.of(titulo.split(" "));

            return respuesta.libro().stream()
                    .filter(libro -> palabrasClave.stream().anyMatch(palabra -> libro.titulo().toLowerCase().contains(palabra.toLowerCase())))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error al obtener datos de la API", e);
            return List.of();
        }
    }

    private void registrarLibro(LibroDTO dto) {
        Libro libro = dto.toLibro();
        libro.setIdiomas(libro.getIdiomas().replace("[", "").replace("]", ""));
        libro.setAutor(getOrCreateAutores(dto));
        libroService.crearLibro(libro);
        System.out.println("Libro registrado: " + libro.getTitulo());
    }

    private Autor getOrCreateAutores(LibroDTO dto) {
        String nombreAutor = dto.autores().get(0).nombre();
        return autorService.obtenerAutorPorNombre(nombreAutor)
                .orElseGet(() -> autorService.crearAutor(dto.autores().get(0).toAutor()));
    }

    private boolean debeActualizar(Libro existente, Libro nuevo) {
        boolean descargasMayores = nuevo.getNumeroDescargas() > existente.getNumeroDescargas();
        boolean idiomaDistinto = !nuevo.getIdiomas().equals(existente.getIdiomas());
        boolean autorDistinto = !nuevo.getAutor().equals(existente.getAutor());

        return descargasMayores || idiomaDistinto || autorDistinto;
    }

    private void listarLibros() {
        List<Libro> libros = libroService.listarLibros();
        System.out.println("Número de libros registrados: " + libros.size());
        libros.forEach(this::mostrarDetallesLibro);
    }

    private void mostrarDetallesLibro(Libro libro) {
        System.out.printf("""
                ------LIBRO--------
                Título: %s
                Autor: %s
                Idioma: %s
                Número de descargas: %d
                """,
                libro.getTitulo(),
                libro.getAutor() == null ? "Desconocido" : libro.getAutor().getNombre(),
                libro.getIdiomas(),
                libro.getNumeroDescargas()
        );
    }

    private void mostrarDetallesLibro(LibroDTO dto) {
        System.out.printf("""
                ------LIBRO--------
                Título: %s
                Autor: %s
                Idioma: %s
                Número de descargas: %d
                """,
                dto.titulo(),
                dto.autores().isEmpty() ? "Desconocido" : dto.autores().get(0).nombre(),
                dto.idiomas().get(0),
                dto.numeroDescargas()
        );
    }

    private void listarAutores() {
        List<Autor> autores = autorService.listarAutor();
        System.out.println("Número de autores registrados: " + autores.size());
        autores.forEach(this::mostrarDetallesAutor);
    }

    private void mostrarDetallesAutor(Autor autor) {
        String libros = autor.getLibros().stream()
                .map(Libro::getTitulo)
                .collect(Collectors.joining(", "));

        System.out.printf("""
                -------AUTOR-------
                Autor: %s
                Fecha de nacimiento: %d
                Fecha de fallecimiento: %s
                Libros: [ %s ]
                """,
                autor.getNombre(),
                autor.getAnoNacimiento(),
                autor.getAnoFallecimiento() != null ? autor.getAnoFallecimiento() : "Desconocido",
                libros
        );
    }

    private void listarAutoresVivosEnAno(Scanner sc) {
        System.out.print("Ingrese el año vivo de autor(es) que desea buscar: ");
        int ano = sc.nextInt();
        sc.nextLine();

        List<Autor> vivos = autorService.listarAutorVivosEnAno(ano);
        if (vivos.isEmpty()) {
            System.out.println("No se encontraron autores vivos en el año " + ano);
        } else {
            vivos.forEach(this::mostrarDetallesAutor);
        }
    }

    private void listarLibrosPorIdioma(Scanner sc) {
        System.out.println("Ingrese el idioma (es, en, fr, pt):");
        String idioma = sc.nextLine().trim().toLowerCase();

        if (List.of("es", "en", "fr", "pt").contains(idioma)) {
            libroService.listarLibrosPorIdioma(idioma).forEach(this::mostrarDetallesLibro);
        } else {
            System.out.println("Idioma no válido.");
        }
    }
}
