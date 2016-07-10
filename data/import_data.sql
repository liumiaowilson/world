CREATE TABLE IF NOT EXISTS `data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `value` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE data ADD INDEX (name);

INSERT INTO `data`(`name`, `value`) values ('user.max_hp', '100');
INSERT INTO `data`(`name`, `value`) values ('user.hp', '100');
