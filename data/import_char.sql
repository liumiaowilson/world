CREATE TABLE IF NOT EXISTS `statuses` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `icon` varchar(20) NOT NULL,
  `description` varchar(200) NOT NULL,
  `activator` varchar(50) NOT NULL,
  `deactivator` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE statuses ADD INDEX (name);
