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

import fr.paris.lutece.plugins.grubusiness.business.notification.EnumNotificationType;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import java.sql.Statement;

/**
 * This class provides Data Access methods for NotificationContent objects
 */
public final class NotificationContentDAO implements INotificationContentDAO
{
    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_notification_content, notification_id, notification_type, id_temporary_status, status_id, file_key, file_store FROM notificationstore_notification_content WHERE id_notification_content = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO notificationstore_notification_content ( notification_id, notification_type, id_temporary_status, status_id, file_key, file_store ) VALUES ( ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM notificationstore_notification_content WHERE id_notification_content = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE notificationstore_notification_content SET notification_type = ?, id_temporary_status = ?, status_id = ?, file_key = ?, file_store = ? WHERE id_notification_content = ?";
    private static final String SQL_QUERY_SELECTALL = "SELECT id_notification_content, notification_id, notification_type, id_temporary_status, status_id, file_key, file_store FROM notificationstore_notification_content";
    private static final String SQL_QUERY_SELECT_BY_ID_NOTIF = "SELECT id_notification_content, notification_id, notification_type, id_temporary_status, status_id, file_key, file_store FROM notificationstore_notification_content WHERE notification_id = ?";
    private static final String SQL_PARAM_QUERY_TYPE_NOTIF = " AND notification_type IN (";

    /**
     * {@inheritDoc }
     */
    @Override
    public void insert( NotificationContent notificationContent, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, Statement.RETURN_GENERATED_KEYS, plugin ) )
        {
            int nIndex = 0;
            daoUtil.setInt( ++nIndex, notificationContent.getIdNotification( ) );
            daoUtil.setString( ++nIndex, notificationContent.getNotificationType( ) );
            daoUtil.setInt( ++nIndex, notificationContent.getIdTemporaryStatus( ) != null ? notificationContent.getIdTemporaryStatus( ) : -1 );
            daoUtil.setInt( ++nIndex, notificationContent.getStatusId( ) != null ? notificationContent.getStatusId( ) : -1 );
            daoUtil.setString( ++nIndex, notificationContent.getFileKey( ) );
            daoUtil.setString( ++nIndex, notificationContent.getFileStore( ) );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                notificationContent.setId( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public NotificationContent load( int nId, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin ) )
        {
            daoUtil.setInt( 1, nId );
            daoUtil.executeQuery( );

            NotificationContent notificationContent = null;

            if ( daoUtil.next( ) )
            {
                notificationContent = loadNotificationContent( daoUtil );
            }

            return notificationContent;
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void delete( int nNotificationContentId, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin ) )
        {
            daoUtil.setInt( 1, nNotificationContentId );
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void store( NotificationContent notificationContent, Plugin plugin )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin ) )
        {
            int nIndex = 0;
            daoUtil.setString( ++nIndex, notificationContent.getNotificationType( ) );
            daoUtil.setInt( ++nIndex, notificationContent.getIdTemporaryStatus( ) );
            daoUtil.setInt( ++nIndex, notificationContent.getStatusId( ) );
            daoUtil.setString( ++nIndex, notificationContent.getFileKey( ) );
            daoUtil.setString( ++nIndex, notificationContent.getFileStore( ) );
            daoUtil.setInt( ++nIndex, notificationContent.getId( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public List<NotificationContent> selectNotificationContentsList( Plugin plugin )
    {
        List<NotificationContent> listNotificationContents = new ArrayList<>( );
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin ) )
        {
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                listNotificationContents.add( loadNotificationContent( daoUtil ) );
            }

            return listNotificationContents;
        }
    }

    @Override
    public List<NotificationContent> selectNotificationContentsByIdNotification( int nIdNotification, Plugin plugin )
    {
        List<NotificationContent> listNotificationContents = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT_BY_ID_NOTIF, plugin ) )
        {
            daoUtil.setInt( 1, nIdNotification );

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                listNotificationContents.add( loadNotificationContent( daoUtil ) );
            }
        }

        return listNotificationContents;
    }

    @Override
    public List<NotificationContent> selectNotificationContentsByIdAndTypeNotification( int nIdNotification, List<EnumNotificationType> listNotificationType,
            Plugin plugin )
    {
        String strQuery = SQL_QUERY_SELECT_BY_ID_NOTIF;
        if ( CollectionUtils.isNotEmpty( listNotificationType ) )
        {
            strQuery += SQL_PARAM_QUERY_TYPE_NOTIF + listNotificationType.stream( ).map( i -> "?" ).collect( Collectors.joining( "," ) ) + " ) ";
        }

        List<NotificationContent> listNotificationContents = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( strQuery, plugin ) )
        {
            int nIndexIn = 1;

            daoUtil.setInt( nIndexIn++, nIdNotification );
            if ( CollectionUtils.isNotEmpty( listNotificationType ) )
            {
                for ( EnumNotificationType notificationType : listNotificationType )
                {
                    daoUtil.setString( nIndexIn, notificationType.name( ) );
                    nIndexIn++;
                }
            }

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                listNotificationContents.add( loadNotificationContent( daoUtil ) );
            }
        }

        return listNotificationContents;
    }

    /**
     * Load notification
     * 
     * @param daoUtil
     * @return
     */
    private NotificationContent loadNotificationContent( DAOUtil daoUtil )
    {
        NotificationContent notificationContent = new NotificationContent( );
        notificationContent.setId( daoUtil.getInt( "id_notification_content" ) );
        notificationContent.setIdNotification( daoUtil.getInt( "notification_id" ) );
        notificationContent.setNotificationType( daoUtil.getString( "notification_type" ) );
        notificationContent.setIdTemporaryStatus( daoUtil.getInt( "id_temporary_status" ) );
        notificationContent.setStatusId( daoUtil.getInt( "status_id" ) );
        notificationContent.setFileKey( daoUtil.getString( "file_key" ) );
        notificationContent.setFileStore( daoUtil.getString( "file_store" ) );

        return notificationContent;
    }

}
