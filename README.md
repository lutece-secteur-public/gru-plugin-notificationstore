![](https://dev.lutece.paris.fr/jenkins/buildStatus/icon?job=gru-plugin-notificationstore-deploy)
[![Alerte](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-notificationstore&metric=alert_status)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-notificationstore)
[![Line of code](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-notificationstore&metric=ncloc)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-notificationstore)
[![Coverage](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-notificationstore&metric=coverage)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-notificationstore)

# Introduction

The **notificationstore** plugin is a component of the GRU (Gestion de la Relation Usager — Citizen Relationship Management) stack for the Lutèce platform. It provides persistent storage andmanagement of notifications sent to citizens as part of their demand processing workflow. It also exposes REST services so that other application components can interact with these data.

The main features of the plugin are:

 
* Receiving and storing notifications associated with citizen demands.
* Managing the demand lifecycle (creation, update, closure, deletion).
* Managing demand types and demand categories.
* Managing temporary statuses and their mapping to generic statuses.
* Recording notification-related events via NotificationEvent objects.
* Automatic purge of old events via a scheduled daemon.
* Reassigning notifications from one customer to another (identity consolidation use case).
* Exposing REST APIs (version 3) for all of the above features.
* Managing access to notification-related files via a database-backed file storage provider.

# Configuration

## Properties File

The `notificationstore.properties` file controls the plugin's runtime behaviour. The available properties are:

| Property| Default Value| Description|
|-----------------|-----------------|-----------------|
|  `notificationstore.notification.compress` |  `false` | Enables notification compression on storage.|
|  `notificationstore.notification.decompress` |  `false` | Enables notification decompression on retrieval.|
|  `notificationstore.daemon.NotificationEventDaemon.purge.nbDaysBefore` |  `90` | Retention period in days for notification events before they are purged by the daemon.|
|  `notificationstore.default.client.code` |  `TEST` | Default client code used when calling the IdentityStore service.|
|  `notificationstore.notification.considerGuidAsCuid` |  `false` | When enabled, the connection ID (GUID) is used as the customer ID (CUID) if the latter is absent.|
|  `notificationstore.notification.store.storeEventCustomerIdDoesNotExists` |  `false` | When enabled, notifications are stored even if the customer does not exist in the IdentityStore.|
|  `notificationstore.api.rest.limit.demand` |  `10` | Maximum number of demands returned per page by the REST APIs.|

## Injected Spring Beans

The Spring context file `notificationstore_context.xml` declares the following beans:

| Bean ID| Class| Description|
|-----------------|-----------------|-----------------|
|  `notificationstore.demandService` |  `DemandService` | Main service for managing demands, notifications and events. Implements `IDemandServiceProvider` . Injected into `DemandRestService` via `@Inject @Named` .|
|  `notificationstore.demandDao` |  `DemandDAO` | DAO for demand persistence.|
|  `notificationstore.notificationDao` |  `NotificationDAO` | DAO for notification persistence.|
|  `notificationstore.notificationEventDao` |  `NotificationEventDAO` | DAO for notification event persistence.|
|  `notificationstore.temporaryStatusDao` |  `TemporaryStatusDAO` | DAO for temporary status persistence.|
|  `notificationstore.demandTypeDao` |  `DemandTypeDAO` | DAO for demand type persistence.|
|  `notificationstore.demandCategoryDao` |  `DemandCategoryDAO` | DAO for demand category persistence.|
|  `notificationstore.notificationContentDao` |  `NotificationContentDAO` | DAO for notification content (file references) persistence.|
|  `notificationstore.accessDeniedFileRBACService` |  `AccessDeniedFileRBACService` | RBAC service controlling access to files stored in the database.|
|  `notificationstore.notificationStoreDatabaseFileService` |  `LocalDatabaseFileService` | File storage provider backed by the database (registered as the default provider).|

## Daemon

The plugin declares the following daemon, which can be scheduled via the Lutèce back-office:

| Daemon ID| Class| Description|
|-----------------|-----------------|-----------------|
|  `NotificationEventDaemon` |  `fr.paris.lutece.plugins.notificationstore.service.NotificationEventDaemon` | Automatic purge daemon for notification events. On each run it deletes events older than the number of days configured by the property `notificationstore.daemon.NotificationEventDaemon.purge.nbDaysBefore` (90 days by default).|

## Lutèce Caches

The plugin extends `AbstractCacheableService` for two services with built-in caching:

| Cache Name| Class| Description|
|-----------------|-----------------|-----------------|
|  `DemandRefCacheService` |  `DemandService` | Caches the list of demand types and individual demand types (prefix `DEMAND_TYPE_` ).|
|  `DemandTypeCacheService` |  `DemandTypeService` | Caches individual demand types looked up by ID.|
|  `temporaryStatusCacheService` |  `TemporaryStatusCacheService` | Caches the full list of temporary statuses (key `[temporaryStatus]` ). The cache is invalidated on every create or update of a status.|

## Admin Rights

The plugin registers the following admin features in the `CORE_ADMIN_RIGHT` table:

| Right ID| URL| Description|
|-----------------|-----------------|-----------------|
|  `NOTIFICATIONSTORE_MANAGEMENT` |  `jsp/admin/plugins/notificationstore/ManageStatus.jsp` | Access to notification status management (level 0).|
|  `NOTIFICATIONSTORE_DEMANDTYPE_MANAGEMENT` |  `jsp/admin/plugins/notificationstore/ManageDemandTypes.jsp` | Access to demand type management (level 0).|

# Usage

## NotificationService

The class `fr.paris.lutece.plugins.notificationstore.service.NotificationService` is the main entry point for notification processing. It is accessed through its singleton: `NotificationService.instance()` .

| Method| Description|
|-----------------|-----------------|
|  `instance()` | Returns the singleton instance. Initialises the `DemandService` and all registered notifiers.|
|  `newNotification(String strJson)` | Processes an incoming notification in JSON format. Verifies the customer against the IdentityStore, validates the demand, stores the notification and its associated demand, generates warning events if needed, and forwards the notification to all registered notifiers.|
|  `newNotificationEvent(String strJson)` | Stores a notification event ( `NotificationEvent` ) provided as JSON.|
|  `getNotification(String idDemand, String idDemandType, String customerId, String notificationType, long notificationDate)` | Retrieves a specific notification matching the provided criteria (demand ID, type, customer, notification type and date).|
|  `reassignNotifications(String strJson)` | Reassigns all notifications and demands from an old CUID to a new CUID (identity consolidation). Generates a MERGE event for each reassigned notification.|
|  `forward(Notification notification)` | Forwards the notification to all `INotifierServiceProvider` beans registered in the Spring context.|

## DemandService

The class `fr.paris.lutece.plugins.notificationstore.service.DemandService` implements `IDemandServiceProvider` and is available as the Spring bean `notificationstore.demandService` .

| Method| Description|
|-----------------|-----------------|
|  `findByCustomerId(String strCustomerId)` | Returns all demands associated with a customer ID, including their notifications.|
|  `findByReference(String strReference)` | Returns all demands matching a given reference, including their notifications.|
|  `findByPrimaryKey(String strDemandId, String strDemandTypeId, String strCustomerId)` | Returns the demand for the composite key (demand ID, type, customer), including its notifications.|
|  `create(Demand demand)` | Persists a demand and notifies all registered `IDemandListener` beans.|
|  `create(Notification notification)` | Persists a notification, stores its content ( `NotificationContentHome` ), and notifies all registered `INotificationListener` beans.|
|  `create(NotificationEvent notificationEvent)` | Persists a notification event.|
|  `update(Demand demand)` | Updates a demand and notifies all registered `IDemandListener` beans.|
|  `updateDemandsStatusId(int nNewStatusId, int nTemporaryStatusId)` | Updates the generic status ID on all demands linked to a given temporary status.|
|  `remove(String strDemandId, String strDemandTypeId, String strCustomerId)` | Deletes a demand and its notifications, then notifies the relevant listeners.|
|  `deleteAllDemandByCustomerId(String strCustomerId)` | Deletes all data for a given customer within a single transaction: notification contents, notifications, demands and events.|
|  `getDemandTypesList()` | Returns the complete list of demand types (with caching).|
|  `getDemandType(String type_id)` | Returns a demand type by its ID (with caching).|
|  `findEventsByDateAndDemandTypeIdAndStatus(long dStart, long dEnd, String strDemandTypeId, String strStatus)` | Searches notification events by date range, demand type ID and status.|
|  `getStatusByLabel(String strStatusLabel)` | Looks up a temporary status matching a given label (delegated to `TemporaryStatusService` ).|

## TemporaryStatusService

The class `fr.paris.lutece.plugins.notificationstore.service.TemporaryStatusService` manages the temporary statuses associated with notifications. It is accessed through `TemporaryStatusService.getInstance()` .

| Method| Description|
|-----------------|-----------------|
|  `create(TemporaryStatus status)` | Creates a temporary status and invalidates the cache.|
|  `update(TemporaryStatus status)` | Updates a temporary status within a transaction. If the status transitions from UNDEFINED to a defined value, cascades the update to associated demands and notification contents.|
|  `remove(int nKey)` | Deletes a temporary status and invalidates the cache.|
|  `findByPrimaryKey(int nKey)` | Looks up a temporary status by its primary key.|
|  `findByStatus(String strStatus)` | Looks up a temporary status by its label (case-insensitive, whitespace-insensitive, from cache).|
|  `getStatusList()` | Returns the complete list of temporary statuses.|

## REST APIs

All REST APIs are exposed under the base path `/rest/notificationstore/v3/` . They produce and consume JSON ( `application/json` ).

 **Notifications — NotificationRestService** 

| Verb| Path| Description| Parameters|
|-----------------|-----------------|-----------------|-----------------|
| POST|  `/notification` | Submits a new notification (JSON body).| Body: notification JSON|
| GET|  `/notification` | Retrieves a specific notification.|  `idDemand` , `idDemandType` , `customerId` , `notificationType` , `notificationDate` (all required)|
| GET|  `/notification/list` | Retrieves the list of notifications for a demand.|  `idDemand` , `idDemandType` , `customerId` (required); `notificationType` (optional)|
| GET|  `/notificationnotificationType` | Returns all available notification types ( `EnumNotificationType` ).| None|
| POST|  `/notificationEvent` | Stores a notification event (JSON body).| Body: NotificationEvent JSON|
| PUT|  `/notification/reassign` | Reassigns notifications from one customer ID to another.| Body: JSON with `oldCustomerId` and `newCustomerId` |

 **Demands — DemandRestService** 

| Verb| Path| Description| Parameters|
|-----------------|-----------------|-----------------|-----------------|
| GET|  `/demand/list` | Returns a paginated list of demands for a customer.|  `customerId` (required); `idDemandType` , `index` , `limitResult` , `notificationType` , `directionDateOrderBy` (optional)|
| GET|  `/demand/status` | Returns demands for a customer filtered by one or more statuses.|  `customerId` , `listStatus` (required); `listIdsDemandType` , `index` , `limitResult` , `notificationType` , `categoryCode` (optional)|
| DELETE|  `/demand/{customerId}` | Deletes all data (demands, notifications, events) for a customer.|  `customerId` (path)|

 **Demand Types — DemandTypeRestService** 

| Verb| Path| Description| Parameters|
|-----------------|-----------------|-----------------|-----------------|
| GET|  `/demandType` | Returns the list of demand types.|  `direct` (optional, legacy direct-response mode)|
| GET|  `/demandType/{id}` | Returns a demand type by its ID.|  `id` (path)|
| POST|  `/demandType` | Creates a new demand type.| Form params: `id_demand_type` , `label` , `url` , `app_code` (required); `category` (optional)|
| PUT|  `/demandType/{id}` | Updates an existing demand type.|  `id` (path); same form params as POST|
| DELETE|  `/demandType/{id}` | Deletes a demand type (rejected if existing notifications reference it).|  `id` (path)|

 **Demand Categories — DemandCategoryRestService** 

| Verb| Path| Description| Parameters|
|-----------------|-----------------|-----------------|-----------------|
| GET|  `/category/list` | Returns the list of demand categories.| None|
| GET|  `/category/{id}` | Returns a category by its ID.|  `id` (path)|
| POST|  `/category` | Creates a new demand category.| Form params: `code` , `label` (required); `isDefault` (optional)|
| PUT|  `/category/{id}` | Updates an existing demand category.|  `id` (path); same form params as POST|
| DELETE|  `/category/{id}` | Deletes a demand category.|  `id` (path)|

 **Temporary Statuses — StatusRestService** 

| Verb| Path| Description| Parameters|
|-----------------|-----------------|-----------------|-----------------|
| GET|  `/status` | Returns the list of temporary statuses.| None|
| GET|  `/status/{id}` | Returns a temporary status by its ID.|  `id` (path)|
| GET|  `/status/genericStatus` | Returns the list of generic statuses ( `EnumGenericStatus` ).| None|
| POST|  `/status` | Creates a new temporary status (JSON body).| Body: TemporaryStatus JSON|
| PUT|  `/status` | Updates an existing temporary status (JSON body).| Body: TemporaryStatus JSON|
| DELETE|  `/status/{id}` | Deletes a temporary status.|  `id` (path)|


[Maven documentation and reports](https://dev.lutece.paris.fr/plugins/plugin-notificationstore/)



 *generated by [xdoc2md](https://github.com/lutece-platform/tools-maven-xdoc2md-plugin) - do not edit directly.*