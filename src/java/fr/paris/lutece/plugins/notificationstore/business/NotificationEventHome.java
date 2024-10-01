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

import fr.paris.lutece.plugins.grubusiness.business.notification.INotificationEventDAO;
import fr.paris.lutece.plugins.grubusiness.business.notification.NotificationEvent;
import fr.paris.lutece.plugins.grubusiness.business.notification.NotificationFilter;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import java.util.List;
import java.util.Optional;

/**
 * This class provides instances management methods (create, find, ...) for Notification Event object
 */
public final class NotificationEventHome
{

    private static INotificationEventDAO _dao = SpringContextService.getBean( "notificationstore.notificationEventDao" );

    /**
     * Private constructor
     */
    private NotificationEventHome( )
    {
    }

    /**
     * Find the demand's NotificationEvents
     * 
     * @param strDemandId
     * @param strDemandTypeId
     * @return the NotificationEvent list
     */
    public static List<NotificationEvent> findByDemand( String strDemandId, String strDemandTypeId )
    {
        return _dao.loadByDemand( strDemandId, strDemandTypeId );
    }

    /**
     * Find the demand's NotificationEvents
     * 
     * @param strDemandId
     * @param strDemandTypeId
     * @return the NotificationEvent list
     */
    public static List<NotificationEvent> findByNotification( String strDemandId, String strDemandTypeId, long lNotificationDate )
    {
        return _dao.loadByNotification( strDemandId, strDemandTypeId, lNotificationDate );
    }

    /**
     * Finds a NotificationEvent with the specified id
     * 
     * @param strId
     * @return the NotificationEvent
     */
    public static Optional<NotificationEvent> getById( int nId )
    {
        return _dao.loadById( nId );
    }

    /**
     * Finds a NotificationEvent with the specified id
     * 
     * @param strId
     * @return the NotificationEvent
     */
    public static List<NotificationEvent> getByIds( List<Integer> listIds )
    {
        return _dao.loadByIds( listIds );
    }

    /**
     * Find the NotificationEvents according to the filter
     * 
     * @param notificationFilter
     * @return the NotificationEvent list
     */
    public static List<NotificationEvent> findByFilter( NotificationFilter notificationFilter )
    {
        return _dao.loadByFilter( notificationFilter );
    }

    /**
     * Find the NotificationEvents according to the filter
     * 
     * @param notificationFilter
     * @return the NotificationEvent list
     */
    public static List<Integer> findIdsByFilter( NotificationFilter notificationFilter )
    {
        return _dao.loadIdsByFilter( notificationFilter );
    }

    /**
     * Purge the NotificationEvents after N days
     * 
     * @param the
     *            nb of days
     * @return a success message
     */
    public static String purge( int nbDaysBeforePurge )
    {
        long today = System.currentTimeMillis( );
        long purgeBeforeDate = today - ( (long) nbDaysBeforePurge * 1000 * 3600 * 24 );

        return _dao.deleteBeforeDate( purgeBeforeDate );
    }
    
    /**
     * Delete by customer id
     * 
     * @param strCustomerId
     */
    public static void deleteByCustomerId( String strCustomerId )
    {
        _dao.deleteByCustomerId( strCustomerId );
    }
}
