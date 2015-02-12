DROP TABLE IF EXISTS `Cart`;
CREATE TABLE  `Cart` (
  `id` BIGINT unsigned NOT NULL auto_increment,
  `userId` varchar(100) NOT NULL,
  `createDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `trackId` varchar(100) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `CartItem`;
CREATE TABLE  `CartItem` (
  `cartId` BIGINT unsigned NOT NULL,
  `skuId` BIGINT signed NOT NULL,
  `number` int(10) signed NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `SkuTradeResult`;
CREATE TABLE  `SkuTradeResult` (
  `id` BIGINT unsigned NOT NULL auto_increment,
  `skuId` bigint NOT NULL unique,
  `productId` int NOT NULL,
  `number` bigint COMMENT '销售数量',
  `backNumber` bigint COMMENT '退货数量',
  `payNumber` bigint comment '付款成功数量',
  PRIMARY KEY(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `orderTable`;
CREATE TABLE `orderTable` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `orderNo` bigint(20) NOT NULL,
  `userId` varchar(100) NOT NULL,
  `userName` varchar(100) NOT NULL,
  `accountType` varchar(100) NOT NULL,
  `payType` varchar(100) NOT NULL,
  `payBank` varchar(100) DEFAULT NULL,
  `totalPrice` varchar(20) NOT NULL,
  `invoice` varchar(20) NOT NULL,
  `invoiceType` varchar(20) DEFAULT NULL,
  `invoiceTitle` varchar(20) DEFAULT NULL,
  `invoiceContent` varchar(20) DEFAULT NULL,
  `companyName` varchar(100) NOT NULL,
  `orderState` varchar(20) NOT NULL,
  `createDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `modifyDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `cancelDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `endDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `payDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `milliDate` bigint(20) NOT NULL,
  `valid` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `virtualOrderId` bigint(20) NOT NULL,
  `storageId` int(11) NOT NULL,
  `customerId` int(11) NOT NULL,
  `isDelete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除(1 表示已删除, 0 表示未删除), 默认是 0',
  `supplierName` varchar(100) DEFAULT '' COMMENT '商品家(快照冗余)',
  `storageName` varchar(255) DEFAULT '' COMMENT '仓库名(快照冗余)',
  `invoiceTypeRewrite` varchar(20) DEFAULT NULL,
  `invoiceTitleRewrite` varchar(20) DEFAULT NULL,
  `invoiceContentRewrite` varchar(20) DEFAULT NULL,
  `companyNameRewrite` varchar(100) DEFAULT NULL,
  `editor` varchar(20) DEFAULT NULL,
  `timeRewrite` datetime DEFAULT NULL,
  `deliveryType` varchar(20) default 'shentong' COMMENT '订单物流公司(顺风, 中通等), 与订单物流表中冗余',
  `couponId`  int NULL COMMENT '现金券Id',
  `priceMessage` varchar(64) NULL COMMENT '使用现金券或积分时记录其订单价格相关说明',
  `priceMessageDetail` varchar(256) NULL COMMENT '使用现金券或积分时记录其订单价格相关详细说明',
  `integralPercent` varchar(256) NULL COMMENT '使用现金券或积分时记录其订单价格相关详细说明',
  PRIMARY KEY (`id`),
  UNIQUE KEY `OrderTable` (`orderNo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `VirtualOrder`;
CREATE TABLE  `VirtualOrder` (
  `id` BIGINT unsigned NOT NULL auto_increment,
  `userId` varchar(100) NOT NULL,
  `accountType` varchar(100) NOT NULL,
  `virtualState` varchar(20) NOT NULL,
  `payBank` varchar(20) NOT NULL,
  `totalPrice` bigint NOT NULL,
  `payPrice` bigint NOT NULL,
  `createDate`  datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `cancelDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `modifyDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `endDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `isDelete` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除(1 表示已删除, 0 表示未删除), 默认是 0',
   PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `logistics`;
CREATE TABLE `logistics` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `orderId` bigint(20) NOT NULL,
  `addressId` int(11) NOT NULL,
  `deliveryType` varchar(20) NOT NULL,
  `deliveryTime` varchar(20) NOT NULL,
  `waybillNumber` varchar(24) DEFAULT NULL,
  `cost` varchar(20) NOT NULL,
  `addressOwner` varchar(100) NOT NULL,
  `name` varchar(20) NOT NULL,
  `province` varchar(200) NOT NULL,
  `location` varchar(200) NOT NULL,
  `mobile` varchar(20) NOT NULL,
  `telephone` varchar(20) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `zipCode` varchar(10) NOT NULL,
  `editor` varchar(16) DEFAULT NULL,
  `nameRewrite` varchar(20) DEFAULT NULL,
  `provinceRewrite` varchar(80) DEFAULT NULL,
  `locationRewrite` varchar(200) DEFAULT NULL,
  `mobileRewrite` varchar(20) DEFAULT NULL,
  `zipCodeRewrite` varchar(10) DEFAULT NULL,
  `timeRewrite` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `orderItem`;
CREATE TABLE `orderItem` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `orderId` bigint(20) unsigned NOT NULL,
  `skuId` bigint(20) NOT NULL,
  `unitPrice` bigint(20) unsigned NOT NULL DEFAULT '0' COMMENT '下单时的商品价格',
  `itemTotalPrice` bigint(20) UNSIGNED NOT NULL DEFAULT 0 COMMENT '订单项总额(理论上=单价*数量, 若有使用积分或现金券, 则分摊至单个订单项)',
  `orderState` varchar(20) NOT NULL COMMENT '订单中的商品对应的状态, 默认 等待付款',
  `storeStrategy` varchar(16) NOT NULL DEFAULT 'NormalStrategy' COMMENT 'NormalStrategy.普通策略, 创建即扣减库存, 取消则回加库存; PayStrategy.付款策略, 付款成功后才会扣减',
  `number` int(10) unsigned NOT NULL COMMENT '购买时的 sku 数量',
  `skuExplain` varchar(80) NOT NULL DEFAULT ' ',
  `skuMainPicture` varchar(120) NOT NULL DEFAULT 'http://img07.yijushang.com/images/none_222.jpg',
  `skuName` varchar(80) NOT NULL DEFAULT ' ',
  `barCode` varchar(120) NOT NULL DEFAULT '未填' COMMENT '条形码',
  `itemNo` varchar(120) NOT NULL DEFAULT '未填' COMMENT '编码',
  `storageId` int NULL DEFAULT 0 COMMENT '库位ID',
  `productId` int NULL DEFAULT 0 COMMENT '商品ID',
  `categoryId` int NULL DEFAULT 0 COMMENT '类目ID',
  `customerId` int NULL DEFAULT 0 COMMENT '商家ID',
  `brandId` int NULL DEFAULT 0 COMMENT '品牌ID',
  `skuMarketingId` int NULL DEFAULT 0 COMMENT '活动ID',
  `skuActivityType` varchar(20) NULL default 'Normal' COMMENT '活动类型',
  `appraise` tinyint(1) NULL DEFAULT 0 COMMENT '是否已评价(0 false 表示未评价, 1 true 表示已评价), 默认是 0',
  `createDate` datetime NULL DEFAULT '0000-00-00 00:00:00',
  `updateDate` datetime NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `SystemEvent`;
CREATE TABLE  `SystemEvent` (
  `orderId` BIGINT unsigned NOT NULL,
  `date` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `operator` varchar(20) NOT NULL,
  `beforeState` varchar(20) NOT NULL,
  `afterState` varchar(20) NOT NULL,
  `eventInfo` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `PaymentEvent`;
CREATE TABLE  `PaymentEvent` (
  `orderId` BIGINT unsigned NOT NULL,
  `date` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `operator` varchar(20) NOT NULL,
  `beforeState` varchar(20) NOT NULL,
  `afterState` varchar(20) NOT NULL,
  `eventInfo` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `LogisticsEvent`;
CREATE TABLE  `LogisticsEvent` (
  `logisticsId` BIGINT unsigned NOT NULL,
  `date` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `operator` varchar(20) NOT NULL,
  `beforeState` varchar(20) NOT NULL,
  `afterState` varchar(20) NOT NULL,
  `eventInfo` varchar(500) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `Address`;
CREATE TABLE  `Address` (
  `id` int unsigned NOT NULL auto_increment,
  `userId` varchar(100) not null,
  `name` varchar(20) not null,
  `province` varchar(200) not null,
  `location` varchar(200) not null ,
  `mobile` varchar(20) not null ,
  `telephone` varchar(20) ,
  `email` varchar(50) ,
  `zipCode` varchar(10) not null ,
  `defaultAddress` BOOLEAN  not null,
  `frequency` int not null,
  `isDelete` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `createDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
   PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `InvoiceCompany`;
CREATE TABLE  `InvoiceCompany` (
  `id` int unsigned NOT NULL auto_increment,
  `userId` varchar(50) not null,
  `companyName` varchar(100) not null,
  `isDelete` tinyint(1) unsigned NOT NULL DEFAULT 0,
  `createDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updateDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
   PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `OrderStateHistory`;
CREATE TABLE  `OrderStateHistory` (
  `id` BIGINT unsigned NOT NULL AUTO_INCREMENT,
  `orderId` BIGINT unsigned NOT NULL,
  `orderState` varchar(50) not null,

  `overlay` BOOLEAN not null DEFAULT false COMMENT '用户是否可见(已无用), 默认是 false',
  `debugMode` BOOLEAN not null DEFAULT false COMMENT '是否对用户可见, 默认是 0(可见)',
  `stateLevel` int not null,

  `operator` varchar(100) COMMENT '操作者',
  `doWhat` varchar(1024) COMMENT '做了什么',
  `remark` varchar(1024) COMMENT '说明',

  `date` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '操作时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `LogisticsInfo`;
CREATE TABLE `LogisticsInfo` (
  `expressNo` varchar(32) NOT NULL COMMENT '快递单号, 主键',
  `expressValue` text NOT NULL COMMENT '物流信息. {text 的值为 65535[(2 << 15) - 1] 个字符}',
  `status` tinyint(1) NOT NULL default '0' COMMENT '状态(1表示配送完成, 0表示未完成). 配送完成后则不需要再去走第三方通道进行查询.',
  PRIMARY KEY  (`expressNo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `trade`;
CREATE TABLE  `trade` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tradeNo` varchar(25) NOT NULL COMMENT '交易流水号',
  `outerTradeNo` varchar(45) NOT NULL COMMENT '第三方平台交易号',
  `outerBuyerId` varchar(45) DEFAULT NULL COMMENT '买家在第三方支付平台的用户id',
  `outerBuyerAccount` varchar(45) DEFAULT NULL COMMENT '买家在第三方支付平台的账户',
  `outerPlatformType` varchar(45) DEFAULT NULL COMMENT '第三方支付平台类型：支付宝：alipay, 财付通：tenpay',
  `bizType` varchar(45) DEFAULT NULL COMMENT '业务方式：订单or优惠券',
  `defaultbank` varchar(45) DEFAULT NULL COMMENT '支付方式(银行)',
  `payMethod` varchar(45) DEFAULT NULL COMMENT '第三方支付平台的支付类型',
  `payTotalFee` bigint(20) DEFAULT NULL COMMENT '支付金额',
  `tradeStatus` varchar(45) DEFAULT NULL COMMENT '交易状态',
  `tradeGmtCreateTime` datetime DEFAULT NULL COMMENT '第三方支付平台交易创建时间',
  `tradeGmtModifyTime` datetime DEFAULT NULL COMMENT '第三支付平台交易修改时间',
  `gmtCreateTime` datetime DEFAULT NULL COMMENT '记录创建时间',
  `gmtModifyTime` datetime DEFAULT NULL COMMENT '记录修改时间',
  `notifyId` varchar(100) DEFAULT NULL COMMENT '第三方消息ID',
  `notifyType` varchar(45) DEFAULT NULL COMMENT '第三方消息类型',
  PRIMARY KEY (`id`),
  UNIQUE KEY `outerTradeNo_UNIQUE` (`outerTradeNo`),
  UNIQUE KEY `tradeNo_UNIQUE` (`tradeNo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `tradeOrder`;
CREATE TABLE `tradeOrder` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tradeNo` varchar(25) NOT NULL COMMENT '交易流水号',
  `orderNo` bigint(20) NOT NULL COMMENT '订单编号',
  `payFlag` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态(0.创建, 1.付款成功), 默认是 0',
  `tradeType` varchar(10) NOT NULL DEFAULT 'BuyProduct' COMMENT '购买类型(可能是商品，可能是充值，重复付款), 默认是商品购买',
  `createTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '修改时间',
  `isDelete` tinyint(4) DEFAULT '0' COMMENT '是否删除(0. 未删除, 1.已删除), 默认是 0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `backGoods`;
CREATE TABLE `backGoods` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '退货单编号',
  `orderNo` bigint(20) NOT NULL COMMENT '订单编号',
  `orderId` bigint(20) NOT NULL COMMENT '订单ID',
  `userId` int(10) NOT NULL COMMENT '用户Id' ,
  `userName` varchar(100) NOT NULL COMMENT '用户名(第三方账户可能会重复)',
  `accountType` varchar(16) NULL DEFAULT 'KRQ' COMMENT '用户账户类型',
  `expressNo` varchar(32) COMMENT '退货单物流编号',
  `backReason` varchar(100) COMMENT '退货详细描述(不能超过500个字)',
  `backReasonReal` varchar(100) COMMENT '退货原因 质量问题  非质量问题',
  `backShopperName` varchar(1024) COMMENT '联系人姓名',
  `backPhone` varchar(20) COMMENT '联系人号码',
  `backPrice` bigint(20) unsigned NOT NULL default '0' COMMENT '退货金额',
  `uploadFiles` varchar(512) COMMENT '退单图片的名称和地址',
  `processMode` varchar(100) COMMENT '处理方式 退货 退款 保修 目前还没用',
  `backAddress` varchar(100) COMMENT '退货地址 目前还没用',
  `backState` varchar(20) NOT NULL COMMENT '退货状态',
  `backType` varchar(20) NULL COMMENT '退货类型(已发货的退单 YetSend, 未发货的退单 NoSend)',
  `createTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '修改时间',
  `isDelete` tinyint(4) NOT NULL default '0' COMMENT '是否删除(1 表示已删除, 0 表示未删除), 默认是 0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `backGoodsItem`;
CREATE TABLE `backGoodsItem` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `backGoodsId` bigint(20) NOT NULL COMMENT '退货单编号',
  `orderItemId` bigint(20) NOT NULL COMMENT '订单详情编号',
  `unitPrice` bigint(20) unsigned NOT NULL COMMENT '购买 sku 时的单价',
  `number` int(10) unsigned NOT NULL COMMENT '数量',
  `orderState`  varchar(20) DEFAULT '' COMMENT '创建退货单详情时的订单详情对应的状态' ,
  `createTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '修改时间',
  `isDelete` tinyint(4) NOT NULL default '0' COMMENT '是否删除(1 表示已删除, 0 表示未删除), 默认是 0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `backGoodsLog`;
CREATE TABLE `backGoodsLog` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `backGoodsId` bigint(20) NOT NULL COMMENT '退货单编号',
  `userName` varchar(100) NOT NULL COMMENT '操作者(用户名)',
  `operaTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '操作时间',
  `doWhat` varchar(1024) NOT NULL COMMENT '做了什么',
  `remark` varchar(1024) COMMENT '说明',
  `backState` varchar(20) NOT NULL COMMENT '操作时的退货状态',
  `isDelete` tinyint(4) NOT NULL default '0' COMMENT '是否删除(1 表示已删除, 0 表示未删除), 默认是 0',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS `refundTrade`;
CREATE TABLE `refundTrade` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `batchNo` varchar(25) NOT NULL COMMENT '退款批次号',
  `successNum` int DEFAULT '0' NOT NULL COMMENT '成功笔数',
  `realRefund` bigint NOT NULL default 0 COMMENT '本批次实际退款总金额',
  `createTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  `isDelete` tinyint(4) DEFAULT '0' COMMENT '是否删除(0. 未删除, 1.已删除), 默认是 0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `refundTradeOrder`;
CREATE TABLE `refundTradeOrder` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `batchNo` varchar(25) NOT NULL COMMENT '退款批次号',
  `backGoodsId` bigint(20) NOT NULL COMMENT '退货单编号',
  `outerTradeNo` varchar(50) NOT NULL COMMENT '支付宝交易号',
  `refund` bigint NOT NULL COMMENT '退款金额',
  `realRefund` bigint default 0 NOT NULL COMMENT '实际成功金额',
  `createTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `updateTime` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  `success` tinyint(4) DEFAULT '0' NOT NULL COMMENT '是否成功,1表示成功，0表示失败',
  `isDelete` tinyint(4) DEFAULT '0' COMMENT '是否删除(0. 未删除, 1.已删除), 默认是 0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `coupon`;
CREATE TABLE  `coupon` (
  `id` int unsigned NOT NULL auto_increment,
  `code` varchar(20) NOT NULL unique COMMENT '现金券编码',
  `couponType` varchar(20) NOT NULL default 'Normal',
  `used` tinyint NOT NULL default 0 COMMENT '是否使用',
  `publish` tinyint NOT NULL default 0 COMMENT '是否已分发',
  `orderNo` bigint NOT NULL default 0,
  `userId` int NOT NULL default 0,
  `price` bigint NOT NULL default 0 COMMENT '金额',
  `miniApplyOrderPrice` bigint NOT NULL default 0 COMMENT '能够使用的最少订单价格',
  `msgRemind` tinyint not null default 0 comment '是否有发送短信提醒',
  `startDate`  datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '开始时间',
  `expireDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '过期时间',
  `createDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `updateDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `OrderMessage`;
CREATE TABLE `OrderMessage` (
  `id`  bigint(20) NOT NULL AUTO_INCREMENT ,
  `orderId`  bigint(20) NOT NULL COMMENT '订单Id' ,
  `orderNo`  bigint(20) NOT NULL COMMENT '订单编号' ,
  `userType`  varchar(16) NOT NULL COMMENT 'User用户, Server客服, Supplier商家' ,
  `userId`  int(10) NOT NULL COMMENT '用户Id' ,
  `userName`  varchar(100) NOT NULL COMMENT '用户名' ,
  `messageInfo`  varchar(1024) NULL COMMENT '留言信息' ,
  `createDate`  datetime NULL COMMENT '创建时间' ,
  `updateDate`  datetime NULL COMMENT '更新时间' ,
  `isDelete` tinyint(4) DEFAULT '0' COMMENT '是否删除(0. 未删除, 1.已删除), 默认是 0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='';


DROP TABLE IF EXISTS `valuation`;
CREATE TABLE `valuation` (
`id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
`userId` int(11) NOT NULL COMMENT '用户id',
`operatorId` int(11) NULL DEFAULT 0 COMMENT '回复人id',
`userName` varchar(20) NOT NULL COMMENT '用户名',
`operator` varchar(20) NULL COMMENT '回复人',
`point` int(11) DEFAULT NULL COMMENT '评价分数',
`content` text DEFAULT NULL COMMENT '评价内容',
`replyContent` text DEFAULT NULL COMMENT '回复内容',
`orderItemId` int(11) DEFAULT NULL COMMENT '订单项id',
`productId` int(11) unsigned DEFAULT NULL COMMENT '商品id',
`orderCreateDate` datetime DEFAULT NULL COMMENT '所评订单的下单时间',
`createDate` datetime DEFAULT NULL COMMENT '创建时间',
`updateDate` datetime DEFAULT NULL COMMENT '更新时间',
`replyTime` datetime DEFAULT NULL COMMENT '回复时间',
`appendContent` text NULL COMMENT '追加评价',
`appendDate` datetime NULL DEFAULT NULL COMMENT '追加时间',
`appendReplyContent` text NULL COMMENT '追加回复评价',
`appendOperator` varchar(20) NULL COMMENT '追加回复评价的操作者',
`appendReplyDate` datetime NULL COMMENT '追加回复时间',
`isDelete` tinyint(4) NOT NULL DEFAULT '0',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `umpayrequest`;
CREATE TABLE `umpayrequest` (
`id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
`orderId` varchar(100) NOT NULL unique COMMENT '联动优势的订单号',
`coupon` varchar(1000) NULL COMMENT '平台生成的现金券信息',
`goodsId` varchar(50) NULL COMMENT '编号',
`createDate` datetime DEFAULT NULL COMMENT '创建时间',
`isDelete` tinyint(4) NOT NULL DEFAULT '0',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `rotary`;
CREATE TABLE `rotary` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL COMMENT '轮盘名',
  `description` varchar(128) COMMENT '详细说明',
  `tally` int(11) NOT NULL COMMENT '每次抽奖消耗的点数',
  `rule` varchar(64) NOT NULL COMMENT '抽奖规则',
  `detailRule` text COMMENT '详细规则',
  `startDate` datetime DEFAULT '0000-00-00 00:00:00' COMMENT '开始时间',
  `expireDate` datetime DEFAULT '0000-00-00 00:00:00' COMMENT '过期时间',
  `createDate` datetime DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `updateDate` datetime DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  `isDelete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除(1 表示已删除, 0 表示未删除), 默认是 0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='抽奖活动';


DROP TABLE IF EXISTS `rotaryMeed`;
CREATE TABLE `rotaryMeed` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rotaryId` int(11) NOT NULL COMMENT '抽奖活动 Id',
  `meedType` varchar(16) NOT NULL COMMENT '奖品类型(积分还是商品)',
  `meedValue` varchar(16) NOT NULL COMMENT '奖品值(类型是商品则 proId-skuId, 现金券则是面值-最小使用金额, 积分则是数值)',
  `imageUrl` varchar(64) NOT NULL COMMENT '图片 url',
  `description` varchar(128) NOT NULL COMMENT '图片描述',
  `meedIndex` int(11) NOT NULL COMMENT '序号(同一个抽奖活动的序号不能重复)',
  `meedProbability` int(11) NOT NULL COMMENT '中奖概率(万分之比例, 比如: 在 10000 里面抽中 100 次, 则此值设置为 100)',
  `createDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `updateDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='轮盘里面的奖品项';


DROP TABLE IF EXISTS `lottery`;
CREATE TABLE `lottery` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `rotaryId` int(11) NOT NULL COMMENT '抽奖Id',
  `rotaryMeedId` int(11) NOT NULL COMMENT '抽奖的奖品Id',
  `userName` varchar(100) NOT NULL COMMENT '中奖人',
  `meedType` varchar(16) NOT NULL COMMENT '奖品类型(积分还是商品)',
  `meedValue` varchar(64) NOT NULL COMMENT '奖品值(类型是商品则 proId-skuId, 现金券则是 面值-最低使用金额, 积分则是数值)',
  `value` varchar(64) COMMENT '商品存储商品名, 现金券则存储面值, 积分则显示数值',
  `really` tinyint(1) default 0 COMMENT '是否真实(1 表示真实, 0 表示不真实), 默认是 0',
  `consigneeName` varchar(16) COMMENT '收货人名字',
  `consigneePhone` varchar(16) COMMENT '收货人电话',
  `consigneeAddress` varchar(64) COMMENT '收货人地址',
  `sendOut` tinyint(1) DEFAULT 0 COMMENT '是否已发货(1 表示已发货, 0 表示未发货), 默认是 0',
  `createDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '创建时间',
  `updateDate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00' COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='中奖信息';


