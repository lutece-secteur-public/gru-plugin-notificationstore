/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
package fr.paris.lutece.plugins.notificationstore.web.rs;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.grubusiness.business.notification.EnumNotificationType;
import fr.paris.lutece.plugins.grubusiness.business.notification.Notification;
import fr.paris.lutece.plugins.grubusiness.business.notification.NotificationFilter;
import fr.paris.lutece.plugins.grubusiness.business.web.rs.NotificationResult;
import fr.paris.lutece.plugins.grubusiness.business.web.rs.SearchResult;
import fr.paris.lutece.plugins.grubusiness.business.web.rs.responseStatus.ResponseStatus;
import fr.paris.lutece.plugins.grubusiness.business.web.rs.responseStatus.ResponseStatusFactory;
import fr.paris.lutece.plugins.notificationstore.business.NotificationHome;
import fr.paris.lutece.plugins.notificationstore.service.NotificationService;
import fr.paris.lutece.plugins.notificationstore.utils.NotificationStoreConstants;
import fr.paris.lutece.plugins.notificationstore.utils.NotificationStoreUtils;
import fr.paris.lutece.plugins.rest.service.RestConstants;

@Path( RestConstants.BASE_PATH + NotificationStoreConstants.PLUGIN_NAME + NotificationStoreConstants.VERSION_PATH_V3  )
public class NotificationRestService
{

    /**
     * process the notification 
     * 
     * @param strJson
     *            The JSON flow
     * @return The response
     */
    @POST
    @Path( NotificationStoreConstants.PATH_NOTIFICATION )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public Response notification( String strJson )
    {
    	return NotificationService.instance( ).newNotification( strJson );
    }
    

    
    /**
     * store the notification  event
     * 
     * @param strJson
     *            The JSON flow
     * @return The response
     */
    @POST
    @Path( NotificationStoreConstants.PATH_NOTIFICATION_EVENT )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public Response notificationEvent( String strJson )
    {
    	return NotificationService.instance( ).newNotificationEvent( strJson );
    }
    
     
    /**
     * Gets list of notification
     * 
     * @param strIdDemand
     */
    @GET
    @Path( NotificationStoreConstants.PATH_NOTIFICATION + NotificationStoreConstants.PATH_LIST)
    @Produces( MediaType.APPLICATION_JSON )
    public Response getListNotification( @QueryParam( NotificationStoreConstants.QUERY_PARAM_ID_DEMAND ) String strIdDemand,
            @QueryParam( NotificationStoreConstants.QUERY_PARAM_ID_DEMAND_TYPE ) String strIdDemandType,
            @QueryParam( NotificationStoreConstants.QUERY_PARAM_CUSTOMER_ID ) String strCustomerId,
            @QueryParam( NotificationStoreConstants.QUERY_PARAM_NOTIFICATION_TYPE ) String strNotificationType )
    {
        NotificationResult result = new NotificationResult( );

        if ( StringUtils.isNotEmpty( strIdDemand ) && StringUtils.isNotEmpty( strIdDemandType ) && StringUtils.isNotEmpty( strCustomerId ) )
        {
            NotificationFilter filter = new NotificationFilter( );
            if( StringUtils.isNotEmpty( strNotificationType ) )
            {
                filter.getListNotificationType( ).add( EnumNotificationType.valueOf( strNotificationType ) );
            }

            List<Notification> notifications = NotificationHome.getByDemandIdTypeIdCustomerId( strIdDemand, strIdDemandType, strCustomerId, filter );

            result.setNotifications( notifications );
            result.setStatus( ResponseStatusFactory.ok( ) );
            result.setNumberResult( notifications.size( ) );

            return Response.status( Response.Status.OK ).entity( NotificationStoreUtils.convertToJsonString( result ) ).build( );
        }
        else
        {
        	result.setStatus( ResponseStatusFactory.badRequest( ).setMessage( NotificationStoreConstants.MESSAGE_ERROR_NOTIF ).setMessageKey( SearchResult.ERROR_FIELD_MANDATORY) );
            
            return Response.status( Response.Status.BAD_REQUEST ).entity( NotificationStoreUtils.convertToJsonString( result ) ).build( );
        }
    }
    
    /**
     * Gets list of notification types
     * 
     * @return list of demand types
     */
    @GET
    @Path( NotificationStoreConstants.PATH_NOTIFICATION + NotificationStoreConstants.PATH_TYPE_NOTIFICATION )
    @Produces( MediaType.APPLICATION_JSON )
    public Response getNotificationTypes( )
    {
        String strResult = NotificationStoreUtils.convertToJsonString( EnumNotificationType.values( ) );
        return Response.ok( strResult ).build( );
    }

}