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

import java.util.List;
import java.util.Optional;

import fr.paris.lutece.plugins.grubusiness.business.demand.TemporaryStatus;
import fr.paris.lutece.plugins.grubusiness.business.demand.ITemporaryStatusDAO;
import fr.paris.lutece.portal.service.spring.SpringContextService;

/**
 * This class provides instances management methods (create, find, ...) for Status objects
 */
public final class TemporaryStatusHome
{
    // Static variable pointed at the DAO instance
    private static ITemporaryStatusDAO _dao = SpringContextService.getBean( "notificationstore.temporaryStatusDao" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private TemporaryStatusHome( )
    {
    }

    /**
     * Create an instance of the status class
     * 
     * @param status
     *            The instance of the Status which contains the informations to store
     * @return The instance of status which has been created with its primary key.
     */
    public static TemporaryStatus create( TemporaryStatus status )
    {
        _dao.insert( status );

        return status;
    }

    /**
     * Update of the status which is specified in parameter
     * 
     * @param status
     *            The instance of the Status which contains the data to store
     * @return The instance of the status which has been updated
     */
    public static TemporaryStatus update( TemporaryStatus status )
    {
        _dao.store( status );

        return status;
    }

    /**
     * Remove the status whose identifier is specified in parameter
     * 
     * @param nKey
     *            The status Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey );
    }

    /**
     * Returns an instance of a status whose identifier is specified in parameter
     * 
     * @param nKey
     *            The status primary key
     * @return an instance of Status
     */
    public static Optional<TemporaryStatus> findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey );
    }

    /**
     * Returns an instance of a status
     * 
     * @param strStatus
     *            The status name
     * @return an instance of Status
     */
    public static Optional<TemporaryStatus> findByStatusId( int nStatusId )
    {
        return _dao.loadByStatusId( nStatusId );
    }

    /**
     * Returns an instance of a status
     * 
     * @param strStatus
     *            The status name
     * @return an instance of Status
     */
    public static Optional<TemporaryStatus> findByStatus( String strStatus )
    {
        return _dao.loadByStatus( strStatus );
    }

    /**
     * Load the data of all the status objects and returns them as a list
     * 
     * @return the list which contains the data of all the status objects
     */
    public static List<TemporaryStatus> getStatusList( )
    {
        return _dao.selectStatusList( );
    }

    /**
     * Load the id of all the status objects and returns them as a list
     * 
     * @return the list which contains the id of all the status objects
     */
    public static List<Integer> getIdStatusList( )
    {
        return _dao.selectIdStatusList( );
    }

    /**
     * Load the data of all the avant objects and returns them as a list
     * 
     * @param listIds
     *            liste of ids
     * @return the list which contains the data of all the avant objects
     */
    public static List<TemporaryStatus> getStatusListByIds( List<Integer> listIds )
    {
        return _dao.selectStatusListByIds( listIds );
    }

}
