INSERT INTO `T_SYSPARA` VALUES ('order_open', 'order_open', null, 'true', '100', '2', '订单交易状态(false不可下单)');
INSERT INTO `T_SYSPARA` VALUES ('login_num', 'login_num', null, '0', '100', '0', '前端用户登陆次数统计');
INSERT INTO `T_SYSPARA` VALUES ('withdraw_fee_type', 'withdraw_fee_type', null, 'fixed', '100', '2', '提现手续费收取方式,fixed是单笔固定金额，rate是百分比');
INSERT INTO `T_SYSPARA` VALUES ('withdraw_fee', 'withdraw_fee', null, '10', '100', '2', '提现手续费,关联“提现手续费收取方式”');
INSERT INTO `T_SYSPARA` VALUES ('order_close_line', 'order_close_line', null, '110', '100', '2', '默认订单亏损剩余平仓线(公式：保证金/盈亏>=参数%)。单位%');
INSERT INTO `T_SYSPARA` VALUES ('data_interval', 'data_interval', null, '1000', '100', '0', 'Realtime保存间隔(毫秒)');
DELETE FROM  T_SYSPARA WHERE UUID = 'party_userid_sequence';
INSERT INTO `T_SYSPARA` VALUES ('kyc_verify', 'kyc_verify', null, 'true', '100', '2', '是否开启KYC验证，开启后用户需要KYC验证后可方交易。true开启，false关闭');

INSERT INTO `T_SYSPARA` VALUES ('index_top_symbols', 'index_top_symbols', null, 'btc,eth,etc', '100', '2', '首页顶部的三个行情展示代码');


INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('CNY_in', 'in', 7.3, 'CNY', '人民币');
INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('CNY_out', 'out', 7.3, 'CNY', '人民币');

INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('HKD_in', 'in', 8, 'HKD', '港币');
INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('HKD_out', 'out', 8, 'HKD', '港币');


INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('TWD_in', 'in', 33, 'TWD', '台币');
INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('TWD_out', 'out', 33, 'TWD', '台币');

INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('USD_in', 'in', 1, 'USD', '美元');
INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('USD_out', 'out', 1, 'USD', '美元');

INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('EUR_in', 'in', 1, 'EUR', '欧元');
INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('EUR_out', 'out', 1, 'EUR', '欧元');

INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('THB_in', 'in', 35, 'THB', '泰铢');
INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('THB_out', 'out', 35, 'THB', '泰铢');

INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('SGD_in', 'in', 1.7, 'SGD', '新币');
INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('SGD_out', 'out', 1.7, 'SGD', '新币');

INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('MYR_in', 'in', 1, 'MYR', '马币');
INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('MYR_out', 'out', 1, 'MYR', '马币');



INSERT INTO `T_SYSPARA` VALUES ('customer_service_url', 'customer_service_url', null, '', '100', '2', '在线客服URL');


INSERT INTO `T_SYSPARA` VALUES ('order_close_line_type', 'order_close_line_type', null, '1', '100', '2', '平仓方式。 全仓[1],单个持仓[2]');

