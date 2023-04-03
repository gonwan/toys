DROP TABLE IF EXISTS `t_user`;
DROP TABLE IF EXISTS `t_user2`;
DROP TABLE IF EXISTS `t_user3`;

CREATE TABLE `t_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `create_time` datetime(6) NOT NULL,
  `update_time` timestamp(6) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `t_user2` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `t_user3` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `t_user`(`id`, `username`, `password`, `version`, `create_time`, `update_time`) VALUES (1, 'uaaa', 'paaa', 1, '2020-01-01', '2020-01-01');
INSERT INTO `t_user2`(`id`, `username`, `password`) VALUES (1, 'uaaa2', 'paaa2');
INSERT INTO `t_user3`(`id`, `username`, `password`) VALUES (1, 'uaaa3', 'paaa3');
