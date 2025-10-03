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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import fr.paris.lutece.plugins.grubusiness.business.demand.IDemandServiceProvider;
import fr.paris.lutece.plugins.grubusiness.business.demand.TemporaryStatus;
import fr.paris.lutece.plugins.grubusiness.business.web.rs.EnumGenericStatus;
import fr.paris.lutece.plugins.notificationstore.business.NotificationContentHome;
import fr.paris.lutece.plugins.notificationstore.business.TemporaryStatusHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.sql.TransactionManager;

/**
 * 
 * TemporaryStatusService
 *
 */
public class TemporaryStatusService
{
    // Bean names
    private static final String BEAN_STORAGE_SERVICE = "notificationstore.demandService";
    
    private static TemporaryStatusService _instance;
    private static IDemandServiceProvider _demandService;
    private static TemporaryStatusCacheService _cache;
    
    /**
     * Private constructor
     */
    private TemporaryStatusService()
    {
        //Do nothing
    }
    
    /**
     * Public constructor
     * @return instance
     */
    public static TemporaryStatusService getInstance( )
    {
        if ( _instance == null )
        {
            _instance = new TemporaryStatusService( );
            _demandService = SpringContextService.getBean( BEAN_STORAGE_SERVICE );
            _cache =  TemporaryStatusCacheService.getInstance( );
        }
        return _instance;
    }
    
    /**
     * Create an instance of the status class
     * 
     * @param status
     *            The instance of the Status which contains the informations to store
     * @return The instance of status which has been created with its primary key.
     */
    public TemporaryStatus create( TemporaryStatus status )
    {
        TemporaryStatusHome.create( status );
        
        //Remove cache
        TemporaryStatusCacheService.getInstance( ).removeCache( );
        
        return status;
    }

    /**
     * Update of the status which is specified in parameter
     * 
     * @param status
     *            The instance of the Status which contains the data to store
     * @return The instance of the status which has been updated
     */
    public TemporaryStatus update( TemporaryStatus status )
    {     
        try
        {
            //Début de la transaction
            TransactionManager.beginTransaction( null );
            
            Optional<TemporaryStatus> oldStatus = TemporaryStatusHome.findByPrimaryKey( status.getId( ) );
            
            if( oldStatus.isPresent( ) 
                    && EnumGenericStatus.UNDEFINED.getStatusId( ).equals( oldStatus.get( ).getGenericStatus( ).getStatusId( ) )
                    && !EnumGenericStatus.UNDEFINED.getStatusId( ).equals( status.getGenericStatus( ).getStatusId( ) ) )
            {
                //Update demands status id
                _demandService.updateDemandsStatusId( status.getGenericStatus( ).getStatusId( ), status.getId( ) );
                //Update notifications status
                NotificationContentHome.updateStatusId( status.getGenericStatus( ).getStatusId( ), status.getId( ) );
            }
            
            TemporaryStatusHome.update( status );
            
            //Commit de la transaction
            TransactionManager.commitTransaction( null );
            
            //Remove cache
            TemporaryStatusCacheService.getInstance( ).removeCache( );
        } 
        catch (Exception e) 
        {
            //Roll back
            TransactionManager.rollBack( null );
            AppLogService.error( "Une erreur s'est produite lors de la mise à jour du statut temporaire {}", status.getId( ), e.getMessage( ) );
        }

        return status;
    }

    /**
     * Remove the status whose identifier is specified in parameter
     * 
     * @param nKey
     *            The status Id
     */
    public void remove( int nKey )
    {
        TemporaryStatusHome.remove( nKey );
        
        //Remove cache
        TemporaryStatusCacheService.getInstance( ).removeCache( );
    }

    /**
     * Returns an instance of a status whose identifier is specified in parameter
     * 
     * @param nKey
     *            The status primary key
     * @return an instance of Status
     */
    public Optional<TemporaryStatus> findByPrimaryKey( int nKey )
    {
        return TemporaryStatusHome.findByPrimaryKey( nKey );
    }

    /**
     * Returns an instance of a status
     * 
     * @param strStatus
     *            The status name
     * @return an instance of Status
     */
    public Optional<TemporaryStatus> findByStatusId( int nStatusId )
    {
        return TemporaryStatusHome.findByStatusId( nStatusId );
    }

    /**
     * Returns an instance of a status (like '%strStatus%')
     * 
     * @param strStatus
     *            The status name
     * @return Only the first status found returned
     */
    public Optional<TemporaryStatus> findByStatus( String strStatus )
    {
        List<TemporaryStatus> listTemporaryStatus = _cache.getList( );
        
        strStatus = strStatus.replaceAll("\\s", "").toLowerCase( );
        
        if ( listTemporaryStatus != null && !listTemporaryStatus.isEmpty( ) )
        {
            for ( TemporaryStatus temporaryStatus : listTemporaryStatus )
            {
                String strStatusExist = temporaryStatus.getStatus( ).replaceAll("\\s", "").toLowerCase( );
                if( strStatus.contains( strStatusExist ) )
                {
                    return Optional.ofNullable( temporaryStatus );
                }
                
            }
        }
        return Optional.empty( );
    }

    /**
     * Load the data of all the status objects and returns them as a list
     * 
     * @return the list which contains the data of all the status objects
     */
    public List<TemporaryStatus> getStatusList( )
    {
        return TemporaryStatusHome.getStatusList( );
    }

    /**
     * Load the id of all the status objects and returns them as a list
     * 
     * @return the list which contains the id of all the status objects
     */
    public List<Integer> getIdStatusList( )
    {
        return TemporaryStatusHome.getIdStatusList( );
    }

    /**
     * Load the data of all the avant objects and returns them as a list
     * 
     * @param listIds
     *            liste of ids
     * @return the list which contains the data of all the avant objects
     */
    public List<TemporaryStatus> getStatusListByIds( List<Integer> listIds )
    {
        return TemporaryStatusHome.getStatusListByIds( listIds );
    }
    
    /**
     * search ids
     * 
     * @param mapFilterCriteria
     * @param strColumnToOrder
     * @param strSortMode
     * @param plugin
     * @return the ids list
     */
    public List<Integer> searchStatusIdsList(  Map<String,String> mapFilterCriteria, 
    		String strColumnToOrder, String strSortMode ) 
    {
    	return TemporaryStatusHome.searchItemsIdList( mapFilterCriteria, strColumnToOrder, strSortMode);
    }
}
