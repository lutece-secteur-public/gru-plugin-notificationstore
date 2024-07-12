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

import fr.paris.lutece.plugins.grubusiness.business.demand.DemandCategory;
import fr.paris.lutece.plugins.notificationstore.business.DemandCategoryHome;
import fr.paris.lutece.plugins.notificationstore.utils.NotificationStoreConstants;
import fr.paris.lutece.plugins.rest.service.RestConstants;
import fr.paris.lutece.util.json.ErrorJsonResponse;
import fr.paris.lutece.util.json.JsonResponse;
import fr.paris.lutece.util.json.JsonUtil;

/**
 * 
 * CategoryRest
 *
 */
@Path( RestConstants.BASE_PATH + NotificationStoreConstants.PLUGIN_NAME + NotificationStoreConstants.PATH_CATEGORY )
public class DemandCategoryRestService
{

    /**
     * Get DemandCategory List
     * @param nVersion the API version
     * @return the DemandCategory List
     */
    @GET
    @Path( StringUtils.EMPTY )
    @Produces( MediaType.APPLICATION_JSON )
    public Response getDemandCategoryList( )
    {
        List<DemandCategory> listDemandCategorys = DemandCategoryHome.getDemandCategoriesList( );
        
        if ( listDemandCategorys.isEmpty( ) )
        {
            return Response.status( Response.Status.NO_CONTENT )
                .entity( JsonUtil.buildJsonResponse( new JsonResponse( "{}" ) ) )
                .build( );
        }
        return Response.status( Response.Status.OK )
                .entity( JsonUtil.buildJsonResponse( new JsonResponse( listDemandCategorys ) ) )
                .build( );
    }
    
    
    /**
     * Create DemandCategory
     * @param nVersion the API version
     * @param code the code
     * @param label the label
     * @return the DemandCategory if created
     */
    @POST
    @Path( StringUtils.EMPTY )
    @Produces( MediaType.APPLICATION_JSON )
    public Response createDemandCategory( @FormParam( NotificationStoreConstants.DEMANDCATEGORY_ATTRIBUTE_CODE ) String code, 
            @FormParam( NotificationStoreConstants.DEMANDCATEGORY_ATTRIBUTE_LABEL ) String label )
    {
        if ( StringUtils.isEmpty( code ) || StringUtils.isEmpty( label ) )
        {
            return Response.status( Response.Status.BAD_REQUEST )
                    .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.BAD_REQUEST.name( ), NotificationStoreConstants.MESSAGE_ERROR_BAD_REQUEST_EMPTY_PARAMETER ) ) )
                    .build( );
        }
        
        DemandCategory demandcategory = new DemandCategory( );
        demandcategory.setCode( code );
        demandcategory.setLabel( label );
        DemandCategoryHome.create( demandcategory );
        
        return Response.status( Response.Status.OK )
                .entity( JsonUtil.buildJsonResponse( new JsonResponse( demandcategory ) ) )
                .build( );
    }
    
    
    /**
     * Modify DemandCategory
     * @param nVersion the API version
     * @param id the id
     * @param code the code
     * @param label the label
     * @return the DemandCategory if modified
     */
    @PUT
    @Path( NotificationStoreConstants.PATH_ID )
    @Produces( MediaType.APPLICATION_JSON )
    public Response modifyDemandCategory( @PathParam( NotificationStoreConstants.ID ) Integer id,
            @FormParam( NotificationStoreConstants.DEMANDCATEGORY_ATTRIBUTE_CODE ) String code,
            @FormParam( NotificationStoreConstants.DEMANDCATEGORY_ATTRIBUTE_LABEL ) String label )
    {
        if ( StringUtils.isEmpty( code ) || StringUtils.isEmpty( label ) )
        {
            return Response.status( Response.Status.BAD_REQUEST )
                    .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.BAD_REQUEST.name( ), NotificationStoreConstants.MESSAGE_ERROR_BAD_REQUEST_EMPTY_PARAMETER ) ) )
                    .build( );
        }
        
        Optional<DemandCategory> optDemandCategory = DemandCategoryHome.findByPrimaryKey( id );
        
        if ( !optDemandCategory.isPresent( ) )
        {
            return Response.status( Response.Status.NOT_FOUND )
                    .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.name( ), NotificationStoreConstants.MESSAGE_ERROR_NOT_FOUND_RESOURCE ) ) )
                    .build( );
        }
        else
        {
            DemandCategory demandcategory = optDemandCategory.get( );
            demandcategory.setCode( code );
            demandcategory.setLabel( label );
            DemandCategoryHome.update( demandcategory );
            
            return Response.status( Response.Status.OK )
                    .entity( JsonUtil.buildJsonResponse( new JsonResponse( demandcategory ) ) )
                    .build( );
        }
    }
    
    
    /**
     * Delete DemandCategory
     * @param nVersion the API version
     * @param id the id
     * @return the DemandCategory List if deleted
     */
    @DELETE
    @Path( NotificationStoreConstants.PATH_ID )
    @Produces( MediaType.APPLICATION_JSON )
    public Response deleteDemandCategory( @PathParam( NotificationStoreConstants.ID ) Integer id )
    {
        Optional<DemandCategory> optDemandCategory = DemandCategoryHome.findByPrimaryKey( id );
        if ( !optDemandCategory.isPresent( ) )
        {
            return Response.status( Response.Status.NOT_FOUND )
                    .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.name( ), NotificationStoreConstants.MESSAGE_ERROR_NOT_FOUND_RESOURCE ) ) )
                    .build( );
        }
        
        DemandCategoryHome.remove( id );
        
        return Response.status( Response.Status.OK )
                .entity( JsonUtil.buildJsonResponse( new JsonResponse( "{}" ) ) )
                .build( );
    }
    
    
    /**
     * Get DemandCategory
     * @param nVersion the API version
     * @param id the id
     * @return the DemandCategory
     */
    @GET
    @Path( NotificationStoreConstants.PATH_ID )
    @Produces( MediaType.APPLICATION_JSON )
    public Response getDemandCategory( @PathParam( NotificationStoreConstants.ID ) Integer id )
    {
        Optional<DemandCategory> optDemandCategory = DemandCategoryHome.findByPrimaryKey( id );
        if ( !optDemandCategory.isPresent( ) )
        {
            return Response.status( Response.Status.NOT_FOUND )
                    .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.NOT_FOUND.name( ), NotificationStoreConstants.MESSAGE_ERROR_NOT_FOUND_RESOURCE ) ) )
                    .build( );
        }
        
        return Response.status( Response.Status.OK )
                .entity( JsonUtil.buildJsonResponse( new JsonResponse( optDemandCategory.get( ) ) ) )
                .build( );
    }
}
