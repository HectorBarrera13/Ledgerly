package toast.appback.src.auth.application.usecase.contract;

import toast.appback.src.shared.application.UseCaseProcedure;

/**
 * Contrato del caso de uso para finalizar/revocar una sesión mediante su identificador (por ejemplo, refresh token).
 * <p>
 * Implementaciones deben validar identidad y revocar la sesión correspondiente.
 */
public interface TerminateSession extends UseCaseProcedure<String> {
}
