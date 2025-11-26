CREATE TABLE `users` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT 'userId, PK',
  `email` varchar(255) NOT NULL COMMENT '로그인 용 이메일',
  `password` varchar(255) NOT NULL COMMENT '암호화된 비밀번호',
  `name` varchar(100) COMMENT '사용자 이름',
  `status` varchar(255) NOT NULL DEFAULT 'ACTIVE' COMMENT '계정 상태 (ACTIVE, DEACTIVE)',
  `createdAt` datetime NOT NULL,
  `updatedAt` datetime NOT NULL
);

CREATE TABLE `concerts` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT 'concertId, PK',
  `title` varchar(255) NOT NULL COMMENT '콘서트 이름',
  `description` text COMMENT '콘서트 설명',
  `createdAt` datetime NOT NULL,
  `updatedAt` datetime NOT NULL
);

CREATE TABLE `concerts_schedules` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT 'scheduleId, PK',
  `concertId` bigint NOT NULL COMMENT 'FK → concerts.id',
  `startAt` datetime NOT NULL COMMENT '공연 시작 시각',
  `isReservable` boolean NOT NULL DEFAULT true COMMENT '예약 가능 여부',
  `createdAt` datetime NOT NULL,
  `updatedAt` datetime NOT NULL
);

CREATE TABLE `concerts_schedules_seats` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT 'seatId, PK',
  `scheduleId` bigint NOT NULL COMMENT 'FK → concerts_schedules.id',
  `seatNumber` varchar(20) NOT NULL COMMENT '좌석 번호 (예 : 10)',
  `seatPrice` int NOT NULL COMMENT '좌석 가격',
  `seatStatus` seat_status NOT NULL COMMENT '좌석 상태',
  `createdAt` datetime NOT NULL,
  `updatedAt` datetime NOT NULL
);

CREATE TABLE `queue_tokens` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `token` varchar(100) NOT NULL COMMENT '대기열 토큰 문자열',
  `userId` bigint NOT NULL COMMENT 'FK → users.id',
  `scheduleId` bigint NOT NULL COMMENT 'FK → concerts_schedules.id',
  `tokenStatus` token_status NOT NULL COMMENT '토큰 상태',
  `position` int COMMENT '현재 대기 순번',
  `waitingTime` int COMMENT '예상 대기 시간 (초)',
  `activatedAt` datetime COMMENT 'ACTIVE 된 시각',
  `expiredAt` datetime COMMENT 'EXPIRED 된 시각',
  `createdAt` datetime NOT NULL,
  `updatedAt` datetime NOT NULL
);

CREATE TABLE `seats_reservations` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT 'reservationId, PK',
  `userId` bigint NOT NULL COMMENT 'FK → users.id',
  `scheduleId` bigint NOT NULL COMMENT 'FK → concerts_schedules.id',
  `seatId` bigint NOT NULL COMMENT 'FK → concerts_schedules_seats.id',
  `reservationStatus` reservation_status NOT NULL COMMENT '현재 예약 상태',
  `reservationExpiresAt` datetime COMMENT '임시 배정 만료 시각 (TEMPORARY일 때)',
  `createdAt` datetime NOT NULL,
  `updatedAt` datetime NOT NULL,
  `isActive` tinyint COMMENT '좌석 임시 배정 (예약) 시 동시성 이슈 등을 처리하기 위함 (null, 1)'
);

CREATE TABLE `seats_reservations_histories` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `reservationId` bigint NOT NULL COMMENT 'FK → seats_reservations.id',
  `status` reservation_status NOT NULL COMMENT '이 시점의 예약 상태',
  `expiresAt` datetime COMMENT '이 시점 기준 만료 시각',
  `description` varchar(255) COMMENT '상태 변경 사유/메모',
  `createdAt` datetime NOT NULL COMMENT '이력 생성 시각'
);

CREATE TABLE `users_points` (
  `userId` bigint PRIMARY KEY COMMENT 'FK → users.id',
  `point` int NOT NULL COMMENT '현재 포인트 잔액',
  `updatedAt` datetime NOT NULL
);

CREATE TABLE `users_points_histories` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `userId` bigint NOT NULL COMMENT 'FK → users.id',
  `type` point_history_type NOT NULL COMMENT '포인트 이력 타입 (현재 CHARGE / USE 사용, REFUND는 향후 확장)',
  `amount` int NOT NULL COMMENT '증감량 (양수, type으로 의미 구분)',
  `afterPoint` int NOT NULL COMMENT '이 트랜잭션 이후 잔액',
  `name` varchar(255) COMMENT '이력 이름 (예 : 카드 충전, 콘서트 결제)',
  `description` text COMMENT '상세 내역',
  `paymentId` bigint COMMENT 'FK → seats_payments.id (USE 시 연결)',
  `createdAt` datetime NOT NULL COMMENT '이력 생성 시각'
);

CREATE TABLE `seats_payments` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT 'paymentId, PK',
  `reservationId` bigint NOT NULL COMMENT 'FK → seats_reservations.id',
  `userId` bigint NOT NULL COMMENT 'FK → users.id',
  `amount` int NOT NULL COMMENT '결제 금액',
  `status` payment_status NOT NULL COMMENT '현재 결제 상태',
  `paidAt` datetime COMMENT 'SUCCESS 시 결제 완료 시각',
  `createdAt` datetime NOT NULL COMMENT '결제 요청 시각',
  `updatedAt` datetime NOT NULL COMMENT '마지막 상태 변경 시각'
);

CREATE TABLE `seats_payments_histories` (
  `id` bigint PRIMARY KEY AUTO_INCREMENT,
  `paymentId` bigint NOT NULL COMMENT 'FK → seats_payments.id',
  `paymentStatus` payment_status NOT NULL COMMENT '이 시점의 결제 상태',
  `description` varchar(255) COMMENT '실패/취소 사유 등',
  `createdAt` datetime NOT NULL COMMENT '이력 생성 시각'
);

CREATE UNIQUE INDEX `uk_users_email` ON `users` (`email`);

CREATE INDEX `idx_concerts_concertId` ON `concerts_schedules` (`concertId`);

CREATE INDEX `idx_concerts_schedules_scheduleId` ON `concerts_schedules_seats` (`scheduleId`);

CREATE UNIQUE INDEX `uk_concerts_schedules_seats_scheduleId_seatNumber` ON `concerts_schedules_seats` (`scheduleId`, `seatNumber`);

CREATE UNIQUE INDEX `uk_queue_tokens_token` ON `queue_tokens` (`token`);

CREATE INDEX `idx_users_concerts_schedules_userId_scheduleId` ON `queue_tokens` (`userId`, `scheduleId`);

CREATE INDEX `idx_users_userId` ON `seats_reservations` (`userId`);

CREATE INDEX `idx_concerts_schedules_scheduleId` ON `seats_reservations` (`scheduleId`);

CREATE UNIQUE INDEX `uk_seats_reservations_scheduleId_seatId_isActive` ON `seats_reservations` (`scheduleId`, `seatId`, `isActive`);

CREATE INDEX `idx_seats_reservations_reservationId` ON `seats_reservations_histories` (`reservationId`);

CREATE INDEX `idx_users_userId` ON `users_points_histories` (`userId`);

CREATE INDEX `idx_seats_payments_paymentId` ON `users_points_histories` (`paymentId`);

CREATE UNIQUE INDEX `uk_seats_payments_reservationId` ON `seats_payments` (`reservationId`);

CREATE INDEX `idx_users_userId` ON `seats_payments` (`userId`);

CREATE INDEX `idx_seats_payments_paymentId` ON `seats_payments_histories` (`paymentId`);

ALTER TABLE `concerts_schedules` ADD FOREIGN KEY (`concertId`) REFERENCES `concerts` (`id`);

ALTER TABLE `concerts_schedules_seats` ADD FOREIGN KEY (`scheduleId`) REFERENCES `concerts_schedules` (`id`);

ALTER TABLE `queue_tokens` ADD FOREIGN KEY (`userId`) REFERENCES `users` (`id`);

ALTER TABLE `queue_tokens` ADD FOREIGN KEY (`scheduleId`) REFERENCES `concerts_schedules` (`id`);

ALTER TABLE `seats_reservations` ADD FOREIGN KEY (`userId`) REFERENCES `users` (`id`);

ALTER TABLE `seats_reservations` ADD FOREIGN KEY (`scheduleId`) REFERENCES `concerts_schedules` (`id`);

ALTER TABLE `seats_reservations` ADD FOREIGN KEY (`seatId`) REFERENCES `concerts_schedules_seats` (`id`);

ALTER TABLE `seats_reservations_histories` ADD FOREIGN KEY (`reservationId`) REFERENCES `seats_reservations` (`id`);

ALTER TABLE `users_points` ADD FOREIGN KEY (`userId`) REFERENCES `users` (`id`);

ALTER TABLE `users_points_histories` ADD FOREIGN KEY (`userId`) REFERENCES `users` (`id`);

ALTER TABLE `users_points_histories` ADD FOREIGN KEY (`paymentId`) REFERENCES `seats_payments` (`id`);

ALTER TABLE `seats_payments` ADD FOREIGN KEY (`reservationId`) REFERENCES `seats_reservations` (`id`);

ALTER TABLE `seats_payments` ADD FOREIGN KEY (`userId`) REFERENCES `users` (`id`);

ALTER TABLE `seats_payments_histories` ADD FOREIGN KEY (`paymentId`) REFERENCES `seats_payments` (`id`);
