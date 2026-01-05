# ECommerceFullTestFramework

[![GitHub issues](https://img.shields.io/github/issues/manojjchahar/ECommerceFullTestFramework)](https://github.com/manojjchahar)


[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-red.svg)](https://maven.apache.org/)
[![TestNG](https://img.shields.io/badge/TestNG-7.8.0-orange.svg)](https://testng.org/)
[![Cucumber](https://img.shields.io/badge/Cucumber-7.15.0-green.svg)](https://cucumber.io/)
[![Rest Assured](https://img.shields.io/badge/RestAssured-5.3.2-purple.svg)](https://rest-assured.io/)
[![Selenium](https://img.shields.io/badge/Selenium-4.16.1-yellow.svg)](https://selenium.dev/)

***

## 🚀 Project Overview

**ECommerceFullTestFramework** is a modern automation suite targeting the [AutomationExercise](https://www.automationexercise.com) website. It provides robust UI and API automation for functional, regression, data-driven, and integration testing, using open-source tools and enterprise CI/CD standards.

The goal is to deliver portfolio-level, industry-standard automation that is easy to maintain, scale, and extend for real-world ecommerce platforms.

***

## ✨ Key Features

- UI automation with **Selenium WebDriver**
- API automation with **Rest Assured**
- BDD specification using **Cucumber**
- Data-driven testing with flexible JSON schema and test data files
- Comprehensive reporting: **ExtentReports**, **Cucumber**, and **TestNG**
- Azure DevOps pipeline-ready (**yaml**) with caching and retry controls
- Modular Page Object Model and API client structure
- Clean code adhering to SOLID and DRY principles
- Schema validation and negative-path testing
- Continuous integration support (build, test, artifact publication)
- Hooks, setup/teardown utilities, screenshot on failure
- Flaky-test resilience: TestNG RetryAnalyzer + Surefire reruns (configurable)
- Cloud/Grid ready: run locally, headless, or on Selenium Grid via `-DgridUrl`

***

## 🛠 Technology Stack

| Technology | Version | Role |
| :--: |:-------:| :-- |
| Java |   21    | Programming language |
| Maven |  3.8+   | Build, dependencies |
| Selenium | 4.16.1  | UI browser automation |
| Rest Assured |  5.3.2  | API automation |
| TestNG |  7.8.0  | Test execution |
| Cucumber | 7.15.0  | BDD scenarios |
| ExtentReports |  5.0.9  | HTML reporting |
| WebDriverManager |  5.6.2  | Browser drivers |
| Jackson |  2.15+  | JSON data handling |
| TOML4J |  0.7.2  | Config management |


***

## ✅ Prerequisites

- Java Development Kit (JDK) 21 installed and active on PATH
- Maven 3.8+ installed
- Set JAVA_HOME to your JDK 21 path
- Browsers: Chrome or Edge installed for UI tests

Verify setup:
```bash
java -version   # should report 21
mvn -v          # Maven should run on Java 21
```

## 🚀 Quick start

```bash
git clone https://github.com/manojjchahar/ECommerceFullTestFramework.git
cd ECommerceFullTestFramework
mvn clean test
```

Notes:
- The Maven Compiler Plugin is configured with `<release>21</release>`.
- Annotation processing is disabled (`<proc>none</proc>`) to avoid loading stray processors (e.g., Lombok) unintentionally. If you adopt Lombok later, add Lombok as a dependency and remove `<proc>none</proc>`.

## 🎯 Application Coverage

### UI Coverage

- Login \& Sign-up flows (`/login`, `/signup`)
- Home/dashboard/landing page (`/`)
- Product catalog (`/products`)
- Cart operations (`/view_cart`)
- Orders and order history (`/orders`)
- Profile \& account management (`/profile`)
- Product review creation (`/products`)
- Contact us form (`/contact_us`)
- Navigation, filtering, popups, error handling


### API Coverage

- POST login: `/api/verifyLogin`
- POST create user: `/api/createAccount`
- GET products: `/api/productsList`
- GET brands: `/api/brandsList`
- POST search product: `/api/searchProduct`
- GET user details by email: `/api/getUserDetailByEmail`
- DELETE account: `/api/deleteAccount`
- PUT update account: `/api/updateAccount`
- Response code, schema, boundary checks

***

## 📂 Project Structure

```
ECommerceFullTestFramework/
├── src/
│   ├── main/java/com/ecommercefull/
│   │   ├── base/
│   │   │   ├── BaseTest.java
│   │   │   ├── BaseAPI.java
│   │   │   └── TestContext.java
│   │   ├── pages/
│   │   │   ├── LoginPage.java
│   │   │   ├── SignupPage.java
│   │   │   ├── ProductPage.java
│   │   │   ├── CartPage.java
│   │   │   ├── OrdersPage.java
│   │   │   ├── ProfilePage.java
│   │   │   ├── ReviewPage.java
│   │   │   └── ContactUsPage.java
│   │   ├── api/
│   │   │   ├── AuthAPI.java
│   │   │   ├── AccountAPI.java
│   │   │   ├── ProductAPI.java
│   │   │   ├── CartAPI.java
│   │   │   ├── OrderAPI.java
│   │   │   └── APIEndpoints.java
│   │   ├── models/
│   │   │   ├── User.java
│   │   │   ├── Product.java
│   │   │   ├── Order.java
│   │   │   ├── Review.java
│   │   │   ├── Cart.java
│   │   │   └── AuthResponse.java
│   │   ├── utils/
│   │   │   ├── ConfigReader.java
│   │   │   ├── WebDriverManager.java
│   │   │   ├── ExtentReportManager.java
│   │   │   └── JSONUtils.java
│   ├── test/java/
│   │   ├── stepDefinitions/
│   │   │   ├── LoginSteps.java
│   │   │   ├── SignupSteps.java
│   │   │   ├── ProductSteps.java
│   │   │   ├── CartSteps.java
│   │   │   ├── OrderSteps.java
│   │   │   ├── APISteps.java
│   │   │   └── Hooks.java
│   │   ├── runners/
│   │   │   ├── TestRunner.java
│   │   │   ├── SmokeTestRunner.java
│   │   │   └── RegressionTestRunner.java
│   │   ├── apiTests/
│   │   │   ├── LoginAPITest.java
│   │   │   ├── ProductAPITest.java
│   │   │   ├── CartAPITest.java
│   │   │   └── AccountAPITest.java
├── resources/
│   ├── features/
│   │   ├── Login.feature
│   │   ├── Signup.feature
│   │   ├── Product.feature
│   │   ├── Cart.feature
│   │   ├── Order.feature
│   │   ├── APIValidation.feature
│   ├── testData/
│   │   ├── users.json
│   │   ├── products.json
│   │   ├── orders.json
│   │   ├── cart.json
│   ├── schemas/
│   │   ├── userSchema.json
│   │   ├── productSchema.json
│   │   ├── orderSchema.json
│   │   ├── cartSchema.json
│   ├── config/
│   │   └── config.toml
│   └── testng.xml
├── azure-pipelines.yml
├── pom.xml
├── README-ECommerce-Test-Framework.md
├── .gitignore
```


***

## ⚙️ Configuration Files

**config.toml example:**

```toml
[ui]
base_url = "https://www.automationexercise.com"
login_page = "/login"
signup_page = "/signup"
products_page = "/products"
cart_page = "/view_cart"
orders_page = "/orders"

[api]
base_url = "https://automationexercise.com"
login_endpoint = "/api/verifyLogin"
register_endpoint = "/api/createAccount"
products_list_endpoint = "/api/productsList"
search_product_endpoint = "/api/searchProduct"
brands_list_endpoint = "/api/brandsList"
delete_account_endpoint = "/api/deleteAccount"
update_account_endpoint = "/api/updateAccount"
get_user_detail_by_email_endpoint = "/api/getUserDetailByEmail"
```


***

## 📖 Feature Files \& BDD Scenarios

- **Login.feature:** UI and API login actions, negative path, session handling
- **Signup.feature:** New user registration, both UI and API
- **Product.feature:** Product browsing, search, filter, cart, reviews
- **Order.feature:** Cart to order workflow, order history validation
- **APIValidation.feature:** API-only flows, CRUD, schema checks

Each scenario uses Given-When-Then syntax and data tables for dynamic parameters.

***

## 🧪 Test Execution

### Common Maven Commands

```bash
# Run all tests (serial)
mvn clean test

# Run only smoke or regression scenarios
mvn test -Dcucumber.filter.tags="@smoke"
mvn test -Dcucumber.filter.tags="@regression"

# UI config flags
mvn test -Dbrowser=chrome -Dheadless=true -Denvironment=test

# Parallel run with TestNG DataProvider (Cucumber scenarios)
mvn -Dtest=ParallelTestRunner test -Ddataproviderthreadcount=4

# Apply flaky-test retries (both TestNG-level and Surefire reruns)
mvn test -Dretry.count=1 -Dsurefire.rerunFailingTestsCount=1

# Execute on Selenium Grid (e.g., local Docker Grid)
mvn -Dtest=ParallelTestRunner test -DgridUrl=http://localhost:4444/wd/hub -Dheadless=true -Ddataproviderthreadcount=4
```


### Parallelization

Use `maven-surefire-plugin` and TestNG for running tests in parallel (methods/classes).

***

## 📊 Reporting

- **ExtentReports:** HTML, chart/graph, logs, screenshots
- **Cucumber Reports:** JSON, HTML, XML
- **TestNG Reporting:** HTML, XML
- **Allure (optional):** Advanced drilldown

Error screenshots and logs captured for debugging.

***

## 🔁 CI/CD Pipeline (Azure DevOps YAML)

This project ships with an Azure Pipelines YAML configured for Java 21. It builds, tests, and publishes reports on every commit/merge.

Key variables:
- `JAVA_VERSION`: `'21'`
- `MAVEN_GOALS`: `'clean test'`

Example pipeline:
```yaml
trigger:
  branches:
    include:
      - main
      - master

pool:
  vmImage: 'windows-latest'

variables:
  MAVEN_OPTS: '-Xmx1024m'
  JAVA_VERSION: '21'
  MAVEN_GOALS: 'clean test'

steps:
  - task: UseJavaVersion@1
    inputs:
      versionSpec: '$(JAVA_VERSION)'
      architecture: 'x64'

  - task: Maven@3
    inputs:
      mavenPomFile: 'pom.xml'
      goals: '$(MAVEN_GOALS)'
      options: '-Dcucumber.filter.tags=$(cucumberTags) -Dbrowser=$(browser)'
      publishJUnitResults: true
      testResultsFiles: '**/surefire-reports/*.xml'
      javaHomeOption: 'JDKVersion'
      jdkVersionOption: '$(JAVA_VERSION)'
      mavenOptions: '-Xmx1024m'
      sonarQubeRunAnalysis: false
      sqMavenPluginVersionChoice: 'latest'

  - task: PublishBuildArtifacts@1
    displayName: 'Publish Cucumber and Extent reports'
    inputs:
      PathtoPublish: 'target'
      ArtifactName: 'reports'
      publishLocation: 'Container'
```

## 🧰 Build Notes

- Java 21 toolchain: Maven Compiler Plugin configured with `<release>21</release>`; project `maven.compiler.source/target` set to `21`.
- Test execution: Maven Surefire Plugin `3.2.5` for JDK 21 compatibility.
- Java version enforcement: Maven Enforcer Plugin requires Java `[21,22)` to prevent accidental builds on the wrong JDK.
- Annotation processing: disabled via `<proc>none</proc>` to avoid loading stray processors (e.g., Lombok). If you adopt Lombok later, add Lombok as a dependency and remove `<proc>none</proc>`.

## 🗓 Changelog

- 2025-11-01: Upgraded build to JDK 21. Updated `pom.xml` (compiler to 21, Surefire 3.2.5, Enforcer rule for 21) and `azure-pipelines.yml` (`JAVA_VERSION: '21'`). README updated to reflect these changes.

***

## 📦 Data Files

- Sample/test users, products, cart and order objects in JSON
- Utilities for generation and loading of test data

***

## 👩‍🔬 Sample End-to-End Scenario

**End-to-End Flow:**

1. Register user (UI \& API)
2. Login user (UI \& API)
3. Browse/search products, add item to cart
4. Submit product review, validate in UI and via API
5. Place order, check order history in UI and backend response (API)
6. Update or delete account
7. Log out and verify session termination

All steps validated by both Selenium UI checks and direct API calls.

***

## 📝 Contribution \& Best Practices

- Code reviews and adherence to SOLID, DRY design
- POM and API client abstraction
- Data-driven, modular, maintainable patterns
- Documentation and test case expansion
- CI validation before merge
- Issue reporting and enhancement proposals via GitHub

***

## 📄 License

MIT License - see LICENSE in repo.

***

## ♥ Why use ECommerceFullTestFramework?

- Real world website (‘AutomationExercise’) with dynamic data
- Full stack: UI + API with live endpoints
- Modern architecture and reporting
- CI/CD and parallel test support
- Portfolio-ready for job interviews and role advancement

***

**Made with pride for advanced Test Automation practice and career growth.**

