package pl.edu.pk.gamelibrary.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.pk.gamelibrary.auth.dto.AuthResponse;
import pl.edu.pk.gamelibrary.auth.dto.LoginRequest;
import pl.edu.pk.gamelibrary.auth.dto.RegisterRequest;
import pl.edu.pk.gamelibrary.auth.model.AppUser;
import pl.edu.pk.gamelibrary.auth.model.Role;
import pl.edu.pk.gamelibrary.auth.repository.UserRepository;
import pl.edu.pk.gamelibrary.auth.service.AuthService;
import pl.edu.pk.gamelibrary.security.JwtService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testy jednostkowe dla AuthService.
 *
 * Izolacja zależności: UserRepository, PasswordEncoder i JwtService
 * są mockowane przez Mockito – żadna baza danych ani kontekst Spring
 * nie jest ładowany.
 *
 * Pokryte przypadki:
 *  - happy path  : rejestracja nowego użytkownika, logowanie poprawnymi danymi
 *  - negatywny   : logowanie nieistniejącym użytkownikiem, błędne hasło
 *  - walidacja   : rejestracja zajętej nazwy użytkownika
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService – testy jednostkowe")
class AuthServiceTest {

    // ------------------------------------------------------------------ mocks

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    // ================================================================ register

    @Nested
    @DisplayName("register")
    class Register {

        /**
         * HAPPY PATH – rejestracja nowego, unikalnego użytkownika.
         * Weryfikuje: token obecny, username i rola USER w odpowiedzi.
         */
        @Test
        @DisplayName("rejestruje nowego użytkownika i zwraca token gdy username jest wolny")
        void register_shouldReturnAuthResponse_whenUsernameIsAvailable() {
            // given
            RegisterRequest request = new RegisterRequest();
            request.setUsername("janek");
            request.setPassword("tajneHaslo123");
            request.setRole(null); // brak roli → domyślnie USER

            when(userRepository.existsByUsername("janek")).thenReturn(false);
            when(passwordEncoder.encode("tajneHaslo123")).thenReturn("$hashed$");
            when(userRepository.save(any(AppUser.class))).thenAnswer(inv -> inv.getArgument(0));
            when(jwtService.generateToken("janek", "USER")).thenReturn("mock.jwt.token");

            // when
            AuthResponse response = authService.register(request);

            // then
            assertNotNull(response, "Odpowiedź nie powinna być null");
            assertEquals("janek", response.getUsername(),
                    "Username w odpowiedzi powinien być 'janek'");
            assertEquals("USER", response.getRole(),
                    "Domyślna rola powinna być USER");
            assertEquals("mock.jwt.token", response.getToken(),
                    "Token powinien być zwrócony");

            verify(userRepository, times(1)).existsByUsername("janek");
            verify(passwordEncoder, times(1)).encode("tajneHaslo123");
            verify(userRepository, times(1)).save(any(AppUser.class));
            verify(jwtService, times(1)).generateToken("janek", "USER");
        }

        /**
         * HAPPY PATH – rejestracja z jawną rolą ADMIN.
         */
        @Test
        @DisplayName("przyznaje rolę ADMIN gdy request.role == 'ADMIN'")
        void register_shouldGrantAdminRole_whenRoleIsAdmin() {
            // given
            RegisterRequest request = new RegisterRequest();
            request.setUsername("adminUser");
            request.setPassword("admin123");
            request.setRole("ADMIN");

            when(userRepository.existsByUsername("adminUser")).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("$hashed$");
            when(userRepository.save(any(AppUser.class))).thenAnswer(inv -> inv.getArgument(0));
            when(jwtService.generateToken("adminUser", "ADMIN")).thenReturn("admin.jwt");

            // when
            AuthResponse response = authService.register(request);

            // then
            assertEquals("ADMIN", response.getRole(),
                    "Rola powinna być ADMIN gdy request zawiera 'ADMIN'");
            assertEquals("admin.jwt", response.getToken());
        }

        /**
         * WALIDACJA BIZNESOWA – próba rejestracji zajętej nazwy użytkownika
         * → IllegalArgumentException, zapis NIE jest wywoływany.
         */
        @Test
        @DisplayName("rzuca IllegalArgumentException gdy username jest już zajęty")
        void register_shouldThrow_whenUsernameAlreadyExists() {
            // given
            RegisterRequest request = new RegisterRequest();
            request.setUsername("istniejacy");
            request.setPassword("haslo123");

            when(userRepository.existsByUsername("istniejacy")).thenReturn(true);

            // when / then
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> authService.register(request),
                    "Powinien zostać rzucony IllegalArgumentException gdy username zajęty"
            );

            assertTrue(ex.getMessage().contains("istniejacy"),
                    "Komunikat wyjątku powinien zawierać nazwę użytkownika");

            // zapis NIE powinien być wywołany
            verify(userRepository, never()).save(any());
            verify(jwtService, never()).generateToken(anyString(), anyString());
        }
    }

    // ================================================================ login

    @Nested
    @DisplayName("login")
    class Login {

        /**
         * HAPPY PATH – logowanie poprawnymi danymi zwraca token i dane użytkownika.
         */
        @Test
        @DisplayName("zwraca token gdy username i hasło są poprawne")
        void login_shouldReturnToken_whenCredentialsAreValid() {
            // given
            LoginRequest request = new LoginRequest();
            request.setUsername("janek");
            request.setPassword("tajneHaslo123");

            AppUser storedUser = new AppUser("janek", "$hashed$", Role.USER);

            when(userRepository.findByUsername("janek")).thenReturn(Optional.of(storedUser));
            when(passwordEncoder.matches("tajneHaslo123", "$hashed$")).thenReturn(true);
            when(jwtService.generateToken("janek", "USER")).thenReturn("login.jwt.token");

            // when
            AuthResponse response = authService.login(request);

            // then
            assertNotNull(response);
            assertEquals("janek", response.getUsername());
            assertEquals("USER", response.getRole());
            assertEquals("login.jwt.token", response.getToken());

            verify(userRepository, times(1)).findByUsername("janek");
            verify(passwordEncoder, times(1)).matches("tajneHaslo123", "$hashed$");
            verify(jwtService, times(1)).generateToken("janek", "USER");
        }

        /**
         * PRZYPADEK NEGATYWNY – użytkownik o podanej nazwie nie istnieje
         * → IllegalArgumentException.
         */
        @Test
        @DisplayName("rzuca IllegalArgumentException gdy username nie istnieje")
        void login_shouldThrow_whenUserNotFound() {
            // given
            LoginRequest request = new LoginRequest();
            request.setUsername("nieznany");
            request.setPassword("cokolwiek");

            when(userRepository.findByUsername("nieznany")).thenReturn(Optional.empty());

            // when / then
            assertThrows(
                    IllegalArgumentException.class,
                    () -> authService.login(request),
                    "Powinien zostać rzucony IllegalArgumentException gdy użytkownik nie istnieje"
            );

            // encoder NIE powinien być wywoływany – użytkownik nie znaleziony wcześniej
            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(jwtService, never()).generateToken(anyString(), anyString());
        }

        /**
         * PRZYPADEK NEGATYWNY – nieprawidłowe hasło → IllegalArgumentException.
         * Weryfikuje, że token NIE jest generowany.
         */
        @Test
        @DisplayName("rzuca IllegalArgumentException gdy hasło jest błędne")
        void login_shouldThrow_whenPasswordIsWrong() {
            // given
            LoginRequest request = new LoginRequest();
            request.setUsername("janek");
            request.setPassword("zleHaslo");

            AppUser storedUser = new AppUser("janek", "$hashed$", Role.USER);

            when(userRepository.findByUsername("janek")).thenReturn(Optional.of(storedUser));
            when(passwordEncoder.matches("zleHaslo", "$hashed$")).thenReturn(false);

            // when / then
            assertThrows(
                    IllegalArgumentException.class,
                    () -> authService.login(request),
                    "Powinien zostać rzucony IllegalArgumentException gdy hasło nieprawidłowe"
            );

            // token NIE powinien być wygenerowany
            verify(jwtService, never()).generateToken(anyString(), anyString());
        }
    }
}
