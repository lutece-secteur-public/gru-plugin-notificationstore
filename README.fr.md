![](https://dev.lutece.paris.fr/jenkins/buildStatus/icon?job=gru-plugin-notificationstore-deploy)
[![Alerte](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-notificationstore&metric=alert_status)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-notificationstore)
[![Line of code](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-notificationstore&metric=ncloc)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-notificationstore)
[![Coverage](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-notificationstore&metric=coverage)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-notificationstore)

# Introduction

Le plugin **notificationstore** est un composant du socle GRU (Gestion de la Relation Usager) de la plateforme Lutèce. Il assure le stockage et la gestion des notifications envoyées aux usagers dansle cadre du traitement de leurs demandes. Il expose également des services REST permettant à d'autres composants applicatifs d'interagir avec ces données.

Les principales fonctionnalités du plugin sont :

 
* Réception et stockage des notifications associées à des demandes usagers.
* Gestion du cycle de vie des demandes (création, mise à jour, fermeture, suppression).
* Gestion des types de demandes et des catégories de demandes.
* Gestion des statuts temporaires et de leur correspondance avec les statuts génériques.
* Historisation des événements liés aux notifications via des NotificationEvent.
* Purge automatique des anciens événements via un daemon planifié.
* Réassignation des notifications d'un usager vers un autre (cas de consolidation d'identités).
* Exposition d'API REST (version 3) pour toutes ces fonctionnalités.
* Gestion de l'accès aux fichiers liés aux notifications via un fournisseur de stockage.

# Configuration

## Fichier de propriétés

Le fichier `notificationstore.properties` permet de configurer le comportement du plugin. Les propriétés disponibles sont :

| Propriété| Valeur par défaut| Description|
|-----------------|-----------------|-----------------|
|  `notificationstore.notification.compress` |  `false` | Active la compression des notifications lors du stockage.|
|  `notificationstore.notification.decompress` |  `false` | Active la décompression des notifications lors de la lecture.|
|  `notificationstore.daemon.NotificationEventDaemon.purge.nbDaysBefore` |  `90` | Nombre de jours de rétention des événements de notification avant purge automatique.|
|  `notificationstore.default.client.code` |  `TEST` | Code client par défaut utilisé pour les appels à l'IdentityStore.|
|  `notificationstore.notification.considerGuidAsCuid` |  `false` | Si activé, l'identifiant de connexion (GUID) est utilisé comme identifiant client (CUID) lorsque ce dernier est absent.|
|  `notificationstore.notification.store.storeEventCustomerIdDoesNotExists` |  `false` | Si activé, les notifications sont stockées même si l'usager n'existe pas dans l'IdentityStore.|
|  `notificationstore.api.rest.limit.demand` |  `10` | Nombre maximum de demandes retournées par page via les API REST.|

## Beans Spring injectés

Le fichier de contexte Spring `notificationstore_context.xml` déclare les beans suivants :

| Bean ID| Classe| Description|
|-----------------|-----------------|-----------------|
|  `notificationstore.demandService` |  `DemandService` | Service principal de gestion des demandes, notifications et événements. Implémente `IDemandServiceProvider` . Injecté dans `DemandRestService` via `@Inject @Named` .|
|  `notificationstore.demandDao` |  `DemandDAO` | DAO pour la persistance des demandes.|
|  `notificationstore.notificationDao` |  `NotificationDAO` | DAO pour la persistance des notifications.|
|  `notificationstore.notificationEventDao` |  `NotificationEventDAO` | DAO pour la persistance des événements de notification.|
|  `notificationstore.temporaryStatusDao` |  `TemporaryStatusDAO` | DAO pour la persistance des statuts temporaires.|
|  `notificationstore.demandTypeDao` |  `DemandTypeDAO` | DAO pour la persistance des types de demandes.|
|  `notificationstore.demandCategoryDao` |  `DemandCategoryDAO` | DAO pour la persistance des catégories de demandes.|
|  `notificationstore.notificationContentDao` |  `NotificationContentDAO` | DAO pour la persistance des contenus de notification (fichiers).|
|  `notificationstore.accessDeniedFileRBACService` |  `AccessDeniedFileRBACService` | Service RBAC de contrôle d'accès aux fichiers stockés en base.|
|  `notificationstore.notificationStoreDatabaseFileService` |  `LocalDatabaseFileService` | Fournisseur de stockage de fichiers en base de données (défini comme fournisseur par défaut).|

## Daemon

Le plugin déclare le daemon suivant, configurable via le panneau d'administration Lutèce :

| ID Daemon| Classe| Description|
|-----------------|-----------------|-----------------|
|  `NotificationEventDaemon` |  `fr.paris.lutece.plugins.notificationstore.service.NotificationEventDaemon` | Daemon de purge automatique des événements de notification. À chaque exécution, il supprime les événements antérieurs au nombre de jours configuré par la propriété `notificationstore.daemon.NotificationEventDaemon.purge.nbDaysBefore` (90 jours par défaut).|

## Caches Lutèce

Le plugin étend `AbstractCacheableService` pour deux services avec cache intégré :

| Nom du cache| Classe| Description|
|-----------------|-----------------|-----------------|
|  `DemandRefCacheService` |  `DemandService` | Met en cache la liste des types de demandes et les types de demandes individuels (préfixe `DEMAND_TYPE_` ).|
|  `DemandTypeCacheService` |  `DemandTypeService` | Met en cache les types de demandes individuels lors des requêtes par identifiant.|
|  `temporaryStatusCacheService` |  `TemporaryStatusCacheService` | Met en cache la liste complète des statuts temporaires (clé `[temporaryStatus]` ). Le cache est invalidé à chaque création ou modification de statut.|

## Droits d'administration

Le plugin déclare les fonctionnalités d'administration suivantes (table `CORE_ADMIN_RIGHT` ) :

| ID Droit| URL| Description|
|-----------------|-----------------|-----------------|
|  `NOTIFICATIONSTORE_MANAGEMENT` |  `jsp/admin/plugins/notificationstore/ManageStatus.jsp` | Accès à la gestion des statuts de notification (niveau 0).|
|  `NOTIFICATIONSTORE_DEMANDTYPE_MANAGEMENT` |  `jsp/admin/plugins/notificationstore/ManageDemandTypes.jsp` | Accès à la gestion des types de demandes (niveau 0).|

# Utilisation

## Service NotificationService

La classe `fr.paris.lutece.plugins.notificationstore.service.NotificationService` est le point d'entrée principal pour le traitement des notifications. Elle s'utilise via son instance singleton : `NotificationService.instance()` .

| Méthode| Description|
|-----------------|-----------------|
|  `instance()` | Retourne l'instance singleton du service. Initialise le `DemandService` et les notifieurs enregistrés.|
|  `newNotification(String strJson)` | Traite une nouvelle notification au format JSON. Contrôle l'usager via l'IdentityStore, vérifie la cohérence de la demande, stocke la notification et la demande associée, génère les événements d'avertissement si nécessaire, puis transmet la notification aux notifieurs enregistrés.|
|  `newNotificationEvent(String strJson)` | Stocke un événement de notification ( `NotificationEvent` ) au format JSON.|
|  `getNotification(String idDemand, String idDemandType, String customerId, String notificationType, long notificationDate)` | Recherche une notification précise selon les critères fournis (identifiant de demande, type, usager, type de notification et date).|
|  `reassignNotifications(String strJson)` | Réassigne toutes les notifications et demandes d'un ancien CUID vers un nouveau CUID (cas de consolidation d'identités). Génère un événement de type MERGE pour chaque notification réassignée.|
|  `forward(Notification notification)` | Transmet la notification à tous les notifieurs ( `INotifierServiceProvider` ) enregistrés dans le contexte Spring.|

## Service DemandService

La classe `fr.paris.lutece.plugins.notificationstore.service.DemandService` implémente `IDemandServiceProvider` et est disponible via le bean Spring `notificationstore.demandService` .

| Méthode| Description|
|-----------------|-----------------|
|  `findByCustomerId(String strCustomerId)` | Retourne toutes les demandes associées à un identifiant client, avec leurs notifications.|
|  `findByReference(String strReference)` | Retourne toutes les demandes correspondant à une référence donnée, avec leurs notifications.|
|  `findByPrimaryKey(String strDemandId, String strDemandTypeId, String strCustomerId)` | Retourne la demande correspondant à la clé composite (id demande, type, client), avec ses notifications.|
|  `create(Demand demand)` | Crée une demande en base et notifie les `IDemandListener` enregistrés.|
|  `create(Notification notification)` | Crée une notification en base, stocke son contenu ( `NotificationContentHome` ) et notifie les `INotificationListener` .|
|  `create(NotificationEvent notificationEvent)` | Crée un événement de notification en base.|
|  `update(Demand demand)` | Met à jour une demande en base et notifie les `IDemandListener` .|
|  `updateDemandsStatusId(int nNewStatusId, int nTemporaryStatusId)` | Met à jour le statut générique des demandes liées à un statut temporaire donné.|
|  `remove(String strDemandId, String strDemandTypeId, String strCustomerId)` | Supprime une demande et ses notifications, puis notifie les listeners concernés.|
|  `deleteAllDemandByCustomerId(String strCustomerId)` | Supprime en transaction toutes les données d'un usager : contenus de notification, notifications, demandes et événements.|
|  `getDemandTypesList()` | Retourne la liste complète des types de demandes (avec cache).|
|  `getDemandType(String type_id)` | Retourne un type de demande par son identifiant (avec cache).|
|  `findEventsByDateAndDemandTypeIdAndStatus(long dStart, long dEnd, String strDemandTypeId, String strStatus)` | Recherche des événements de notification selon une plage de dates, un type de demande et un statut.|
|  `getStatusByLabel(String strStatusLabel)` | Recherche un statut temporaire correspondant à un libellé donné (via `TemporaryStatusService` ).|

## Service TemporaryStatusService

La classe `fr.paris.lutece.plugins.notificationstore.service.TemporaryStatusService` gère les statuts temporaires associés aux notifications. Elle s'utilise via `TemporaryStatusService.getInstance()` .

| Méthode| Description|
|-----------------|-----------------|
|  `create(TemporaryStatus status)` | Crée un statut temporaire et invalide le cache.|
|  `update(TemporaryStatus status)` | Met à jour un statut temporaire en transaction. Si le statut passe d'UNDEFINED à un statut défini, met à jour en cascade les demandes et contenus de notification associés.|
|  `remove(int nKey)` | Supprime un statut temporaire et invalide le cache.|
|  `findByPrimaryKey(int nKey)` | Recherche un statut temporaire par son identifiant.|
|  `findByStatus(String strStatus)` | Recherche un statut temporaire par son libellé (comparaison insensible aux espaces et à la casse, via le cache).|
|  `getStatusList()` | Retourne la liste complète des statuts temporaires.|

## API REST

Toutes les API REST sont exposées sous le chemin de base `/rest/notificationstore/v3/` . Elles produisent et consomment du JSON ( `application/json` ).

 **Notifications — NotificationRestService** 

| Verbe| Chemin| Description| Paramètres|
|-----------------|-----------------|-----------------|-----------------|
| POST|  `/notification` | Soumet une nouvelle notification (corps JSON).| Corps : JSON de notification|
| GET|  `/notification` | Récupère une notification précise.|  `idDemand` , `idDemandType` , `customerId` , `notificationType` , `notificationDate` (tous obligatoires)|
| GET|  `/notification/list` | Récupère la liste des notifications d'une demande.|  `idDemand` , `idDemandType` , `customerId` (obligatoires) ; `notificationType` (optionnel)|
| GET|  `/notificationnotificationType` | Retourne la liste des types de notification disponibles ( `EnumNotificationType` ).| Aucun|
| POST|  `/notificationEvent` | Stocke un événement de notification (corps JSON).| Corps : JSON de NotificationEvent|
| PUT|  `/notification/reassign` | Réassigne les notifications d'un CUID vers un autre.| Corps : JSON avec `oldCustomerId` et `newCustomerId` |

 **Demandes — DemandRestService** 

| Verbe| Chemin| Description| Paramètres|
|-----------------|-----------------|-----------------|-----------------|
| GET|  `/demand/list` | Récupère la liste paginée des demandes d'un usager.|  `customerId` (obligatoire) ; `idDemandType` , `index` , `limitResult` , `notificationType` , `directionDateOrderBy` (optionnels)|
| GET|  `/demand/status` | Récupère les demandes d'un usager filtrées par statut(s).|  `customerId` , `listStatus` (obligatoires) ; `listIdsDemandType` , `index` , `limitResult` , `notificationType` , `categoryCode` (optionnels)|
| DELETE|  `/demand/{customerId}` | Supprime toutes les données (demandes, notifications, événements) d'un usager.|  `customerId` (chemin)|

 **Types de demandes — DemandTypeRestService** 

| Verbe| Chemin| Description| Paramètres|
|-----------------|-----------------|-----------------|-----------------|
| GET|  `/demandType` | Retourne la liste des types de demandes.|  `direct` (optionnel, mode de réponse direct)|
| GET|  `/demandType/{id}` | Retourne un type de demande par son identifiant.|  `id` (chemin)|
| POST|  `/demandType` | Crée un nouveau type de demande.| Paramètres de formulaire : `id_demand_type` , `label` , `url` , `app_code` (obligatoires) ; `category` (optionnel)|
| PUT|  `/demandType/{id}` | Modifie un type de demande existant.|  `id` (chemin) ; paramètres de formulaire identiques au POST|
| DELETE|  `/demandType/{id}` | Supprime un type de demande (refusé si des notifications l'utilisent encore).|  `id` (chemin)|

 **Catégories de demandes — DemandCategoryRestService** 

| Verbe| Chemin| Description| Paramètres|
|-----------------|-----------------|-----------------|-----------------|
| GET|  `/category/list` | Retourne la liste des catégories de demandes.| Aucun|
| GET|  `/category/{id}` | Retourne une catégorie par son identifiant.|  `id` (chemin)|
| POST|  `/category` | Crée une nouvelle catégorie de demande.| Paramètres de formulaire : `code` , `label` (obligatoires) ; `isDefault` (optionnel)|
| PUT|  `/category/{id}` | Modifie une catégorie de demande existante.|  `id` (chemin) ; paramètres de formulaire identiques au POST|
| DELETE|  `/category/{id}` | Supprime une catégorie de demande.|  `id` (chemin)|

 **Statuts temporaires — StatusRestService** 

| Verbe| Chemin| Description| Paramètres|
|-----------------|-----------------|-----------------|-----------------|
| GET|  `/status` | Retourne la liste des statuts temporaires.| Aucun|
| GET|  `/status/{id}` | Retourne un statut temporaire par son identifiant.|  `id` (chemin)|
| GET|  `/status/genericStatus` | Retourne la liste des statuts génériques ( `EnumGenericStatus` ).| Aucun|
| POST|  `/status` | Crée un nouveau statut temporaire (corps JSON).| Corps : JSON de TemporaryStatus|
| PUT|  `/status` | Modifie un statut temporaire existant (corps JSON).| Corps : JSON de TemporaryStatus|
| DELETE|  `/status/{id}` | Supprime un statut temporaire.|  `id` (chemin)|


[Maven documentation and reports](https://dev.lutece.paris.fr/plugins/plugin-notificationstore/)



 *generated by [xdoc2md](https://github.com/lutece-platform/tools-maven-xdoc2md-plugin) - do not edit directly.*