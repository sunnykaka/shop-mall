DROP TABLE IF EXISTS `Role`;
CREATE TABLE  `Role` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `roleName` varchar(100) NOT NULL,
  `functionSet` varchar(1000) NULL COMMENT '功能集合',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色';

INSERT INTO `role` (`id`,`roleName`) VALUES
 (1,'超级管理员');



DROP TABLE IF EXISTS `Account_Role`;
CREATE TABLE  `Account_Role` (
  `userId` int(10) unsigned NOT NULL,
  `roleId` int(10) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户和角色关联表';

INSERT INTO `account_role` (`userId`,`roleId`) VALUES
 (1,1);

DROP TABLE IF EXISTS `Role_Permission`;
CREATE TABLE  `Role_Permission` (
  `roleId` int(10) unsigned NOT NULL,
  `permissionId` int(10) unsigned NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色与权限关联表';

INSERT INTO `role_permission` (`roleId`,`permissionId`) VALUES
 (1,1),
 (1,2),
 (1,3);


DROP TABLE IF EXISTS `UrlPermission`;
CREATE TABLE  `UrlPermission` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `path` varchar(100) NOT NULL,
  `resource` varchar(100) NOT NULL,
  `permissionName` varchar(100) NOT NULL,
  `category` varchar(1000) NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='url权限表';

INSERT INTO `urlpermission` (`id`,`path`,`resource`,`permissionName`) VALUES
 (1,'/account/*','Account','用户管理'),
 (2,'/role/*','Role','角色管理'),
 (3,'/permission/*','Permission','权限管理');


DROP TABLE IF EXISTS `RoleScope`;
CREATE TABLE  `RoleScope` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `roleId` int(10) unsigned NOT NULL,
  `resource` varchar(100) NOT NULL,
  `resourceAuthScript` text NOT NULL,
  `uiAuthScript` text NOT NULL,
  `scopeValue` varchar(100) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;