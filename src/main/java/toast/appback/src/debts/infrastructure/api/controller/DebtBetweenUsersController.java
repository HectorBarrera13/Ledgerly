package toast.appback.src.debts.infrastructure.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import toast.appback.src.auth.infrastructure.config.auth.CustomUserDetails;
import toast.appback.src.debts.application.communication.command.CreateDebtBetweenUsersCommand;
import toast.appback.src.debts.application.communication.command.EditDebtStatusCommand;
import toast.appback.src.debts.application.communication.result.DebtView;
import toast.appback.src.debts.application.port.DebtBetweenUsersReadRepository;
import toast.appback.src.debts.application.port.DebtReadRepository;
import toast.appback.src.debts.application.usecase.contract.CreateDebtBetweenUsers;
import toast.appback.src.debts.application.usecase.contract.EditDebtStatus;
import toast.appback.src.debts.application.usecase.implementation.RejectDebtPaymentUseCase;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.debts.infrastructure.api.dto.DebtResponseMapper;
import toast.appback.src.debts.infrastructure.api.dto.request.CreateDebtBetweenUsersRequest;
import toast.appback.src.debts.infrastructure.api.dto.response.DebtResponse;
import toast.appback.src.shared.infrastructure.Pageable;
import toast.appback.src.users.domain.UserId;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/debt-between-users")
public class DebtBetweenUsersController {

    private final EditDebtStatus rejectDebtPaymentUseCase;
    private final EditDebtStatus confirmDebtPaymentUseCase;
    private final EditDebtStatus reportDebtPaymentUseCase;
    private final CreateDebtBetweenUsers createDebtBetweenUsers;
    private final DebtReadRepository debtReadRepository;
    private final DebtBetweenUsersReadRepository debtBetweenUsersReadRepository;

    public DebtBetweenUsersController(
            @Qualifier("rejectDebtPaymentUseCase") EditDebtStatus reject,
            @Qualifier("confirmDebtPaymentUseCase") EditDebtStatus confirm,
            @Qualifier("reportDebtPaymentUseCase") EditDebtStatus report,
            CreateDebtBetweenUsers createDebtBetweenUsers,
            DebtReadRepository debtReadRepository,
            DebtBetweenUsersReadRepository debtBetweenUsersReadRepository
    ) {
        this.rejectDebtPaymentUseCase = reject;
        this.confirmDebtPaymentUseCase = confirm;
        this.reportDebtPaymentUseCase = report;
        this.createDebtBetweenUsers = createDebtBetweenUsers;
        this.debtReadRepository = debtReadRepository;
        this.debtBetweenUsersReadRepository = debtBetweenUsersReadRepository;
    }


    @PostMapping()
    public ResponseEntity<DebtResponse> createDebtBetweenUsers(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody CreateDebtBetweenUsersRequest request
    ) {
        CreateDebtBetweenUsersCommand command = request.toCreateDebtBetweenUsersCommand();
        DebtView debtView = createDebtBetweenUsers.execute(command);
        DebtResponse debtResponse = DebtResponseMapper.toDebtResponse(debtView);
        return ResponseEntity.ok(debtResponse);
    }

    @GetMapping("/debtor")
    public ResponseEntity<Pageable<DebtResponse, UUID>> getDebtBetweenUsersAsDebtor(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
            @RequestParam(value = "cursor", required = false) UUID cursor
    ) {
        UserId userId = customUserDetails.getUserId();
        List<DebtResponse> debtsContent;
        if (cursor == null) {
            debtsContent = debtBetweenUsersReadRepository.getDebtorDebtsBetweenUsers(userId, limit + 1)
                    .stream().map(DebtResponseMapper::toDebtResponse).toList();
        } else {
            debtsContent = debtBetweenUsersReadRepository.getDebtorDebtsBetweenUsersAfterCursor(userId, cursor, limit + 1)
                    .stream().map(DebtResponseMapper::toDebtResponse).toList();
        }
        if (debtsContent.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        int length = debtsContent.size();
        List<DebtResponse> pagedDebts = length > limit
                ? debtsContent.subList(0, limit)
                : debtsContent;
        var response = new Pageable<>(
                pagedDebts,
                length > limit ? pagedDebts.getLast().id() : null
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/creditor")
    public ResponseEntity<Pageable<DebtResponse, UUID>> getDebtBetweenUsersAsCreditor(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
            @RequestParam(value = "cursor", required = false) UUID cursor
    ) {
        UserId userId = customUserDetails.getUserId();
        List<DebtResponse> debtsContent;
        if (cursor == null) {
            debtsContent = debtBetweenUsersReadRepository.getCreditorDebtsBetweenUsers(userId, limit + 1)
                    .stream().map(DebtResponseMapper::toDebtResponse).toList();
        } else {
            debtsContent = debtBetweenUsersReadRepository.getCreditorDebtsBetweenUsersAfterCursor(userId, cursor, limit + 1)
                    .stream().map(DebtResponseMapper::toDebtResponse).toList();
        }
        if (debtsContent.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        int length = debtsContent.size();
        List<DebtResponse> pagedDebts = length > limit
                ? debtsContent.subList(0, limit)
                : debtsContent;
        var response = new Pageable<>(
                pagedDebts,
                length > limit ? pagedDebts.getLast().id() : null
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{debtId}/verify-payment")
    public ResponseEntity<DebtResponse> verifyDebtPayment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("debtId") UUID debtId
    ) {
        UserId userId = customUserDetails.getUserId();
        DebtId requiredDebtId = DebtId.load(debtId);
        EditDebtStatusCommand command = new EditDebtStatusCommand(requiredDebtId, userId);
        DebtView debtView = confirmDebtPaymentUseCase.execute(command);
        DebtResponse debtResponse = DebtResponseMapper.toDebtResponse(debtView);
        return ResponseEntity.ok(debtResponse);
    }

    @PostMapping("/{debtId}/reject-payment")
    public ResponseEntity<DebtResponse> rejectDebtPayment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("debtId") UUID debtId
    ) {
        UserId userId = customUserDetails.getUserId();
        DebtId requiredDebtId = DebtId.load(debtId);
        EditDebtStatusCommand command = new EditDebtStatusCommand(requiredDebtId, userId);
        DebtView debtView = rejectDebtPaymentUseCase.execute(command);
        DebtResponse debtResponse = DebtResponseMapper.toDebtResponse(debtView);
        return ResponseEntity.ok(debtResponse);
    }

    @PostMapping("/{debtId}/settle")
    public ResponseEntity<DebtResponse> settleDebt(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("debtId") UUID debtId
    ) {
        UserId userId = customUserDetails.getUserId();
        DebtId requiredDebtId = DebtId.load(debtId);
        EditDebtStatusCommand command = new EditDebtStatusCommand(requiredDebtId, userId);
        DebtView debtView = reportDebtPaymentUseCase.execute(command);
        DebtResponse debtResponse = DebtResponseMapper.toDebtResponse(debtView);
        return ResponseEntity.ok(debtResponse);

    }
}
