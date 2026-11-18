# Banking System

JavaFX banking assignment with a JDBC/H2 data layer.

## Run

Use JDK 25 and Maven, then run `mvn javafx:run` from the project root. The application creates `./data/banking.mv.db` on first launch.

The first launch seeds ten example customers (`sample01` through `sample10`), each with password `Password123` and one sample account. You can also register a customer from the login screen, then open savings, investment, or cheque accounts. New accounts default to the Gaborone branch; the branch can be changed on the account screen.

Investment accounts require at least BWP 500. Cheque accounts require employer name and address. Savings accounts reject withdrawals. Passwords are stored as salted PBKDF2 hashes. The application applies eligible account interest every 30 days while it is running; the Apply Interest button can apply the current month's interest early, and the database prevents a second application in the same month.

## Persistence

`CustomerDAO` and `AccountDAO` provide create, read, update, and delete operations. `DatabaseManager` creates the `customers`, `accounts`, and `transactions` tables. Set the `bank.db.url` Java system property to use another JDBC URL, such as an in-memory H2 database for development.

Run the database integration, ten-record seed-data, and schema-migration tests with `mvn test`.

To run the full JavaFX screen-to-database flow, use `mvn '-Dbank.ui.integration=true' test` from PowerShell in an interactive desktop session. The opt-in GUI test enters login details, opens a savings account, deposits funds, and verifies the persisted balance and transaction. Keeping this test opt-in allows the normal Maven test command to run in headless Codespaces.
