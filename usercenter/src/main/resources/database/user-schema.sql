DROP TABLE IF EXISTS `user`;
CREATE TABLE  `user` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `userName` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `email`    varchar(100) default '',
  `phone` varchar(32) default '' COMMENT '手机号',
  `isActive` tinyint(4) NOT NULL default 0,
  `registerDate` varchar(24) Not Null,
  `registerIP` varchar(24) Not Null,
  `hasForbidden` tinyint(4) NOT NULL default 0,
  `loginTime` datetime not null DEFAULT '0000-00-00 00:00:00',
  `loginCount` tinyint(6) NOT NULL default 0 COMMENT '登录次数',
  `isDelete` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `accountType` varchar(16) Not Null DEFAULT 'Anonymous',
  `grade` varchar(45) NOT NULL DEFAULT 'A' COMMENT '等级标识',
  `expenseTotal` bigint(20) NOT NULL DEFAULT 0 COMMENT '消费累计金额',
  `pointTotal` bigint(20) NOT NULL DEFAULT 0 COMMENT '总积分',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `userdata`;
CREATE TABLE `userdata` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `userId` int(10) unsigned NOT NULL,
  `name` varchar(40) DEFAULT NULL,
  `sex` tinyint(1) unsigned DEFAULT '0' COMMENT '0,1,2',
  `birthday` varchar(80) DEFAULT NULL,
  `phoneNumber` varchar(80) DEFAULT NULL,
  `familyNumber` tinyint(1) unsigned DEFAULT '0' COMMENT '0,1,2,3',
  `hasMarried` tinyint(1) unsigned DEFAULT '0' COMMENT '0,1,2',
  `isDelete` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `createDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  UNIQUE KEY (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `usersession`;
CREATE TABLE  `usersession` (
  `userId` int(10) NOT NULL,
  `cookieValue` varchar(100) NOT NULL,
  `checkInDate` varchar(100) NOT NULL,
  PRIMARY KEY  (`userId`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `statisticsentry`;
CREATE TABLE `statisticsentry` (
  `entryId` bigint(20) NOT NULL auto_increment COMMENT '主键(记录登录)',
  `userName` varchar(100) NOT NULL COMMENT '用户名',
  `entryTime` datetime not null DEFAULT '0000-00-00 00:00:00' COMMENT '登录时间',
  `entryIP` varchar(16) NOT NULL COMMENT '登录的 IP 地址',
  `isDelete` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除(1 表示已删除, 0 表示未删除), 默认是 0',
  PRIMARY KEY  (`entryId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='当前表主要用来记录用户登录时的时间和登录地址. 方便后期做活跃用户分析';


DROP TABLE IF EXISTS `userouter`;
CREATE TABLE `userouter` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `outerId` varchar(60) NOT NULL,
  `accountType` varchar(16) NOT NULL DEFAULT 'Anonymous',
  `userId` int(10) unsigned NOT NULL DEFAULT '0',
  `isLocked` tinyint(2) unsigned NOT NULL DEFAULT '0',
  `isDelete` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `createDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `messagetask`;
CREATE TABLE `messagetask` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `contact` varchar(45) DEFAULT NULL COMMENT '联系方式',
  `content` varchar(1204) DEFAULT NULL COMMENT '发送内容',
  `sendWay` varchar(45) DEFAULT NULL COMMENT '发送方式：\\nemail：邮件\\nsms:短信\\n',
  `createDate` datetime DEFAULT NULL,
  `updateDate` datetime DEFAULT NULL,
  `isDelete` tinyint(4) unsigned NOT NULL DEFAULT '0',
  `sendCount` int(11) DEFAULT NULL COMMENT '发送次数',
  `sendSuccess` tinyint(4) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;




DROP TABLE IF EXISTS `userpoint`;
CREATE TABLE `userpoint` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` bigint(20) DEFAULT NULL COMMENT '用户id',
  `point` bigint(20) DEFAULT NULL COMMENT '分数',
  `inoutcomingType` varchar(45) DEFAULT NULL COMMENT '获取或者支出积分的类型',
  `createTime` datetime DEFAULT NULL,
  `updateTime` datetime DEFAULT NULL,
  `type` varchar(45) DEFAULT NULL COMMENT 'income-收入     consume-支出',
  `description` varchar(45) DEFAULT NULL COMMENT '对积分使用或者支出的描述',
  `isDelete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `usergraderule`;
CREATE TABLE `usergraderule` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(10) DEFAULT NULL COMMENT '等级名称',
  `grade` varchar(45) DEFAULT NULL COMMENT '等级标识',
  `totalExpense` bigint(20) DEFAULT NULL COMMENT '累计消费金额',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  `isDelete` tinyint(1) NOT NULL DEFAULT '0',
  `onceExpense` bigint(20) DEFAULT NULL COMMENT '单笔订单金额',
  `valuationRatio` double DEFAULT NULL COMMENT ' 当前级别评价系数',
  `gradePic` varchar(100) DEFAULT NULL COMMENT '等级图标地址',
  `gradeDescription` text DEFAULT NULL COMMENT '等级描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `usergradehistory`;
CREATE TABLE `usergradehistory` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) DEFAULT NULL,
  `grade` varchar(45) DEFAULT NULL,
  `createTime` datetime DEFAULT NULL,
  `updateTime` datetime DEFAULT NULL,
  `isDelete` tinyint(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `UnSubscribe`;
CREATE TABLE `UnSubscribe` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(100) NOT NULL COMMENT '邮箱',
  `createTime` datetime DEFAULT '0000-00-00 00:00:00',
  `updateTime` datetime DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='取消订阅的邮件列表';


DROP TABLE IF EXISTS `SmsMould`;
CREATE TABLE `SmsMould` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` varchar(1024) NOT NULL COMMENT '短信模板',
  `description` varchar(200) NOT NULL COMMENT '短信模板描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='短信模板列表';


DROP TABLE IF EXISTS `SmsCharacter`;
CREATE TABLE `SmsCharacter` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `value` varchar(50) NOT NULL COMMENT '短信字符值',
  `name` varchar(100) NOT NULL COMMENT '短信字符内容',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='短信字符列表';
INSERT INTO `smscharacter` (`id`,`value`,`name`) VALUES (1,'mobile','电话'), (2,'sendTime','发送时间');


DROP TABLE IF EXISTS `userSignIn`;
CREATE TABLE `userSignIn` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL UNIQUE COMMENT '用户Id',
  `signInCount` int(11) NOT NULL COMMENT '连续签到次数',
  `createDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户签到记录';

