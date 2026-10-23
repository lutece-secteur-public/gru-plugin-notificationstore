--
-- Structure for table notificationstore_demande_type_tag
--
CREATE TABLE notificationstore_demande_type_tag (
id int AUTO_INCREMENT,
name varchar(255) default '' NOT NULL,
code varchar(255) default '' NOT NULL,
PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IDX_notificationstore_demande_type_tag_code on  notificationstore_demande_type_tag (code) ;

--
-- Data for table notificationstore_demande_type_tag
--
INSERT INTO notificationstore_demande_type_tag (name, code) VALUES
    ('Rendez-vous', 'rdv'),
    ('Facturation', 'facture');