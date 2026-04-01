package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

  private static final Pattern PHONE_PATTERN =
          Pattern.compile("(\\+?\\d[\\d\\s()\\-]{8,}\\d)");

  public static void main(String[] args) {
    String inputFileName = "input.txt";
    String outputFileName = "output.txt";

    try {
      String text = readFromResources(inputFileName);

      Result result = normalizePhonesInText(text);

      writeToFile(outputFileName, result.text());

      System.out.println("Обработка завершена.");
      System.out.println("Количество замен: " + result.count());
      System.out.println("Результат записан в файл: " + outputFileName);

    } catch (IOException e) {
      System.out.println("Ошибка при работе с файлом: " + e.getMessage());
    }
  }

  // Чтение файла из resources
  public static String readFromResources(String fileName) throws IOException {
    InputStream inputStream = Main.class
            .getClassLoader()
            .getResourceAsStream(fileName);

    if (inputStream == null) {
      throw new IOException("Файл не найден в resources: " + fileName);
    }

    return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
  }

  // Запись результата в обычный файл
  public static void writeToFile(String fileName, String content) throws IOException {
    Files.writeString(Path.of(fileName), content, StandardCharsets.UTF_8);
  }

  // Обработка всего текста
  public static Result normalizePhonesInText(String text) {
    Matcher matcher = PHONE_PATTERN.matcher(text);
    StringBuffer sb = new StringBuffer();
    int replacementsCount = 0;

    while (matcher.find()) {
      String originalPhone = matcher.group();
      String normalizedPhone = normalizePhone(originalPhone);

      if (normalizedPhone != null) {
        matcher.appendReplacement(sb, Matcher.quoteReplacement(normalizedPhone));
        replacementsCount++;
      } else {
        matcher.appendReplacement(sb, Matcher.quoteReplacement(originalPhone));
      }
    }

    matcher.appendTail(sb);

    return new Result(sb.toString(), replacementsCount);
  }

  // Нормализация одного номера
  public static String normalizePhone(String rawPhone) {
    // Удаляем все символы, кроме цифр
    String digits = rawPhone.replaceAll("\\D", "");

    // Если номер состоит из 11 цифр и начинается с 7 или 8,
    // удаляем первую цифру
    if (digits.length() == 11 && (digits.startsWith("7") || digits.startsWith("8"))) {
      digits = digits.substring(1);
    }

    // После этого должно остаться 10 цифр
    if (digits.length() != 10) {
      return null;
    }

    String areaCode = digits.substring(0, 3);
    String localNumber = digits.substring(3);

    // Единый формат с новым кодом страны
    return "+1 (" + areaCode + ") " + localNumber;
  }

  // Класс для хранения результата
  record Result(String text, int count) {
  }
}