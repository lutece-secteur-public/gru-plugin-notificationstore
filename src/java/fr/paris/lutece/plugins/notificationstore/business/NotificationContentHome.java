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
import fr.paris.lutece.plugins.grubusiness.business.demand.TemporaryStatus;
import fr.paris.lutece.plugins.grubusiness.business.notification.EnumNotificationType;
import fr.paris.lutece.plugins.grubusiness.business.notification.Notification;
import fr.paris.lutece.plugins.grubusiness.business.web.rs.EnumGenericStatus;
import fr.paris.lutece.plugins.notificationstore.service.NotificationStorePlugin;
import fr.paris.lutece.plugins.notificationstore.service.TemporaryStatusService;
import fr.paris.lutece.plugins.notificationstore.utils.NotificationStoreConstants;
import fr.paris.lutece.plugins.notificationstore.utils.NotificationStoreUtils;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.FileServiceException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.string.StringUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.MediaType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class provides instances management methods (create, find, ...) for NotificationContent objects
 */

public final class NotificationContentHome
{

    // Static variable pointed at the DAO instance

    private static INotificationContentDAO _dao = (INotificationContentDAO) SpringContextService.getBean( "notificationstore.notificationContentDao" );

    /**
     * Private constructor - this class need not be instantiated
     */

    private NotificationContentHome( )
    {
    }

    /**
     * Create an instance of the notificationContent class
     * 
     * @param notificationContent
     *            The instance of the NotificationContent which contains the informations to store
     * @param plugin
     *            the Plugin
     * @return The instance of notificationContent which has been created with its primary key.
     */

    public static NotificationContent create( NotificationContent notificationContent )
    {
        _dao.insert( notificationContent, NotificationStorePlugin.getPlugin( ) );

        return notificationContent;
    }

    /**
     * Create an instance of the notificationContent class
     * 
     * @param notificationContent
     *            The instance of the NotificationContent which contains the informations to store
     * @param plugin
     *            the Plugin
     * @return The instance of notificationContent which has been created with its primary key.
     */

    public static List<NotificationContent> create( Notification notification )
    {
        List<NotificationContent> listNotificationContent = getListNotificationContent( notification );
        for ( NotificationContent content : listNotificationContent )
        {
            _dao.insert( content, NotificationStorePlugin.getPlugin( ) );
        }

        return listNotificationContent;
    }

    /**
     * Update of the notificationContent which is specified in parameter
     * 
     * @param notificationContent
     *            The instance of the NotificationContent which contains the data to store
     * @param plugin
     *            the Plugin
     * @return The instance of the notificationContent which has been updated
     */

    public static NotificationContent update( NotificationContent notificationContent )
    {
        _dao.store( notificationContent, NotificationStorePlugin.getPlugin( ) );

        return notificationContent;
    }
    
    /**
     * Update the record in the table
     * 
     * @param nNewStatusId
     *            the new status id
     * @param nTemporaryStatusId
     *            This nTemporaryStatusId is allows to filter on the notifications to be updated
     */

    public static void updateStatusId( int nNewStatusId, int nTemporaryStatusId )
    {
        _dao.updateStatusId( nNewStatusId, nTemporaryStatusId, NotificationStorePlugin.getPlugin( ) );
    }
    
    /**
     * Remove the notificationContent whose identifier is specified in parameter
     * 
     * @param nNotificationContentId
     *            The notificationContent Id
     * @param plugin
     *            the Plugin
     */

    public static void remove( int nNotificationContentId )
    {
        _dao.delete( nNotificationContentId, NotificationStorePlugin.getPlugin( ) );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a notificationContent whose identifier is specified in parameter
     * 
     * @param nKey
     *            The notificationContent primary key
     * @param plugin
     *            the Plugin
     * @return an instance of NotificationContent
     */

    public static NotificationContent findByPrimaryKey( int nKey )
    {
        return _dao.load( nKey, NotificationStorePlugin.getPlugin( ) );
    }

    /**
     * Load the data of all the notificationContent objects and returns them in form of a list
     * 
     * @return the list which contains the data of all the notificationContent objects
     */

    public static List<NotificationContent> getNotificationContentsList( )
    {
        return _dao.selectNotificationContentsList( NotificationStorePlugin.getPlugin( ) );
    }

    /**
     * Load the data by id notification and returns them in form of a list
     * 
     * @param nIdNotification
     *            the id notification
     * @return the list which contains the data by id notification
     */

    public static List<NotificationContent> getNotificationContentsByIdNotification( int nIdNotification )
    {
        return _dao.selectNotificationContentsByIdNotification( nIdNotification, NotificationStorePlugin.getPlugin( ) );
    }

    /**
     * Load the data by id notification and notification type
     * 
     * @param nIdNotification
     *            the id notification
     * @return the list which contains the data by id notification
     */

    public static List<NotificationContent> getNotificationContentsByIdAndTypeNotification( int nIdNotification,
            List<EnumNotificationType> listNotificationType )
    {
        return _dao.selectNotificationContentsByIdAndTypeNotification( nIdNotification, listNotificationType, NotificationStorePlugin.getPlugin( ) );
    }

    private static List<NotificationContent> getListNotificationContent( Notification notification )
    {
        List<NotificationContent> listNotificationContent = new ArrayList<>( );

        try
        {
            ObjectMapper mapperr = NotificationStoreUtils.getMapper( );
            Demand demand = notification.getDemand( );

            if ( notification.getSmsNotification( ) != null )
            {
                listNotificationContent.add(
                        initNotificationContent( notification, EnumNotificationType.SMS, mapperr.writeValueAsString( notification.getSmsNotification( ) ) ) );
            }

            if ( notification.getBackofficeNotification( ) != null )
            {
                listNotificationContent.add( initNotificationContent( notification, EnumNotificationType.BACKOFFICE,
                        mapperr.writeValueAsString( notification.getBackofficeNotification( ) ) ) );
            }

            if ( CollectionUtils.isNotEmpty( notification.getBroadcastEmail( ) ) )
            {
                listNotificationContent.add( initNotificationContent( notification, EnumNotificationType.BROADCAST_EMAIL,
                        mapperr.writeValueAsString( notification.getBroadcastEmail( ) ) ) );
            }

            if ( notification.getMyDashboardNotification( ) != null )
            {
                NotificationContent notificationContent = initNotificationContent( notification, EnumNotificationType.MYDASHBOARD,
                        mapperr.writeValueAsString( notification.getMyDashboardNotification( ) ) );
                listNotificationContent.add( notificationContent );
                //Update demand status only for mydashboard notification
                demand.setStatusId( notificationContent.getStatusId( ) );
            }

            if ( notification.getEmailNotification( ) != null )
            {
                listNotificationContent.add( initNotificationContent( notification, EnumNotificationType.CUSTOMER_EMAIL,
                        mapperr.writeValueAsString( notification.getEmailNotification( ) ) ) );
            }
            //Update modify date of demand
            demand.setModifyDate( new Date( ).getTime( ) );
            DemandHome.update( demand );

        }
        catch( JsonProcessingException e )
        {
            AppLogService.error( "Error while writing JSON of notification", e );
        }
        catch( IOException e )
        {
            AppLogService.error( "Error while compressing or writing JSON of notification", e );
        }

        return listNotificationContent;
    }

    /**
     * Initialization of notification content and retrieval of temporary and generic status
     * 
     * @param nNotificationId
     * @param notificationType
     * @param strNotificationContent
     * @throws IOException
     */
    private static NotificationContent initNotificationContent( Notification notification, EnumNotificationType notificationType,
            String strNotificationContent ) throws IOException
    {
        NotificationContent notificationContent = new NotificationContent( );
        notificationContent.setIdNotification( notification.getId( ) );
        notificationContent.setNotificationType( notificationType.name( ) );
        notificationContent.setFileKey( saveContentInFileStore(notification, notificationType, strNotificationContent ) );
        notificationContent.setFileStore( NotificationStoreConstants.FILE_STORE_PROVIDER );

        //Calculate status
        Integer nStatusId = getStatusGenericId( notification, EnumNotificationType.MYDASHBOARD );
        notificationContent.setStatusId( nStatusId );
        notificationContent.setIdTemporaryStatus( -1 );
        
        //If no generic status found.
        if( nStatusId == -1 )
        {
            notificationContent.setIdTemporaryStatus( getTemporaryStatusId( notification, EnumNotificationType.MYDASHBOARD ) );
        }
        
        return notificationContent;
    }
    
    /**
     * Save notification content in file store
     * @param notification
     * @param notificationType
     * @param strNotificationContent
     * @return file id
     * @throws IOException
     */
    private static String saveContentInFileStore( Notification notification, EnumNotificationType notificationType,
            String strNotificationContent) throws IOException
    {
        strNotificationContent = strNotificationContent.replaceAll( NotificationStoreConstants.CHARECTER_REGEXP_FILTER, "" );
        
        //Convert notification content to bytes
        byte [ ] bytes;

        if ( AppPropertiesService.getPropertyBoolean( NotificationStoreConstants.PROPERTY_COMPRESS_NOTIFICATION, false ) )
        {
            bytes = StringUtil.compress( strNotificationContent );
        }
        else
        {
            bytes = strNotificationContent.getBytes( StandardCharsets.UTF_8 );
        }
        
        //Create file
        File file = new File( );
        file.setTitle( notification.getDemand( ).getId( ) + "_" + notificationType.name( ) + "_" + notification.getDemand( ).getCustomer( ).getConnectionId( ) );
        file.setSize( bytes.length );
        file.setMimeType( MediaType.APPLICATION_JSON  );
        
        PhysicalFile physiqueFile = new PhysicalFile( );
        physiqueFile.setValue( bytes );
        
        file.setPhysicalFile( physiqueFile );
        
        try
        {
            //Save file
            return FileService.getInstance( ).getFileStoreServiceProvider( NotificationStoreConstants.FILE_STORE_PROVIDER ).storeFile( file );
            
        } catch ( FileServiceException e )
        {
            AppLogService.error( "An error occurred while saving the notification content, demand_id {}", notification.getDemand( ).getId( )  , e.getMessage( ) );
        }
        return StringUtils.EMPTY;
    }

    /**
     * Get status for mydashboard notification
     * 
     * @param notification
     */
    private static Integer getStatusGenericId( Notification notification, EnumNotificationType statusType )
    {
        if ( EnumNotificationType.MYDASHBOARD.equals( statusType ) && notification.getMyDashboardNotification( ) != null )
        {
            if ( EnumGenericStatus.exists( notification.getMyDashboardNotification( ).getStatusId( ) ) )
            {
                return notification.getMyDashboardNotification( ).getStatusId( );
            }
            else
            {
                Optional<TemporaryStatus> status =  TemporaryStatusService.getInstance( ).findByStatus( notification.getMyDashboardNotification( ).getStatusText( ) );
                if ( status.isPresent( ) && status.get( ).getGenericStatus( ) != null )
                {
                    return status.get( ).getGenericStatus( ).getStatusId( );
                }
                return -1;
            }
        }

        return -1;
    }
    
    /**
     * Returns the id of the temporary status if it exists, otherwise we create it
     * @param notification
     * @param statusType
     * @return temporary status id
     */
    private static Integer getTemporaryStatusId ( Notification notification, EnumNotificationType statusType )
    {
        if ( EnumNotificationType.MYDASHBOARD.equals( statusType ) && notification.getMyDashboardNotification( ) != null )
        {
            Optional<TemporaryStatus> status =  TemporaryStatusService.getInstance( ).findByStatus( notification.getMyDashboardNotification( ).getStatusText( ) );
            
            if ( status.isPresent( ) )
            {
                return status.get( ).getId( );
            } 
            else
            {
                //Create temporary status if not exist
                TemporaryStatus newStatus = new TemporaryStatus( );
                newStatus.setStatus( notification.getMyDashboardNotification( ).getStatusText( ) );
                
                newStatus =  TemporaryStatusService.getInstance( ).create( newStatus );
                
                return newStatus.getId( );
            }
        }
        return -1;
    }

}
