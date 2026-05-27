# acompañame — PsicologíaApp

**Trabajo de Fin de Grado (TFG)**  
Ciclo formativo: Desarrollo de Aplicaciones Multiplataforma (DAM2)  
Curso académico: 2025–2026

**Autor:** Jesús Conde Barba  
**Fecha:** 26 de mayo de 2026

---

## 2. Índice del documento

1. [Portada](#acompañame--psicologíaapp)
2. [Índice del documento](#2-índice-del-documento)
3. [Introducción](#3-introducción)
  - 3.1 [Justificación del proyecto](#31-justificación-del-proyecto)
  - 3.2 [Análisis comparativo de aplicaciones similares](#32-análisis-comparativo-de-aplicaciones-similares)
  - 3.3 [Tendencias](#33-tendencias)
  - 3.4 [Beneficios y expectativas](#34-beneficios-y-expectativas)
4. [Descripción del proyecto](#4-descripción-del-proyecto)
5. [Objetivos del proyecto](#5-objetivos-del-proyecto)
6. [Alcance del proyecto](#6-alcance-del-proyecto)
7. [Requisitos del proyecto](#7-requisitos-del-proyecto)
8. [Planificación del proyecto](#8-planificación-del-proyecto)
9. [Plan de gestión de riesgos](#9-plan-de-gestión-de-riesgos)
10. [Diseño](#10-diseño)
11. [Instalación y preparación](#11-instalación-y-preparación)
12. [Documentación de ejecución y plan de calidad](#12-documentación-de-ejecución-y-plan-de-calidad)
13. [Distribución](#13-distribución)
14. [Manuales](#14-manuales)
15. [Conclusiones](#15-conclusiones)
16. [Anexos](#16-anexos)
17. [Índice de tablas e imágenes](#17-índice-de-tablas-e-imágenes)
18. [Bibliografía y referencias](#18-bibliografía-y-referencias)

---

## 3. Introducción

### 3.1. Justificación del proyecto

**[COMPLETAR — texto del autor]**

*Espacio reservado para la narrativa personal sobre el origen de la idea (experiencia, motivación, contexto del centro formativo, etc.). Sustituir este bloque cuando el autor aporte el texto definitivo.*

**Contexto académico y técnico (documentado en el proyecto):**

El presente TFG aborda la digitalización del vínculo terapéutico entre **paciente** y **psicólogo** en un entorno móvil seguro. La práctica psicológica combina sesiones presenciales u online con un seguimiento entre consultas (registro emocional, tareas, comunicación y agenda) que, en muchos casos, sigue repartiéndose entre herramientas genéricas (mensajería, calendarios, documentos) sin integración ni control de acceso por rol.

**PsicologíaApp** — con la aplicación cliente **acompañame** y la API **bdPsicologiaApp** — nace para unificar en un solo ecosistema funciones de acompañamiento complementario a la terapia: notas personales del paciente visibles para su profesional asignado, tareas terapéuticas, citas, chat y notificaciones, con autenticación Firebase y lógica de negocio centralizada en servidor.

### 3.2. Análisis comparativo de aplicaciones similares

Se comparan dos referentes del mercado español con el alcance real de este TFG. **TherapyChat** (marca evolucionada a **Therapyside**) y **eholo** representan modelos distintos: telepsicología comercial frente a software de gestión de consulta.


| Criterio                           | Therapyside (antes TherapyChat)                  | eholo                                           | acompañame (este TFG)                                                                          |
| ---------------------------------- | ------------------------------------------------ | ----------------------------------------------- | ---------------------------------------------------------------------------------------------- |
| **Público principal**              | Persona que busca psicólogo online               | Psicólogo o centro de psicología                | Paciente y psicólogo ya vinculados                                                             |
| **Modelo de negocio**              | Marketplace + sesiones de pago por videollamada  | SaaS de gestión de consulta (suscripción)       | Proyecto académico; sin monetización                                                           |
| **Videollamadas terapéuticas**     | Sí (núcleo del servicio)                         | Sí (videollamada Eholo o Google Meet integrada) | **No implementado**                                                                            |
| **Facturación / normativa fiscal** | Planes y precios comerciales                     | Facturación, Verifactu, Eholo Pay               | **No implementado**                                                                            |
| **Notas / diario del paciente**    | No es el foco del producto                       | Historial clínico del profesional               | Notas creadas por el paciente; lectura del psicólogo asignado                                  |
| **Tareas terapéuticas**            | No destacado                                     | Cuestionarios y automatizaciones                | CRUD de tareas psicólogo → paciente; estados realizada/aceptada                                |
| **Chat**                           | Sí (comunicaciones encriptadas en app comercial) | Contexto de consulta y sesión                   | Firebase Realtime Database + notificación push vía API                                         |
| **Citas / agenda**                 | Reserva de sesiones comerciales                  | Agenda, recordatorios SMS/email                 | Disponibilidad del psicólogo y reserva por el paciente                                         |
| **Inteligencia artificial**        | No como eje del producto público                 | MIA (transcripción, resúmenes de sesión, etc.)  | Resumen de notas (Groq, solo servidor) y detección asíncrona de riesgo con alerta al psicólogo |
| **Código y despliegue**            | Producto cerrado comercial                       | Producto cerrado comercial                      | Repositorios propios (Android + Spring Boot); API en Render                                    |


**Therapyside** (fundada en 2016 como TherapyChat) conecta a usuarios con psicólogos colegiados para terapia por videollamada, con planes flexibles y primera sesión de prueba. Su misión es democratizar el acceso a la psicología online (Psicología y Mente; EL PAÍS, 2022). No sustituye el objetivo de **acompañame**: este TFG no ofrece contratación de terapeutas ni sesiones de videoterapia de pago.

**eholo** centraliza la gestión administrativa y clínica de la consulta (agenda, facturas, historiales encriptados, consentimientos, IA para transcripciones). Está orientado al **profesional**, no al paciente como actor principal de un diario entre sesiones. **acompañame** complementa el trabajo del psicólogo con visibilidad de las notas del paciente y herramientas ligeras de seguimiento, sin aspirar a ERP de consulta ni cumplimiento Verifactu.

**Diferenciación del TFG:** herramienta de **acompañamiento entre sesiones** (notas, tareas, citas, chat), con IA como **apoyo** al psicólogo (resumen y señal de riesgo en nivel alto), no como motor de contratación ni diagnóstico automatizado.

### 3.3. Tendencias

- **Salud mental digital y telepsicología:** mayor demanda de apoyo psicológico con barreras de acceso (económicas, horarias, estigma), acelerada tras la pandemia COVID-19.
- **Aplicaciones móviles nativas y APIs REST:** arquitectura cliente–servidor con sincronización y roles diferenciados.
- **IA generativa en contexto clínico:** resúmenes y detección de señales de riesgo, con exigencias de privacidad (minimización de datos en prompts) y supervisión humana.
- **Notificaciones push y tiempo real:** FCM y bases en tiempo real (Firebase RTDB) para mensajería y alertas.
- **Backend as a Service:** Firebase Auth para identidad; lógica sensible y claves de IA solo en servidor (Spring Boot + Groq).

### 3.4. Beneficios y expectativas


| Ámbito                  | Beneficio / expectativa                                                                                                                                                       |
| ----------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Paciente**            | Registrar notas entre sesiones, consultar tareas, reservar citas, chatear con su psicólogo y recibir notificaciones de mensajes o tareas.                                     |
| **Psicólogo**           | Ver pacientes asignados, sus notas, generar resumen IA, recibir alertas de riesgo elevado, asignar tareas y gestionar agenda de citas.                                        |
| **Académico / técnico** | Demostrar dominio de MVVM en Android (Compose, Hilt, Room), API REST en Spring Boot, integración multi-servicio (Firebase, PostgreSQL, Groq) y despliegue real (Render, APK). |


---

## 4. Descripción del proyecto

### Tipo de proyecto

**Trabajo de Fin de Grado (DAM2)** compuesto por dos repositorios:

1. **Cliente Android** (`psicologiaapp`): aplicación nativa **acompañame** (`dam2.tfg.psicologiaapp`).
2. **Backend** (`bdPsicologiaApp`): API REST **Spring Boot 3** desplegada en producción en `https://bdpsicologiaapp.onrender.com/`.

### Características principales


| Área               | Paciente                                                               | Psicólogo                                                 |
| ------------------ | ---------------------------------------------------------------------- | --------------------------------------------------------- |
| **Cuenta**         | Registro, inicio de sesión, recuperación de contraseña, foto de perfil | Igual + alta con número de colegiado y especialidades     |
| **Vínculo**        | Buscar y asignar psicólogo                                             | Listado y ficha de pacientes asignados                    |
| **Notas**          | Crear, editar y consultar notas personales                             | Ver notas del paciente; resumen asistido por IA (vía API) |
| **Tareas**         | Ver tareas asignadas; marcar realizadas / aceptadas                    | Crear y gestionar tareas por paciente                     |
| **Citas**          | Consultar disponibilidad, reservar y ver mis citas                     | Agenda de citas con pacientes                             |
| **Chat**           | Mensajería con el psicólogo asignado                                   | Chat por paciente                                         |
| **Notificaciones** | Mensajes, nuevas tareas                                                | Alertas clínicas de riesgo (IA en backend)                |
| **Preferencias**   | Tema claro / oscuro / sistema, ajustes de cuenta                       | Hub de ajustes y perfil profesional                       |


### Usuarios destinatarios

- **Pacientes** en seguimiento psicológico que utilizan la app para el acompañamiento digital con su profesional asignado.
- **Psicólogos** colegiados (registro con **número de colegiado** y especialidades) que gestionan pacientes, tareas, citas y revisión de notas.

---

## 5. Objetivos del proyecto

### Objetivo general

Desarrollar un ecosistema digital seguro que conecte a **paciente** y **psicólogo asignado** para el seguimiento terapéutico complementario entre sesiones, con arquitectura por capas, autenticación centralizada y despliegue demostrable.

### Objetivos específicos

1. Implementar autenticación con **Firebase Auth** y sincronización de perfiles con la API REST mediante token Bearer.
2. Permitir el alta de pacientes y psicólogos y la **asignación** paciente ↔ psicólogo.
3. Gestionar **notas** del paciente (CRUD) con sincronización incremental respecto al servidor.
4. Gestionar **tareas terapéuticas** creadas por el psicólogo y su seguimiento por el paciente.
5. Implementar **citas** con consulta de disponibilidad, reserva y listados por rol.
6. Ofrecer **chat en tiempo real** (Firebase Realtime Database) con notificaciones push coordinadas por el backend.
7. Integrar **IA en servidor** (Groq): resumen de notas para el psicólogo y detección asíncrona de riesgo con notificación en nivel alto.
8. Desplegar el backend en **Render** (Docker + PostgreSQL) y distribuir el cliente como **APK** (flavor `prod`, GitHub Releases).
9. Aplicar arquitectura **MVVM** (cliente) y por capas **web → service → repository → domain** (servidor), con pruebas automatizadas en el backend.

---

## 6. Alcance del proyecto

### Qué incluye

- Aplicación Android: **Kotlin**, **Jetpack Compose**, **Material 3**, **Hilt**, **Room**, **Retrofit**, **DataStore**; `minSdk` 26, `compileSdk` 36; flavors `**local`** y `**prod**`.
- API REST bajo prefijo `**/api**`, seguridad Spring con filtro Firebase, roles `ROLE_PACIENTE` / `ROLE_PSICOLOGO`.
- Persistencia **PostgreSQL** en producción (Flyway); **H2** en perfil `dev`.
- **Firebase**: Auth, Realtime Database (chat), Cloud Messaging (push); **App Check** en builds release del cliente.
- **Groq** exclusivamente en backend para resumen y detección de riesgo.

### Límites y exclusiones


| Elemento                                                       | Estado en el proyecto                                                          |
| -------------------------------------------------------------- | ------------------------------------------------------------------------------ |
| Videollamadas terapéuticas                                     | No implementado                                                                |
| Facturación, Verifactu, pasarela de pagos                      | No implementado                                                                |
| Marketplace abierto de psicólogos                              | No; solo búsqueda y asignación de vínculo                                      |
| Certificación como dispositivo médico / diagnóstico automático | Fuera de alcance; IA como apoyo, no sustituto del criterio clínico             |
| IA sin `GROQ_API_KEY`                                          | Resumen → HTTP 503; detección de riesgo deshabilitada; notas operativas        |
| Caché offline completa de notas/tareas                         | Limitada; Room para perfiles; notas/tareas principalmente vía API              |
| `google-services.json` en repositorio público                  | Excluido (`.gitignore`); builds de terceros requieren proyecto Firebase propio |


### Restricciones

- **Render (plan gratuito):** posible *cold start*; mitigación con `GET /api/mantener-activo`.
- **Rate limiting** y `@PreAuthorize` en endpoints sensibles.
- Dependencia de conectividad para la mayoría de operaciones de negocio.

---

## 7. Requisitos del proyecto

### 7.1. Requisitos funcionales

**Paciente**

- RF-P01: Registrarse e iniciar sesión con email/contraseña (Firebase).
- RF-P02: Crear perfil de usuario en la API tras autenticación.
- RF-P03: Darse de alta como paciente y asignar un psicólogo.
- RF-P04: Crear, editar, eliminar y listar sus notas.
- RF-P05: Ver tareas asignadas; marcar como realizada o aceptada.
- RF-P06: Consultar disponibilidad del psicólogo, reservar y ver sus citas.
- RF-P07: Enviar y recibir mensajes de chat con el psicólogo asignado.
- RF-P08: Actualizar email, foto de perfil y preferencias (tema); eliminar cuenta.
- RF-P09: Recibir notificaciones push (mensajes, tareas).

**Psicólogo**

- RF-S01: Registrarse con número de colegiado y especialidades.
- RF-S02: Buscar pacientes y ver listado de pacientes asignados.
- RF-S03: Consultar notas de un paciente asignado.
- RF-S04: Solicitar resumen IA del historial de notas de un paciente.
- RF-S05: Crear, editar y eliminar tareas para un paciente.
- RF-S06: Gestionar y consultar citas con pacientes.
- RF-S07: Chatear con pacientes asignados.
- RF-S08: Recibir notificación push ante alerta de riesgo alto (IA).
- RF-S09: Actualizar perfil profesional (descripción, especialidades).

**Sistema / API**

- RF-A01: Validar token Firebase (o atajo `dev:` solo en perfil `dev`).
- RF-A02: Resolver roles según registros en BD (`ServicioRoles`).
- RF-A03: Sincronización incremental mediante endpoints de estado (notas, tareas, citas).
- RF-A04: Registrar tokens FCM y enviar notificaciones push.
- RF-A05: Análisis asíncrono de riesgo en notas con deduplicación de alertas.

### 7.2. Requisitos técnicos


| Componente                  | Requisito                                                                    |
| --------------------------- | ---------------------------------------------------------------------------- |
| **Cliente**                 | Android 8.0+ (API 26); JDK 11; Gradle Kotlin DSL; Android Studio recomendado |
| **UI**                      | Jetpack Compose, Navigation Compose, MVVM, Hilt, Coroutines, StateFlow       |
| **Red cliente**             | Retrofit, OkHttp, Gson; `BASE_URL` por flavor (`local` / `prod`)             |
| **Persistencia cliente**    | Room (caché perfiles), DataStore (preferencias)                              |
| **Backend**                 | JDK 17; Spring Boot 3.3.5; Kotlin 1.9; Spring Data JPA; Flyway (prod)        |
| **BD producción**           | PostgreSQL; variables `SPRING_DATASOURCE_`*                                  |
| **BD desarrollo**           | H2 en memoria, perfil `spring.profiles.active=dev`                           |
| **Servicios externos**      | Firebase (Auth, Admin SDK, RTDB, FCM); Groq API (`GROQ_API_KEY`)             |
| **Contenedor**              | Docker multi-stage (JDK 17)                                                  |
| **Documentación API (dev)** | SpringDoc OpenAPI → `http://localhost:8080/swagger-ui.html`                  |


### 7.3. Requisitos legales o normativos

*(El TFG no constituye certificación de cumplimiento; se documentan principios aplicables.)*

- **RGPD / LOPDGDD:** tratamiento de datos de identidad y contenido sensible (notas). Minimización en IA: solo `asunto` y `descripcion` de las últimas N notas hacia Groq; sin nombres, email, IDs ni fechas absolutas (`ServicioResumenIa`). Logs de riesgo sin contenido de notas (`ServicioDeteccionRiesgo`).
- **Encargados / infraestructura:** Google (Firebase), Groq, Render; credenciales en variables de entorno, no en el APK.
- **Permisos Android:** `INTERNET`, `POST_NOTIFICATIONS`; `allowBackup="false"` en manifiesto.
- **Ética profesional:** la detección de riesgo notifica al psicólogo en nivel **ALTO** con ventana de deduplicación; no sustituye la evaluación clínica.
- **Licencia:** proyecto académico; reutilización fuera del TFG sujeta a autorización del autor (README de ambos repositorios).

---

## 8. Planificación del proyecto

### 8.1. Estructura de tareas (fases)


| Fase                   | Contenido                                                           | Periodo aproximado (git) |
| ---------------------- | ------------------------------------------------------------------- | ------------------------ |
| **1. Fundamentos**     | Arquitectura MVVM, repositorios base, integración Retrofit/Firebase | Mar 2026 (desde 13/03)   |
| **2. Core de negocio** | Usuarios, paciente, psicólogo, notas, tareas, sincronización        | Mar–Abr 2026             |
| **3. Citas y UI**      | Módulo citas, mejoras home y formularios                            | Abr 2026                 |
| **4. Comunicación**    | Chat RTDB, push FCM, notificaciones en UI                           | May 2026 (desde 02/05)   |
| **5. IA y seguridad**  | Resumen Groq, detección de riesgo, endurecimiento                   | May 2026 (13–20/05)      |
| **6. Cierre**          | README, capturas, documentación GUION                               | 25–26/05/2026            |


### 8.2. Cronograma (Gantt)

```mermaid
gantt
    title Cronograma aproximado TFG acompañame
    dateFormat YYYY-MM-DD
    section Fundamentos
    Arquitectura MVVM y repos base     :done, f1, 2026-03-13, 2026-03-20
    section Core_negocio
    Usuarios paciente psicologo notas  :done, f2, 2026-03-20, 2026-04-15
    Citas y mejoras UI                 :done, f3, 2026-04-15, 2026-04-28
    section Comunicacion
    Chat RTDB y push                   :done, f4, 2026-05-02, 2026-05-08
    section IA_seguridad
    Resumen y deteccion riesgo Groq    :done, f5, 2026-05-13, 2026-05-19
    Endurecimiento seguridad           :done, f6, 2026-05-15, 2026-05-20
    section Cierre
    Documentacion README GUION         :done, f7, 2026-05-25, 2026-05-26
```



### 8.3. Recursos necesarios (técnicos)


| Recurso                                        | Uso                                     |
| ---------------------------------------------- | --------------------------------------- |
| PC con **Android Studio** (Ladybug o superior) | Desarrollo y depuración del cliente     |
| **JDK 11** (app) y **JDK 17** (backend)        | Compilación                             |
| **Gradle wrapper** (incluido en repos)         | Builds                                  |
| Cuenta **Firebase**                            | Auth, RTDB, FCM, `google-services.json` |
| **PostgreSQL** (Render) + variables de entorno | Producción backend                      |
| Clave **Groq API**                             | Resumen y detección de riesgo           |
| **Git / GitHub**                               | Control de versiones y Releases         |
| Emulador Android API 26+ o dispositivo físico  | Pruebas de la app                       |
| **Docker** (opcional)                          | Imagen backend local o despliegue       |


---

## 9. Plan de gestión de riesgos


| ID  | Riesgo                                             | Prob. | Impacto | Recursos preventivos                                            | Plan de mitigación                                                       |
| --- | -------------------------------------------------- | ----- | ------- | --------------------------------------------------------------- | ------------------------------------------------------------------------ |
| R1  | Caída o *sleep* del servicio en Render             | Media | Alto    | Endpoint `GET /api/mantener-activo`; Dockerfile                 | Documentar cold start; reintentos en cliente; monitorizar disponibilidad |
| R2  | Fuga de credenciales Firebase / Groq               | Baja  | Alto    | `.gitignore`; variables de entorno; clave Groq solo en servidor | Rotación de claves; Firebase App Check en release                        |
| R3  | Falsos positivos/negativos en detección de riesgo  | Media | Alto    | Solo notifica nivel **ALTO**; deduplicación por ventana horaria | Revisión humana obligatoria; `IA_RIESGO_HABILITADO=false` si procede     |
| R4  | Pérdida de funcionalidad sin red                   | Media | Medio   | Room para perfiles; diseño sync incremental                     | Mensajes de error en UI; re-sincronizar al recuperar conexión            |
| R5  | Indisponibilidad de Groq                           | Media | Medio   | IA opcional; creación de notas independiente                    | HTTP 503 en resumen; desactivar detección sin clave                      |
| R6  | Expectativa de producto comercial (vs alcance TFG) | Alta  | Medio   | Alcance §6 y comparativa §3.2 explícitos                        | Comunicar límites en documentación y defensa                             |


---

## 10. Diseño

### 10.1. Prototipado (wireframes)

Prototipos iniciales en la raíz del repositorio cliente:


| Pantalla          | Fichero                                           |
| ----------------- | ------------------------------------------------- |
| Inicio de sesión  | `doc/diseño-interfaz-app/LoginScreen.jpeg`        |
| Registro          | `doc/diseño-interfaz-app/RegistrationScreen.jpeg` |
| Home paciente     | `doc/diseño-interfaz-app/PatientHome.jpeg`        |
| Tareas asignadas  | `doc/diseño-interfaz-app/AssignedTasks.jpeg`      |
| Perfil de usuario | `doc/diseño-interfaz-app/UserProfile.jpeg`        |


Capturas de la aplicación implementada: carpeta `doc/capturas-app/` (véase §17).

### 10.2. Especificaciones técnicas

**Cliente (`dam2.tfg.psicologiaapp`)**

- Arquitectura **MVVM por capas**: `presentation` → `domain` ← `data`.
- Features: `auth`, `usuario`, `paciente`, `psicologo`, `nota`, `tarea`, `cita`, `chat`, `notificaciones`, `resumenIa`, `preferencias`.
- **Flavors:** `local` → `http://10.0.2.2:8080/`; `prod` → `https://bdpsicologiaapp.onrender.com/`.
- Interceptor HTTP: `Authorization: Bearer <firebase_id_token>`.

**Servidor (`bdPsicologiaApp`)**

- Capas: `web` → `service` → `repository` → `domain`.
- Controladores: usuarios, pacientes, psicólogos, notas, tareas, citas, chat, resumen IA, notificaciones FCM, archivos de perfil.
- Chat: metadatos y push en API; mensajes en **Firebase RTDB** en el cliente.

### 10.3. Diagramas

#### Arquitectura del sistema

```mermaid
flowchart LR
  subgraph client [Cliente Android]
    App[acompaname Compose MVVM]
  end
  subgraph firebase [Firebase]
    Auth[Auth]
    RTDB[Realtime Database]
    FCM[Cloud Messaging]
  end
  subgraph server [Backend Render]
    API[Spring Boot API]
    PG[(PostgreSQL)]
    Groq[Groq API]
  end
  App --> Auth
  App --> RTDB
  App --> FCM
  App -->|REST Bearer| API
  API --> Auth
  API --> PG
  API --> Groq
  API --> FCM
```



#### Casos de uso (resumen ampliado)

```mermaid
flowchart TB
  P[Paciente]
  S[Psicologo]
  P --> UC1[Registrarse e iniciar sesion Firebase]
  P --> UC2[Crear perfil en API]
  P --> UC3[Alta paciente y asignar psicologo]
  P --> UC4[Gestionar mis notas]
  P --> UC5[Ver tareas y marcar estados]
  P --> UC6[Reservar y ver citas]
  P --> UC7[Chat con psicologo asignado]
  P --> UC8[Actualizar perfil y preferencias]
  S --> UC9[Alta psicologo colegiado]
  S --> UC10[Ver pacientes y sus notas]
  S --> UC11[Resumen IA de notas]
  S --> UC12[Gestionar tareas por paciente]
  S --> UC13[Gestionar agenda de citas]
  S --> UC14[Chat con pacientes]
  S --> UC15[Recibir alerta riesgo alto]
  UC1 -.-> FB[(Firebase Auth)]
  UC2 --> API[(API REST)]
  UC3 --> API
  UC4 --> API
  UC5 --> API
  UC6 --> API
  UC7 --> RTDB[(Firebase RTDB)]
  UC7 --> API
  UC8 --> API
  UC9 --> API
  UC10 --> API
  UC11 --> API
  UC12 --> API
  UC13 --> API
  UC14 --> RTDB
  UC14 --> API
  UC15 --> API
```



#### Modelo de datos (servidor — simplificado)

```mermaid
classDiagram
  class Usuario {
    Long id
    String firebaseUid
    String email
    String nombre
    String apellidos
  }
  class Psicologo {
    Long id
    String numeroColegiado
    String especialidad
  }
  class Paciente {
    Long id
  }
  class Nota {
    Long id
    String asunto
    String descripcion
  }
  class Tarea {
    Long id
    String tituloTarea
    boolean realizada
  }
  class Cita {
    Long id
    LocalDateTime inicio
    String estado
  }
  Usuario "1" -- "0..1" Psicologo
  Usuario "1" -- "0..1" Paciente
  Psicologo "1" o-- "*" Paciente
  Paciente "1" *-- "*" Nota
  Psicologo "1" *-- "*" Nota
  Paciente "1" *-- "*" Tarea
  Psicologo "1" *-- "*" Tarea
  Paciente "1" *-- "*" Cita
  Psicologo "1" *-- "*" Cita
```



#### Diagrama entidad-relación (físico PostgreSQL)

```mermaid
erDiagram
  usuarios ||--o| psicologos : usuario_id
  usuarios ||--o| pacientes_v2 : user_id
  psicologos ||--o{ pacientes_v2 : psicologo_id
  pacientes_v2 ||--o{ notas : paciente_id
  psicologos ||--o{ notas : psicologo_id
  pacientes_v2 ||--o{ tareas : paciente_id
  psicologos ||--o{ tareas : psicologo_id
  pacientes_v2 ||--o{ citas : paciente_id
  psicologos ||--o{ citas : psicologo_id
  usuarios ||--o{ fcm_tokens : usuario_id
```



#### Secuencia — petición autenticada

```mermaid
sequenceDiagram
  participant App as App Android
  participant FB as Firebase Auth
  participant API as Spring API
  participant Filter as FirebaseTokenFilter
  participant Svc as Servicio y Repo
  participant DB as PostgreSQL

  App->>FB: Obtener ID token
  FB-->>App: idToken
  App->>API: Peticion con Bearer
  API->>Filter: Validar token
  Filter->>Svc: Cargar roles por firebaseUid
  Svc->>DB: Consultar paciente o psicologo
  DB-->>Svc: Roles
  Filter->>API: SecurityContext
  API->>Svc: Logica del endpoint
  Svc->>DB: JPA
  DB-->>App: JSON respuesta
```



#### Secuencia — paciente crea nota y evaluación de riesgo

```mermaid
sequenceDiagram
  participant App as App Android
  participant API as NotaController
  participant Svc as ServicioNota
  participant Riesgo as ServicioDeteccionRiesgo
  participant Groq as Groq API
  participant FCM as FCM

  App->>API: POST /api/notas
  API->>Svc: crearNota
  Svc-->>API: Nota creada
  API-->>App: 201 Created
  API->>Riesgo: analizar asincrono
  Riesgo->>Groq: Prompt sin PII
  Groq-->>Riesgo: nivel y justificacion
  alt nivel ALTO
    Riesgo->>FCM: Push al psicologo
  end
```



Documentación ampliada: `doc/DISEÑO_SISTEMA.md` (raíz del repositorio `psicologiaapp`). Endpoints: `doc/ENDPOINTS.md` en el repositorio `bdPsicologiaApp`.

---

## 11. Instalación y preparación

### 11.1. Puesta en marcha del proyecto

**Backend (desarrollo local)**

```bash
cd bdPsicologiaApp
./gradlew bootRun --args='--spring.profiles.active=dev'
```

Servidor en `http://localhost:8080`. Token de prueba en Swagger: `Authorization: Bearer dev:uid1:uid1@local.test`.

**Cliente Android**

1. Clonar repositorio `psicologiaapp` y abrir en Android Studio; sincronizar Gradle.
2. Crear proyecto Firebase; añadir app Android `dam2.tfg.psicologiaapp`; descargar `google-services.json` → `app/google-services.json` (no subir a Git público).
3. Habilitar Authentication (email/contraseña), Realtime Database y Cloud Messaging.
4. Con backend local en emulador: variante `**localDebug`** (`BASE_URL` = `10.0.2.2:8080`).
5. En dispositivo físico con backend local: cambiar IP en flavor `local` en `app/build.gradle.kts`.

```bash
./gradlew :app:installLocalDebug
```

**Producción:** backend con variables en Render; cliente `**prodRelease`** o APK desde GitHub Releases.

### 11.2. Control de versiones

- Dos repositorios Git independientes: **psicologiaapp** (cliente) y **bdPsicologiaApp** (API).
- No commitear: `google-services.json`, credenciales Firebase, `GROQ_API_KEY`, contraseñas de BD.
- Mensajes de commit descriptivos en español; historial desde marzo 2026.

### 11.3. Registro de incidencias

- Usar **GitHub Issues** en cada repositorio.
- Incluir: título, pasos para reproducir, flavor (`local`/`prod`), logs (Logcat / consola Spring), versión de la app (`versionName` 1.0).

---

## 12. Documentación de ejecución y plan de calidad

### 12.1. Procedimientos operativos


| Entorno                     | Procedimiento                                                                  |
| --------------------------- | ------------------------------------------------------------------------------ |
| Desarrollo                  | Backend `dev` + app `localDebug` + token `dev:` en Swagger                     |
| Pruebas integradas manuales | Registrar paciente y psicólogo de prueba; asignar vínculo; recorrer flujos §14 |
| Producción                  | API en Render; APK `prod`; verificar FCM y App Check                           |


### 12.2. Registro de pruebas

**Backend** (`./gradlew test`):


| Clase de prueba                   | Ámbito              |
| --------------------------------- | ------------------- |
| `BdPsicologiaAppApplicationTests` | Contexto Spring     |
| `UsuarioControllerTest`           | API usuarios        |
| `PacienteControllerTest`          | API pacientes       |
| `PsicologoControllerTest`         | API psicólogos      |
| `NotaControllerTest`              | API notas           |
| `TareaControllerTest`             | API tareas          |
| `ChatControllerTest`              | API chat            |
| `ServicioUsuarioTest`             | Servicio usuarios   |
| `ServicioPacienteTest`            | Servicio pacientes  |
| `ServicioPsicologoTest`           | Servicio psicólogos |
| `ServicioNotaTest`                | Servicio notas      |
| `ServicioTareaTest`               | Servicio tareas     |
| `ServicioChatTest`                | Servicio chat       |


**Cliente** (`./gradlew :app:testDebugUnitTest`):


| Clase                        | Ámbito                     |
| ---------------------------- | -------------------------- |
| `ExampleUnitTest`            | Plantilla                  |
| `IniciarSesionViewModelTest` | ViewModel inicio de sesión |


### 12.3. Indicadores de calidad

- Build Gradle exitoso en cliente y servidor.
- Tests backend en verde (cobertura mayor que en cliente).
- Ausencia de regresiones en sincronización y autenticación tras cambios.
- Tiempos de respuesta aceptables en operaciones CRUD (perfil `dev`).

### 12.4. Métodos de verificación

1. **Automática:** suites anteriores en CI local o manual pre-entrega.
2. **Manual por rol:** checklist — registro → asignación → nota → tarea → cita → chat → notificación → resumen IA (con `GROQ_API_KEY` configurada).

---

## 13. Distribución

### 13.1. Tecnología de distribución


| Componente            | Tecnología                                          |
| --------------------- | --------------------------------------------------- |
| Cliente               | APK Android (Gradle `assembleProdRelease`)          |
| Servidor              | Imagen **Docker** en **Render**                     |
| Artefactos opcionales | **GitHub Releases** (`acompaname-prod-release.apk`) |


### 13.2. Descripción del proceso

1. **Backend:** `docker build` → despliegue en Render con variables de entorno (`SPRING_DATASOURCE_`*, `FIREBASE_CREDENTIALS`, `GROQ_API_KEY`, `APP_URL_PUBLICA_BASE`, etc.). URL pública: `https://bdpsicologiaapp.onrender.com/`.
2. **Cliente:** con `google-services.json` del autor, `./gradlew :app:assembleProdRelease` → APK en `app/build/outputs/apk/prod/release/`.
3. **Publicación:** subir APK como asset en GitHub Releases etiquetada (p. ej. `v1.0` según `versionName`).
4. **Usuario final:** descargar APK, permitir instalación desde orígenes desconocidos si el sistema lo exige, abrir app con conexión a Internet y backend operativo.

---

## 14. Manuales

### 14.1. Manual de instalación (usuario final)

1. Obtener el APK desde **GitHub Releases** del repositorio o copia facilitada por el autor del TFG.
2. Comprobar **Android 8.0 o superior**.
3. Instalar el fichero; aceptar permiso de instalación de fuentes desconocidas si aparece.
4. Al primer arranque, conceder permiso de **notificaciones** (Android 13+) si se desea recibir alertas.
5. Requisitos: conexión a Internet; servicio API de producción disponible.

### 14.2. Manual de uso resumido

**Paciente**

1. **Registro / acceso:** abrir app → registrarse como paciente o iniciar sesión.
2. **Psicólogo:** si no hay asignación, buscar psicólogo y confirmar vínculo.
3. **Notas:** desde inicio → mis notas → crear o editar entradas (contenido ocultable en UI).
4. **Tareas:** revisar tareas asignadas; marcar realizada o aceptada.
5. **Citas:** consultar disponibilidad del psicólogo y reservar cita.
6. **Chat:** abrir conversación con el psicólogo asignado.
7. **Ajustes:** tema claro/oscuro/sistema; actualizar perfil o cerrar sesión.

**Psicólogo**

1. **Registro:** alta con número de colegiado y especialidades.
2. **Pacientes:** listado en home → ficha de paciente.
3. **Notas:** consultar notas del paciente; opcionalmente solicitar **resumen IA**.
4. **Tareas:** crear o editar tareas para el paciente seleccionado.
5. **Citas:** revisar agenda de citas.
6. **Chat y alertas:** mensajes por paciente; atender notificaciones de **riesgo alto** con criterio clínico profesional.

Ilustraciones: `doc/capturas-app/` (inicio de sesión, home paciente/psicólogo, notas, tareas, citas, chat, resumen).

---

## 15. Conclusiones

### 15.1. Informe final

Se ha implementado el ecosistema **PsicologíaApp** conforme a los objetivos del §5: cliente **acompañame** en Android y API **bdPsicologiaApp** desplegada, con autenticación Firebase, gestión de perfiles por rol, notas, tareas, citas, chat en tiempo real, notificaciones push e integración de IA en servidor para resumen y alertas de riesgo.

### 15.2. Resultados obtenidos

- Aplicación funcional con arquitectura MVVM y UI en Jetpack Compose.
- API REST documentada (Swagger en `dev`, `ENDPOINTS.md` en producción).
- Persistencia relacional con migraciones Flyway; sincronización incremental en entidades principales.
- Despliegue real en Render y distribución mediante APK.
- Batería de tests automatizados en backend; pruebas unitarias básicas en cliente.

### 15.3. Viabilidad del proyecto

**Viabilidad técnica y académica:** alta como TFG DAM2 que demuestra integración full-stack móvil + cloud.

**Viabilidad como producto sanitario comercial:** limitada sin ampliar requisitos legales (RGPD operativo, consentimientos, evaluación clínica certificada, videoterapia, facturación) y validación con profesionales y pacientes reales.

### 15.4. Mejoras futuras

- Videollamadas integradas o enlace seguro a sesión.
- Política de privacidad y consentimiento informado in-app.
- Mayor cobertura de tests (UI Android, integración E2E).
- Modo offline ampliado para notas en Room.
- Cifrado reforzado del chat; panel web para psicólogos.
- Portabilidad a iOS o PWA.

---

## 16. Anexos


| Anexo                                | Ubicación                                                                |
| ------------------------------------ | ------------------------------------------------------------------------ |
| A. Diseño completo del sistema       | `doc/DISEÑO_SISTEMA.md`                                                  |
| B. Documentación técnica Actividad 4 | `doc/ACTIVIDAD4.md`                                                      |
| C. Endpoints REST detallados         | Repositorio `bdPsicologiaApp` → `doc/ENDPOINTS.md`                       |
| D. Esquema SQL inicial               | `bdPsicologiaApp/src/main/resources/db/migration/V1__schema_inicial.sql` |
| E. README cliente y servidor         | `README.md` (ambos repos)                                                |
| F. Reglas Firebase RTDB              | `database.rules.json` (raíz `psicologiaapp`)                             |


*Rutas relativas a la raíz de cada repositorio salvo indicación contraria.*

---

## 17. Índice de tablas e imágenes

### Tablas


| Nº  | Título                                       | Sección |
| --- | -------------------------------------------- | ------- |
| T1  | Comparativa Therapyside / eholo / acompañame | §3.2    |
| T2  | Funcionalidades por rol                      | §4      |
| T3  | Beneficios y expectativas                    | §3.4    |
| T4  | Exclusiones de alcance                       | §6      |
| T5  | Requisitos técnicos                          | §7.2    |
| T6  | Fases de planificación                       | §8.1    |
| T7  | Recursos técnicos                            | §8.3    |
| T8  | Matriz de riesgos                            | §9      |
| T9  | Wireframes (prototipo)                       | §10.1   |
| T10 | Pruebas backend                              | §12.2   |
| T11 | Pruebas cliente                              | §12.2   |
| T12 | Tecnología de distribución                   | §13.1   |
| T13 | Anexos documentales                          | §16     |


### Imágenes


| Nº  | Título                            | Ruta                                               |
| --- | --------------------------------- | -------------------------------------------------- |
| I1  | Wireframe login                   | `doc/diseño-interfaz-app/LoginScreen.jpeg`         |
| I2  | Wireframe registro                | `doc/diseño-interfaz-app/RegistrationScreen.jpeg`  |
| I3  | Wireframe home paciente           | `doc/diseño-interfaz-app/PatientHome.jpeg`         |
| I4  | Wireframe tareas                  | `doc/diseño-interfaz-app/AssignedTasks.jpeg`       |
| I5  | Wireframe perfil                  | `doc/diseño-interfaz-app/UserProfile.jpeg`         |
| I6  | Captura inicio de sesión          | `doc/capturas-app/pantalla_iniciosesion.jpg`       |
| I7  | Captura elegir rol                | `doc/capturas-app/elegir-rol.jpg`                  |
| I8  | Captura registro psicólogo        | `doc/capturas-app/registro-psico.jpg`              |
| I9  | Captura home paciente             | `doc/capturas-app/home-paciente.jpg`               |
| I10 | Captura home sin psicólogo        | `doc/capturas-app/home-paciente-sin-psicologo.jpg` |
| I11 | Captura home psicólogo            | `doc/capturas-app/home-psicologo.jpg`              |
| I12 | Captura mis notas                 | `doc/capturas-app/mis-notas.jpg`                   |
| I13 | Captura crear nota                | `doc/capturas-app/crear-nota.jpg`                  |
| I14 | Captura tarea                     | `doc/capturas-app/tarea.jpg`                       |
| I15 | Captura agendar cita              | `doc/capturas-app/agendar-cita.jpg`                |
| I16 | Captura cita agendada             | `doc/capturas-app/cita-agendada.jpg`               |
| I17 | Captura chat                      | `doc/capturas-app/chat.jpg`                        |
| I18 | Captura perfil psicólogo          | `doc/capturas-app/perfil-psicologo.jpg`            |
| I19 | Captura resumen notas (psicólogo) | `doc/capturas-app/resumen-notas-psicologo.jpg`     |


*Los diagramas Mermaid del §10 se exportan a imagen desde el visor Markdown o herramientas compatibles para la memoria en PDF.*

---

## 18. Bibliografía y referencias

### Documentación técnica oficial

- Android Developers. *Guías de Jetpack Compose, Architecture Components y Firebase en Android.* [https://developer.android.com/](https://developer.android.com/)
- Google. *Firebase Documentation* (Authentication, Realtime Database, Cloud Messaging). [https://firebase.google.com/docs](https://firebase.google.com/docs)
- VMware Spring. *Spring Boot 3 Reference Documentation.* [https://docs.spring.io/spring-boot/](https://docs.spring.io/spring-boot/)
- Groq. *API Reference* (modelos compatibles OpenAI). [https://console.groq.com/docs](https://console.groq.com/docs)
- Render. *Deploy Docker applications.* [https://render.com/docs](https://render.com/docs)

### Referencias sectoriales y comparativa

- Therapyside (antes TherapyChat). *Psicólogos online.* [https://www.therapyside.com/es-es](https://www.therapyside.com/es-es)
- eholo. *Software para psicólogos y centros.* [https://www.eholo.health/](https://www.eholo.health/)
- Psicología y Mente. *Entrevista a TherapyChat: haciendo accesible la terapia online.* [https://psicologiaymente.com/entrevistas/therapychat-terapia-online](https://psicologiaymente.com/entrevistas/therapychat-terapia-online)
- EL PAÍS Semanal (2022). *Un psicólogo que te acompaña a través del móvil.* [https://elpais.com/eps/2022-04-28/un-psicologo-que-te-acompana-a-traves-del-movil.html](https://elpais.com/eps/2022-04-28/un-psicologo-que-te-acompana-a-traves-del-movil.html)
- Google Play. *Therapyside | Psicólogo Online* (`com.terapiachat.android`). [https://play.google.com/store/apps/details?id=com.terapiachat.android&hl=es](https://play.google.com/store/apps/details?id=com.terapiachat.android&hl=es)

### Normativa y privacidad (consulta)

- Reglamento (UE) 2016/679 (RGPD).
- Ley Orgánica 3/2018 de protección de datos y garantía de los derechos digitales (LOPDGDD).

### Repositorios del TFG

- Cliente Android: repositorio **psicologiaapp** — módulo `dam2.tfg.psicologiaapp`.
- Backend: repositorio **bdPsicologiaApp** — grupo `dam2.tfg.psicologiaapp.backend`.

---

*Jesús Conde Barba — DAM2 — TFG — Curso 2025–2026*