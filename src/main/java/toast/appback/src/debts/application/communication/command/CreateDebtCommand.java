package toast.appback.src.debts.application.communication.command;

public record CreateDebtCommand(String purpose, String description, String currency, Long amount) {

}
