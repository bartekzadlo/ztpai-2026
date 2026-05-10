# 🎮 GameLibrary Frontend

Aplikacja Angular konsumująca REST API `game-library-api`.

## Stack

| Warstwa | Technologia |
|---------|-------------|
| Framework | Angular 18 |
| Język | TypeScript |
| Style | SCSS (dark theme) |
| HTTP | HttpClient + Interceptor JWT |
| Routing | Lazy-loaded modules |
| Formularze | Reactive Forms |
| Auth | JWT (jwt-decode) |

## Uruchomienie

```bash
# 1. Zainstaluj zależności
npm install

# 2. Upewnij się, że backend działa na localhost:8080
./mvnw spring-boot:run  # w katalogu backendu

# 3. Uruchom frontend
ng serve
# → http://localhost:4200
```

## Struktura

```
src/app/
├── core/
│   ├── models/         # Interfejsy TypeScript (GameResponse, ReviewResponse, ...)
│   ├── services/       # AuthService, GameService, ReviewService, LibraryService
│   ├── guards/         # AuthGuard (chroni trasy wymagające logowania)
│   └── interceptors/   # AuthInterceptor (dodaje Bearer token do requestów)
├── shared/
│   ├── navbar/         # Nawigacja z linkami i stanem logowania
│   └── pagination/     # Komponent paginacji wielokrotnego użytku
└── features/
    ├── auth/           # Login, Register
    ├── games/          # Lista, Szczegóły, Formularz (CRUD dla ADMIN)
    └── library/        # Prywatna biblioteka zalogowanego użytkownika
```

## Funkcjonalności

- **Katalog gier** – lista z paginacją, filtrowanie (tytuł, gatunek, platforma, rok), sortowanie
- **Szczegóły gry** – okładka, opis, statystyki ocen z wykresem słupkowym, lista recenzji
- **Recenzje** – tworzenie, edytowanie, usuwanie (1 na grę); oceny: gameplay, grafika, dźwięk, fabuła, regrywalność
- **Biblioteka** – dodawanie gry z statusem (Gram / Ukończona / Porzucona itp.)
- **Autoryzacja** – JWT przechowywany w localStorage, rola USER/ADMIN
- **Panel ADMIN** – dodawanie, edytowanie i usuwanie gier
