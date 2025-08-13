package org.example;

import com.microsoft.playwright.*;
import com.microsoft.playwright.assertions.LocatorAssertions;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.*;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class AdvancedWaitsDemo {
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
  void testWaitsInRealScenario() {
    /// 1. АВТОМАТИЧЕСКИЕ ОЖИДАНИЯ
    page.navigate("https://demoqa.com/dynamic-properties");

    // Кнопка станет активной через 5 сек - авто ожидание сработает!
    page.locator("#enableAfter").click();

    // Поле появится через 5 сек - и здесь ожидание сработает
//    page.locator("#visibleAfter").fill("Test");

    /// 2. ЯВНЫЕ ОЖИДАНИЯ ДЛЯ СЛОЖНЫХ УСЛОВИЙ
    // ждем появления элемента с таймаутом 7 сек
    page.waitForSelector("#visibleAfter",
        new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE)
            .setTimeout(7000));

    // ожидаем изменения CSS-свойства (кастомное условие)
    page.waitForFunction(
        "() => window.getComputedStyle(document.querySelector('#colorChange')).color === 'rgb(255, 0, 0)'",
        new Page.WaitForFunctionOptions().setTimeout(8000)
    );

    // Ожидание перехода на страницу
    page.waitForURL("**/checkout/confirmation",
        new Page.WaitForURLOptions().setTimeout(5000));

    /// 3. УМНЫЕ АССЕРТЫ С ОЖИДАНИЯМИ
    // проверка текста с автоматическим ожиданием
    assertThat(page.locator("#app > div > div > div.row > div.col-12.mt-4.col-md-6 > div"))
        .hasText("This text has random Id",
            new LocatorAssertions.HasTextOptions().setTimeout(5000));

    // ПРАВИЛЬНАЯ проверка видимости и активности
    Locator checkoutButton = page.locator("#visibleAfter");

    // Отдельные ассерты для каждого условия
    assertThat(checkoutButton)
        .isVisible(new LocatorAssertions.IsVisibleOptions().setTimeout(3000));

    assertThat(checkoutButton)
        .isEnabled(new LocatorAssertions.IsEnabledOptions().setTimeout(3000));

    // дополнительная проверка атрибута
    assertThat(checkoutButton)
        .hasAttribute("data-status", "activa",
            new LocatorAssertions.HasAttributeOptions().setTimeout(2000));

  }
}
