DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `userName` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `englishName` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `leaveOffice` tinyint(1)  NOT NULL DEFAULT 0,
  `deleteData` tinyint(1)  NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `userName` (`userName`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `englishName` (`englishName`)
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8;
INSERT INTO `account` (`id`,`userName`,`englishName`,`email`,`password`,`leaveOffice`,`deleteData`) VALUES
 (1,'admin','admin','yijushang@kariqu.com','f379eaf3c831b04de153469d1bec345e',0,0);

drop table if exists Employee_Boss;
create table Employee_Boss(
  employeeId int not null,
  bossId int not null
);


drop table if exists Position;
create table Position (
  id int unsigned not null auto_increment,
  positionName varchar(255),
  level varchar(20) not null,
  primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `Position` (`id`,`positionName`,`level`) VALUES (1,'总裁','ONE'),(2,'总监','TWO'),(3,'主管','THREE'),(4,'员工','FOUR'),(5,'HR','FOUR');

drop table if exists Employee_Position;
create table Employee_Position(
  employeeId int not null,
  positionId int not null
);


drop table if exists Department;
create table Department (
  id int unsigned not null auto_increment,
  name varchar(255),
  primary key (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

drop table if exists Employee_Department;
create table Employee_Department(
  employeeId int not null,
  departmentId int not null
);

