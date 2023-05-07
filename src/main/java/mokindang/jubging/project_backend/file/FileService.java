package mokindang.jubging.project_backend.file;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.certification_board.domain.CertificationBoard;
import mokindang.jubging.project_backend.image.domain.Image;
import mokindang.jubging.project_backend.image.repository.ImageRepository;
import mokindang.jubging.project_backend.member.domain.Member;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public static final String PROFILE_IMAGE = "profile_image";
    public static final String CERTIFICATION_BOARD_IMAGE = "certificationBoard_image";
    private final AmazonS3Client amazonS3Client;
    private final ImageRepository imageRepository;

    public FileResponse uploadFile(MultipartFile multipartFile) {

        String uploadFilePath = PROFILE_IMAGE;
        String originalFileName = multipartFile.getOriginalFilename();
        String uploadFileName = getUuidFileName(originalFileName);
        String uploadFileUrl = "";

        ObjectMetadata objectMetadata = getObjectMetadata(multipartFile);
        uploadFileUrl = uploadToS3(multipartFile, uploadFilePath, uploadFileName, uploadFileUrl, objectMetadata);

        return new FileResponse(uploadFileUrl, uploadFileName);
    }

    public void uploadFiles(List<MultipartFile> multipartFiles, Member member, CertificationBoard certificationBoard) {

        String uploadFilePath = CERTIFICATION_BOARD_IMAGE;

        for (MultipartFile multipartFile : multipartFiles) {
            String originalFileName = multipartFile.getOriginalFilename();
            String uploadFileName = getUuidFileName(originalFileName);
            String uploadFileUrl = "";

            ObjectMetadata objectMetadata = getObjectMetadata(multipartFile);
            uploadFileUrl = uploadToS3(multipartFile, uploadFilePath, uploadFileName, uploadFileUrl, objectMetadata);

            log.info("memberId = {}, alias = {} 의 boardId = {} 인증 게시판 이미지 {} 업로드", member.getId(), member.getAlias(), certificationBoard.getId(), uploadFileName);
            imageRepository.save(new Image(certificationBoard, uploadFileName, uploadFileUrl));
        }
    }

    private String uploadToS3(MultipartFile multipartFile, String uploadFilePath, String uploadFileName, String uploadFileUrl, ObjectMetadata objectMetadata) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            String keyName = uploadFilePath + "/" + uploadFileName;
            amazonS3Client.putObject(new PutObjectRequest(bucket, keyName, inputStream, objectMetadata));
            uploadFileUrl = amazonS3Client.getUrl(bucket, keyName).toString();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("S3로 파일 업로드가 실패했습니다.", e);
        }
        return uploadFileUrl;
    }

    private static ObjectMetadata getObjectMetadata(MultipartFile multipartFile) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());
        return objectMetadata;
    }

    public void deleteFile(String fileUrl, String filePath) {
        String uploadFileName = getFileNameFromURL(fileUrl);
        String uploadFilePath = filePath;

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

    private String getFileNameFromURL(String url) {
        return url.substring(url.lastIndexOf('/') + 1, url.length());
    }

    public String getUuidFileName(String fileName) {
        String ext = fileName.substring(fileName.indexOf(".") + 1);
        return UUID.randomUUID().toString() + "." + ext;
    }
}
