package toast.appback.src.shared.domain;

/**
 * Interfaz para códigos de negocio que permiten clasificar errores/estados esperados.
 * Implementaciones concretas pueden definir enumeraciones que implementen este contrato.
 */
public interface BusinessCode {
    BusinessCode code();
}
