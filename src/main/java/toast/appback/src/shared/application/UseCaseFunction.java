package toast.appback.src.shared.application;

/**
 * Interfaz genérica que define el contrato para ejecutar casos de uso.
 *
 * @param <R> Tipo del resultado retornado por el caso de uso.
 * @param <C> Tipo del comando o entrada requerida para ejecutar el caso de uso.
 */
public interface UseCaseFunction<R, C> {
    /**
     * Ejecuta el caso de uso con el comando proporcionado.
     *
     * @param command Comando para ejecutar el caso de uso.
     * @return Resultado de la ejecución del caso de uso.
     */
    R execute(C command);
}