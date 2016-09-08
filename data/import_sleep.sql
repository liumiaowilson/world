CREATE TABLE IF NOT EXISTS `sleeps` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `start_time` bigint(20) NOT NULL,
  `end_time` bigint(20) NOT NULL,
  `quality` int(11) NOT NULL,
  `dreams` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;
