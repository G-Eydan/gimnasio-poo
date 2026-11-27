# Sprint 3 — Retrospectiva

**Fecha:** viernes 27/11/2026 · **Modera:** David Jove (I2, Scrum Master)
**Asistentes:** David Jove (I2). Luis Vera (I1) y Eydan Mayna (I3) ausentes.

Los hechos salen del historial de GitHub. Como solo asistió el SM, la mejora para el Sprint 4 queda como **propuesta**: se confirma con el equipo en el planning del Sprint 4.

## ¿Se cumplió la mejora del Sprint 2?

Mejora acordada: proteger `develop` y llegar a **0 commits directos y 0 PR fusionados sin aprobación**.

| Medida | Resultado |
|---|---|
| Protección de `develop` | Sí: ruleset «Proteccion develop» (1 aprobación), creado por I3 el 20/11. |
| Commits directos a `develop` / `develop2` | 0 / 0 |
| PR fusionados con aprobación | 1 (#30, aprobado por I3) |
| PR fusionados sin aprobación | 15: #47 (fusionado por I3 en la rama de #46, que no estaba protegida) y #46, #48 a #60 (fusionados por I2 en `develop2` y `main` sin revisor disponible) |

**Resultado: Parcial.** La protección funcionó: no hubo commits directos y `develop` solo recibió el PR aprobado. Pero, al no haber nadie que revisara, la integración se hizo en `develop2`, sin aprobaciones.

## Hechos del sprint
- 19 de 19 puntos terminados (aceptación provisional del SM).
- Los 15 issues (tareas) del milestone cerrados, cada uno con su PR; tablero con las 15 tarjetas en «Hecho», sprint S3.
- Pruebas: de 81 a **135** correctas.
- Burndown plano en 19 hasta el jueves (`Sprint3_Burndown.md`): ninguna historia estaba completa antes del último día.
- I1 e I3 no registraron commits ni PR en el sprint; el viernes se activó el plan B y I2 hizo las 10 tareas de ambos con su propio usuario.
- I3 estuvo activo el viernes de madrugada: aprobó y fusionó #30 y dejó revisiones preliminares en #46 y #47. Fusionó #47 dentro de la rama de #46, y #46 quedó bloqueado por falta de aprobación.
- `Closes #n` no cierra issues en PR hacia ramas que no son la principal: los issues se cerraron a mano con un comentario.

## Velero

**Viento (lo que impulsó)**
- Las firmas públicas de la tabla 7.1.1: con ellas, I2 pudo programar los paneles antes de tener la lógica y conectarlos después.
- La guía del sprint con tareas, ramas y criterios ya definidos.
- Las plantillas de issue y PR, y las pruebas sin JUnit en `PruebasLogicaNegocio`.

**Ancla (lo que frenó)**
- La falta de participación de I1 e I3: 10 de las 15 tareas no se empezaron hasta el último día.
- Los PR de I2 bloqueados por dependencias (#31) y luego por la falta de aprobación.
- La revisión del martes no activó el plan B a tiempo: se activó el viernes.

**Rocas (riesgos para el Sprint 4)**
- Repetir la concentración de trabajo en una sola persona.
- `develop` y `develop2` divergen: hay que integrarlas antes de empezar el Sprint 4.
- Temas nuevos (archivos, gráficos con `paintComponent`) y la carga de exámenes.
- Los datos solo viven en memoria: la persistencia del Sprint 4 es necesaria para la demo final.

**Isla (adónde queremos llegar)**
- Entregar `v1.0` el 10/12 con todo funcionando en la GUI, con persistencia y con la participación de los tres en el historial.

## Mejora propuesta para el Sprint 4

**Qué:** avance visible a mitad del sprint. El martes, cada integrante debe tener al menos un PR (aunque sea en borrador) de cada tarea que tenga asignada. Si no lo tiene, el miércoles el SM redistribuye esa tarea en la revisión de mitad de sprint, sin esperar al último día.
**Medida:** el martes del Sprint 4, al menos un PR por integrante; 0 historias que se completen recién el último día.
**Responsable:** I2 (SM) revisa el martes por la noche y lo anota en el grupo.

## Acciones antes del Sprint 4
1. Integrar `develop2` en `develop` con un PR revisado por otro integrante y borrar `develop2` (I2 abre el PR; I3 o I1 aprueba).
2. Que el PO revise la aceptación provisional y las decisiones D3 a D7.
3. Confirmar esta mejora con el equipo en el planning del Sprint 4.
