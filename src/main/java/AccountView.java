import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class AccountView extends VBox {

    public AccountView(AccountController controller) {
        setSpacing(10);
        setPadding(new Insets(18));

        Label title = new Label("Banking System — " + controller.getCustomerDisplayName());
        ComboBox<String> accountSelector = new ComboBox<>(FXCollections.observableArrayList(controller.getAccountNumbers()));
        accountSelector.setId("account-selector");
        accountSelector.setPromptText("Select an account");
        accountSelector.setMaxWidth(Double.MAX_VALUE);
        accountSelector.valueProperty().addListener((obs, oldAccount, newAccount) -> controller.selectAccount(newAccount));

        Label openHeading = new Label("Open an account");
        ComboBox<String> accountType = new ComboBox<>(FXCollections.observableArrayList(controller.getSupportedAccountTypes()));
        accountType.setId("account-type");
        accountType.getSelectionModel().selectFirst();
        TextField openingBalance = new TextField("0");
        openingBalance.setId("opening-balance");
        openingBalance.setPromptText("Opening balance (BWP)");
        TextField branchName = new TextField(controller.getDefaultBranch());
        branchName.setId("branch-name");
        branchName.setPromptText("Branch");
        TextField employerName = new TextField(); employerName.setPromptText("Employer name (Cheque only)");
        TextField employerAddress = new TextField(); employerAddress.setPromptText("Employer address (Cheque only)");
        Button openButton = new Button("Open account");
        openButton.setId("open-account-button");

        TextField amount = new TextField(); amount.setPromptText("Amount (BWP)");
        amount.setId("transaction-amount");
        Button depositButton = new Button("Deposit");
        depositButton.setId("deposit-button");
        Button withdrawButton = new Button("Withdraw");
        Button balanceButton = new Button("View balance");
        Button historyButton = new Button("Transaction history");
        Button interestButton = new Button("Apply interest");
        Label message = new Label();
        message.setId("account-message");

        openButton.setOnAction(event -> {
            try {
                String createdAccountNumber = controller.openAccount(accountType.getValue(), openingBalance.getText(), branchName.getText(), employerName.getText(), employerAddress.getText());
                accountSelector.getItems().setAll(controller.getAccountNumbers());
                accountSelector.getSelectionModel().select(createdAccountNumber);
                message.setText("Account opened: " + createdAccountNumber);
            } catch (IllegalArgumentException e) {
                message.setText(e.getMessage() == null ? "Enter a valid opening balance." : e.getMessage());
            } catch (IllegalStateException e) { message.setText(e.getMessage()); }
        });
        depositButton.setOnAction(event -> message.setText(controller.deposit(amount.getText())));
        withdrawButton.setOnAction(event -> message.setText(controller.withdraw(amount.getText())));
        balanceButton.setOnAction(event -> {
            try { message.setText("Balance: BWP " + controller.getBalance()); }
            catch (IllegalStateException e) { message.setText(e.getMessage()); }
        });
        historyButton.setOnAction(event -> {
            try { message.setText(controller.getHistory()); }
            catch (IllegalStateException e) { message.setText(e.getMessage()); }
        });
        interestButton.setOnAction(event -> {
            try { message.setText(controller.applyInterest()); }
            catch (IllegalStateException e) { message.setText(e.getMessage()); }
        });

        getChildren().addAll(title, new Label("Your accounts"), accountSelector, openHeading, accountType,
            openingBalance, branchName, employerName, employerAddress, openButton, amount, depositButton,
            withdrawButton, balanceButton, historyButton, interestButton, message);
    }
}
