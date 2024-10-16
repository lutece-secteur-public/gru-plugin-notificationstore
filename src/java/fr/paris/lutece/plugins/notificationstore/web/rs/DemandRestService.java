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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.grubusiness.business.demand.Demand;
import fr.paris.lutece.plugins.grubusiness.business.demand.DemandStatus;
import fr.paris.lutece.plugins.grubusiness.business.demand.IDemandServiceProvider;
import fr.paris.lutece.plugins.grubusiness.business.notification.Notification;
import fr.paris.lutece.plugins.grubusiness.business.web.rs.DemandDisplay;
import fr.paris.lutece.plugins.grubusiness.business.web.rs.DemandResult;
import fr.paris.lutece.plugins.grubusiness.business.web.rs.EnumGenericStatus;
import fr.paris.lutece.plugins.grubusiness.business.web.rs.SearchResult;
import fr.paris.lutece.plugins.grubusiness.business.web.rs.responseStatus.ResponseStatusFactory;
import fr.paris.lutece.plugins.notificationstore.business.DemandHome;
import fr.paris.lutece.plugins.notificationstore.business.DemandTypeHome;
import fr.paris.lutece.plugins.notificationstore.business.NotificationHome;
import fr.paris.lutece.plugins.notificationstore.business.StatusHome;
import fr.paris.lutece.plugins.notificationstore.utils.NotificationStoreConstants;
import fr.paris.lutece.plugins.notificationstore.utils.NotificationStoreUtils;
import fr.paris.lutece.plugins.rest.service.RestConstants;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.l10n.LocaleService;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.json.ErrorJsonResponse;
import fr.paris.lutece.util.json.JsonResponse;
import fr.paris.lutece.util.json.JsonUtil;

/**
 * 
 * Service Rest DemandNotificationRestService
 *
 */
@Path( RestConstants.BASE_PATH + NotificationStoreConstants.PLUGIN_NAME + NotificationStoreConstants.VERSION_PATH_V3 + NotificationStoreConstants.PATH_DEMAND )
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
    public Response getListDemand( @QueryParam( NotificationStoreConstants.QUERY_PARAM_ID_DEMAND_TYPE ) String strIdDemandType,
            @QueryParam( NotificationStoreConstants.QUERY_PARAM_INDEX ) String strIndex,
            @QueryParam( NotificationStoreConstants.QUERY_PARAM_LIMIT ) String strLimitResult,
            @QueryParam( NotificationStoreConstants.QUERY_PARAM_CUSTOMER_ID ) String strCustomerId,
            @QueryParam( NotificationStoreConstants.QUERY_PARAM_NOTIFICATION_TYPE ) String strNotificationType )
    {
        int nIndex = StringUtils.isEmpty( strIndex ) ? 1 : Integer.parseInt( strIndex );
        int nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( NotificationStoreConstants.LIMIT_DEMAND_API_REST, 10 );
        if( StringUtils.isNotEmpty( strLimitResult ) )
        {
            nDefaultItemsPerPage = Integer.parseInt( strLimitResult );
        }

        DemandResult result = new DemandResult( );
        if ( StringUtils.isEmpty( strCustomerId ) )
        {
        	result.setStatus( ResponseStatusFactory.badRequest( )
        			.setMessage(NotificationStoreConstants.MESSAGE_ERROR_DEMAND )
        			.setMessageKey(SearchResult.ERROR_FIELD_MANDATORY));
            return Response.status( Response.Status.BAD_REQUEST ).entity( NotificationStoreUtils.convertToJsonString( result ) ).build( );
        }

        List<Integer> listIds = DemandHome.getIdsByCustomerIdAndDemandTypeId( strCustomerId, strNotificationType, strIdDemandType );
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
    public Response getListOfDemandByStatus( @QueryParam( NotificationStoreConstants.QUERY_PARAM_ID_DEMAND_TYPE ) String strIdDemandType,
            @QueryParam( NotificationStoreConstants.QUERY_PARAM_INDEX ) String strIndex,
            @QueryParam( NotificationStoreConstants.QUERY_PARAM_LIMIT ) String strLimitResult,
            @QueryParam( NotificationStoreConstants.QUERY_PARAM_CUSTOMER_ID ) String strCustomerId,
            @QueryParam( NotificationStoreConstants.QUERY_PARAM_LIST_STATUS ) String strListStatus,
            @QueryParam( NotificationStoreConstants.QUERY_PARAM_NOTIFICATION_TYPE ) String strNotificationType,
            @QueryParam( NotificationStoreConstants.QUERY_PARAM_CATEGORY_CODE ) String strCategoryCode )
    {
        int nIndex = StringUtils.isEmpty( strIndex ) ? 1 : Integer.parseInt( strIndex );
        int nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( NotificationStoreConstants.LIMIT_DEMAND_API_REST, 10 );
        if( StringUtils.isNotEmpty( strLimitResult ) )
        {
            nDefaultItemsPerPage = Integer.parseInt( strLimitResult );
        }
        
        DemandResult result = new DemandResult( );
        
        //Récupération des types de demande liés à la catégorie en paramètre.
        StringBuilder sbIdsTypeDemand = new StringBuilder( );
        if(StringUtils.isNotEmpty( strIdDemandType ))
        {
            sbIdsTypeDemand.append( Integer.parseInt( strIdDemandType ) + "," );
        }
        if( StringUtils.isNotEmpty( strCategoryCode ) )
        {
            DemandTypeHome.getDemandTypesListByCategoryCode( strCategoryCode ).stream( ).forEach( dt -> sbIdsTypeDemand.append( dt.getIdDemandType( ) + "," ) );
        }
        
        //Si aucun type de demande n'est trouvé pour la catégorie en paramètre
        if( StringUtils.isNotEmpty( strCategoryCode ) && sbIdsTypeDemand.length( ) < 1  )
        {
            result.setStatus( ResponseStatusFactory.noResult( ).setMessageKey( "no_result" ) );      
            return Response.status( result.getStatus( ).getHttpCode( ) ).entity( result ).build( );
        }
               
        if ( StringUtils.isEmpty( strCustomerId ) || StringUtils.isEmpty( strListStatus ) )
        {
        	result.setStatus( ResponseStatusFactory.badRequest( ) );
        			
        	result.setStatus( ResponseStatusFactory.badRequest( )
        			.setMessage( NotificationStoreConstants.MESSAGE_ERROR_STATUS )
        			.setMessageKey(SearchResult.ERROR_FIELD_MANDATORY));

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
    public Response doDeleteAllDemands ( @PathParam( NotificationStoreConstants.QUERY_PARAM_CUSTOMER_ID ) String  strCustomerId )
    {
        
        if( StringUtils.isNotEmpty( strCustomerId ) )
        {
            _demandService.deleteAllDemandByCustomerId( strCustomerId );
            
            return Response.status( Response.Status.OK )
                    .entity( JsonUtil.buildJsonResponse( new JsonResponse( Response.Status.OK ) ) ).build( );            
        } else
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
        
        // extends ResponseDto
        //return Response.status( Response.Status.OK  ).entity( entity ).type( MediaType.APPLICATION_JSON_TYPE ).build( );
        //return Response.status( entity.getStatus( ).getHttpCode( ) ).entity( entity ).type( MediaType.APPLICATION_JSON_TYPE ).build( );
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
        Collections.reverse( listDemandDisplay );
        return listDemandDisplay;
    }

    /**
     * Get status label
     * 
     * @param demand
     * @return status
     */
    private String getLabelStatus( Demand demand )
    {
        Notification notification = NotificationHome.getLastNotifByDemandIdAndDemandTypeId( String.valueOf( demand.getId( ) ),
                String.valueOf( demand.getTypeId( ) ) );

        if ( notification.getMyDashboardNotification( ) != null )
        {
            EnumGenericStatus enumGenericStatus = EnumGenericStatus.getByStatusId( notification.getMyDashboardNotification( ).getStatusId( ) );
            if ( enumGenericStatus != null )
            {
                return I18nService.getLocalizedString( enumGenericStatus.getLabel( ), LocaleService.getDefault( ) );
            }
            else
            {
                Optional<DemandStatus> status = StatusHome.findByStatus( notification.getMyDashboardNotification( ).getStatusText( ) );
                if ( status.isPresent( ) && status.get( ).getGenericStatus( ) != null )
                {
                    return I18nService.getLocalizedString( status.get( ).getGenericStatus( ).getLabel( ), LocaleService.getDefault( ) );
                }
            }
            return notification.getMyDashboardNotification( ).getStatusText( );
        }

        return StringUtils.EMPTY;
    }


}
