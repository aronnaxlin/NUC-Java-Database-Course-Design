-- 智慧物业管理系统 数据库表结构

CREATE DATABASE IF NOT EXISTS property_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE property_management;

DROP TABLE IF EXISTS utility_cards;
DROP TABLE IF EXISTS fees;
DROP TABLE IF EXISTS properties;
DROP TABLE IF EXISTS users;

-- 1. 用户表 (系统账号 + 业主档案)
-- 合并优化: 将原 Owner 表信息合并至此，消除传递依赖
CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    user_name VARCHAR(50) NOT NULL COMMENT '登录账号',
    password VARCHAR(100) NOT NULL COMMENT '加密后的密码',
    user_type VARCHAR(20) NOT NULL COMMENT '用户类型: ADMIN, OWNER',

    -- 业主档案字段 (Merge from owners)
    name VARCHAR(50) COMMENT '真实姓名',
    gender VARCHAR(10) COMMENT '性别',
    phone VARCHAR(20) UNIQUE COMMENT '联系电话',

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_name (user_name),
    -- 数据约束
    CONSTRAINT chk_user_type CHECK (user_type IN ('ADMIN', 'OWNER')),
    CONSTRAINT chk_gender CHECK (gender IN ('Male', 'Female', ''))
) DEFAULT CHARSET=utf8mb4 COMMENT='系统用户与业主档案表';

-- 2. 房产信息表
CREATE TABLE properties (
    p_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    building_no VARCHAR(20) NOT NULL COMMENT '楼栋号',
    unit_no VARCHAR(20) NOT NULL COMMENT '单元号',
    room_no VARCHAR(20) NOT NULL COMMENT '房间号',
    area DECIMAL(10, 2) COMMENT '建筑面积(平方米)',
    p_status VARCHAR(20) DEFAULT 'UNSOLD' COMMENT '房屋状态: SOLD-已售, UNSOLD-未售, RENTED-已租',

    -- 直接关联用户表 (Flattened Hierarchy)
    user_id BIGINT COMMENT '关联业主(用户)ID',
    CONSTRAINT fk_property_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,

    UNIQUE KEY uk_house_code (building_no, unit_no, room_no),
    CONSTRAINT chk_p_status CHECK (p_status IN ('SOLD', 'UNSOLD', 'RENTED'))
) DEFAULT CHARSET=utf8mb4 COMMENT='房产资源表';

-- 3. 费用账单表
CREATE TABLE fees (
    f_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    p_id BIGINT NOT NULL COMMENT '关联房产ID',
    fee_type VARCHAR(50) NOT NULL COMMENT '费用类型: PROPERTY_FEE-物业费, HEATING_FEE-取暖费',
    amount DECIMAL(10, 2) NOT NULL COMMENT '账单金额',
    is_paid TINYINT(1) DEFAULT 0 COMMENT '缴费状态: 0-未缴, 1-已缴',
    pay_date DATETIME COMMENT '缴费时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '账单生成时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '状态更新时间',
    CONSTRAINT fk_fee_property FOREIGN KEY (p_id) REFERENCES properties(p_id) ON DELETE CASCADE,
    -- 核心性能优化: 联合索引用于快速检测欠费 (Hard Interception)
    INDEX idx_check_arrears (p_id, is_paid),
    CONSTRAINT chk_fee_type CHECK (fee_type IN ('PROPERTY_FEE', 'HEATING_FEE')),
    CONSTRAINT chk_is_paid CHECK (is_paid IN (0, 1))
) DEFAULT CHARSET=utf8mb4 COMMENT='物业缴费账单表';

-- 4. 水电卡表
CREATE TABLE utility_cards (
    card_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    p_id BIGINT NOT NULL COMMENT '关联房产ID',
    card_type VARCHAR(20) NOT NULL COMMENT '卡片类型: WATER-水卡, ELECTRICITY-电卡',
    balance DECIMAL(10, 2) DEFAULT 0.00 COMMENT '卡内余额',
    last_topup DATETIME COMMENT '最后充值时间',
    CONSTRAINT fk_card_property FOREIGN KEY (p_id) REFERENCES properties(p_id) ON DELETE CASCADE,
    UNIQUE KEY uk_card_property_type (p_id, card_type),
    CONSTRAINT chk_card_type CHECK (card_type IN ('WATER', 'ELECTRICITY'))
) DEFAULT CHARSET=utf8mb4 COMMENT='水电卡自助管理表';

CREATE USER 'propertyAdmin'@'%' IDENTIFIED BY 'realAronnaxlin917-';
GRANT ALL PRIVILEGES ON property_management.* TO 'propertyAdmin'@'%';
FLUSH PRIVILEGES;