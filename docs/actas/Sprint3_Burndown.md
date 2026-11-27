# Sprint 3 — Burndown y registro de integración

**Periodo:** lunes 23/11/2026 al viernes 27/11/2026 · **Compromiso:** 19 puntos (HG-01 5, HU-06 3, HU-07 3, HU-08 8) · **Versión:** `v0.3`

Solo se descuentan las historias **completas** (todas sus tareas fusionadas y verificadas), no las tareas sueltas.

| Día | lun 23 | mar 24 | mié 25 | jue 26 | vie 27 |
|---|---|---|---|---|---|
| Ideal | 19 | 14 | 10 | 5 | 0 |
| Real | 19 | 19 | 19 | 19 | 0 |

**Lectura:** la línea real se quedó plana hasta el viernes. Hasta el jueves solo había avance de I2 (PR #30 de la ventana principal y los borradores #46 y #47 del panel y el diálogo de miembros), y ninguna historia estaba completa: las tareas de lógica de I1 (#31, #33, #40, #41) e I3 (#32, #39) no tenían rama ni commits. El viernes 27/11 se activó el plan B (sección 14 de la guía) y se completaron las cuatro historias.

## Plan B y cambios en el flujo

- **Redistribución:** I2 tomó con su propio usuario las tareas de I1 (#31, #33, #37, #40, #41) e I3 (#32, #38, #39, #42, #43). Cada issue tiene un comentario con el motivo.
- **Rama `develop2`:** `develop` está protegida por el ruleset «Proteccion develop» (1 aprobación obligatoria) y no había otro integrante disponible para revisar. Se creó `develop2` desde `develop` como rama temporal de integración. **Después del sprint** hay que integrar `develop2` en `develop` (con la revisión de otro integrante) y borrar `develop2`.
- **Revisiones:** los PR #48 a #58 se fusionaron en `develop2` sin revisión de otro integrante; cada PR lo indica.

## Integración en `develop2`

| PR | Historia · tarea | Issue |
|---|---|---|
| #30 | HG-01 · ventana principal (fusionado en `develop` por I3) | #34 |
| #46 (+ #47) | HG-01 · T2 PanelMiembros y T3 DialogoMiembro | #35, #36 |
| #48 | HG-01 · T1 actualizar y dar de baja | #31 |
| #49 | HG-01 · Dar de baja, filtro y edición con `actualizarMiembro` | #35, #36 |
| #50 | HU-06 · T1 Entrenador y Gimnasio | #32 |
| #51 | HU-07 · T1 asignarEntrenador (CP-10) | #39 |
| #52 | HU-08 · T1 Rutina y DetalleRutina | #33 |
| #53 | HU-08 · T2 y T3 crearRutina y pruebas CP-11 a CP-13 | #40, #41 |
| #54 | HG-01 · T5 PanelPlanesPagos | #37 |
| #55 | HU-06 · T2 PanelEntrenadores | #38 |
| #56 | HU-07 · T2 asignar desde la GUI | #42 |
| #57 | HU-08 · T4 PanelRutinas | #43 |
| #58 | HG-01 · T4 EstadoCellRenderer y paneles conectados | #44 |

## Verificación final

- Compila sin advertencias (`javac --release 17 -Xlint:all`).
- `PruebasLogicaNegocio`: **135 correctas, 0 con fallo** (81 al inicio del sprint).
- Guion de la demo (guía 11.2) recorrido sobre la ventana real: 15 comprobaciones correctas, incluido el total S/ 335.16 y los colores por estado.
- Capturas en `capturas/sprint3/`.
