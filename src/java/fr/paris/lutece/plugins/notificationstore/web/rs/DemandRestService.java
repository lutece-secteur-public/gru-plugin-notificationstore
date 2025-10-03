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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.grubusiness.business.demand.Demand;
import fr.paris.lutece.plugins.grubusiness.business.demand.IDemandServiceProvider;
import fr.paris.lutece.plugins.grubusiness.business.web.rs.DemandDisplay;
import fr.paris.lutece.plugins.grubusiness.business.web.rs.DemandResult;
import fr.paris.lutece.plugins.grubusiness.business.web.rs.EnumGenericStatus;
import fr.paris.lutece.plugins.grubusiness.business.web.rs.SearchResult;
import fr.paris.lutece.plugins.grubusiness.business.web.rs.responseStatus.ResponseStatusFactory;
import fr.paris.lutece.plugins.notificationstore.business.DemandHome;
import fr.paris.lutece.plugins.notificationstore.business.DemandTypeHome;
import fr.paris.lutece.plugins.notificationstore.utils.NotificationStoreConstants;
import fr.paris.lutece.plugins.notificationstore.utils.NotificationStoreUtils;
import fr.paris.lutece.plugins.notificationstore.web.rs.swagger.SwaggerConstants;
import fr.paris.lutece.plugins.rest.service.RestConstants;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.l10n.LocaleService;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.json.ErrorJsonResponse;
import fr.paris.lutece.util.json.JsonResponse;
import fr.paris.lutece.util.json.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 
 * Service Rest DemandNotificationRestService
 *
 */
@Path( RestConstants.BASE_PATH + NotificationStoreConstants.PLUGIN_NAME + NotificationStoreConstants.VERSION_PATH_V3 + NotificationStoreConstants.PATH_DEMAND )
@Api( RestConstants.BASE_PATH + NotificationStoreConstants.PLUGIN_NAME + NotificationStoreConstants.VERSION_PATH_V3 + NotificationStoreConstants.PATH_DEMAND )
public class DemandRestService
{

    @Inject
    @Named( "notificationstore.demandService" )
    private IDemandServiceProvider _demandService;

    /**
     * Return list of demand
     * 
     * @param strDemandType
     * @param strPage
     */
    @GET
    @Path( NotificationStoreConstants.PATH_LIST )
    @Produces( MediaType.APPLICATION_JSON )
    @ApiOperation( value = "Get demand list for a customer Id", response = DemandResult.class )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "Success" ), @ApiResponse( code = 400, message = "Bad request or missing mandatory parameters" ),
            @ApiResponse( code = 403, message = "Failure" )
    } )
    public Response getListDemand(
            @ApiParam( name = NotificationStoreConstants.QUERY_PARAM_ID_DEMAND_TYPE, value = SwaggerConstants.QUERY_PARAM_ID_DEMAND_TYPE_DESCRIPTION ) @QueryParam( NotificationStoreConstants.QUERY_PARAM_ID_DEMAND_TYPE ) String strIdDemandType,
            @ApiParam( name = NotificationStoreConstants.QUERY_PARAM_INDEX, value = SwaggerConstants.QUERY_PARAM_INDEX_DESCRIPTION ) @QueryParam( NotificationStoreConstants.QUERY_PARAM_INDEX ) String strIndex,
            @ApiParam( name = NotificationStoreConstants.QUERY_PARAM_LIMIT, value = SwaggerConstants.QUERY_PARAM_LIMIT_DESCRIPTION ) @QueryParam( NotificationStoreConstants.QUERY_PARAM_LIMIT ) String strLimitResult,
            @ApiParam( name = NotificationStoreConstants.QUERY_PARAM_CUSTOMER_ID, value = SwaggerConstants.QUERY_PARAM_CUSTOMER_ID_DESCRIPTION ) @QueryParam( NotificationStoreConstants.QUERY_PARAM_CUSTOMER_ID ) String strCustomerId,
            @ApiParam( name = NotificationStoreConstants.QUERY_PARAM_NOTIFICATION_TYPE, value = SwaggerConstants.QUERY_PARAM_NOTIFICATION_TYPE_DESCRIPTION ) @QueryParam( NotificationStoreConstants.QUERY_PARAM_NOTIFICATION_TYPE ) String strNotificationType,
            @ApiParam( name = NotificationStoreConstants.QUERY_PARAM_DIRECTION_DATE_ORDER_BY, value = SwaggerConstants.QUERY_PARAM_DIRECTION_DATE_ORDER_BY_DESCRIPTION ) @QueryParam( NotificationStoreConstants.QUERY_PARAM_DIRECTION_DATE_ORDER_BY ) @DefaultValue( "" ) String strDirectionDateOrderBy )
    {
        int nIndex = StringUtils.isEmpty( strIndex ) ? 1 : Integer.parseInt( strIndex );
        int nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( NotificationStoreConstants.LIMIT_DEMAND_API_REST, 10 );
        if ( StringUtils.isNotEmpty( strLimitResult ) )
        {
            nDefaultItemsPerPage = Integer.parseInt( strLimitResult );
        }

        DemandResult result = new DemandResult( );
        if ( StringUtils.isEmpty( strCustomerId ) )
        {
            result.setStatus( ResponseStatusFactory.badRequest( ).setMessage( NotificationStoreConstants.MESSAGE_ERROR_DEMAND )
                    .setMessageKey( SearchResult.ERROR_FIELD_MANDATORY ) );
            return Response.status( Response.Status.BAD_REQUEST ).entity( NotificationStoreUtils.convertToJsonString( result ) ).build( );
        }
        if ( StringUtils.isNotEmpty( strDirectionDateOrderBy ) && !List.of( "ASC", "DESC" ).contains( strDirectionDateOrderBy ) )
        {
            result.setStatus( ResponseStatusFactory.badRequest( ).setMessage( NotificationStoreConstants.MESSAGE_ERROR_DIRECTION_DATE_ORDER_BY_WRONG_VALUE )
                    .setMessageKey( SearchResult.ERROR_FIELD_WRONG_VALUE ) );
            return Response.status( Response.Status.BAD_REQUEST ).entity( NotificationStoreUtils.convertToJsonString( result ) ).build( );
        }

        List<Integer> listIds = DemandHome.getIdsByCustomerIdAndDemandTypeId( strCustomerId, strNotificationType, strIdDemandType, strDirectionDateOrderBy );
        return getResponse( result, nIndex, nDefaultItemsPerPage, listIds );
    }

    /**
     * Get list by status
     * 
     * @param strIdDemandType
     * @param strIndex
     * @param strCustomerId
     * @return list of active demand
     */
    @GET
    @Path( NotificationStoreConstants.PATH_DEMAND_STATUS )
    @Produces( MediaType.APPLICATION_JSON )
    @ApiOperation( value = "Get demand list for a customer Id by status", response = DemandResult.class )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "Success (with or without result)" ),
            @ApiResponse( code = 400, message = "Bad request or missing mandatory parameters" ), @ApiResponse( code = 403, message = "Failure" )
    } )
    public Response getListOfDemandByStatus(
            @ApiParam( name = NotificationStoreConstants.QUERY_PARAM_ID_DEMAND_TYPE, value = SwaggerConstants.QUERY_PARAM_ID_DEMAND_TYPE_DESCRIPTION ) @QueryParam( NotificationStoreConstants.QUERY_PARAM_ID_DEMAND_TYPE ) String strIdDemandType,
            @ApiParam( name = NotificationStoreConstants.QUERY_PARAM_INDEX, value = SwaggerConstants.QUERY_PARAM_INDEX_DESCRIPTION ) @QueryParam( NotificationStoreConstants.QUERY_PARAM_INDEX ) String strIndex,
            @ApiParam( name = NotificationStoreConstants.QUERY_PARAM_LIMIT, value = SwaggerConstants.QUERY_PARAM_LIMIT_DESCRIPTION ) @QueryParam( NotificationStoreConstants.QUERY_PARAM_LIMIT ) String strLimitResult,
            @ApiParam( name = NotificationStoreConstants.QUERY_PARAM_CUSTOMER_ID, value = SwaggerConstants.QUERY_PARAM_CUSTOMER_ID_DESCRIPTION ) @QueryParam( NotificationStoreConstants.QUERY_PARAM_CUSTOMER_ID ) String strCustomerId,
            @ApiParam( name = NotificationStoreConstants.QUERY_PARAM_LIST_STATUS, value = SwaggerConstants.QUERY_PARAM_LIST_STATUS_DESCRIPTION ) @QueryParam( NotificationStoreConstants.QUERY_PARAM_LIST_STATUS ) String strListStatus,
            @ApiParam( name = NotificationStoreConstants.QUERY_PARAM_NOTIFICATION_TYPE, value = SwaggerConstants.QUERY_PARAM_NOTIFICATION_TYPE_DESCRIPTION ) @QueryParam( NotificationStoreConstants.QUERY_PARAM_NOTIFICATION_TYPE ) String strNotificationType,
            @ApiParam( name = NotificationStoreConstants.QUERY_PARAM_CATEGORY_CODE, value = SwaggerConstants.QUERY_PARAM_CATEGORY_CODE_DESCRIPTION ) @QueryParam( NotificationStoreConstants.QUERY_PARAM_CATEGORY_CODE ) String strCategoryCode )
    {
        int nIndex = StringUtils.isEmpty( strIndex ) ? 1 : Integer.parseInt( strIndex );
        int nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( NotificationStoreConstants.LIMIT_DEMAND_API_REST, 10 );
        if ( StringUtils.isNotEmpty( strLimitResult ) )
        {
            nDefaultItemsPerPage = Integer.parseInt( strLimitResult );
        }

        DemandResult result = new DemandResult( );

        // Retrieving request types related to the category as a parameter.
        StringBuilder sbIdsTypeDemand = new StringBuilder( );
        if ( StringUtils.isNotEmpty( strIdDemandType ) )
        {
            sbIdsTypeDemand.append( Integer.parseInt( strIdDemandType ) + "," );
        }
        if ( StringUtils.isNotEmpty( strCategoryCode ) )
        {
            DemandTypeHome.getDemandTypesListByCategoryCode( strCategoryCode ).stream( ).forEach( dt -> sbIdsTypeDemand.append( dt.getIdDemandType( ) + "," ) );
        }

        // If no request type is found for the parameter category
        if ( StringUtils.isNotEmpty( strCategoryCode ) && sbIdsTypeDemand.length( ) < 1 )
        {
            result.setStatus( ResponseStatusFactory.noResult( ).setMessageKey( "no_result" ) );
            return Response.status( result.getStatus( ).getHttpCode( ) ).entity( result ).build( );
        }

        if ( StringUtils.isEmpty( strCustomerId ) || StringUtils.isEmpty( strListStatus ) )
        {
            result.setStatus( ResponseStatusFactory.badRequest( ) );

            result.setStatus( ResponseStatusFactory.badRequest( ).setMessage( NotificationStoreConstants.MESSAGE_ERROR_STATUS )
                    .setMessageKey( SearchResult.ERROR_FIELD_MANDATORY ) );

            return Response.status( Response.Status.BAD_REQUEST ).entity( NotificationStoreUtils.convertToJsonString( result ) ).build( );
        }

        List<String> listStatus = Arrays.asList( strListStatus.split( "," ) );
        List<Integer> listIds = DemandHome.getIdsByStatus( strCustomerId, listStatus, strNotificationType, sbIdsTypeDemand.toString( ) );

        return getResponse( result, nIndex, nDefaultItemsPerPage, listIds );
    }

    @DELETE
    @Path( NotificationStoreConstants.PATH_CUSTOMER_ID )
    @Consumes( MediaType.APPLICATION_JSON )
    @Produces( MediaType.APPLICATION_JSON )
    public Response doDeleteAllDemands( @PathParam( NotificationStoreConstants.QUERY_PARAM_CUSTOMER_ID ) String strCustomerId )
    {

        if ( StringUtils.isNotEmpty( strCustomerId ) )
        {
            _demandService.deleteAllDemandByCustomerId( strCustomerId );

            return Response.status( Response.Status.OK ).entity( JsonUtil.buildJsonResponse( new JsonResponse( Response.Status.OK ) ) ).build( );
        }
        else
        {
            return Response.status( Response.Status.BAD_REQUEST )
                    .entity( JsonUtil.buildJsonResponse( new ErrorJsonResponse( Response.Status.BAD_REQUEST.getReasonPhrase( ) ) ) ).build( );

        }

    }

    /**
     * Get response
     * 
     * @param result
     * @param nIndex
     * @param nDefaultItemsPerPage
     * @param listIds
     * @return
     */
    private Response getResponse( DemandResult result, int nIndex, int nDefaultItemsPerPage, List<Integer> listIds )
    {

        if ( !listIds.isEmpty( ) )
        {
            Paginator<Integer> paginator = new Paginator<>( listIds, nDefaultItemsPerPage, StringUtils.EMPTY, StringUtils.EMPTY, String.valueOf( nIndex ) );

            result.setListDemandDisplay( getListDemandDisplay( paginator.getPageItems( ) ) );
            result.setIndex( String.valueOf( nIndex ) );
            result.setPaginator( nIndex + "/" + paginator.getPagesCount( ) );
            result.setNumberResult( listIds.size( ) );

            result.setStatus( ResponseStatusFactory.ok( ) );
        }
        else
        {
            result.setStatus( ResponseStatusFactory.noResult( ).setMessageKey( "no_result" ) );
        }

        return Response.status( result.getStatus( ).getHttpCode( ) ).entity( result ).build( );
    }

    /**
     * 
     * @param listIds
     * @return list of demand display
     */
    private List<DemandDisplay> getListDemandDisplay( List<Integer> listIds )
    {
        List<DemandDisplay> listDemandDisplay = new ArrayList<>( );
        List<Demand> listDemand = DemandHome.getByIds( listIds );

        for ( Demand demand : listDemand )
        {
            DemandDisplay demandDisplay = new DemandDisplay( );
            demandDisplay.setDemand( demand );
            demandDisplay.setStatus( getLabelStatus( demand ) );

            listDemandDisplay.add( demandDisplay );
        }

        // keep original order
        return listDemandDisplay.stream( ).sorted( Comparator.comparingInt( dem -> listIds.indexOf( dem.getDemand( ).getUID( ) ) ) )
                .collect( Collectors.toList( ) );
    }

    /**
     * Get status label by demand status id
     * 
     * @param demand
     * @return Generic status label
     */
    private String getLabelStatus( Demand demand )
    {
        EnumGenericStatus enumGenericStatus = EnumGenericStatus.getByStatusId( demand.getStatusId( ) );
        if ( enumGenericStatus != null )
        {
            return I18nService.getLocalizedString( enumGenericStatus.getLabel( ), LocaleService.getDefault( ) );
        }
        return StringUtils.EMPTY;
    }

}
