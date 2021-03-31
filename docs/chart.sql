-- ----------------------------
-- Table structure for conversation_group
-- ----------------------------
DROP TABLE IF EXISTS `conversation_group`;
CREATE TABLE `conversation_group` (
                                      `id` bigint NOT NULL AUTO_INCREMENT,
                                      `type` int NOT NULL,
                                      `name` varchar(64) COLLATE utf8mb4_zh_0900_as_cs NOT NULL DEFAULT '',
                                      `create_time` bigint NOT NULL,
                                      PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for conversation_group_user
-- ----------------------------
DROP TABLE IF EXISTS `conversation_group_user`;
CREATE TABLE `conversation_group_user` (
                                           `id` bigint NOT NULL AUTO_INCREMENT,
                                           `conversation_group_id` bigint NOT NULL,
                                           `type` int NOT NULL,
                                           `user_id` bigint NOT NULL,
                                           PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `conversation_group_id` bigint NOT NULL,
                           `user_id` bigint NOT NULL,
                           `msg` varchar(255) COLLATE utf8mb4_zh_0900_as_cs NOT NULL,
                           `timestamp` bigint NOT NULL,
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `nick_name` varchar(64) COLLATE utf8mb4_zh_0900_as_cs NOT NULL,
                        `phone_num` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_zh_0900_as_cs NOT NULL,
                        `pass_word` varchar(64) COLLATE utf8mb4_zh_0900_as_cs NOT NULL,
                        `create_time` bigint NOT NULL,
                        `update_time` bigint NOT NULL,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for user_conversation_time
-- ----------------------------
DROP TABLE IF EXISTS `user_conversation_time`;
CREATE TABLE `user_conversation_time` (
                                          `id` bigint NOT NULL AUTO_INCREMENT,
                                          `conversation_group_id` bigint NOT NULL,
                                          `user_id` bigint NOT NULL,
                                          `timestamp` bigint NOT NULL,
                                          PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;