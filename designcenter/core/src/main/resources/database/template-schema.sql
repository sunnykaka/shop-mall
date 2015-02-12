DROP TABLE IF EXISTS `template`;
CREATE TABLE  `template` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(45) NOT NULL,
  `description` varchar(100) NULL,
  `templateType` varchar(45) NOT NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `commonmodule`;
CREATE TABLE `commonmodule` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `caption` varchar(64) COMMENT '模块说明',
  `moduleCssContent` text NULL,
  `moduleContent` text NULL,
  `editModuleContent` text NULL,
  `formContent` text NULL,
  `editFormContent` text NULL,
  `logicCode` text NULL,
  `editLogicCode` text NULL,
  `moduleJs` text NULL,
  `editModuleJs` text NULL,
  `name` varchar(45) NOT NULL,
  `moduleGranularity` varchar(45) NOT NULL default 'DEFAULT',
  `language` varchar(45) NULL,
  `version` varchar(45) NULL,
  `config` varchar(45) NULL,
  `moduleConfig` text NULL,
  `moduleConfigKey` varchar(45) NULL,
  `isDelete` tinyint default 0,
  PRIMARY KEY  (`id`),
  UNIQUE KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `templatemodule`;
CREATE TABLE `templatemodule` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `templateVersionId` int(10) unsigned NOT NULL,
  `moduleCssContent` text NULL,
  `moduleContent` text NULL,
  `editModuleContent` text NULL,
  `name` varchar(45) NOT NULL,
  `language` varchar(45) NULL,
  `version` varchar(45) NULL,
  `config` varchar(45) NULL,
  `moduleConfig` text NULL,
  `moduleConfigKey` varchar(45) NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `templatepage`;
CREATE TABLE  `templatepage` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `pageContent` text NULL,
  `pageName` varchar(45) NOT NULL,
  `templateVersionId` int(10) unsigned NOT NULL,
  `configContent` text NULL,
  `configKey` varchar(45) NULL,
  `pageType` varchar(45) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `templateresource`;
CREATE TABLE `templateresource` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `templateVersionId` int(10) unsigned NOT NULL,
  `name` varchar(150) NOT NULL,
  `description` varchar(45) NULL,
  `content` longtext NULL,
  `byteData` MEDIUMBLOB NULL,
  `resourceKey` varchar(45) NULL,
  `resourceType` varchar(45) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `templatestyle`;
CREATE TABLE  `templatestyle` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(45) NOT NULL,
  `description` varchar(45) NULL,
  `templateVersionId` int(10) unsigned NOT NULL,
  `styleResourceId` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`id`)
  )ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `templateversion`;
CREATE TABLE  `templateversion` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `templateId` int(10) unsigned NOT NULL,
  `headContent` text NULL,
  `footContent` text NULL,
  `headConfigContent` text NULL,
  `footConfigContent` text NULL,
  `state` varchar(45) NOT NULL,
  `version` varchar(45) NOT NULL,
  `globalCssId` int(10) unsigned NULL,
  `globalJsId` int(10) unsigned NULL,
  `defaultStyleId` int(10) unsigned NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `PagePrototype`;
CREATE TABLE  `PagePrototype` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `name` varchar(45) NOT NULL,
  `description` varchar(100) NULL,
  `areaType` varchar(45) NOT NULL,
  `prototypeState` varchar(45) NOT NULL,
  `pageCode` text NOT NULL,
  `configContent` text NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `shoptemplate`;
CREATE TABLE  `shoptemplate` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `shopId` int(10) unsigned NOT NULL,
  `templateVersionId` int(10) unsigned NOT NULL,
  `editHeadConfigContent` text NULL,
  `editFootConfigContent` text NULL,
  `editHeadContent` text NULL,
  `editFootContent` text NULL,
  `editGlobalCss` text NULL,
  `editGlobalJs` text NULL,
  `editStyle` text NULL,
  `prodHeadConfigContent` text NULL,
  `prodFootConfigContent` text NULL,
  `prodHeadContent` text NULL,
  `prodFootContent` text NULL,
  `prodGlobalCss` text NULL,
  `prodGlobalJs` text NULL,
  `prodStyle` text NULL,
  `globalModuleInfoConfig` text NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `shoppage`;
CREATE TABLE  `shoppage` (
  `id` bigint(20) unsigned NOT NULL auto_increment,
  `name` varchar(45) NOT NULL,
  `title` varchar(100) NULL,
  `keywords` varchar(150) NULL,
  `description` varchar(200) NULL,
  `shopId` int(10) unsigned NOT NULL,
  `editConfigContent` LONGTEXT NULL,
  `prodConfigContent` LONGTEXT NULL,
  `shopPageType` varchar(45) NOT NULL,
  `pageType` varchar(45) NOT NULL,
  `pageStatus` varchar(45) NOT NULL,
  `editPageContent` text NULL,
  `prodPageContent` text NULL,
  `jsContent` text NULL,
  `cssContent` text NULL,
  `shopTemplateId` int(10) unsigned NOT NULL,
  `config` varchar(100) NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `moduleinstanceparam`;
CREATE TABLE  `moduleinstanceparam` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `moduleInstanceId` varchar(100) NOT NULL,
  `modulePrototypeId` int(10) unsigned NOT NULL,
  `pageId` int(10) unsigned NULL,
  `shopId` int(10) unsigned NOT NULL,
  `paramName` varchar(100) NOT NULL,
  `paramValue` longtext NULL,
   paramType varchar(10),
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
