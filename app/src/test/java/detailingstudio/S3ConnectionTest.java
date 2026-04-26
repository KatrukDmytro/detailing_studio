package detailingstudio;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("h2")
class S3ConnectionTest {

    @Autowired
    private S3Client s3Client;

    @Value("${app.s3.bucket}")
    private String bucketName;

    @Test
    void verifyConnection() {
        assertDoesNotThrow(() -> {
            System.out.println("Checking connection to bucket: " + bucketName);
            ListObjectsV2Response response = s3Client.listObjectsV2(ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .maxKeys(1)
                    .build());
            System.out.println("Connection successful! Found " + response.contents().size() + " objects (max 1 checked).");
        }, "S3 Connection failed!");
    }
}
