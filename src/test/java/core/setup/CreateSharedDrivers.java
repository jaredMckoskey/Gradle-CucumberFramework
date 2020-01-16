package core.setup;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeDriverService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.concurrent.TimeUnit;

import static core.utilities.Tools.logger;

@SuppressWarnings("DanglingJavadoc")
public class CreateSharedDrivers {

  /**
   * initialize this class to create a driver if driver is not null
   */
  public CreateSharedDrivers() {
    if (Hooks.getDriver() == null) {
      createAndSetAddedDriver();
    }
  }

  /**
   * quits all storedDrivers with a shutdown hook
   */
  static {
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () ->
                    Hooks.storedDrivers.forEach(
                        driver -> {
                          logger()
                              .info(
                                  String.format(
                                      "Stored Driver Count: [%s]",
                                      Hooks.storedDrivers.size()));

                          logger().info(String.format("Driver [%s] will be quit", driver));

                          try {
                            driver.quit();
                          } catch (Exception e) {
                            //noinspection UnusedAssignment
                            driver = null;
                          }
                        })));
  }

  /**
   * creates a driver, adds it to to storedDrivers which sets the driver to the current driver being
   * added
   */
  private void createAndSetAddedDriver() {
    logger().traceEntry();
    createDriver();
    logger().traceExit();
  }

  private void createDriver() {
    if (Config.IS_REMOTE) {
      try {
        Hooks.addDriver(new RemoteWebDriver(Hooks.url, Hooks.capabilities));
        Hooks.getDriver().manage().deleteAllCookies();
      } catch (ElementNotInteractableException e) {
        // Ignore Exception
      }

    } else {
      logger().traceEntry();

      switch (Config.getDeviceName()) {
        case "chrome":
          WebDriverManager.chromedriver().setup();
          System.setProperty("webdriver.chrome.silentOutput", "true");
          Hooks.addDriver(new ChromeDriver(new ChromeOptions()
              .addArguments("--start-maximized")));
          break;
        case "chromeHeadless":
          WebDriverManager.chromedriver().setup();
          System.setProperty("webdriver.chrome.silentOutput", "true");
          Hooks.addDriver(
              new ChromeDriver(
                  new ChromeOptions()
                      .setHeadless(true)
                      .addArguments("no-sandbox")
                      .addArguments("window-size=1920,1080")));
          break;
        case "firefox":
          WebDriverManager.firefoxdriver().setup();
          System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "false");
          Hooks.addDriver(new FirefoxDriver());
          Hooks.getDriver().manage().window().maximize();
          break;
        case "edge":
          WebDriverManager.edgedriver().setup();
          System.setProperty(EdgeDriverService.EDGE_DRIVER_LOG_PROPERTY, "null");
          Hooks.addDriver(new EdgeDriver());
          Hooks.getDriver().manage().window().maximize();
          break;
        case "ie11":
          WebDriverManager.iedriver().setup();
//          InternetExplorerDriverService.Builder ieDriverService = new InternetExplorerDriverService.Builder().withSilent(true);
//          Hooks.addDriver(new InternetExplorerDriver(ieDriverService.build()));
          InternetExplorerOptions ieOptions = new InternetExplorerOptions();
//          ieOptions.setCapability(InternetExplorerDriver.UNEXPECTED_ALERT_BEHAVIOR, "accept");
//          ieOptions.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL, "http://localhost");
//          ieOptions.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
//          ieOptions.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING, true);
//          ieOptions.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
//          ieOptions.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
//          ieOptions.setCapability(InternetExplorerDriver.ENABLE_ELEMENT_CACHE_CLEANUP, true);
//          ieOptions.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false);
//          ieOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);
//          ieOptions.setCapability(InternetExplorerDriver.FORCE_CREATE_PROCESS, true);
//          ieOptions.setCapability(InternetExplorerDriver.IE_SWITCHES, "-private");
//          System.setProperty("webdriver.ie.driver.silent", "true");
          Hooks.addDriver(new InternetExplorerDriver(ieOptions));
          Hooks.getDriver().manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
          Hooks.getDriver().manage().window().maximize();
          break;
        default:
          throw new IllegalStateException("Unexpected value: " + Config.getDeviceName());
      }
    }
  }
}
