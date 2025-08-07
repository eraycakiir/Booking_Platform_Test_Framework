# 🏨 Booking.com Platform Test Automation Framework

Modern, platform-independent test automation framework for Booking.com with support for both Web (Chrome) and iOS (real device) testing using a unified codebase.

## 🎯 About This Framework

This framework provides comprehensive test automation for Booking.com platform across multiple platforms using the same test logic. Built with interface-based design pattern to maximize code reusability between Web and Mobile testing.

## 📋 Key Features

- ✅ **Platform-Independent Architecture**: Same test code runs on Web and iOS
- ✅ **Interface-Based Design**: 85-90% code sharing between platforms
- ✅ **Web Automation**: Chrome browser with Selenium WebDriver
- ✅ **iOS Automation**: Real device testing with Appium
- ✅ **Page Object Model**: Clean, maintainable test structure
- ✅ **PageFactory Pattern**: Platform abstraction layer
- ✅ **Abstract Base Classes**: Shared functionality across platforms
- ✅ **Allure Reporting**: Rich HTML reports with screenshots
- ✅ **Wait Strategies**: Intelligent element waiting
- ✅ **Error Handling**: Comprehensive exception handling
- ✅ **Configuration Management**: Environment-based settings
- ✅ **Logging**: Log4j2 with detailed test execution logs

## 🛠️ Technology Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17+ | Programming Language |
| Maven | 3.8+ | Build Management |
| Selenium WebDriver | 4.20.0 | Web Browser Automation |
| Appium | 9.3.0 | iOS Mobile Automation |
| TestNG | 7.8.0 | Test Framework |
| Allure | 2.24.0 | Test Reporting |
| Log4j2 | 2.21.1 | Logging |

## 🏗️ Project Architecture

```
Booking_Platform_Test_Framework/
├── src/main/java/
│   ├── interfaces/              # Platform-independent interfaces
│   │   ├── IHomePage.java       # Home page interface
│   │   └── ILoginPage.java      # Login page interface
│   ├── factory/
│   │   └── PageFactory.java     # Platform-specific page creator
│   ├── pages/
│   │   ├── BasePage.java        # Abstract base for all pages
│   │   ├── web/                 # Web platform implementations
│   │   │   ├── WebBasePage.java
│   │   │   ├── WebHomePage.java
│   │   │   └── WebLoginPage.java
│   │   └── ios/                 # iOS platform implementations
│   │       ├── IOSBasePage.java
│   │       ├── IOSHomePage.java
│   │       └── IOSLoginPage.java
│   ├── core/                    # Core framework components
│   │   ├── DriverFactory.java   # Platform-aware driver management
│   │   ├── ConfigReader.java    # Configuration management
│   │   └── WaitHelper.java      # Wait strategies
│   └── enums/                   # Enum constants
│       ├── PlatformType.java    # Platform enumeration
│       └── BrowserType.java     # Browser enumeration
├── src/test/java/
│   ├── tests/ui/
│   │   ├── BaseTest.java        # Platform-independent base test
│   │   └── LoginFlowTest.java   # Login flow tests (works on both platforms)
│   └── listeners/               # TestNG listeners
├── src/main/resources/
│   ├── config.properties        # Configuration file
│   └── log4j2.xml              # Logging configuration
└── reports/                     # Test reports and screenshots
```

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.8 or higher
- Chrome browser (for web testing)
- iPhone with iOS (for mobile testing)
- Appium server (for mobile testing)
- Xcode (for iOS automation setup)

### 1. Clone Repository

```bash
git clone git@github.com:eraycakiir/Booking_Platform_Test_Framework.git
cd Booking_Platform_Test_Framework
```

### 2. Install Dependencies

```bash
mvn clean compile
```

### 3. Configuration

#### For Web Testing:
Set `platform=web` in `src/main/resources/config.properties`

#### For iOS Testing:
Set `platform=ios` and configure iOS-specific settings:
```properties
platform=ios
ios.app.bundle.id=com.booking.BookingApp
ios.device.name=iPhone 16 Pro
ios.platform.version=18.5
ios.udid=YOUR_DEVICE_UDID
ios.team.id=YOUR_TEAM_ID
appium.server.url=http://localhost:4723
```

### 4. Run Tests

#### Web Platform (Chrome):
```bash
# Set platform to web in config.properties
mvn clean test -Dtest=LoginFlowTest#testLoginFlowToEmailEntry
```

#### iOS Platform (Real Device):
```bash
# Start Appium server first
appium

# Set platform to ios in config.properties
mvn clean test -Dtest=LoginFlowTest#testLoginFlowToEmailEntry
```

## 🧪 Platform-Independent Testing

### How It Works

The framework uses interface-based design to achieve platform independence:

1. **Common Test Logic**: Same test code works on both platforms
2. **Interface Abstraction**: Tests interact with `IHomePage`, `ILoginPage` interfaces
3. **PageFactory**: Automatically returns correct platform implementation
4. **Platform Detection**: Automatically detects platform from configuration

### Example Test Code

```java
@Test
public void testLoginFlowToEmailEntry() {
    // Same code works on both Web and iOS
    IHomePage homePage = PageFactory.getHomePage();
    ILoginPage loginPage = PageFactory.getLoginPage();
    
    // Platform-independent operations
    homePage.clickSignInButton();
    loginPage.enterEmail("test@example.com");
    
    // Platform-specific flow automatically handled
    if (PageFactory.getCurrentPlatform() == PlatformType.IOS) {
        loginPage.clickSignInOrRegister();
        loginPage.clickContinueWithEmail();
    }
}
```

## 📊 Platform Implementations

### Web Implementation (Selenium)
- Uses CSS selectors and XPath
- Chrome browser automation
- WebDriver-based element interactions

### iOS Implementation (Appium)
- Uses accessibility IDs and XCUITest
- Real device automation
- Appium-based element interactions

## 📈 Code Sharing Analysis

- **Test Logic**: 100% shared between platforms
- **Framework Core**: 100% shared (DriverFactory, ConfigReader, etc.)
- **Page Logic**: 90% shared (only locators differ)
- **Base Classes**: 100% shared
- **Overall**: 85-90% code reusability

## 🎯 Current Test Coverage

### Login Flow Tests ✅
- **Web**: Chrome → Booking.com → Sign In → Email Entry
- **iOS**: App Launch → Profile Tab → Sign In → Email Entry
- **Status**: Both platforms working successfully

## 📊 Test Execution Examples

### Web Testing
```bash
# Set platform=web in config.properties
mvn clean test -Dtest=LoginFlowTest
# Result: Opens Chrome → Booking.com → Login flow
```

### iOS Testing
```bash
# Set platform=ios in config.properties
# Start Appium server: appium
mvn clean test -Dtest=LoginFlowTest
# Result: Opens Booking app → Login flow
```

### Platform Switching
Simply change `platform=web|ios` in config.properties - same tests run on different platforms!

## 🔧 Framework Benefits

1. **Single Codebase**: Write once, test everywhere
2. **Reduced Maintenance**: Fix once, applies to all platforms
3. **Consistent Testing**: Same test logic ensures consistent coverage
4. **Easy Platform Addition**: Add Android by implementing interfaces
5. **Scalable Architecture**: Clean separation of concerns

## 📞 Support

This framework demonstrates modern test automation architecture with platform independence as a core principle.

## 📄 Features Demonstrated

- ✅ Interface-based design pattern
- ✅ Factory pattern for platform abstraction  
- ✅ Abstract base class hierarchy
- ✅ Platform-aware driver management
- ✅ Unified configuration management
- ✅ Cross-platform element waiting strategies
- ✅ Comprehensive error handling and logging

---

**Happy Testing! 🎉 Cross-Platform Automation Made Simple!**