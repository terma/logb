package com.github.terma.logb.server;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class E2EIT {

    private Runner server;
    private WebDriver driver;

    @Before
    public void setUp() throws Exception {
        server = new Runner();
        server.start();

        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @Test
    public void showMainPage() throws Exception {
        driver.get(Runner.ADDRESS);
        assertThat(driver.getTitle(), containsString("logb"));

        Thread.sleep(1000L);

        List<WebElement> apps = driver.findElements(By.tagName("a"));
        assertThat(apps, hasSize(3));

        List<String> texts = getTexts(apps);
        assertThat(texts, Matchers.equalTo(Arrays.asList("app-local", "app-remote", "app-without-servers")));
    }

    private static List<String> getTexts(List<WebElement> apps) {
        List<String> texts = new ArrayList<>();
        for (WebElement element : apps) {
            texts.add(element.getText());
        }
        return texts;
    }

    @Test
    public void canSelectApp() throws Exception {
        driver.get(Runner.ADDRESS+"#aaa");
        Thread.sleep(1000L);
        driver.findElement(By.tagName("a")).click();
        Thread.sleep(1000L);

        assertThat(driver.getCurrentUrl(), Matchers.containsString("#/ls/app-local"));

        List<WebElement> files = driver.findElement(By.id("content")).findElements(By.tagName("a"));
        assertThat(files, Matchers.hasSize(1));
        assertThat(files.get(0).getText(), Matchers.containsString("config-e2e.json"));
    }

    @Test
    public void canSelectFile() throws Exception {
        driver.get(Runner.ADDRESS + "#/ls/app-local");
        Thread.sleep(1000L);
        final String currentWindow = driver.getWindowHandle();
        driver.findElement(By.id("content")).findElement(By.tagName("a")).click();
        Set<String> windowHandles = driver.getWindowHandles();
        windowHandles.remove(currentWindow);
        driver.switchTo().window(windowHandles.iterator().next());
        Thread.sleep(1000L);

        assertThat(driver.getCurrentUrl(), Matchers.containsString("#/tail/app-local/"));
        assertThat(driver.findElement(By.id("log-header")).getText(), Matchers.containsString("/config-e2e.json"));
        assertThat(driver.findElement(By.id("content")).getText(), Matchers.containsString("\"name\": \"app-local\","));
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
//        server.join();
        driver.quit();
    }

}
