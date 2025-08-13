package org.example;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ParallelExecutionTest {

  private Playwright playwright;
  private Browser browser;

  @BeforeEach
  void setup() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
        .setHeadless(true));
  }

  @AfterEach
  void tearDown() {
    browser.close();
    playwright.close();
  }

  @Test
  void testGoogleTitle() {
    BrowserContext context = browser.newContext();
    Page page = context.newPage();
    page.navigate("https://www.google.com");
    assertTrue(page.title().contains("Google"));
    context.close();
  }

  @Test
  void testPlaywrightDocs() {
    BrowserContext context = browser.newContext();
    Page page = context.newPage();
    page.navigate("https://playwright.dev/java");
    assertTrue(page.title().contains("Playwright"));
    context.close();
  }

  @Test
  void testWikipedia() {
    BrowserContext context = browser.newContext();
    Page page = context.newPage();
    page.navigate("https://www.wikipedia.org");
    assertTrue(page.title().contains("Wikipedia"));
    context.close();
  }
}
