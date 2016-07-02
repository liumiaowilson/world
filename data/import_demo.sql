CREATE TABLE IF NOT EXISTS `names` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `first` varchar(50) NOT NULL,
  `last` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

INSERT INTO `names` (`id`, `first`, `last`) VALUES
(1, 'John', 'Doe'),
(2, 'Jane', 'Doe'),
(3, 'John', 'Smith'),
(4, 'Jane', 'Smith');
