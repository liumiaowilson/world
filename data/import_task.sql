CREATE TABLE IF NOT EXISTS `tasks` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `content` varchar(200) NOT NULL,
  `created_time` bigint(20) NOT NULL,
  `modified_time` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE tasks ADD INDEX (name);

CREATE TABLE IF NOT EXISTS `task_attrs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_id` int(11) NOT NULL,
  `name` varchar(20) NOT NULL,
  `value` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE task_attrs ADD INDEX (task_id);
ALTER TABLE task_attrs ADD INDEX (name);

CREATE TABLE IF NOT EXISTS `task_attr_defs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `type` varchar(20) NOT NULL,
  `description` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE task_attr_defs ADD INDEX (name);

CREATE TABLE IF NOT EXISTS `task_attr_rules` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `priority` int(11) NOT NULL,
  `policy` varchar(20) NOT NULL,
  `impl` varchar(50),
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE task_attr_rules ADD INDEX (name);

INSERT INTO task_attr_rules(name, priority, policy) values ('Priority', 7, 'reversed');
INSERT INTO task_attr_rules(name, priority, policy) values ('Urgency', 6, 'reversed');
INSERT INTO task_attr_rules(name, priority, policy) values ('Impact', 5, 'reversed');
INSERT INTO task_attr_rules(name, priority, policy) values ('Fun', 4, 'reversed');
INSERT INTO task_attr_rules(name, priority, policy) values ('Difficulty', 3, 'normal');
INSERT INTO task_attr_rules(name, priority, policy) values ('DueAt', 2, 'normal');
INSERT INTO task_attr_rules(name, priority, policy) values ('Effort', 1, 'normal');

CREATE TABLE IF NOT EXISTS `contexts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `color` varchar(20) NOT NULL,
  `description` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE contexts ADD INDEX (name);

INSERT INTO contexts(name, color, description) values ('Work', 'orange', 'Work');
INSERT INTO contexts(name, color, description) values ('Leisure', 'limegreen', 'Leisure');

CREATE TABLE IF NOT EXISTS `task_seeds` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `pattern` varchar(50) NOT NULL,
  `spawner` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE task_seeds ADD INDEX (name);

CREATE TABLE IF NOT EXISTS `task_followers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `symbol` varchar(20) NOT NULL,
  `impl` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE task_followers ADD INDEX (name);

CREATE TABLE IF NOT EXISTS `task_tags` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `task_id` int(11) NOT NULL,
  `tags` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;
