# 🏦 MifosX Banking Test Automation Framework

Modern, enterprise-grade test automation framework for MifosX Banking platform with CI/CD support, parallel execution, and comprehensive reporting.

## 🎯 About MifosX
MifosX is an open-source microfinance platform that provides core banking functionality for financial institutions. This framework provides comprehensive test automation for the platform's web interface and APIs.

## 📋 Features

- ✅ **Multi-browser Support**: Chrome, Firefox, Edge, Safari
- ✅ **Selenium Grid Integration**: Remote execution support
- ✅ **Parallel Test Execution**: TestNG-based parallelization
- ✅ **Page Object Model**: Clean, maintainable test structure
- ✅ **Allure Reporting**: Rich HTML reports with screenshots
- ✅ **ExtentReports**: Alternative reporting framework
- ✅ **API Testing**: REST Assured integration
- ✅ **CI/CD Ready**: Jenkins pipeline included
- ✅ **Docker Support**: Containerized Selenium Grid
- ✅ **Auto Screenshots**: Failure screenshot capture
- ✅ **Flexible Configuration**: Environment-based settings
- ✅ **Comprehensive Logging**: Log4j2 with multiple appenders

## 🛠️ Technology Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17+ | Programming Language |
| Maven | 3.8+ | Build Management |
| Selenium WebDriver | 4.16.1 | Browser Automation |
| TestNG | 7.8.0 | Test Framework |
| Allure | 2.24.0 | Test Reporting |
| REST Assured | 5.3.2 | API Testing |
| WebDriverManager | 5.6.2 | Driver Management |
| Log4j2 | 2.21.1 | Logging |
| Docker | Latest | Containerization |
| Jenkins | Latest | CI/CD |

## 🏗️ Project Structure

```
selenium-base-project/
├── src/
│   ├── main/java/
│   │   ├── core/              # Core framework components
│   │   ├── enums/             # Enum constants
│   │   ├── utils/             # Utility classes
│   │   └── data/              # Test data generators
│   ├── test/java/
│   │   ├── tests/             # Test classes
│   │   │   ├── ui/            # UI tests
│   │   │   └── api/           # API tests
│   │   ├── pages/             # Page Object classes
│   │   ├── listeners/         # TestNG listeners
│   │   └── runners/           # Test runners
│   └── resources/
│       ├── config.properties  # Configuration file
│       ├── log4j2.xml         # Logging configuration
│       └── testdata.json      # Test data
├── reports/                   # Test reports and screenshots
├── docker-compose.yml         # Selenium Grid setup
├── Jenkinsfile               # CI/CD pipeline
├── testng.xml                # TestNG configuration
└── pom.xml                   # Maven dependencies
```

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.8 or higher
- Docker (optional, for Selenium Grid)

### 1. Clone Repository

```bash
git clone <repository-url>
cd selenium-base-project
```

### 2. Install Dependencies

```bash
mvn clean compile
```

### 3. Configure for Your Application

Before running tests, update the following files for your application:

1. **Update locators in Page Objects:**
   - `src/test/java/pages/LoginPage.java`
   - `src/test/java/pages/HomePage.java`

2. **Update configuration:**
   - `src/main/resources/config.properties` (base.url)
   - `src/main/resources/testdata.json` (credentials, URLs)

3. **Enable and update tests:**
   - `src/test/java/tests/ui/LoginTest.java` (enabled=false → enabled=true)

### 4. Run Tests (Local)

```bash
# Run framework verification test
mvn test -Dtest=LoginTest#testFrameworkWorks

# Run all tests (after updating for your app)
mvn clean test

# Run specific test suite
mvn clean test -Dgroups=smoke

# Run with specific browser
mvn clean test -Dbrowser=chrome

# Run in headless mode
mvn clean test -Dheadless=true
```

### 5. Run with Selenium Grid

```bash
# Start Selenium Grid
docker-compose up -d

# Run tests on Grid
mvn clean test -Dremote.execution=true -Dbrowser=chrome

# Stop Selenium Grid
docker-compose down
```

## ⚙️ Configuration

### Browser Configuration

Configure in `src/main/resources/config.properties`:

```properties
# Browser settings
browser=chrome
headless=false
remote.execution=false
hub.url=http://localhost:4444/wd/hub

# Timeouts
implicit.wait=10
explicit.wait=20
page.load.timeout=30

# Environment
environment=test
base.url=https://opensource-demo.orangehrmlive.com
```

### TestNG Configuration

Configure test suites in `testng.xml`:

```xml
<test name="Smoke Tests">
    <parameter name="browser" value="chrome"/>
    <groups>
        <run>
            <include name="smoke"/>
        </run>
    </groups>
    <classes>
        <class name="tests.ui.LoginTest"/>
    </classes>
</test>
```

## 🧪 Writing Tests

### Page Object Example

```java
@Component
public class LoginPage {
    @FindBy(name = "username")
    private WebElement usernameField;
    
    @Step("Login with credentials")
    public HomePage login(String username, String password) {
        usernameField.sendKeys(username);
        // ... rest of implementation
        return new HomePage();
    }
}
```

### Test Class Example

```java
@Epic("Authentication")
@Feature("Login")
public class LoginTest extends BaseTest {
    
    @Test(groups = {"smoke", "regression"})
    @Story("Successful Login")
    public void testSuccessfulLogin() {
        LoginPage loginPage = new LoginPage();
        HomePage homePage = loginPage.login("Admin", "admin123");
        Assert.assertTrue(homePage.isDisplayed());
    }
}
```

## 📊 Reporting

### Allure Reports

```bash
# Generate Allure report
mvn allure:report

# Serve Allure report
mvn allure:serve
```

### View Reports

- **Allure Report**: `reports/allure-report/index.html`
- **Screenshots**: `reports/screenshots/`
- **Logs**: `reports/application.log`

## 🐳 Docker Support

### Selenium Grid

```bash
# Start full Grid with Chrome, Firefox, and Edge nodes
docker-compose up -d

# Scale Chrome nodes
docker-compose up -d --scale chrome-node=3

# View Grid console
open http://localhost:4444

# View Allure reports
open http://localhost:5050
```

### Custom Test Runner

```bash
# Build and run tests in Docker
docker-compose --profile ci up test-runner
```

## 🔄 CI/CD Pipeline

### Jenkins Pipeline Features

- ✅ Multi-browser support
- ✅ Environment selection
- ✅ Parallel execution
- ✅ Automatic reporting
- ✅ Email notifications
- ✅ Artifact archival

### Pipeline Parameters

| Parameter | Options | Description |
|-----------|---------|-------------|
| BROWSER | chrome, firefox, edge | Target browser |
| ENVIRONMENT | test, staging, dev | Test environment |
| TEST_SUITE | smoke, regression, api, all | Test suite |
| PARALLEL_EXECUTION | true, false | Enable parallel execution |
| REMOTE_EXECUTION | true, false | Use Selenium Grid |

### Trigger Pipeline

```bash
# Via Jenkins UI
http://jenkins-url/job/selenium-tests/build

# Via curl
curl -X POST http://jenkins-url/job/selenium-tests/buildWithParameters \
  --data "BROWSER=chrome&TEST_SUITE=smoke&PARALLEL_EXECUTION=true"
```

## 🎯 Test Execution Examples

### Local Execution

```bash
# Smoke tests on Chrome
mvn test -Dgroups=smoke -Dbrowser=chrome

# Regression tests in parallel
mvn test -Dgroups=regression -Dparallel=methods -DthreadCount=4

# API tests only
mvn test -Dgroups=api

# Headless execution
mvn test -Dheadless=true
```

### Remote Execution

```bash
# Start Grid
docker-compose up -d selenium-hub chrome-node firefox-node

# Run on Grid
mvn test -Dremote.execution=true -Dbrowser=chrome -Dgroups=smoke

# Cross-browser testing
mvn test -Dremote.execution=true -Dbrowser=firefox -Dgroups=regression
```

## 📈 Best Practices

### Test Organization

1. **Group Tests**: Use TestNG groups (smoke, regression, api)
2. **Page Objects**: Separate page logic from test logic
3. **Data Driven**: Use external data sources
4. **Assertions**: Use meaningful assertion messages
5. **Logging**: Add appropriate log statements

### Performance

1. **Parallel Execution**: Enable for faster test runs
2. **Grid Usage**: Use for scalable execution
3. **Driver Management**: Automatic driver downloads
4. **Resource Cleanup**: Proper WebDriver cleanup

### Maintenance

1. **Modular Design**: Reusable components
2. **Configuration**: Environment-based settings
3. **Version Control**: Track framework changes
4. **Documentation**: Keep README updated

## 🔧 Troubleshooting

### Common Issues

| Issue | Solution |
|-------|----------|
| WebDriver not found | WebDriverManager handles automatically |
| Grid connection failed | Check docker-compose services |
| Test failures | Check screenshots and logs |
| Memory issues | Adjust JVM settings in Maven |
| Port conflicts | Modify ports in docker-compose.yml |

### Debug Commands

```bash
# Check Grid status
curl http://localhost:4444/status

# View container logs
docker logs selenium-hub
docker logs selenium-node-chrome

# Check running containers
docker ps

# Maven debug mode
mvn test -X
```

## 📞 Support

For questions or issues:

1. Check existing documentation
2. Review logs and screenshots
3. Check Jenkins pipeline output
4. Contact the automation team

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

**Happy Testing! 🎉**