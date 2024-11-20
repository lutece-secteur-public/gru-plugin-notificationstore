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
import fr.paris.lutece.plugins.grubusiness.business.demand.ITemporaryStatusDAO;
import fr.paris.lutece.plugins.grubusiness.business.demand.TemporaryStatus;
import fr.paris.lutece.plugins.grubusiness.business.notification.INotificationDAO;
import fr.paris.lutece.plugins.grubusiness.business.notification.INotificationEventDAO;
import fr.paris.lutece.plugins.grubusiness.business.notification.INotificationListener;
import fr.paris.lutece.plugins.grubusiness.business.notification.Notification;
import fr.paris.lutece.plugins.grubusiness.business.notification.NotificationEvent;
import fr.paris.lutece.plugins.grubusiness.business.notification.NotificationFilter;
import fr.paris.lutece.plugins.notificationstore.business.DemandHome;
import fr.paris.lutece.plugins.notificationstore.business.DemandTypeHome;
import fr.paris.lutece.plugins.notificationstore.business.NotificationContent;
import fr.paris.lutece.plugins.notificationstore.business.NotificationContentHome;
import fr.paris.lutece.plugins.notificationstore.business.NotificationEventHome;
import fr.paris.lutece.plugins.notificationstore.business.NotificationHome;
import fr.paris.lutece.portal.service.cache.AbstractCacheableService;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.sql.TransactionManager;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * This class manages demands
 *
 */
public class DemandService  extends AbstractCacheableService implements IDemandServiceProvider 
{

    private IDemandDAO _demandDao;
    private INotificationDAO _notificationDao;
    private INotificationEventDAO _notificationEventDao;
    private ITemporaryStatusDAO _statusDao;

    private static final String SERVICE_NAME = "DemandRefCacheService";
	private static final String DEMAND_TYPE_CACHE_PREFIX = "DEMAND_TYPE_";
	private static final String DEMAND_TYPE_LIST_CACHE_KEY = "DEMAND_TYPE_LIST";
    
	/**
	 * Constructor
	 * 
	 */
    public DemandService() {
		super();
		
		initCache();
	}
    
	/**
	 * Constructor
	 * 
	 * @param _demandDao
	 * @param _notificationDao
	 * @param _notificationEventDao
	 * @param _statusDao
	 */
    public DemandService(IDemandDAO _demandDao, INotificationDAO _notificationDao,
			INotificationEventDAO _notificationEventDao, ITemporaryStatusDAO _statusDao) {
		super();
		this._demandDao = _demandDao;
		this._notificationDao = _notificationDao;
		this._notificationEventDao = _notificationEventDao;
		this._statusDao = _statusDao;
		
		initCache();
	}

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
                demand.setNotifications( _notificationDao.loadByDemand( demand.getId( ), demand.getTypeId( ), demand.getCustomer( ).getConnectionId( ) ) );
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
                demand.setNotifications( _notificationDao.loadByDemand( demand.getId( ), demand.getTypeId( ), demand.getCustomer( ).getCustomerId( ) ) );
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
    public Demand findByPrimaryKey( String strDemandId, String strDemandTypeId, String strCustomerId )
    {
        Demand demand = _demandDao.loadByDemandIdAndTypeIdAndCustomerId( strDemandId, strDemandTypeId, strCustomerId );

        if ( demand != null )
        {
            demand.setNotifications( _notificationDao.loadByDemandIdTypeIdCustomerId( strDemandId, strDemandTypeId, strCustomerId ) );
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
     * Update status demand 
     * 
     * @param nNewStatusId
     *            the new status id
     * @param nTemporaryStatusId
     *            To find Demands that are linked to notifications that have the temporary status in parameter
     */
    public void updateDemandsStatusId( int nNewStatusId, int nTemporaryStatusId )
    {
        _demandDao.updateDemandsStatusId( nNewStatusId, nTemporaryStatusId );
    }

    /**
     * Removes a demand with the specified id and type id
     * 
     * @param strDemandId
     *            the demand id
     * @param strDemandTypeId
     *            the demand type id
     */
    public void remove( String strDemandId, String strDemandTypeId, String strCustomerId )
    {
        _notificationDao.deleteByDemand( strDemandId, strDemandTypeId, strCustomerId );
        for ( INotificationListener iNotificationListener : SpringContextService.getBeansOfType( INotificationListener.class ) )
        {
            iNotificationListener.onDeleteDemand( strDemandId, strDemandTypeId );
        }
        _demandDao.delete( strDemandId, strDemandTypeId, strCustomerId );
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
    	List<DemandType> demandTypeList = (List<DemandType>) getFromCache( DEMAND_TYPE_LIST_CACHE_KEY );
    	if ( demandTypeList == null )
    	{
    		demandTypeList = DemandTypeHome.getDemandTypesList( );
    		if ( demandTypeList != null )
    		{
    			putInCache(DEMAND_TYPE_LIST_CACHE_KEY, demandTypeList);
    		}
    	}
    	
        return demandTypeList;
    }

    /**
     * get demand Type list
     * 
     * @return the demand type list
     */
    public Optional<DemandType> getDemandType( String type_id )
    {
    	DemandType dt = (DemandType) getFromCache( DEMAND_TYPE_CACHE_PREFIX + type_id );
    	
    	if ( dt != null )
    	{
    		return Optional.of( dt );
    	}
    	else
    	{
    		Optional<DemandType> odt = DemandTypeHome.getDemandType( type_id );
    		if ( odt.isPresent( ) ) 
    		{
    			putInCache( DEMAND_TYPE_CACHE_PREFIX + type_id, odt.get( ) );
    		}
    		return odt;
    	}
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
    public void setStatusDao( ITemporaryStatusDAO dao )
    {
        _statusDao = dao;
    }

    @Override
    public Optional<TemporaryStatus> getStatusByLabel( String strStatusLabel )
    {
        return _statusDao.loadByStatus( strStatusLabel );
    }

    @Override
    public void deleteAllDemandByCustomerId( String strCustomerId )
    {
        try
        {
            //Début de la transaction
            TransactionManager.beginTransaction( null );
            
            // Notifications
            List<Notification> listNotification = NotificationHome.findByDemand( null, null, strCustomerId );
            for( Notification notification : listNotification )
            {                 
                List<NotificationContent> listNotificationContent = NotificationContentHome.getNotificationContentsByIdNotification( notification.getId( ) );
                
                for( NotificationContent notifContent : listNotificationContent )
                {
                    //Remove File
                    FileService.getInstance( ).getFileStoreServiceProvider( notifContent.getFileStore( ) ).delete( notifContent.getFileKey( ) );

                    //Remove notification content
                    NotificationContentHome.remove( notifContent.getId( ) );
                }
                //Remove notification
                NotificationHome.remove( notification.getId( ) );
            }
            
            // Demands
            Collection<Demand> demands = DemandHome.getDemandIdCustomer( strCustomerId );
            for( Demand demand :  demands )
            {
                //Remove demand
                DemandHome.deleteByUid( demand.getUID( ) );
            }
            
            // Events
            NotificationEventHome.deleteByCustomerId( strCustomerId );
            
            //Commit de la transaction
            TransactionManager.commitTransaction( null );
        }
        catch (Exception e)
        {
            //Roll back
            TransactionManager.rollBack( null );
            
            AppLogService.error( "Une erreur s'est produite lors de la suppression des demandes et des données liées de l'usager {}", strCustomerId , e.getMessage( ) );
        }
        
    }

	@Override
	public String getName( ) 
	{
		return SERVICE_NAME;
	}

}
