
DROP TABLE IF EXISTS `value`;
CREATE TABLE  `value` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `valueName` varchar(45) NOT NULL,
  `isDelete` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `createTime` datetime not null DEFAULT '0000-00-00 00:00:00',
  `updateTime` datetime not null DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `property`;
CREATE TABLE  `property` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(45) NOT NULL,
  `isDelete` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `createTime` datetime not null DEFAULT '0000-00-00 00:00:00',
  `updateTime` datetime not null DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `productcategory`;
CREATE TABLE  `productcategory` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(45) NOT NULL,
  `description` varchar(100) NULL,
  `parentId` int(10) signed NOT NULL,
  `isDelete` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `createTime` datetime not null DEFAULT '0000-00-00 00:00:00',
  `updateTime` datetime not null DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `navigatecategory`;
CREATE TABLE  `navigatecategory` (
  `id` int(10) signed NOT NULL auto_increment,
  `name` varchar(45) NOT NULL,
  `description` varchar(100) NULL,
  `keyWord` varchar(100) NULL,
  `conditions` varchar(100) NULL,
  `settings` varchar(1000) NULL,
  `priority` int(10) unsigned NOT NULL,
  `parentId` int(10) signed NOT NULL,
  `isDelete` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `createTime` datetime not null DEFAULT '0000-00-00 00:00:00',
  `updateTime` datetime not null DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `CategoryAssociation`;
CREATE TABLE  `CategoryAssociation` (
  `navId` int(10) signed NOT NULL,
  `cid` int(10) signed NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `categoryproperty`;
CREATE TABLE  `categoryproperty` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `propertyType` varchar(45) NOT NULL,
  `categoryId` int(10) unsigned NOT NULL,
  `propertyId` int(10) unsigned NOT NULL,
  `multiValue` tinyint(1) unsigned NOT NULL,
  `compareable` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `priority` int(10) unsigned NOT NULL,
  `isDelete` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `createTime` datetime not null DEFAULT '0000-00-00 00:00:00',
  `updateTime` datetime not null DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `navcategoryproperty`;
CREATE TABLE  `navcategoryproperty` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `navCategoryId` int(10) unsigned NOT NULL,
  `propertyId` int(10) unsigned NOT NULL,
  `priority` int(10) unsigned NOT NULL,
  `isDelete` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `createTime` datetime not null DEFAULT '0000-00-00 00:00:00',
  `updateTime` datetime not null DEFAULT '0000-00-00 00:00:00',
  `searchable` tinyint(1) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `categorypropertyvalue`;
CREATE TABLE  `categorypropertyvalue` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `categoryId` int(10) unsigned NOT NULL,
  `propertyId` int(10) unsigned NOT NULL,
  `valueId` int(10) unsigned NOT NULL,
  `priority` int(10) unsigned NOT NULL DEFAULT 0,
  `isDelete` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `createTime` datetime not null DEFAULT '0000-00-00 00:00:00',
  `updateTime` datetime not null DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `navcategorypropertyvalue`;
CREATE TABLE  `navcategorypropertyvalue` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `navCategoryId` int(10) unsigned NOT NULL,
  `propertyId` int(10) unsigned NOT NULL,
  `valueId` int(10) unsigned NOT NULL,
  `priority` int(10) unsigned NOT NULL DEFAULT 0,
  `isDelete` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `createTime` datetime not null DEFAULT '0000-00-00 00:00:00',
  `updateTime` datetime not null DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `propertyvaluedetail`;
CREATE TABLE  `propertyvaluedetail` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `propertyId` int(10) unsigned NOT NULL,
  `valueId` int(10) unsigned NOT NULL,
  `pictureUrl` varchar(100) NOT NULL,
  `description` varchar(100) NOT NULL,
  `isDelete` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `createTime` datetime not null DEFAULT '0000-00-00 00:00:00',
  `updateTime` datetime not null DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


