CREATE TABLE IF NOT EXISTS `documents` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `content` text NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE documents ADD INDEX (name);
