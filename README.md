# GymPro — Sistema de Administración de un Gimnasio

Proyecto final del curso *Desarrollo de Software Orientado a Objetos* (2501211)
Escuela Profesional de Ingeniería de Sistemas — Universidad Nacional de San Agustín de Arequipa, 2026-B.

GymPro es una aplicación de escritorio en Java para administrar un gimnasio: miembros, entrenadores, planes, membresías, pagos, rutinas de entrenamiento y reportes.

## Estado actual

| Versión | Sprint | Qué se puede hacer |
|---|---|---|
| `v0.1` | Sprint 1 | Menú de consola: registrar miembros (regular, estudiante, corporativo), buscarlos por DNI, listarlos, y crear y listar planes (mensual, trimestral, anual) con su importe. |

La interfaz gráfica (Swing), los archivos y la base de datos se agregan en los siguientes sprints (ver el [Plan Scrum](docs/Plan_Scrum_GymPro.md)).

## Requisitos

- **JDK 17 o superior.** Para comprobarlo: `java -version` y `javac -version`.
- **Git**, para clonar el repositorio.
- **IntelliJ IDEA** (recomendado; trae Maven incluido) u otro IDE con soporte Maven. También se puede compilar desde la terminal sin IDE.

## Obtener el código

```bash
git clone https://github.com/G-Eydan/gimnasio-poo.git
cd gimnasio-poo
```

La rama `main` tiene la última versión estable.

## Ejecutar

### Opción 1: IntelliJ IDEA

1. **File → Open** y elegir la carpeta `gimnasio-poo`. IntelliJ detecta el `pom.xml` como proyecto Maven.
2. Si pide un SDK, elegir un JDK 17 o superior (**File → Project Structure → Project → SDK**).
3. Si el código aparece en rojo, abrir la pestaña **Maven** (a la derecha) y pulsar **Reload All Maven Projects**.
4. Abrir `src/main/java/gympro/app/Main.java` y pulsar **Run** (el triángulo verde junto a `main`).

El menú aparece en la consola de IntelliJ.

### Opción 2: terminal, sin IDE

Desde la carpeta del proyecto.

**Windows (PowerShell):**

```powershell
javac --release 17 -encoding UTF-8 -d out (Get-ChildItem -Recurse src\main\java -Filter *.java).FullName
java -cp out gympro.app.Main
```

**Linux, macOS o Git Bash:**

```bash
javac --release 17 -encoding UTF-8 -d out $(find src/main/java -name "*.java")
java -cp out gympro.app.Main
```

### Opción 3: Maven

```bash
mvn compile
java -cp target/classes gympro.app.Main
```

## Uso del menú

```
========== GymPro ==========
1. Registrar miembro
2. Buscar miembro por DNI
3. Listar miembros
4. Crear plan
5. Listar planes
0. Salir
```

Ejemplo de recorrido (demo del Sprint 1):

1. **Opción 4** tres veces para crear los planes: tipo `1` código `PM`, tipo `2` código `PT` y tipo `3` código `PA`, todos con precio `120`.
2. **Opción 5**: muestra los importes S/ 120.00, S/ 324.00 y S/ 1152.00.
3. **Opción 1** para registrar un miembro de cada tipo. DNI de 8 dígitos, teléfono de 9 dígitos y email con `@`; la fecha de nacimiento (`dd/mm/aaaa`) es opcional.
4. **Opción 1** con DNI `1234`: se muestra el mensaje de error y el menú continúa.
5. **Opción 2** con un DNI registrado: muestra código, datos, tipo, descuento y estado.
6. **Opción 3**: lista los miembros ordenados por apellido.

Los datos se guardan solo en memoria mientras el programa está abierto (la persistencia llega en el Sprint 4).

## Pruebas

Las pruebas están en `src/test/java` y no necesitan JUnit: cada clase de prueba tiene un `main` que muestra `OK` o `FALLO` por caso y un resumen al final.

**IntelliJ:** abrir `src/test/java/gympro/PruebasLogicaNegocio.java` y pulsar **Run**.

**Terminal (Windows, PowerShell)**, después de compilar el programa como en la opción 2:

```powershell
javac --release 17 -encoding UTF-8 -cp out -d out-test (Get-ChildItem -Recurse src\test\java -Filter *.java).FullName
java -cp "out;out-test" gympro.PruebasLogicaNegocio
```

**Terminal (Linux o macOS):**

```bash
javac --release 17 -encoding UTF-8 -cp out -d out-test $(find src/test/java -name "*.java")
java -cp "out:out-test" gympro.PruebasLogicaNegocio
```

En Git Bash sobre Windows el separador del classpath también es `;` (`"out;out-test"`).

Resultado esperado: `Resultado: 50 correctas, 0 con fallo.`

## Base de datos

Pendiente para el Sprint 4. Aquí se agregarán las instrucciones para crear la base de datos con `sql/gympro.sql` y configurar la conexión.

## Estructura del proyecto

```
gimnasio-poo/
 ├── docs/             Especificación y Plan Scrum
 ├── Diagramas_UML/    Diagramas de clases
 ├── sql/              Script de la base de datos
 ├── lib/              Conector JDBC
 ├── datos/            Archivos CSV, binarios y respaldos
 ├── reportes/         Comprobantes y reportes .txt generados
 └── src/
      ├── main/java/gympro/
      │    ├── app/                    Main, MenuConsola
      │    ├── modelo/persona/         Persona, Miembro y subclases, Entrenador
      │    ├── modelo/membresia/       Plan y subclases, Membresia, Pago
      │    ├── modelo/entrenamiento/   Ejercicio, DetalleRutina, Rutina
      │    ├── modelo/enums/           Enumeraciones
      │    ├── servicio/               Gimnasio
      │    ├── util/                   Ordenamiento, Busqueda, Validador
      │    ├── excepciones/            GimnasioException y subclases
      │    ├── persistencia/           Archivos, DAO y JDBC (Sprint 4)
      │    └── gui/                    Ventanas y paneles Swing (Sprint 3)
      └── test/java/gympro/            Pruebas
```

## Equipo: *Los terrores del ArrayList*

| Integrante | Rol Scrum | Área técnica |
|---|---|---|
| *(nombre)* — I1 | Product Owner | Lógica de negocio |
| *(nombre)* — I2 | Scrum Master | Interfaz gráfica e integración |
| *(nombre)* — I3 | Desarrollador | Modelo, persistencia y pruebas (QA) |

## Documentación

- [Especificación del sistema](docs/Especificacion_GymPro.md)
- [Plan Scrum](docs/Plan_Scrum_GymPro.md)

## Flujo de trabajo

Ramas `main` (estable, se actualiza al final de cada sprint con un tag `v0.x`), `develop` (integración) y `feature/<ID>-<tarea>` o `fix/<descripción>`.
Commits con formato `<ID>: <qué se hizo>`. Detalle en el Plan Scrum, sección 11.
