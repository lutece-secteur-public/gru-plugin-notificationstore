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
package fr.paris.lutece.plugins.notificationstore.web;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.grubusiness.business.demand.DemandType;
import fr.paris.lutece.plugins.notificationstore.business.DemandCategoryHome;
import fr.paris.lutece.plugins.notificationstore.business.DemandTypeHome;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage DemandType features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageDemandTypes.jsp", controllerPath = "jsp/admin/plugins/notificationstore/", right = "NOTIFICATIONSTORE_DEMANDTYPE_MANAGEMENT" )
public class DemandTypeJspBean extends AbstractJspBean<Integer, DemandType>
{
    /**
     * 
     */
    private static final long serialVersionUID = 949798411107898964L;
    // Templates
    private static final String TEMPLATE_MANAGE_DEMANDTYPES = "/admin/plugins/notificationstore/demandType/manage_demandtypes.html";
    private static final String TEMPLATE_CREATE_DEMANDTYPE = "/admin/plugins/notificationstore/demandType/create_demandtype.html";
    private static final String TEMPLATE_MODIFY_DEMANDTYPE = "/admin/plugins/notificationstore/demandType/modify_demandtype.html";

    // Parameters
    private static final String PARAMETER_ID_DEMANDTYPE = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_DEMANDTYPES = "notificationstore.manage_demandtypes.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_DEMANDTYPE = "notificationstore.modify_demandtype.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_DEMANDTYPE = "notificationstore.create_demandtype.pageTitle";

    // Markers
    private static final String MARK_DEMANDTYPE = "demandtype";
    private static final String MARK_DEMANDCATEGORIES = "demandCategories";

    private static final String JSP_MANAGE_DEMANDTYPES = "jsp/admin/plugins/notificationstore/ManageDemandTypes.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_DEMANDTYPE = "notificationstore.message.confirmRemoveDemandType";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "notificationstore.model.entity.demandtype.attribute.";

    // Views
    private static final String VIEW_MANAGE_DEMANDTYPES = "manageDemandTypes";
    private static final String VIEW_CREATE_DEMANDTYPE = "createDemandType";
    private static final String VIEW_MODIFY_DEMANDTYPE = "modifyDemandType";

    // Actions
    private static final String ACTION_CREATE_DEMANDTYPE = "createDemandType";
    private static final String ACTION_MODIFY_DEMANDTYPE = "modifyDemandType";
    private static final String ACTION_REMOVE_DEMANDTYPE = "removeDemandType";
    private static final String ACTION_CONFIRM_REMOVE_DEMANDTYPE = "confirmRemoveDemandType";

    // Infos
    private static final String INFO_DEMANDTYPE_CREATED = "notificationstore.info.demandtype.created";
    private static final String INFO_DEMANDTYPE_UPDATED = "notificationstore.info.demandtype.updated";
    private static final String INFO_DEMANDTYPE_REMOVED = "notificationstore.info.demandtype.removed";

    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";

    // Session variable to store working values
    private DemandType _demandtype;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_DEMANDTYPES, defaultView = true )
    public String getManageDemandTypes( HttpServletRequest request )
    {
        _demandtype = null;

        fillModelWithSearchParamsAndResult( request, JSP_MANAGE_DEMANDTYPES );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_DEMANDTYPES, TEMPLATE_MANAGE_DEMANDTYPES, _model );
    }

    /**
     * Get Items from Ids list
     * 
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
    @Override
    List<DemandType> getItemsFromIds( List<Integer> listIds )
    {
        List<DemandType> listDemandType = DemandTypeHome.getDemandTypesListByIds( listIds );

        // keep original order
        return listDemandType.stream( ).sorted( Comparator.comparingInt( notif -> listIds.indexOf( notif.getId( ) ) ) ).collect( Collectors.toList( ) );
    }

    /**
     * getItemIdsList
     */
    @Override
    protected List<Integer> getItemIdsList( HashMap<String, String> _mapFilterCriteria, String strOrderByColumn, String strSortMode )
    {
        return DemandTypeHome.searchItemsIdList( _mapFilterCriteria, strOrderByColumn, strSortMode );
    }

    /**
     * Returns the form to create a demandtype
     *
     * @param request
     *            The Http request
     * @return the html code of the demandtype form
     */
    @View( VIEW_CREATE_DEMANDTYPE )
    public String getCreateDemandType( HttpServletRequest request )
    {
        _demandtype = ( _demandtype != null ) ? _demandtype : new DemandType( );

        Map<String, Object> model = getModel( );
        model.put( MARK_DEMANDTYPE, _demandtype );
        model.put( MARK_DEMANDCATEGORIES, DemandCategoryHome.getDemandCategoriesReferenceList( ) );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_DEMANDTYPE ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_DEMANDTYPE, TEMPLATE_CREATE_DEMANDTYPE, model );
    }

    /**
     * Process the data capture form of a new demandtype
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_DEMANDTYPE )
    public String doCreateDemandType( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _demandtype, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_DEMANDTYPE ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _demandtype, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_DEMANDTYPE );
        }

        DemandTypeHome.create( _demandtype );
        addInfo( INFO_DEMANDTYPE_CREATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_DEMANDTYPES );
    }

    /**
     * Manages the removal form of a demandtype whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_DEMANDTYPE )
    public String getConfirmRemoveDemandType( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DEMANDTYPE ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_DEMANDTYPE ) );
        url.addParameter( PARAMETER_ID_DEMANDTYPE, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_DEMANDTYPE, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a demandtype
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage demandtypes
     */
    @Action( ACTION_REMOVE_DEMANDTYPE )
    public String doRemoveDemandType( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DEMANDTYPE ) );

        DemandTypeHome.remove( nId );
        addInfo( INFO_DEMANDTYPE_REMOVED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_DEMANDTYPES );
    }

    /**
     * Returns the form to update info about a demandtype
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_DEMANDTYPE )
    public String getModifyDemandType( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DEMANDTYPE ) );

        if ( _demandtype == null || ( _demandtype.getIdDemandType( ) != nId ) )
        {
            Optional<DemandType> optDemandType = DemandTypeHome.findByPrimaryKey( nId );
            _demandtype = optDemandType.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_DEMANDTYPE, _demandtype );
        model.put( MARK_DEMANDCATEGORIES, DemandCategoryHome.getDemandCategoriesReferenceList( ) );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_DEMANDTYPE ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_DEMANDTYPE, TEMPLATE_MODIFY_DEMANDTYPE, model );
    }

    /**
     * Process the change form of a demandtype
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_DEMANDTYPE )
    public String doModifyDemandType( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _demandtype, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_DEMANDTYPE ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _demandtype, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_DEMANDTYPE, PARAMETER_ID_DEMANDTYPE, _demandtype.getIdDemandType( ) );
        }

        DemandTypeHome.update( _demandtype );
        addInfo( INFO_DEMANDTYPE_UPDATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_DEMANDTYPES );
    }
}
