drop table if exists `Customer`;
create table `Customer` (
  `id` int unsigned not null auto_increment,
  `name` varchar(255) COMMENT '商家名称',
  `defaultLogistics` varchar(50) not null DEFAULT 'ems' COMMENT '商家默认物流',
  primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商家';


drop table if exists `ProductStorage`;
create table `ProductStorage` (
  `id` int unsigned not null auto_increment,
  `name` varchar(255) COMMENT '商品仓库名称',
  `consignor` varchar(225) COMMENT '仓库发货人',
  `telephone` varChar(20) COMMENT '仓库发货人电话',
  `address`  varchar(500) COMMENT '发货地址',
  `remarks` varchar(250) COMMENT '商品仓库备注',
  `company` varchar(225) COMMENT '商品仓库所属公司',
  `customerId` int COMMENT '商品仓库关联的商家Id',
  primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品仓库';


drop table if exists `Brand`;
create table `Brand` (
  `id` int unsigned not null auto_increment,
  `name` varchar(255) COMMENT '品牌名称',
  `picture` varchar(255) NULL COMMENT '品牌 logo 图',
  `desc` varchar(512) NULL COMMENT '品牌文字描述',
  `story` text COMMENT '品牌故事',
  `customerId` int unsigned not null COMMENT '关联的商家Id',
  primary key (id,customerId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='品牌';


drop table if exists `CustomerAccount`;
create table `CustomerAccount` (
  `id` int unsigned not null auto_increment,
  `accountName` varchar(255) COMMENT '商家账号名称',
  `password` varchar(100) COMMENT '商家账号密码',
  `email` varchar(100) COMMENT '商家账号email',
  `isNormal` tinyint(4) DEFAULT 0 COMMENT '是否激活',
  `customerId` int COMMENT '关联商家Id',
  `isMainAccount` tinyint(4) DEFAULT 0 COMMENT '是否为主账号',
  primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商家账号';


drop table if exists `LogisticsPrintInfo`;
create table `LogisticsPrintInfo` (
  `id` int unsigned not null auto_increment,
  `name` varchar(150) not null COMMENT '物流名称',
  `law` int not null COMMENT '物流单号递增规律',
  `printHtml` TEXT COMMENT '物流信息打印的lodop代码',
  `logisticsPicturePath` varchar(500) COMMENT '物流图片路径',
  `customerId` int COMMENT '关联商家Id',
  primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='物流打印信息';


drop table if exists `SupplierLog`;
CREATE TABLE  `SupplierLog` (
  `id` int unsigned NOT NULL auto_increment,
  `supplierId` int not null COMMENT '商家Id',
  `operator` varchar(100) NULL COMMENT '日志操作人',
  `ip` varchar(20) NOT NULL COMMENT 'ip地址',
  `title` varchar(50) NOT NULL COMMENT '日志标题',
  `content` text NULL COMMENT '日志内容',
  `date` datetime not null DEFAULT '0000-00-00 00:00:00' COMMENT '日志创建时间',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商家操作日志';


DROP TABLE IF EXISTS `Role`;
CREATE TABLE  `Role` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `roleName` varchar(100) NOT NULL COMMENT '角色名称',
  `functionSet` varchar(1000) NULL COMMENT '功能集合',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商家角色';


DROP TABLE IF EXISTS `Account_Role`;
CREATE TABLE  `Account_Role` (
  `userId` int(10) unsigned NOT NULL COMMENT '商家账号Id',
  `roleId` int(10) unsigned NOT NULL COMMENT '角色Id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商家账号和角色关联表，该表的命名不规范，是为了与权限系统兼容';


DROP TABLE IF EXISTS `Role_Permission`;
CREATE TABLE  `Role_Permission` (
  `roleId` int(10) unsigned NOT NULL COMMENT '角色Id',
  `permissionId` int(10) unsigned NOT NULL COMMENT '权限Id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色与权限关联表';


DROP TABLE IF EXISTS `UrlPermission`;
CREATE TABLE  `UrlPermission` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `path` varchar(100) NOT NULL COMMENT '权限路径',
  `resource` varchar(100) NOT NULL COMMENT '资源名称',
  `permissionName` varchar(100) NOT NULL COMMENT '权限名称',
  `category` varchar(1000) NULL COMMENT '权限类别',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='权限表';


INSERT INTO `urlpermission` (`path`,`resource`,`permissionName`,`category`) VALUES
 ('/supplier/permission/*','权限','权限管理','用户中心'),
 ('/supplier/role/*','角色','角色管理','用户中心'),
 ('/supplier/supplierAccount/*','用户','用户管理','用户中心'),
 ('/supplier/orderPrint/*','订单中心','订单打印','订单'),
 ('/supplier/orderList/*','订单列表操作','订单列表','订单'),
 ('/supplier/logisticsPrintInfo/*','物流','物流单设计','物流信息'),
 ('/supplier/orderInspection/*','验货','订单验货','订单'),
 ('/supplier/orderDetail/*','订单','订单明细','订单'),
 ('/supplier/queryStockKeepingUnit','库存','查看库存','订单'),
 ('/supplier/productReportPage','订单','报表','订单'),
 ('/supplier/supplierLog/*','日志','用户操作日志','用户中心');


DROP TABLE IF EXISTS `RoleScope`;
CREATE TABLE  `RoleScope` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `roleId` int(10) unsigned NOT NULL COMMENT '角色Id',
  `resource` varchar(100) NOT NULL COMMENT '资源',
  `resourceAuthScript` text NOT NULL COMMENT '资源权限授权脚本',
  `uiAuthScript` text NOT NULL COMMENT 'UI权限授权脚本',
  `scopeValue` varchar(100) NOT NULL COMMENT '操作范围或者域',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色操作范围';


DROP TABLE IF EXISTS `Role_Supplier`;
CREATE TABLE  `Role_Supplier` (
  `roleId` int(10) unsigned NOT NULL COMMENT '角色Id',
  `supplierId` int(10) unsigned NOT NULL COMMENT '商家Id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色商家关联表';



