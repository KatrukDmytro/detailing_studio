package detailingstudio.controller;

import detailingstudio.service.S3Service;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final S3Service s3Service;

    public MediaController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @GetMapping("/presigned-url")
    public S3Service.PresignedUrlResponse getPresignedUrl(
            @RequestParam String filename,
            @RequestParam String contentType) {
        return s3Service.generatePresignedUrl(filename, contentType);
    }
}
