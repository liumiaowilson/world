CREATE TABLE IF NOT EXISTS `flashcard_sets` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `description` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE flashcard_sets ADD INDEX (name);

CREATE TABLE IF NOT EXISTS `flashcards` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `set_id` int(11) NOT NULL,
  `top` varchar(100) NOT NULL,
  `bottom` varchar(400) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE flashcards ADD INDEX (name);

INSERT INTO flashcard_sets (name, description) values ('Words', 'English words that you want to learn');
INSERT INTO flashcard_sets (name, description) values ('Signs', 'Signs that you want to learn');
