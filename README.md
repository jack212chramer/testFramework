# Java API Testing Framework

This project is a **sample framework** for automated REST API testing in Java, using:

- **Rest Assured** for API testing
- **JUnit 5** for test management
- **Allure** for test reporting
- **GitHub Actions** for continuous integration

---

##  Getting Started

1. **Clone the repository:**
    ```bash
    git clone https://github.com/your-repository-name.git
    ```

2. **Run tests :**
    ```bash
    mvn -B clean test
    ```

---

## Project Overview

- All test cases are located in the `src/test/java/tests` directory.
- The provided test cases are **examples** - they do not cover all possible scenarios - because I don't have any business requirements and specifications.
- The framework is designed to be easily extendable.

---

##  GitHub Actions & Allure Reports

- This project includes a ready-to-use GitHub Actions workflow at `.github/workflows/tests.yml`.
- Tests run automatically on every push or pull request.
- After each workflow run, **the Allure report is automatically published to GitHub Pages**.  
  You can always access the latest interactive test report from the repository’s GitHub Pages site.

---

##  Adding New Tests

1. Add a new test class under `src/test/java/tests`.
2. Use the existing examples as templates for your own scenarios.

---

## Using dynamic parameters

You can use param in json body or path param using this syntax: `${paramName}` or `${paramName::default_value}`.

---

> Framework is not thread safe because of singleton pattern used in ApiHandler class. For parallel execution, consider using a different design pattern or refactor the code to avoid shared state.
