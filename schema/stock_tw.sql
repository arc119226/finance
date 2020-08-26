-- --------------------------------------------------------
-- 主機:                           127.0.0.1
-- 伺服器版本:                        10.5.4-MariaDB - mariadb.org binary distribution
-- 伺服器操作系統:                      Win64
-- HeidiSQL 版本:                  9.5.0.5278
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- 傾印 stock_tw 的資料庫結構
CREATE DATABASE IF NOT EXISTS `stock_tw` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;
USE `stock_tw`;

-- 傾印  表格 stock_tw.company_group 結構
CREATE TABLE IF NOT EXISTS `company_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_code` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '集團代號',
  `group_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '集團分類',
  PRIMARY KEY (`id`),
  UNIQUE KEY `group_code` (`group_code`),
  UNIQUE KEY `group_name` (`group_name`)
) ENGINE=InnoDB AUTO_INCREMENT=477 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='集團股分類目錄';

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.company_stock 結構
CREATE TABLE IF NOT EXISTS `company_stock` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '集團代號',
  `security_code` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '證卷代號',
  PRIMARY KEY (`id`),
  UNIQUE KEY `group_code_security_code` (`group_code`,`security_code`)
) ENGINE=InnoDB AUTO_INCREMENT=1964 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='股票與集團代碼';

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.concept_group 結構
CREATE TABLE IF NOT EXISTS `concept_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '概念股分類代號',
  `group_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '概念股分類',
  PRIMARY KEY (`id`),
  UNIQUE KEY `concept_code_concept_name` (`group_code`,`group_name`),
  UNIQUE KEY `concept_code` (`group_code`),
  UNIQUE KEY `concept_name` (`group_name`)
) ENGINE=InnoDB AUTO_INCREMENT=2521 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='概念股類別目錄';

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.concept_stock 結構
CREATE TABLE IF NOT EXISTS `concept_stock` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '概念股分類代號',
  `security_code` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '證卷代號',
  PRIMARY KEY (`id`),
  UNIQUE KEY `group_code_security_code` (`group_code`,`security_code`)
) ENGINE=InnoDB AUTO_INCREMENT=67671 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='概念股代碼與股票代碼';

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.daily_foreign_shareholding_by_industrial 結構
CREATE TABLE IF NOT EXISTS `daily_foreign_shareholding_by_industrial` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category_of_Industry` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '產業別',
  `numbers` bigint(20) NOT NULL DEFAULT 0 COMMENT '家數',
  `number_of_shares_issued` bigint(20) NOT NULL DEFAULT 0 COMMENT '總發行股數',
  `currently_foreign_and_mainland_area_shares_held` bigint(20) NOT NULL DEFAULT 0 COMMENT '僑外資及陸資持有總股數',
  `percentage_of_foreign_and_mainland_area_shares_held` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '僑外資及陸資持股比率',
  `traded_day` int(11) NOT NULL DEFAULT 0 COMMENT '交易日其',
  PRIMARY KEY (`id`),
  UNIQUE KEY `category_of_Industry_traded_day` (`category_of_Industry`,`traded_day`)
) ENGINE=InnoDB AUTO_INCREMENT=155628 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='外資及陸資投資類股彙總持股比率表';

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.foreign_mainland_area_investors_trading_and_shareholding 結構
CREATE TABLE IF NOT EXISTS `foreign_mainland_area_investors_trading_and_shareholding` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `security_code` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '證券代號',
  `name_of_security` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '證券名稱',
  `isin_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '國際證券編碼',
  `number_of_shares_issued` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '發行股數',
  `available_shares_bought` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '外資尚可投資股數',
  `currently_shares_held` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '全體外資持有股數',
  `percentage_of_available_investment` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '外資尚可投資比率',
  `percentage_of_shares_held` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '全體外資持股比率',
  `upper_limit` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '法令投資上限比率',
  `upper_limit_mainland` decimal(15,2) DEFAULT 0.00 COMMENT '陸資法令投資上限比率',
  `reasons_of_change` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '與前日異動原因',
  `last_filing_date_by_listed_companies` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '最近一次上市公司申報外資持股異動日期',
  `traded_day` int(11) NOT NULL DEFAULT 0 COMMENT '交易日其',
  PRIMARY KEY (`id`),
  UNIQUE KEY `security_code_traded_day` (`security_code`,`traded_day`)
) ENGINE=InnoDB AUTO_INCREMENT=3669461 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='外資投資持股統計\r\n\r\n一、與前日異動原因以空白及數字2,3,4,5表示，其代表意義說明如下：,\r\n''空白''係指外陸資於集中市場交易所產生之股數異動，不包含外陸資配認股數、外資原始股東出售股數、存託憑證發行等異動。,\r\n  ''2''係上市公司因股本變動申報最近一次除權交易日或股東會外陸資持股情形、因依規定之固定基準日申報外陸資持股情形、因前次申報資料有誤予以更新、或因發行海外有價證券、合併、公開收購等申報外陸資持股情形。,\r\n  ''3''係指非前項因素所產生之持股變動，如海外存託憑證經證期局核可尚未發行之預扣等。,\r\n  ''4''係依海外存託憑證流通餘額及外國人將海外存託憑證兌回普通股或將普通股再發行海外存託憑證情形更新外陸資持股數。,\r\n  ''5''係依上市公司每月申報國內海外有價證券轉換情形更新外陸資持股數。';

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.highlights_of_daily_trading 結構
CREATE TABLE IF NOT EXISTS `highlights_of_daily_trading` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date` int(11) NOT NULL DEFAULT 0 COMMENT '日期',
  `trade_volume` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '成交量',
  `trade_value` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '成交金額',
  `transaction` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '成交筆數',
  `taiex` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '發行量加權股價指數',
  `change` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '漲跌點數',
  `traded_day` int(11) NOT NULL DEFAULT 0 COMMENT '成交日期',
  PRIMARY KEY (`id`),
  UNIQUE KEY `date` (`date`)
) ENGINE=InnoDB AUTO_INCREMENT=5447 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='市場成交資訊';

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.industry_mapping 結構
CREATE TABLE IF NOT EXISTS `industry_mapping` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name_en` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0',
  `name_tw` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.investors 結構
CREATE TABLE IF NOT EXISTS `investors` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `security_code` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '證卷代號',
  `traded_day` int(11) NOT NULL DEFAULT 0 COMMENT '交易日期',
  `total_buy` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '買超',
  `total_sell` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '賣超',
  `difference` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '買賣差',
  `type` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT 'FD=外資 D=自營商 SITC=投信',
  PRIMARY KEY (`id`),
  UNIQUE KEY `security_code_traded_day_type` (`security_code`,`traded_day`,`type`)
) ENGINE=InnoDB AUTO_INCREMENT=4859575 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='三大法人買賣超';

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.margin_transactions_all 結構
CREATE TABLE IF NOT EXISTS `margin_transactions_all` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `security_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '股票代號',
  `margin_purchase` decimal(15,2) DEFAULT NULL COMMENT '融資買進',
  `margin_sales` decimal(15,2) DEFAULT NULL COMMENT '融資賣出',
  `cash_redemption` decimal(15,2) DEFAULT NULL COMMENT '融資現金償還',
  `balance_of_previous_day` decimal(15,2) DEFAULT NULL COMMENT '融資前日餘額',
  `balance_of_the_day` decimal(15,2) DEFAULT NULL COMMENT '融資今日餘額',
  `quota` decimal(15,2) DEFAULT NULL COMMENT '限額',
  `short_covering` decimal(15,2) DEFAULT NULL COMMENT '融卷買進',
  `short_sale` decimal(15,2) DEFAULT NULL COMMENT '融卷賣出',
  `stock_redemption` decimal(15,2) DEFAULT NULL COMMENT '現券償還',
  `short_balance_of_previous_day` decimal(15,2) DEFAULT NULL COMMENT '融卷前日餘額',
  `short_balance_of_the_day` decimal(15,2) DEFAULT NULL COMMENT '融卷今日餘額',
  `short_quota` decimal(15,2) DEFAULT NULL COMMENT '限額',
  `offsetting_of_margin_purchases_and_short_sales` decimal(15,2) DEFAULT NULL COMMENT '資券互抵',
  `note` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '註記',
  `traded_day` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `security_code_traded_day` (`security_code`,`traded_day`)
) ENGINE=InnoDB AUTO_INCREMENT=3841025 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='融資融券彙總 (全部)';

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.margin_transaction_summary 結構
CREATE TABLE IF NOT EXISTS `margin_transaction_summary` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `item` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '項目',
  `margin_purchase_short_covering` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '買進',
  `margin_saleshort_sale` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '賣出',
  `cash_redemption_stock_redemption` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '現金(券)償還',
  `balance_of_previous_day` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '前日餘額',
  `balance_of_the_day` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '今日餘額',
  `traded_day` int(11) NOT NULL DEFAULT 0 COMMENT '交易日其',
  PRIMARY KEY (`id`),
  UNIQUE KEY `item_traded_day` (`item`,`traded_day`)
) ENGINE=InnoDB AUTO_INCREMENT=14788 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='信用交易統計';

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.monthly_closing_average_price 結構
CREATE TABLE IF NOT EXISTS `monthly_closing_average_price` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '時間類型',
  `closing_price` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '收盤價',
  `traded_day` int(11) NOT NULL DEFAULT 0 COMMENT '交易日',
  `security_code` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '證卷代號',
  PRIMARY KEY (`id`),
  UNIQUE KEY `date_traded_day_security_code` (`date`,`traded_day`,`security_code`)
) ENGINE=InnoDB AUTO_INCREMENT=5603967 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='美日收盤價及每月均價';

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.monthly_trading_summary 結構
CREATE TABLE IF NOT EXISTS `monthly_trading_summary` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `security_code` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '證卷代號',
  `year` int(11) NOT NULL COMMENT '年度',
  `month` int(11) NOT NULL COMMENT '月份',
  `highest_price` decimal(15,2) NOT NULL COMMENT '最高價',
  `lowest_price` decimal(15,2) NOT NULL COMMENT '最低價',
  `weighted_average_price_ab` decimal(15,2) NOT NULL COMMENT '加權(A/B)平均價',
  `transaction` decimal(15,2) NOT NULL COMMENT '成交筆數',
  `trade_value_a` decimal(15,2) NOT NULL COMMENT '成交金額(A)',
  `trade_volumeb` decimal(15,2) NOT NULL COMMENT '成交股數(B)',
  `turnover_ratio` decimal(15,2) NOT NULL COMMENT '週轉率(%)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `security_code_year_month` (`security_code`,`year`,`month`)
) ENGINE=InnoDB AUTO_INCREMENT=385410 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='個股月成交資訊';

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.pe_dy_pb 結構
CREATE TABLE IF NOT EXISTS `pe_dy_pb` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `security_code` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '證卷代號',
  `traded_day` int(11) NOT NULL COMMENT '交易日期',
  `dividend_yield` decimal(15,2) DEFAULT NULL COMMENT '殖利率(年報酬率) = 現金股利 ÷ 股價',
  `dividend_year` decimal(15,2) DEFAULT NULL COMMENT '股利年度',
  `pe_ratio` decimal(15,2) DEFAULT NULL COMMENT '本益比 花多少年才能回本 PE',
  `pb_ratio` decimal(15,2) DEFAULT NULL COMMENT '股價淨值比 PBR = 股價 ÷ 每股淨值 淨值 = 資產總值 – 總負債和無形資產',
  `fiscal_year_quarter` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '財報年/季',
  PRIMARY KEY (`id`),
  UNIQUE KEY `stock_code_traded_day` (`security_code`,`traded_day`)
) ENGINE=InnoDB AUTO_INCREMENT=5278936 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='本益比 \r\n判斷公司股價是否合理，通常用來分析獲利穩定的公司。\r\n淨值比\r\n判斷公司股價是否合理，通常用來分析獲利不穩定或虧損的公司。\r\n殖利率\r\n年報酬率';

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.short_sales_volume_and_value 結構
CREATE TABLE IF NOT EXISTS `short_sales_volume_and_value` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `security_code` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT '0' COMMENT '證卷代號',
  `traded_day` int(11) NOT NULL DEFAULT 0 COMMENT '交易日',
  `securities_lending_volume` decimal(15,2) DEFAULT 0.00 COMMENT '融卷賣出張數',
  `securities_lending_value` decimal(15,2) DEFAULT 0.00 COMMENT '榮卷賣出金額',
  `borrow_volume` decimal(15,2) DEFAULT 0.00 COMMENT '借卷賣出張數',
  `borrow_value` decimal(15,2) DEFAULT 0.00 COMMENT '借卷賣出金額',
  PRIMARY KEY (`id`),
  UNIQUE KEY `security_code_traded_day` (`security_code`,`traded_day`)
) ENGINE=InnoDB AUTO_INCREMENT=2694577 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='當日融券賣出與借券賣出成交量值';

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.statistics_trade_per_minute 結構
CREATE TABLE IF NOT EXISTS `statistics_trade_per_minute` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `security_code` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0',
  `traded_day` int(11) NOT NULL DEFAULT 0 COMMENT '證卷代號',
  `time` bigint(20) NOT NULL DEFAULT 0 COMMENT '時間yyyyMMddHHmmss',
  `acc_bid_orders` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '累積委託買進筆數',
  `acc_bid_volume` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '累積委託買進數量',
  `acc_ask_orders` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '累積委託賣出筆數',
  `acc_ask_volume` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '累積委託賣出數量',
  `acc_transaction` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '累積成交筆數',
  `acc_trade_volume` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '累積成交數量',
  `acc_trade_value_ntm` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '累積成交金額',
  PRIMARY KEY (`id`),
  UNIQUE KEY `security_code_traded_day_time` (`security_code`,`traded_day`,`time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='每分鐘委託成交統計';

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.stock 結構
CREATE TABLE IF NOT EXISTS `stock` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `security_code` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '證卷代號',
  `stock_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '證卷名稱',
  `isin_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '國際證券編碼',
  `listing_day` int(11) NOT NULL COMMENT '上市日期',
  `stock_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '上市 興櫃 上櫃',
  `stock_category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '產業類別',
  `cfi_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `product_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '金融商品別',
  `update_day` int(11) DEFAULT NULL COMMENT '更新日',
  PRIMARY KEY (`id`),
  UNIQUE KEY `stock_code` (`security_code`),
  UNIQUE KEY `stock_name` (`stock_name`),
  UNIQUE KEY `isin_code` (`isin_code`),
  KEY `listing_day` (`listing_day`),
  KEY `stock_type` (`stock_type`),
  KEY `stock_category` (`stock_category`),
  KEY `product_type` (`product_type`)
) ENGINE=InnoDB AUTO_INCREMENT=687678 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='股票基本資訊';

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.stock_day 結構
CREATE TABLE IF NOT EXISTS `stock_day` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `security_code` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '證卷代號',
  `traded_day` int(11) NOT NULL COMMENT '交易日期',
  `trade_volume` decimal(15,0) DEFAULT NULL COMMENT '成交股數',
  `trade_value` decimal(15,2) DEFAULT NULL COMMENT '成交金額',
  `opening_price` decimal(15,2) DEFAULT NULL COMMENT '開盤價',
  `highest_price` decimal(15,2) DEFAULT NULL COMMENT '最高價',
  `lowest_price` decimal(15,2) DEFAULT NULL COMMENT '最低價',
  `closing_price` decimal(15,2) DEFAULT NULL COMMENT '收盤價',
  `change` decimal(15,2) DEFAULT NULL COMMENT '漲跌價差',
  `transaction` decimal(15,2) DEFAULT NULL COMMENT '成交筆數',
  `updown_times` int(11) DEFAULT NULL COMMENT '連續漲跌次數',
  `last_updown_times` int(11) DEFAULT NULL COMMENT '前一日漲跌次數',
  `date` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '日期',
  PRIMARY KEY (`id`),
  UNIQUE KEY `security_code_traded_day` (`security_code`,`traded_day`),
  KEY `closing_price` (`closing_price`),
  KEY `traded_day` (`traded_day`),
  KEY `security_code` (`security_code`),
  KEY `change` (`change`)
) ENGINE=InnoDB AUTO_INCREMENT=8441774 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='盤後 日交易量';

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.taiex 結構
CREATE TABLE IF NOT EXISTS `taiex` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date` int(11) NOT NULL DEFAULT 0 COMMENT '日期',
  `opening_index` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '開盤指數',
  `highest_index` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '最高指數',
  `lowest_index` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '最低指數',
  `closing_index` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '收盤指數',
  `traded_day` int(11) NOT NULL DEFAULT 0 COMMENT '交易日',
  PRIMARY KEY (`id`),
  UNIQUE KEY `date_traded_day` (`date`,`traded_day`)
) ENGINE=InnoDB AUTO_INCREMENT=5393 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='TAIEX  發行量加權股價指數歷史資料';

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.yearly_trading_summary 結構
CREATE TABLE IF NOT EXISTS `yearly_trading_summary` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `year` int(11) DEFAULT NULL COMMENT '年',
  `trade_volume` decimal(15,2) DEFAULT NULL COMMENT '成交量',
  `trade_value` decimal(15,2) DEFAULT NULL COMMENT '成交金額',
  `transaction` decimal(15,2) DEFAULT NULL COMMENT '成交筆數',
  `highest_price` decimal(15,2) DEFAULT NULL COMMENT '最高價',
  `date_highest_price` int(11) NOT NULL COMMENT '最高日',
  `lowest_price` decimal(15,2) DEFAULT NULL COMMENT '最低價',
  `date_lowest_price` int(11) NOT NULL COMMENT '最低日',
  `average_closing_price` decimal(15,2) DEFAULT NULL COMMENT '平均收盤價',
  `security_code` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '證卷代碼',
  PRIMARY KEY (`id`),
  UNIQUE KEY `year_security_code` (`year`,`security_code`)
) ENGINE=InnoDB AUTO_INCREMENT=367270 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='年度成交資訊';

-- 取消選取資料匯出。
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
