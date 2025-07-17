## 🔩 API REST - Product Similar Service

### 🛠️ Tecnologías

- **Java 17**
- **Spring Boot**
- **Spring WebFlux (WebClient)**
- **Spring Cache (con configuración simple)**
- **Maven**
- **Docker + Docker Compose**
- **k6 (para stress testing)**

---

### ▶️ Cómo ejecutar

1. **Compila y levanta la API:**

```bash
./mvnw clean package
docker-compose up --build
```

2. **Test de rendimiento con k6:**

```bash
docker-compose run --rm k6 run /scripts/test.js
```

---

### 🚀 Endpoints disponibles

- `GET /product/{productId}/similar`\
  Devuelve una lista de productos similares con sus detalles.

---

### 📌 Explicación técnica para la corrección

#### ✅ Objetivo de mejora

El objetivo principal fue **optimizar el rendimiento del endpoint **``, ya que este consulta múltiples recursos externos de forma secuencial en su versión original.

#### 🧪 Etapas de mejora implementadas

1. **Versión inicial:**

   - Usaba `RestTemplate`.
   - Las llamadas a los productos similares eran secuenciales.
   - Tiempo de respuesta alto y poco escalable bajo carga.

2. **Optimización con **``**:**

   - Migración a WebClient para operaciones no bloqueantes.
   - Se mantuvo la operación bloqueante usando `.block()` por simplicidad, pero se ganó velocidad.
   - Se paralelizó el fetch de productos similares usando `parallelStream()`.

3. **Añadido de caché (**``**):**

   - Se cachea cada producto individual (`/product/{id}`).
   - Las respuestas repetidas no hacen llamadas externas.
   - Se reduce la latencia y el número de peticiones HTTP drásticamente.

#### 📊 Resultados

- Con solo `WebClient`:

  - \~17.000 iteraciones completadas
  - Tiempos promedio por request \~41ms

- Con `WebClient + Cache`:

  - Sólo 23 peticiones HTTP realizadas (al calentarse la caché)
  - Mayor duración inicial por warm-up, pero mejora general significativa

#### 🧠 Conclusión

El sistema ahora es **más eficiente**, **escalable** y preparado para cargas mayores. El uso de paralelización y caché ha reducido la carga sobre servicios externos y mejorado el rendimiento percibido por los usuarios.

