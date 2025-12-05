package toast.appback.src.users.infrastructure.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Generador de códigos QR en formato JPEG.
 * <p>
 * Clase utilitaria estática.
 */
public class QRGenerator {
    private QRGenerator() {
    }

    /**
     * Genera un JPEG con el código QR que representa el contenido proporcionado.
     *
     * @param content Texto a codificar en el QR.
     * @param width   Ancho de la imagen en píxeles.
     * @param height  Alto de la imagen en píxeles.
     * @return Array de bytes con la imagen JPEG.
     * @throws WriterException Si la librería de QR falla al generar la matriz.
     * @throws IOException     Si hay un error al escribir el flujo de bytes.
     */
    public static byte[] generate(String content, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "JPEG", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
