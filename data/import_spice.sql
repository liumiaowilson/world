CREATE TABLE IF NOT EXISTS `spices` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `prerequisite` varchar(100) NOT NULL,
  `cost` int(11) NOT NULL,
  `content` varchar(400) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE spices ADD INDEX (name);
