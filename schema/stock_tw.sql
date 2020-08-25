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
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_code` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '群組代碼',
  `group_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '集團分類',
  PRIMARY KEY (`id`),
  UNIQUE KEY `group_code` (`group_code`),
  UNIQUE KEY `group_name` (`group_name`)
) ENGINE=InnoDB AUTO_INCREMENT=273 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.company_stock 結構
CREATE TABLE IF NOT EXISTS `company_stock` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '集團代碼',
  `security_code` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '股票代碼',
  PRIMARY KEY (`id`),
  UNIQUE KEY `group_code_security_code` (`group_code`,`security_code`)
) ENGINE=InnoDB AUTO_INCREMENT=1309 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='股票與集團代碼';

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.concept_group 結構
CREATE TABLE IF NOT EXISTS `concept_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '群組代碼',
  `group_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '概念股分類',
  PRIMARY KEY (`id`),
  UNIQUE KEY `concept_code_concept_name` (`group_code`,`group_name`),
  UNIQUE KEY `concept_code` (`group_code`),
  UNIQUE KEY `concept_name` (`group_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1121 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.concept_stock 結構
CREATE TABLE IF NOT EXISTS `concept_stock` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `group_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `security_code` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `group_code_security_code` (`group_code`,`security_code`)
) ENGINE=InnoDB AUTO_INCREMENT=38665 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC COMMENT='概念股代碼與股票代碼';

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.investors 結構
CREATE TABLE IF NOT EXISTS `investors` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `security_code` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0',
  `traded_day` int(11) NOT NULL DEFAULT 0,
  `total_buy` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '買超',
  `total_sell` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '賣超',
  `difference` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '買賣差',
  `type` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT 'FD=外資 D=自營商 SITC=投信',
  PRIMARY KEY (`id`),
  UNIQUE KEY `security_code_traded_day_type` (`security_code`,`traded_day`,`type`)
) ENGINE=InnoDB AUTO_INCREMENT=3581755 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='三大法人買賣超';

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.monthly_closing_average_price 結構
CREATE TABLE IF NOT EXISTS `monthly_closing_average_price` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `date` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '時間類型',
  `closing_price` decimal(15,2) NOT NULL DEFAULT 0.00 COMMENT '收盤價',
  `traded_day` int(11) NOT NULL DEFAULT 0 COMMENT '交易日',
  `security_code` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `date_traded_day_security_code` (`date`,`traded_day`,`security_code`)
) ENGINE=InnoDB AUTO_INCREMENT=5311169 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='美日收盤價及每月均價';

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.monthly_trading_summary 結構
CREATE TABLE IF NOT EXISTS `monthly_trading_summary` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `security_code` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
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
) ENGINE=InnoDB AUTO_INCREMENT=260909 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='個股月成交資訊';

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.pe_dy_pb 結構
CREATE TABLE IF NOT EXISTS `pe_dy_pb` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `security_code` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `traded_day` int(11) NOT NULL,
  `dividend_yield` decimal(15,2) DEFAULT NULL COMMENT '殖利率(年報酬率) = 現金股利 ÷ 股價',
  `dividend_year` decimal(15,2) DEFAULT NULL COMMENT '鼓利年度',
  `pe_ratio` decimal(15,2) DEFAULT NULL COMMENT '本益比 花多少年才能回本 PE',
  `pb_ratio` decimal(15,2) DEFAULT NULL COMMENT '股價淨值比 PBR = 股價 ÷ 每股淨值 淨值 = 資產總值 – 總負債和無形資產',
  `fiscal_year_quarter` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '財報年/季',
  PRIMARY KEY (`id`),
  UNIQUE KEY `stock_code_traded_day` (`security_code`,`traded_day`)
) ENGINE=InnoDB AUTO_INCREMENT=2266247 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='本益比 \r\n判斷公司股價是否合理，通常用來分析獲利穩定的公司。\r\n淨值比\r\n判斷公司股價是否合理，通常用來分析獲利不穩定或虧損的公司。\r\n殖利率\r\n年報酬率';

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.statistics_trade_per_minute 結構
CREATE TABLE IF NOT EXISTS `statistics_trade_per_minute` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `security_code` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0',
  `traded_day` int(11) NOT NULL DEFAULT 0,
  `time` int(11) NOT NULL DEFAULT 0,
  `acc_bid_orders` decimal(15,2) NOT NULL DEFAULT 0.00,
  `acc_bid_volume` decimal(15,2) NOT NULL DEFAULT 0.00,
  `acc_ask_orders` decimal(15,2) NOT NULL DEFAULT 0.00,
  `acc_ask_volume` decimal(15,2) NOT NULL DEFAULT 0.00,
  `acc_transaction` decimal(15,2) NOT NULL DEFAULT 0.00,
  `acc_trade_volume` decimal(15,2) NOT NULL DEFAULT 0.00,
  `acc_trade_value_ntm` decimal(15,2) NOT NULL DEFAULT 0.00,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.stock 結構
CREATE TABLE IF NOT EXISTS `stock` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `security_code` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0' COMMENT '股票代碼',
  `stock_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '股票名稱',
  `isin_code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
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
) ENGINE=InnoDB AUTO_INCREMENT=417458 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='股票基本資訊';

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.stock_day 結構
CREATE TABLE IF NOT EXISTS `stock_day` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `security_code` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '股票代碼',
  `traded_day` int(11) NOT NULL COMMENT 'yyyyMMdd',
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
  `date` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `security_code_traded_day` (`security_code`,`traded_day`),
  KEY `closing_price` (`closing_price`),
  KEY `traded_day` (`traded_day`),
  KEY `security_code` (`security_code`),
  KEY `change` (`change`)
) ENGINE=InnoDB AUTO_INCREMENT=7543638 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='盤後 日交易量';

-- 取消選取資料匯出。
-- 傾印  表格 stock_tw.yearly_trading_summary 結構
CREATE TABLE IF NOT EXISTS `yearly_trading_summary` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `year` int(11) DEFAULT NULL,
  `trade_volume` decimal(15,2) DEFAULT NULL,
  `trade_value` decimal(15,2) DEFAULT NULL,
  `transaction` decimal(15,2) DEFAULT NULL,
  `highest_price` decimal(15,2) DEFAULT NULL,
  `date_highest_price` int(11) NOT NULL,
  `lowest_price` decimal(15,2) DEFAULT NULL,
  `date_lowest_price` int(11) NOT NULL,
  `average_closing_price` decimal(15,2) DEFAULT NULL,
  `security_code` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `year_security_code` (`year`,`security_code`)
) ENGINE=InnoDB AUTO_INCREMENT=90831 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='年度成交資訊';

-- 取消選取資料匯出。
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
