--
-- Structure for table notificationstore_demand
--
DROP TABLE IF EXISTS notificationstore_demand;
CREATE TABLE notificationstore_demand (
uid int  AUTO_INCREMENT,
id varchar(100) NOT NULL,               -- old "id" column
demand_type_id varchar(50) NOT NULL,   -- old "type_id" column
subtype_id varchar(50) NULL,
reference varchar(50) NOT NULL,    
status_id int default 0 NOT NULL ,
customer_id varchar(100) NULL,
creation_date timestamp NOT NULL,
closure_date timestamp NULL,
max_steps int NULL,
current_step int NULL,
modify_date timestamp NULL,
PRIMARY KEY ( uid ),
UNIQUE( demand_type_id,customer_id,id )
);

CREATE INDEX notificationstore_demand_u_index on notificationstore_demand ( demand_type_id,customer_id,id);
CREATE INDEX notificationstore_demand_id_index on notificationstore_demand ( id );
CREATE INDEX notificationstore_demand_customer_index ON notificationstore_demand (customer_id);

--
-- Structure for table notificationstore_notification
--
DROP TABLE IF EXISTS notificationstore_notification;
CREATE TABLE notificationstore_notification (
id int  AUTO_INCREMENT,
demand_id varchar(100) NOT NULL,
demand_type_id varchar(50) NOT NULL,
customer_id varchar(100) NULL,
date timestamp NOT NULL,
PRIMARY KEY (id)
);
CREATE INDEX notificationstore_notification_index on notificationstore_notification ( demand_type_id, customer_id,demand_id );
CREATE INDEX idx_notificationstore_notification_date on notificationstore_notification (date ASC, demand_type_id ASC) ;

ALTER TABLE notificationstore_notification ADD CONSTRAINT fk_notificationstore_notification_demand_id FOREIGN KEY (demand_type_id, customer_id,demand_id)
      REFERENCES notificationstore_demand (demand_type_id, customer_id ,id) ON DELETE CASCADE ON UPDATE RESTRICT;
   

DROP TABLE IF EXISTS notificationstore_notification_event;
CREATE TABLE notificationstore_notification_event (
id int AUTO_INCREMENT,
event_date timestamp NOT NULL,
demand_id varchar(100) NOT NULL,
demand_type_id varchar(50) NOT NULL,
customer_id varchar(100) NULL,                    -- ?
notification_date timestamp NOT NULL,
msg_id varchar(255) ,
type varchar(255) default '' NOT NULL,
status varchar(255) default '' NOT NULL,
redelivry int default '0',
message long varchar,
PRIMARY KEY (id)
);

CREATE INDEX IDX_NOTIFICATION_EVENT_DEMAND_ID on  notificationstore_notification_event (demand_id, demand_type_id) ;
CREATE INDEX IDX_NOTIFICATION_EVENT_DATE on notificationstore_notification_event (event_date ASC, demand_type_id ASC) ;

   
--
-- Structure for table notificationstore_temporary_status
--


DROP TABLE IF EXISTS notificationstore_temporary_status;
CREATE TABLE notificationstore_temporary_status (
id_temporary_status int AUTO_INCREMENT,
status long varchar NOT NULL,
status_id int default '-1',
PRIMARY KEY (id)
);

CREATE INDEX IDX_notificationstore_status_text on  notificationstore_temporary_status (status(200), status_id) ;

--
-- Structure for table notificationstore_demand_type
--

DROP TABLE IF EXISTS notificationstore_demand_type;
CREATE TABLE notificationstore_demand_type (
id int AUTO_INCREMENT,
demande_type_id varchar(255) default '' NOT NULL,    -- old "code" column
label varchar(255) default '' NOT NULL,
code_category varchar(255) default '',
url varchar(255) default '',
application_code varchar(255) not null,
PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IDX_notificationstore_demand_type on  notificationstore_demand_type (demande_type_id) ;
--
-- Structure for table notificationstore_demand_category
--

DROP TABLE IF EXISTS notificationstore_demand_category;
CREATE TABLE notificationstore_demand_category (
id_demand_category int AUTO_INCREMENT,
code varchar(50) default '' NOT NULL,
label long varchar NOT NULL,
is_default SMALLINT NOT NULL default 0,
PRIMARY KEY (id_demand_category)
);

CREATE UNIQUE INDEX IDX_notificationstore_demand_category_code on  notificationstore_demand_category (code) ;

--
-- Structure for table notificationstore_notification_content
--

DROP TABLE IF EXISTS notificationstore_notification_content;
CREATE TABLE notificationstore_notification_content (
id_notification_content int AUTO_INCREMENT,
notification_id int NOT NULL,
notification_type varchar(100) default '' NOT NULL,
id_temporary_status int default "-1",
status_id int default "-1",
file_key VARCHAR(255) DEFAULT NULL,
file_store VARCHAR(255) DEFAULT NULL,
PRIMARY KEY (id_notification_content)
);

CREATE UNIQUE INDEX index_notification_id ON notificationstore_notification_content (notification_id, notification_type);
CREATE UNIQUE INDEX index_notificationstore_notification_content_id_temporary_status ON notificationstore_notification_content ( id_temporary_status );

