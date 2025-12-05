package toast.appback.src.users.infrastructure.service;

import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.infrastructure.exceptions.FriendRequestException;

import java.io.IOException;

/**
 * Servicio responsable de generar códigos QR para peticiones de amistad.
 *
 * <p>Encapsula la lógica de configuración (ancho/alto) y delega la generación al util {@link QRGenerator}.
 * Lancea {@link FriendRequestException} si la generación falla.
 */
@Service
public class FriendRequestQRService {

    @Value("${friendRequest.qr.width}")
    private int qrWidth;
    @Value("${friendRequest.qr.height}")
    private int qrHeight;

    /**
     * Genera un código QR en bytes (JPEG) para el identificador de usuario proporcionado.
     *
     * @param userId Identificador del usuario origen de la petición de amistad.
     * @return Array de bytes con la imagen JPEG del QR.
     * @throws FriendRequestException si falla la generación del QR.
     */
    public byte[] generateQR(UserId userId) {
        try {
            return QRGenerator.generate(userId.getValue().toString(), qrWidth, qrHeight);
        } catch (WriterException | IOException e) {
            throw new FriendRequestException("Failed to generate QR code for friend request", e);
        }
    }
}
