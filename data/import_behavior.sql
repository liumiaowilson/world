CREATE TABLE IF NOT EXISTS `behavior_defs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `description` varchar(200) NOT NULL,
  `karma` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE behavior_defs ADD INDEX (name);

CREATE TABLE IF NOT EXISTS `behavior` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `def_id` int(11) NOT NULL,
  `time` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;
