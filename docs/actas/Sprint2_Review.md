# Sprint 2 — Acta de Sprint Review

**Fecha:** jueves 19/11/2026 · **Versión:** `v0.2`
**Asistentes:** David Jove (I2, Scrum Master) y Eydan Mayna (I3, QA).
**Ausente:** Luis Vera (I1, Product Owner). En su ausencia, la aceptación la hicieron el SM y QA contra los criterios de aceptación del Plan Scrum (sección 7.1). El PO puede revisarla al volver.

**Objetivo del sprint:** "Completar el ciclo de vida de una membresía (contratar, pagar, renovar, vencer) y entrar al sistema con una ventana de login."
**Resultado:** objetivo cumplido.

## Historias

| Historia | Pts | Resultado | Observación |
|---|---|---|---|
| HU-02 Contratar un plan y registrar el pago | 8 | Aceptada | CP-03 = S/ 335.16 con desglose (324.00 − 48.60 + 50.00 + 9.76). Contratar con una membresía vigente avisa y ofrece renovar; la renovación empieza el día siguiente al fin, queda `PENDIENTE` y no cobra matrícula (CP-06, CP-07). PRs #12. |
| HU-03 Ver estado y vencimiento | 3 | Aceptada | La ficha muestra estado, vencimiento y días restantes de la membresía vigente, e historial ordenado. PRs #13 y #14. |
| HU-05 Vencidas y por vencer | 5 | Aceptada | Estados actualizados al iniciar; opción 7 con N = 7 por defecto y configurable; listas ordenadas por fecha de fin con inserción. PRs #25 y #27. |
| HU-12 Iniciar sesión según el rol | 3 | Aceptada | Login con `JPasswordField`, 3 usuarios de prueba, error con `JOptionPane` y ventana principal con las pestañas de cada rol; el entrenador no ve Reportes (CP-18). PRs #23 y #24. |

**Puntos aceptados:** 19 de 19 comprometidos.

## Demo realizada
1. Login con `admin`, `recepcion` y `entrenador`: cada uno ve sus pestañas y menús; credenciales incorrectas muestran el error.
2. En consola: crear PT y PM, registrar una estudiante y contratar PT con tarjeta → **S/ 335.16**.
3. Contratar otra vez → aviso de membresía vigente → renovar con PM (S/ 102.00, `PENDIENTE`).
4. Ficha del miembro: `ACTIVO`, vence 18/02/2027, 91 días, historial con las dos membresías en orden.
5. Opción 7: vencidas y por vencer ordenadas por fecha de fin.

**Verificación técnica:** compila sin advertencias (`-Xlint:all`); `PruebasLogicaNegocio` 81 correctas, 0 con fallo.

## Cambios al backlog
- Sin historias rechazadas ni devueltas al backlog.
- Para decidir con el PO:
  - Un miembro que ya renovó sigue apareciendo `POR_VENCER` hasta que termina su membresía vigente.
  - Un miembro cuya única membresía está `PENDIENTE` aparece como `SIN_MEMBRESIA`.
- Las pestañas de la ventana principal están vacías; su contenido es HG-01 (Sprint 3).
