CREATE TABLE IF NOT EXISTS `hoppers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `description` varchar(200) NOT NULL,
  `period` int(11) NOT NULL,
  `action` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE hoppers ADD INDEX (name);

CREATE TABLE IF NOT EXISTS `hopper_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `hopper_id` int(11) NOT NULL,
  `status` varchar(20) NOT NULL,
  `fail_count` int(11) NOT NULL,
  `last_time` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;
