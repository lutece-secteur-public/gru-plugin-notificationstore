/*
 * Copyright (c) 2002-2025, City of Paris
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

import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.grubusiness.business.customer.Customer;
import fr.paris.lutece.plugins.grubusiness.business.demand.Demand;
import fr.paris.lutece.plugins.grubusiness.business.notification.EnumNotificationType;
import fr.paris.lutece.plugins.grubusiness.business.notification.Event;
import fr.paris.lutece.plugins.grubusiness.business.notification.INotificationEventDAO;
import fr.paris.lutece.plugins.grubusiness.business.notification.NotificationEvent;
import fr.paris.lutece.plugins.grubusiness.business.notification.NotificationFilter;
import fr.paris.lutece.plugins.notificationstore.service.NotificationStorePlugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for NotificationEvent objects
 */
public final class NotificationEventDAO implements INotificationEventDAO
{
    // Constants
    private static final String SQL_QUERY_SELECTALL = "SELECT id, event_date, type, status, redelivry, message, msg_id, demand_id, demand_type_id, customer_id, notification_date FROM notificationstore_notification_event ";
    private static final String SQL_QUERY_INSERT = "INSERT INTO notificationstore_notification_event ( event_date, type, status, redelivry, message, demand_id, demand_type_id, customer_id, notification_date, msg_id ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM notificationstore_notification_event WHERE id = ? ";
    private static final String SQL_QUERY_DELETE_BY_CUSTOMER_ID = "DELETE FROM notificationstore_notification_event WHERE customer_id = ? ";
    private static final String SQL_QUERY_DELETE_BY_DATE = "DELETE FROM notificationstore_notification_event WHERE event_date < ? ";
    private static final String SQL_QUERY_SELECT_BY_DEMAND = SQL_QUERY_SELECTALL + " WHERE demand_id = ? AND demand_type_id = ? ";
    private static final String SQL_QUERY_SELECT_BY_NOTIFICATION = SQL_QUERY_SELECTALL
            + " WHERE demand_id = ? AND demand_type_id = ? and notification_date = ? ";
    private static final String SQL_QUERY_SELECT_BY_FILTER = SQL_QUERY_SELECTALL + " WHERE 1  ";
    private static final String SQL_QUERY_FILTER_BY_ID = " AND id in ( %s ) ";
    private static final String SQL_QUERY_FILTER_BY_DEMAND_ID = " AND demand_id = ? ";
    private static final String SQL_QUERY_FILTER_BY_DEMAND_TYPE_ID = " AND demand_type_id = ? ";
    private static final String SQL_QUERY_FILTER_BY_STARTDATE = " AND event_date >= ? ";
    private static final String SQL_QUERY_FILTER_BY_ENDDATE = " AND event_date <= ? ";
    private static final String SQL_QUERY_FILTER_BY_STATUS = " AND status = ? ";
    private static final String SQL_QUERY_FILTER_BY_TYPE = " AND type in ( ";
    private static final String SQL_QUERY_FILTER_ORDER_BY = " ORDER BY event_date DESC ";

    /**
     * {@inheritDoc }
     */
    @Override
    public NotificationEvent insert( NotificationEvent notificationEvent )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, NotificationStorePlugin.getPlugin( ) ) )
        {
            int nIndex = 1;
            daoUtil.setTimestamp( nIndex++,
                    notificationEvent.getEvent( ).getEventDate( ) > 0 ? new Timestamp( notificationEvent.getEvent( ).getEventDate( ) ) : null );
            daoUtil.setString( nIndex++, notificationEvent.getEvent( ).getType( ) );
            daoUtil.setString( nIndex++, notificationEvent.getEvent( ).getStatus( ) );
            daoUtil.setInt( nIndex++, notificationEvent.getEvent( ).getRedelivry( ) );
            daoUtil.setString( nIndex++, notificationEvent.getEvent( ).getMessage( ) );
            daoUtil.setString( nIndex++, String.valueOf( notificationEvent.getDemand( ).getId( ) ) );
            daoUtil.setString( nIndex++, String.valueOf( notificationEvent.getDemand( ).getTypeId( ) ) );
            String strCustomerId = StringUtils.EMPTY;

            if ( notificationEvent.getDemand( ).getCustomer( ) != null
                    && StringUtils.isNotEmpty( notificationEvent.getDemand( ).getCustomer( ).getCustomerId( ) ) )
            {
                strCustomerId = notificationEvent.getDemand( ).getCustomer( ).getCustomerId( );
            }
            daoUtil.setString( nIndex++, strCustomerId );
            daoUtil.setTimestamp( nIndex++, notificationEvent.getNotificationDate( ) > 0 ? new Timestamp( notificationEvent.getNotificationDate( ) ) : null );
            daoUtil.setString( nIndex++, notificationEvent.getMsgId( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                notificationEvent.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }

            return notificationEvent;
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<NotificationEvent> loadById( int nKey )
    {
        List<Integer> list = new ArrayList<>( );
        list.add( nKey );

        List<NotificationEvent> listEvent = loadByIds( list );

        if ( listEvent.size( ) == 1 )
        {
            return Optional.of( listEvent.get( 0 ) );
        }
        else
        {
            return Optional.empty( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<NotificationEvent> loadByIds( List<Integer> listIds )
    {
        if ( listIds != null && !listIds.isEmpty( ) )
        {
            NotificationFilter filter = new NotificationFilter( );
            filter.setIds( listIds );

            return loadByFilter( filter );
        }
        else
        {
            return new ArrayList<>( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nKey )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeUpdate( );
            daoUtil.free( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void deleteByCustomerId( String strCustomerId )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_CUSTOMER_ID, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.setString( 1, strCustomerId );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<NotificationEvent> loadByDemand( String strDemandId, String strDemandTypeId )
    {
        List<NotificationEvent> notificationEventList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_DEMAND, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.setString( 1, strDemandId );
            daoUtil.setString( 2, strDemandTypeId );

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                NotificationEvent notificationEvent = getItemFromDao( daoUtil );

                notificationEventList.add( notificationEvent );
            }

            daoUtil.free( );
            return notificationEventList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<NotificationEvent> loadByNotification( String strDemandId, String strDemandTypeId, long lNotificationDate )
    {
        List<NotificationEvent> notificationEventList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_NOTIFICATION, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.setString( 1, strDemandId );
            daoUtil.setString( 2, strDemandTypeId );
            daoUtil.setTimestamp( 3, new Timestamp( lNotificationDate ) );

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                NotificationEvent notificationEvent = getItemFromDao( daoUtil );

                notificationEventList.add( notificationEvent );
            }

            daoUtil.free( );
            return notificationEventList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<NotificationEvent> loadByFilter( NotificationFilter filter )
    {

        List<NotificationEvent> notificationEventList = new ArrayList<>( );
        StringBuilder strSql = new StringBuilder( SQL_QUERY_SELECT_BY_FILTER );

        getFilterCriteriaClauses( strSql, filter );

        try ( DAOUtil daoUtil = new DAOUtil( strSql.toString( ), NotificationStorePlugin.getPlugin( ) ) )
        {
            addFilterCriteriaValues( daoUtil, filter );

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                NotificationEvent notificationEvent = getItemFromDao( daoUtil );

                notificationEventList.add( notificationEvent );
            }

            daoUtil.free( );
            return notificationEventList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> loadIdsByFilter( NotificationFilter filter )
    {
        List<Integer> notificationEventList = new ArrayList<>( );
        StringBuilder strSql = new StringBuilder( SQL_QUERY_SELECT_BY_FILTER );

        getFilterCriteriaClauses( strSql, filter );

        try ( DAOUtil daoUtil = new DAOUtil( strSql.toString( ), NotificationStorePlugin.getPlugin( ) ) )
        {
            addFilterCriteriaValues( daoUtil, filter );

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                notificationEventList.add( daoUtil.getInt( 1 ) );
            }

            daoUtil.free( );
            return notificationEventList;
        }
    }

    /**
     * get notification event from daoUtil
     * 
     * @param daoUtil
     * @return the item
     */
    private NotificationEvent getItemFromDao( DAOUtil daoUtil )
    {
        NotificationEvent notificationEvent = new NotificationEvent( );
        int nIndex = 1;

        notificationEvent.setId( daoUtil.getInt( nIndex++ ) );

        Event event = new Event( );
        Timestamp eventDate = daoUtil.getTimestamp( nIndex++ );
        event.setEventDate( eventDate != null ? eventDate.getTime( ) : 0 );
        event.setType( daoUtil.getString( nIndex++ ) );
        event.setStatus( daoUtil.getString( nIndex++ ) );
        event.setRedelivry( daoUtil.getInt( nIndex++ ) );
        event.setMessage( daoUtil.getString( nIndex++ ) );
        notificationEvent.setEvent( event );

        notificationEvent.setMsgId( daoUtil.getString( nIndex++ ) );

        Demand demand = new Demand( );
        demand.setId( daoUtil.getString( nIndex++ ) );
        demand.setTypeId( daoUtil.getString( nIndex++ ) );

        Customer customer = new Customer( );
        customer.setCustomerId( daoUtil.getString( nIndex++ ) );
        customer.setId( customer.getCustomerId( ) );

        demand.setCustomer( customer );

        notificationEvent.setDemand( demand );

        notificationEvent.setNotificationDate( daoUtil.getTimestamp( nIndex ) != null ? daoUtil.getTimestamp( nIndex ).getTime( ) : 0 );

        return notificationEvent;
    }

    /**
     * build sql filter
     * 
     * @param sql
     * @param filter
     */
    private void getFilterCriteriaClauses( StringBuilder sbSql, NotificationFilter filter )
    {

        if ( filter.containsId( ) )
        {
            String sql = String.format( SQL_QUERY_FILTER_BY_ID, filter.getIds( ).stream( ).map( v -> "?" ).collect( Collectors.joining( ", " ) ) );

            sbSql.append( sql );
        }
        if ( filter.containsDemandId( ) )
        {
            sbSql.append( SQL_QUERY_FILTER_BY_DEMAND_ID );
        }
        if ( filter.containsDemandTypeId( ) )
        {
            sbSql.append( SQL_QUERY_FILTER_BY_DEMAND_TYPE_ID );
        }

        if ( filter.containsStartDate( ) )
        {
            sbSql.append( SQL_QUERY_FILTER_BY_STARTDATE );
        }

        if ( filter.containsEndDate( ) )
        {
            sbSql.append( SQL_QUERY_FILTER_BY_ENDDATE );
        }

        if ( !StringUtils.isEmpty( filter.getEventStatus( ) ) )
        {
            sbSql.append( SQL_QUERY_FILTER_BY_STATUS );
        }

        if ( filter.containsNotificationTypeFilter( ) )
        {
            sbSql.append( SQL_QUERY_FILTER_BY_TYPE );
            for ( int i = 0; i < filter.getListNotificationType( ).size( ) - 1; i++ )
            {
                sbSql.append( "?, " );
            }
            sbSql.append( "?)" );
        }

        sbSql.append( SQL_QUERY_FILTER_ORDER_BY );
    }

    /**
     * fill DAO with values
     * 
     * @param daoUtil
     * @param filter
     */
    private void addFilterCriteriaValues( DAOUtil daoUtil, NotificationFilter filter )
    {

        int i = 1;
        if ( filter.containsId( ) )
        {
            for ( Integer id : filter.getIds( ) )
            {
                daoUtil.setInt( i++, id );
            }
        }
        if ( filter.containsDemandId( ) )
        {
            daoUtil.setString( i++, filter.getDemandId( ) );
        }
        if ( filter.containsDemandTypeId( ) )
        {
            daoUtil.setString( i++, filter.getDemandTypeId( ) );
        }
        if ( filter.containsStartDate( ) )
        {
            daoUtil.setTimestamp( i++, new Timestamp( filter.getStartDate( ) ) );
        }
        if ( filter.containsEndDate( ) )
        {
            daoUtil.setTimestamp( i++, new Timestamp( filter.getEndDate( ) ) );
        }
        if ( !StringUtils.isEmpty( filter.getEventStatus( ) ) )
        {
            daoUtil.setString( i++, filter.getEventStatus( ) );
        }
        if ( filter.getListNotificationType( ) != null && !filter.getListNotificationType( ).isEmpty( ) )
        {
            for ( EnumNotificationType type : filter.getListNotificationType( ) )
            {
                daoUtil.setString( i++, type.name( ) );
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    public String deleteBeforeDate( long lDate )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_DATE, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.setTimestamp( 1, new Timestamp( lDate ) );
            daoUtil.executeUpdate( );
            daoUtil.free( );
        }

        return "Success";
    }
}
