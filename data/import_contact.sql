CREATE TABLE IF NOT EXISTS `contacts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `content` varchar(200) NOT NULL,
  `created_time` bigint(20) NOT NULL,
  `modified_time` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE contacts ADD INDEX (name);

CREATE TABLE IF NOT EXISTS `contact_attrs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `contact_id` int(11) NOT NULL,
  `name` varchar(20) NOT NULL,
  `value` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE contact_attrs ADD INDEX (contact_id);
ALTER TABLE contact_attrs ADD INDEX (name);

CREATE TABLE IF NOT EXISTS `contact_attr_defs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `type` varchar(20) NOT NULL,
  `description` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE contact_attr_defs ADD INDEX (name);
