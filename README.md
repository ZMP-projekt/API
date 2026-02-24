# GymSystem API - Backend Service

System zarządzania siłownią zbudowany w architekturze REST API.

## 🛠 Technologie
* **Język:** Java 21
* **Framework:** Spring Boot 3.4.x
* **Baza danych:** PostgreSQL (Aiven Cloud)
* **Bezpieczeństwo:** Spring Security + JWT
* **Dokumentacja:** Swagger UI (OpenAPI 3.0)

## 🔐 Zaimplementowane Zabezpieczenia
1. **JWT Authentication:** Bezstanowa autoryzacja za pomocą tokenów.
2. **Password Hashing:** Hasła są szyfrowane algorytmem BCrypt przed zapisem w bazie danych.
3. **Role-Based Access Control (RBAC):** - `@PreAuthorize` na poziomie metod i kontrolerów.
   - Podział na role: `USER`, `ADMIN`.
4. **Zmienne Środowiskowe:** Wrażliwe dane (URL bazy, hasła) wstrzykiwane poza kodem źródłowym.
5. **CORS:** Skonfigurowana polityka dostępu dla wielu typów klientów (Web/Mobile/Desktop).

## 🚀 Wdrożone Endpointy
### Moduł Auth
* `POST /auth/register` - Rejestracja nowego konta.
* `POST /auth/login` - Logowanie (zwraca token JWT).

### Moduł User
* `GET /api/users/me` - Pobranie profilu zalogowanego użytkownika (na podstawie tokena).

### Moduł Admin
* `GET /api/admin/users` - Lista wszystkich zarejestrowanych osób.
* `PATCH /api/admin/users/{id}/role` - Nadanie uprawnień (np. zmiana na ADMIN).
* `DELETE /api/admin/users/{id}` - Usunięcie konta użytkownika.

## 📝 Do wdrożenia (Roadmap Security)
- [ ] **Refresh Tokens:** Dłuższa żywotność sesji bez konieczności ciągłego logowania.
- [ ] **Rate Limiting:** Ochrona przed atakami Brute Force.
- [ ] **Email Verification:** Potwierdzanie konta przez link aktywacyjny.
- [ ] **Audit Logging:** Rejestrowanie kluczowych akcji administracyjnych w bazie.
