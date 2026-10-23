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
import java.util.List;
import java.util.Optional;

import fr.paris.lutece.plugins.grubusiness.business.demand.DemandTypeTag;
import fr.paris.lutece.plugins.grubusiness.business.demand.IDemandTypeTagDAO;
import fr.paris.lutece.plugins.notificationstore.service.NotificationStorePlugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for DemandTypeTag objects
 */
public final class DemandTypeTagDAO implements IDemandTypeTagDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id, name, code FROM notificationstore_demande_type_tag WHERE id = ?";
    private static final String SQL_QUERY_SELECT_BY_CODE = "SELECT id, name, code FROM notificationstore_demande_type_tag WHERE code = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO notificationstore_demande_type_tag ( name, code ) VALUES ( ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM notificationstore_demande_type_tag WHERE id = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE notificationstore_demande_type_tag SET name = ?, code = ? WHERE id = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id, name, code FROM notificationstore_demande_type_tag";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id FROM notificationstore_demande_type_tag";
    private static final String SQL_QUERY_SELECTALL_BY_IDS = "SELECT id, name, code FROM notificationstore_demande_type_tag WHERE id IN (  ";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( DemandTypeTag demandTypeTag)
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, NotificationStorePlugin.getPlugin( ) ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, demandTypeTag.getName( ) );
            daoUtil.setString( nIndex++, demandTypeTag.getCode( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                demandTypeTag.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<DemandTypeTag> load(int nKey )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );
            DemandTypeTag demandTypeTag = null;

            if ( daoUtil.next( ) )
            {
                demandTypeTag = new DemandTypeTag( );
                int nIndex = 1;

                demandTypeTag.setId( daoUtil.getInt( nIndex++ ) );
                demandTypeTag.setName( daoUtil.getString( nIndex++ ) );
                demandTypeTag.setCode( daoUtil.getString( nIndex ) );
            }

            return Optional.ofNullable(demandTypeTag);
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<DemandTypeTag> selectByCode(String strCode )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_CODE, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.setString( 1, strCode );
            daoUtil.executeQuery( );
            DemandTypeTag demandTypeTag = null;

            if ( daoUtil.next( ) )
            {
                demandTypeTag = new DemandTypeTag( );
                int nIndex = 1;

                demandTypeTag.setId( daoUtil.getInt( nIndex++ ) );
                demandTypeTag.setName( daoUtil.getString( nIndex++ ) );
                demandTypeTag.setCode( daoUtil.getString( nIndex ) );
            }

            return Optional.ofNullable(demandTypeTag);
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
    public void store( DemandTypeTag demandTypeTag)
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, NotificationStorePlugin.getPlugin( ) ) )
        {
            int nIndex = 1;

            daoUtil.setString( nIndex++, demandTypeTag.getName( ) );
            daoUtil.setString( nIndex++, demandTypeTag.getCode( ) );
            daoUtil.setInt( nIndex++, demandTypeTag.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<DemandTypeTag> selectDemandTypeTagsList( )
    {
        List<DemandTypeTag> demandTypeTagList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                DemandTypeTag demandTypeTag = new DemandTypeTag( );
                int nIndex = 1;

                demandTypeTag.setId( daoUtil.getInt( nIndex++ ) );
                demandTypeTag.setName( daoUtil.getString( nIndex++ ) );
                demandTypeTag.setCode( daoUtil.getString( nIndex ) );

                demandTypeTagList.add(demandTypeTag);
            }

            return demandTypeTagList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdDemandTypeTagsList( )
    {
        List<Integer> demandTypeTagList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                demandTypeTagList.add( daoUtil.getInt( 1 ) );
            }

            return demandTypeTagList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<DemandTypeTag> selectDemandTypeTagsListByIds(List<Integer> listIds )
    {
        List<DemandTypeTag> demandTypeTagList = new ArrayList<>( );

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
                    DemandTypeTag demandTypeTag = new DemandTypeTag( );
                    int nIndex = 1;

                    demandTypeTag.setId( daoUtil.getInt( nIndex++ ) );
                    demandTypeTag.setName( daoUtil.getString( nIndex++ ) );
                    demandTypeTag.setCode( daoUtil.getString( nIndex ) );

                    demandTypeTagList.add(demandTypeTag);
                }

                daoUtil.free( );

            }
        }
        return demandTypeTagList;

    }
}
