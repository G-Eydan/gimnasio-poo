# Sprint 2 — Retrospectiva

**Fecha:** jueves 19/11/2026 · **Modera:** David Jove (I2, Scrum Master)
**Asistentes:** David Jove (I2) y Eydan Mayna (I3). Luis Vera (I1) ausente.

El equipo revisó los hechos del sprint, tomados del historial de GitHub, y acordó los puntos de esta acta.

## Hechos del sprint
- 19 de 19 puntos aceptados.
- 10 Pull Requests fusionados en `develop` (#12, #13, #14, #23 a #27).
- **#12, #13 y #23 se fusionaron sin aprobación registrada.**
- **2 commits subidos directo a `develop` sin PR:** `c47e3f8` y `475bbff`. El segundo quitaba el `Collections.sort` sin agregar la inserción, y las vencidas salían desordenadas. Se corrigió en el PR #27.
- El PR #14 se abrió primero hacia `main` y se corrigió a `develop`.
- El issue #21 se cerró antes de que la tarea estuviera terminada.
- Las tareas de I1 se adelantaron en el Sprint 1 y no se verificaron hasta el cierre del Sprint 2.
- La carga de I2 se concentró al final del sprint: HU-12 y la parte de HU-05 se hicieron el último día.
- El PO no estuvo en el cierre; la aceptación la hicieron el SM y QA.

## Empezar / Dejar de / Continuar

**EMPEZAR a hacer** (acuerdos del equipo)
- Esperar el **Approve** antes de fusionar cualquier PR.
- Proteger la rama `develop` en GitHub para que solo se pueda cambiar por PR.
- Mover la tarjeta y cerrar el issue solo cuando la tarea esté fusionada y verificada.
- Avisar en el grupo si alguien no podrá estar en la review y dejar su estado escrito.

**DEJAR de hacer**
- Subir commits directo a `develop`.
- Formatear automáticamente líneas que no tienen que ver con la tarea (ensucia el diff del PR).
- Dejar para el último día las historias grandes.

**CONTINUAR**
- Una rama por tarea y PRs con descripción de "qué hace" y "cómo probar".
- Revisiones con evidencias, como la del PR #14 (informe con capturas) y la del #27 (pruebas de orden).
- Agregar pruebas para cada historia en `PruebasLogicaNegocio` (de 50 a 81 en este sprint).

## Mejora acordada para el Sprint 3
**Proteger `develop`:** Settings → Branches → regla para `develop` con "Require a pull request before merging" y 1 aprobación.
**Medida:** 0 commits directos y 0 PRs fusionados sin aprobación en el Sprint 3.
**Responsable:** I3 (dueño del repositorio).

**¿Se cumplió la mejora del sprint anterior?** No hay acta de retrospectiva del Sprint 1, así que no se puede evaluar. Desde este sprint, las actas quedan en `docs/actas/`.
