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

import fr.paris.lutece.plugins.grubusiness.business.demand.Demand;
import fr.paris.lutece.plugins.grubusiness.business.demand.IDemandDAO;
import fr.paris.lutece.plugins.grubusiness.business.notification.NotificationFilter;
import fr.paris.lutece.plugins.notificationstore.service.NotificationStorePlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.Collection;
import java.util.List;

/**
 * This class provides instances management methods (create, find, ...) for Project objects
 */
public final class DemandHome
{
    // Static variable pointed at the DAO instance
    private static IDemandDAO _dao = SpringContextService.getBean( "notificationstore.demandDao" );
    private static Plugin _plugin = NotificationStorePlugin.getPlugin( );

    /**
     * Private constructor - this class need not be instantiated
     */
    private DemandHome( )
    {
    }

    /**
     * Finds all the demands
     * 
     * @return all the demands. An empty collection is returned if no demands has been found.
     */
    public static List<Demand> getByIds( List<Integer> listIds )
    {
        return _dao.loadByIds( listIds );
    }

    /**
     * search demands by filter
     * 
     * @param strKey
     * @return
     */
    public static List<Integer> searchIdsByFilter( NotificationFilter filter )
    {
        return _dao.loadIdsByFilter( filter );
    }

    /**
     * search demands by filter
     * 
     * @param strKey
     * @return
     */
    public static Collection<Demand> searchByFilter( NotificationFilter filter )
    {
        return _dao.loadByFilter( filter );
    }

    /**
     * Finds a demand with the specified id and type id
     * 
     * @param nKey
     *            The project primary key
     * @return an instance of Project
     */
    public static Demand findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey );
    }

    /**
     * Finds the demands associated to the specified customer id
     * 
     * @return the list which contains the id of all the project objects
     */
    public static Collection<Demand> getDemandIdCustomer( String strCustomerId )
    {
        return _dao.loadByCustomerId( strCustomerId );
    }

    /**
     * Finds the demands associated to the specified demand id and demand type id
     * 
     * @param strDemandId
     * @param strDemandTypeId
     * @return the list which contains the id of all the project objects
     */
    public static Demand getDemandByDemandIdAndTypeIdAndCustomerId( String strDemandId, String strDemandTypeId, String strCustomerId )
    {
        return _dao.loadByDemandIdAndTypeIdAndCustomerId( strDemandId, strDemandTypeId, strCustomerId );
    }

    
    /**
     * Load demand ids ordered by date notification
     * 
     * @param strCustomerId
     * @param strNotificationType
     * @param strIdDemandType
     *            (Optional can be null)
     * @return The list of demand ids
     */
    public static List<Integer> getIdsByCustomerIdAndDemandTypeId( String strCustomerId, String strNotificationType, String strIdDemandType, String strDirectionDateOrderBy )
    {
        return _dao.loadIdsByCustomerIdAndIdDemandType( strCustomerId, strNotificationType, strIdDemandType, strDirectionDateOrderBy );
    }

    /**
     * Load demand ids by status
     * 
     * @param strCustomerId
     * @param listStatus
     * @param strNotificationType
     * @param strIdDemandType
     * @return The list of demand ids
     */
    public static List<Integer> getIdsByStatus( String strCustomerId, List<String> listStatus, String strNotificationType, String strIdDemandType )
    {
        return _dao.loadIdsByStatus( strCustomerId, listStatus, strNotificationType, strIdDemandType );
    }

    /**
     * Updates a demand
     * 
     * @param demand
     *            the demand to update
     * @return the updated demand
     */
    public static Demand update( Demand demand )
    {
        return _dao.store( demand );
    }

    /**
     *  reassign  demands
     *    
     * @param strOldCustomerId
     * @param strNewCustomerId
     */
    public static void reassignDemands( String strOldCustomerId, String strNewCustomerId )
    {
        _dao.reassignDemands( strOldCustomerId, strNewCustomerId );
    }
    
    /**
     * Delete a demand by uid
     * @param nUid
     */
    public static void deleteByUid ( int nUid )
    {
        _dao.deleteByUid( nUid );
    }
}
