INSERT INTO member (EMAIL, ALIAS, REGION, PROFILE_IMAGE_URL)
VALUES ('test@email.com', '테스트유저', '동작구', 'https://dognejupging-xyz-image-bucket.s3.ap-northeast-2.amazonaws.com/profile_image/profileimage2.png');

INSERT INTO recruitment_board (MEMBER_ID, CREATING_DATE_TIME, STARTING_DATE, ACTIVITY_CATEGORY, TITLE, CONTENT_BODY,
                               REGION, ON_RECRUITMENT, longitude_point, latitude_point, address)
VALUES (1, '2023-3-9-12-0', '2023-3-15', 'RUNNING', '제목', '본문내용', '동작구', true, 1.1, 1.2, '서울시 동작구 상도동 1-1');

INSERT INTO recruitment_board (MEMBER_ID, CREATING_DATE_TIME, STARTING_DATE, ACTIVITY_CATEGORY, TITLE, CONTENT_BODY,
                               REGION, ON_RECRUITMENT, longitude_point, latitude_point, address)
VALUES (1, '2023-3-9-12-0', '2023-3-16', 'RUNNING', '제목2', '본문내용2', '동작구', true, 1.3, 1.4, '서울시 동작구 상도동 1-2');

INSERT INTO comment (body, created_date_time, last_modified_date_time, certification_board_id, recruitment_board_id,
                     member_id)
VALUES ('테스트 댓글', '2023-3-10-11-0', '2023-3-10-11-0', null, 1, 1);

--대댓글
INSERT INTO reply_comment (reply_comment_id, created_date_time, last_modified_date_time, body, comment_id, member_id)
VALUES (1, '2023-3-10-11-0', '2023-3-10-11-0', '테스트 대댓글1', 1, 1);

INSERT INTO reply_comment (reply_comment_id, created_date_time, last_modified_date_time, body, comment_id, member_id)
VALUES (2, '2023-3-10-11-0', '2023-4-10-11-0', '테스트 대댓글2', 1, 1);

INSERT INTO participation(member_id, recruitment_board_id)
VALUES (1, 1);

