package org.folio.edge.sip2.domain.messages.enumerations;

import static org.assertj.core.api.Assertions.assertThat;

import org.folio.edge.sip2.support.tags.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

@UnitTest
class MessagesTest {

  @ParameterizedTest
  @EnumSource(Messages.class)
  void fromStringSafe_parameterized_uppercase(Messages message) {
    var name = message.name();
    var result = Messages.fromStringSafe(name);
    assertThat(result).isEqualTo(message);
  }

  @ParameterizedTest
  @EnumSource(Messages.class)
  void fromStringSafe_parameterized_lowercase(Messages message) {
    var name = message.name().toLowerCase();
    var result = Messages.fromStringSafe(name);
    assertThat(result).isEqualTo(message);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "INVALID_MESSAGE",
      "",
      "  ",
      "12345",
      "PATRON-STATUS-REQUEST",
      "check out"
  })
  void fromStringSafe_negative_invalidName(String name) {
    var result = Messages.fromStringSafe(name);
    assertThat(result).isNull();
  }

  @Test
  void fromStringSafe_negative_nullInput() {
    var result = Messages.fromStringSafe(null);
    assertThat(result).isNull();
  }
}
