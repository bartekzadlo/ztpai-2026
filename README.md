# 🎮 game-library-api

REST API do zarządzania biblioteką gier — projekt zaliczeniowy z przedmiotu **Zaawansowane Technologie Programowania Aplikacji Internetowych** (ZTPAI, semestr letni 2025/2026).

Aplikacja umożliwia przeglądanie i zarządzanie kolekcją gier z podziałem na role: użytkownicy mogą przeglądać katalog, administratorzy mogą go edytować. Dostęp chroniony jest tokenami JWT.

---

## 🛠️ Stack technologiczny

| Warstwa      | Technologia                          |
|--------------|--------------------------------------|
| Backend      | Java 17, Spring Boot 3.4.4           |
| Persistence  | Spring Data JPA, H2 (in-memory)      |
| Security     | Spring Security, JWT (JJWT 0.12.6)  |
| Walidacja    | Jakarta Validation                   |
| Testy        | JUnit 5, Mockito                     |
| Build        | Maven (Maven Wrapper)                |

---

## 🚀 Uruchomienie

### Wymagania

- **Java 17+**
- **Maven 3.8+** (lub użyj dołączonego `./mvnw`)

### Kroki

```bash
# 1. Sklonuj repozytorium
git clone https://github.com/bartekzadlo/game-library-api.git
cd game-library-api

# 2. Uruchom aplikację
./mvnw spring-boot:run

# 3. API dostępne pod adresem
http://localhost:8080
```

> **Windows:** użyj `mvnw.cmd spring-boot:run` zamiast `./mvnw spring-boot:run`

### Konsola H2 (baza danych)

Dostępna w trybie deweloperskim pod adresem: `http://localhost:8080/h2-console`

- **JDBC URL:** `jdbc:h2:mem:gamelibrary`
- **User:** `sa`
- **Password:** *(puste)*

---

## 📡 Endpointy API

### Autentykacja

| Metoda | Endpoint              | Opis                          | Dostęp    |
|--------|-----------------------|-------------------------------|-----------|
| POST   | `/api/auth/register`  | Rejestracja nowego użytkownika | Publiczny |
| POST   | `/api/auth/login`     | Logowanie, zwraca token JWT    | Publiczny |

> ⚠️ **Wersja deweloperska:** `/api/auth/register` pozwala przekazać `role` (w tym `ADMIN`).
> To jest celowo zostawione na potrzeby developmentu/testów. W produkcji nie wolno na to pozwalać.

**Przykład rejestracji:**
```json
POST /api/auth/register
{
  "username": "jan",
  "password": "haslo123",
  "role": "USER"
}
```

**Przykład logowania (odpowiedź):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Gry

> Wszystkie endpointy wymagają nagłówka `Authorization: Bearer <token>`

| Metoda | Endpoint          | Opis                     | Rola       |
|--------|-------------------|--------------------------|------------|
| GET    | `/api/games`      | Lista wszystkich gier    | USER/ADMIN |
| GET    | `/api/games/{id}` | Szczegóły gry            | USER/ADMIN |
| POST   | `/api/games`      | Dodaj nową grę           | ADMIN      |
| PUT    | `/api/games/{id}` | Zaktualizuj grę          | ADMIN      |
| DELETE | `/api/games/{id}` | Usuń grę                 | ADMIN      |

**Przykład ciała zapytania (POST/PUT):**
```json
{
  "title": "The Witcher 3",
  "description": "RPG z otwartym światem",
  "genre": "RPG",
  "platform": "PC",
  "releaseYear": 2015,
  "coverUrl": "https://example.com/cover.jpg",
  "hasStory": true,
  "defaultRatingProfile": "DEFAULT"
}
```

### Recenzje

> Wszystkie endpointy wymagają nagłówka `Authorization: Bearer <token>`.
> Autor recenzji jest pobierany z tokena JWT (nie trzeba przekazywać `authorId` w żądaniu).

| Metoda | Endpoint                       | Opis                              |
|--------|--------------------------------|-----------------------------------|
| GET    | `/api/reviews/game/{gameId}`   | Lista recenzji dla gry            |
| GET    | `/api/reviews/{id}`            | Szczegóły recenzji                |
| GET    | `/api/reviews/game/{gameId}/average` | Średnia ocena gry (overall) |
| POST   | `/api/reviews`                 | Dodaj recenzję (1 na grę / użytk.)|
| PUT    | `/api/reviews/{id}`            | Edytuj recenzję (tylko autor)     |
| DELETE | `/api/reviews/{id}`            | Usuń recenzję (tylko autor)       |

### Admin

> Endpointy dostępne tylko dla roli `ADMIN`.

| Metoda | Endpoint             | Opis                              |
|--------|----------------------|-----------------------------------|
| POST   | `/api/admin/users`   | Utwórz użytkownika (USER/ADMIN)   |

**Przykład ciała zapytania (POST/PUT):**
```json
{
  "gameId": 1,
  "title": "Świetna gra",
  "content": "Bardzo wciągająca rozgrywka i świetny klimat.",
  "gameplayScore": 9,
  "graphicsScore": 8,
  "soundScore": 9,
  "storyScore": 8,
  "replayValueScore": 8,
  "ratingProfile": "DEFAULT"
}
```

> `storyScore` może być pominięte (N/A) dla gier bez fabuły. Wtedy ocena ogólna jest liczona z pozostałych kryteriów z renormalizacją wag.

### Kody HTTP

| Kod | Znaczenie                          |
|-----|------------------------------------|
| 200 | OK                                 |
| 201 | Zasób utworzony                    |
| 204 | Usunięto (brak treści w odpowiedzi)|
| 400 | Błędne dane wejściowe              |
| 401 | Brak / nieprawidłowy token JWT     |
| 403 | Brak uprawnień (zła rola)          |
| 404 | Zasób nie znaleziony               |
| 409 | Konflikt (np. użytkownik już istnieje) |

---

## 🏗️ Struktura projektu

```
src/
├── main/java/pl/edu/pk/gamelibrary/
│   ├── auth/
│   │   ├── controller/   # AuthController
│   │   ├── dto/          # LoginRequest, RegisterRequest, AuthResponse
│   │   ├── model/        # AppUser, Role
│   │   ├── repository/   # UserRepository
│   │   └── service/      # AuthService
│   ├── exception/        # GlobalExceptionHandler, ResourceNotFoundException
│   ├── game/
│   │   ├── dto/          # GameRequest, GameResponse
│   │   ├── Game.java     # Encja JPA
│   │   ├── GameController.java
│   │   ├── GameMapper.java
│   │   ├── GameRepository.java
│   │   └── GameService.java
│   ├── review/
│   │   ├── dto/          # ReviewRequest, ReviewResponse
│   │   ├── Review.java   # Encja JPA
│   │   ├── ReviewController.java
│   │   ├── ReviewMapper.java
│   │   ├── ReviewRepository.java
│   │   └── ReviewService.java
│   ├── security/         # JwtAuthFilter, JwtService, SecurityConfig
│   └── util/             # RatingCalculator
└── test/
    └── ...               # GameServiceTest, ReviewServiceTest, RatingCalculatorTest
```

---

## 🧪 Testy

```bash
./mvnw test
```

Projekt zawiera testy jednostkowe dla `GameService` (Mockito) oraz `RatingCalculator`.

---

## ⚙️ Konfiguracja

Główna konfiguracja w `src/main/resources/application.properties`.

> ⚠️ **Przed wdrożeniem produkcyjnym** zmień `jwt.secret` na bezpieczny klucz co najmniej 256-bitowy i zastąp H2 bazą danych produkcyjną (np. PostgreSQL).
