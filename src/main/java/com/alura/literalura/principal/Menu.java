package com.alura.literalura.principal;

import com.alura.literalura.dto.LibroDTO;
import com.alura.literalura.dto.RespuestaLibrosDTO;
import com.alura.literalura.model.Autor;
import com.alura.literalura.model.Libro;
import com.alura.literalura.repository.AutorRepository;
import com.alura.literalura.repository.LibroRepository;
import com.alura.literalura.service.ConsumoAPI;
import com.alura.literalura.service.ConvierteDatos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class Menu {

    private static final Logger logger = LoggerFactory.getLogger(Menu.class);
    private static final String BASE_URL = "https://gutendex.com/books/";
    private final ConsumoAPI consumoAPI;
    private final ConvierteDatos convierteDatos;
    private final AutorRepository autorRepository;
    private final LibroRepository libroRepository;

    public Menu(
            ConsumoAPI consumoAPI,
            ConvierteDatos convierteDatos,
            AutorRepository autorRepository,
            LibroRepository libroRepository
    ) {
        this.consumoAPI = consumoAPI;
        this.convierteDatos = convierteDatos;
        this.autorRepository = autorRepository;
        this.libroRepository = libroRepository;
    }

    public void mostrarMenu() {
        try (Scanner scanner = new Scanner(System.in)) {
            int opcion = -1;
            do {
                System.out.println("""
                    
                    --- LITERALURA ---
                    1 - Buscar libro por título
                    2 - Buscar libro por autor
                    3 - Listar libros registrados
                    4 - Listar autores registrados
                    5 - Listar autores registrados vivos en un año específico
                    6 - Listar libros por idioma
                    0 - Salir
                    """);
                System.out.print("Seleccione una opción: ");

                // Validar la entrada para asegurar que sea un número entero
                if (scanner.hasNextInt()) {
                    opcion = scanner.nextInt();
                    scanner.nextLine(); // Limpia el buffer

                    switch (opcion) {
                        case 1 -> buscarLibroPorTitulo(scanner);
                        case 2 -> buscarLibroPorAutor(scanner);
                        case 3 -> listarLibros();
                        case 4 -> listarAutores();
                        case 5 -> listarAutoresVivosEnAno(scanner);
                        case 6 -> listarLibrosPorIdioma(scanner);
                        case 0 -> System.out.println("Saliendo...");
                        default -> System.out.println("Opción no válida. Por favor, intente de nuevo.");
                    }
                } else {
                    System.out.println("Entrada no válida. Por favor, ingrese un número.");
                    scanner.nextLine(); // Limpia el buffer para evitar un bucle infinito
                }
            } while (opcion != 0);
        }
    }

    private void buscarLibroPorTitulo(Scanner sc) {
        System.out.print("Ingrese el título del libro: ");
        String titulo = sc.nextLine().trim();

        List<LibroDTO> librosDTO = obtenerLibrosDeAPI(titulo);

        if (!librosDTO.isEmpty()) {
            System.out.println("Libros encontrados:");
            librosDTO.forEach(dto -> {
                Optional<Libro> libroExistenteOpt = libroRepository.findByTituloIgnoreCase(dto.titulo());
                if (libroExistenteOpt.isPresent()) {
                    Libro libroExistente = libroExistenteOpt.get();
                    actualizarLibro(libroExistente, dto);
                } else {
                    registrarLibro(dto);
                }
            });
        } else {
            System.out.println("No se encontraron libros que coincidan con el título ingresado.");
        }
    }

    private void buscarLibroPorAutor(Scanner sc) {
        System.out.print("Ingrese el nombre del autor: ");
        String autorNombre = sc.nextLine().trim();

        // Llama a la API y obtiene los libros filtrados por autor
        List<LibroDTO> librosDTO = obtenerLibrosDeAPI(autorNombre);

        if (!librosDTO.isEmpty()) {
            System.out.println("Libros encontrados para el autor:");
            librosDTO.forEach(dto -> {
                // Verifica si el libro ya existe en la base de datos
                Optional<Libro> libroExistenteOpt = libroRepository.findByTituloIgnoreCase(dto.titulo());
                if (libroExistenteOpt.isPresent()) {
                    Libro libroExistente = libroExistenteOpt.get();
                    actualizarLibro(libroExistente, dto);
                } else {
                    registrarLibro(dto);
                }
            });
        } else {
            System.out.println("No se encontraron libros para el autor ingresado.");
        }
    }

    // Lógica ajustada para obtener libros filtrados exclusivamente por autores
    private List<LibroDTO> obtenerLibrosDeAPI(String terminoBusqueda) {
        try {
            String url = BASE_URL + "?search=" + URLEncoder.encode(terminoBusqueda, StandardCharsets.UTF_8);
            String json = consumoAPI.obtenerDatos(url);
            RespuestaLibrosDTO respuesta = convierteDatos.obtenerDatos(json, RespuestaLibrosDTO.class);

            // Filtrar libros que coincidan con el autor ingresado
            return respuesta.libro().stream()
                    .filter(libro -> libro.autores().stream()
                            .anyMatch(autor -> autor.nombre().equalsIgnoreCase(terminoBusqueda)))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error al obtener datos de la API", e);
            return Collections.emptyList();
        }
    }

    // Nueva implementación del filtro por autor
    private List<LibroDTO> obtenerLibrosDeAPIPorAutor(String autorNombre) {
        try {
            String url = BASE_URL + "?search=" + URLEncoder.encode(autorNombre, StandardCharsets.UTF_8);
            String json = consumoAPI.obtenerDatos(url);
            RespuestaLibrosDTO respuesta = convierteDatos.obtenerDatos(json, RespuestaLibrosDTO.class);

            // Filtrar libros por autor coincidente
            return respuesta.libro().stream()
                    .filter(libro -> libro.autores().stream()
                            .anyMatch(autor -> autor.nombre().equalsIgnoreCase(autorNombre)))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error al obtener datos de la API para autor", e);
            return Collections.emptyList();
        }
    }

    private void registrarLibro(LibroDTO dto) {
        Libro libro = dto.toLibro();
        libro.setIdiomas(libro.getIdiomas().replace("[", "").replace("]", ""));
        libro.setAutor(getOrCreateAutor(dto));
        libroRepository.save(libro);
        mostrarDetallesLibro(libro);
    }

    private void actualizarLibro(Libro libroExistente, LibroDTO dto) {
        Libro libroNuevo = dto.toLibro();
        libroNuevo.setId(libroExistente.getId());
        libroNuevo.setIdiomas(libroNuevo.getIdiomas().replace("[", "").replace("]", ""));
        libroNuevo.setAutor(getOrCreateAutor(dto));

        if (debeActualizar(libroExistente, libroNuevo)) {
            libroRepository.save(libroNuevo);
            System.out.println("Se actualizó el libro con nueva información:");
            mostrarDetallesLibro(libroNuevo);
        } else {
            System.out.println("El libro ya existe y no hay más información para actualizar:");
            mostrarDetallesLibro(libroExistente);
        }
    }

    private Autor getOrCreateAutor(LibroDTO dto) {
        String nombreAutor = dto.autores().get(0).nombre();
        return autorRepository.findByNombre(nombreAutor)
                .orElseGet(() -> autorRepository.save(dto.autores().get(0).toAutor()));
    }

    private boolean debeActualizar(Libro existente, Libro nuevo) {
        boolean descargasMayores = nuevo.getNumeroDescargas() > existente.getNumeroDescargas();
        boolean idiomaDistinto = !nuevo.getIdiomas().equals(existente.getIdiomas());
        boolean autorDistinto = !Objects.equals(nuevo.getAutor(), existente.getAutor());
        return descargasMayores || idiomaDistinto || autorDistinto;
    }

    private void listarLibros() {
        List<Libro> libros = libroRepository.findAll();
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

    private void listarAutores() {
        List<Autor> autores = autorRepository.findAllConLibros();
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

        List<Autor> vivos = autorRepository.findAutoresVivosEnAnoConLibros(ano);
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
            libroRepository.findByIdiomas(idioma).forEach(this::mostrarDetallesLibro);
        } else {
            System.out.println("Idioma no válido.");
        }
    }
}
