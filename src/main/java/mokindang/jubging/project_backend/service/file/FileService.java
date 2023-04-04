package mokindang.jubging.project_backend.service.file;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class FileService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3Client amazonS3Client;

    public FileResponse uploadFile(MultipartFile multipartFile) {

        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("이미지가 선택되지 않았습니다.");
        }

        String uploadFilePath = "profile_image";

        String originalFileName = multipartFile.getOriginalFilename();
        String uploadFileName = getUuidFileName(originalFileName);
        String uploadFileUrl = "";

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {

            String keyName = uploadFilePath + "/" + uploadFileName;

            amazonS3Client.putObject(
                    new PutObjectRequest(bucket, keyName, inputStream, objectMetadata));

            uploadFileUrl = amazonS3Client.getUrl(bucket, keyName).toString();

        } catch (IOException e) {
            e.printStackTrace();
            log.error("Filed upload failed", e);
        }

        return new FileResponse(uploadFileUrl, uploadFileName);
    }

    public void deleteFile(String uploadFileName) {

        String uploadFilePath = "profile_image";

        try {
            String keyName = uploadFilePath + "/" + uploadFileName;
            boolean isObjectExist = amazonS3Client.doesObjectExist(bucket, keyName);
            if (isObjectExist) {
                amazonS3Client.deleteObject(bucket, keyName);
            } else {
                log.info("file not found");
            }
        } catch (Exception e) {
            log.info("Delete File failed", e);
        }
    }


    public String getUuidFileName(String fileName) {
        String ext = fileName.substring(fileName.indexOf(".") + 1);
        return UUID.randomUUID().toString() + "." + ext;
    }
}
