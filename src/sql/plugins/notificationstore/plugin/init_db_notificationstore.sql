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



--
-- Insert notification status
--

INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(1, 'Traité', 15 );
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(2, 'Traitée', 15);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(3, 'Validé', 15);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(4, 'Validée', 15);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(5, 'Achevé', 15);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(6, 'Achevée', 15);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(7, 'Finalisé', 15);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(8, 'Finalisée', 15);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(9, 'Concrétisé', 15);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(10, 'Concrétisée', 15);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(11, 'Clôturé', 15);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(12, 'Clôturée', 15);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(13, 'Confirmé', 15);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(14, 'Confirmée', 15);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(15, 'Déposé', 15);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(16, 'Déposée', 15);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(17, 'Réceptionné', 15);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(18, 'Réceptionnée', 15);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(19, 'Confirmation', 15);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(20, 'Ouverture de droit', 15);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(21, 'Idée publiée', 15);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(22, 'Inscription enregistrée', 15);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(23, 'Inscription confirmée', 15);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(24, 'Payée', 14);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(25, 'Paiement finalisé', 14);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(26, 'Paiement effectué', 14);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(27, 'Confirmation de paiement', 14);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(28, 'Facture réglée', 14);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(29, 'Facture payée', 14);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(30, 'Payé', 14);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(31, 'Payée', 14);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(32, 'Réglé', 14);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(33, 'Réglée', 14);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(34, 'En attente de compléments', 13);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(35, 'En attente de pièce(s) complémentaire(s)', 13);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(36, 'À compléter', 13);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(37, 'Action attendue de votre part', 13);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(38, 'Paiement à réaliser', 12);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(39, 'Paiement en attente', 12);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(40, 'Paiement à effectuer', 12);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(41, 'Paiement à régler', 12);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(42, 'En attente de paiement', 12);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(43, 'Facture à payer', 12);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(44, 'Facture en attente', 12);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(45, 'Facture à régler', 12);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(46, 'À payer', 12);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(47, 'En cours de traitement', 11);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(48, 'En cours', 11);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(49, 'Compléments réceptionnés', 11);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(50, 'Suspendu', 11);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(51, 'Suspendue', 11);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(52, 'Annulé', 10);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(53, 'Annulée', 10);
INSERT INTO notificationstore_temporary_status
(id_temporary_status, status, status_id)
VALUES(54, 'Annulation', 10);