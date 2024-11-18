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
package fr.paris.lutece.plugins.notificationstore.web.rs;

import java.util.Optional;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.paris.lutece.plugins.grubusiness.business.demand.TemporaryStatus;
import fr.paris.lutece.plugins.grubusiness.business.web.rs.EnumGenericStatus;
import fr.paris.lutece.plugins.notificationstore.service.TemporaryStatusService;
import fr.paris.lutece.plugins.notificationstore.utils.NotificationStoreConstants;
import fr.paris.lutece.plugins.notificationstore.utils.NotificationStoreUtils;
import fr.paris.lutece.plugins.rest.service.RestConstants;
import fr.paris.lutece.util.json.ErrorJsonResponse;
import fr.paris.lutece.util.json.JsonResponse;
import fr.paris.lutece.util.json.JsonUtil;

/**
 * 
 * StatusRest
 *
 */
@Path( RestConstants.BASE_PATH + NotificationStoreConstants.PLUGIN_NAME + NotificationStoreConstants.VERSION_PATH_V3 + NotificationStoreConstants.PATH_DEMAND_STATUS )
public class StatusRestService
{

    /**
     * Gets list status
     * 
     * @return list status
     */
    @GET( )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public Response getListStatus( )
    {
        return Response.status( Response.Status.OK ).entity( JsonUtil.buildJsonResponse( new JsonResponse(  TemporaryStatusService.getInstance( ).getStatusList( ) ) ) ).build( );
    }

    /**
     * Get status by id
     * 
     * @param nId
     * @return status by id
     */
    @GET
    @Path( NotificationStoreConstants.PATH_ID )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public Response getStatus( @PathParam( NotificationStoreConstants.ID ) int nId )
    {
        Optional<TemporaryStatus> status =  TemporaryStatusService.getInstance( ).findByPrimaryKey( nId );

        if ( status.isPresent( ) )
        {
            return Response.status( Response.Status.OK ).entity( JsonUtil.buildJsonResponse( new JsonResponse( status.get( ) ) ) ).build( );
        }

        return Response.status( Response.Status.NOT_FOUND )
                .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.getReasonPhrase( ) ) ) ).build( );

    }

    /**
     * Create status
     * 
     * @param strStatus
     * @return created status
     */
    @POST
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public Response doCreateStatus( String strStatus )
    {

        try
        {
            TemporaryStatus status = NotificationStoreUtils.getMapper( ).readValue( strStatus, TemporaryStatus.class );

            if ( isStatusCompleted( status ) )
            {
                TemporaryStatusService.getInstance( ).create( status );
                return Response.status( Response.Status.CREATED ).entity( JsonUtil.buildJsonResponse( new JsonResponse( status ) ) ).build( );
            }

            return Response.status( Response.Status.NOT_ACCEPTABLE ).entity( JsonUtil
                    .buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_ACCEPTABLE.name( ), NotificationStoreConstants.MESSAGE_ALL_REQUIRED ) ) )
                    .build( );

        }
        catch( JsonProcessingException e )
        {
            return Response.status( Response.Status.BAD_REQUEST )
                    .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.BAD_REQUEST.getReasonPhrase( ) ) ) ).build( );
        }
    }

    /**
     * Modify status
     * 
     * @param strStatus
     * @return modified status
     */
    @PUT
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public Response doModifyStatus( String strStatus )
    {

        try
        {
            TemporaryStatus status = NotificationStoreUtils.getMapper( ).readValue( strStatus, TemporaryStatus.class );

            if ( isStatusCompleted( status ) &&  TemporaryStatusService.getInstance( ).findByPrimaryKey( status.getId( ) ).isPresent( ) )
            {
                TemporaryStatusService.getInstance( ).update( status );
                return Response.status( Response.Status.OK ).entity( JsonUtil.buildJsonResponse( new JsonResponse( status ) ) ).build( );
            }
            return Response.status( Response.Status.NOT_MODIFIED ).entity( JsonUtil
                    .buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_MODIFIED.name( ), NotificationStoreConstants.MESSAGE_ALL_REQUIRED ) ) )
                    .build( );

        }
        catch( JsonProcessingException e )
        {
            return Response.status( Response.Status.BAD_REQUEST )
                    .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.BAD_REQUEST.getReasonPhrase( ) ) ) ).build( );
        }
    }

    /**
     * Delete status
     * 
     * @param nId
     * @return ok if is deleted
     */
    @DELETE
    @Path( NotificationStoreConstants.PATH_ID )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public Response doDeleteStatus( @PathParam( NotificationStoreConstants.ID ) int nId )
    {
        if (  TemporaryStatusService.getInstance( ).findByPrimaryKey( nId ).isPresent( ) )
        {
            TemporaryStatusService.getInstance( ).remove( nId );
            return Response.status( Response.Status.OK ).entity( JsonUtil.buildJsonResponse( new JsonResponse( Response.Status.OK ) ) ).build( );
        }
        return Response.status( Response.Status.NOT_FOUND )
                .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.getReasonPhrase( ) ) ) ).build( );

    }

    /**
     * Gets list status
     * 
     * @return list status
     */
    @GET( )
    @Path( NotificationStoreConstants.PATH_GENERIC_STATUS )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public Response getListGenericStatus( )
    {

        return Response.status( Response.Status.OK )
                .entity( JsonUtil.buildJsonResponse( new JsonResponse( NotificationStoreUtils.getEnumGenericStatusRefList( ) ) ) ).build( );

    }

    /**
     * True if status is completed
     * 
     * @param status
     * @return true if status is completed
     */
    private boolean isStatusCompleted( TemporaryStatus status )
    {
        return status != null && StringUtils.isNotEmpty( status.getStatus( ) )
                && EnumGenericStatus.getByStatusId( status.getGenericStatus( ).getStatusId( ) ) != null;
    }

}
