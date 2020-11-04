/*
Navicat MySQL Data Transfer

Source Server         : Locman3测试环境
Source Server Version : 50637
Source Host           : 193.168.0.91:3306
Source Database       : locman

Target Server Type    : MYSQL
Target Server Version : 50637
File Encoding         : 65001

Date: 2019-03-25 14:43:52
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for Alarm_Order
-- ----------------------------
DROP TABLE IF EXISTS `Alarm_Order`;
CREATE TABLE `Alarm_Order` (
  `id` varchar(32) NOT NULL,
  `alarmId` varchar(255) NOT NULL COMMENT '告警Id',
  `alarmOrderId` varchar(255) NOT NULL COMMENT '告警工单Id',
  PRIMARY KEY (`id`),
  KEY `alarmOrderId_index` (`alarmOrderId`),
  KEY `alarmId_index` (`alarmId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for AlarmInfo
-- ----------------------------
DROP TABLE IF EXISTS `AlarmInfo`;
CREATE TABLE `AlarmInfo` (
  `id` varchar(36) NOT NULL,
  `facilitiesId` varchar(36) DEFAULT NULL COMMENT '设施ID',
  `deviceId` varchar(36) DEFAULT NULL COMMENT '告警设备ID',
  `alarmTime` varchar(20) DEFAULT NULL COMMENT '告警时间',
  `serialNum` bigint(36) DEFAULT NULL COMMENT '告警流水号',
  `reportTime` varchar(20) DEFAULT NULL COMMENT '状态更新时间',
  `facilitiesTypeId` varchar(36) DEFAULT NULL COMMENT '设施类型ID',
  `alarmDesc` varchar(255) DEFAULT NULL COMMENT '告警描述',
  `rule` text,
  `alarmLevel` int(11) DEFAULT NULL COMMENT '告警等级 1：紧急 2：一般',
  `isMatchOrder` bit(1) DEFAULT NULL COMMENT '是否匹配工单',
  `accessSecret` varchar(64) DEFAULT NULL COMMENT '接入方秘钥',
  `isDel` int(11) DEFAULT '1' COMMENT '0已生成工单，1未处理，2已忽略 3正常告警流程处理完成 4 告警转故障处理完成',
  `alarmItem` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id_index` (`id`),
  KEY `accessSecret_index` (`accessSecret`),
  KEY `facilitiesId_index` (`facilitiesId`),
  KEY `deviceId_index` (`deviceId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for AlarmOrder
-- ----------------------------
DROP TABLE IF EXISTS `AlarmOrder`;
CREATE TABLE `AlarmOrder` (
  `id` varchar(32) NOT NULL COMMENT '主键ID',
  `serialNum` bigint(20) DEFAULT NULL COMMENT '告警流水单号',
  `alarmId` varchar(32) DEFAULT NULL COMMENT '告警信息Id(该告警工单对应的最新告警信息Id,告警工单更新和告警工单统计使用该字段)',
  `processId` varchar(32) DEFAULT NULL COMMENT '流程id(工作流用)',
  `accessSecret` varchar(36) DEFAULT NULL COMMENT '接入方秘钥',
  `processState` varchar(32) DEFAULT NULL COMMENT '流程状态（工单状态）0.处理中1.转故障审批中2.转故障被拒绝3.转故障已完成4.已完成5.待处理',
  `userId` varchar(32) DEFAULT NULL COMMENT '工单发起用户id',
  `userName` varchar(32) DEFAULT NULL COMMENT '工单发起用户姓名',
  `phone` varchar(16) DEFAULT NULL COMMENT '工单发起用户电话',
  `createTime` varchar(20) DEFAULT NULL,
  `faultOrderId` varchar(32) DEFAULT NULL,
  `alarmOrderStateTypeId` varchar(255) DEFAULT NULL COMMENT '工单完成or无法修复状态',
  `receiveTime` varchar(32) DEFAULT NULL COMMENT '接受工单时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for AlarmOrderStateType
-- ----------------------------
DROP TABLE IF EXISTS `AlarmOrderStateType`;
CREATE TABLE `AlarmOrderStateType` (
  `id` varchar(32) NOT NULL,
  `sign` varchar(32) DEFAULT NULL,
  `name` varchar(32) DEFAULT NULL,
  `type` varchar(32) DEFAULT NULL COMMENT '页面下拉框类型(1.告警工单列表，流程状态检索条件值 ， 2.告警工单列表，点击完成处理，显示工单类型下拉框值。 3.告警工单列表，点击无法修复，显示问题类型下拉框值。.',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='告警工单状态类型表';

-- ----------------------------
-- Table structure for AlarmRule
-- ----------------------------
DROP TABLE IF EXISTS `AlarmRule`;
CREATE TABLE `AlarmRule` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `oderNum` int(11) DEFAULT NULL COMMENT '序号',
  `ruleName` varchar(255) DEFAULT '' COMMENT '规则名称',
  `deviceTypeId` varchar(32) DEFAULT NULL COMMENT '设备类型id',
  `userId` varchar(32) DEFAULT NULL COMMENT '配置人',
  `crateTime` varchar(255) DEFAULT NULL COMMENT '配置时间',
  `updateTime` varchar(255) DEFAULT NULL COMMENT '修改时间',
  `remark` varchar(255) DEFAULT '' COMMENT '备注',
  `manageState` varchar(20) DEFAULT NULL COMMENT '规则状态',
  `publishState` varchar(20) DEFAULT NULL COMMENT '发布状态',
  `ruleContent` varchar(255) DEFAULT NULL COMMENT '规则内容（原始）',
  `rule` longtext COMMENT '规则',
  `accessSecret` varchar(64) DEFAULT NULL COMMENT '接入方秘钥',
  `isDelete` varchar(32) DEFAULT NULL COMMENT '是否删除',
  `alarmLevel` int(11) DEFAULT NULL COMMENT '告警等级',
  `isMatchOrder` bit(1) DEFAULT b'0',
  `deviceCount` int(11) DEFAULT NULL,
  `ruleType` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id_index` (`id`),
  KEY `deviceTypeId_index` (`deviceTypeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for Area
-- ----------------------------
DROP TABLE IF EXISTS `Area`;
CREATE TABLE `Area` (
  `id` varchar(36) NOT NULL,
  `areaCode` varchar(32) DEFAULT NULL,
  `areaName` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `areaCode_index` (`areaCode`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for BalanceSwitchPowers
-- ----------------------------
DROP TABLE IF EXISTS `BalanceSwitchPowers`;
CREATE TABLE `BalanceSwitchPowers` (
  `id` varchar(64) NOT NULL,
  `facilityTypeId` varchar(64) DEFAULT NULL,
  `organizationId` varchar(64) DEFAULT NULL,
  `postId` varchar(64) DEFAULT NULL COMMENT '岗位',
  `staffType` varchar(32) DEFAULT NULL COMMENT '人员类型',
  `startTime` varchar(32) DEFAULT NULL COMMENT '起始时间',
  `endTime` varchar(32) DEFAULT NULL COMMENT '结束时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `accessSecret` varchar(64) DEFAULT NULL,
  `manageState` varchar(32) DEFAULT NULL COMMENT '管理状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for BalanceSwitchStateRecord
-- ----------------------------
DROP TABLE IF EXISTS `BalanceSwitchStateRecord`;
CREATE TABLE `BalanceSwitchStateRecord` (
  `id` varchar(64) NOT NULL,
  `deviceId` varchar(64) DEFAULT NULL,
  `facilityId` varchar(64) DEFAULT NULL,
  `deviceTypeId` varchar(64) DEFAULT NULL,
  `operationTime` varchar(32) DEFAULT NULL COMMENT '平衡告警开关开启关闭操作时间',
  `accessSecret` varchar(64) DEFAULT NULL,
  `state` varchar(32) DEFAULT NULL COMMENT '管理状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for Base_AlarmRule
-- ----------------------------
DROP TABLE IF EXISTS `Base_AlarmRule`;
CREATE TABLE `Base_AlarmRule` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `oderNum` bigint(20) DEFAULT NULL COMMENT '序号',
  `ruleName` varchar(255) DEFAULT '' COMMENT '规则名称',
  `deviceTypeId` varchar(32) DEFAULT NULL COMMENT '设备类型id',
  `userId` varchar(32) DEFAULT NULL COMMENT '配置人',
  `crateTime` varchar(255) DEFAULT NULL COMMENT '配置时间',
  `updateTime` varchar(255) DEFAULT NULL COMMENT '修改时间',
  `remark` varchar(255) DEFAULT '' COMMENT '备注',
  `manageState` varchar(20) DEFAULT NULL COMMENT '规则状态',
  `publishState` varchar(20) DEFAULT NULL COMMENT '发布状态',
  `ruleContent` varchar(255) DEFAULT NULL COMMENT '规则内容（原始）',
  `rule` longtext COMMENT '规则',
  `accessSecret` varchar(64) DEFAULT NULL COMMENT '接入方秘钥',
  `isDelete` varchar(32) DEFAULT NULL COMMENT '是否删除',
  `alarmLevel` int(11) DEFAULT NULL COMMENT '告警等级',
  `isMatchOrder` bit(1) DEFAULT b'0',
  `deviceCount` int(11) DEFAULT NULL COMMENT '设备数量',
  `ruleType` varchar(32) DEFAULT NULL COMMENT '规则类型(1、自定义规则 2 通用规则)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='告警规则';

-- ----------------------------
-- Table structure for Base_DeviceInfoConvert
-- ----------------------------
DROP TABLE IF EXISTS `Base_DeviceInfoConvert`;
CREATE TABLE `Base_DeviceInfoConvert` (
  `id` varchar(255) NOT NULL,
  `dicKey` varchar(255) NOT NULL COMMENT '字典key',
  `dicValue` varchar(255) NOT NULL COMMENT '字典value',
  `createTime` varchar(255) DEFAULT NULL COMMENT '创建时间',
  `updateTime` varchar(255) DEFAULT NULL COMMENT '更新时间',
  `accessSecret` varchar(255) DEFAULT NULL COMMENT '接入方密钥',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for Base_DeviceProperties
-- ----------------------------
DROP TABLE IF EXISTS `Base_DeviceProperties`;
CREATE TABLE `Base_DeviceProperties` (
  `id` varchar(32) NOT NULL COMMENT '设备属性表',
  `devicePropertiesName` varchar(255) DEFAULT NULL COMMENT '设备属性名',
  `devicePropertiesSign` varchar(255) DEFAULT NULL COMMENT '设备属性标识',
  `dataType` varchar(255) DEFAULT NULL COMMENT '数据类型',
  `dataValue` varchar(255) DEFAULT NULL COMMENT '数据值',
  `readWrite` int(10) DEFAULT NULL COMMENT '读写类型',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `creationTime` varchar(255) DEFAULT NULL COMMENT '创建时间',
  `templateId` varchar(255) DEFAULT NULL COMMENT '模版id',
  `orderNo` int(10) DEFAULT NULL COMMENT '排序序号',
  `appIcon` varchar(255) DEFAULT NULL COMMENT 'app图标',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备属性表';

-- ----------------------------
-- Table structure for Base_DeviceProperties_Template
-- ----------------------------
DROP TABLE IF EXISTS `Base_DeviceProperties_Template`;
CREATE TABLE `Base_DeviceProperties_Template` (
  `id` varchar(32) NOT NULL,
  `templateName` varchar(32) DEFAULT NULL COMMENT '模版名称',
  `creationTime` varchar(20) DEFAULT NULL COMMENT '创建时间',
  `editorTime` varchar(20) DEFAULT NULL COMMENT '修改时间',
  `manageState` varchar(20) DEFAULT NULL COMMENT '管理状态',
  `accessSecret` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备属性模板管理';

-- ----------------------------
-- Table structure for Base_DeviceType_Template
-- ----------------------------
DROP TABLE IF EXISTS `Base_DeviceType_Template`;
CREATE TABLE `Base_DeviceType_Template` (
  `id` varchar(32) NOT NULL,
  `deviceTypePropertyConfigId` varchar(32) DEFAULT NULL COMMENT '设备类型Id',
  `devicePropertyTemplateId` varchar(32) DEFAULT NULL COMMENT '设备属性模板Id',
  `accessSecret` varchar(255) DEFAULT NULL COMMENT '接入方秘钥',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备类型与设备属性模版中间表';

-- ----------------------------
-- Table structure for Base_FacilitiesType
-- ----------------------------
DROP TABLE IF EXISTS `Base_FacilitiesType`;
CREATE TABLE `Base_FacilitiesType` (
  `id` varchar(32) NOT NULL COMMENT '设施类型ID',
  `facilityTypeBaseId` varchar(32) NOT NULL COMMENT '基础设施类型id',
  `facilityTypeAlias` varchar(64) NOT NULL COMMENT '设施类型名称',
  `accessSecret` varchar(64) NOT NULL COMMENT '接入方密钥',
  `manageState` varchar(32) DEFAULT NULL COMMENT '设施类型管理状态',
  `creationUserId` varchar(36) DEFAULT NULL COMMENT '创建人id',
  `creationTime` varchar(20) DEFAULT NULL COMMENT '创建时间',
  `editorUserId` varchar(36) DEFAULT NULL COMMENT '修改人id',
  `editorTime` varchar(20) DEFAULT NULL COMMENT '修改时间',
  `remark` varchar(64) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设施类型表(模板)';

-- ----------------------------
-- Table structure for Base_FacilitiesTypeBase
-- ----------------------------
DROP TABLE IF EXISTS `Base_FacilitiesTypeBase`;
CREATE TABLE `Base_FacilitiesTypeBase` (
  `id` varchar(32) NOT NULL COMMENT '基础设施类型ID',
  `facilityTypeName` varchar(64) DEFAULT NULL COMMENT '基础设施类型名称',
  `facilityTypeIco` varchar(255) DEFAULT NULL COMMENT '基础设施类型图标',
  `manageState` varchar(32) DEFAULT NULL COMMENT '管理状态(enable/disable:启用/停用)',
  `createTime` varchar(255) DEFAULT NULL COMMENT '创建时间',
  `createUser` varchar(255) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for BaseDateSynchronizationState
-- ----------------------------
DROP TABLE IF EXISTS `BaseDateSynchronizationState`;
CREATE TABLE `BaseDateSynchronizationState` (
  `id` varchar(32) NOT NULL,
  `baseAlarmRule` tinyint(1) NOT NULL COMMENT '同步告警规则,1:true:未同步,0:false:已同步',
  `baseDeviceInfoConvert` tinyint(1) NOT NULL COMMENT '同步特殊值转换,1:true:未同步,0:false:已同步',
  `baseDeviceTypeTemplate` tinyint(1) NOT NULL COMMENT '同步设备类型模板,1:true:未同步,0:false:已同步',
  `baseFacilitiesType` tinyint(1) NOT NULL COMMENT '同步设施类型,1:true:未同步,0:false:已同步',
  `accessSecret` varchar(64) NOT NULL COMMENT '接入方密钥(不可重复)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for DATABASECHANGELOG
-- ----------------------------
DROP TABLE IF EXISTS `DATABASECHANGELOG`;
CREATE TABLE `DATABASECHANGELOG` (
  `ID` varchar(255) NOT NULL,
  `AUTHOR` varchar(255) NOT NULL,
  `FILENAME` varchar(255) NOT NULL,
  `DATEEXECUTED` datetime NOT NULL,
  `ORDEREXECUTED` int(11) NOT NULL,
  `EXECTYPE` varchar(10) NOT NULL,
  `MD5SUM` varchar(35) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `COMMENTS` varchar(255) DEFAULT NULL,
  `TAG` varchar(255) DEFAULT NULL,
  `LIQUIBASE` varchar(20) DEFAULT NULL,
  `CONTEXTS` varchar(255) DEFAULT NULL,
  `LABELS` varchar(255) DEFAULT NULL,
  `DEPLOYMENT_ID` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for DATABASECHANGELOGLOCK
-- ----------------------------
DROP TABLE IF EXISTS `DATABASECHANGELOGLOCK`;
CREATE TABLE `DATABASECHANGELOGLOCK` (
  `ID` int(11) NOT NULL,
  `LOCKED` bit(1) NOT NULL,
  `LOCKGRANTED` datetime DEFAULT NULL,
  `LOCKEDBY` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for DefinedAlarmRule_Device
-- ----------------------------
DROP TABLE IF EXISTS `DefinedAlarmRule_Device`;
CREATE TABLE `DefinedAlarmRule_Device` (
  `id` varchar(32) NOT NULL COMMENT '主键id',
  `deviceId` varchar(255) DEFAULT NULL COMMENT '设备id',
  `alarmRuleId` varchar(255) DEFAULT NULL COMMENT '自定义告警规则ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='自定义规则和设备绑定关系表设备';

-- ----------------------------
-- Table structure for Device
-- ----------------------------
DROP TABLE IF EXISTS `Device`;
CREATE TABLE `Device` (
  `id` varchar(41) NOT NULL DEFAULT '',
  `deviceName` varchar(36) DEFAULT NULL COMMENT '设备名称',
  `deviceKey` varchar(36) DEFAULT NULL COMMENT '设备秘钥',
  `protocolType` varchar(36) DEFAULT NULL COMMENT '协议类型',
  `openProtocols` varchar(36) DEFAULT NULL COMMENT '开放/私有协议',
  `deviceType` varchar(36) DEFAULT NULL COMMENT '设备类型id',
  `appTag` varchar(128) DEFAULT NULL,
  `manageState` varchar(36) DEFAULT NULL COMMENT '设备有效状态',
  `accessSecret` varchar(64) NOT NULL COMMENT '接入方秘钥',
  `gatewayId` varchar(64) DEFAULT NULL COMMENT '设备网关id',
  `deviceDefendState` varchar(32) NOT NULL DEFAULT 'normal' COMMENT '表示设备状态，有故障工单时，该设备故障，默认值正常（normal/fault）',
  `subDeviceId` varchar(32) NOT NULL DEFAULT '' COMMENT '子设备id',
  PRIMARY KEY (`id`),
  KEY `accessSecret_index` (`accessSecret`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for DEVICE_INFO
-- ----------------------------
DROP TABLE IF EXISTS `DEVICE_INFO`;
CREATE TABLE `DEVICE_INFO` (
  `ID` varchar(36) NOT NULL,
  `DEVICECODE` varchar(64) DEFAULT NULL,
  `DEVICENAME` varchar(64) DEFAULT NULL,
  `DEVICETYPEID` varchar(36) DEFAULT NULL,
  `DEVICETYPENAME` varchar(64) DEFAULT NULL,
  `FACILITIESID` varchar(36) DEFAULT NULL,
  `ONLINESTATE` int(1) DEFAULT NULL,
  `STATE` int(1) DEFAULT NULL,
  `FACTORYID` varchar(36) DEFAULT NULL,
  `ONLINETIME` datetime(6) DEFAULT NULL,
  `ONLYCODE` varchar(64) DEFAULT NULL,
  `COMPANYID` varchar(36) DEFAULT NULL,
  `REPORTSTATUS` int(1) DEFAULT NULL COMMENT '?????0?????1?????',
  `SERIALNUMBERX` varchar(20) DEFAULT NULL,
  `LOCKIDX` varchar(20) DEFAULT NULL,
  `MOBILEX` varchar(20) DEFAULT NULL,
  `STARTTIMEX` datetime(6) DEFAULT NULL,
  `ENDTIMEX` datetime(6) DEFAULT NULL,
  `IDENTIFYCODEX` varchar(15) DEFAULT NULL,
  `FACTORYFLAG` varchar(4) DEFAULT NULL COMMENT '????',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='??';

-- ----------------------------
-- Table structure for Device_Job
-- ----------------------------
DROP TABLE IF EXISTS `Device_Job`;
CREATE TABLE `Device_Job` (
  `id` varchar(32) NOT NULL,
  `deviceId` varchar(64) NOT NULL COMMENT '设备id',
  `jobId` varchar(64) NOT NULL COMMENT '定时器id',
  `item` varchar(64) NOT NULL COMMENT '下发命令开启的key',
  `openTime` varchar(64) NOT NULL COMMENT '下发命令开启时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for Device_RealRported
-- ----------------------------
DROP TABLE IF EXISTS `Device_RealRported`;
CREATE TABLE `Device_RealRported` (
  `id` varchar(64) NOT NULL,
  `deviceId` varchar(64) NOT NULL COMMENT '设备ID',
  `device_bv` varchar(64) DEFAULT NULL,
  `device_sig` varchar(64) DEFAULT NULL,
  `device_rsrp` varchar(64) DEFAULT NULL,
  `device_sinr` varchar(64) DEFAULT NULL,
  `device_ls` varchar(64) DEFAULT NULL,
  `lastReportTime` varchar(32) DEFAULT NULL COMMENT '设备最新上报时间',
  `onLineState` varchar(20) DEFAULT NULL COMMENT '设备在线离线状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for DeviceDataStorage
-- ----------------------------
DROP TABLE IF EXISTS `DeviceDataStorage`;
CREATE TABLE `DeviceDataStorage` (
  `id` varchar(32) NOT NULL,
  `deviceId` varchar(64) NOT NULL COMMENT '工程版中设备ID',
  `deviceNumber` varchar(64) NOT NULL COMMENT '设备硬件编号',
  `bluetooth` varchar(64) NOT NULL COMMENT '设备蓝牙名称',
  `deviceAddress` varchar(255) NOT NULL COMMENT '设备安装地址',
  `longitude` varchar(64) NOT NULL COMMENT '设备安装经度',
  `latitude` varchar(64) NOT NULL COMMENT '设备安装纬度',
  `ipPort` varchar(255) NOT NULL COMMENT '设备上报地址',
  `serialNumber` varchar(64) NOT NULL COMMENT '序列号（井盖序列号、井盖铭牌号）',
  `status` varchar(64) NOT NULL COMMENT '设施设备状态（survey：勘测、project：安装）',
  `properties` text NOT NULL COMMENT '扩展属性，JSON字符串',
  `deviceTypeId` varchar(64) NOT NULL COMMENT '设备/设施类型ID',
  `synchronizationState` varchar(64) NOT NULL COMMENT '同步状态',
  `areaId` varchar(64) NOT NULL COMMENT '区域id',
  `errorInfo` text NOT NULL COMMENT '同步错误信息',
  `showExtend` text NOT NULL COMMENT '解析扩展属性与locman一致',
  `extend` text NOT NULL COMMENT '解析扩展属性与locman一致',
  `createTime` varchar(32) NOT NULL DEFAULT '' COMMENT '创建时间',
  `updateTime` varchar(32) NOT NULL DEFAULT '' COMMENT '修改时间',
  `synchTime` varchar(32) NOT NULL DEFAULT '' COMMENT '同步时间',
  `updateBy` varchar(32) NOT NULL DEFAULT '' COMMENT '修改人',
  `synchBy` varchar(32) NOT NULL DEFAULT '' COMMENT '同步人',
  `isDelete` varchar(32) NOT NULL DEFAULT '' COMMENT '数据是否被删除,被删除时存删除人id ,默认为空',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for DeviceInfoConvert
-- ----------------------------
DROP TABLE IF EXISTS `DeviceInfoConvert`;
CREATE TABLE `DeviceInfoConvert` (
  `id` varchar(255) NOT NULL,
  `dicKey` varchar(255) NOT NULL COMMENT '字典key',
  `dicValue` varchar(255) NOT NULL COMMENT '字典value',
  `createTime` varchar(255) NOT NULL COMMENT '创建时间',
  `updateTime` varchar(255) NOT NULL COMMENT '更新时间',
  `accessSecret` varchar(255) NOT NULL COMMENT '接入方密钥',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for DeviceProperties
-- ----------------------------
DROP TABLE IF EXISTS `DeviceProperties`;
CREATE TABLE `DeviceProperties` (
  `id` varchar(32) NOT NULL,
  `devicePropertiesName` varchar(255) DEFAULT NULL COMMENT '设备属性名',
  `devicePropertiesSign` varchar(255) DEFAULT NULL COMMENT '设备属性标识',
  `dataType` varchar(255) DEFAULT NULL COMMENT '数据类型',
  `dataValue` varchar(255) DEFAULT NULL COMMENT '数据值',
  `readWrite` int(10) DEFAULT NULL COMMENT '读写类型',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `creationTime` varchar(255) DEFAULT NULL COMMENT '创建时间',
  `templateId` varchar(255) DEFAULT NULL COMMENT '模版id',
  `orderNo` int(10) DEFAULT NULL COMMENT '排序序号',
  `appIcon` varchar(255) DEFAULT NULL COMMENT 'app图标',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备属性表';

-- ----------------------------
-- Table structure for DeviceProperties_Template
-- ----------------------------
DROP TABLE IF EXISTS `DeviceProperties_Template`;
CREATE TABLE `DeviceProperties_Template` (
  `id` varchar(32) NOT NULL,
  `templateName` varchar(32) DEFAULT NULL COMMENT '模版名称',
  `creationTime` varchar(20) DEFAULT NULL COMMENT '创建时间',
  `editorTime` varchar(20) DEFAULT NULL COMMENT '修改时间',
  `manageState` varchar(20) DEFAULT NULL COMMENT '管理状态',
  `accessSecret` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备属性模板管理';

-- ----------------------------
-- Table structure for DeviceStateHistory
-- ----------------------------
DROP TABLE IF EXISTS `DeviceStateHistory`;
CREATE TABLE `DeviceStateHistory` (
  `id` varchar(32) NOT NULL,
  `deviceId` varchar(32) DEFAULT NULL COMMENT '设备id',
  `datas` text COMMENT '状态数据',
  `reportTime` varchar(32) DEFAULT NULL COMMENT '上报时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for DeviceType
-- ----------------------------
DROP TABLE IF EXISTS `DeviceType`;
CREATE TABLE `DeviceType` (
  `id` varchar(32) NOT NULL,
  `deviceTypeName` varchar(255) NOT NULL COMMENT '设备类型名称',
  `parentId` varchar(32) DEFAULT NULL COMMENT '父类型id',
  `createTime` varchar(255) DEFAULT NULL COMMENT '创建时间',
  `updateTime` varchar(255) DEFAULT NULL COMMENT '修改时间',
  `createBy` varchar(255) DEFAULT NULL COMMENT '创建人',
  `updateBy` varchar(255) DEFAULT NULL COMMENT '修改人',
  `accessSecret` varchar(64) DEFAULT NULL COMMENT '接入方密钥',
  `typeSign` varchar(64) DEFAULT NULL COMMENT '设备类型标记\r\n设备类型标记 ---区分该设备类型是否属于 \r\n1.一体化智能监控终端\r\n2.智能监测终端（II型）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for DeviceType_Template
-- ----------------------------
DROP TABLE IF EXISTS `DeviceType_Template`;
CREATE TABLE `DeviceType_Template` (
  `id` varchar(32) NOT NULL,
  `deviceTypePropertyConfigId` varchar(32) DEFAULT NULL COMMENT '设备类型Id',
  `devicePropertyTemplateId` varchar(32) DEFAULT NULL COMMENT '设备属性模板Id',
  `accessSecret` varchar(255) DEFAULT NULL COMMENT '接入方秘钥',
  PRIMARY KEY (`id`),
  KEY `devicePropertyTemplateId_index` (`devicePropertyTemplateId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备类型与设备属性模版中间表';

-- ----------------------------
-- Table structure for DistributionPowers
-- ----------------------------
DROP TABLE IF EXISTS `DistributionPowers`;
CREATE TABLE `DistributionPowers` (
  `id` varchar(32) NOT NULL COMMENT '主键ID',
  `facilityTypeId` varchar(32) DEFAULT NULL COMMENT '设施类型ID',
  `startTime` varchar(32) DEFAULT NULL COMMENT '开始时间',
  `endTime` varchar(32) DEFAULT NULL COMMENT '结束时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `accessSecret` varchar(64) DEFAULT NULL COMMENT '接入方秘钥',
  `manageState` varchar(10) DEFAULT NULL COMMENT '管理状态',
  `hour` varchar(32) DEFAULT NULL COMMENT '超时未关所配小时',
  `minute` varchar(32) DEFAULT NULL COMMENT '超时未关所配分钟',
  `powerName` varchar(32) DEFAULT NULL COMMENT '分权分域名称',
  `userInfo` longtext COMMENT '分权分域所配权限人员信息',
  `orgName` text COMMENT '前台展示组织名称',
  `userName` text COMMENT '前台展示人员名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='分权分域表';

-- ----------------------------
-- Table structure for Drools
-- ----------------------------
DROP TABLE IF EXISTS `Drools`;
CREATE TABLE `Drools` (
  `id` varchar(255) NOT NULL COMMENT '告警规则表',
  `rule` text COMMENT '规则',
  `isDelete` varchar(255) DEFAULT NULL COMMENT '是否删除',
  `ruleName` varchar(255) DEFAULT NULL COMMENT '规则名',
  `remark` bigint(20) DEFAULT NULL COMMENT '备注字段',
  `state` varchar(255) DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Table structure for Facilities
-- ----------------------------
DROP TABLE IF EXISTS `Facilities`;
CREATE TABLE `Facilities` (
  `id` varchar(36) NOT NULL,
  `facilitiesCode` varchar(32) DEFAULT NULL,
  `facilitiesTypeId` varchar(36) DEFAULT NULL,
  `organizationId` varchar(36) DEFAULT NULL,
  `areaId` varchar(36) DEFAULT NULL,
  `longitude` varchar(32) DEFAULT NULL,
  `latitude` varchar(32) DEFAULT NULL,
  `address` varchar(512) DEFAULT NULL,
  `manageState` varchar(20) DEFAULT NULL,
  `accessSecret` varchar(64) DEFAULT NULL,
  `extend` text,
  `creationUserId` varchar(36) DEFAULT NULL,
  `creationUserName` varchar(36) DEFAULT NULL,
  `creationTime` varchar(20) DEFAULT NULL,
  `editorUserId` varchar(36) DEFAULT NULL,
  `editorUserName` varchar(36) DEFAULT NULL,
  `editorTime` varchar(20) DEFAULT NULL,
  `version` varchar(36) DEFAULT NULL,
  `showExtend` text,
  `completeAddress` varchar(255) DEFAULT NULL COMMENT '区域和位置组合在一起的完整地址',
  PRIMARY KEY (`id`),
  KEY `id_index` (`id`),
  KEY `accessSecret_index` (`accessSecret`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for FACILITIES_INFO
-- ----------------------------
DROP TABLE IF EXISTS `FACILITIES_INFO`;
CREATE TABLE `FACILITIES_INFO` (
  `ID` varchar(36) NOT NULL,
  `FACILITIESCODE` varchar(64) DEFAULT NULL,
  `FACILITIESTYPEID` varchar(36) DEFAULT NULL,
  `FACILITIESTYPENAME` varchar(64) DEFAULT NULL,
  `LONGITUDE` varchar(32) DEFAULT NULL,
  `LATITUDE` varchar(32) DEFAULT NULL,
  `AREAID` varchar(36) DEFAULT NULL,
  `AREANAME` varchar(64) DEFAULT NULL,
  `ORGANIZATIONALID` varchar(36) NOT NULL,
  `ADDRESS` text,
  `STATE` int(1) DEFAULT NULL,
  `IMPORTANTIEVEL` int(1) DEFAULT NULL,
  `CREATEID` varchar(36) NOT NULL,
  `CREATIONTIME` datetime(6) DEFAULT NULL,
  `COMPANYID` varchar(36) DEFAULT NULL,
  `CREATETIME` datetime(6) DEFAULT NULL,
  `MODIFYID` varchar(255) DEFAULT NULL,
  `MODIFYTIME` datetime(6) DEFAULT NULL,
  `ORGANIZATIONALNAME` varchar(255) DEFAULT NULL,
  `USERNAME` varchar(255) DEFAULT NULL,
  `MARKETINGCENTERID` varchar(255) DEFAULT NULL,
  `MARKETINGCENTERNAME` varchar(255) DEFAULT NULL,
  `PEREDEVICENUMBER` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='??';

-- ----------------------------
-- Table structure for FacilitiesDataType
-- ----------------------------
DROP TABLE IF EXISTS `FacilitiesDataType`;
CREATE TABLE `FacilitiesDataType` (
  `id` varchar(36) NOT NULL,
  `facilitiesTypeId` varchar(36) DEFAULT NULL,
  `dataType` varchar(20) DEFAULT NULL COMMENT 'image:图片,time:时间,radio:单选,checkbox:多选,select:下拉选择,textArea:文本域,input:文本,video:视频',
  `name` varchar(36) DEFAULT NULL,
  `sign` varchar(36) DEFAULT NULL,
  `initialValue` varchar(512) DEFAULT NULL,
  `isNotMandatory` varchar(36) DEFAULT NULL COMMENT '是否允许为空1:必填0:选填',
  `state` varchar(36) DEFAULT NULL,
  `remarks` varchar(36) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for FacilitiesType
-- ----------------------------
DROP TABLE IF EXISTS `FacilitiesType`;
CREATE TABLE `FacilitiesType` (
  `id` varchar(32) NOT NULL COMMENT '设施类型ID',
  `facilityTypeBaseId` varchar(32) DEFAULT NULL COMMENT '设施类型名称',
  `facilityTypeAlias` varchar(64) DEFAULT NULL,
  `accessSecret` varchar(64) DEFAULT NULL,
  `manageState` varchar(32) DEFAULT NULL COMMENT '设施类型管理状态',
  `creationUserId` varchar(36) DEFAULT NULL,
  `creationTime` varchar(20) DEFAULT NULL,
  `editorUserId` varchar(36) DEFAULT NULL,
  `editorTime` varchar(20) DEFAULT NULL,
  `remark` varchar(64) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `id_index` (`id`),
  KEY `facilityTypeBaseId_index` (`facilityTypeBaseId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设施基础类型';

-- ----------------------------
-- Table structure for FacilitiesTypeBase
-- ----------------------------
DROP TABLE IF EXISTS `FacilitiesTypeBase`;
CREATE TABLE `FacilitiesTypeBase` (
  `id` varchar(32) NOT NULL COMMENT '基础设施类型ID',
  `facilityTypeName` varchar(64) DEFAULT NULL,
  `facilityTypeIco` varchar(255) DEFAULT NULL,
  `manageState` varchar(32) DEFAULT NULL,
  `createTime` varchar(255) DEFAULT NULL,
  `createUser` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for Facility_Device
-- ----------------------------
DROP TABLE IF EXISTS `Facility_Device`;
CREATE TABLE `Facility_Device` (
  `id` varchar(64) NOT NULL,
  `deviceId` varchar(64) DEFAULT NULL,
  `facilityId` varchar(64) DEFAULT NULL,
  `deviceTypeId` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `facilityId_index` (`facilityId`),
  KEY `deviceId_index` (`deviceId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='设备与设施绑定关系表';

-- ----------------------------
-- Table structure for Facility_TimeoutReportConfig
-- ----------------------------
DROP TABLE IF EXISTS `Facility_TimeoutReportConfig`;
CREATE TABLE `Facility_TimeoutReportConfig` (
  `id` varchar(64) NOT NULL,
  `facilityId` varchar(64) NOT NULL COMMENT '设施id',
  `timeoutReportConfigId` varchar(64) DEFAULT NULL COMMENT '超时未上报配置id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for Factory
-- ----------------------------
DROP TABLE IF EXISTS `Factory`;
CREATE TABLE `Factory` (
  `id` varchar(64) NOT NULL,
  `factoryName` varchar(32) DEFAULT NULL COMMENT '厂家名称',
  `contacts` varchar(32) DEFAULT NULL COMMENT '联系人',
  `contactsPhone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `accessSecret` varchar(64) DEFAULT NULL,
  `manageState` varchar(10) DEFAULT NULL COMMENT '管理状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for Factory_AppTag
-- ----------------------------
DROP TABLE IF EXISTS `Factory_AppTag`;
CREATE TABLE `Factory_AppTag` (
  `id` varchar(32) NOT NULL,
  `factoryId` varchar(64) DEFAULT NULL,
  `appTag` varchar(128) DEFAULT NULL,
  `appId` varchar(64) DEFAULT NULL COMMENT '应用id',
  `appKey` varchar(64) DEFAULT NULL COMMENT '应用key',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='apptag与厂家中间表';

-- ----------------------------
-- Table structure for FaultOrder_Device
-- ----------------------------
DROP TABLE IF EXISTS `FaultOrder_Device`;
CREATE TABLE `FaultOrder_Device` (
  `id` varchar(32) NOT NULL,
  `deviceId` varchar(255) DEFAULT NULL COMMENT '设备id',
  `faultOrderId` varchar(255) DEFAULT NULL COMMENT '故障工单id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='故障工单与设备绑定关系表';

-- ----------------------------
-- Table structure for FaultOrderProcess
-- ----------------------------
DROP TABLE IF EXISTS `FaultOrderProcess`;
CREATE TABLE `FaultOrderProcess` (
  `id` varchar(32) NOT NULL COMMENT '主键id',
  `serialNumber` varchar(255) DEFAULT NULL COMMENT '故障流水号',
  `createTime` varchar(255) DEFAULT NULL COMMENT ' 申报时间',
  `createBy` varchar(255) DEFAULT NULL COMMENT '申报人',
  `faultType` int(11) DEFAULT NULL COMMENT '故障类型id',
  `mark` text COMMENT '故障描述',
  `accessSecret` varchar(255) DEFAULT NULL COMMENT '接入方秘钥',
  `phone` varchar(255) DEFAULT NULL COMMENT '联系方式',
  `deviceCount` int(11) DEFAULT NULL COMMENT '已绑定的设备数量',
  `manager` varchar(255) DEFAULT NULL COMMENT '申报人',
  `processState` varchar(255) DEFAULT NULL COMMENT '流程状态',
  `processId` varchar(255) DEFAULT NULL COMMENT '流程id',
  `userId` varchar(255) DEFAULT NULL COMMENT '用户Id',
  `updateTime` varchar(255) DEFAULT NULL COMMENT '修改时间',
  `orderImg` text COMMENT '工单附件（多个附件用逗号隔开）',
  `factoryId` varchar(255) DEFAULT NULL COMMENT '厂家id',
  `orderName` varchar(255) DEFAULT NULL COMMENT '工单名称',
  `faultProcessType` varchar(32) DEFAULT NULL COMMENT '故障工单流程类型'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='故障工单流程表';

-- ----------------------------
-- Table structure for FaultOrderProcessState
-- ----------------------------
DROP TABLE IF EXISTS `FaultOrderProcessState`;
CREATE TABLE `FaultOrderProcessState` (
  `id` varchar(32) NOT NULL,
  `name` varchar(255) DEFAULT NULL COMMENT '流程状态名称',
  `accessSecret` varchar(255) DEFAULT NULL COMMENT '接入方秘钥',
  `sign` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='故障工单流程状态表';

-- ----------------------------
-- Table structure for FaultOrderProcessType
-- ----------------------------
DROP TABLE IF EXISTS `FaultOrderProcessType`;
CREATE TABLE `FaultOrderProcessType` (
  `id` varchar(32) NOT NULL,
  `name` varchar(255) DEFAULT NULL COMMENT '故障工单类型（1硬件锁体故障 2APP错误 3web端错误）',
  `accessSecret` varchar(255) DEFAULT NULL COMMENT '接入方秘钥',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='故障工单类型表';

-- ----------------------------
-- Table structure for process_info
-- ----------------------------
DROP TABLE IF EXISTS `process_info`;
CREATE TABLE `process_info` (
  `id` varchar(32) NOT NULL,
  `processType` varchar(255) NOT NULL COMMENT '基础流程类型ID',
  `updateTime` varchar(255) DEFAULT NULL COMMENT '修改时间',
  `createTime` varchar(255) NOT NULL COMMENT '创建时间',
  `createBy` varchar(255) NOT NULL COMMENT '创建人',
  `updateBy` varchar(255) DEFAULT NULL COMMENT '修改人',
  `manageState` varchar(255) NOT NULL COMMENT '管理状态',
  `accessSecret` varchar(255) NOT NULL COMMENT '接入方秘钥',
  `fileId` varchar(255) NOT NULL COMMENT 'xml文件id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for process_node_person
-- ----------------------------
DROP TABLE IF EXISTS `process_node_person`;
CREATE TABLE `process_node_person` (
  `id` varchar(32) NOT NULL,
  `processId` varchar(255) NOT NULL COMMENT '流程id',
  `node` varchar(255) NOT NULL COMMENT '节点',
  `personId` varchar(255) NOT NULL COMMENT '人员id',
  `organizeId` varchar(255) NOT NULL COMMENT '组织id(所属一级组织id)',
  `nodeName` varchar(255) NOT NULL COMMENT '节点名',
  `orderByNum` varchar(255) NOT NULL COMMENT '节点排序',
  `realOrganizeId` varchar(255) DEFAULT NULL,
  `realOrganizeName` varchar(255) DEFAULT NULL COMMENT '真实组织名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for process_type_base
-- ----------------------------
DROP TABLE IF EXISTS `process_type_base`;
CREATE TABLE `process_type_base` (
  `id` varchar(32) NOT NULL,
  `processType` varchar(255) NOT NULL COMMENT '流程类型',
  `processSign` varchar(255) NOT NULL COMMENT '流程标识',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for RemoteControlRecord
-- ----------------------------
DROP TABLE IF EXISTS `RemoteControlRecord`;
CREATE TABLE `RemoteControlRecord` (
  `id` varchar(32) NOT NULL COMMENT '主键id',
  `deviceId` varchar(32) DEFAULT NULL COMMENT '远控设备id',
  `controlItem` varchar(255) DEFAULT NULL COMMENT '远控项',
  `controlValue` varchar(255) DEFAULT NULL COMMENT '远控值',
  `controlTime` varchar(32) DEFAULT NULL COMMENT '命令发起时间',
  `controlUserId` varchar(32) DEFAULT NULL COMMENT '命令发起人ID',
  `operateUserName` varchar(255) DEFAULT NULL COMMENT '命令发起人姓名',
  `operateUserPhone` varchar(255) DEFAULT NULL COMMENT '电话',
  `reason` text COMMENT '远控原因',
  `controlType` int(1) DEFAULT NULL COMMENT '控制类型：1：工单， 2：分权分域',
  `controlState` varchar(64) DEFAULT NULL COMMENT 'valid:有效，invalid:无效',
  `controlDestroyTime` varchar(64) DEFAULT NULL COMMENT '命令下发销毁时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for SimpleOrder_Device
-- ----------------------------
DROP TABLE IF EXISTS `SimpleOrder_Device`;
CREATE TABLE `SimpleOrder_Device` (
  `id` varchar(32) NOT NULL COMMENT '主键id',
  `simpleOrderId` varchar(255) DEFAULT NULL COMMENT '一般流程工单id',
  `deviceId` varchar(255) DEFAULT NULL COMMENT '设备id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='一般流程工单和设备绑定关系表';

-- ----------------------------
-- Table structure for SimpleOrder_Facilities
-- ----------------------------
DROP TABLE IF EXISTS `SimpleOrder_Facilities`;
CREATE TABLE `SimpleOrder_Facilities` (
  `id` varchar(32) NOT NULL,
  `simpleOrderId` varchar(255) NOT NULL COMMENT '工单id',
  `facilitiesId` varchar(255) NOT NULL COMMENT '设施id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for SimpleOrderProcess
-- ----------------------------
DROP TABLE IF EXISTS `SimpleOrderProcess`;
CREATE TABLE `SimpleOrderProcess` (
  `id` varchar(32) NOT NULL COMMENT '主键id',
  `orderName` varchar(255) DEFAULT NULL COMMENT '工单名称',
  `serialNumber` varchar(255) DEFAULT NULL COMMENT '流水号',
  `orderType` int(11) DEFAULT NULL COMMENT '工单类型',
  `createBy` varchar(255) DEFAULT NULL COMMENT '创建人',
  `createTime` varchar(255) DEFAULT NULL COMMENT '创建时间',
  `processStartTime` varchar(255) DEFAULT NULL COMMENT '流程开始时间',
  `constructBy` varchar(255) DEFAULT NULL COMMENT '施工单位',
  `accessSecret` varchar(255) DEFAULT NULL COMMENT '接入方秘钥',
  `manager` varchar(255) DEFAULT NULL COMMENT '联系人',
  `phone` varchar(255) DEFAULT NULL COMMENT '联系方式',
  `processEndTime` varchar(255) DEFAULT NULL COMMENT '流程结束时间',
  `mark` text COMMENT '施工说明',
  `orderImg` text COMMENT '工单附件（多个附件用逗号隔开）',
  `deviceCount` int(11) DEFAULT NULL COMMENT '已绑定的设备数量',
  `processState` varchar(255) DEFAULT NULL COMMENT '流程状态',
  `processId` varchar(255) DEFAULT NULL COMMENT '流程',
  `userId` varchar(255) DEFAULT NULL COMMENT '用户id',
  `updateTime` varchar(255) DEFAULT NULL COMMENT '更新时间',
  `remindTime` varchar(16) NOT NULL DEFAULT '' COMMENT '提醒工单延迟时间',
  `remindRule` varchar(16) NOT NULL DEFAULT 'default' COMMENT 'default默认提醒,custom自定义提醒',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='一般流程表';

-- ----------------------------
-- Table structure for SimpleOrderProcessState
-- ----------------------------
DROP TABLE IF EXISTS `SimpleOrderProcessState`;
CREATE TABLE `SimpleOrderProcessState` (
  `id` varchar(32) NOT NULL COMMENT '主键id',
  `name` varchar(255) DEFAULT NULL COMMENT '流程状态名称',
  `sign` varchar(255) DEFAULT NULL COMMENT '状态标记',
  `accessSecret` varchar(255) DEFAULT NULL COMMENT '接入方秘钥',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for SimpleOrderProcessType
-- ----------------------------
DROP TABLE IF EXISTS `SimpleOrderProcessType`;
CREATE TABLE `SimpleOrderProcessType` (
  `id` varchar(32) NOT NULL COMMENT '主键id',
  `name` varchar(255) DEFAULT NULL COMMENT '工单类型名称(1勘测 2建设 3维护 4验收 5其他)',
  `accessSecret` varchar(255) DEFAULT NULL COMMENT '接入方秘钥',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='一般流程工单类型表';

-- ----------------------------
-- Table structure for SmsInfo
-- ----------------------------
DROP TABLE IF EXISTS `SmsInfo`;
CREATE TABLE `SmsInfo` (
  `id` varchar(32) CHARACTER SET sjis NOT NULL,
  `accessSecret` varchar(120) NOT NULL COMMENT '接入方密匙',
  `gatewayUrl` varchar(255) NOT NULL COMMENT '短信发送地址',
  `creatTime` varchar(32) DEFAULT NULL COMMENT '授权创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for SmsRecord
-- ----------------------------
DROP TABLE IF EXISTS `SmsRecord`;
CREATE TABLE `SmsRecord` (
  `id` varchar(32) NOT NULL,
  `alarmSerialNum` bigint(20) NOT NULL COMMENT '告警流水号',
  `userName` varchar(32) NOT NULL COMMENT '接收人姓名',
  `phoneNumber` varchar(32) NOT NULL COMMENT '发送手机号',
  `smsContent` varchar(255) NOT NULL COMMENT '短信内容',
  `sendTime` varchar(255) NOT NULL COMMENT '发送时间',
  `accessSecret` varchar(64) DEFAULT NULL COMMENT '接入方密匙',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for StaffType
-- ----------------------------
DROP TABLE IF EXISTS `StaffType`;
CREATE TABLE `StaffType` (
  `id` varchar(32) NOT NULL,
  `staffTypeName` varchar(32) DEFAULT NULL COMMENT '人员类型名称',
  `manageState` varchar(32) DEFAULT NULL COMMENT '管理状态 disabled:停用 enabled:启用',
  `createTime` varchar(32) DEFAULT NULL COMMENT '创建时间',
  `editTime` varchar(32) DEFAULT NULL COMMENT '修改时间',
  `controlType` int(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='人员类型表';

-- ----------------------------
-- Table structure for SwitchLockRecord
-- ----------------------------
DROP TABLE IF EXISTS `SwitchLockRecord`;
CREATE TABLE `SwitchLockRecord` (
  `id` varchar(32) NOT NULL,
  `lockState` varchar(255) NOT NULL COMMENT '开关状态',
  `reportTime` varchar(255) NOT NULL COMMENT '上报时间',
  `deviceId` varchar(255) NOT NULL COMMENT '设备Id',
  `arrangeUserId` varchar(255) NOT NULL COMMENT '操作人Id',
  `remoteControlRecordId` varchar(255) DEFAULT NULL COMMENT '命令Id',
  `accessSecret` varchar(255) DEFAULT NULL COMMENT '接入方密钥',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for TimeoutReportConfig
-- ----------------------------
DROP TABLE IF EXISTS `TimeoutReportConfig`;
CREATE TABLE `TimeoutReportConfig` (
  `id` varchar(64) NOT NULL,
  `name` varchar(64) NOT NULL COMMENT '配置名称',
  `createTime` varchar(32) DEFAULT NULL COMMENT '创建时间',
  `createUserId` varchar(64) NOT NULL COMMENT '创建人',
  `updateTime` varchar(32) DEFAULT NULL COMMENT '更新时间',
  `updateUserId` varchar(64) DEFAULT NULL COMMENT '修改人',
  `timeoutReportTime` int(11) NOT NULL COMMENT '时间,单位:小时',
  `accessSecret` varchar(64) NOT NULL COMMENT '接入方密钥',
  `managerState` varchar(64) NOT NULL COMMENT '管理状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nick_name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `register_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
