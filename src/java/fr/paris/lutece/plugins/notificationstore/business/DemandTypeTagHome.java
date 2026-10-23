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

import fr.paris.lutece.plugins.grubusiness.business.demand.DemandTypeTag;
import fr.paris.lutece.plugins.grubusiness.business.demand.IDemandTypeTagDAO;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

/**
 * This class provides instances management methods (create, find, ...) for DemandTypeTag objects
 */
public final class DemandTypeTagHome
{
    // Static variable pointed at the DAO instance
    private static IDemandTypeTagDAO _dao = SpringContextService.getBean( "notificationstore.demandTypeTagDao" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private DemandTypeTagHome( )
    {
    }

    /**
     * Create an instance of the demandTypeTag class
     * 
     * @param demandTypeTag
     *            The instance of the DemandTypeTag which contains the informations to store
     * @return The instance of demandTypeTag which has been created with its primary key.
     */
    public static DemandTypeTag create(DemandTypeTag demandTypeTag)
    {
        _dao.insert(demandTypeTag);

        return demandTypeTag;
    }

    /**
     * Update of the demandTypeTag which is specified in parameter
     * 
     * @param demandTypeTag
     *            The instance of the DemandTypeTag which contains the data to store
     * @return The instance of the demandTypeTag which has been updated
     */
    public static DemandTypeTag update(DemandTypeTag demandTypeTag)
    {
        _dao.store(demandTypeTag);

        return demandTypeTag;
    }

    /**
     * Remove the demandTypeTag whose identifier is specified in parameter
     * 
     * @param nKey
     *            The demandTypeTag Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey );
    }

    /**
     * Returns an instance of a demandTypeTag whose identifier is specified in parameter
     * 
     * @param nKey
     *            The demandTypeTag primary key
     * @return an instance of DemandTypeTag
     */
    public static Optional<DemandTypeTag> findByPrimaryKey(int nKey )
    {
        return _dao.load( nKey );
    }

    /**
     * Returns an instance of a demandTypeTag whose code is specified in parameter
     *
     * @param strCode
     *            The demandTypeTag unique code
     * @return an instance of DemandTypeTag or an empty Optional
     */
    public static Optional<DemandTypeTag> getDemandTypeTagByCode(String strCode )
    {
        return _dao.selectByCode( strCode );
    }

    /**
     * Load the data of all the demandTypeTag objects and returns them as a list
     * 
     * @return the list which contains the data of all the demandTypeTag objects
     */
    public static List<DemandTypeTag> getDemandTypeTagsList( )
    {
        return _dao.selectDemandTypeTagsList( );
    }

    /**
     * Load the id of all the demandTypeTag objects and returns them as a list
     * 
     * @return the list which contains the id of all the demandTypeTag objects
     */
    public static List<Integer> getIdDemandTypeTagsList( )
    {
        return _dao.selectIdDemandTypeTagsList( );
    }

    /**
     * Load the data of the demandTypeTag objects from a list of ids and returns them as a list
     * 
     * @param listIds
     *            list of ids
     * @return the list which contains the data of the demandTypeTag objects
     */
    public static List<DemandTypeTag> getDemandTypeTagsListByIds(List<Integer> listIds )
    {
        return _dao.selectDemandTypeTagsListByIds( listIds );
    }

    /**
     * get demande type tags as reference list
     * 
     * @return
     */
    public static ReferenceList getDemandTypeTagsReferenceList( )
    {
        ReferenceList list = new ReferenceList( );
        getDemandTypeTagsList( ).stream( ).forEach( t -> list.addItem( t.getCode( ), t.getName( ) ) );

        return list;
    }
}
