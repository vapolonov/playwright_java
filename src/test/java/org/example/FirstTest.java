package org.example;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FirstTest {

  Playwright playwright;
  Browser browser;
  Page page;

  @BeforeEach
  void setUp() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
    page = browser.newPage();
  }

  @AfterEach
  void tearDown() {
    browser.close();
    playwright.close();
  }

  @Test
  void shouldOpenBrowser() {
    page.navigate("https://playwright.dev");
    String title = page.title();
    assertEquals("Playwright", title);
  }
}
