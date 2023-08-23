INSERT INTO member (member_id, EMAIL, ALIAS, REGION, PROFILE_IMAGE_URL)
VALUES (1, 'test@email.com', '테스트유저1', '동작구',
        'https://dognejupging-xyz-image-bucket.s3.ap-northeast-2.amazonaws.com/profile_image/profileimage2.png');

INSERT INTO member (member_id, EMAIL, ALIAS, REGION, PROFILE_IMAGE_URL)
VALUES (2, 'test2@email.com', '테스트유저2', '동작구',
        'https://dognejupging-xyz-image-bucket.s3.ap-northeast-2.amazonaws.com/profile_image/profileimage2.png');

INSERT INTO member (member_id, EMAIL, ALIAS, REGION, PROFILE_IMAGE_URL)
VALUES (3, 'test3@email.com', '테스트유저3', '동작구',
        'https://dognejupging-xyz-image-bucket.s3.ap-northeast-2.amazonaws.com/profile_image/profileimage2.png');

INSERT INTO member (member_id, EMAIL, ALIAS, REGION, PROFILE_IMAGE_URL)
VALUES (4, 'test4@email.com', '테스트유저4', '동작구',
        'https://dognejupging-xyz-image-bucket.s3.ap-northeast-2.amazonaws.com/profile_image/profileimage2.png');

INSERT INTO member (member_id, EMAIL, ALIAS, REGION, PROFILE_IMAGE_URL)
VALUES (5, 'test5@email.com', '테스트유저5', '동작구',
        'https://dognejupging-xyz-image-bucket.s3.ap-northeast-2.amazonaws.com/profile_image/profileimage2.png');

INSERT INTO member (member_id, EMAIL, ALIAS, REGION, PROFILE_IMAGE_URL)
VALUES (6, 'test6@email.com', '테스트유저6', '동작구',
        'https://dognejupging-xyz-image-bucket.s3.ap-northeast-2.amazonaws.com/profile_image/profileimage2.png');

INSERT INTO member (member_id, EMAIL, ALIAS, REGION, PROFILE_IMAGE_URL)
VALUES (7, 'test7@email.com', '테스트유저7', '동작구',
        'https://dognejupging-xyz-image-bucket.s3.ap-northeast-2.amazonaws.com/profile_image/profileimage2.png');

INSERT INTO member (member_id, EMAIL, ALIAS, REGION, PROFILE_IMAGE_URL)
VALUES (8, 'test8@email.com', '테스트유저8', '동작구',
        'https://dognejupging-xyz-image-bucket.s3.ap-northeast-2.amazonaws.com/profile_image/profileimage2.png');


-- 게시글
INSERT INTO recruitment_board (RECRUITMENT_BOARD_ID, MEMBER_ID, CREATING_DATE_TIME, STARTING_DATE, ACTIVITY_CATEGORY,
                               TITLE, CONTENT_BODY,
                               REGION, ON_RECRUITMENT, longitude_point, latitude_point, address, count_of_participation,
                               max_count_of_participation, version)
VALUES (1, 1, '2023-3-9-12-0', '2023-3-15', 'RUNNING', '제목', '본문내용', '동작구', true, 1.1, 1.2, '서울시 동작구 상도동 1-1', 1, 8, 1);

INSERT INTO recruitment_board (RECRUITMENT_BOARD_ID, MEMBER_ID, CREATING_DATE_TIME, STARTING_DATE, ACTIVITY_CATEGORY,
                               TITLE, CONTENT_BODY,
                               REGION, ON_RECRUITMENT, longitude_point, latitude_point, address, count_of_participation,
                               max_count_of_participation, version)
VALUES (2, 2, '2023-3-9-12-0', '2023-3-16', 'RUNNING', '제목2', '본문내용2', '동작구', true, 1.3, 1.4, '서울시 동작구 상도동 1-2', 1, 8,
        1);

INSERT INTO certification_board (MEMBER_ID, CREATED_DATE_TIME, modifiedtime, TITLE, CONTENT_BODY)
VALUES (1, '2023-3-9-12-0', '2023-3-9-12-0', '인증제목1', '인증본문1');


-- 댓글
INSERT INTO comment (body, created_date_time, last_modified_date_time, certification_board_id, recruitment_board_id,
                     member_id)
VALUES ('테스트 댓글', '2023-3-10-11-0', '2023-3-10-11-0', null, 1, 1);

INSERT INTO comment (body, created_date_time, last_modified_date_time, certification_board_id, recruitment_board_id,
                     member_id)
VALUES ('테스트 댓글', '2023-3-10-11-0', '2023-3-10-11-0', 1, null, 1);


-- 대댓글
INSERT INTO reply_comment (reply_comment_id, created_date_time, last_modified_date_time, body, comment_id, member_id)
VALUES (1, '2023-3-10-11-0', '2023-3-10-11-0', '테스트 대댓글1', 1, 1);

INSERT INTO reply_comment (reply_comment_id, created_date_time, last_modified_date_time, body, comment_id, member_id)
VALUES (2, '2023-3-10-11-0', '2023-4-10-11-0', '테스트 대댓글2', 1, 1);

-- 참여 2번 게시글에 1번 유저가 참여한 상태
INSERT INTO participation(member_id, recruitment_board_id)
VALUES (1, 1);

INSERT INTO participation(member_id, recruitment_board_id)
VALUES (2, 2);
