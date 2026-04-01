package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {

  @Test
  void testNormalizePhoneValid() {
    String result = Main.normalizePhone("+79001234567");
    assertEquals("+1 (900) 1234567", result);
  }

  @Test
  void testNormalizePhoneInvalid() {
    String result = Main.normalizePhone("12345");
    assertNull(result);
  }
}