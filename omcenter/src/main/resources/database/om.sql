DROP TABLE IF EXISTS `SystemLog`;
CREATE TABLE  `SystemLog` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `operator` varchar(100) NULL  COMMENT '操作人',
  `ip` varchar(20) NOT NULL COMMENT 'ip地址',
  `title` varchar(50) NOT NULL COMMENT '日志主题',
  `roleName` varchar(50) NOT NULL COMMENT '角色名称',
  `content` longtext NULL COMMENT '日志内容',
  `date` datetime not null DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='系统日志';

DROP TABLE IF EXISTS `BugInfo`;
CREATE TABLE `BugInfo` (
  `id` int(11) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `type` varchar(50) DEFAULT NULL  COMMENT '反馈信息内容',
  `content` text  COMMENT '反馈内容',
  `information` varchar(50) DEFAULT NULL  COMMENT '反馈人联系方式',
  `uploadFile` longblob  COMMENT '上传附件',
  `fileType` varchar(100) DEFAULT NULL  COMMENT '文件类型',
  `fileName` varchar(100) DEFAULT NULL  COMMENT '文件名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8 COMMENT='反馈信息，对应Feedback对象';


DROP TABLE IF EXISTS `Questionnaire_Survey`;
CREATE TABLE `Questionnaire_Survey` (
  `id` int(11) NOT NULL auto_increment,
  `surveyName` varchar(100) NOT NULL COMMENT '问卷调查名称',
  `surveyExplain` varchar(1024) NOT NULL COMMENT '问卷调查说明',
  `createTime` datetime default '0000-00-00 00:00:00',
  `updateTime` datetime default '0000-00-00 00:00:00',
  `isDelete` tinyint(1) NOT NULL default '0' COMMENT '是否删除(1 表示已删除, 0 表示未删除), 默认是 0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB auto_increment = 1000000 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='问卷调查表';


DROP TABLE IF EXISTS `Questionnaire_Question`;
CREATE TABLE `Questionnaire_Question` (
  `id` int(11) NOT NULL auto_increment,
  `surveyId` int(11) NOT NULL COMMENT '问卷调查外键',
  `questionDetail` varchar(128) NOT NULL COMMENT '问卷调查详细问题',
  `mustReply` tinyint(1) NOT NULL default '1' COMMENT '是否要求用户必须回答(0.否, 1.是. 默认是1)',
  `answerType` varchar(128) NOT NULL COMMENT '问卷调查的答案类型(单选, 多选)',
  `createTime` datetime default '0000-00-00 00:00:00',
  `updateTime` datetime default '0000-00-00 00:00:00',
  `isDelete` tinyint(1) NOT NULL default '0' COMMENT '是否删除(1 表示已删除, 0 表示未删除), 默认是 0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB auto_increment = 2000000 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='问卷调查问题表';


DROP TABLE IF EXISTS `Questionnaire_Answer`;
CREATE TABLE `Questionnaire_Answer` (
  `id` int(11) NOT NULL auto_increment,
  `questionId` int(11) NOT NULL COMMENT '问题Id',
  `answerDetail` varchar(128) NOT NULL COMMENT '答案内容',
  `hasWrite` tinyint(1) NOT NULL default '0' COMMENT '是否需要用户手动输入(0.否, 1.是. 默认是 0)',
  `createTime` datetime default '0000-00-00 00:00:00',
  `updateTime` datetime default '0000-00-00 00:00:00',
  `isDelete` tinyint(1) NOT NULL default '0' COMMENT '是否删除(1 表示已删除, 0 表示未删除), 默认是 0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB auto_increment = 3000000 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='问卷调查答案表';


DROP TABLE IF EXISTS `Questionnaire_Result`;
CREATE TABLE `Questionnaire_Result` (
  `id` int(11) NOT NULL auto_increment,
  `surveyId` int(11) NOT NULL COMMENT '问卷调查Id',
  `userId` int(11) default NULL COMMENT '问卷提交人(若不登录提交则此值为空)',
  `email` varchar(128) default '' COMMENT '问卷提交人的邮箱',
  `address` varchar(256) default '' COMMENT '问卷提交人的收件地址',
  `userName` varchar(32) default '' COMMENT '问卷提交人的名字(用于收货)',
  `mobile` varchar(20) default '' COMMENT '问卷提交人的电话(用于收货)',
  `suggest` varchar(1024) default NULL COMMENT '提交的建议',
  `createTime` datetime default '0000-00-00 00:00:00',
  `updateTime` datetime default '0000-00-00 00:00:00',
  `isDelete` tinyint(1) default '0' COMMENT '是否删除(1 表示已删除, 0 表示未删除), 默认是 0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB auto_increment = 4000000 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='问卷调查用户提交结果表';


DROP TABLE IF EXISTS `Questionnaire_Result_Detail`;
CREATE TABLE `Questionnaire_Result_Detail` (
  `id` int(11) NOT NULL auto_increment,
  `resultId` int(11) NOT NULL COMMENT '问卷结果Id',
  `questionId` int(11) NOT NULL COMMENT '问题Id',
  `answerId` int(11) NOT NULL COMMENT '答案Id',
  `answerInput` varchar(255) default '' COMMENT '用户输入的回答',
  `createTime` datetime default '0000-00-00 00:00:00',
  `updateTime` datetime default '0000-00-00 00:00:00',
  `isDelete` tinyint(1) NOT NULL default '0' COMMENT '是否删除(1 表示已删除, 0 表示未删除), 默认是 0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB auto_increment = 5000000 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='问卷调查用户提交结果详情表';


DROP TABLE IF EXISTS `seo`;
CREATE TABLE `seo` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `seoObjectId` varchar(45) NOT NULL,
  `title` varchar(120) DEFAULT NULL,
  `description` varchar(240) DEFAULT NULL,
  `keywords` varchar(120) DEFAULT NULL,
  `seoType` varchar(10) NOT NULL,
  `createDate` datetime NOT NULL,
  `updateDate` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `Const`;
CREATE TABLE `Const` (
  `id`  int NOT NULL AUTO_INCREMENT,
  `constKey`  varchar(32) UNIQUE NOT NULL DEFAULT '' COMMENT '全局常量键',
  `constValue`  varchar(64) NOT NULL DEFAULT '' COMMENT '全局常量键值',
  `constComment`  varchar(128) NULL COMMENT '常量说明',
  `createDate`  datetime NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `updateDate`  datetime NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='需要全局设定的值表';
insert into const(constKey, constValue, constComment, createDate, updateDate)
values('integralCount', '100', '积分兑换点数, 若值是 100, 则表示 100 点兑换 1 元', now(), now());
insert into const(constKey, constValue, constComment, createDate, updateDate)
values('couponRemindDay', '5', '优惠券过期提醒时间, 单位天. 若值是 5, 则表示今天提醒 5 天后过期的优惠券数据', now(), now());
insert into const(constKey, constValue, constComment, createDate, updateDate)
values('signInRule', '10, 20, 30, 40, 50, 60, 70', '签到送积分规则, 第一个是第一天送的积分, 第二个是第二天送的, 依此类推... 逗号隔开', now(), now());
insert into const(constKey, constValue, constComment, createDate, updateDate)
values('integralBack', '1', '交易成功后返回给用户的积分比率. 若商品 100 元, 此值设置为 5, 则返给用户 500 点', now(), now());
