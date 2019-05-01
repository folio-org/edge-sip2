package org.folio.edge.sip2.repositories;

import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utils for the repository tests.
 *
 * @author mreno-EBSCO
 *
 */
public class RepositoryTestUtils {
  private RepositoryTestUtils() {
    super();
  }

  static String getJsonFromFile(String fileName) {
    try {
      return String.join("\n", Files.readAllLines(
          Paths.get(PatronRepositoryTests.class.getClassLoader().getResource(fileName).toURI())));
    } catch (Exception e) {
      fail(e);
      return null;
    }
  }
}
