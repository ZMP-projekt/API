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
* `PUT /api/admin/trainers/{id}` - Dodawanie Trenera. 



### Moduł Trainer 
* `PUT /api/trainers/me` - Profil Trenera (widoczny tylko dla niego - ograniczenie hasRole TRAINER.
* `GET /api/trainers/me` - pobieranie danych profilu trenera.
* `GET /api/trainers` - Lista trenerów, endpoint ogólnodostępny. 
  


### Moduł Membership & Access 
* `POST /api/memberships/purchase` – Zakup karnetu (OPEN, NIGHT, STUDENT). Generuje wpis w audycie.
* `GET /api/memberships/me` – Sprawdzenie statusu własnego karnetu (typ, data ważności).
* `POST /api/access/check` – Wirtualna bramka (tez weryfikuje ważność i godziny dla karnetu NIGHT).


## 📝 Do wdrożenia (Roadmap Security)
- [ ] **Refresh Tokens:** Dłuższa żywotność sesji bez konieczności ciągłego logowania.
- [ ] **Rate Limiting:** Ochrona przed atakami Brute Force.
- [ ] **Email Verification:** Potwierdzanie konta przez link aktywacyjny.
- [ ] **Audit Logging:** Rejestrowanie kluczowych akcji administracyjnych w bazie.
