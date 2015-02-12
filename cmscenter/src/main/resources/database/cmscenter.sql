DROP TABLE IF EXISTS `Category`;
CREATE TABLE Category (
  `id` int unsigned NOT NULL auto_increment,
  `name` varchar(200) NOT NULL,
  `directory` varchar(200) NULL COMMENT '目录名，用来生成文件夹',
  `parent` int NOT NULL,
  `priority` int NOT NULL DEFAULT 0 COMMENT '类目优先级',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='栏目';


DROP TABLE IF EXISTS `Content`;
CREATE TABLE Content (
  `id` int unsigned NOT NULL auto_increment,
  `title` varchar(200) NOT NULL COMMENT '主题',
  `url` varchar(200) NULL COMMENT 'URL路径',
  `content` text NOT NULL COMMENT '内容',
  `categoryId` int NOT NULL COMMENT '类目Id',
  `templateId` int NOT NULL DEFAULT 0 COMMENT '模块Id',
  `priority` int NOT NULL DEFAULT 0 COMMENT '优先级，小优先',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='内容';

DROP TABLE IF EXISTS `RenderTemplate`;
CREATE TABLE RenderTemplate (
  `id` int unsigned NOT NULL auto_increment,
  `name` varchar(200) NOT NULL COMMENT '模块名称',
  `templateType` varchar(10) NOT NULL COMMENT '模块类型',
  `templateContent` text NOT NULL COMMENT '模块内容',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='栏目模块';


