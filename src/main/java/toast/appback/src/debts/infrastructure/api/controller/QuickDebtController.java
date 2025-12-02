package toast.appback.src.debts.infrastructure.api.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import toast.appback.src.auth.infrastructure.config.auth.CustomUserDetails;
import toast.appback.src.debts.application.communication.command.CreateQuickDebtCommand;
import toast.appback.src.debts.application.communication.command.EditDebtCommand;
import toast.appback.src.debts.application.communication.command.EditDebtStatusCommand;
import toast.appback.src.debts.application.communication.result.QuickDebtView;
import toast.appback.src.debts.application.port.QuickDebtReadRepository;
import toast.appback.src.debts.application.usecase.contract.CreateQuickDebt;
import toast.appback.src.debts.application.usecase.contract.EditQuickDebtStatus;
import toast.appback.src.debts.domain.vo.DebtId;
import toast.appback.src.debts.infrastructure.api.dto.DebtResponseMapper;
import toast.appback.src.debts.infrastructure.api.dto.request.CreateQuickDebtRequest;
import toast.appback.src.debts.infrastructure.api.dto.request.EditDebtRequest;
import toast.appback.src.debts.infrastructure.api.dto.response.QuickDebtResponse;
import toast.appback.src.debts.infrastructure.service.transactional.EditQuickDebtService;
import toast.appback.src.shared.infrastructure.Pageable;
import toast.appback.src.users.domain.UserId;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/quick-debt")
@RequiredArgsConstructor
public class QuickDebtController {
    private final QuickDebtReadRepository quickDebtReadRepository;
    private final EditQuickDebtStatus settleQuickDebt;
    private final CreateQuickDebt createQuickDebt;
    private final EditQuickDebtService editQuickDebt;

    @PostMapping
    public ResponseEntity<QuickDebtResponse> createQuickDebt(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody CreateQuickDebtRequest createQuickDebtRequest
            ) {
        UserId userId = customUserDetails.getUserId();
        CreateQuickDebtCommand command = createQuickDebtRequest.toCreateQuickDebtCommand(userId);
        QuickDebtView debtView = createQuickDebt.execute(command);
        QuickDebtResponse debtResponse = DebtResponseMapper.toQuickDebtResponse(debtView);
        return ResponseEntity.ok(debtResponse);
    }

    @GetMapping("/{debtId}")
    public ResponseEntity<QuickDebtResponse> getQuickDebtById(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("debtId") UUID debtId
    ) {
        DebtId requiredDebtId = DebtId.load(debtId);
        QuickDebtView debtView = quickDebtReadRepository.findById(requiredDebtId).orElseThrow(
                () -> new IllegalArgumentException("Debt not found")
        );
        QuickDebtResponse debtResponse = DebtResponseMapper.toQuickDebtResponse(debtView);
        return ResponseEntity.ok(debtResponse);
    }

    @PostMapping("/{debtId}/settle")
    public ResponseEntity<QuickDebtResponse> settleQuickDebt(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("debtId") UUID debtId
    ) {
        UserId userId = customUserDetails.getUserId();
        DebtId requiredDebtId = DebtId.load(debtId);
        EditDebtStatusCommand command = new EditDebtStatusCommand(requiredDebtId, userId);
        QuickDebtView debtView = settleQuickDebt.execute(command);
        QuickDebtResponse debtResponse = DebtResponseMapper.toQuickDebtResponse(debtView);
        return ResponseEntity.ok(debtResponse);
    }

    @GetMapping("")
    public ResponseEntity<Pageable<QuickDebtResponse, UUID>> getQuickDebtsByRole(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
            @RequestParam(value = "cursor", required = false) UUID cursor,
            @RequestParam(value = "role", required = true) String role
    ) {
        UserId userId = customUserDetails.getUserId();
        List<QuickDebtResponse> debtsContent;
        if (cursor == null) {
            debtsContent = quickDebtReadRepository.getQuickDebts(userId, role,limit+1)
                    .stream().map(DebtResponseMapper::toQuickDebtResponse).toList();
        } else {
            debtsContent = quickDebtReadRepository.getQuickDebtsAfterCursor(userId, role, cursor, limit + 1)
                    .stream().map(DebtResponseMapper::toQuickDebtResponse).toList();
        }
        if (debtsContent.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        int length = debtsContent.size();
        List<QuickDebtResponse> pagedDebts = length > limit
                ? debtsContent.subList(0, limit)
                : debtsContent;
        var response = new Pageable<>(
                pagedDebts,
                length > limit ? pagedDebts.getLast().id() : null
        );
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{debtId}")
    public ResponseEntity<QuickDebtResponse> editQuickDebt(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable("debtId") UUID debtId,
            @RequestBody EditDebtRequest request
    ) {
        UserId userId = customUserDetails.getUserId();
        DebtId requiredDebtId = DebtId.load(debtId);
        EditDebtCommand command = request.toEditDebtCommand(userId, requiredDebtId);
        QuickDebtView debtView = editQuickDebt.execute(command);
        QuickDebtResponse debtResponse = DebtResponseMapper.toQuickDebtResponse(debtView);
        return ResponseEntity.ok(debtResponse);
    }
}
