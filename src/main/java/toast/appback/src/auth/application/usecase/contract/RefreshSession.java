package toast.appback.src.auth.application.usecase.contract;

import toast.appback.src.auth.application.communication.result.AccessToken;
import toast.appback.src.shared.application.UseCaseFunction;

public interface RefreshSession extends UseCaseFunction<AccessToken, String> {
}
