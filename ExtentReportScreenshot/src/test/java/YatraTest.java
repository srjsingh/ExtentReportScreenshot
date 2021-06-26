import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class YatraTest {
	
	public WebDriver driver;
	public ExtentReports extent;
	public ExtentTest extentTest;
	
	
	@BeforeTest
	public void setExtent() {
		
		extent =  new ExtentReports(System.getProperty("user.dir") + "\\test-output\\" + "Extent.html", true);
		extent.addSystemInfo("Host Name", "Suraj Windows");
		extent.addSystemInfo("User Name", "Suraj Practice");
		extent.addSystemInfo("Environment", "QA");
		
	}
	
	@AfterTest
	public void endReport() {
		
		extent.flush();
		extent.close();
		
	}
	
	public static String getScreenshot(WebDriver driver, String screenshotName) throws IOException{
		
		String dateName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		
		File source = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		
		String destination = System.getProperty("user.dir") + "\\FailedTestsScreenshots\\" + screenshotName + dateName + ".png";
		
		FileUtils.copyFile(source, new File(destination));
		
		return destination;
		
	}

	@BeforeMethod
	public void setup() {
		
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\Suraj\\Desktop\\Selenium Jar\\Chrome Driver\\chromedriver.exe");
		Map<String, Object> prefs = new HashMap<String, Object>();

        prefs.put("profile.default_content_setting_values.notifications", 2);

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", prefs);
		
		driver = new ChromeDriver(options);
		
		driver.manage().deleteAllCookies();
		driver.manage().window().maximize();
		driver.manage().timeouts().pageLoadTimeout(50, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);
		
		
		driver.get("http://yatra.com");
		
	}
	
	@Test
	public void yatraTitleTest() {
		
		extentTest = extent.startTest("yatraTitleTest");
		
		String title = driver.getTitle();
		
		System.out.println(title);
		
		Assert.assertEquals(title, "Flight, Cheap Air Tickets , Hotels, Holiday, Trains Package Booking - Yatra.com");
		

	}
	
	@AfterMethod
	public void tearDown(ITestResult result) throws IOException {
		
		if (result.getStatus() == ITestResult.FAILURE) {
			
			extentTest.log(LogStatus.FAIL, "Test Case Failed is: " + result.getName()); // this step will add test case name in extent report
			extentTest.log(LogStatus.FAIL, "Test Case Failes is: " + result.getThrowable()); // this step will add error in extent report
			
			String screenshotPath = YatraTest.getScreenshot(driver, result.getName());
			extentTest.log(LogStatus.FAIL, extentTest.addScreenCapture(screenshotPath)); // this step will add screenshot in extent report
			
			// can add execution video also using logger.addScreencast(screenshotPath);
			
		}
		else if (result.getStatus() == ITestResult.SKIP) {
			
			extentTest.log(LogStatus.SKIP, "Test Case Skipped is: " + result.getName());
			
		}
		else if (result.getStatus() == ITestResult.SUCCESS) {
			
			extentTest.log(LogStatus.PASS, "Test Case Passes is: " + result.getName());
			
		}
		
		extent.endTest(extentTest); // end test ends the test and prepare the report
		
		driver.quit();
		
	}
	
	
}
