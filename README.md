# GymPro — Sistema de Administración de un Gimnasio

Proyecto final del curso *Desarrollo de Software Orientado a Objetos* (2501211)
Escuela Profesional de Ingeniería de Sistemas — Universidad Nacional de San Agustín de Arequipa, 2026-B.

GymPro es una aplicación de escritorio en Java para administrar un gimnasio: miembros, entrenadores, planes, membresías, pagos, rutinas de entrenamiento y reportes.

## Estado actual

| Versión | Sprint | Qué se puede hacer |
|---|---|---|
| `v0.1` | Sprint 1 | Menú de consola: registrar miembros (regular, estudiante, corporativo), buscarlos por DNI, listarlos, y crear y listar planes (mensual, trimestral, anual) con su importe. |
| `v0.2` | Sprint 2 | Inicio de sesión gráfico con 3 roles y ventana principal con las pestañas y menús de cada rol. En consola: contratar y renovar membresías con el desglose del pago, ver la ficha del miembro (estado, vencimiento, días restantes e historial) y listar las membresías vencidas y por vencer. |
| `v0.3` | Sprint 3 | Todo desde la interfaz gráfica: miembros (registrar, editar, dar de baja, búsqueda en vivo y colores por estado), planes y pagos (desglose en vivo y comprobante), entrenadores (especialidades, turno, disponibilidad y asignación de miembros con cupo de 15) y rutinas (hasta 20 ejercicios, ordenar por día e historial). |

Dashboard, reportes, archivos y base de datos se agregan en los siguientes sprints (ver el [Plan Scrum](docs/Plan_Scrum_GymPro.md)).

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

Primero se abre la ventana de inicio de sesión; después de ingresar se abre la ventana principal y el menú aparece en la consola de IntelliJ.

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

## Inicio de sesión

Al ejecutar `Main` se abre la ventana **Iniciar sesión**. Usuarios de prueba:

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | `admin123` | Administrador |
| `recepcion` | `recepcion123` | Recepcionista |
| `entrenador` | `entrenador123` | Entrenador |

Si el usuario o la contraseña son incorrectos aparece un mensaje de error. Al ingresar se abre la **ventana principal**, que muestra solo las pestañas y menús del rol:

| Rol | Pestañas | Menús |
|---|---|---|
| Administrador | Dashboard, Miembros, Planes y Pagos, Entrenadores, Rutinas, Reportes | Archivo (completo), Base de datos, Ayuda |
| Recepcionista | Dashboard, Miembros, Planes y Pagos | Archivo (Cerrar sesión y Salir), Ayuda |
| Entrenador | Miembros, Rutinas (no ve Reportes) | Archivo (Cerrar sesión y Salir), Ayuda |

## Uso de la interfaz gráfica

Todo se usa desde las pestañas de la ventana principal (el menú de consola ya no se abre; la clase `MenuConsola` se conserva). Los datos se guardan solo en memoria mientras el programa está abierto (la persistencia llega en el Sprint 4).

| Pestaña | Qué se puede hacer |
|---|---|
| **Miembros** | Tabla con DNI, nombre, tipo, plan, vencimiento, días, estado y entrenador. Búsqueda en vivo por DNI o nombre, filtro por estado (incluye «Dados de baja») y orden por columna. Botones **Nuevo** y **Editar** (formulario modal; al editar no se cambian el DNI ni el tipo), **Dar de baja** (baja lógica, con confirmación) y **Ver ficha** (también con doble clic). Filas en verde (activo), amarillo (por vencer), rojo (vencido), blanco (sin membresía) y gris (dado de baja). |
| **Planes y Pagos** | Tabla de planes y formulario para crear planes. Contratar o renovar: buscar el DNI, elegir el plan y el método de pago; el desglose (importe, descuento, matrícula, recargo y total) se recalcula al instante. **Registrar pago** muestra el comprobante; si el miembro ya tiene una membresía vigente, ofrece renovarla. Historial de pagos. |
| **Entrenadores** | Registrar entrenadores con especialidades (casillas), turno y disponibilidad semanal en una grilla de 7 días × 3 turnos. Tabla de entrenadores con filtro por especialidad; al seleccionar uno se ven sus datos. **Asignar miembro** (solo miembros con membresía activa, máximo 15 por entrenador) y lista de sus miembros asignados. |
| **Rutinas** | Elegir un miembro con membresía activa y crear una rutina con nombre y objetivo (la anterior pasa al historial). Agregar ejercicios del catálogo (filtrable por grupo muscular) con día, series (1–10), repeticiones (1–50), peso y descanso; quitar y ordenar por día. La barra muestra cuántos ejercicios lleva la rutina (máximo 20). |
| Dashboard y Reportes | Llegan en el Sprint 4. |

Menú **Archivo → Cerrar sesión** vuelve a la ventana de inicio de sesión; **Salir** (o `Ctrl+Q`) cierra el programa después de confirmar.

Ejemplo de recorrido (demo del Sprint 3):

1. Ingresar como `admin` / `admin123`.
2. **Planes y Pagos**: crear los planes `PM` (mensual) y `PT` (trimestral) con precio `120`.
3. **Miembros → Nuevo**: registrar una estudiante (DNI de 8 dígitos, teléfono de 9 dígitos, email con `@`, universidad y código).
4. **Planes y Pagos**: buscar su DNI, elegir `PT` y **Tarjeta**: el total es **S/ 335.16** (importe S/ 324.00, descuento S/ 48.60, matrícula S/ 50.00, recargo S/ 9.76). Al cambiar a efectivo el total baja a S/ 325.40. **Registrar pago**.
5. **Miembros**: la fila aparece en verde (ACTIVO).
6. **Entrenadores**: registrar un entrenador con especialidades, turno y disponibilidad; seleccionarlo y **Asignar miembro**.
7. **Rutinas**: elegir al miembro, crear una rutina, agregar ejercicios, **Ordenar por día** y **Quitar** uno.
8. **Archivo → Cerrar sesión** e ingresar como `entrenador`: solo ve Miembros y Rutinas.

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

Resultado esperado: `Resultado: 135 correctas, 0 con fallo.` Cubren, entre otros, CP-01 a CP-13 y CP-18: importes, contratar y renovar, vencimientos, actualizar y dar de baja, entrenadores, cupo de 15 (CP-10), rutina llena (CP-11), eliminar sin huecos (CP-12) e historial de rutinas (CP-13).

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
      │    └── gui/                    Login, ventana principal, paneles, diálogo y modelos de tabla
      └── test/java/gympro/            Pruebas
```

## Equipo: *Los terrores del ArrayList*

| Integrante | Rol Scrum | Área técnica |
|---|---|---|
| Luis Alberto Vera Salcedo — I1 | Product Owner | Lógica de negocio |
| David Rodrigo Jove Choquecota — I2 (líder) | Scrum Master | Interfaz gráfica e integración |
| Eydan Fabricio Mayna Mollapaza — I3 | Desarrollador | Modelo, persistencia y pruebas (QA) |

## Documentación

- [Especificación del sistema](docs/Especificacion_GymPro.md)
- [Plan Scrum](docs/Plan_Scrum_GymPro.md)
- Diagramas de clases UML (actualizados al Sprint 3; en verde las clases ya implementadas): [completo](Diagramas_UML/Diagrama_UML_Gimnasio.svg) y [simplificado](Diagramas_UML/Diagrama_UML_Gimnasio_Simplificado.svg). Las fuentes `.puml` están en la misma carpeta.

## Flujo de trabajo

Ramas `main` (estable, se actualiza al final de cada sprint con un tag `v0.x`), `develop` (integración) y `feature/<ID>-<tarea>` o `fix/<descripción>`.
Commits con formato `<ID>: <qué se hizo>`. Detalle en el Plan Scrum, sección 11.
