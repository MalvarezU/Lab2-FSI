# Laboratorio 2 — Reglas de Negocio con Spring Boot y Drools

Aplicación Spring Boot que usa el motor de reglas **Drools** (BRMS) para evaluar
reglas de negocio externalizadas en archivos `.drl`.

Incluye dos dominios:

- **Créditos bancarios** (guía del laboratorio): 6 reglas en `rules/credit_rules.drl`.
- **Aerolínea** (ejercicio): 10 reglas en `rules/airline_rules.drl`.

---

## Requisitos

- JDK 17 o superior (probado en JDK 17 y JDK 21).
- Maven (o usar el wrapper `./mvnw` incluido).

> **Nota JDK 21:** el proyecto fuerza `mvel2 2.5.4.Final` para compatibilidad con
> JDK 21. Ver `INFORME_LAB2.md` §8.1.

---

## Ejecutar

```bash
# Tests
./mvnw clean test

# Empaquetar
./mvnw clean package -DskipTests

# Correr
./mvnw spring-boot:run
# o
java -jar target/droolrules-0.0.1-SNAPSHOT.jar
```

Aplicación en `http://localhost:8080`.

---

## Endpoints

### API REST

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/credit/api/evaluate` | Evalúa una solicitud de crédito |
| `POST` | `/credit/evaluateCredit` | Evalúa y agrega explicación con IA (si está habilitada) |
| `POST` | `/airline/api/evaluate` | Evalúa las 10 reglas de la aerolínea |

### Vistas web (Thymeleaf)

| Ruta | Descripción |
|---|---|
| `/credit/form` | Formulario de solicitud de crédito |
| `/airline/form` | Formulario de evaluación de aerolínea |

### Ejemplo — aerolínea

```bash
curl -X POST http://localhost:8080/airline/api/evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "passenger": {"name":"Pablo","age":70,"membershipTier":"Platinum","travellingWithChildren":false},
    "flight": {"flightNumber":"AV116","delayMinutes":200,"durationHours":6.0,"emergencyExitSeatsAvailable":1},
    "luggageWeightKg": 8.0,
    "seatPreference": "Any",
    "basePrice": 500.0
  }'
```

### Ejemplo — crédito

```bash
curl -X POST http://localhost:8080/credit/api/evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "customer": {"name":"Juan Perez","age":30,"monthlyIncome":2000.0,"creditScore":750},
    "creditType": "personal",
    "requestAmount": 15000.0
  }'
```

---

## Capa de IA (opcional)

Deshabilitada por defecto (no requiere API key). Para habilitarla:

```bash
OPENAI_API_KEY=sk-... ./mvnw spring-boot:run -Dspring-boot.run.profiles=ai
```

---

## Tests

```bash
./mvnw clean test
```

| Clase | Pruebas |
|---|---|
| `AirlineRulesTest` | 17 |
| `CreditRulesTest` | 8 |
| `CreditValidationTest` | 2 |
| `DroolrulesApplicationTests` | 1 |

**Total: 28 pruebas.**

Las colecciones de Postman están en `postman/`.

---

## Estructura

```
src/main/java/com/udea/droolrules/
├── airline/        # Dominio de la aerolínea (model, service, controller)
├── ai/             # AIExplanationService (opcional)
├── config/         # DroolsConfig (carga ambos DRL)
├── controller/     # CreditEvaluationController
├── model/          # Customer, CreditRequest, CreditResponse
└── service/        # CreditEvaluationService

src/main/resources/
├── rules/          # credit_rules.drl, airline_rules.drl
├── templates/      # Vistas Thymeleaf
├── application.yml
└── application-ai.yml
```

---

