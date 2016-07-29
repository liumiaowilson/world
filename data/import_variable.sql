CREATE TABLE IF NOT EXISTS `variables` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `expression` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE variables ADD INDEX (name);
