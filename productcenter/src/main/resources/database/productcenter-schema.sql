DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `productCode` varchar(200) default '',
  `categoryId` int(10) NOT NULL,
  `customerId` int(10) NOT NULL,
  `brandId` int(10) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` varchar(100) NOT NULL COMMENT '推荐理由',
  `storeStrategy` varchar(16) NOT NULL DEFAULT 'NormalStrategy' COMMENT 'NormalStrategy.普通策略, 创建即扣减库存, 取消则回加库存; PayStrategy.付款策略, 付款成功后才会扣减' ,
  `createTime`  datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `onlineTime`  datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `offlineTime`  datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `onLineTimeLong` bigint NOT NULL DEFAULT 0,
  `updateTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `isDelete` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除(0表示未删除, 1表示已删除)',
  `online` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否上架(0表示未上架, 1表示上架)',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `ProductPicture`;
CREATE TABLE  `ProductPicture` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `productId` int(10) signed NOT NULL,
  `originalName` varchar(100) NOT NULL,
  `name` varchar(100) NOT NULL,
  `mainPic` tinyint(1) NULL DEFAULT 0 COMMENT '主图(0 代表不是主图, 1 代表是, 默认是 0)',
  `minorPic` tinyint(1) NULL DEFAULT 0 COMMENT '副图(0 代表不是主图, 1 代表是, 默认是 0)',
  `pictureUrl` varchar(100) NOT NULL,
  `pictureLocalUrl` varchar(100) NOT NULL,
  `skuId` bigint DEFAULT 0,
  `number` tinyint(20) DEFAULT 0,
   PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `ProductProperty`;
CREATE TABLE  `ProductProperty` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `productId` int(10) signed NOT NULL,
  `json` varchar(1000) NOT NULL,
  `propertyType` varchar(45) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `Html`;
CREATE TABLE  `Html` (
  `productId` int(10) signed NOT NULL,
  `name` varchar(100) NOT NULL,
  `content` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `RecommendProduct`;
CREATE TABLE  `RecommendProduct` (
  `productId` int(10) signed NOT NULL,
  `recommendProductId` int(10) signed NOT NULL,
  `recommendType` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `StockKeepingUnit`;
CREATE TABLE  `StockKeepingUnit` (
  `id` BIGINT unsigned NOT NULL auto_increment,
  `productId` int(10) signed NOT NULL,
  `price` BIGINT signed NOT NULL,
  `marketPrice` BIGINT signed NOT NULL DEFAULT  0,
  `skuPropertiesInDb` varchar(1000) NULL,
  `skuState` varchar(100) NOT NULL,
  `barCode` varchar(120) NOT NULL  DEFAULT '未填',
  `skuCode` varchar(120),
  `createTime` timestamp NOT NULL,
  `updateTime` timestamp NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `SkuStorage`;
CREATE TABLE  `SkuStorage` (
  `skuId` int(10) signed NOT NULL,
  `stockQuantity` int(10) unsigned NOT NULL  DEFAULT 0,
  `tradeMaxNumber` int(10)  NOT NULL,
  `productStorageId` int(10) signed NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `SpacePicture`;
CREATE TABLE  `SpacePicture` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `spaceId` int(10)  NOT NULL,
  `pictureName` varchar(50) NOT NULL,
  `originalName` varchar(50) NOT NULL,
  `pictureUrl` varchar(100) NOT NULL,
  `pictureLocalUrl` varchar(100) NOT NULL,
  primary key(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `SpaceProperty`;
CREATE TABLE  `SpaceProperty` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `spaceName` varchar(100)  NOT NULL,
  primary key(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

insert into SpaceProperty(spaceName) values ('默认');

DROP TABLE IF EXISTS `AttentionInfo`;
CREATE TABLE  `AttentionInfo` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `productId` int(10) signed NOT NULL,
  `type` varchar(15) NOT NULL,
  `info` varchar(100) NOT NULL,
  primary key(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 商品收藏表
DROP TABLE IF EXISTS `productCollect`;
CREATE TABLE `productCollect` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `userId` varchar(200) NOT NULL,
  `productId` int(10) unsigned NOT NULL,
  `collectTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `productName` VARCHAR(80) NOT NULL DEFAULT '已下架',
  `productMainPicture` VARCHAR(120) NOT NULL DEFAULT 'http://img07.yijushang.com/images/none_222.jpg',
  `unitPrice` BIGINT(20) NOT NULL DEFAULT 0,
  `isDelete` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `isCancel` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `createDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `consultation`;
CREATE TABLE `consultation` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `productId` int(10) NOT NULL,
  `askedUserName` varchar(100) NOT NULL,
  `askUserId` varchar(50) NOT NULL,
  `askContent` text NOT NULL,
  `answerContent` text ,
  `consultationCategory` varchar(30) NOT NULL,
  `hasAnswer` tinyint(1) default 0,
  `askTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `answerTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `isDelete` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `createDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `productmarketing`;
CREATE TABLE  `productmarketing` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `productId` int(11) NOT NULL,
  `marketingId` int(11) NOT NULL,
  `discount` double DEFAULT NULL,
  `marketingPrice` int(11) DEFAULT NULL,
  `economizePrice` int(11) DEFAULT NULL,
  `lastDay` tinyint(1) NOT NULL DEFAULT '0',
  `createDate` datetime DEFAULT NULL,
  `updateDate` datetime DEFAULT NULL,
  `isDelete` tinyint(4) NOT NULL DEFAULT '0',
  `marketingPictureUrl` varchar(150) DEFAULT NULL,
  `priority` int(10) unsigned NOT NULL DEFAULT 0,
  `productName` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `marketing`;
CREATE TABLE  `marketing` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) DEFAULT NULL,
  `serialNum` int(11) DEFAULT NULL,
  `marketingState` varchar(45) NOT NULL,
  `startDate` datetime DEFAULT NULL,
  `createDate` datetime DEFAULT NULL,
  `updateDate` datetime DEFAULT NULL,
  `endDate` datetime DEFAULT NULL,
  `isDelete` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `skumarketing`;
CREATE TABLE  `skumarketing` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `skuId` bigint DEFAULT NULL,
  `marketingId` int(11) NOT NULL,
  `skuMarketingState` varchar(45) NOT NULL,
  `skuPrice` bigint DEFAULT NULL,
  `marketingPrice` bigint DEFAULT NULL,
  `productMarketingId` int(11) DEFAULT NULL,
  `createDate` datetime DEFAULT NULL,
  `updateDate` varchar(45) DEFAULT NULL,
  `isDelete` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `subscriptioninfo`;
CREATE TABLE  `subscriptioninfo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `createDate` datetime DEFAULT NULL,
  `updateDate` datetime DEFAULT NULL,
  `isDelete` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `limitedTimeDiscount`;
CREATE TABLE `limitedTimeDiscount` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `productId` int(11) NOT NULL COMMENT '商品Id(限时折扣是基于商品而不是 SKU 的)',
  `skuDetailsJson` varchar(1024) NOT NULL COMMENT '商品下的所有 SKU 对应的 id 及价格. 以 json 数据进行存储',
  `discountType` varchar(255) NOT NULL COMMENT '折扣类型(比例或具体的金额)',
  `discount` bigint(20) NOT NULL COMMENT '折扣(如果折扣类型是比例则此值是100的倍数, 85折表示为85; 若是固定金额则此值单位为分)',
  `beginDate` datetime NOT NULL COMMENT '限时折扣开始时间',
  `endDate` datetime NOT NULL COMMENT '限时折扣结束时间',
  `createDate` datetime DEFAULT NULL COMMENT '创建时间',
  `updateDate` datetime DEFAULT NULL COMMENT '修改时间',
  `isDelete` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除(1已删除, 0未删除. 默认是 0)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='限时折扣';


DROP TABLE IF EXISTS `ProductActivity`;
CREATE TABLE  `ProductActivity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `productId` int(11) DEFAULT NULL COMMENT '商品ID',
  `activityId` int(11) DEFAULT NULL COMMENT '活动ID',
  `activityType` varchar(45) DEFAULT NULL COMMENT '活动类型',
  `activityPrice` varchar(45) DEFAULT NULL COMMENT '活动价格',
  `startDate` datetime DEFAULT NULL COMMENT '活动开始时间',
  `endDate` datetime DEFAULT NULL COMMENT '活动结束时间',
  `isDelete` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `MealSet`;
CREATE TABLE  `MealSet` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL COMMENT '套餐名字',
  `price` text NULL COMMENT '套餐价格',
  `recommendReason` text NULL COMMENT '推荐理由',
  `createDate` datetime DEFAULT NULL COMMENT '创建时间',
  `updateDate` datetime DEFAULT NULL COMMENT '修改时间',
  `isDelete` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `MealItem`;
CREATE TABLE  `MealItem` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mealSetId` int(11) NOT NULL COMMENT '套餐ID',
  `skuId` bigint NOT NULL,
  `skuPrice` bigint NOT NULL COMMENT '加入套餐的价格',
  `number` int NOT NULL COMMENT '加入套餐的数量',
  `productId` int NOT NULL,
  `isDelete` int(11) NOT NULL DEFAULT '0',
  `createDate` datetime DEFAULT NULL COMMENT '创建时间',
  `updateDate` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `Weibo`;
CREATE TABLE  `Weibo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `outerId` bigint NOT NULL COMMENT '发布到微博平台后得到的ID',
  `content` varchar(500)  NOT NULL COMMENT '微博内容',
  `configContent` varchar(500)  NOT NULL COMMENT '活动配置',
  /* `picUrl` varchar(100)  NULL COMMENT '微薄配图', */
  `reposts` int(11) NOT NULL COMMENT '转发次数',
  `weiboImage` MediumBlob NULL COMMENT '微博图片(二进制数据)',
  `weiboImageType` varchar(32) NULL COMMENT '微博图片类型(二进制数据)',
  `type` varchar(10) NOT NULL,
  `publish` int(11)  NOT NULL DEFAULT '0',
  `isDelete` int(11) NOT NULL DEFAULT '0',
  `startDate` datetime DEFAULT NULL COMMENT '开始时间',
  `endDate` datetime DEFAULT NULL COMMENT '结束时间',
  `createDate` datetime DEFAULT NULL COMMENT '创建时间',
  `updateDate` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `WeiboBuy`;
CREATE TABLE  `WeiboBuy` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `weiboId` int(11) NOT NULL COMMENT '微博Id',
  `productId` int(11) NOT NULL COMMENT '商品Id',
  `isDelete` int(11) NOT NULL DEFAULT '0',
  `createDate` datetime DEFAULT NULL COMMENT '创建时间',
  `updateDate` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `WeiboToken`;
CREATE TABLE  `WeiboToken` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `token` varchar(500)  NOT NULL,
  `type` varchar(10) NOT NULL,
  `createDate` datetime DEFAULT NULL COMMENT '创建时间',
  `expireDate` datetime DEFAULT NULL COMMENT '过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `browsinghistory`;
CREATE TABLE  `browsinghistory` (
  `id` bigint not null auto_increment ,
  `userId` int(11) NOT NULL DEFAULT '0',
  `productId` int(11) NOT NULL,
  `trackId` varchar(100),
  `createDate` datetime DEFAULT NULL COMMENT '创建时间',
  `deleteDate` datetime DEFAULT NULL COMMENT '删除时间',
  `isDelete` tinyint NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/* 人造的微博订单号 */
DROP TABLE IF EXISTS `HumenOrder`;
CREATE TABLE `HumenOrder` (
  `id`  int NOT NULL AUTO_INCREMENT ,
  `weiboId`  int NOT NULL COMMENT '微博Id' ,
  `userName`  varchar(255) NOT NULL COMMENT '用户名' ,
  `tradeOrderNo`  varchar(255) NOT NULL COMMENT '交易号' ,
  `tradeDate`  datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '交易时间' ,
  PRIMARY KEY (`id`),
  unique(`weiboId`, `userName`) /* 微博Id 与 用户名 联合索引 */
) COMMENT='人造的微博订单号' ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `productofplatform`;
CREATE TABLE `productofplatform` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `productId` int(10) unsigned NOT NULL,
  `platform` varchar(45) NOT NULL,
  `productOfJson` text,
  `isDelete` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `createDate` datetime NOT NULL,
  `updateDate` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS `productAuction`;
CREATE TABLE `productAuction` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `productId` int(11) NOT NULL COMMENT '商品id',
  `startCount` int(11) NOT NULL COMMENT '竞拍起始点数',
  `endCount` int(11) NOT NULL COMMENT '竞拍结束点数',
  `userAuctionCount` int(11) NOT NULL COMMENT '用户竞拍次数',
  `startDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '竞拍开始时间',
  `endDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '竞拍结束时间',
  `createDate` datetime DEFAULT '0000-00-00 00:00:00',
  `updateDate` datetime DEFAULT '0000-00-00 00:00:00',
  `isDelete` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品竞拍表';


DROP TABLE IF EXISTS `productIntegralConversion`;
CREATE TABLE `productIntegralConversion` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `productId` int(11) NOT NULL COMMENT '商品id',
  `skuId` int(11) NOT NULL COMMENT '商品的sku的Id',
  `integralCount` int(11) NOT NULL COMMENT '积分点数',
  `userBuyCount` int(11) NOT NULL COMMENT '用户购买次数',
  `startDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '积分兑换开始时间',
  `endDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '积分兑换结束时间',
  `createDate` datetime DEFAULT '0000-00-00 00:00:00',
  `updateDate` datetime DEFAULT '0000-00-00 00:00:00',
  `isDelete` tinyint(1) NOT NULL DEFAULT '0',
  `mockSale` int(11) DEFAULT '0' COMMENT '假的销售',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='商品积分兑换表';


DROP TABLE IF EXISTS `productSuperConversion`;
CREATE TABLE `productSuperConversion` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `productId` int(11) NOT NULL COMMENT '商品id',
  `skuId` int(11) NOT NULL COMMENT '商品的sku的Id',
  `integralCount` int(11) NOT NULL COMMENT '积分点数',
  `money` bigint(20) NOT NULL COMMENT '购买金额',
  `userBuyCount` int(11) NOT NULL COMMENT '用户购买次数',
  `startDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '超值兑换开始时间',
  `endDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '超值兑换结束时间',
  `createDate` datetime DEFAULT '0000-00-00 00:00:00',
  `updateDate` datetime DEFAULT '0000-00-00 00:00:00',
  `isDelete` tinyint(1) NOT NULL DEFAULT '0',
  `mockSale` int(11) DEFAULT '0' COMMENT '假的销售',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='商品超值兑换表';


DROP TABLE IF EXISTS `userAuction`;
CREATE TABLE `userAuction` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userName` varchar(100) NOT NULL COMMENT '用户名' ,
  `auctionId` int(11) NOT NULL COMMENT '商品竞拍id',
  `productId` int(11) NOT NULL COMMENT '商品id',
  `auctionCount` int(11) NOT NULL COMMENT '用户竞拍的点数',
  `createDate` datetime DEFAULT '0000-00-00 00:00:00',
  `updateDate` datetime DEFAULT '0000-00-00 00:00:00',
  `isDelete` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户竞拍表';
