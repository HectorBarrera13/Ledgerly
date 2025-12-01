package toast.appback.src.debts.application.port.services;

public interface DebtStatusUpdateService {
    void updateDebtStatusById(String debtId, String status);
}
