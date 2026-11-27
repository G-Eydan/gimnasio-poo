# Sprint 3 — Acta de Sprint Review

**Fecha:** viernes 27/11/2026 · **Versión:** `v0.3`
**Asistentes:** David Jove (I2, Scrum Master).
**Ausentes:** Luis Vera (I1, Product Owner) y Eydan Mayna (I3, QA), sin actividad durante el sprint. Como en el Sprint 2, la aceptación se hizo contra los criterios de aceptación (guía 11.3), esta vez solo por el SM: queda **provisional** hasta que el PO y QA la revisen.

**Objetivo del sprint:** "Que todo el sistema se use desde la interfaz gráfica: miembros, planes y pagos, entrenadores y rutinas."
**Resultado:** objetivo cumplido.

## Historias

| Historia | Pts | Resultado | Observación |
|---|---|---|---|
| HG-01 Ventana principal con pestañas | 5 | Aceptada (provisional) | Menú, pestañas y barra de estado con `Timer` (#30). Miembros: `JTable`, búsqueda en vivo, Nuevo / Editar / Dar de baja y formulario en `JDialog` (#46, #47, #48, #49). Planes y Pagos con desglose en vivo: estudiante + PT + tarjeta = S/ 335.16 (#54). Filas verde / amarillo / rojo y gris para dados de baja (#58). |
| HU-06 Registrar entrenadores | 3 | Aceptada (provisional) | Especialidades con `JCheckBox`, turno con `JComboBox`, grilla 7 × 3 copiada al arreglo 2D `boolean` y filtro por especialidad (#50, #55). |
| HU-07 Asignar entrenadores | 3 | Aceptada (provisional) | Solo miembros con membresía activa; máximo 15 (`EntrenadorSinCupoException`, CP-10); al reasignar sale de la lista del anterior (#51, #56). |
| HU-08 Crear rutinas | 8 | Aceptada (provisional) | Rutina con nombre y objetivo para un miembro activo; ejercicios del catálogo con `JSpinner` en los rangos de RN-09; máximo 20 (`RutinaLlenaException`, CP-11); quitar sin huecos (CP-12); ordenar por día con inserción; la anterior pasa al historial (CP-13) (#52, #53, #57). |

**Puntos aceptados:** 19 de 19 comprometidos (provisional).

## Criterios de aceptación (guía 11.3)

| Historia | Criterio | Cumple |
|---|---|---|
| HG-01 | JFrame con JMenuBar, JTabbedPane y barra de estado | ✔ |
| HG-01 | Miembros: JTable, búsqueda en vivo, Nuevo / Editar / Dar de baja, formulario en JDialog | ✔ |
| HG-01 | Planes y Pagos: contratar o renovar con el importe recalculado en vivo | ✔ |
| HG-01 | Filas verde / amarillo / rojo según el estado | ✔ |
| HU-06 | Formulario con especialidades (JCheckBox) y turno (JComboBox) | ✔ |
| HU-06 | Disponibilidad en grilla 7 × 3 (arreglo 2D boolean) | ✔ |
| HU-06 | Búsqueda de entrenadores por especialidad | ✔ |
| HU-07 | Asignar a un miembro con membresía activa | ✔ |
| HU-07 | Máximo 15 miembros (EntrenadorSinCupoException) | ✔ |
| HU-07 | Reasignar: sale de la lista del entrenador anterior | ✔ |
| HU-08 | Miembro activo + rutina con nombre y objetivo | ✔ |
| HU-08 | Ejercicios del catálogo con día, series 1–10, repeticiones 1–50, peso y descanso | ✔ |
| HU-08 | Máximo 20 ejercicios (RutinaLlenaException) | ✔ |
| HU-08 | Quitar un ejercicio y ordenar por día | ✔ |
| HU-08 | La rutina anterior queda en el historial | ✔ |

## Demo realizada (guion de la guía 11.2)
1. Login como `admin`: 6 pestañas y los 3 menús.
2. Miembros → Nuevo → estudiante «Ana Quispe Mamani», DNI 74125896.
3. Búsqueda «quis» en vivo: queda una fila.
4. Planes y Pagos → DNI 74125896 → PT → Tarjeta: el desglose muestra **S/ 335.16** antes de registrar; con efectivo baja a S/ 325.40.
5. Miembros: fila en verde, ACTIVO, vence el 26/02/2027. Las demás filas muestran amarillo (por vencer), rojo (vencido), blanco (sin membresía) y gris (dado de baja).
6. Entrenadores → «Carlos Ramos», Musculación y Funcional, turno Mañana, disponible lunes, miércoles y viernes en la mañana.
7. Filtro por especialidad Funcional: aparece Carlos.
8. Asignar miembro → Ana: contador 1 / 15 y columna Entrenador en Miembros.
9. Rutinas → Ana → «Fuerza inicial», FUERZA → 5 ejercicios en días distintos → Ordenar por día → quitar uno: 4 / 20.
10. Segunda rutina para Ana: la anterior pasa al historial (CP-13).
11. `PruebasLogicaNegocio`: CP-10 a CP-13 correctas.
12. Archivo → Cerrar sesión e ingresar como `entrenador`: solo Miembros y Rutinas, sin Reportes (CP-18).

Capturas en `capturas/sprint3/`.

**Verificación técnica:** compila sin advertencias (`-Xlint:all`); `PruebasLogicaNegocio` **135 correctas, 0 con fallo** (81 al inicio del sprint). Tag `v0.3` sobre `main` (PR #60).

## Cambios al backlog
- Sin historias rechazadas ni devueltas al backlog.
- Tareas redistribuidas por el plan B (guía, sección 14): las de I1 (#31, #33, #37, #40, #41) e I3 (#32, #38, #39, #42, #43) las hizo I2; cada issue lo indica.
- Integración en la rama temporal `develop2` (no había revisor disponible para `develop`). **Pendiente después del sprint:** integrar `develop2` en `develop` con la revisión de otro integrante y borrar `develop2`.
- Para el Sprint 4: Dashboard y Reportes (pestañas con aviso «Sprint 4»), archivos (menú Archivo), vincular usuario y entrenador (decisión D4) y persistencia, porque hoy los datos solo viven en memoria y los planes se crean en cada ejecución.
- Decisiones del planning tomadas por el SM sin el PO (provisionales): D3 (sin menú de consola en paralelo), D4 (crean rutinas ADMINISTRADOR y ENTRENADOR), D5 (no se editan DNI ni tipo), D6 (baja lógica) y D7 (el miembro conserva su entrenador al vencer).
