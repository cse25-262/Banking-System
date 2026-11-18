import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

@EnabledIfSystemProperty(named = "bank.ui.integration", matches = "true")
class BankingGuiIntegrationTest {
    private static final String TEST_PASSWORD = "TestPassword1";

    @BeforeAll
    static void startJavaFxToolkit() throws Exception {
        CountDownLatch started = new CountDownLatch(1);
        try {
            Platform.startup(() -> started.countDown());
        } catch (IllegalStateException alreadyStarted) {
            started.countDown();
        }
        assertTrue(started.await(10, TimeUnit.SECONDS), "JavaFX toolkit did not start.");
        Platform.setImplicitExit(false);
    }

    @AfterAll
    static void stopJavaFxToolkit() {
        Platform.exit();
    }

    @Test
    void customerCanLogInThroughJavaFxAndDepositIntoDatabase() throws Exception {
        String previousUrl = System.getProperty("bank.db.url");
        System.setProperty("bank.db.url", "jdbc:h2:mem:banking-gui-" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");

        LoginView loginView = new LoginView();
        Stage stage = onFxThread(Stage::new);
        try {
            Bank seedBank = new Bank();
            String username = "integration-" + UUID.randomUUID();
            seedBank.registerCustomer(new Customer(
                "Test", "Customer", "Gaborone", "12345678", username, TEST_PASSWORD));

            onFxThread(() -> {
                loginView.start(stage);
                return null;
            });

            onFxThread(() -> {
                ((TextField) stage.getScene().lookup("#login-username")).setText(username);
                ((PasswordField) stage.getScene().lookup("#login-password")).setText(TEST_PASSWORD);
                ((Button) stage.getScene().lookup("#login-button")).fire();
                return null;
            });

            String[] uiResults = onFxThread(() -> {
                assertTrue(stage.getScene().getRoot() instanceof AccountView,
                    "Successful GUI login should open the account screen.");
                AccountView accountView = (AccountView) stage.getScene().getRoot();
                @SuppressWarnings("unchecked")
                ComboBox<String> type = (ComboBox<String>) accountView.lookup("#account-type");
                type.setValue("Savings");
                ((TextField) accountView.lookup("#opening-balance")).setText("100");
                ((TextField) accountView.lookup("#branch-name")).setText("Gaborone Central");
                ((Button) accountView.lookup("#open-account-button")).fire();

                String openedMessage = ((Label) accountView.lookup("#account-message")).getText();
                @SuppressWarnings("unchecked")
                ComboBox<String> selector = (ComboBox<String>) accountView.lookup("#account-selector");
                String selectedAccountNumber = selector.getValue();
                assertNotNull(selectedAccountNumber, "Opening an account through the GUI should select it.");

                ((TextField) accountView.lookup("#transaction-amount")).setText("25.50");
                ((Button) accountView.lookup("#deposit-button")).fire();
                String depositMessage = ((Label) accountView.lookup("#account-message")).getText();
                return new String[] { openedMessage, depositMessage, selectedAccountNumber };
            });

            assertTrue(uiResults[0].startsWith("Account opened:"), uiResults[0]);
            assertEquals("Deposit successful.", uiResults[1]);

            Account persisted = seedBank.getAccountDAO().read(uiResults[2]);
            assertNotNull(persisted, "The account should be readable from the database.");
            assertEquals("Gaborone Central", persisted.getBranch());
            assertEquals(125.50, persisted.getBalance(), 0.0001);
            assertEquals(1, persisted.getTransactions().size());
        } finally {
            onFxThread(() -> {
                stage.close();
                loginView.stop();
                return null;
            });
            if (previousUrl == null) System.clearProperty("bank.db.url");
            else System.setProperty("bank.db.url", previousUrl);
        }
    }

    private static <T> T onFxThread(Callable<T> action) throws Exception {
        if (Platform.isFxApplicationThread()) return action.call();
        FutureTask<T> task = new FutureTask<>(action);
        Platform.runLater(task);
        return task.get(15, TimeUnit.SECONDS);
    }
}
