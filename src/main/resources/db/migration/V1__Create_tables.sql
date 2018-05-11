CREATE TABLE IF NOT EXISTS `Product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL DEFAULT '',
  `description` varchar(255) ,
  `price` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `Inventory` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `count` int(11) NOT NULL DEFAULT 0,
  `lockedCount` int(11) DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `OrderInfo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL DEFAULT 1,
  `totalPrice` varchar(20) NOT NULL DEFAULT '',
  `status` varchar(25) NOT NULL DEFAULT '',
  `createTime` varchar(25) NOT NULL DEFAULT '',
  `finishTime` varchar(25) DEFAULT NULL,
  `paidTime` varchar(25) DEFAULT NULL,
  `withdrawnTime` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `LogisticsRecord` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `orderId` int(11) NOT NULL ,
  `logisticsStatus` varchar(20) NOT NULL DEFAULT '',
  `outboundTime` varchar(25) NOT NULL DEFAULT 'null',
  `signedTime` varchar(25) NOT NULL DEFAULT 'null',
  `deliveryMan` varchar(30) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS`ProductSnap` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `productId` int(11) NOT NULL ,
  `orderId` int(11) NOT NULL ,
  `productName` varchar(50) NOT NULL DEFAULT '',
  `productDescription` varchar(255) DEFAULT NULL,
  `purchasePrice` int(11) NOT NULL DEFAULT 0,
  `purchaseCount` int(11) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;
