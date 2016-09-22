CREATE TABLE IF NOT EXISTS `reactions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `cond` varchar(200) NOT NULL,
  `result` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE reactions ADD INDEX (name);
