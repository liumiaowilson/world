CREATE TABLE IF NOT EXISTS `scenarios` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `stimuli` varchar(400) NOT NULL,
  `reaction` varchar(400) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE scenarios ADD INDEX (name);
