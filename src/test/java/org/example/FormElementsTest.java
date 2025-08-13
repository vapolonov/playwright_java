package org.example;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.SelectOption;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormElementsTest {
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
  void testFormElements() {
    page.navigate("https://demoqa.com/automation-practice-form");
    /// РАБОТА С РАДИО-КНОПКАМИ
    page.locator("label[for='gender-radio-1']").click();
    boolean isMaleSelected = page.locator("#gender-radio-1").isChecked();
    assertTrue(isMaleSelected, "Радио кнопка 'Male' должна быть выбрана");

    /// РАБОТА С ЧЕКБОКСАМИ
    // Выбор двух чекбоксов
    page.locator("label[for='hobbies-checkbox-1']").click();
    page.locator("label[for='hobbies-checkbox-3']").click();

    // Проверка состояния
    boolean isSportsChecked = page.locator("#hobbies-checkbox-1").isChecked();
    boolean isMusicChecked = page.locator("#hobbies-checkbox-3").isChecked();
    assertTrue(isSportsChecked, "Чекбокс 'Sports' должен быть выбран");
    assertTrue(isMusicChecked, "Чекбокс 'Music' должен быть выбран");

    // Снятие выбора с одного чекбокса
    page.locator("label[for='hobbies-checkbox-3']").click();
    boolean isMusicUnchecked = !page.locator("#hobbies-checkbox-3").isChecked();
    assertTrue(isMusicUnchecked, "Чекбокс 'Music' должен быть снят");

    /// РАБОТА С ВЫПАДАЮЩИМ СПИСКОМ
    // Раскрытие списка
    page.locator("#state").click();

    // Выбор опции по значению
    page.locator("#state").selectOption(new SelectOption().setValue("NCR"));

    // Проверка выбора
    String selectedState = page.locator("#state div.css-1uccc91-singleValue").innerText();
    assertEquals("NCR", selectedState, "Должен быть выбран штат NCR");

    // Комбинированный пример
    // Выбор опции по видимому тексту
    page.locator("#city").click();
    page.locator("#city").selectOption(new SelectOption().setLabel("Delhi"));

    // Проверка комбинированного состояния
    String selectedCity = page.locator("#city div.css-1uccc91-singleValue").innerText();
    assertEquals("Delhi", selectedCity, "Должен быть выбран город Delhi");
    assertTrue(page.locator("#gender-radio-1").isChecked(), "Пол должен остаться выбранным");
    assertTrue(page.locator("#gender-radio-3").isChecked(), "Sports должен остаться выбранным");


  }


}
