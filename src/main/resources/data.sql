INSERT INTO member (EMAIL, ALIAS, REGION, PROFILE_IMAGE_URL, PROFILE_IMAGE_NAME)
VALUES ('test@email.com', '테스트유저', '동작구', 'DEFAULT_PROFILE_IMAGE_URL', 'DEFAULT_PROFILE_IMAGE_NAME');
INSERT INTO recruitment_board (MEMBER_ID, CREATING_DATE_TIME, STARTING_DATE, ACTIVITY_CATEGORY, TITLE, CONTENT_BODY, REGION, ON_RECRUITMENT)
VALUES (1,'2023-3-9-12-0', '2023-3-15', 'RUNNING', '제목', '본문내용', '동작구', false);
INSERT INTO comment (body, created_date_time, last_modified_date_time, certification_board_id, recruitment_board_id, member_id)
VALUES ('테스트 댓글', '2023-3-10-11-0', '2023-3-10-11-0', null, 1, 1);
INSERT INTO reply_comment (reply_comment_id, created_date_time, last_modified_date_time, body, comment_id, member_id)
VALUES (1, '2023-3-10-11-0', '2023-3-10-11-0', '테스트 대댓글', 1, 1);
