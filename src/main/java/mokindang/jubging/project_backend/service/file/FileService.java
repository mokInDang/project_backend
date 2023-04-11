package mokindang.jubging.project_backend.service.file;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.domain.member.Member;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static mokindang.jubging.project_backend.domain.member.vo.ProfileImage.DEFAULT_PROFILE_IMAGE_NAME;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final String PROFILE_IMAGE = "profile_image";
    private final AmazonS3Client amazonS3Client;

    public FileResponse uploadFile(MultipartFile multipartFile, Member member) {
        if(multipartFile.isEmpty()){
            return new FileResponse(member.getProfileImage().getProfileImageUrl(), member.getProfileImage().getProfileImageName());
        }

        String uploadFilePath = PROFILE_IMAGE;
        String originalFileName = multipartFile.getOriginalFilename();
        String uploadFileName = getUuidFileName(originalFileName);
        String uploadFileUrl = "";

        ObjectMetadata objectMetadata = getObjectMetadata(multipartFile);

        try (InputStream inputStream = multipartFile.getInputStream()) {
            String keyName = uploadFilePath + "/" + uploadFileName;
            amazonS3Client.putObject(
                    new PutObjectRequest(bucket, keyName, inputStream, objectMetadata));
            uploadFileUrl = amazonS3Client.getUrl(bucket, keyName).toString();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Filed upload failed", e);
        }

        log.info("memberId = {}, alias = {} 의 프로필 이미지 {} 업로드", member.getId(), member.getAlias(), uploadFileName);
        return new FileResponse(uploadFileUrl, uploadFileName);
    }

    private static ObjectMetadata getObjectMetadata(MultipartFile multipartFile) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());
        return objectMetadata;
    }

    public void deleteFile(Member member) {
        String uploadFileName = member.getProfileImage()
                .getProfileImageName();
        String uploadFilePath = PROFILE_IMAGE;

        if (checkNameDefault(uploadFileName)) return;

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
        log.info("memberId = {}, alias = {} 의 이전 프로필 이미지 {} 삭제", member.getId(), member.getAlias(), member.getProfileImage().getProfileImageName());
    }

    private static boolean checkNameDefault(String uploadFileName) {
        if(uploadFileName.equals(DEFAULT_PROFILE_IMAGE_NAME)){
            log.info("기본이미지로 삭제되지 않았습니다.");
            return true;
        }
        return false;
    }

    public String getUuidFileName(String fileName) {
        String ext = fileName.substring(fileName.indexOf(".") + 1);
        return UUID.randomUUID().toString() + "." + ext;
    }
}
