CREATE TABLE `knjiga_popisa` (
  `oznaka` int(11) NOT NULL AUTO_INCREMENT,
  `zaduzenje` decimal(20,2) DEFAULT NULL,
  `promet` decimal(20,2) DEFAULT NULL,
  `datum` datetime DEFAULT NULL,
  `dokument` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`oznaka`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;