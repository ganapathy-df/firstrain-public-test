USE env8_spy_db;

DROP TABLE IF EXISTS DOC_CATEGORIZE_RESPONSE_HISTORY;

CREATE TABLE DOC_CATEGORIZE_RESPONSE_HISTORY (
  ID            					BIGINT AUTO_INCREMENT NOT NULL,
  VERSION       					INT(10) NOT NULL,
  INSERT_TIME   					TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UPDATE_TIME   					TIMESTAMP NOT NULL DEFAULT '1971-01-01 01:00:00',
  DOC_ID  							BIGINT NOT NULL,
  TAXONOMY_DIRECTIVE 				VARCHAR(120),
  FR_CATEGORIZE_SERVICE_RESPONSE 	TEXT,
  ACTUAL_RESPONSE 					TEXT,
  PRIMARY KEY (ID)
) ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE DOC_CATEGORIZE_FEEDBACK (
  ID            					BIGINT AUTO_INCREMENT NOT NULL,
  VERSION       					INT(10) NOT NULL,
  INSERT_TIME   					TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UPDATE_TIME   					TIMESTAMP NOT NULL DEFAULT '1971-01-01 01:00:00',
  FEEDBACK 							TEXT,
  PRIMARY KEY (ID)
) ENGINE = INNODB DEFAULT CHARSET=utf8;

CREATE TABLE `private_entity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `search_token` varchar(250) NOT NULL,
  `name` varchar(200) NOT NULL,
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `last_promoted` timestamp DEFAULT NULL,
  `id_dev` bigint(20) NOT NULL,
  `definition_dev` text NOT NULL,
  `id_live` bigint(20) DEFAULT NULL,
  `definition_live` text,
  `status` varchar(10) NOT NULL,
  `user` varchar(200) NOT NULL,
  `email` varchar(200) DEFAULT NULL,
  `brand` varchar(200) NOT NULL,
  `node` varchar(200) DEFAULT NULL,
  `enterprise_id` varchar(200) NOT NULL,
  `version` int(10) NOT NULL,
  PRIMARY KEY (`search_token`),
  KEY `id` (`id`),
  KEY `idx_updateTime` (`update_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

CREATE TABLE `auto_increment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(50) DEFAULT NULL,
  `version` int(20) NOT NULL,
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2000000000 DEFAULT CHARSET=utf8;

CREATE TABLE `entity_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `search_token` varchar(250) NOT NULL,
  `name` varchar(200) NOT NULL,
  `insert_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `id_dev` bigint(20) NOT NULL,
  `definition_dev` text NOT NULL,
  `id_live` bigint(20) DEFAULT NULL,
  `definition_live` text,
  `status` varchar(10) NOT NULL,
  `user` varchar(200) NOT NULL,
  `email` varchar(200) DEFAULT NULL,
  `version` int(20) NOT NULL,
  KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `entity_backtest` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `SEARCH_TOKEN` varchar(250) NOT NULL,
  `CAT_ID` bigint(20) NOT NULL,
  `DEFINITION` text NOT NULL,
  `STATUS` varchar(100) NOT NULL,
  `DOC_JSON` text,
  `RESULT_JSON` text,
  `MATCH_POOL` int(11) DEFAULT NULL,
  `MATCHED_DOCS` int(11) DEFAULT NULL,
  `INSERT_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `COMMENTS` varchar(200) DEFAULT NULL,
  `VERSION` int(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `CAT_ID` (`CAT_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
