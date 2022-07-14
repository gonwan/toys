DROP TABLE IF EXISTS `t_user`;

CREATE TABLE `t_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `t_user`(`id`, `username`, `password`) VALUES
(1, 'Nick', 'Fury'),
(2, 'Phil', 'Coulson'),
(3, 'Clint', 'Barton'),
(4, 'Maria', 'Hill'),
(5, 'Melinda', 'May'),
(6, 'Daisy', 'Johnson'),
(7, 'Grant', 'Ward'),
(8, 'Leopold', 'Fitz'),
(9, 'Jemma', 'Simmons')
;
