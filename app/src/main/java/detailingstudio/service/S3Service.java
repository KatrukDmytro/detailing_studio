package detailingstudio.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucket;
    private final String publicUrlPrefix;

    public S3Service(S3Client s3Client, 
                     S3Presigner s3Presigner,
                     @Value("${app.s3.bucket}") String bucket,
                     @Value("${app.s3.public-url-prefix}") String publicUrlPrefix) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.bucket = bucket;
        this.publicUrlPrefix = publicUrlPrefix;
    }

    public String uploadFile(byte[] data, String originalFilename, String contentType) {
        String key = generateKey(originalFilename);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(data));

        return publicUrlPrefix + "/" + key;
    }

    public PresignedUrlResponse generatePresignedUrl(String originalFilename, String contentType) {
        String key = generateKey(originalFilename);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        
        String uploadUrl = presignedRequest.url().toString();
        String publicUrl = publicUrlPrefix + "/" + key;

        return new PresignedUrlResponse(uploadUrl, publicUrl);
    }

    private String generateKey(String originalFilename) {
        String extension = "";
        int i = originalFilename.lastIndexOf('.');
        if (i > 0) {
            extension = originalFilename.substring(i);
        }
        return UUID.randomUUID().toString() + extension;
    }

    public record PresignedUrlResponse(String uploadUrl, String publicUrl) {}
}
