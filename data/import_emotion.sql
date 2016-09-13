CREATE TABLE IF NOT EXISTS `emotions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(200) NOT NULL,
  `ecstacy` int(11) NOT NULL,
  `grief` int(11) NOT NULL,
  `admiration` int(11) NOT NULL,
  `loathing` int(11) NOT NULL,
  `rage` int(11) NOT NULL,
  `terror` int(11) NOT NULL,
  `vigilance` int(11) NOT NULL,
  `amazement` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE emotions ADD INDEX (name);
