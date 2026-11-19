# Banking System Integration Test Report

## Test purpose

Verify the account lifecycle and login-to-deposit path from the JavaFX boundary through the controllers and domain model to the H2 JDBC data access layer, and verify database migration for the branch attribute.

## Test cases

`BankingIntegrationTest.customerCanLogInDepositAndReadUpdatedBalanceFromDatabase` checks the Bank, controller, model, and DAO path using an isolated in-memory H2 database. `BankingIntegrationTest.firstLaunchSeedsTenCustomersWithOneAccountEach` verifies the ten demo customers and their accounts required for the presentation. `BankingIntegrationTest.existingDatabaseReceivesDefaultBranchColumn` verifies that an existing accounts table gets a default Gaborone branch when the schema is upgraded.

`BankingGuiIntegrationTest.customerCanLogInThroughJavaFxAndDepositIntoDatabase` uses the JavaFX login and account screens. It enters credentials, fires the Login button, opens a savings account at Gaborone Central through the account screen, enters a deposit, and fires the Deposit button. It then reads the branch, account and transaction back through `AccountDAO`.

## Expected result

The customer authenticates, opens a savings account with BWP 100 at Gaborone Central, deposits BWP 25.50, and the DAO returns the same branch, a persisted balance of BWP 125.50, and one deposit transaction. A first launch seeds ten sample customers with one account each. A legacy account receives the default Gaborone branch during migration.

## Observed result

The full integration run completed on 18 November 2026 with:

- Tests run: 4
- Failures: 0
- Errors: 0
- Skipped: 0
- Build: SUCCESS

Run the headless test with `mvn test`. The JavaFX end-to-end test is opt-in and requires an interactive desktop. In PowerShell, run `mvn -o '-Dbank.ui.integration=true' test`.

## Scope limit

The GUI test covers the login and deposit flow through the boundary, controllers, model, and database. The sample-data case validates the ten records required at presentation. The tests do not simulate the 30-day monthly-interest scheduler interval; demonstrate that behavior separately during the final presentation.
