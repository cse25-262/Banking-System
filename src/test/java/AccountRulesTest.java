import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AccountRulesTest {
    @Test
    void depositsRecordTheirDescriptionAndRejectInvalidOrOverflowingBalances() {
        SavingsAccount account = new SavingsAccount("S-1", 10);

        account.deposit(5, "Cash deposit");

        assertEquals(15, account.getBalance(), 0.0001);
        assertEquals(TransactionType.DEPOSIT, account.getTransactions().get(0).getType());
        assertEquals("Cash deposit", account.getTransactions().get(0).getDescription());
        assertThrows(IllegalArgumentException.class, () -> account.deposit(0));
        assertThrows(IllegalArgumentException.class, () -> account.deposit(Double.NaN));

        SavingsAccount maximumBalance = new SavingsAccount("S-2", Double.MAX_VALUE);
        assertThrows(IllegalArgumentException.class, () -> maximumBalance.deposit(Double.MAX_VALUE));
        assertEquals(Double.MAX_VALUE, maximumBalance.getBalance());
    }

    @Test
    void savingsAndInvestmentRulesAreEnforced() {
        SavingsAccount savings = new SavingsAccount("S-3", 1000);
        assertThrows(UnsupportedOperationException.class, () -> savings.withdraw(1));
        assertEquals(0.5, savings.calculateInterest(), 0.0001);
        savings.applyInterest();
        assertEquals(1000.5, savings.getBalance(), 0.0001);

        assertThrows(IllegalArgumentException.class, () -> new InvestmentAccount("I-1", 499.99));
        InvestmentAccount investment = new InvestmentAccount("I-2", 500);
        assertEquals(25, investment.calculateInterest(), 0.0001);
        investment.applyInterest();
        assertEquals(525, investment.getBalance(), 0.0001);
    }

    @Test
    void chequeAccountsRequireEmployerDetailsAndBranchesArePreserved() {
        assertThrows(IllegalArgumentException.class,
            () -> new ChequeAccount("C-1", 100, "", "Gaborone"));
        assertThrows(IllegalArgumentException.class,
            () -> new ChequeAccount("C-2", 100, "Employer", " "));

        ChequeAccount cheque = new ChequeAccount("C-3", 100, "Francistown", "Employer", "Gaborone");
        assertEquals("Francistown", cheque.getBranch());
        assertEquals("Employer", cheque.getEmployerName());
        assertEquals("Gaborone", cheque.getEmployerAddress());
        assertTrue(cheque.getTransactions().isEmpty());
    }
}
