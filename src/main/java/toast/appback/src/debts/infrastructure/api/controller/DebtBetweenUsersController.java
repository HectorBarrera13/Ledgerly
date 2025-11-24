package toast.appback.src.debts.infrastructure.api.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import toast.appback.src.auth.infrastructure.config.auth.CustomUserDetails;
import toast.appback.src.debts.application.communication.command.CreateDebtBetweenUsersCommand;
import toast.appback.src.debts.application.communication.command.EditDebtStatusCommand;
import toast.appback.src.debts.application.communication.result.DebtBetweenUsersView;
import toast.appback.src.debts.application.communication.result.DebtView;
import toast.appback.src.debts.application.port.DebtBetweenUsersReadRepository;
import toast.appback.src.debts.application.port.DebtReadRepository;
import toast.appback.src.debts.application.usecase.contract.CreateDebtBetweenUsers;
import toast.appback.src.debts.application.usecase.contract.EditDebtBetweenUsersStatus;
import toast.appback.src.debts.application.usecase.implementation.CreateDebtBetweenUsersUseCase;
import toast.appback.src.debts.domain.repository.DebtRepository;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.debts.infrastructure.api.dto.DebtResponseMapper;
import toast.appback.src.debts.infrastructure.api.dto.request.CreateDebtBetweenUsersRequest;
import toast.appback.src.debts.infrastructure.api.dto.response.DebtBetweenUsersResponse;
import toast.appback.src.debts.infrastructure.api.dto.response.DebtResponse;
import toast.appback.src.shared.infrastructure.Pageable;
import toast.appback.src.users.domain.UserId;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/debt-between-users")
public class DebtBetweenUsersController {
    private final EditDebtBetweenUsersStatus acceptDebtUseCase;
    private final EditDebtBetweenUsersStatus declineDebtUseCase;
    private final EditDebtBetweenUsersStatus reportDebtPaymentUseCase;
    private final EditDebtBetweenUsersStatus confirmDebtPaymentUseCase;
    private final EditDebtBetweenUsersStatus rejectDebtPaymentUseCase;
    private final CreateDebtBetweenUsers createDebtBetweenUsersUseCase;
    private final DebtRepository debtRepository;
    private final DebtBetweenUsersReadRepository debtBetweenUsersReadRepository;

    public DebtBetweenUsersController(
            @Qualifier("acceptDebtUseCase") EditDebtBetweenUsersStatus acceptDebtUseCase,
            @Qualifier("declineDebtUseCase") EditDebtBetweenUsersStatus declineDebtUseCase,
            @Qualifier("rejectDebtPaymentUseCase") EditDebtBetweenUsersStatus reject,
            @Qualifier("confirmDebtPaymentUseCase") EditDebtBetweenUsersStatus confirm,
            @Qualifier("reportDebtPaymentUseCase") EditDebtBetweenUsersStatus report,
            CreateDebtBetweenUsers createDebtBetweenUsers,
            DebtRepository debtRepository,
            DebtBetweenUsersReadRepository debtBetweenUsersReadRepository
    ) {
        this.acceptDebtUseCase = acceptDebtUseCase;
        this.declineDebtUseCase = declineDebtUseCase;
        this.rejectDebtPaymentUseCase = reject;
        this.confirmDebtPaymentUseCase = confirm;
        this.reportDebtPaymentUseCase = report;
        this.createDebtBetweenUsersUseCase = createDebtBetweenUsers;
        this.debtRepository = debtRepository;
        this.debtBetweenUsersReadRepository = debtBetweenUsersReadRepository;
    }

    @PostMapping()
    public ResponseEntity<DebtBetweenUsersResponse> createDebtBetweenUsers(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody CreateDebtBetweenUsersRequest request
    ) {
        UserId userId = customUserDetails.getUserId();
        CreateDebtBetweenUsersCommand command = request.toCreateDebtBetweenUsersCommand(userId);
        DebtBetweenUsersView debtView = createDebtBetweenUsersUseCase.execute(command);
        DebtBetweenUsersResponse debtResponse = DebtResponseMapper.toDebtBetweenUsersResponse(debtView);
        return ResponseEntity.ok(debtResponse);
    }

    @GetMapping("/{debtId}")
    public ResponseEntity<DebtBetweenUsersResponse> getDebtBetweenUsersById(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("debtId") UUID debtId
    ) {
        DebtId requiredDebtId = DebtId.load(debtId);
        DebtBetweenUsersView debtView = debtBetweenUsersReadRepository.findById(requiredDebtId).orElseThrow(() ->
                new IllegalArgumentException("Debt between users not found"));
        DebtBetweenUsersResponse debtResponse = DebtResponseMapper.toDebtBetweenUsersResponse(debtView);
        return ResponseEntity.ok(debtResponse);
    }

    @GetMapping()
    public ResponseEntity<Pageable<DebtBetweenUsersResponse, UUID>> getDebtBetweenUsersByRole(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
            @RequestParam(value = "cursor", required = false) UUID cursor,
            @RequestParam(value = "role", required = true) String role
    ) {
        UserId userId = customUserDetails.getUserId();
        List<DebtBetweenUsersResponse> debtsContent;
        if (cursor == null) {
            debtsContent = debtBetweenUsersReadRepository.getDebtsBetweenUsers(userId, role,  limit + 1)
                    .stream().map(DebtResponseMapper::toDebtBetweenUsersResponse).toList();
        } else {
            debtsContent = debtBetweenUsersReadRepository.getDebtsBetweenUsersAfterCursor(userId,role, cursor, limit + 1)
                    .stream().map(DebtResponseMapper::toDebtBetweenUsersResponse).toList();
        }
        if (debtsContent.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        int length = debtsContent.size();
        List<DebtBetweenUsersResponse> pagedDebts = length > limit
                ? debtsContent.subList(0, limit)
                : debtsContent;
        var response = new Pageable<>(
                pagedDebts,
                length > limit ? pagedDebts.getLast().id() : null
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{debtId}/accept-debt")
    public ResponseEntity<DebtBetweenUsersResponse> settleDebt(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("debtId") UUID debtId
    ) {
        System.out.println("Accepting debt with ID: " + debtId);
        UserId userId = customUserDetails.getUserId();
        DebtId requiredDebtId = DebtId.load(debtId);
        EditDebtStatusCommand command = new EditDebtStatusCommand(requiredDebtId, userId);
        DebtBetweenUsersView debtView = acceptDebtUseCase.execute(command);
        DebtBetweenUsersResponse debtResponse = DebtResponseMapper.toDebtBetweenUsersResponse(debtView);
        return ResponseEntity.ok(debtResponse);
    }

    @PostMapping("/{debtId}/decline-debt")
    public ResponseEntity<DebtBetweenUsersResponse> declineDebt(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("debtId") UUID debtId
    ) {
        UserId userId = customUserDetails.getUserId();
        DebtId requiredDebtId = DebtId.load(debtId);
        EditDebtStatusCommand command = new EditDebtStatusCommand(requiredDebtId, userId);
        DebtBetweenUsersView debtView = declineDebtUseCase.execute(command);
        DebtBetweenUsersResponse debtResponse = DebtResponseMapper.toDebtBetweenUsersResponse(debtView);
        return ResponseEntity.ok(debtResponse);
    }

    @PostMapping("/{debtId}/report-payment")
    public ResponseEntity<DebtBetweenUsersResponse> reportDebtPayment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("debtId") UUID debtId
    ) {
        UserId userId = customUserDetails.getUserId();
        DebtId requiredDebtId = DebtId.load(debtId);
        EditDebtStatusCommand command = new EditDebtStatusCommand(requiredDebtId, userId);
        DebtBetweenUsersView debtView = reportDebtPaymentUseCase.execute(command);
        DebtBetweenUsersResponse debtResponse = DebtResponseMapper.toDebtBetweenUsersResponse(debtView);
        return ResponseEntity.ok(debtResponse);
    }

    @PostMapping("/{debtId}/verify-payment")
    public ResponseEntity<DebtBetweenUsersResponse> verifyDebtPayment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("debtId") UUID debtId
    ) {
        UserId userId = customUserDetails.getUserId();
        DebtId requiredDebtId = DebtId.load(debtId);
        EditDebtStatusCommand command = new EditDebtStatusCommand(requiredDebtId, userId);
        DebtBetweenUsersView debtView = confirmDebtPaymentUseCase.execute(command);
        DebtBetweenUsersResponse debtResponse = DebtResponseMapper.toDebtBetweenUsersResponse(debtView);
        return ResponseEntity.ok(debtResponse);
    }

    @PostMapping("/{debtId}/reject-payment")
    public ResponseEntity<DebtBetweenUsersResponse> rejectDebtPayment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("debtId") UUID debtId
    ) {
        UserId userId = customUserDetails.getUserId();
        DebtId requiredDebtId = DebtId.load(debtId);
        EditDebtStatusCommand command = new EditDebtStatusCommand(requiredDebtId, userId);
        DebtBetweenUsersView debtView = rejectDebtPaymentUseCase.execute(command);
        DebtBetweenUsersResponse debtResponse = DebtResponseMapper.toDebtBetweenUsersResponse(debtView);
        return ResponseEntity.ok(debtResponse);
    }


}
