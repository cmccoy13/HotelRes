CREATE TABLE `CreditCards` (
  `CCNum` bigint(20) NOT NULL,
  `FirstName` varchar(20) NOT NULL,
  `LastName` varchar(20) NOT NULL,
  `Balance` float DEFAULT '0',
  PRIMARY KEY (`CCNum`),
  UNIQUE KEY `CCNum` (`CCNum`));

CREATE TABLE `Customers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `FirstName` varchar(20) NOT NULL,
  `LastName` varchar(20) NOT NULL,
  `CC` bigint(20) DEFAULT NULL,
  `Manager` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`),
  KEY `Card` (`CC`),
  CONSTRAINT `Card` FOREIGN KEY (`CC`) REFERENCES `CreditCards` (`CCNum`));

CREATE TABLE `Rooms` (
  `RoomCode` varchar(5) NOT NULL,
  `RoomName` varchar(30) DEFAULT NULL,
  `Beds` int(11) DEFAULT NULL,
  `bedType` varchar(8) DEFAULT NULL,
  `maxOcc` int(11) DEFAULT NULL,
  `basePrice` float DEFAULT NULL,
  `decor` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`RoomCode`),
  UNIQUE KEY `RoomName` (`RoomName`));

CREATE TABLE `Reservations` (
  `Code` int(11) NOT NULL AUTO_INCREMENT,
  `Room` varchar(5) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `CheckIn` date DEFAULT NULL,
  `CheckOut` date DEFAULT NULL,
  `Rate` decimal(5,2) DEFAULT NULL,
  `LastName` varchar(15) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `FirstName` varchar(15) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
  `Adults` int(11) DEFAULT NULL,
  `Kids` int(11) DEFAULT NULL,
  PRIMARY KEY (`Code`));

