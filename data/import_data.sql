CREATE TABLE IF NOT EXISTS `data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `value` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8 AUTO_INCREMENT=5 ;

ALTER TABLE data ADD INDEX (name);

INSERT INTO `data`(`name`, `value`) values ('user.max_hp', '100');
INSERT INTO `data`(`name`, `value`) values ('user.hp', '100');
INSERT INTO `data`(`name`, `value`) values ('user.max_mp', '100');
INSERT INTO `data`(`name`, `value`) values ('user.mp', '100');
INSERT INTO `data`(`name`, `value`) values ('user.max_stamina', '100');
INSERT INTO `data`(`name`, `value`) values ('user.stamina', '100');

INSERT INTO `data`(`name`, `value`) values ('user.speed', '20');

INSERT INTO `data`(`name`, `value`) values ('user.strength', '20');
INSERT INTO `data`(`name`, `value`) values ('user.construction', '20');
INSERT INTO `data`(`name`, `value`) values ('user.dexterity', '20');
INSERT INTO `data`(`name`, `value`) values ('user.intelligence', '20');
INSERT INTO `data`(`name`, `value`) values ('user.charisma', '20');
INSERT INTO `data`(`name`, `value`) values ('user.willpower', '20');
INSERT INTO `data`(`name`, `value`) values ('user.luck', '20');
