# Add Books API Testing Project

This project is designed to test the Add Books API using Gherkin syntax for behavior-driven development (BDD). The tests are written in `.feature` files and executed using Java and Maven.

## Project Structure

- `src/test/resources/features/`: Contains `.feature` files written in Gherkin syntax to define test scenarios.
- `src/test/java/`: Contains step definitions and test runners for executing the scenarios.

## How to Run Tests

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd <repository-folder>
    ```
2. Run tests using Maven:  
   mvn test
3. 
   Alternatively, run tests directly from IntelliJ IDEA:  
   Open the project in IntelliJ IDEA.
   Navigate to the desired .feature file or test runner.
   Right-click and select Run.
##   Dependencies
   The project uses the following dependencies:  
- **Cucumber**: For writing and executing BDD tests.
- **JUnit**: For running tests.
- **RestAssured**: For API testing.

Dependencies are managed via Maven. See the pom.xml file for details. 
# Adding a New Endpoint and Payloads for API Tests

## Steps to Add a New Endpoint

1. **Define the Endpoint**:
    - Add the new endpoint in the `Endpoint` enum (e.g., `src/test/java/core/endpoints/Endpoint.java`).
    - Example:
      ```java
      public enum Endpoint {
          BOOKS("/books"),
          BOOKS_ID("/books/{id}"),
          NEW_ENDPOINT("/new/endpoint"); // Add your new endpoint here
      }
      ```

2. **Update the Feature File**:
    - Add a new scenario in the `.feature` file (e.g., `src/test/resources/features/`) to test the new endpoint.
    - Example:
      ```gherkin
      Scenario: Test new endpoint
        Given I set params
          | param1 | value1 |
        When I send GET request to NEW_ENDPOINT
        Then I receive response code 200
        Then response matches schema NEW_SCHEMA
      ```

3. **Add Payloads (if required)**:
    - If the new endpoint requires a payload, create a payload file in `src/test/resources/payloads/`.
    - Use placeholders for parameters in the payload, such as ```${parameter_name}``` or ```${parameter_name::default_value} ```.

## How Endpoints and Payloads Are Loaded

1. **Endpoints**:
    - Defined in the `Endpoint` enum.
    - Dynamically set using the `setEndpoint` method in the `ApiHandler` class.
    - Placeholders in the endpoint path (e.g., `${id}`) are replaced with actual values.

2. **Payloads**:
    - Loaded using the `PayloadLoader` class.
    - Placeholders in the payload (e.g., `${id::1001}`) are replaced with actual or default values.

## Example Workflow for Adding a New API Test

1. Define the endpoint in the `Endpoint` enum.
2. Create a payload file (if required) in `src/test/resources/payloads/`.
3. Add a scenario in the `.feature` file.
4. Run the test using the test runner (e.g., `RegressionTestRunner`).

### Framework is not thread safe because of singleton pattern used in ApiActions class. For parallel execution, consider using a different design pattern or refactor the code to avoid shared state.