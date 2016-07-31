CREATE TABLE IF NOT EXISTS `skill_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `scope` varchar(20) NOT NULL,
  `target` varchar(20) NOT NULL,
  `cost` int(11) NOT NULL,
  `cooldown` int(11) NOT NULL,
  `can_trigger` varchar(50) NOT NULL,
  `trigger_impl` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE skill_data ADD INDEX (name);

CREATE TABLE IF NOT EXISTS `user_skills` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `skill_id` int(11) NOT NULL,
  `level` int(11) NOT NULL,
  `exp` int(11) NOT NULL,
  `last_time` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;
