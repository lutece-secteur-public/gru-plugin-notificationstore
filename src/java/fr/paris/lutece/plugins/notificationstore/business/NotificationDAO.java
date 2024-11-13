/*
 * Copyright (c) 2002-2024, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.notificationstore.business;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fr.paris.lutece.plugins.grubusiness.business.customer.Customer;
import fr.paris.lutece.plugins.grubusiness.business.notification.BackofficeNotification;
import fr.paris.lutece.plugins.grubusiness.business.notification.BroadcastNotification;
import fr.paris.lutece.plugins.grubusiness.business.notification.EmailNotification;
import fr.paris.lutece.plugins.grubusiness.business.notification.EnumNotificationType;
import fr.paris.lutece.plugins.grubusiness.business.notification.INotificationDAO;
import fr.paris.lutece.plugins.grubusiness.business.notification.MyDashboardNotification;
import fr.paris.lutece.plugins.grubusiness.business.notification.Notification;
import fr.paris.lutece.plugins.grubusiness.business.notification.NotificationFilter;
import fr.paris.lutece.plugins.grubusiness.business.notification.SMSNotification;
import fr.paris.lutece.plugins.notificationstore.service.NotificationStorePlugin;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.FileServiceException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.sql.DAOUtil;
import fr.paris.lutece.util.string.StringUtil;

/**
 * This class provides Data Access methods for Notification objects stored in SQL database
 */
public final class NotificationDAO implements INotificationDAO
{
    private static final String COLUMN_NOTIFICATION_ID = "id";
    private static final String COLUMN_DEMAND_ID = "demand_id";
    private static final String COLUMN_DEMAND_TYPE_ID = "demand_type_id";
    private static final String COLUMN_CUSTOMER_ID = "demand_type_id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_CUSTOMER = "customer_id";
    
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id ) FROM notificationstore_notification";
    private static final String SQL_QUERY_FILTER_SELECT_BASE = "SELECT id, demand_id, demand_type_id, customer_id, date FROM notificationstore_notification ";
    private static final String SQL_QUERY_FILTER_SELECT_ID_BASE = "SELECT distinct id FROM notificationstore_notification ";
    private static final String SQL_QUERY_FILTER_WHERE_BASE = " WHERE ";
    private static final String SQL_QUERY_FILTER_WHERE_DEMANDID = " demand_id = ? ";
    private static final String SQL_QUERY_FILTER_WHERE_ID_IN = " id in ( %s )";
    private static final String SQL_QUERY_FILTER_WHERE_DEMANDTYPEID = " demand_type_id = ? ";
    private static final String SQL_QUERY_FILTER_WHERE_CUSTOMERID = " customer_id = ? ";
    private static final String SQL_QUERY_FILTER_ORDER = " ORDER BY id ASC";
    private static final String SQL_QUERY_FILTER_WHERE_START_DATE = " date >= ? ";
    private static final String SQL_QUERY_FILTER_WHERE_END_DATE = " date <= ? ";
    private static final String SQL_QUERY_AND = " AND ";
    private static final String SQL_QUERY_FILTER_NOTIFICATION_TYPE = " id IN (SELECT notification_id FROM notificationstore_notification_content WHERE notification_type in (  ";

    private static final String SQL_QUERY_INSERT = "INSERT INTO notificationstore_notification ( id, demand_id, demand_type_id, customer_id, date ) VALUES ( ?, ?, ?, ?, ? );";
    private static final String SQL_QUERY_DELETE = "DELETE FROM notificationstore_notification WHERE id = ?";
    private static final String SQL_QUERY_DELETE_BY_DEMAND = "DELETE FROM notificationstore_notification WHERE demand_id = ? AND demand_type_id = ? AND customer_id = ? ";
    private static final String SQL_QUERY_DISTINCT_DEMAND_TYPE_ID = " SELECT DISTINCT demand_type_id FROM notificationstore_notification ORDER BY demand_type_id ";
    private static final String SQL_QUERY_SELECT_BY_DEMAND_CUSTOMER_TYPE = " SELECT * FROM notificationstore_notification"
            + " WHERE demand_id = ? AND demand_type_id = ?  AND customer_id = ? ";

    private static final String SQL_QUERY_SELECT_LAST_NOTIFICATION = "SELECT * FROM notificationstore_notification " + " WHERE demand_id = ?"
            + " AND demand_type_id = ?" + " ORDER BY date desc, id desc " + " LIMIT 1";

    private static final String PROPERTY_DECOMPRESS_NOTIFICATION = "grustoragedb.notification.decompress";

    ObjectMapper _mapper;

    /**
     * Constructor
     */
    public NotificationDAO( )
    {
        super( );
        _mapper = new ObjectMapper( );
        _mapper.configure( DeserializationFeature.UNWRAP_ROOT_VALUE, false );
        _mapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
        _mapper.configure( SerializationFeature.WRAP_ROOT_VALUE, false );
        _mapper.configure( Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true );
        // => _mapper.configure( JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature( ) , true ); ??
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Notification> loadByDemand( String strDemandId, String strDemandTypeId, String strCustomerId)
    {
        NotificationFilter filter = new NotificationFilter( );
        filter.setDemandId( strDemandId );
        filter.setDemandTypeId( strDemandTypeId );
        filter.setCustomerId( strCustomerId );

        return loadByFilter( filter );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Notification> loadByFilter( NotificationFilter notificationFilter )
    {
        String strSQL = getFilterCriteriaClauses( SQL_QUERY_FILTER_SELECT_BASE, notificationFilter );

        try ( DAOUtil daoUtil = new DAOUtil( strSQL, NotificationStorePlugin.getPlugin( ) ) )
        {
            addFilterCriteriaValues( daoUtil, notificationFilter );

            daoUtil.executeQuery( );

            return getNotificationsFromDao( daoUtil, notificationFilter );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> loadIdsByFilter( NotificationFilter notificationFilter )
    {
        String strSQL = getFilterCriteriaClauses( SQL_QUERY_FILTER_SELECT_ID_BASE, notificationFilter );
        List<Integer> listIds = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( strSQL, NotificationStorePlugin.getPlugin( ) ) )
        {

            addFilterCriteriaValues( daoUtil, notificationFilter );

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                int nNotificationId = daoUtil.getInt( COLUMN_NOTIFICATION_ID );
                listIds.add( nNotificationId );
            }

            return listIds;
        }
    }

    /**
     * @param notificationFilter
     * @return the query string
     */
    private String getFilterCriteriaClauses( String strBaseQuery, NotificationFilter notificationFilter )
    {
        StringBuilder sbQuery = new StringBuilder( strBaseQuery );
        boolean hasOneWhere = false;

        // WHERE

        if ( notificationFilter.containsId( ) )
        {
            sbQuery.append( BooleanUtils.toString( hasOneWhere, SQL_QUERY_AND, SQL_QUERY_FILTER_WHERE_BASE ) );

            String sql = String.format( SQL_QUERY_FILTER_WHERE_ID_IN,
                    notificationFilter.getIds( ).stream( ).map( v -> "?" ).collect( Collectors.joining( ", " ) ) );

            sbQuery.append( sql );
            hasOneWhere = true;
        }
        if ( notificationFilter.containsDemandId( ) )
        {
            sbQuery.append( SQL_QUERY_FILTER_WHERE_BASE );
            sbQuery.append( SQL_QUERY_FILTER_WHERE_DEMANDID );
            hasOneWhere = true;
        }
        if ( notificationFilter.containsDemandTypeId( ) )
        {
            sbQuery.append( BooleanUtils.toString( hasOneWhere, SQL_QUERY_AND, SQL_QUERY_FILTER_WHERE_BASE ) );
            sbQuery.append( SQL_QUERY_FILTER_WHERE_DEMANDTYPEID );
            hasOneWhere = true;
        }
        if ( notificationFilter.containsCustomerId( ) )
        {
            sbQuery.append( BooleanUtils.toString( hasOneWhere, SQL_QUERY_AND, SQL_QUERY_FILTER_WHERE_BASE ) );
            sbQuery.append( SQL_QUERY_FILTER_WHERE_CUSTOMERID );
            hasOneWhere = true;
        }
        if ( notificationFilter.containsNotificationTypeFilter( ) )
        {
            sbQuery.append( BooleanUtils.toString( hasOneWhere, SQL_QUERY_AND, SQL_QUERY_FILTER_WHERE_BASE ) );
            sbQuery.append( SQL_QUERY_FILTER_NOTIFICATION_TYPE );
            boolean hasOneNotiType = false;
            if ( notificationFilter.containsBackofficeNotificationType( ) )
            {
                sbQuery.append( BooleanUtils.toString( hasOneNotiType, ", ", StringUtils.EMPTY ) );
                sbQuery.append( "'" + EnumNotificationType.BACKOFFICE.name( ) + "'" );
                hasOneNotiType = true;
            }
            if ( notificationFilter.containsSmsNotificationType( ) )
            {
                sbQuery.append( BooleanUtils.toString( hasOneNotiType, ", ", StringUtils.EMPTY ) );
                sbQuery.append( "'" + EnumNotificationType.SMS.name( ) + "'" );
                hasOneNotiType = true;
            }
            if ( notificationFilter.containsCustomerEmailNotificationType( ) )
            {
                sbQuery.append( BooleanUtils.toString( hasOneNotiType, ", ", StringUtils.EMPTY ) );
                sbQuery.append( "'" + EnumNotificationType.CUSTOMER_EMAIL.name( ) + "'" );
                hasOneNotiType = true;
            }
            if ( notificationFilter.containsMyDashboardNotificationType( ) )
            {
                sbQuery.append( BooleanUtils.toString( hasOneNotiType, ", ", StringUtils.EMPTY ) );
                sbQuery.append( "'" + EnumNotificationType.MYDASHBOARD.name( ) + "'" );
                hasOneNotiType = true;
            }
            if ( notificationFilter.containsBroadcastEmailNotificationType( ) )
            {
                sbQuery.append( BooleanUtils.toString( hasOneNotiType, ", ", StringUtils.EMPTY ) );
                sbQuery.append( "'" + EnumNotificationType.BROADCAST_EMAIL.name( ) + "'" );
                hasOneNotiType = true;
            }
            if ( hasOneNotiType )
            {
                sbQuery.append( "))" );
            }
        }

        if ( notificationFilter.containsStartDate( ) )
        {
            sbQuery.append( BooleanUtils.toString( hasOneWhere, SQL_QUERY_AND, SQL_QUERY_FILTER_WHERE_BASE ) );
            sbQuery.append( SQL_QUERY_FILTER_WHERE_START_DATE );
            hasOneWhere = true;
        }
        if ( notificationFilter.containsEndDate( ) )
        {
            sbQuery.append( BooleanUtils.toString( hasOneWhere, SQL_QUERY_AND, SQL_QUERY_FILTER_WHERE_BASE ) );
            sbQuery.append( SQL_QUERY_FILTER_WHERE_END_DATE );
            hasOneWhere = true;
        }

        // ORDER
        sbQuery.append( SQL_QUERY_FILTER_ORDER );

        return sbQuery.toString( );
    }

    /**
     * @param daoUtil
     * @param notificationFilter
     */
    private void addFilterCriteriaValues( DAOUtil daoUtil, NotificationFilter notificationFilter )
    {
        int nIndex = 1;

        if ( notificationFilter.containsId( ) )
        {
            for ( Integer id : notificationFilter.getIds( ) )
            {
                daoUtil.setInt( nIndex++, id );
            }
        }
        if ( notificationFilter.containsDemandId( ) )
        {
            daoUtil.setString( nIndex++, notificationFilter.getDemandId( ) );
        }
        if ( notificationFilter.containsDemandTypeId( ) )
        {
            daoUtil.setString( nIndex++, notificationFilter.getDemandTypeId( ) );
        }
        if ( notificationFilter.containsCustomerId( ) )
        {
            daoUtil.setString( nIndex++, notificationFilter.getCustomerId( ) );
        }
        if ( notificationFilter.containsStartDate( ) )
        {
            daoUtil.setTimestamp( nIndex++, new Timestamp( notificationFilter.getStartDate( ) ) );
        }
        if ( notificationFilter.containsEndDate( ) )
        {
            daoUtil.setTimestamp( nIndex++, new Timestamp( notificationFilter.getEndDate( ) ) );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized Notification insert( Notification notification )
    {
        int nNotificationId = newPrimaryKey( );
        notification.setId( nNotificationId );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, NotificationStorePlugin.getPlugin( ) ) )
        {

            int nIndex = 1;

            daoUtil.setInt( nIndex++, notification.getId( ) );
            daoUtil.setString( nIndex++, notification.getDemand( ).getId( ) );
            daoUtil.setString( nIndex++, notification.getDemand( ).getTypeId( ) );
            
            String strCustomerId = StringUtils.EMPTY;           
            if( notification.getDemand( ).getCustomer( ) != null 
                    && StringUtils.isNotEmpty( notification.getDemand( ).getCustomer( ).getCustomerId( ) ) )
            {
                strCustomerId = notification.getDemand( ).getCustomer( ).getCustomerId( );
            }           
            daoUtil.setString( nIndex++, strCustomerId );
            
            daoUtil.setTimestamp( nIndex++, notification.getDate( ) > 0 ? new Timestamp( notification.getDate( ) ) : null );

            daoUtil.executeUpdate( );
        }

        return notification;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( int id )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, id );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByDemand( String strDemandId, String strDemandTypeId, String strCustomerId )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_DEMAND, NotificationStorePlugin.getPlugin( ) ) )
        {

            daoUtil.setString( 1, strDemandId );
            daoUtil.setString( 2, strDemandTypeId );
            daoUtil.setString( 3, strCustomerId );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * Generates a new primary key
     *
     * @return the primary key
     */
    private int newPrimaryKey( )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.executeQuery( );

            int nKey = 1;

            if ( daoUtil.next( ) )
            {
                nKey = daoUtil.getInt( 1 ) + 1;
            }

            return nKey;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Notification> loadById( int id )
    {
        NotificationFilter filter = new NotificationFilter( );
        List<Integer> list = new ArrayList<>( );
        list.add( id );
        filter.setIds( list );

        List<Notification> listNotifs = loadByFilter( filter );

        if ( listNotifs.size( ) == 1 )
        {
            return Optional.of( listNotifs.get( 0 ) );
        }

        return Optional.empty( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Notification> loadByIds( List<Integer> listIds )
    {
        if ( listIds != null && listIds.isEmpty( ) )
        {
            return new ArrayList<>( );
        }
        else
        {
            NotificationFilter filter = new NotificationFilter( );
            filter.setIds( listIds );

            return loadByFilter( filter );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Notification> loadByDemandAndDate( String strDemandId, String strDemandTypeId, long lDate )
    {
        NotificationFilter filter = new NotificationFilter( );
        filter.setDemandId( strDemandId );
        filter.setDemandTypeId( strDemandTypeId );
        filter.setStartDate( lDate );
        filter.setEndDate( lDate );

        return loadByFilter( filter );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> loadDistinctDemandTypeIds( )
    {
        List<String> listIds = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DISTINCT_DEMAND_TYPE_ID, NotificationStorePlugin.getPlugin( ) ) )
        {

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                String strId = daoUtil.getString( 1 );
                listIds.add( strId );
            }

            return listIds;
        }
    }

    /**
     * get notification list from dao
     * 
     * @param daoUtil
     * @return the list
     */
    private List<Notification> getNotificationsFromDao( DAOUtil daoUtil, NotificationFilter notificationFilter )
    {
        List<Notification> listNotifications = new ArrayList<>( );
        while ( daoUtil.next( ) )
        {
            Notification notification = new Notification( );
            notification.setId( daoUtil.getInt( COLUMN_NOTIFICATION_ID ) );
            notification.setDate( daoUtil.getTimestamp( COLUMN_DATE ) != null ? daoUtil.getTimestamp( COLUMN_DATE ).getTime ( ) : 0 );

            String strIdDemand = daoUtil.getString( COLUMN_DEMAND_ID );
            String strDemandTypeId = daoUtil.getString( COLUMN_DEMAND_TYPE_ID );
            String strCustomerId = daoUtil.getString( COLUMN_CUSTOMER_ID );
            
            notification.setDemand( DemandHome.getDemandByDemandIdAndTypeIdAndCustomerId( strIdDemand, strDemandTypeId, strCustomerId ) );
            setNotificationContent( notification, notificationFilter );
            
            Customer customer = new Customer ();
            customer.setCustomerId( daoUtil.getString( COLUMN_CUSTOMER ) );
            customer.setId( customer.getCustomerId( ) );
            notification.getDemand( ).setCustomer( customer );

            listNotifications.add( notification );
        }

        return listNotifications;
    }

    /**
     * Retrieval of notification content
     * 
     * @param notif
     */
    private void setNotificationContent( Notification notif, NotificationFilter notificationFilter )
    {
        List<NotificationContent> listNotificiationContent = NotificationContentHome.getNotificationContentsByIdAndTypeNotification( notif.getId( ),
                notificationFilter.getListNotificationType( ) );

        for ( NotificationContent notifContent : listNotificiationContent )
        {
            if ( EnumNotificationType.BACKOFFICE.name( ).equals( notifContent.getNotificationType( ) ) )
            {
                notif.setBackofficeNotification( convertToObject( notifContent, new TypeReference<BackofficeNotification>( )
                {
                } ) );
            }
            if ( EnumNotificationType.BROADCAST_EMAIL.name( ).equals( notifContent.getNotificationType( ) ) )
            {
                notif.setBroadcastEmail( convertToObject( notifContent, new TypeReference<List<BroadcastNotification>>( )
                {
                } ) );
            }
            if ( EnumNotificationType.CUSTOMER_EMAIL.name( ).equals( notifContent.getNotificationType( ) ) )
            {
                notif.setEmailNotification( convertToObject( notifContent, new TypeReference<EmailNotification>( )
                {
                } ) );
            }
            if ( EnumNotificationType.MYDASHBOARD.name( ).equals( notifContent.getNotificationType( ) ) )
            {
                notif.setMyDashboardNotification( convertToObject( notifContent, new TypeReference<MyDashboardNotification>( )
                {
                } ) );
            }
            if ( EnumNotificationType.SMS.name( ).equals( notifContent.getNotificationType( ) ) )
            {
                notif.setSmsNotification( convertToObject( notifContent, new TypeReference<SMSNotification>( )
                {
                } ) );
            }
        }
    }

    /**
     * 
     * @param <T>
     * @param notifContent
     * @param typeReference
     * @return
     */
    private <T> T convertToObject( NotificationContent notifContent, TypeReference<T> typeReference )
    {
        try
        {
            File file = FileService.getInstance( ).getFileStoreServiceProvider( notifContent.getFileStore( ) ).getFile( notifContent.getFileKey( ) );            
            
            String strNotification;
            
            if ( AppPropertiesService.getPropertyBoolean( PROPERTY_DECOMPRESS_NOTIFICATION, false ) )
            {
                strNotification = StringUtil.decompress( file.getPhysicalFile( ).getValue( ) );
            }
            else
            {
                strNotification = new String( file.getPhysicalFile( ).getValue( ), StandardCharsets.UTF_8 );
            }
            return _mapper.readValue( strNotification, typeReference );

        }
        catch( FileServiceException | IOException e )
        {
            AppLogService.error( "Error while reading JSON of notification " + notifContent.getIdNotification( ), e );
        }

        return null;
    }

    @Override
    public List<Notification> loadByDemandIdTypeIdCustomerId( String strDemandId, String strDemandTypeId, String strCustomerId )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_DEMAND_CUSTOMER_TYPE, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.setString( 1, strDemandId );
            daoUtil.setString( 2, strDemandTypeId );
            daoUtil.setString( 3, strCustomerId );

            daoUtil.executeQuery( );

            List<Notification> listNotifications = new ArrayList<>( );

            while ( daoUtil.next( ) )
            {
                Notification notification = new Notification( );
                notification.setId( daoUtil.getInt( COLUMN_NOTIFICATION_ID ) );
                notification.setDate( daoUtil.getTimestamp( COLUMN_DATE ) != null ? daoUtil.getTimestamp( COLUMN_DATE ).getTime ( ) : 0 );

                notification.setDemand( DemandHome.getDemandByDemandIdAndTypeIdAndCustomerId( strDemandId, strDemandTypeId, strCustomerId ) );
                setNotificationContent( notification, new NotificationFilter( ) );
                
                Customer customer = new Customer ();
                customer.setCustomerId( daoUtil.getString( COLUMN_CUSTOMER ) );
                notification.getDemand( ).setCustomer( customer );
                
                listNotifications.add( notification );
            }

            return listNotifications;
        }
    }

    @Override
    public Notification loadLastNotifByDemandIdAndDemandTypeId( String strDemandId, String strDemandTypeId )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_LAST_NOTIFICATION, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.setString( 1, strDemandId );
            daoUtil.setString( 2, strDemandTypeId );

            daoUtil.executeQuery( );

            Notification notification = null;

            while ( daoUtil.next( ) )
            {
                notification = new Notification( );
                notification.setId( daoUtil.getInt( COLUMN_NOTIFICATION_ID ) );
                notification.setDate( daoUtil.getTimestamp( COLUMN_DATE ) != null ? daoUtil.getTimestamp( COLUMN_DATE ).getTime ( ) : 0 );
                setNotificationContent( notification, new NotificationFilter( ) );

            }

            return notification;
        }
    }
}
