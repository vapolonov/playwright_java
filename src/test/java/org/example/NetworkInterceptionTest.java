package org.example;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NetworkInterceptionTest {
  Playwright playwright;
  Browser browser;
  BrowserContext context;
  Page page;

  @BeforeEach
  void setup() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
        .setHeadless(false));
    context = browser.newContext();
    page = context.newPage();
  }

  @Test
  void mockBookApi() {
    // Мокируем ответ без задержки
    page.route("**/BookStore/v1/Books", route -> {
      String mockResponse = """
          {
            "books": [
              {
                "isbn": "test-isbn-123",
                "title": "Playwright для QA",
                "subtitle": "Тестирование с удовольствием",
                "author": "Иван Тестировщиков",
                "publish_date": "2020-06-04T09:12:43.000Z",
                "publisher": "Test-Издат",
                "pages": 333,
                "description": "Лучшая книга по тестированию с playwright",
                "website": "https://playwright.com"
              }
            ]
          }
          """;
      route.fulfill(new Route.FulfillOptions()
          .setStatus(200)
          .setContentType("application/json")
          .setBody(mockResponse));
    });

    // навигация с ожиданием загрузки DOMContentLoaded
    page.navigate("https://demoqa.com/books", new Page.NavigateOptions()
        .setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

    // Имитируем задержку (например, задержка рендеринга UI после получения данных)
    page.waitForTimeout(1500);

    // Ждем появления таблицы с данными
    Locator bookRow = page.locator(".rt-tbody .rt-tr-group").first();
    bookRow.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));

    // Проверяем данные
    Locator bookTitle = bookRow.locator(".rt-td:nth-child(2)");
    assertEquals("Playwright для QA", bookTitle.textContent().trim());
  }

  @AfterEach
  void tearDown() {
    if (page != null) page.close();
    if (context != null) context.close();
    if (browser != null) browser.close();
    if (playwright != null) playwright.close();
  }
}
