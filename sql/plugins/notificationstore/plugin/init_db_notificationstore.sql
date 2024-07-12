--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'NOTIFICATIONSTORE_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('NOTIFICATIONSTORE_MANAGEMENT','notificationstore.adminFeature.Manage.name',1,'jsp/admin/plugins/notificationstore/ManageStatus.jsp','notificationstore.adminFeature.Manage.description',0,'notificationstore',NULL,NULL,NULL,4);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'NOTIFICATIONSTORE_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('NOTIFICATIONSTORE_MANAGEMENT',1);

--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'NOTIFICATIONSTORE_DEMANDTYPE_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('NOTIFICATIONSTORE_DEMANDTYPE_MANAGEMENT','notificationstore.adminFeature.ManageDemandType.name',1,'jsp/admin/plugins/notificationstore/ManageDemandTypes.jsp','notificationstore.adminFeature.ManageDemandType.description',0,'notificationstore',NULL,NULL,NULL,4);

--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'NOTIFICATIONSTORE_DEMANDTYPE_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('NOTIFICATIONSTORE_DEMANDTYPE_MANAGEMENT',1);



DELETE FROM core_admin_right WHERE id_right = 'DEMAND_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES
('DEMAND_MANAGEMENT','notificationstore.adminFeature.ManageDemand.name',1,'jsp/admin/plugins/notificationstore/ManageDemand.jsp','notificationstore.adminFeature.ManageDemand.description',0,'notificationstore',NULL,NULL,NULL,4);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'DEMAND_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('DEMAND_MANAGEMENT',1);