-- ----------------------------
-- Table structure for t_wallet
-- ----------------------------
DROP TABLE IF EXISTS `T_EXCHANGE_RATE`;
CREATE TABLE `T_EXCHANGE_RATE` (
  `UUID` varchar(32) NOT NULL DEFAULT '' COMMENT '主键',
  `OUT_OR_IN` varchar(32) ,
  `RATA` double ,
  `CURRENCY` varchar(32) ,
  `NAME` varchar(64) ,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create index INDEX_EXCHANGE_OUT_OR_IN on T_EXCHANGE_RATE (
   OUT_OR_IN 
);


DROP TABLE IF EXISTS `T_WALLET_LOG`;
CREATE TABLE `T_WALLET_LOG` (
  `UUID` varchar(32) NOT NULL DEFAULT '' COMMENT '主键',
   `PARTY_ID` varchar(32) NOT NULL DEFAULT '',
   `ORDER_NO` varchar(64) ,
  `AMOUNT` double ,
  `STATUS` int ,
  `CREATE_TIME` datetime ,
  `CATEGORY` varchar(32) ,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create index INDEX_WALLET_LOG_PARTY_ID on T_WALLET_LOG (
   PARTY_ID,CREATE_TIME
);


