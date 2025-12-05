package toast.appback.src.shared.application;

/**
 * Contrato genérico para casos de uso que no retornan valor (procedimientos).
 *
 * @param <C> Tipo del comando de entrada.
 */
public interface UseCaseProcedure<C> {
    /**
     * Ejecuta el procedimiento con el comando dado.
     *
     * @param command Comando de entrada.
     */
    void execute(C command);
}
