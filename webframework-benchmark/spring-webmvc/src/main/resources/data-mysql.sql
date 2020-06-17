DROP TABLE IF EXISTS `world`;;

CREATE TABLE `world` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `random_number` int(11) NOT NULL DEFAULT 0,
  `random_text` varchar(100) NOT NULL DEFAULT '',
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;;

DROP PROCEDURE IF EXISTS `load_data`;;

CREATE PROCEDURE load_data()
BEGIN
  DECLARE v_max INT UNSIGNED DEFAULT 10000;
  DECLARE v_counter INT UNSIGNED DEFAULT 0;
  TRUNCATE TABLE `world`;
  START TRANSACTION;
  WHILE v_counter < v_max DO
    INSERT INTO `world` (`random_number`, `random_text`, `update_time`) VALUES (floor((rand() * 10000)), '哈哈', NOW());
    SET v_counter = v_counter + 1;
  END WHILE;
  COMMIT;
END;;

CALL `load_data`();;
