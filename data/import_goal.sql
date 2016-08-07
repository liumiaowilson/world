CREATE TABLE IF NOT EXISTS `goal_defs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `description` varchar(200) NOT NULL,
  `steps` int(11) NOT NULL,
  `start_time` bigint(20) NOT NULL,
  `start_amount` int(11) NOT NULL,
  `end_time` bigint(20) NOT NULL,
  `end_amount` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE goal_defs ADD INDEX (name);

CREATE TABLE IF NOT EXISTS `goals` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `def_id` int(11) NOT NULL,
  `time` bigint(20) NOT NULL,
  `amount` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;
