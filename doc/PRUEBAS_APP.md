# Cómo ejecutar la app según entorno (local / prod / prod release)

Este proyecto usa **product flavors** (`local`, `prod`) y **build types** (`debug`, `release`). La URL del backend se define en `BuildConfig.BASE_URL` (ver `app/build.gradle.kts`).

| Variante      | Backend por defecto | Uso típico |
|---------------|---------------------|------------|
| **localDebug**   | `http://10.0.2.2:8080/` (emulador → Spring en tu PC, puerto 8080) | Desarrollo y pruebas contra API local |
| **prodDebug**    | `https://bdpsicologiaapp.onrender.com/` | Pruebas contra producción (Render) sin generar release |
| **prodRelease**  | Misma URL que prod | Build optimizado, listo para firmar APK/AAB o pruebas “casi finales” |

> **Nota:** `10.0.2.2` solo funciona en el **emulador Android** apuntando al `localhost` de tu máquina. En **dispositivo físico** usa la IP LAN de tu PC (p. ej. `http://192.168.x.x:8080/`) o prueba solo con **prod**.

---

## Requisitos comunes

- Android Studio instalado y proyecto abierto en la carpeta `psicologiaapp`.
- Permiso `INTERNET` ya declarado en el manifest.
- El flavor **local** permite tráfico HTTP (cleartext) para `http://10.0.2.2`; el flavor **prod** usa HTTPS y no depende de cleartext.

---

## 1. Ejecutar en **local** (`localDebug`)

1. Levanta el backend Spring en tu máquina (puerto **8080** por defecto).
2. En Android Studio: **Build → Select Build Variant** (o panel **Build Variants**).
3. En el módulo **app**, elige **`localDebug`**.
4. Arranca un **emulador** (recomendado para `10.0.2.2`) y pulsa **Run** ▶.

Si algo no conecta: comprueba que el backend escucha en `0.0.0.0` o `localhost:8080` y que la variante sea realmente `localDebug`.

---

## 2. Ejecutar en **prod** (`prodDebug`)

1. No hace falta levantar el backend en local; debe estar desplegado y accesible en Render.
2. En **Build Variants**, elige **`prodDebug`**.
3. En la barra de ejecución, elige **emulador o dispositivo físico** y pulsa **Run** ▶.

Útil para validar login, registro, asignación de psicólogo y notas contra el entorno real, con depuración y logs.

### Dispositivo físico (móvil) frente a emulador

- **Contra Render (`prodDebug`)** da igual el emulador o el móvil: la app usa **HTTPS** a internet. Conecta el móvil por USB con **Depuración USB** activada, autoriza el PC si te lo pide, y selecciona el dispositivo en Android Studio antes de **Run**.
- Los comandos `./gradlew :app:installProdDebug` (o `installLocalDebug`) **no son solo para emulador**: instalan en **el dispositivo que ADB tenga conectado**. Comprueba con `adb devices` que tu móvil aparece como `device`.

---

## 3. Ejecutar **prod release** (`prodRelease`)

1. En **Build Variants**, elige **`prodRelease`**.
2. Para instalar desde Android Studio: **Build → Build Bundle(s) / APK(s) → Build APK(s)** o ejecutar con esa variante (según tu flujo).
3. Para distribución: firma el APK o el AAB con tu keystore (Play Console / pruebas internas).

`prodRelease` usa la misma URL de Render que `prodDebug`, pero con el build **release** (optimizado; ProGuard/R8 según lo tengas configurado en `build.gradle.kts`).

---

## Referencia rápida (Gradle)

Desde la raíz del proyecto `psicologiaapp` (opcional, terminal). Con el móvil por USB (o ADB por Wi‑Fi), ejecuta antes `adb devices` y verifica que tu dispositivo está listado:

```bash
# Debug local
./gradlew :app:installLocalDebug

# Debug contra Render
./gradlew :app:installProdDebug

# Release contra Render (requiere configuración de firma si generas AAB/APK de publicación)
./gradlew :app:assembleProdRelease
```

---

## Dónde está configurado

- Flavors y `BASE_URL`: `app/build.gradle.kts`
- Retrofit usa `BuildConfig.BASE_URL`: `app/src/main/java/dam2/tfg/psicologiaapp/di/RedModulo.kt`
- Cleartext solo en flavor local: `app/src/local/AndroidManifest.xml`
