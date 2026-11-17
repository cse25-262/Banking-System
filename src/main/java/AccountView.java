

import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class AccountView extends VBox {

    public AccountView() {

        setSpacing(10);

        AccountController controller = new AccountController();

        Label title = new Label("Banking System");
        Label accountNumber = new Label(
            "Account Number: " + controller.getAccountNumber()
        );

        TextField amount = new TextField();
        amount.setPromptText("Enter Amount");

        Button depositButton = new Button("Deposit");
        Button withdrawButton = new Button("Withdraw");
        Button balanceButton = new Button("View Balance");
        Button historyButton = new Button("Transaction History");

        Label message = new Label();

        depositButton.setOnAction(event -> {
            message.setText(controller.deposit(amount.getText()));
        });

        withdrawButton.setOnAction(event -> {
            message.setText(controller.withdraw(amount.getText()));
        });

        balanceButton.setOnAction(event -> {
            message.setText("Balance: BWP " + controller.getBalance());
        });

        historyButton.setOnAction(event -> {
            message.setText(controller.getHistory());
        });

        getChildren().addAll(
            title,
            accountNumber,
            amount,
            depositButton,
            withdrawButton,
            balanceButton,
            historyButton,
            message
        );
    }
}
