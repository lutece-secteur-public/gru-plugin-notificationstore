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

import java.util.List;
import java.util.Optional;

import fr.paris.lutece.plugins.grubusiness.business.notification.INotificationDAO;
import fr.paris.lutece.plugins.grubusiness.business.notification.Notification;
import fr.paris.lutece.plugins.grubusiness.business.notification.NotificationFilter;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

/**
 * This class provides instances management methods (create, find, ...) for Notification objects
 */
public final class NotificationHome
{
    // Static variable pointed at the DAO instance
    private static INotificationDAO _dao = SpringContextService.getBean( "notificationstore.notificationDao" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private NotificationHome( )
    {
    }

    /**
     * Find the demand's notifications
     * 
     * @param strDemandId
     * @param strDemandTypeId
     * @return the notification list
     */
    public static List<Notification> findByDemand( String strDemandId, String strDemandTypeId, String strCustomerId )
    {
        return _dao.loadByDemand( strDemandId, strDemandTypeId, strCustomerId );
    }

    /**
     * Find the notification by demand id's and date
     * 
     * @param strDemandId
     * @param strDemandTypeId
     * @param lDate
     * @return the notification list
     */
    public static List<Notification> findByNotification( String strDemandId, String strDemandTypeId, long lDate )
    {
        return _dao.loadByDemandAndDate( strDemandId, strDemandTypeId, lDate );
    }

    /**
     * Finds a notification with the specified id
     * 
     * @param strId
     * @return the notification
     */
    public static Optional<Notification> getById( int id )
    {
        return _dao.loadById( id );
    }

    /**
     * search notifications by filter
     * 
     * @param filter
     * @return the notification list
     */
    public static List<Notification> findByFilter( NotificationFilter filter )
    {
        return _dao.loadByFilter( filter );
    }

    /**
     * search notifications Ids by filter
     * 
     * @param filter
     * @return the notification list
     */
    public static List<Integer> findIdsByFilter( NotificationFilter filter )
    {
        return _dao.loadIdsByFilter( filter );
    }

    /**
     * Find the notifications according to the filter
     * 
     * @param notificationFilter
     * @return the notification list
     */
    public static List<Notification> getByFilter( NotificationFilter notificationFilter )
    {
        return _dao.loadByFilter( notificationFilter );
    }

    /**
     * Find the notifications by demand id, type id, customer id
     * 
     * @param strDemandId
     * @param strDemandTypeId
     * @param strCustomerId
     * @param filter
     * @return the notification list
     */
    public static List<Notification> getByDemandIdTypeIdCustomerId( String strDemandId, String strDemandTypeId, String strCustomerId,
            NotificationFilter filter )
    {
        return _dao.loadByDemandIdTypeIdCustomerId( strDemandId, strDemandTypeId, strCustomerId, filter );
    }

    /**
     * Find the notifications according to the filter
     * 
     * @param notificationFilter
     * @return the notification list
     */
    public static List<Notification> getByIds( List<Integer> listIds )
    {
        return _dao.loadByIds( listIds );
    }

    /**
     * Get distinct demand type ids list
     * 
     * @return the list
     */
    public static ReferenceList getDemandTypeIds( )
    {
        List<String> strList = _dao.loadDistinctDemandTypeIds( );

        ReferenceList refList = new ReferenceList( );
        for ( String strId : strList )
        {
            refList.addItem( strId, strId );
        }

        return refList;
    }

    /**
     * Create an instance of the Notification class
     * 
     * @param Notification
     *            The instance of the Notification which contains the informations to store
     * @return The instance of Notification which has been created with its primary key.
     */
    public static Notification create( Notification notification )
    {
        _dao.insert( notification );

        return notification;
    }

    /**
     * Remove the Notification whose identifier is specified in parameter
     * 
     * @param nKey
     *            The Notification Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey );
    }

    /**
     * test if a notif exists with the specified demand_type_id
     * 
     * @param nDemandTypeId
     * @return true if exists
     */
    public static boolean existsNotificationWithDemandTypeId( int nDemandTypeId )
    {
        return _dao.existsNotificationWithDemandTypeId( nDemandTypeId );
    }

    /**
     * Get last notification by demand id and demand type id
     * 
     * @param strDemandId
     * @param strDemandTypeId
     * @return last notification
     */
    public static Notification getLastNotifByDemandIdAndDemandTypeId( String strDemandId, String strDemandTypeId )
    {
        return _dao.loadLastNotifByDemandIdAndDemandTypeId( strDemandId, strDemandTypeId );
    }

    /**
     * Reassign notifications
     * 
     * @param strOldCustomerId
     * @param strNewCustomerId
     */
    public static void reassignNotifications( String strOldCustomerId, String strNewCustomerId )
    {
        _dao.reassignNotifications( strOldCustomerId, strNewCustomerId );
    }
}
