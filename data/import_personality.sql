CREATE TABLE IF NOT EXISTS `personalities` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `tags` varchar(400) NOT NULL,
  `description` varchar(400) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE personalities ADD INDEX (name);
