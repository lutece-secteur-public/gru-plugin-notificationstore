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

import fr.paris.lutece.plugins.grubusiness.business.demand.DemandCategory;
import fr.paris.lutece.plugins.grubusiness.business.demand.IDemandCategoryDAO;
import fr.paris.lutece.plugins.notificationstore.service.NotificationStorePlugin;
import fr.paris.lutece.util.sql.DAOUtil;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class provides Data Access methods for DemandCategory objects
 */
public final class DemandCategoryDAO implements IDemandCategoryDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_demand_category, code, label, is_default FROM notificationstore_demand_category WHERE id_demand_category = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO notificationstore_demand_category ( code, label, is_default ) VALUES ( ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM notificationstore_demand_category WHERE id_demand_category = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE notificationstore_demand_category SET code = ?, label = ?, is_default = ? WHERE id_demand_category = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_demand_category, code, label, is_default FROM notificationstore_demand_category";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_demand_category FROM notificationstore_demand_category";
    private static final String SQL_QUERY_SELECTALL_BY_IDS = "SELECT id_demand_category, code, label, is_default FROM notificationstore_demand_category WHERE id_demand_category IN (  ";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( DemandCategory demandCategory )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, NotificationStorePlugin.getPlugin( ) ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, demandCategory.getCode( ) );
            daoUtil.setString( nIndex++, demandCategory.getLabel( ) );
            daoUtil.setBoolean( nIndex++, demandCategory.isDefault( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                demandCategory.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<DemandCategory> load( int nKey )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );
            DemandCategory demandCategory = null;

            if ( daoUtil.next( ) )
            {
                demandCategory = new DemandCategory( );
                int nIndex = 1;

                demandCategory.setId( daoUtil.getInt( nIndex++ ) );
                demandCategory.setCode( daoUtil.getString( nIndex++ ) );
                demandCategory.setLabel( daoUtil.getString( nIndex++ ) );
                demandCategory.setDefault( daoUtil.getBoolean( nIndex ) );
            }

            return Optional.ofNullable( demandCategory );
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
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( DemandCategory demandCategory )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, NotificationStorePlugin.getPlugin( ) ) )
        {
            int nIndex = 1;

            daoUtil.setString( nIndex++, demandCategory.getCode( ) );
            daoUtil.setString( nIndex++, demandCategory.getLabel( ) );
            daoUtil.setBoolean( nIndex++, demandCategory.isDefault( ) );
            daoUtil.setInt( nIndex++, demandCategory.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<DemandCategory> selectDemandCategoriesList( )
    {
        List<DemandCategory> demandCategoryList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                DemandCategory demandCategory = new DemandCategory( );
                int nIndex = 1;

                demandCategory.setId( daoUtil.getInt( nIndex++ ) );
                demandCategory.setCode( daoUtil.getString( nIndex++ ) );
                demandCategory.setLabel( daoUtil.getString( nIndex++ ) );
                demandCategory.setDefault( daoUtil.getBoolean( nIndex ) );

                demandCategoryList.add( demandCategory );
            }

            return demandCategoryList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdDemandCategoriesList( )
    {
        List<Integer> demandCategoryList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                demandCategoryList.add( daoUtil.getInt( 1 ) );
            }

            return demandCategoryList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<DemandCategory> selectDemandCategoriesListByIds( List<Integer> listIds )
    {
        List<DemandCategory> demandCategoryList = new ArrayList<>( );

        StringBuilder builder = new StringBuilder( );

        if ( !listIds.isEmpty( ) )
        {
            for ( int i = 0; i < listIds.size( ); i++ )
            {
                builder.append( "?," );
            }

            String placeHolders = builder.deleteCharAt( builder.length( ) - 1 ).toString( );
            String stmt = SQL_QUERY_SELECTALL_BY_IDS + placeHolders + ")";

            try ( DAOUtil daoUtil = new DAOUtil( stmt, NotificationStorePlugin.getPlugin( ) ) )
            {
                int index = 1;
                for ( Integer n : listIds )
                {
                    daoUtil.setInt( index++, n );
                }

                daoUtil.executeQuery( );
                while ( daoUtil.next( ) )
                {
                    DemandCategory demandCategory = new DemandCategory( );
                    int nIndex = 1;

                    demandCategory.setId( daoUtil.getInt( nIndex++ ) );
                    demandCategory.setCode( daoUtil.getString( nIndex++ ) );
                    demandCategory.setLabel( daoUtil.getString( nIndex++ ) );
                    demandCategory.setDefault( daoUtil.getBoolean( nIndex ) );

                    demandCategoryList.add( demandCategory );
                }

                daoUtil.free( );

            }
        }
        return demandCategoryList;

    }
}
