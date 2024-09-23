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

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.grubusiness.business.demand.DemandCategory;
import fr.paris.lutece.plugins.grubusiness.business.demand.IDemandCategoryDAO;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.ReferenceList;

/**
 * This class provides instances management methods (create, find, ...) for DemandCategory objects
 */
public final class DemandCategoryHome
{
    // Static variable pointed at the DAO instance
    private static IDemandCategoryDAO _dao = SpringContextService.getBean( "notificationstore.demandCategoryDao" );

    /**
     * Private constructor - this class need not be instantiated
     */
    private DemandCategoryHome( )
    {
    }

    /**
     * Create an instance of the demandCategory class
     * 
     * @param demandCategory
     *            The instance of the DemandCategory which contains the informations to store
     * @return The instance of demandCategory which has been created with its primary key.
     */
    public static DemandCategory create( DemandCategory demandCategory )
    {
        _dao.insert( demandCategory );

        return demandCategory;
    }

    /**
     * Update of the demandCategory which is specified in parameter
     * 
     * @param demandCategory
     *            The instance of the DemandCategory which contains the data to store
     * @return The instance of the demandCategory which has been updated
     */
    public static DemandCategory update( DemandCategory demandCategory )
    {
        _dao.store( demandCategory );

        return demandCategory;
    }

    /**
     * Remove the demandCategory whose identifier is specified in parameter
     * 
     * @param nKey
     *            The demandCategory Id
     */
    public static void remove( int nKey )
    {
        _dao.delete( nKey );
    }

    /**
     * Returns an instance of a demandCategory whose identifier is specified in parameter
     * 
     * @param nKey
     *            The demandCategory primary key
     * @return an instance of DemandCategory
     */
    public static Optional<DemandCategory> findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey );
    }

    /**
     * Load the data of all the demandCategory objects and returns them as a list
     * 
     * @return the list which contains the data of all the demandCategory objects
     */
    public static List<DemandCategory> getDemandCategoriesList( )
    {
        return _dao.selectDemandCategoriesList( );
    }

    /**
     * Load the id of all the demandCategory objects and returns them as a list
     * 
     * @return the list which contains the id of all the demandCategory objects
     */
    public static List<Integer> getIdDemandCategoriesList( )
    {
        return _dao.selectIdDemandCategoriesList( );
    }

    /**
     * Load the data of all the avant objects and returns them as a list
     * 
     * @param listIds
     *            liste of ids
     * @return the list which contains the data of all the avant objects
     */
    public static List<DemandCategory> getDemandCategoriesListByIds( List<Integer> listIds )
    {
        return _dao.selectDemandCategoriesListByIds( listIds );
    }

    /**
     * get categories as reference list
     * 
     * @return
     */
    public static ReferenceList getDemandCategoriesReferenceList( )
    {

        ReferenceList list = new ReferenceList( );
        list.addItem( StringUtils.EMPTY, StringUtils.EMPTY );;
        getDemandCategoriesList( ).stream( ).forEach( c -> list.addItem( c.getCode( ), c.getLabel( ) ) );

        return list;
    }

}
