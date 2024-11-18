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

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.paris.lutece.plugins.grubusiness.business.demand.TemporaryStatus;
import fr.paris.lutece.plugins.grubusiness.business.demand.ITemporaryStatusDAO;
import fr.paris.lutece.plugins.grubusiness.business.web.rs.EnumGenericStatus;
import fr.paris.lutece.plugins.notificationstore.service.NotificationStorePlugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Status objects
 */
public final class TemporaryStatusDAO implements ITemporaryStatusDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_temporary_status, status, status_id FROM notificationstore_temporary_status WHERE id_temporary_status = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO notificationstore_temporary_status ( status, status_id ) VALUES ( ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM notificationstore_temporary_status WHERE id_temporary_status = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE notificationstore_temporary_status SET status = ?, status_id = ? WHERE id_temporary_status = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_temporary_status, status, status_id FROM notificationstore_temporary_status";
    private static final String SQL_QUERY_SELECTALL_ID = "SELECT id_temporary_status FROM notificationstore_temporary_status";
    private static final String SQL_QUERY_SELECTALL_BY_IDS = "SELECT id_temporary_status, status, status_id FROM notificationstore_temporary_status WHERE id_temporary_status IN (  ";
    private static final String SQL_QUERY_SELECT_BY_STATUS_ID = "SELECT id_temporary_status, status, status_id FROM notificationstore_temporary_status WHERE status_id = ?";
    private static final String SQL_QUERY_SELECT_BY_STATUS = "SELECT id_temporary_status, status, status_id FROM notificationstore_temporary_status WHERE status = ?";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( TemporaryStatus status )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, NotificationStorePlugin.getPlugin( ) ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, status.getStatus( ) );
            daoUtil.setInt( nIndex++, status.getGenericStatus( ) != null ? status.getGenericStatus( ).getStatusId( ) : -1  );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                status.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }

    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Optional<TemporaryStatus> load( int nKey )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nKey );
            daoUtil.executeQuery( );
            TemporaryStatus status = null;

            if ( daoUtil.next( ) )
            {
                status = loadFromDaoUtil( daoUtil );
            }

            return Optional.ofNullable( status );
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
    public void store( TemporaryStatus status )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, NotificationStorePlugin.getPlugin( ) ) )
        {
            int nIndex = 1;

            daoUtil.setString( nIndex++, status.getStatus( ) );
            daoUtil.setInt( nIndex++, status.getGenericStatus( ).getStatusId( ) );
            daoUtil.setInt( nIndex, status.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<TemporaryStatus> selectStatusList( )
    {
        List<TemporaryStatus> statusList = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                statusList.add( loadFromDaoUtil( daoUtil ) );
            }

            return statusList;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<Integer> selectIdStatusList( )
    {
        List<Integer> statusListIds = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL_ID, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                statusListIds.add( daoUtil.getInt( 1 ) );
            }

            return statusListIds;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<TemporaryStatus> selectStatusListByIds( List<Integer> listIds )
    {
        List<TemporaryStatus> statusList = new ArrayList<>( );

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
                    statusList.add( loadFromDaoUtil( daoUtil ) );
                }
            }
        }
        return statusList;

    }

    @Override
    public Optional<TemporaryStatus> loadByStatusId( int nStatusId )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_STATUS_ID, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nStatusId );
            daoUtil.executeQuery( );
            TemporaryStatus status = null;

            if ( daoUtil.next( ) )
            {
                status = loadFromDaoUtil( daoUtil );
            }

            return Optional.ofNullable( status );
        }
    }

    @Override
    public Optional<TemporaryStatus> loadByStatus( String strStatus )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_STATUS, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.setString( 1, strStatus );
            daoUtil.executeQuery( );
            TemporaryStatus status = null;

            if ( daoUtil.next( ) )
            {
                status = loadFromDaoUtil( daoUtil );
            }

            return Optional.ofNullable( status );
        }
    }

    /**
     * load status from daoUtil
     * 
     * @param daoUtil
     * @return
     */
    private TemporaryStatus loadFromDaoUtil( DAOUtil daoUtil )
    {
        TemporaryStatus status = new TemporaryStatus( );
        int nIndex = 1;

        status.setId( daoUtil.getInt( nIndex++ ) );
        status.setStatus( daoUtil.getString( nIndex++ ) );
        status.setGenericStatus( EnumGenericStatus.getByStatusId( daoUtil.getInt( nIndex++ ) ) );

        return status;
    }
}
