package org.folio.edge.sip2.domain.messages.enumerations;

import static org.assertj.core.api.Assertions.assertThat;

import org.folio.edge.sip2.support.tags.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

@UnitTest
class CurrencyTypeTest {

  @ParameterizedTest
  @EnumSource(CurrencyType.class)
  void fromStringSafe_parameterized_uppercase(CurrencyType currencyType) {
    var name = currencyType.name();
    var result = CurrencyType.fromStringSafe(name);
    assertThat(result).isEqualTo(currencyType);
  }

  @ParameterizedTest
  @EnumSource(CurrencyType.class)
  void fromStringSafe_parameterized_lowercase(CurrencyType currencyType) {
    var name = currencyType.name().toLowerCase();
    var result = CurrencyType.fromStringSafe(name);
    assertThat(result).isEqualTo(currencyType);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "INVALID_MESSAGE",
      "",
      "   ",
      "12345",
  })
  void fromStringSafe_negative_invalidName(String name) {
    var result = CurrencyType.fromStringSafe(name);
    assertThat(result).isNull();
  }

  @Test
  void fromStringSafe_negative_nullInput() {
    var result = CurrencyType.fromStringSafe(null);
    assertThat(result).isNull();
  }
}
