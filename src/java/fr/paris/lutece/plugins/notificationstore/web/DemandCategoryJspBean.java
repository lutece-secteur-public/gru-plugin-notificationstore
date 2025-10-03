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

import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.Action;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.url.UrlItem;
import fr.paris.lutece.util.html.AbstractPaginator;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.grubusiness.business.demand.DemandCategory;
import fr.paris.lutece.plugins.notificationstore.business.DemandCategoryHome;

/**
 * This class provides the user interface to manage DemandCategory features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageDemandCategorys.jsp", controllerPath = "jsp/admin/plugins/notificationstore/", right = "NOTIFICATIONSTORE_MANAGEMENT" )
public class DemandCategoryJspBean extends AbstractManageDemandTypeJspBean<Integer, DemandCategory>
{
    // Templates
    private static final String TEMPLATE_MANAGE_DEMANDCATEGORYS = "/admin/plugins/notificationstore/demandType/manage_demandcategorys.html";
    private static final String TEMPLATE_CREATE_DEMANDCATEGORY = "/admin/plugins/notificationstore/demandType/create_demandcategory.html";
    private static final String TEMPLATE_MODIFY_DEMANDCATEGORY = "/admin/plugins/notificationstore/demandType/modify_demandcategory.html";

    // Parameters
    private static final String PARAMETER_ID_DEMANDCATEGORY = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_DEMANDCATEGORYS = "notificationstore.manage_demandcategorys.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_DEMANDCATEGORY = "notificationstore.modify_demandcategory.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_DEMANDCATEGORY = "notificationstore.create_demandcategory.pageTitle";

    // Markers
    private static final String MARK_DEMANDCATEGORY_LIST = "demandcategory_list";
    private static final String MARK_DEMANDCATEGORY = "demandcategory";

    private static final String JSP_MANAGE_DEMANDCATEGORYS = "jsp/admin/plugins/notificationstore/ManageDemandCategorys.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_DEMANDCATEGORY = "notificationstore.message.confirmRemoveDemandCategory";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "notificationstore.model.entity.demandcategory.attribute.";

    // Views
    private static final String VIEW_MANAGE_DEMANDCATEGORYS = "manageDemandCategorys";
    private static final String VIEW_CREATE_DEMANDCATEGORY = "createDemandCategory";
    private static final String VIEW_MODIFY_DEMANDCATEGORY = "modifyDemandCategory";

    // Actions
    private static final String ACTION_CREATE_DEMANDCATEGORY = "createDemandCategory";
    private static final String ACTION_MODIFY_DEMANDCATEGORY = "modifyDemandCategory";
    private static final String ACTION_REMOVE_DEMANDCATEGORY = "removeDemandCategory";
    private static final String ACTION_CONFIRM_REMOVE_DEMANDCATEGORY = "confirmRemoveDemandCategory";

    // Infos
    private static final String INFO_DEMANDCATEGORY_CREATED = "notificationstore.info.demandcategory.created";
    private static final String INFO_DEMANDCATEGORY_UPDATED = "notificationstore.info.demandcategory.updated";
    private static final String INFO_DEMANDCATEGORY_REMOVED = "notificationstore.info.demandcategory.removed";

    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";

    // Session variable to store working values
    private DemandCategory _demandcategory;
    private List<Integer> _listIdDemandCategorys;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_DEMANDCATEGORYS, defaultView = true )
    public String getManageDemandCategorys( HttpServletRequest request )
    {
        _demandcategory = null;

        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null || _listIdDemandCategorys.isEmpty( ) )
        {
            _listIdDemandCategorys = DemandCategoryHome.getIdDemandCategoriesList( );
        }

        Map<String, Object> model = getPaginatedListModel( request, MARK_DEMANDCATEGORY_LIST, _listIdDemandCategorys, JSP_MANAGE_DEMANDCATEGORYS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_DEMANDCATEGORYS, TEMPLATE_MANAGE_DEMANDCATEGORYS, model );
    }

    /**
     * Get Items from Ids list
     * 
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
    @Override
    List<DemandCategory> getItemsFromIds( List<Integer> listIds )
    {
        List<DemandCategory> listDemandCategory = DemandCategoryHome.getDemandCategoriesListByIds( listIds );

        // keep original order
        return listDemandCategory.stream( ).sorted( Comparator.comparingInt( notif -> listIds.indexOf( notif.getId( ) ) ) ).collect( Collectors.toList( ) );
    }

    /**
     * reset the _listIdDemandCategorys list
     */
    public void resetListId( )
    {
        _listIdDemandCategorys = new ArrayList<>( );
    }

    /**
     * Returns the form to create a demandcategory
     *
     * @param request
     *            The Http request
     * @return the html code of the demandcategory form
     */
    @View( VIEW_CREATE_DEMANDCATEGORY )
    public String getCreateDemandCategory( HttpServletRequest request )
    {
        _demandcategory = ( _demandcategory != null ) ? _demandcategory : new DemandCategory( );

        Map<String, Object> model = getModel( );
        model.put( MARK_DEMANDCATEGORY, _demandcategory );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_DEMANDCATEGORY ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_DEMANDCATEGORY, TEMPLATE_CREATE_DEMANDCATEGORY, model );
    }

    /**
     * Process the data capture form of a new demandcategory
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_DEMANDCATEGORY )
    public String doCreateDemandCategory( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _demandcategory, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_DEMANDCATEGORY ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _demandcategory, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_DEMANDCATEGORY );
        }

        DemandCategoryHome.create( _demandcategory );
        addInfo( INFO_DEMANDCATEGORY_CREATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_DEMANDCATEGORYS );
    }

    /**
     * Manages the removal form of a demandcategory whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_DEMANDCATEGORY )
    public String getConfirmRemoveDemandCategory( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DEMANDCATEGORY ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_DEMANDCATEGORY ) );
        url.addParameter( PARAMETER_ID_DEMANDCATEGORY, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_DEMANDCATEGORY, url.getUrl( ),
                AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a demandcategory
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage demandcategorys
     */
    @Action( ACTION_REMOVE_DEMANDCATEGORY )
    public String doRemoveDemandCategory( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DEMANDCATEGORY ) );

        DemandCategoryHome.remove( nId );
        addInfo( INFO_DEMANDCATEGORY_REMOVED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_DEMANDCATEGORYS );
    }

    /**
     * Returns the form to update info about a demandcategory
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_DEMANDCATEGORY )
    public String getModifyDemandCategory( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DEMANDCATEGORY ) );

        if ( _demandcategory == null || ( _demandcategory.getId( ) != nId ) )
        {
            Optional<DemandCategory> optDemandCategory = DemandCategoryHome.findByPrimaryKey( nId );
            _demandcategory = optDemandCategory.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_DEMANDCATEGORY, _demandcategory );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_DEMANDCATEGORY ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_DEMANDCATEGORY, TEMPLATE_MODIFY_DEMANDCATEGORY, model );
    }

    /**
     * Process the change form of a demandcategory
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_DEMANDCATEGORY )
    public String doModifyDemandCategory( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _demandcategory, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_DEMANDCATEGORY ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _demandcategory, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_DEMANDCATEGORY, PARAMETER_ID_DEMANDCATEGORY, _demandcategory.getId( ) );
        }

        DemandCategoryHome.update( _demandcategory );
        addInfo( INFO_DEMANDCATEGORY_UPDATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_DEMANDCATEGORYS );
    }
}
