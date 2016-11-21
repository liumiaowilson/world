CREATE TABLE IF NOT EXISTS `novel_variables` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `description` varchar(200) NOT NULL,
  `default_value` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE novel_variables ADD INDEX (name);

CREATE TABLE IF NOT EXISTS `novel_roles` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `description` text NOT NULL,
  `definition` text NOT NULL,
  `image` varchar(100),
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE novel_roles ADD INDEX (name);

CREATE TABLE IF NOT EXISTS `novel_stages` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `description` varchar(200) NOT NULL,
  `prev_id` int(11),
  `status` varchar(20) NOT NULL,
  `image` varchar(100),
  `cond` text,
  `pre_code` text,
  `post_code` text,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE novel_stages ADD INDEX (name);

CREATE TABLE IF NOT EXISTS `novel_fragments` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `stage_id` int(11) NOT NULL,
  `cond` text,
  `content` text NOT NULL,
  `pre_code` text,
  `post_code` text,
  `image` varchar(100),
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE novel_fragments ADD INDEX (name);

CREATE TABLE IF NOT EXISTS `novel_tickets` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `doc_id` varchar(100) NOT NULL,
  `name` varchar(20) NOT NULL,
  `description` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE novel_tickets ADD INDEX (name);

CREATE TABLE IF NOT EXISTS `novel_stats` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `doc_id` varchar(100) NOT NULL,
  `time` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;
