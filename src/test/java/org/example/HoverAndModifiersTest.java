package org.example;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.KeyboardModifier;
import org.junit.jupiter.api.*;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HoverAndModifiersTest {
  static Playwright playwright;
  static Browser browser;
  BrowserContext context;
  Page page;

  @BeforeAll
  public static void launchBrowser() {
    playwright = Playwright.create();
    browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
        .setHeadless(false));
  }

  @BeforeEach
  public void createContextAndPage() {
    context = browser.newContext(new Browser.NewContextOptions()
        .setAcceptDownloads(true));
    page = context.newPage();
    page.navigate("https://demoqa.com/menu");
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
  @DisplayName("Тестирование hover и кликов с модификаторами")
  public void testHoverAndModifiers() {
    /// 1. БАЗОВЫЙ HOVER
    // Наводим курсор на основной элемент меню
    Locator mainItem = page.locator("'Main Item 2'");
    mainItem.hover();  // имитация наведения курсора

    // проверяем появление подменю
    Locator subMenu = page.locator("'SUB SUB LIST »'");
    assertTrue(subMenu.isVisible(), "Подменю должно отображаться после hover");

    // Наводим на элемент подменю
    subMenu.hover();  // Каскадный hover эффект

    // Проверяем появление вложенного меню
    Locator subItem = page.locator("'Sub Sub Item 1'");
    assertTrue(subItem.isVisible(), "Вложенное меню должно отображаться после hover");

    /// 2. КЛИКИ С МОДИФИКАТОРАМИ
    // Переходим на страницу для работы с кликами
    page.navigate("https://demoqa.com/links");

    // Shift+Click (Открытие в новой вкладке)
    Page newPage = context.waitForPage(() -> {
      page.locator("#simpleLink").click(new Locator.ClickOptions()
          .setModifiers(Arrays.asList(KeyboardModifier.SHIFT)));
    });

    // Проверяем новую вкладку
    newPage.waitForLoadState();
    assertEquals("https://demoqa.com/", newPage.url(),
        "Ссылка должна открыться в новой вкладке с Shift");

    // Возвращаем фокус на исходную страницу
    page.bringToFront();

    // Ctrl+Click (добавление в закладки)
    page.locator("#dynamicLink").click(new Locator.ClickOptions()
        .setModifiers(Arrays.asList(KeyboardModifier.CONTROL)));

    // Проверяем отсутствие навигации
    assertEquals(("https://demoqa.com/links"), page.url(),
        "Страница не должна навигировать при Ctrl+Click");

    ///  3. РАСКРЫВАЕМ ВСЕ ДЕРЕВО ЭЛЕМЕНТОВ ПЕРЕД ВЗАИМОДЕЙСТВИЕМ
    // переходим на страницу чекбоксов
    page.navigate("https://demoqa.com/checkbox");

    // ВАЖНЫЙ ПОДГОТОВИТЕЛЬНЫЙ ЭТАП!!!
    page.locator("button[title='Toggle']").first().click();
    page.locator("button[title='Toggle']").nth(1).click();
    page.locator("button[title='Toggle']").nth(3).click();

    ///  4. CTRL+CLICK ДЛЯ МНОЖЕСТВЕННОГО ВЫБОРА
    // Выбираем Desktop с помощью Ctrl+Click
    Locator desktopLabel = page.locator("label[for='tree-node-desktop']");
    desktopLabel.click(new Locator.ClickOptions()
        .setModifiers(Arrays.asList(KeyboardModifier.CONTROL)));

    // Проверяем, что desktop выбран
    Locator desktopCheckbox = page.locator("#tree-node-desktop");
    assertTrue(desktopCheckbox.isChecked(), "Desktop должен быть выбран после Ctrl+Click");

    // Выбираем Documents при помощи Ctrl+Click
    Locator documentsLabel = page.locator("label[for='tree-node-documents']");
    documentsLabel.click(new Locator.ClickOptions()
        .setModifiers(Arrays.asList(KeyboardModifier.CONTROL)));

    // проверяем множественный выбор
    Locator documentsCheckbox = page.locator("#tree-node-documents");
    assertTrue(desktopCheckbox.isChecked(), "Desktop должен оставаться выбранным");
    assertTrue(documentsCheckbox.isChecked(), "Documents должен оставаться выбранным");




  }
}
