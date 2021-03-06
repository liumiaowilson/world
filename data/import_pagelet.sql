CREATE TABLE IF NOT EXISTS `pagelets` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `title` varchar(100) NOT NULL,
  `target` varchar(200),
  `type` varchar(20),
  `server_code` text,
  `css` text,
  `html` text,
  `client_code` text,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE pagelets ADD INDEX (name);
