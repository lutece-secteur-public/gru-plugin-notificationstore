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

import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import fr.paris.lutece.plugins.grubusiness.business.demand.DemandType;
import fr.paris.lutece.plugins.grubusiness.business.demand.IDemandTypeDAO;
import fr.paris.lutece.plugins.notificationstore.service.NotificationStorePlugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for DemandType objects
 */
public final class DemandTypeDAO extends AbstractFilterDao<DemandType> implements IDemandTypeDAO
{
    // Constants
    private static final String SQL_QUERY_SELECTALL = "SELECT id, demande_type_id, label, code_category, url, application_code FROM notificationstore_demand_type ";
    private static final String SQL_QUERY_SELECT_BY_ID = SQL_QUERY_SELECTALL + "  WHERE id = ?";
    private static final String SQL_QUERY_SELECT_BY_TYPE_ID = SQL_QUERY_SELECTALL + "  WHERE demande_type_id = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO notificationstore_demand_type ( demande_type_id, label, code_category, url, application_code ) VALUES ( ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM notificationstore_demand_type WHERE id = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE notificationstore_demand_type SET demande_type_id = ?, label = ?, code_category = ? , url = ?, application_code = ? WHERE id = ?";
    private static final String SQL_QUERY_SELECTALL_BY_IDS = SQL_QUERY_SELECTALL + " WHERE id IN (  ";
    private static final String SQL_ORDER_BY = " ORDER BY demande_type_id asc ";

    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id FROM notificationstore_demand_type";

    public DemandTypeDAO( )
    {
        super( );

        // override MapSql (DB column names are not corresponding to Class attributes)
        _mapSql = new HashMap<>( );

        _mapSql.put( "demande_type_id", "int" );
        _mapSql.put( "meta_data", "Map" );
        _mapSql.put( "id", "int" );
        _mapSql.put( "label", "String" );
        _mapSql.put( "code_category", "String" );
        _mapSql.put( "url", "String" );
        _mapSql.put( "application_code", "String" );
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( DemandType demandType )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, NotificationStorePlugin.getPlugin( ) ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, String.valueOf( demandType.getIdDemandType( ) ) );
            daoUtil.setString( nIndex++, demandType.getLabel( ) );
            daoUtil.setString( nIndex++, demandType.getCategory( ) );
            daoUtil.setString( nIndex++, demandType.getUrl( ) );
            daoUtil.setString( nIndex++, demandType.getAppCode( ) );

            daoUtil.executeUpdate( );

            if ( daoUtil.nextGeneratedKey( ) )
            {
                demandType.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<DemandType> load( int nKey )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );
            DemandType demandType = null;

            if ( daoUtil.next( ) )
            {
                demandType = new DemandType( );
                int nIndex = 1;

                demandType.setId( daoUtil.getInt( nIndex++ ) );
                demandType.setIdDemandType( daoUtil.getInt( nIndex++ ) );
                demandType.setLabel( daoUtil.getString( nIndex++ ) );
                demandType.setCategory( daoUtil.getString( nIndex++ ) );
                demandType.setUrl( daoUtil.getString( nIndex++ ) );
                demandType.setAppCode( daoUtil.getString( nIndex++ ) );
            }

            return Optional.ofNullable( demandType );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<DemandType> selectByTypeId( String sKey )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_TYPE_ID, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.setString( 1, sKey );
            daoUtil.executeQuery( );
            DemandType demandType = null;

            if ( daoUtil.next( ) )
            {
                demandType = new DemandType( );
                int nIndex = 1;

                demandType.setId( daoUtil.getInt( nIndex++ ) );
                demandType.setIdDemandType( daoUtil.getInt( nIndex++ ) );
                demandType.setLabel( daoUtil.getString( nIndex++ ) );
                demandType.setCategory( daoUtil.getString( nIndex++ ) );
                demandType.setUrl( daoUtil.getString( nIndex++ ) );
                demandType.setAppCode( daoUtil.getString( nIndex++ ) );
            }

            return Optional.ofNullable( demandType );
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
    public void store( DemandType demandType )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, NotificationStorePlugin.getPlugin( ) ) )
        {
            int nIndex = 1;

            daoUtil.setString( nIndex++, String.valueOf( demandType.getIdDemandType( ) ) );
            daoUtil.setString( nIndex++, demandType.getLabel( ) );
            daoUtil.setString( nIndex++, demandType.getCategory( ) );
            daoUtil.setString( nIndex++, demandType.getUrl( ) );
            daoUtil.setString( nIndex++, demandType.getAppCode( ) );

            daoUtil.setInt( nIndex, demandType.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<DemandType> selectDemandTypesList( )
    {
        List<DemandType> demandTypeList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL + SQL_ORDER_BY, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                DemandType demandType = getFromDao( daoUtil );

                demandTypeList.add( demandType );
            }

            return demandTypeList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdDemandTypesList( )
    {
        List<Integer> demandTypeList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID + SQL_ORDER_BY, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                demandTypeList.add( daoUtil.getInt( 1 ) );
            }

            return demandTypeList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<DemandType> selectDemandTypesListByIds( List<Integer> listIds )
    {
        List<DemandType> demandTypeList = new ArrayList<>( );

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
                    DemandType demandType = getFromDao( daoUtil );

                    demandTypeList.add( demandType );
                }

                daoUtil.free( );

            }
        }
        return demandTypeList;

    }

    /**
     * get from dao
     * 
     * @param daoUtil
     * @return the demand types
     */
    private DemandType getFromDao( DAOUtil daoUtil )
    {
        DemandType demandType = new DemandType( );
        int nIndex = 1;

        demandType.setId( daoUtil.getInt( nIndex++ ) );
        demandType.setIdDemandType( daoUtil.getInt( nIndex++ ) );
        demandType.setLabel( daoUtil.getString( nIndex++ ) );
        demandType.setCategory( daoUtil.getString( nIndex++ ) );
        demandType.setUrl( daoUtil.getString( nIndex++ ) );
        demandType.setAppCode( daoUtil.getString( nIndex ) );

        return demandType;
    }

    @Override
    public List<Integer> searchItemsIdList( Map<String, String> mapFilterCriteria, String strColumnToOrder, String strSortMode )
    {
        return searchItemsIdList( SQL_QUERY_SELECTALL_ID, mapFilterCriteria, strColumnToOrder, strSortMode );
    }
}
