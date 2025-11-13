package toast.appback.src.users.infrastructure.service;

import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import toast.appback.src.users.domain.UserId;
import toast.appback.src.users.infrastructure.exceptions.FriendRequestException;

import java.io.IOException;

@Service
public class FriendRequestQRService {

    @Value("${friendRequest.qr.width}")
    private int qrWidth;
    @Value("${friendRequest.qr.height}")
    private int qrHeight;

    public byte[] generateQR(UserId userId) {
        try {
            return QRGenerator.generate(userId.getValue().toString(), qrWidth, qrHeight);
        } catch (WriterException | IOException e) {
            throw new FriendRequestException("Failed to generate QR code for friend request", e);
        }
    }
}
