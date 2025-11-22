package toast.appback.src.debts.infrastructure.api.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import toast.appback.src.auth.infrastructure.config.auth.CustomUserDetails;
import toast.appback.src.debts.application.communication.command.CreateQuickDebtCommand;
import toast.appback.src.debts.application.communication.result.DebtView;
import toast.appback.src.debts.application.port.DebtBetweenUsersReadRepository;
import toast.appback.src.debts.application.port.DebtReadRepository;
import toast.appback.src.debts.application.port.QuickDebtReadRepository;
import toast.appback.src.debts.application.usecase.contract.CreateQuickDebt;
import toast.appback.src.debts.infrastructure.api.dto.DebtResponseMapper;
import toast.appback.src.debts.infrastructure.api.dto.request.CreateQuickDebtRequest;
import toast.appback.src.debts.infrastructure.api.dto.response.DebtResponse;
import toast.appback.src.shared.infrastructure.Pageable;
import toast.appback.src.users.application.port.UserReadRepository;
import toast.appback.src.users.domain.UserId;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/quick-debt")
@RequiredArgsConstructor
public class QuickDebtController {
    private final QuickDebtReadRepository quickDebtReadRepository;
    private final CreateQuickDebt createQuickDebt;

    @PostMapping
    public ResponseEntity<DebtResponse> getAllDebts(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody CreateQuickDebtRequest createQuickDebtRequest
            ) {
        CreateQuickDebtCommand command = createQuickDebtRequest.toCreateQuickDebtCommand();
        DebtView debtView = createQuickDebt.execute(command);
        DebtResponse debtResponse = DebtResponseMapper.toDebtResponse(debtView);
        return ResponseEntity.ok(debtResponse);
    }

    @GetMapping("/debtor")
    public ResponseEntity<Pageable<DebtResponse, UUID>> getQuickDebtsAsDebtor(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
            @RequestParam(value = "cursor", required = false) UUID cursor
    ) {
        UserId userId = customUserDetails.getUserId();
        List<DebtResponse> debtsContent;
        if (cursor == null) {
            debtsContent = quickDebtReadRepository.getDebtorQuickDebts(userId, limit+1)
                    .stream().map(DebtResponseMapper::toDebtResponse).toList();
        } else {
            debtsContent = quickDebtReadRepository.getDebtorQuickDebtsAfterCursor(userId, cursor, limit + 1)
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
    public ResponseEntity<Pageable<DebtResponse, UUID>> getQuickDebtsAsCreditor(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(value = "limit", defaultValue = "10", required = false) int limit,
            @RequestParam(value = "cursor", required = false) UUID cursor
    ) {
        UserId userId = customUserDetails.getUserId();
        List<DebtResponse> debtsContent;
        if (cursor == null) {
            debtsContent = quickDebtReadRepository.getCreditorQuickDebts(userId, limit+1)
                    .stream().map(DebtResponseMapper::toDebtResponse).toList();
        } else {
            debtsContent = quickDebtReadRepository.getCreditorQuickDebtsAfterCursor(userId, cursor, limit + 1)
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

//    @PatchMapping("/{debtId}")
//    public ResponseEntity<DebtResponse> editQuickDebt(
//            @AuthenticationPrincipal CustomUserDetails customUserDetails,
//            @PathVariable("debtId") UUID debtId,
//            @RequestBody CreateQuickDebtRequest createQuickDebtRequest
//    ) {
//        CreateQuickDebtCommand command = createQuickDebtRequest.toCreateQuickDebtCommandWithId(debtId);
//        DebtView debtView = createQuickDebt.execute(command);
//        DebtResponse debtResponse = DebtResponseMapper.toDebtResponse(debtView);
//        return ResponseEntity.ok(debtResponse);
//    }
}
