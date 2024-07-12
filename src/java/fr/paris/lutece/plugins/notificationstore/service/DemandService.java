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
package fr.paris.lutece.plugins.notificationstore.service;

import fr.paris.lutece.plugins.grubusiness.business.demand.Demand;
import fr.paris.lutece.plugins.grubusiness.business.demand.DemandType;
import fr.paris.lutece.plugins.grubusiness.business.demand.IDemandDAO;
import fr.paris.lutece.plugins.grubusiness.business.demand.IDemandListener;
import fr.paris.lutece.plugins.grubusiness.business.demand.IDemandServiceProvider;
import fr.paris.lutece.plugins.grubusiness.business.demand.IDemandTypeDAO;
import fr.paris.lutece.plugins.grubusiness.business.demand.IDemandStatusDAO;
import fr.paris.lutece.plugins.grubusiness.business.demand.DemandStatus;
import fr.paris.lutece.plugins.grubusiness.business.notification.INotificationDAO;
import fr.paris.lutece.plugins.grubusiness.business.notification.INotificationEventDAO;
import fr.paris.lutece.plugins.grubusiness.business.notification.INotificationListener;
import fr.paris.lutece.plugins.grubusiness.business.notification.Notification;
import fr.paris.lutece.plugins.grubusiness.business.notification.NotificationEvent;
import fr.paris.lutece.plugins.grubusiness.business.notification.NotificationFilter;
import fr.paris.lutece.plugins.notificationstore.business.NotificationContentHome;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * This class manages demands
 *
 */
public class DemandService implements IDemandServiceProvider
{

    private IDemandDAO _demandDao;
    private INotificationDAO _notificationDao;
    private INotificationEventDAO _notificationEventDao;
    private IDemandTypeDAO _demandTypeDao;
    private IDemandStatusDAO _statusDao;

    /**
     * Finds demands for the specified customer id
     * 
     * @param strCustomerId
     *            the customer id
     * @return the demands. An empty collection is returned if no demand has been found
     */
    public Collection<Demand> findByCustomerId( String strCustomerId )
    {
        Collection<Demand> collectionDemands = _demandDao.loadByCustomerId( strCustomerId );

        for ( Demand demand : collectionDemands )
        {
            if ( demand != null )
            {
                demand.setNotifications( _notificationDao.loadByDemand( demand.getDemandId( ), demand.getTypeId( ) ) );
            }
        }
        return collectionDemands;
    }

    /**
     * Finds demands for the specified reference
     * 
     * @param strReference
     *            the reference
     * @return the demands. An empty collection is returned if no demand has been found
     */
    public Collection<Demand> findByReference( String strReference )
    {
        Collection<Demand> collectionDemands = _demandDao.loadByReference( strReference );

        for ( Demand demand : collectionDemands )
        {
            if ( demand != null )
            {
                demand.setNotifications( _notificationDao.loadByDemand( demand.getDemandId( ), demand.getTypeId( ) ) );
            }
        }
        return collectionDemands;
    }

    /**
     * Finds a demand for the specified id and type id
     * 
     * @param strDemandId
     *            the demand id
     * @param strDemandTypeId
     *            the demand type id
     * @return the demand if found, {@code null} otherwise
     */
    public Demand findByPrimaryKey( String strDemandId, String strDemandTypeId )
    {
        Demand demand = _demandDao.loadByDemandIdAndTypeId( strDemandId, strDemandTypeId );

        if ( demand != null )
        {
            demand.setNotifications( _notificationDao.loadByDemand( strDemandId, strDemandTypeId ) );
        }

        return demand;
    }

    /**
     * Creates a demand
     * 
     * @param demand
     *            the demand to create
     * @return the created demand
     */
    public Demand create( Demand demand )
    {
        Demand demandDao = _demandDao.insert( demand );
        for ( IDemandListener demandListener : SpringContextService.getBeansOfType( IDemandListener.class ) )
        {
            demandListener.onCreateDemand( demandDao );
        }
        return demandDao;
    }

    /**
     * Creates a notification
     * 
     * @param notification
     *            the notification to create
     * @return the created notification
     */
    public Notification create( Notification notification )
    {
        Notification notificationDao = _notificationDao.insert( notification );

        NotificationContentHome.create( notification );

        for ( INotificationListener iNotificationListener : SpringContextService.getBeansOfType( INotificationListener.class ) )
        {
            iNotificationListener.onCreateNotification( notificationDao );
        }
        return notificationDao;
    }

    /**
     * Creates a notification event
     * 
     * @param notificationEvent
     * @return the created notification event
     */
    public NotificationEvent create( NotificationEvent notificationEvent )
    {
        NotificationEvent notificationDao = _notificationEventDao.insert( notificationEvent );

        return notificationDao;
    }

    /**
     * Updates a demand
     * 
     * @param demand
     *            the demand to update
     * @return the updated demand
     */
    public Demand update( Demand demand )
    {
        Demand demandDao = _demandDao.store( demand );
        for ( IDemandListener iDemandListener : SpringContextService.getBeansOfType( IDemandListener.class ) )
        {
            iDemandListener.onUpdateDemand( demandDao );
        }
        return demandDao;
    }

    /**
     * Removes a demand with the specified id and type id
     * 
     * @param strDemandId
     *            the demand id
     * @param strDemandTypeId
     *            the demand type id
     */
    public void remove( String strDemandId, String strDemandTypeId )
    {
        _notificationDao.deleteByDemand( strDemandId, strDemandTypeId );
        for ( INotificationListener iNotificationListener : SpringContextService.getBeansOfType( INotificationListener.class ) )
        {
            iNotificationListener.onDeleteDemand( strDemandId, strDemandTypeId );
        }
        _demandDao.delete( strDemandId, strDemandTypeId );
        for ( IDemandListener iDemandListener : SpringContextService.getBeansOfType( IDemandListener.class ) )
        {
            iDemandListener.onDeleteDemand( strDemandId, strDemandTypeId );
        }
    }

    /**
     * Finds events by date and demand_type_id and status
     * 
     * @param dStart
     * @param dEnd
     * @param strDemandTypeId
     * @param strStatus
     * 
     * @return the demands. An empty list is returned if no event has been found
     */
    public List<NotificationEvent> findEventsByDateAndDemandTypeIdAndStatus( long dStart, long dEnd, String strDemandTypeId, String strStatus )
    {
        NotificationFilter notificationFilter = new NotificationFilter( );

        notificationFilter.setStartDate( dStart );
        notificationFilter.setEndDate( dEnd );
        notificationFilter.setDemandTypeId( strDemandTypeId );
        notificationFilter.setEventStatus( strStatus );

        return _notificationEventDao.loadByFilter( notificationFilter );
    }

    /**
     * get demand Type list
     * 
     * @return the demand type list
     */
    public List<DemandType> getDemandTypesList( )
    {

        return _demandTypeDao.selectDemandTypesList( );
    }

    /**
     * get demand Type list
     * 
     * @return the demand type list
     */
    public Optional<DemandType> getDemandType( int id )
    {

        return _demandTypeDao.load( id );
    }

    /**
     * 
     * get demand Ids by CustomerId and DemandTypeId
     * 
     * @return the demand id list
     */
    public List<Integer> getIdsByCustomerIdAndDemandTypeId( String strCustomerId, String strNotificationType, String strIdDemandType )
    {
        return _demandDao.loadIdsByCustomerIdAndIdDemandType( strCustomerId, strNotificationType, strIdDemandType );
    }

    @Override
    public void setDemandDao( IDemandDAO dao )
    {
        _demandDao = dao;
    }

    @Override
    public void setDemandTypeDao( IDemandTypeDAO dao )
    {
        _demandTypeDao = dao;
    }

    @Override
    public void setNotificationEventDao( INotificationEventDAO dao )
    {
        _notificationEventDao = dao;
    }

    @Override
    public void setNotificationDao( INotificationDAO dao )
    {
        _notificationDao = dao;
    }

    @Override
    public void setStatusDao( IDemandStatusDAO dao )
    {
        _statusDao = dao;
    }

    @Override
    public Optional<DemandStatus> getStatusByLabel( String strStatusLabel )
    {
        return _statusDao.loadByStatus( strStatusLabel );
    }

}
