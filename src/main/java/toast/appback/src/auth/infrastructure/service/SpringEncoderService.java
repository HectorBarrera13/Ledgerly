package toast.appback.src.auth.infrastructure.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import toast.appback.src.auth.domain.service.PasswordHasher;

@Service
@RequiredArgsConstructor
public class SpringEncoderService implements PasswordHasher {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(SpringEncoderService.class);

    private final PasswordEncoder passwordEncoder;

    @Override
    public String hash(String rawPassword) {
        log.debug("Hashing password");
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean verify(String rawPassword, String hashedPassword) {
        log.debug("Verifying password: rawPassword={}, hashedPassword={}", rawPassword, hashedPassword);
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}