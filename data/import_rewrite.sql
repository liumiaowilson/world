CREATE TABLE IF NOT EXISTS `rewrites` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `regex` varchar(20) NOT NULL,
  `from_url` varchar(100) NOT NULL,
  `to_url` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE rewrites ADD INDEX (name);
