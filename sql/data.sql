-- 智慧物业管理系统 预设测试数据

USE property_management;

-- 1. 填充用户数据 (包含业主档案)
-- 这一步将自动生成 4 个用户 (ID 1-4)
-- 默认密码: 123456
INSERT INTO users (user_name, password, user_type, name, gender, phone) VALUES
('admin', '123456', 'ADMIN', '系统管理员', 'Male', '13800000000'),
('owner_liang', '123456', 'OWNER', '梁朝伟', 'Male', '13800000001'),
('owner_zhang', '123456', 'OWNER', '张曼玉', 'Female', '13800000002'),
('owner_lau', '123456', 'OWNER', '刘嘉玲', 'Female', '13800000003');

-- 2. 填充房产数据
-- 关联上述生成的用户ID (2, 3, 4)
INSERT INTO properties (building_no, unit_no, room_no, area, p_status, user_id) VALUES
('A1', '1', '101', 89.5, 'SOLD', 2),  -- 梁朝伟
('A1', '1', '102', 120.0, 'SOLD', 3), -- 张曼玉
('A2', '2', '201', 95.0, 'SOLD', 4),  -- 刘嘉玲
('B1', '1', '301', 110.0, 'UNSOLD', NULL);

-- 3. 填充账单数据 (所有均为已缴费状态，模拟健康数据)
INSERT INTO fees (p_id, fee_type, amount, is_paid, pay_date) VALUES
-- p_id=2 (梁朝伟): 物业费+取暖费 全清
(1, 'PROPERTY_FEE', 268.50, 1, '2025-12-01 10:00:00'),
(1, 'HEATING_FEE', 1500.00, 1, '2025-12-01 10:05:00'),

-- p_id=3 (张曼玉): 物业费+取暖费 全清
(2, 'PROPERTY_FEE', 360.00, 1, '2025-12-05 14:30:00'),
(2, 'HEATING_FEE', 1800.00, 1, '2025-12-05 14:30:00'),

-- p_id=4 (刘嘉玲): 物业费 已缴 (无需缴纳取暖费)
(3, 'PROPERTY_FEE', 285.00, 1, '2025-12-10 09:20:00');

-- 4. 填充水电卡数据 (状态正常)
INSERT INTO utility_cards (p_id, card_type, balance, last_topup) VALUES
-- 梁朝伟
(1, 'WATER', 50.00, '2025-11-20 09:00:00'),
(1, 'ELECTRICITY', 100.00, '2025-11-20 09:00:00'),
-- 张曼玉
(2, 'WATER', 200.00, '2025-12-05 15:00:00'),
(2, 'ELECTRICITY', 300.00, '2025-12-05 15:00:00'),
-- 刘嘉玲
(3, 'WATER', 10.00, '2025-11-15 16:20:00');
