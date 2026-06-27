# Tutorial de Prueba — CU36 Seguimiento de Bolsines

## 1. Arrancar el backend

Abrir una terminal en `implementacion/backend` y ejecutar:

```
.\.maven\apache-maven-3.9.6\bin\mvn.cmd spring-boot:run
```

Esperar hasta ver en consola:

```
Started Cu36Application in X.XXX seconds
```

El backend queda corriendo en `http://localhost:8080`. **No cerrar esta terminal.**

---

## 2. Arrancar el frontend

Abrir **otra** terminal en `implementacion/frontend` y ejecutar:

```
npm start
```

Esperar hasta ver:

```
Local: http://localhost:4200/
```

Abrir el navegador en **http://localhost:4200**

---

## 3. Escenarios de prueba

### Flujo principal

Usuario por defecto: `jperez` (CM Buenos Aires)

1. La página carga con la sesión activa
2. Hacer clic en **"Actualizar"** → aparecen 3 bolsines en la lista con ubicaciones en el mapa
3. Hacer clic en cualquier tarjeta de bolsín → aparece modal "¿Desea notificar al GCM destino?"
4. Hacer clic en **"Sí, enviar email"** → mensaje de confirmación con email y fecha/hora

---

### A1 — Sin bolsines enviados

Cambiar de usuario abriendo en el navegador:

```
http://localhost:8080/api/demo/login/cmendez
```

Debe responder: `Sesión: cmendez | CM: CM Rosario`

Volver a `http://localhost:4200` y hacer clic en **"Actualizar"**

> Resultado esperado: `⚠ No hay bolsines en estado Enviado para su comisión.`

---

### A2 — Filtro sin resultados

Volver a jperez:

```
http://localhost:8080/api/demo/login/jperez
```

En la página escribir `99999` en el campo de precinto (el filtro se aplica automáticamente al tipear)

> Resultado esperado: `No hay bolsines que coincidan con el filtro.`

---

### A4 / A5 — EB cancela / No envía email

Con bolsines visibles, hacer clic en cualquier tarjeta → aparece modal

Hacer clic en **"No, gracias"**

> Resultado esperado: `✓ Consulta de seguimiento finalizada.`

---

## 4. Cambiar de usuario

Pegar la URL en el navegador y luego volver a `http://localhost:4200` y hacer clic en **"Actualizar"**.

| URL completa | Usuario | CM | Rol |
|--------------|---------|----|-----|
| `http://localhost:8080/api/demo/login/jperez` | Juan Pérez | Buenos Aires | Empleado |
| `http://localhost:8080/api/demo/login/mlopez` | María López | Buenos Aires | GCM |
| `http://localhost:8080/api/demo/login/cmendez` | Carla Méndez | Rosario | GCM — sin bolsines → A1 |
| `http://localhost:8080/api/demo/login/lgarcia` | Luis García | Mendoza | GCM — sin bolsines → A1 |
