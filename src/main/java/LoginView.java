

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView extends Application {

    @Override
    public void start(Stage stage) {

        LoginController controller = new LoginController();

        Label title = new Label("Banking System Login");

        TextField username = new TextField();
        username.setPromptText("Username");

        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        Button loginButton = new Button("Login");
        Label message = new Label();

        loginButton.setOnAction(event -> {

            if (controller.login(username.getText(),
                                 password.getText())) {

                AccountView accountView = new AccountView();

                Scene accountScene = new Scene(
                    accountView, 450, 450
                );

                stage.setScene(accountScene);
                stage.setTitle("Bank Account");

            } else {
                message.setText("Incorrect username or password");
            }
        });

        VBox layout = new VBox(15);
        layout.setStyle("-fx-padding: 20;");

        layout.getChildren().addAll(
            title, username, password, loginButton, message
        );

        Scene scene = new Scene(layout, 350, 250);

        stage.setTitle("Banking System Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
