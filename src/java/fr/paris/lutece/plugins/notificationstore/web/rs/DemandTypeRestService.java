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

import java.util.List;
import java.util.Optional;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.grubusiness.business.demand.DemandType;
import fr.paris.lutece.plugins.notificationstore.business.DemandTypeHome;
import fr.paris.lutece.plugins.notificationstore.utils.NotificationStoreConstants;
import fr.paris.lutece.plugins.rest.service.RestConstants;
import fr.paris.lutece.util.json.ErrorJsonResponse;
import fr.paris.lutece.util.json.JsonResponse;
import fr.paris.lutece.util.json.JsonUtil;

/**
 * 
 * Service Rest DemandTypeRestService
 *
 */
@Path( RestConstants.BASE_PATH + NotificationStoreConstants.PLUGIN_NAME + NotificationStoreConstants.VERSION_PATH_V3 + NotificationStoreConstants.PATH_DEMAND_TYPE )
public class DemandTypeRestService
{

    /**
     * Get DemandType List
     * 
     * @param nVersion
     *            the API version
     * @return the DemandType List
     */
    @GET
    @Produces( MediaType.APPLICATION_JSON )
    public Response getDemandTypeList( )
    {
        List<DemandType> listDemandTypes = DemandTypeHome.getDemandTypesList( );

        if ( listDemandTypes.isEmpty( ) )
        {
            return Response.status( Response.Status.NO_CONTENT )
                    .entity( JsonUtil.buildJsonResponse( new JsonResponse( "{}" ) ) )
                    .build( );
        }
        return Response.status( Response.Status.OK )
                .entity( JsonUtil.buildJsonResponse( new JsonResponse( listDemandTypes ) ) )
                .build( );
    }

    /**
     * Create DemandType
     * 
     * @param nVersion
     *            the API version
     * @param strIdDemandType
     *            the id_demand_type
     * @param strLabel
     *            the label
     * @param strUrl
     *            the url
     * @param strAppCode
     *            the app_code
     * @param strCategory
     *            the category
     * @return the DemandType if created
     */
    @POST
    @Produces( MediaType.APPLICATION_JSON )
    public Response createDemandType( @FormParam( NotificationStoreConstants.DEMANDTYPE_ATTRIBUTE_ID_DEMAND_TYPE ) String strIdDemandType,
            @FormParam( NotificationStoreConstants.DEMANDTYPE_ATTRIBUTE_LABEL ) String strLabel,
            @FormParam( NotificationStoreConstants.DEMANDTYPE_ATTRIBUTE_URL ) String strUrl,
            @FormParam( NotificationStoreConstants.DEMANDTYPE_ATTRIBUTE_APP_CODE ) String strAppCode,
            @FormParam( NotificationStoreConstants.DEMANDTYPE_ATTRIBUTE_CATEGORY ) String strCategory )
    {
        if ( StringUtils.isEmpty( strIdDemandType ) || StringUtils.isEmpty( strLabel ) 
                || StringUtils.isEmpty( strUrl ) || StringUtils.isEmpty( strAppCode ) )
        {
            return Response.status( Response.Status.BAD_REQUEST )
                    .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.BAD_REQUEST.name( ), NotificationStoreConstants.MESSAGE_ERROR_BAD_REQUEST_EMPTY_PARAMETER ) ) )
                    .build( );
        }
        
        DemandType demandtype = new DemandType( );
        demandtype.setIdDemandType( Integer.parseInt( strIdDemandType ) );
        demandtype.setLabel( strLabel );
        demandtype.setUrl( strUrl );
        demandtype.setAppCode( strAppCode );
        demandtype.setCategory( strCategory );
        
        DemandTypeHome.create( demandtype );

        return Response.status( Response.Status.OK )
                .entity( JsonUtil.buildJsonResponse( new JsonResponse( demandtype ) ) )
                .build( );
    }

    /**
     * Modify DemandType
     * 
     * @param nVersion
     *            the API version
     * @param id
     *            the id
     * @param strIdDemandType
     *            the id_demand_type
     * @param strLabel
     *            the label
     * @param strUrl
     *            the url
     * @param strAppCode
     *            the app_code
     * @param strCategory
     *            the category
     * @return the DemandType if modified
     */
    @PUT
    @Path( NotificationStoreConstants.PATH_ID )
    @Produces( MediaType.APPLICATION_JSON )
    public Response modifyDemandType( @PathParam( NotificationStoreConstants.ID ) Integer nId,
            @FormParam( NotificationStoreConstants.DEMANDTYPE_ATTRIBUTE_ID_DEMAND_TYPE ) String strIdDemandType,
            @FormParam( NotificationStoreConstants.DEMANDTYPE_ATTRIBUTE_LABEL ) String strLabel,
            @FormParam( NotificationStoreConstants.DEMANDTYPE_ATTRIBUTE_URL ) String strUrl,
            @FormParam( NotificationStoreConstants.DEMANDTYPE_ATTRIBUTE_APP_CODE ) String strAppCode,
            @FormParam( NotificationStoreConstants.DEMANDTYPE_ATTRIBUTE_CATEGORY ) String strCategory )
    {
        if ( StringUtils.isEmpty( strIdDemandType ) || StringUtils.isEmpty( strLabel ) 
                || StringUtils.isEmpty( strUrl ) )
        {
            return Response.status( Response.Status.BAD_REQUEST )
                    .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.BAD_REQUEST.name( ), NotificationStoreConstants.MESSAGE_ERROR_BAD_REQUEST_EMPTY_PARAMETER ) ) )
                    .build( );
        }
        Optional<DemandType> optDemandType = DemandTypeHome.findByPrimaryKey( nId );
        
        if ( !optDemandType.isPresent( ) )
        {
            return Response.status( Response.Status.NOT_FOUND )
                    .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.name( ), NotificationStoreConstants.MESSAGE_ERROR_NOT_FOUND_RESOURCE ) ) )
                    .build( );
        }
        else
        {
            DemandType demandtype = optDemandType.get( );
            demandtype.setIdDemandType( Integer.parseInt( strIdDemandType ) );
            demandtype.setLabel( strLabel );
            demandtype.setUrl( strUrl );
            demandtype.setAppCode( StringUtils.isEmpty( strAppCode ) ? StringUtils.EMPTY : strAppCode );
            demandtype.setCategory( StringUtils.isEmpty( strCategory ) ? StringUtils.EMPTY : strCategory );
            
            DemandTypeHome.update( demandtype );

            return Response.status( Response.Status.OK )
                    .entity( JsonUtil.buildJsonResponse( new JsonResponse( demandtype ) ) )
                    .build( );
        }
    }

    /**
     * Delete DemandType
     * 
     * @param nVersion
     *            the API version
     * @param nId
     *            the nId
     * @return the DemandType List if deleted
     */
    @DELETE
    @Path( NotificationStoreConstants.PATH_ID )
    @Produces( MediaType.APPLICATION_JSON )
    public Response deleteDemandType( @PathParam( NotificationStoreConstants.ID ) Integer nId )
    {
        Optional<DemandType> optDemandType = DemandTypeHome.findByPrimaryKey( nId );
        
        if ( !optDemandType.isPresent( ) )
        {
            return Response.status( Response.Status.NOT_FOUND )
                    .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.name( ), NotificationStoreConstants.MESSAGE_ERROR_NOT_FOUND_RESOURCE ) ) )
                    .build( );
        }

        DemandTypeHome.remove( nId );

        return Response.status( Response.Status.OK )
                .entity( JsonUtil.buildJsonResponse( new JsonResponse( "{}" ) ) )
                .build( );
    }

    /**
     * Get DemandType
     * 
     * @param nVersion
     *            the API version
     * @param nId
     *            the nId
     * @return the DemandType
     */
    @GET
    @Path( NotificationStoreConstants.PATH_ID )
    @Produces( MediaType.APPLICATION_JSON )
    public Response getDemandType( @PathParam( NotificationStoreConstants.ID ) Integer nId )
    {
        Optional<DemandType> optDemandType = DemandTypeHome.findByPrimaryKey( nId );
        if ( !optDemandType.isPresent( ) )
        {
            return Response.status( Response.Status.NOT_FOUND )
                    .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.name( ), NotificationStoreConstants.MESSAGE_ERROR_NOT_FOUND_RESOURCE ) ) )
                    .build( );
        }

        return Response.status( Response.Status.OK )
                .entity( JsonUtil.buildJsonResponse( new JsonResponse( optDemandType.get( ) ) ) )
                .build( );
    }

}
