package org.example;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FundamentalsTest {
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
  @DisplayName("Основы Playwright: навигация, поиск элементов и взаимодействие")
  void testPlaywrightFundamentals() {
    /// 1. НАВИГАЦИЯ И ОЖИДАНИЯ
    page.navigate("https://demoqa.com");

    // Явное ожидание вместо Thread.sleep()
    page.waitForSelector(".card", new Page.WaitForSelectorOptions().setTimeout(10000));

    /// 2. ПОИСК ЭЛЕМЕНТОВ
    // Стабильный css селектор
    Locator elementsCard = page.locator("div.card:has-text('Elements')");
    elementsCard.click();

    // поиск по тексту
    page.locator("li.btn-light:has-text('Text Box')").click();

    // Поиск по роли aria
    Locator fullNameLabel = page.getByRole(AriaRole.TEXTBOX,
        new Page.GetByRoleOptions().setName("Full Name"));

    /// 3. ВЗАИМОДЕЙСТВИЕ С ЭЛЕМЕНТАМИ
    // Fill vs Type
    fullNameLabel.fill("Иван Иванов");  // Быстрое заполнение

    Locator emailInput = page.locator("#userEmail");
    emailInput.type("test@example.com");  // Посимвольный ввод - deprecated

    Locator addressArea = page.locator("#currentAddress");
    addressArea.fill("ул. Пушкина, д. 15");

    // Клик по кнопке
    Locator submitButton = page.locator("#submit");
    submitButton.click();

    /// 4. ПРОВЕРКИ И ПОЛЕЧЕНИЕ ДАННЫХ
    // Ожидание появления результата
    page.waitForSelector("#output");

    // Проверка текста
    Locator nameResult = page.locator("#name");
    assertTrue(nameResult.textContent().contains("Иван Иванов"),  // textContent() или innerText()
        "Неверное имя в результате");

    // Проверка атрибута
    Locator emailResult = page.locator("#email");
    assertEquals("test@example.com", emailResult.textContent().replace("Email:", "").trim(),
        "Неверный email в результате");

    /// 5. РАБОТА С ЧЕКБОКСАМИ И РАДИО-КНОПКАМИ

    page.locator("li:has-text('Check Box')").click();

    // Чекбоксы
    Locator homeCheckbox = page.locator("label:has-text('Home') .rct-checkbox");
    homeCheckbox.check();
    assertTrue(homeCheckbox.isChecked(), "Чекбокс Home должен быть выбран");

    // Радио-кнопки
    page.locator("li:has-text('Radio Button')").click();
    Locator impressiveRadio = page.locator("label:has-text('Impressive')");
    impressiveRadio.check();
    assertTrue(impressiveRadio.isChecked(), "Радиокнопка Impressive должна быть выбрана");

  }
}
