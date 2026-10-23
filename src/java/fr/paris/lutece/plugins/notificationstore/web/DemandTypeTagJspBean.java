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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.grubusiness.business.demand.DemandTypeTag;
import fr.paris.lutece.plugins.notificationstore.business.DemandTypeTagHome;

/**
 * This class provides the user interface to manage DemandTypeTag features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageDemandTypeTags.jsp", controllerPath = "jsp/admin/plugins/notificationstore/", right = "NOTIFICATIONSTORE_DEMANDTYPE_MANAGEMENT" )
public class DemandTypeTagJspBean extends AbstractManageDemandTypeJspBean<Integer, DemandTypeTag>
{
    // Templates
    private static final String TEMPLATE_MANAGE_DEMANDTYPETAGS = "/admin/plugins/notificationstore/demandType/manage_demandtypetags.html";
    private static final String TEMPLATE_CREATE_DEMANDTYPETAG = "/admin/plugins/notificationstore/demandType/create_demandtypetag.html";
    private static final String TEMPLATE_MODIFY_DEMANDTYPETAG = "/admin/plugins/notificationstore/demandType/modify_demandtypetag.html";

    // Parameters
    private static final String PARAMETER_ID_DEMANDTYPETAG = "id";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_DEMANDTYPETAGS = "notificationstore.manage_demandtypetags.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_DEMANDTYPETAG = "notificationstore.modify_demandtypetag.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_DEMANDTYPETAG = "notificationstore.create_demandtypetag.pageTitle";

    // Markers
    private static final String MARK_DEMANDTYPETAG_LIST = "demandtypetag_list";
    private static final String MARK_DEMANDTYPETAG = "demandtypetag";

    private static final String JSP_MANAGE_DEMANDTYPETAGS = "jsp/admin/plugins/notificationstore/ManageDemandTypeTags.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_DEMANDTYPETAG = "notificationstore.message.confirmRemoveDemandTypeTag";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "notificationstore.model.entity.demandtypetag.attribute.";

    // Views
    private static final String VIEW_MANAGE_DEMANDTYPETAGS = "manageDemandTypeTags";
    private static final String VIEW_CREATE_DEMANDTYPETAG = "createDemandTypeTag";
    private static final String VIEW_MODIFY_DEMANDTYPETAG = "modifyDemandTypeTag";

    // Actions
    private static final String ACTION_CREATE_DEMANDTYPETAG = "createDemandTypeTag";
    private static final String ACTION_MODIFY_DEMANDTYPETAG = "modifyDemandTypeTag";
    private static final String ACTION_REMOVE_DEMANDTYPETAG = "removeDemandTypeTag";
    private static final String ACTION_CONFIRM_REMOVE_DEMANDTYPETAG = "confirmRemoveDemandTypeTag";

    // Infos
    private static final String INFO_DEMANDTYPETAG_CREATED = "notificationstore.info.demandtypetag.created";
    private static final String INFO_DEMANDTYPETAG_UPDATED = "notificationstore.info.demandtypetag.updated";
    private static final String INFO_DEMANDTYPETAG_REMOVED = "notificationstore.info.demandtypetag.removed";

    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";
    private static final String ERROR_DEMANDTYPETAG_CODE_ALREADY_EXISTS = "notificationstore.validation.demandtypetag.Code.duplicate";

    // Session variable to store working values
    private DemandTypeTag _demandtypetag;
    private List<Integer> _listIdDemandTypeTags = new ArrayList<>( );

    /**
     * Build the Manage View
     *
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_DEMANDTYPETAGS, defaultView = true )
    public String getManageDemandTypeTags( HttpServletRequest request )
    {
        _demandtypetag = null;

        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null || _listIdDemandTypeTags.isEmpty( ) )
        {
            _listIdDemandTypeTags = DemandTypeTagHome.getIdDemandTypeTagsList( );
        }

        Map<String, Object> model = getPaginatedListModel( request, MARK_DEMANDTYPETAG_LIST, _listIdDemandTypeTags, JSP_MANAGE_DEMANDTYPETAGS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_DEMANDTYPETAGS, TEMPLATE_MANAGE_DEMANDTYPETAGS, model );
    }

    /**
     * Get Items from Ids list
     *
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
    @Override
    List<DemandTypeTag> getItemsFromIds(List<Integer> listIds )
    {
        List<DemandTypeTag> listDemandTypeTag = DemandTypeTagHome.getDemandTypeTagsListByIds( listIds );

        // keep original order
        return listDemandTypeTag.stream( ).sorted( Comparator.comparingInt(item -> listIds.indexOf( item.getId( ) ) ) ).collect( Collectors.toList( ) );
    }

    /**
     * reset the _listIdDemandTypeTags list
     */
    public void resetListId( )
    {
        _listIdDemandTypeTags = new ArrayList<>( );
    }

    /**
     * Returns the form to create a demandTypeTag
     *
     * @param request
     *            The Http request
     * @return the html code of the demandTypeTag form
     */
    @View( VIEW_CREATE_DEMANDTYPETAG )
    public String getCreateDemandTypeTag( HttpServletRequest request )
    {
        _demandtypetag = ( _demandtypetag != null ) ? _demandtypetag : new DemandTypeTag( );

        Map<String, Object> model = getModel( );
        model.put( MARK_DEMANDTYPETAG, _demandtypetag );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_DEMANDTYPETAG ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_DEMANDTYPETAG, TEMPLATE_CREATE_DEMANDTYPETAG, model );
    }

    /**
     * Process the data capture form of a new demandTypeTag
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_DEMANDTYPETAG )
    public String doCreateDemandTypeTag( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _demandtypetag, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_DEMANDTYPETAG ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _demandtypetag, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_DEMANDTYPETAG );
        }

        // Check the code is unique
        if ( DemandTypeTagHome.getDemandTypeTagByCode( _demandtypetag.getCode( ) ).isPresent( ) )
        {
            addError( ERROR_DEMANDTYPETAG_CODE_ALREADY_EXISTS, getLocale( ) );
            return redirectView( request, VIEW_CREATE_DEMANDTYPETAG );
        }

        DemandTypeTagHome.create( _demandtypetag );
        addInfo( INFO_DEMANDTYPETAG_CREATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_DEMANDTYPETAGS );
    }

    /**
     * Manages the removal form of a demandTypeTag whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_DEMANDTYPETAG )
    public String getConfirmRemoveDemandTypeTag( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DEMANDTYPETAG ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_DEMANDTYPETAG ) );
        url.addParameter( PARAMETER_ID_DEMANDTYPETAG, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_DEMANDTYPETAG, url.getUrl( ),
                AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a demandTypeTag
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage demandTypeTags
     */
    @Action( ACTION_REMOVE_DEMANDTYPETAG )
    public String doRemoveDemandTypeTag( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DEMANDTYPETAG ) );

        DemandTypeTagHome.remove( nId );
        addInfo( INFO_DEMANDTYPETAG_REMOVED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_DEMANDTYPETAGS );
    }

    /**
     * Returns the form to update info about a demandTypeTag
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_DEMANDTYPETAG )
    public String getModifyDemandTypeTag( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_DEMANDTYPETAG ) );

        if ( _demandtypetag == null || ( _demandtypetag.getId( ) != nId ) )
        {
            Optional<DemandTypeTag> optDemandTypeTag = DemandTypeTagHome.findByPrimaryKey( nId );
            _demandtypetag = optDemandTypeTag.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_DEMANDTYPETAG, _demandtypetag );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_DEMANDTYPETAG ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_DEMANDTYPETAG, TEMPLATE_MODIFY_DEMANDTYPETAG, model );
    }

    /**
     * Process the change form of a demandTypeTag
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_DEMANDTYPETAG )
    public String doModifyDemandTypeTag( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _demandtypetag, request, getLocale( ) );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_DEMANDTYPETAG ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _demandtypetag, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_DEMANDTYPETAG, PARAMETER_ID_DEMANDTYPETAG, _demandtypetag.getId( ) );
        }

        // Check the code is unique (excluding the current record)
        Optional<DemandTypeTag> optExistingTag = DemandTypeTagHome.getDemandTypeTagByCode( _demandtypetag.getCode( ) );
        if ( optExistingTag.isPresent( ) && optExistingTag.get( ).getId( ) != _demandtypetag.getId( ) )
        {
            addError( ERROR_DEMANDTYPETAG_CODE_ALREADY_EXISTS, getLocale( ) );
            return redirect( request, VIEW_MODIFY_DEMANDTYPETAG, PARAMETER_ID_DEMANDTYPETAG, _demandtypetag.getId( ) );
        }

        DemandTypeTagHome.update( _demandtypetag );
        addInfo( INFO_DEMANDTYPETAG_UPDATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_DEMANDTYPETAGS );
    }
}
