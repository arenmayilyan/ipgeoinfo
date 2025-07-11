# 🌐 IP GeoInfo API

A Java Spring Boot application that accepts an IPv4 address and returns geolocation information by querying the external [FreeIPAPI](https://freeipapi.com). It demonstrates good architectural practices, error handling, and extendability for additional providers.

---

## 🚀 Features

- REST endpoint to query IP geolocation
- Integration with [FreeIPAPI](https://freeipapi.com)
- IPv4 address validation
- Global error handling with custom messages
- Easy to extend with additional IP providers
- Caching and rate limiting hooks

---

## 📦 Technologies Used

- Java 17+
- Spring Boot
- Spring Web & Validation
- WebClient (Reactive HTTP)
- Lombok
- JUnit (for testing)

---

## 🔧 Setup & Run

### 1. Clone the repository
git clone https://github.com/arenmayilyan/ipgeoinfo.git
### 2. Build the project
mvn clean install
### 3. Run the application
mvn spring-boot:run

## 📘 API Documentation (Swagger)

This project uses **Swagger UI** for interactive API documentation via **springdoc-openapi**.

Once the app is running, you can access the [Swagger UI](http://localhost:8080/swagger-ui/index.html)
