package org.example;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class IFrameMasterTest {
  static Playwright playwright;
  static Browser browser;
  BrowserContext context;
  Page page;

  @BeforeAll
  public static void launchBrowser() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
        .setHeadless(false)
        .setSlowMo(500));  // замедление
  }

  @BeforeEach
  public void createContexAndPage() {
    context = browser.newContext();
    page = context.newPage();
  }

  @AfterEach
  public void closeContext() {
    context.close();
  }

  @AfterAll
  public static void closeBrowser() {
    browser.close();
    playwright.close();
  }

  @Test
  void testIFrameWorkflow() {
    // 1. Открываем страницу с фреймами
    page.navigate("https://demoqa.com/frames");

    // 2. Захватываем первый фрейм
    FrameLocator firstFrame = page.frameLocator("#frame1");

    // 3. Проверяем текст внутри фрейма
    assertThat(firstFrame.locator("#sampleHeading"))
        .hasText("This is a sample page");

    // 4. Границы фрейма
    page.locator("#frame1").evaluate("e => e.style.border = '3px solid red'");

    // 5. переходим на страницу с вложенными фреймами
    page.locator("'Nested Frames'").click();

    // 6. Работаем с иерархией фреймов
    FrameLocator parentFrame = page.frameLocator("#frame1");
    FrameLocator childFrame = parentFrame.frameLocator("iframe");

    // 7. проверяем текст в дочернем фрейме
    assertThat(childFrame.locator("body"))
        .containsText("Child Iframe");

    // 8. Делаем скриншот содержимого фрейма
    parentFrame.locator("body")
        .screenshot(new Locator.ScreenshotOptions()
            .setPath(Paths.get("parent_frame.png")));

    // 9. Демонстрация работы с динамическими фреймами
    // (в учебных целях - используем тот же фрейм)
    page.frameLocator("//iframe[contains(@id, 'frame')]")
        .locator("body")
        .click();

    System.out.println("Все шаги выполнены успешно");

  }
}
