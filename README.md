# 📚 **Literalura: Gestión de Libros y Autores**

¡Bienvenido a **Literalura**! Este programa es una herramienta interactiva que te permite explorar, gestionar y analizar datos de libros y autores de forma sencilla. Desde realizar búsquedas en una API hasta guardar información en una base de datos, Literalura combina funcionalidad y simplicidad.

---

## ✨ **Funcionalidades Principales**

### 🔎 **1. Búsqueda de Libros por Título**
- Consulta libros directamente desde la API.
- Almacena automáticamente en la base de datos información clave:
  - **Título**, **Autor**, **Idioma**, **Número de descargas**.
- **Actualiza automáticamente** la información si los nuevos datos son más completos.

### 📖 **2. Listado de Libros Registrados**
- Muestra un listado completo de los libros almacenados.
- Incluye detalles como:
  - **Título**  
  - **Autor**  
  - **Idioma**  
  - **Número de descargas**.

### 🌍 **3. Consulta de Libros por Idioma**
- Filtra libros según su idioma:  
  - Idiomas disponibles: `es`, `en`, `fr`, `pt`.
- Muestra estadísticas, como la **cantidad de libros** en cada idioma.

### 🖋️ **4. Gestión de Autores**
- Guarda información de los autores:
  - **Nombre**, **Año de nacimiento**, **Año de fallecimiento**.
- Permite:
  - Listar todos los autores registrados.  
  - Consultar **autores vivos** en un año específico.

### 💾 **5. Persistencia de Datos**
- Integra una base de datos **PostgreSQL** para guardar libros y autores.
- Asegura la relación entre libros y autores, manteniendo la **integridad referencial**.

### 🖥️ **6. Interfaz Interactiva en Consola**
- Presenta un **menú dinámico** con las siguientes opciones:
  - **1:** Buscar libros por título.  
  - **2:** Listar libros registrados.  
  - **3:** Listar autores registrados.  
  - **4:** Listar autores vivos en un año.  
  - **5:** Listar libros por idioma.  
  - **0:** Salir.
- Respuestas claras y detalladas a las consultas del usuario.

---

## 🗂️ **Datos Manejados**

### 📚 **Libros**
- **Título**.  
- **Autor** (único por libro, para simplificar la gestión).  
- **Idioma** (primer idioma de la lista recibida).  
- **Número de descargas**.

### 🖋️ **Autores**
- **Nombre**.  
- **Año de nacimiento**.  
- **Año de fallecimiento** (puede ser nulo si el autor está vivo).

---

## ⚙️ **Interacciones Clave**

### 🌐 **Con la API**
- **Solicitudes HTTP** con `HttpClient`.
- Procesamiento de respuestas JSON usando la biblioteca **Jackson**.

### 👩‍💻 **Con el Usuario**
- Entrada y salida a través de un **menú interactivo en consola**.
- Respuesta inmediata con opciones claras.

### 🛢️ **Con la Base de Datos**
- Gestión automática de persistencia con **Spring Data JPA**.
- Inserción y actualización de datos, garantizando relaciones entre libros y autores.

---

### 🚀 **¿Qué puedes hacer con Literalura?**
Literalura no solo conecta con una API para buscar libros y autores, sino que también los almacena de manera estructurada en una base de datos, ofreciendo opciones avanzadas como:
- Consultar libros por título o idioma.
- Listar autores vivos en años específicos.
- Obtener estadísticas de los datos almacenados.

Literalura transforma datos crudos en información útil para explorar el mundo de los libros y sus autores. 📖✨
