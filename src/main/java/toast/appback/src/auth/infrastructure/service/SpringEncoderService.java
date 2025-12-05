package toast.appback.src.auth.infrastructure.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import toast.appback.src.auth.domain.service.PasswordHasher;

/**
 * Servicio que delega el hashing y la verificación de contraseñas al encoder de Spring Security.
 *
 * <p>Responsabilidad:
 * - Encapsular la lógica de hashing/verificación de contraseñas usando {@link PasswordEncoder}.
 */
@Service
@RequiredArgsConstructor
public class SpringEncoderService implements PasswordHasher {

    private final PasswordEncoder passwordEncoder;

    /**
     * Genera un hash seguro a partir de la contraseña en texto plano.
     *
     * @param rawPassword Contraseña en texto plano.
     * @return Hash de la contraseña (string) listo para almacenar.
     */
    @Override
    public String hash(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * Verifica si la contraseña en texto plano coincide con el hash almacenado.
     *
     * @param rawPassword    Contraseña en texto plano a verificar.
     * @param hashedPassword Hash almacenado para comparar.
     * @return true si coinciden, false en caso contrario.
     */
    @Override
    public boolean verify(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}