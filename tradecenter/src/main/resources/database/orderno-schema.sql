
-- 使用数据库生成订单编号. 调用 getOrderNo() 函数
-- ----------------------------
-- Table structure for `sequence`
-- ----------------------------
DROP TABLE IF EXISTS `sequence`;
CREATE TABLE `sequence` (
  `name` varchar(50) NOT NULL COMMENT '理解为序列名',
  `current_value` int(11) NOT NULL COMMENT '当前值',
  `increment` int(11) NOT NULL default '1' COMMENT '增长量',
  `init_value` int(11) NOT NULL COMMENT '初始值(主要用来当重建此表时用到)',
  PRIMARY KEY  (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of sequence
-- ----------------------------
INSERT INTO `sequence`(name, current_value, increment, init_value) VALUES ('orderNoSeq', '21', '13', '21');

-- ----------------------------
-- Function structure for `currVal`
-- ----------------------------
DROP FUNCTION IF EXISTS `currVal`;
DELIMITER ;;
CREATE FUNCTION `currVal`(seq_name VARCHAR(50)) RETURNS int(11)
BEGIN
  DECLARE `value` INTEGER;
  -- SET `value` = 0;
  SELECT current_value INTO `value`
  FROM sequence WHERE name = seq_name;
  RETURN value;
END
;;
DELIMITER ;

-- ----------------------------
-- Function structure for `getOrderNo`
-- ----------------------------
DROP FUNCTION IF EXISTS `getOrderNo`;
DELIMITER ;;
CREATE FUNCTION `getOrderNo`() RETURNS int(11)
BEGIN
   DECLARE cur INTEGER;
   DECLARE init INTEGER;
   DECLARE max INTEGER;
   select current_value, init_value into cur, init
   from sequence where name = 'orderNoSeq';
   -- 若初始值相同, 表示 mysql 表重建过, 这时候应该去 OrderTable 查出最大值并赋值
   IF cur = init THEN
      -- 取最大订单号第 9 位以后的数值, 转换成无符号整形(正数)
      select CONVERT(SUBSTR(max(orderNo), 9), UNSIGNED) into max from OrderTable;
     -- 将上面查出的值赋值到序列中, 后面的 into max 如果不加, mysql 会报语法错误.
      select setVal('orderNoSeq', max) into max;
   END IF;
   -- 使用 当前年月日 及 序列 生成订单号
   RETURN CAST(concat((CURDATE() + 0), nextVal('orderNoSeq')) as UNSIGNED);
END
;;
DELIMITER ;

-- ----------------------------
-- Function structure for `nextVal`
-- ----------------------------
DROP FUNCTION IF EXISTS `nextVal`;
DELIMITER ;;
CREATE FUNCTION `nextVal`(seq_name VARCHAR(50)) RETURNS int(11)
BEGIN
   UPDATE sequence
   SET current_value = current_value + increment
   WHERE name = seq_name;
   RETURN currVal(seq_name);
END
;;
DELIMITER ;

-- ----------------------------
-- Function structure for `setVal`
-- ----------------------------
DROP FUNCTION IF EXISTS `setVal`;
DELIMITER ;;
CREATE FUNCTION `setVal`(seq_name VARCHAR(50), `value` INTEGER) RETURNS int(11)
BEGIN
   UPDATE sequence SET current_value = `value`
   WHERE name = seq_name;
   RETURN currVal(seq_name);
END
;;
DELIMITER ;

-- select getorderno();