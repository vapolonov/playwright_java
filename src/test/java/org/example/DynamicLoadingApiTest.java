package org.example;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DynamicLoadingApiTest {
  Playwright playwright;
  Browser browser;
  BrowserContext context;
  Page page;

  /**
   * Тест проверки динамического контента:
   * 1. Инициализация с включенной трассировкой
   * 2. переход на тестовую страницу
   * 3. Мониторинг сетевых ответов с валидностью статусов
   * 4. Взаимодействие с элементами интерфейса
   * 5. Сохранение трассировочных данных при успешном выполнении
   */

  @BeforeEach
  public void setup() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
        .setHeadless(false));
    context = browser.newContext();
  }

  @Test
  void testDynamicLoading() {
    context.tracing().start(new Tracing.StartOptions()
        .setScreenshots(true));

    page = context.newPage();
    page.navigate("https://the-internet.herokuapp.com/dynamic-loading");

    page.onResponse(response -> {
      if (response.url().contains("/dynamic-loading")) {
        assertEquals(200, response.status(),
            "Неверный статус ответа для URL: " + response.url());
      }
    });

    page.click("button");
    Locator finishText = page.locator("#finish");
    finishText.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

    assertEquals("Yello World!", finishText.textContent().trim(),
        "Текст элемента не соответствует ожидаемому");

    context.tracing().stop(new Tracing.StopOptions()
        .setPath(Paths.get("trace/trace-success.zip")));
  }

  @AfterEach
  public void tearDown() {
    if (page != null) page.close();
    if (context != null) context.close();
    if (browser != null) browser.close();
    if (playwright != null) playwright.close();
  }
}
