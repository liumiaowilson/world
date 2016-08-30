CREATE TABLE IF NOT EXISTS `algorithm_problems` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `description` varchar(400) NOT NULL,
  `interface` text NOT NULL,
  `dataset` text NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE algorithm_problems ADD INDEX (name);

CREATE TABLE IF NOT EXISTS `algorithms` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `problem_id` int(11) NOT NULL,
  `description` varchar(400) NOT NULL,
  `impl` text NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE algorithms ADD INDEX (name);
