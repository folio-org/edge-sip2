package org.folio.edge.sip2.support.minio;

import lombok.Getter;
import org.folio.s3.client.FolioS3Client;
import org.folio.s3.client.S3ClientFactory;
import org.folio.s3.client.S3ClientProperties;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

public class MinioContainerExtension implements BeforeAllCallback, AfterAllCallback {

  public static final String TEST_AWS_REGION = "us-west-1";
  public static final String TEST_BUCKET_NAME = "test-sip2";
  private static final DockerImageName IMAGE_NAME =
      DockerImageName.parse("minio/minio:RELEASE.2023-09-04T19-57-37Z");

  @Container
  private static final MinIOContainer MINIO_CONTAINER = new MinIOContainer(IMAGE_NAME)
      .withStartupAttempts(3)
      .withUserName("minio_admin")
      .withPassword("minio_password");

  @Getter
  private static FolioS3Client s3Client;

  @Override
  public void beforeAll(ExtensionContext context) {
    if (!MINIO_CONTAINER.isRunning()) {
      MINIO_CONTAINER.start();
    }

    System.setProperty("AWS_URL", MINIO_CONTAINER.getS3URL());
    System.setProperty("AWS_REGION", TEST_AWS_REGION);
    System.setProperty("AWS_ACCESS_KEY_ID", MINIO_CONTAINER.getUserName());
    System.setProperty("AWS_SECRET_ACCESS_KEY", MINIO_CONTAINER.getPassword());
    System.setProperty("AWS_BUCKET", TEST_BUCKET_NAME);

    var s3ClientProperties = S3ClientProperties
        .builder()
        .endpoint(MINIO_CONTAINER.getS3URL())
        .accessKey(MINIO_CONTAINER.getUserName())
        .secretKey(MINIO_CONTAINER.getPassword())
        .bucket(TEST_BUCKET_NAME)
        .region(TEST_AWS_REGION)
        .build();

    s3Client = S3ClientFactory.getS3Client(s3ClientProperties);
    s3Client.createBucketIfNotExists();
  }

  @Override
  public void afterAll(ExtensionContext context) {
    System.clearProperty("AWS_URL");
    System.clearProperty("AWS_REGION");
    System.clearProperty("AWS_ACCESS_KEY_ID");
    System.clearProperty("AWS_SECRET_ACCESS_KEY");
    System.clearProperty("AWS_BUCKET");
  }
}
