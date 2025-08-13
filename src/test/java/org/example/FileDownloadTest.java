package org.example;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileDownloadTest {

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
    page.navigate("https://demoqa.com/upload-download");
  }

  @Test
  @DisplayName("Тестирование загрузки файлов")
  void testFileDownload() throws IOException {
    // Обработка события загрузки
    // Ожидаем событие download
    Download download = page.waitForDownload(() -> {
      page.click("#downloadButton");
    });

    /// 2. СОХРАНЕНИЕ ФАЙЛА
    // Создаем временный путь
    Path tempDir = Files.createTempDirectory("playwright-downloads");
    Path filePath = tempDir.resolve(download.suggestedFilename());

    // Сохраняем файл
    download.saveAs(filePath);
    System.out.println("Файл сохранен: " + filePath);

    /// 3. ПРОВЕРКА ФАЙЛА
    // Проверяем существование
    assertTrue(Files.exists(filePath), "Файл должен существовать");

    // Проверяем размер
    long fileSize = Files.size(filePath);
    assertTrue(fileSize > 0, "Файл не должен быть пустым");

    // Проверяем расширение
    assertTrue(filePath.toString().endsWith(".jpeg"), "Файл должен быть в формате JPEG");

    /// 4. ПРОВЕРКА СОДЕРЖИМОГО
    String mimeType = Files.probeContentType(filePath);
    assertEquals("image/jpeg", mimeType, "MIME-тип должен быть image/jpeg");

    /// 5. ДОПОЛНИТЕЛЬНАЯ ПРОВЕРКА
    byte[] fileContent = Files.readAllBytes(filePath);
    assertTrue(fileContent.length > 1000, "Размер файла должен быть больше 1KB");

    // Удаляем временные файлы (в реальны тестах не нужно)
    Files.deleteIfExists(filePath);

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
}
