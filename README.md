# SpaceX JavaFX App 🚀

Aplicación de escritorio desarrollada en Java que permite consultar información en tiempo real sobre lanzamientos y cohetes de SpaceX, utilizando su API pública. La interfaz está construida con JavaFX y hace uso de técnicas de programación reactiva mediante RxJava y Retrofit.

## 🧩 Funcionalidad principal

- Consulta de lanzamientos espaciales con detalles como nombre, fecha y parche representativo.
- Visualización de cohetes con nombre, tipo, éxito de los lanzamientos y fecha del primer vuelo.
- Búsqueda y filtrado de elementos cargados.
- Carga de datos en segundo plano para evitar bloqueos en la interfaz.
- Visualización de imágenes (parches) proporcionadas por la API.

## 🛠️ Tecnologías utilizadas

- Java 17
- JavaFX
- RxJava
- Retrofit
- GSON
- Maven

## ✅ Requisitos obligatorios cumplidos

- [x] Uso de programación reactiva con RxJava.
- [x] Consumo de 2 endpoints distintos de la API de SpaceX (lanzamientos y cohetes).
- [x] Visualización de los datos con `ObservableList` en componentes `TableView`.
- [x] Carga concurrente de los datos.
- [x] Funcionalidad de búsqueda y filtrado.

## ⭐ Funcionalidades extra implementadas

- [x] Visualización de imágenes obtenidas desde la API (parches de los lanzamientos).
- [x] Exportación a CSV de los datos mostrados, con compresión automática a ZIP usando `CompletableFuture`.
- [x] Uso de GitHub como plataforma de gestión de versiones:
  - Dos ramas: `master` (versión final) y `develop` (funcionalidades extra).
  - Issues creados para hacer seguimiento de las tareas implementadas.
