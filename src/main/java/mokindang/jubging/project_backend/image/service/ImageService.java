package mokindang.jubging.project_backend.image.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mokindang.jubging.project_backend.certification_board.domain.CertificationBoard;
import mokindang.jubging.project_backend.file.FileResponse;
import mokindang.jubging.project_backend.file.FileService;
import mokindang.jubging.project_backend.image.domain.Image;
import mokindang.jubging.project_backend.image.repository.ImageRepository;
import mokindang.jubging.project_backend.image.service.request.ImageDeleteRequest;
import mokindang.jubging.project_backend.image.service.request.ImageRequest;
import mokindang.jubging.project_backend.image.service.response.ImageUrlResponse;
import mokindang.jubging.project_backend.member.domain.Member;
import mokindang.jubging.project_backend.member.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static mokindang.jubging.project_backend.file.FileService.CERTIFICATION_BOARD_IMAGE;
import static mokindang.jubging.project_backend.file.FileService.PROFILE_IMAGE;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ImageService {

    private final String defaultImageUrl = "https://dognejupging-xyz-image-bucket.s3.ap-northeast-2.amazonaws.com/profile_image/default_profile.png";
    private final ImageRepository imageRepository;
    private final FileService fileService;
    private final MemberService memberService;

    public List<String> findImagesUrl(final CertificationBoard board) {
        List<String> imagesUrl = new ArrayList<>();
        List<Image> images = imageRepository.findByCertificationBoard(board)
                .orElseThrow(() -> new IllegalArgumentException("인증 게시판 id로 저장된 이미지가 존재하지 않습니다."));
        setImagesUrl(imagesUrl, images);
        return imagesUrl;
    }

    private void setImagesUrl(List<String> imagesUrl, List<Image> images) {
        for (Image image : images) {
            imagesUrl.add(image.getFilePath());
        }
    }

    public ImageUrlResponse uploadProfileImage(Long memberId, ImageRequest imageRequest) {
        Member member = memberService.findByMemberId(memberId);
        FileResponse fileResponse = fileService.uploadFile(imageRequest.getImage(), PROFILE_IMAGE);
        log.info("memberId = {}, alias = {} 의 프로필 이미지 {} 업로드", member.getId(), member.getAlias(), fileResponse.getUploadFileName());
        return new ImageUrlResponse(fileResponse.getUploadFileUrl());
    }

    public ImageUrlResponse uploadCertificationImage(Long memberId, ImageRequest imageRequest) {
        Member member = memberService.findByMemberId(memberId);
        FileResponse fileResponse = fileService.uploadFile(imageRequest.getImage(), CERTIFICATION_BOARD_IMAGE);
        log.info("memberId = {}, alias = {} 의 인증게시글 이미지 {} 업로드", member.getId(), member.getAlias(), fileResponse.getUploadFileName());
        return new ImageUrlResponse(fileResponse.getUploadFileUrl());
    }

    public ImageUrlResponse deleteProfileImage(ImageDeleteRequest imageDeleteRequest) {
        String imageUrl = imageDeleteRequest.getImageUrl();
        if (imageUrl.equals(defaultImageUrl)) {
            return new ImageUrlResponse(defaultImageUrl);
        }
        fileService.deleteFile(imageUrl, PROFILE_IMAGE);
        return new ImageUrlResponse(defaultImageUrl);
    }

    public void deleteCertificationImage(ImageDeleteRequest imageDeleteRequest) {
        String imageUrl = imageDeleteRequest.getImageUrl();
        fileService.deleteFile(imageUrl, CERTIFICATION_BOARD_IMAGE);
    }
}
