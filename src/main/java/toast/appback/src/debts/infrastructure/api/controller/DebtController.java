package toast.appback.src.debts.infrastructure.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import toast.appback.src.auth.infrastructure.config.auth.CustomUserDetails;
import toast.appback.src.debts.application.communication.command.EditDebtCommand;
import toast.appback.src.debts.application.communication.command.EditDebtStatusCommand;
import toast.appback.src.debts.application.communication.result.DebtView;
import toast.appback.src.debts.application.usecase.contract.EditDebt;
import toast.appback.src.debts.application.usecase.contract.EditDebtStatus;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.debts.infrastructure.api.dto.DebtResponseMapper;
import toast.appback.src.debts.infrastructure.api.dto.request.EditDebtRequest;
import toast.appback.src.debts.infrastructure.api.dto.response.DebtResponse;
import toast.appback.src.users.domain.UserId;

import java.util.UUID;

@RestController
@RequestMapping("/debt")
@RequiredArgsConstructor
public class DebtController {
    private final EditDebt editDebt;
    private final EditDebtStatus editDebtStatus;

    @PatchMapping("/{debtId}")
    public ResponseEntity<DebtResponse> editDebt(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody EditDebtRequest editDebtRequest
            ) {
        EditDebtCommand command = editDebtRequest.toEditDebtCommand();
        DebtView debtView = editDebt.execute(command);
        DebtResponse debtResponse = DebtResponseMapper.toDebtResponse(debtView);
        return ResponseEntity.ok(debtResponse);
    }

    @DeleteMapping("/{debtId}")
    public ResponseEntity<Void> deleteDebt(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("debtId") String debtId
    ){
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{debtId}/settle")
    public ResponseEntity<DebtResponse> settleDebt(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("debtId") UUID debtId
    ) {
        UserId userId = customUserDetails.getUserId();
        DebtId requiredDebtId = DebtId.load(debtId);
        EditDebtStatusCommand command = new EditDebtStatusCommand(requiredDebtId, userId);
        DebtView debtView = editDebtStatus.execute(command);
        DebtResponse debtResponse = DebtResponseMapper.toDebtResponse(debtView);
        return ResponseEntity.ok(debtResponse);
    }
}
