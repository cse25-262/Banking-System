import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView extends Application {
    private LoginController controller;

    @Override
    public void start(Stage stage) {
        controller = new LoginController();
        controller.startMonthlyInterestSchedule();

        Label title = new Label("Banking System Login");

        TextField username = new TextField();
        username.setId("login-username");
        username.setPromptText("Username");

        PasswordField password = new PasswordField();
        password.setId("login-password");
        password.setPromptText("Password");

        Button loginButton = new Button("Login");
        loginButton.setId("login-button");
        Button registerButton = new Button("Register account");
        Label message = new Label();

        loginButton.setOnAction(event -> {
            if (controller.login(username.getText(), password.getText())) {
                showAccountScreen(stage);
            } else {
                message.setText("Incorrect username or password");
            }
        });

        registerButton.setOnAction(event -> {
            TextInputDialog nameDialog = new TextInputDialog();
            nameDialog.setHeaderText("Enter your first name");
            String first = nameDialog.showAndWait().orElse("");
            TextInputDialog surnameDialog = new TextInputDialog();
            surnameDialog.setHeaderText("Enter your surname");
            String surname = surnameDialog.showAndWait().orElse("");
            TextInputDialog addressDialog = new TextInputDialog();
            addressDialog.setHeaderText("Enter your address");
            String address = addressDialog.showAndWait().orElse("");
            TextInputDialog phoneDialog = new TextInputDialog();
            phoneDialog.setHeaderText("Enter your contact number");
            String phone = phoneDialog.showAndWait().orElse("");
            boolean registered = controller.register(first, surname, address, phone, username.getText(), password.getText());
            if (registered) {
                showAccountScreen(stage);
            } else {
                message.setText("Enter a name, username and password that are not already registered.");
            }
        });

        VBox layout = new VBox(15);
        layout.setStyle("-fx-padding: 20;");
        layout.getChildren().addAll(title, username, password, loginButton, registerButton, message);

        stage.setTitle("Banking System Login");
        stage.setScene(new Scene(layout, 350, 250));
        stage.show();
    }

    private void showAccountScreen(Stage stage) {
        AccountController accountController = controller.createAccountController();
        stage.setScene(new Scene(new AccountView(accountController), 450, 600));
        stage.setTitle("Bank Account");
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        if (controller != null) controller.stopMonthlyInterestSchedule();
    }
}
