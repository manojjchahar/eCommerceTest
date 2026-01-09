# ECommerceFullTestFramework

[![Azure DevOps](https://img.shields.io/badge/Azure%20DevOps-Pipeline-0078D7?logo=azure-devops)](https://dev.azure.com/manojchahar/E%20Commerce%20Test%20Automation)
[![GitLab CI](https://img.shields.io/badge/GitLab-CI%2FCD-FC6D26?logo=gitlab)](https://gitlab.com/mxk2/eCommerceTest)
[![GitHub](https://img.shields.io/badge/GitHub-Repository-181717?logo=github)](https://github.com/manojjchahar/eCommerceTest)

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-red.svg)](https://maven.apache.org/)
[![TestNG](https://img.shields.io/badge/TestNG-7.8.0-orange.svg)](https://testng.org/)
[![Cucumber](https://img.shields.io/badge/Cucumber-7.15.0-green.svg)](https://cucumber.io/)
[![Rest Assured](https://img.shields.io/badge/RestAssured-5.3.2-purple.svg)](https://rest-assured.io/)
[![Selenium](https://img.shields.io/badge/Selenium-4.27.0-yellow.svg)](https://selenium.dev/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker)](https://docs.docker.com/compose/)
[![Kubernetes](https://img.shields.io/badge/Kubernetes-Helm-326CE5?logo=kubernetes)](https://kubernetes.io/)

***

## 🚀 Project Overview

**ECommerceFullTestFramework** is a modern automation suite targeting the [AutomationExercise](https://www.automationexercise.com) website. It provides robust UI and API automation for functional, regression, data-driven, and resiliency testing, using open-source tools and enterprise CI/CD standards.

The goal is to deliver portfolio-level, industry-standard automation that is easy to maintain, scale, and extend for real-world ecommerce platforms.

***

## ✨ Key Features

- UI automation with **Selenium WebDriver** & Page Objects
- **Screenplay Pattern** implementation (Actor, Task, Ability)
- API automation with **Rest Assured** (client abstraction + endpoint constants)
- BDD using **Cucumber** (Gherkin feature specs, tags strategy)
- Data-driven testing via centralized JSON test data + dynamic record selection
- Built-in **retry strategies** (fixed delay + exponential backoff) for transient API failures & rate-limit simulation
- Resiliency & security test pack (status variance, rate limit 429, token absence/invalid scenarios)
- JSON schema validation (multiple schemas: product, brand, user, cart, order)
- Comprehensive reporting: **ExtentReports**, **Allure**, **Cucumber**, **TestNG**
- Screenshot & response embedding on failure (UI + API)
- Flaky-test mitigation: **TestNG RetryAnalyzer** + optional Surefire reruns
- Config-driven execution (TOML config + runtime system properties)
- Parallel-ready via TestNG DataProvider & dedicated parallel runner
- **Multi-platform CI/CD**: Azure DevOps + GitLab CI with on-demand parameters
- **Selenium Grid 4.27**: Docker Compose with video recording & VNC
- **Kubernetes ready**: Helm chart for enterprise scaling
- **Multi-repository sync**: Push to Azure DevOps, GitLab, GitHub simultaneously
- Clean code aligned with SOLID/DRY & layered architecture (pages, api, models, utils)

***

## 🛠 Technology Stack

| Technology | Version | Role |
| :-- | :--: | :-- |
| Java | 21 | Language |
| Maven | 3.8+ | Build & dependency management |
| Selenium | 4.16.1 | UI automation |
| Rest Assured | 5.3.2 | API test client |
| TestNG | 7.8.0 | Test execution & retries |
| Cucumber | 7.15.0 | BDD layer |
| ExtentReports | 5.0.9 | HTML rich report |
| Allure | 2.24.0 | Advanced reporting & traceability |
| WebDriverManager | 5.6.2 | Driver binaries |
| Jackson | 2.15.x | JSON serialization/deserialization |
| TOML4J | 0.7.2 | Config parsing |
| AssertJ | 3.26.3 | Fluent assertions (API/model) |
| Awaitility | 4.2.0 | Asynchronous waits (if needed) |
| Logback + SLF4J | 1.2.13 / 1.7.36 | Logging infrastructure |

***

## ✅ Prerequisites

- JDK 21 installed & JAVA_HOME set
- Maven 3.8+ installed
- Chrome (primary) or Edge browser installed
- Docker (optional, for Selenium Grid)

Verify setup:
```bash
java -version
mvn -v
docker --version  # optional
```

***

## 🔄 CI/CD Pipelines

This framework is configured for **multi-platform CI/CD** with identical functionality across:

| Platform | Config File | Status |
|----------|-------------|--------|
| **Azure DevOps** | `azure-pipelines.yml` | ✅ Two-stage pipeline |
| **GitLab CI** | `.gitlab-ci.yml` | ✅ Two-stage pipeline |
| **GitHub** | (manual runs) | ✅ Repository synced |

### Pipeline Features

| Feature | Description |
|---------|-------------|
| **On-Demand Parameters** | Select tags, browser, threads, retry count from UI |
| **Scheduled Triggers** | Nightly (Mon-Fri 2AM) + Weekly (Sat 3AM) |
| **Execution Modes** | local / docker / grid |
| **Artifact Publishing** | JUnit results, Cucumber, Extent, Allure reports |
| **Two Stages** | TestExecution → Reports |

### Running Pipelines

**Azure DevOps:**
```
https://dev.azure.com/manojchahar/E%20Commerce%20Test%20Automation/_build
```

**GitLab CI:**
```
https://gitlab.com/mxk2/eCommerceTest/-/pipelines
```

### Execution Modes

| Mode | gridUrl | What It Does |
|------|---------|--------------|
| `local` | (empty) | WebDriverManager - runs browser on agent |
| `docker` | localhost:4444 | Docker Compose Selenium Grid |
| `grid` | custom URL | External Kubernetes/Cloud Grid |

***

## 🐳 Selenium Grid (Docker)

Modern Selenium Grid 4.27 with video recording, VNC access, and auto-scaling.

### Quick Start
```bash
# Start Grid (scales Chrome to 3 instances)
docker-compose up -d --scale chrome=3

# View Grid UI
open http://localhost:4444/ui

# VNC to watch tests live (password: secret)
open vnc://localhost:7900

# Run tests against Grid
mvn verify -DgridUrl=http://localhost:4444

# View video recordings
ls ./recordings/

# Stop Grid
docker-compose down
```

### Docker Compose Services

| Service | Ports | Purpose |
|---------|-------|---------|
| selenium-hub | 4444 | Grid endpoint |
| chrome | 7900 (VNC), 5900 | Chrome browser node |
| firefox | 7901, 5901 | Firefox browser node |
| edge | 7902, 5902 | Edge browser node |
| *-video | - | Records test execution to `./recordings/` |

***

## ☸️ Kubernetes Deployment

For enterprise-scale parallel execution, deploy Selenium Grid on Kubernetes using Helm.

### Files
- `infrastructure/k8s/selenium-grid-values.yaml` - Helm values
- `infrastructure/k8s/README.md` - Deployment guide

### Quick Deploy
```bash
# Add Selenium Helm repo
helm repo add selenium https://www.selenium.dev/docker-selenium
helm repo update

# Deploy Grid
helm install selenium-grid selenium/selenium-grid \
  -f infrastructure/k8s/selenium-grid-values.yaml \
  --namespace selenium --create-namespace

# Port forward to access
kubectl port-forward svc/selenium-grid-hub 4444:4444 -n selenium

# Run tests
mvn verify -DgridUrl=http://localhost:4444
```

***

## 📦 Multi-Repository Setup

The framework is synchronized across three Git platforms:

```bash
# View all remotes
git remote -v

# Push to ALL remotes at once
git push all <branch>

# Push to individual remotes
git push origin <branch>  # Azure DevOps
git push gitlab <branch>  # GitLab
git push github <branch>  # GitHub
```

***
## 🚀 Quick Start
```bash
git clone https://github.com/manojjchahar/ECommerceFullTestFramework.git
cd ECommerceFullTestFramework
mvn clean test
```
Notes:
- Compiler uses `<release>21`.
- Annotation processing disabled (`<proc>none>`). Add Lombok later if needed.

***
## 🎯 Application Coverage

### UI Feature Coverage (current implemented feature files)
- UserAuthentication.feature (signup, login, account actions, duplicate registration)
- ProductCatalog.feature (search, filter by category & brand, add to cart)
- Cart.feature (add/remove items, checkout draft flow)

### API Feature Coverage (current implemented feature files)
- AuthAccountAPI.feature (register, login, update, fetch, delete lifecycle)
- BrandsAPI.feature (brand list & schema validation)
- ProductsAPI.feature (list, search, empty search cases, schema validation)
- DataDrivenAPI.feature (multi-user login from JSON test data)
- SecurityResiliencyAPI.feature (token resilience, error mapping, rate limit & retry behaviors)

### Endpoint Set (resolved via APIEndpoints + ConfigReader)
- POST /api/verifyLogin
- POST /api/createAccount
- PUT /api/updateAccount
- GET /api/getUserDetailByEmail
- DELETE /api/deleteAccount
- GET /api/productsList
- POST /api/searchProduct
- GET /api/brandsList
- External resiliency target: httpbin.org /status/200 /status/429 (simulated throttling)

***
## 📂 Project Structure (current)
```
ECommerceFullTestFramework/
├── pom.xml                                    # Maven build configuration
├── azure-pipelines.yml                        # Azure DevOps CI/CD pipeline
├── .gitlab-ci.yml                             # GitLab CI/CD pipeline
├── docker-compose.yml                         # Selenium Grid 4.27 with video/VNC
├── config.toml                                # Root config file
├── README-ECommerce-Test-Framework.md         # Main documentation
├── README-DataExternalization-ScenarioContext.md  # Data externalization guide
├── infrastructure/
│   └── k8s/
│       ├── selenium-grid-values.yaml          # Kubernetes Helm values
│       └── README.md                          # K8s deployment guide
├── src/
│   ├── main/
│   │   ├── java/com/eCommerceTest/
│   │   │   ├── api/                           # API client classes
│   │   │   │   ├── APIEndpoints.java
│   │   │   │   ├── AccountAPI.java
│   │   │   │   ├── AuthAPI.java
│   │   │   │   ├── CartAPI.java
│   │   │   │   └── ProductAPI.java
│   │   │   ├── base/                          # Base classes & context
│   │   │   │   ├── BaseAPI.java
│   │   │   │   ├── BaseTest.java
│   │   │   │   ├── IApiClient.java
│   │   │   │   ├── ScenarioContext.java
│   │   │   │   └── TestContext.java
│   │   │   ├── models/                        # POJOs for data
│   │   │   │   ├── AuthResponse.java
│   │   │   │   ├── Cart.java
│   │   │   │   ├── PaymentDetails.java
│   │   │   │   ├── Product.java
│   │   │   │   ├── User.java
│   │   │   │   └── UserRegistrationData.java
│   │   │   ├── pages/                         # Page Objects
│   │   │   │   ├── CartPage.java
│   │   │   │   ├── CheckoutPage.java
│   │   │   │   ├── LoginPage.java
│   │   │   │   ├── PaymentPage.java
│   │   │   │   ├── ProductPage.java
│   │   │   │   └── SignupPage.java
│   │   │   ├── screenplay/                    # Screenplay Pattern (NEW)
│   │   │   │   ├── Ability.java               # Base ability interface
│   │   │   │   ├── Actor.java                 # Actor implementation
│   │   │   │   ├── Task.java                  # Task interface
│   │   │   │   ├── abilities/
│   │   │   │   │   └── BrowseTheWeb.java      # Web browsing ability
│   │   │   │   └── tasks/
│   │   │   │       └── Login.java             # Login task
│   │   │   └── utils/                         # Utility classes
│   │   │       ├── BrowserManager.java        # WebDriver lifecycle
│   │   │       ├── ConfigReader.java          # TOML config
│   │   │       ├── Constants.java             # Constants
│   │   │       ├── DataRepository.java        # JSON data loader
│   │   │       ├── ExtentReportManager.java   # Extent Reports
│   │   │       ├── Logging.java               # SLF4J logging
│   │   │       ├── RetryFilter.java           # API retry filter
│   │   │       ├── RetryPolicy.java           # Retry strategies
│   │   │       ├── SchemaValidator.java       # JSON schema validation
│   │   │       ├── SchemaValidationReport.java
│   │   │       ├── TestDataManager.java       # Test data access
│   │   │       └── UiNavigator.java           # Navigation helper
│   │   └── resources/
│   │       └── config/config.toml
│   ├── test/
│   │   ├── java/com/eCommerceTest/
│   │   │   ├── apiTests/                      # API test classes
│   │   │   │   ├── AccountAPITest.java
│   │   │   │   ├── CartAPITest.java
│   │   │   │   ├── LoginAPITest.java
│   │   │   │   └── ProductAPITest.java
│   │   │   ├── listeners/                     # TestNG listeners
│   │   │   │   ├── RetryAnalyzer.java
│   │   │   │   └── RetryAnnotationTransformer.java
│   │   │   ├── logging/
│   │   │   │   └── LoggingSmokeTest.java
│   │   │   ├── runners/                       # Cucumber runners
│   │   │   │   ├── ParallelTestRunner.java
│   │   │   │   └── SequentialTestRunner.java
│   │   │   └── stepDefinitions/               # Cucumber steps
│   │   │       ├── APISteps.java
│   │   │       ├── CartSteps.java
│   │   │       ├── Hooks.java
│   │   │       ├── LoginSteps.java
│   │   │       ├── ProductSteps.java
│   │   │       └── SignupSteps.java
│   │   └── resources/
│   │       ├── extent.properties              # Extent Reports config
│   │       ├── logback-test.xml               # Logging config
│   │       ├── testNG.xml                     # TestNG suite
│   │       ├── features/
│   │       │   ├── API/
│   │       │   │   ├── AuthAccountAPI.feature
│   │       │   │   ├── BrandsAPI.feature
│   │       │   │   ├── DataDrivenAPI.feature
│   │       │   │   ├── ProductsAPI.feature
│   │       │   │   └── SecurityResiliencyAPI.feature
│   │       │   └── UI/
│   │       │       ├── Cart.feature
│   │       │       ├── ProductCatalog.feature
│   │       │       └── UserAuthentication.feature
│   │       ├── schemas/                       # JSON schemas
│   │       │   ├── brandSchema.json
│   │       │   ├── cartSchema.json
│   │       │   ├── orderSchema.json
│   │       │   ├── productSchema.json
│   │       │   └── userSchema.json
│   │       └── testData/                      # Test data files
│   │           ├── cart.json
│   │           ├── orders.json
│   │           ├── payments.json
│   │           ├── products.json
│   │           ├── registration-data.json
│   │           └── users.json
├── recordings/                                # Video recordings (Docker Grid)
├── allure-results/                            # Allure raw results
├── target/                                    # Build output
└── test-output/                               # TestNG output
```

***
## ⚙️ Configuration (config.toml)
Minimal example:
```toml
[ui]
base_url = "https://www.automationexercise.com"

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
Runtime overrides: pass `-Dapi.baseUrl=... -Dbrowser=chrome -Dheadless=true` etc.

***
## 🧪 Execution & Runners

Common Maven commands:
```bash
# Full test run (parallel by default)
mvn clean test -Dtest=ParallelTestRunner -Dcucumber.filter.tags="@ui or @api"

# Sequential execution (single thread)
mvn test -Dtest=SequentialTestRunner -Dcucumber.filter.tags="@ui"

# Parallel with custom thread count
mvn test -Dtest=ParallelTestRunner -Ddataproviderthreadcount=4 -Dcucumber.filter.tags="@ui"

# API only (parallel)
mvn test -Dtest=ParallelTestRunner -Dcucumber.filter.tags="@api"

# Smoke / Regression via tags
mvn test -Dtest=ParallelTestRunner -Dcucumber.filter.tags="@smoke"
mvn test -Dtest=ParallelTestRunner -Dcucumber.filter.tags="@regression"

# Dry-run (step definitions completeness)
mvn test -Dtest=ParallelTestRunner -Dcucumber.execution.dry-run=true -Dcucumber.filter.tags="@api"
```
Tag strategy examples:
- `@API`, `@UI`, `@negative`, `@security`, `@ratelimit`, `@account`, `@ProductsAPI` etc.
Combine with logical expressions: `-Dcucumber.filter.tags="@API and not @negative"`.

***
## 🗓 Recent Changes
- **2026-01**: Added multi-platform CI/CD pipelines (Azure DevOps + GitLab CI)
- **2026-01**: Upgraded Selenium Grid to 4.27 with video recording & VNC access
- **2026-01**: Added Kubernetes Helm deployment support (`infrastructure/k8s/`)
- **2026-01**: Added execution mode parameter (local/docker/grid)
- **2026-01**: Multi-repository setup (Azure DevOps, GitLab, GitHub)
- **2026-01**: On-demand pipeline parameters & scheduled triggers
- 2025-11: Added resiliency/rate-limit feature set & retry strategies
- 2025-11: Upgraded to Java 21 toolchain & aligned plugins
- 2025-11: Integrated Allure adapters (results present under allure-results/)
- 2025-11: Refactoring: consolidated utilities (BrowserManager, DataRepository, RetryPolicy, SchemaValidationReport)
- 2025-11: Removed legacy util classes: DriverFactory, WaitFactory, JSONUtils, TestDataLoader, FixedDelayRetryStrategy, ExponentialBackoffRetryStrategy

## ♻ Refactoring & Consolidated Utilities (Updated)
Unified components now:
- BrowserManager: Lifecycle + waits (replaces DriverFactory & WaitFactory).
- DataRepository: Generic cached JSON/resource loader (replaces JSONUtils & TestDataLoader).
- RetryPolicy + RetryFilter: Single entry for FIXED / EXPONENTIAL strategies via builder (legacy strategy classes removed).
- SchemaValidator + SchemaValidationReport: Rich schema validation + violations.
- TestDataManager: High-level domain data access built atop DataRepository.

Removed (fully deleted or stubbed): DriverFactory, WaitFactory, JSONUtils, TestDataLoader, FixedDelayRetryStrategy, ExponentialBackoffRetryStrategy.

Migration notes:
- Replace any direct JSONUtils.read* calls with DataRepository.load(path, TypeReference).
- Replace FixedDelayRetryStrategy/ExponentialBackoffRetryStrategy with RetryPolicy.builder().fixed()/exponential().
- Use BrowserManager.init()/getDriver()/quit() and BrowserManager.waitShort()/waitLong().

Example updated usage:
```java
RetryPolicy policy = RetryPolicy.builder()
        .exponential()
        .attempts(4)
        .baseDelay(250)
        .maxDelay(3000)
        .codes(java.util.Set.of(429,500,503))
        .build();
new RetryFilter(policy);

var users = DataRepository.load("/testData/users.json", new com.fasterxml.jackson.core.type.TypeReference<java.util.List<com.eCommerceTest.models.User>>(){});
var report = SchemaValidator.validate(response, "/schemas/productSchema.json");
assert report.isSuccess() : report.getViolations();
```

## 🔄 Resiliency & Retry Design
- Single `RetryPolicy` enum with builder supports FIXED and EXPONENTIAL (with optional jitter, status code set).
- `RetryFilter` applies policy; removed separate strategy classes.
- Rate limit simulation via httpbin `/status/429` validates backoff & attempt count.

## 🗃 Test Data Management
- DataRepository provides unified cached JSON loading.
- TestDataManager offers domain-specific accessors (users, products, payments, registration profiles).
- `selectRecord(file, key)` helper replaces former TestDataLoader behavior.

***
## 📑 Schema Validation
Schemas stored in `src/test/resources/schemas/` and validated with Rest Assured + `SchemaValidator` utility for: product, brand, user, cart, order.

***
## 📊 Reporting

The framework integrates **four comprehensive reporting tools** that automatically generate detailed test execution reports. Each serves a specific purpose and provides unique insights into test results.

### 1️⃣ Cucumber HTML Report
**Purpose**: Standard BDD-style report showing feature files, scenarios, and steps execution.

**Configuration**: Configured in `SequentialTestRunner.java` and `ParallelTestRunner.java` via the `plugin` option:
```java
"html:target/cucumber-reports/sequential.html",
"json:target/cucumber-reports/sequential.json"
```

**Output Locations**:
- HTML Report: `target/cucumber-reports/sequential.html` (or `parallel.html`)
- JSON Data: `target/cucumber-reports/sequential.json` (or `parallel.json`)

**How to View**: 
```bash
mvn clean test
# Open: target/cucumber-reports/sequential.html in a browser
```

---

### 2️⃣ Extent Reports
**Purpose**: Rich, interactive HTML report with charts, logs, system info, and embedded screenshots.

**Configuration**: Configured via `extent.properties`:
```properties
extent.reporter.spark.start=true
extent.reporter.spark.out=target/extent/ExtentReport.html
screenshot.dir=target/extent/screenshots
```

**Output Locations**:
- HTML Report: `target/extent/ExtentReport.html`
- Screenshots: `target/extent/screenshots/`

**How to View**:
```bash
mvn clean test
# Open: target/extent/ExtentReport.html in a browser
```

**Features**:
- Dashboard with pass/fail/skip statistics
- Execution timeline and duration metrics
- System information (OS, Java version, user)
- Embedded screenshots on test failures
- Beautiful charts and graphs

---

### 3️⃣ Allure Report
**Purpose**: Enterprise-grade reporting with test history, trends, categories, and comprehensive test analytics.

**Configuration**: Configured in `pom.xml` via:
- Allure Cucumber 7 adapter dependency
- Allure Maven plugin
- AspectJ weaver for report generation

**Output Locations**:
- Raw Results: `target/allure-results/` (JSON and XML files)
- Generated HTML: `target/site/allure-maven-plugin/` (after running report command)

**How to Generate and View**:
```bash
# Option 1: Generate and serve report (opens in browser automatically)
mvn clean test
mvn allure:serve

# Option 2: Generate static HTML report
mvn clean test
mvn allure:report
# Open: target/site/allure-maven-plugin/index.html in a browser
```

**Features**:
- Test execution trends and history
- Categorization by features and suites
- Detailed step-by-step execution logs
- Environment and configuration details
- REST API request/response logging (via allure-rest-assured)
- Screenshots and attachments
- Flaky test detection
- Timeline view

---

### 4️⃣ Maven Surefire Report
**Purpose**: Standard Maven test execution report integrated with Maven site lifecycle.

**Configuration**: Configured in `pom.xml` via `maven-surefire-report-plugin`:
```xml
<plugin>
    <artifactId>maven-surefire-report-plugin</artifactId>
    <version>3.2.5</version>
</plugin>
```

**Output Locations**:
- Test Results XML: `target/surefire-reports/*.xml`
- HTML Report: `target/site/surefire-report.html` (after running report command)

**How to Generate and View**:
```bash
# Option 1: Generate during verify phase
mvn clean verify
# Open: target/site/surefire-report.html

# Option 2: Generate report explicitly
mvn clean test
mvn surefire-report:report
# Open: target/site/surefire-report.html
```

**Features**:
- TestNG test results summary
- Pass/fail/skip statistics
- Execution time per test
- Error messages and stack traces
- Suite-level aggregation

---

### 📁 Quick Reference: Output Folders

| Report Type | Output Location | View Command |
|-------------|----------------|--------------|
| **Cucumber HTML** | `target/cucumber-reports/sequential.html` | Open file directly in browser |
| **Extent Reports** | `target/extent/ExtentReport.html` | Open file directly in browser |
| **Allure Results** | `target/allure-results/` (raw data) | `mvn allure:serve` or `mvn allure:report` |
| **Allure HTML** | `target/site/allure-maven-plugin/` | Open `index.html` after `mvn allure:report` |
| **Surefire Report** | `target/site/surefire-report.html` | Open after `mvn verify` or `mvn surefire-report:report` |
| **Screenshots** | `target/extent/screenshots/` | Embedded in Extent and Allure reports |

### 🎯 Recommended Workflow

**For Local Development**:
```bash
mvn clean test
# View Extent Report immediately (fastest to open)
# Open: target/extent/ExtentReport.html
```

**For Detailed Analysis**:
```bash
mvn clean test allure:serve
# Allure opens automatically with comprehensive analytics
```

**For CI/CD Pipelines**:
```bash
mvn clean verify allure:report
# All reports generated, Allure HTML available in target/site/
# Archive target/extent/, target/allure-results/, target/site/ as artifacts
```

***
## 🧰 Build & Quality Notes
- Java enforced via Maven Enforcer `[21,22)`.
- Surefire 3.2.5 for JDK 21 compatibility & rerun support.
- Logging: SLF4J API + Logback test configuration (`logback-test.xml`).
- Assertions: TestNG + AssertJ (fluent for complex model validation).

***
## 📝 Contribution Standards
- Follow existing layering (api/base/models/pages/utils/stepDefinitions).
- Keep page object actions atomic & log start/end for traceability.
- Prefer AssertJ for expressive model assertions; TestNG for simple boolean/status.
- Add schema updates alongside endpoint changes.
- Tag new scenarios appropriately to avoid unintended inclusion in pipeline runs.

***
## ♥ Why this Framework
- Demonstrates end-to-end UI + API synergy.
- Includes advanced patterns (retry, resiliency, data-driven, schema validation).
- Ready for CI/CD & parallel scaling.

***
**Made for advanced Test Automation practice & career growth.**
