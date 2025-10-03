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
package fr.paris.lutece.plugins.notificationstore.web;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.grubusiness.business.demand.TemporaryStatus;
import fr.paris.lutece.plugins.grubusiness.business.web.rs.EnumGenericStatus;
import fr.paris.lutece.plugins.notificationstore.service.TemporaryStatusService;
import fr.paris.lutece.plugins.notificationstore.utils.NotificationStoreUtils;
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
 * This class provides the user interface to manage Status features ( manage, create, modify, remove )
 */
@Controller( controllerJsp = "ManageStatus.jsp", controllerPath = "jsp/admin/plugins/notificationstore/", right = "NOTIFICATIONSTORE_MANAGEMENT" )
public class StatusJspBean extends AbstractJspBean<Integer, TemporaryStatus>
{
    /**
     * 
     */
    private static final long serialVersionUID = 2030181750801572844L;
    // Templates
    private static final String TEMPLATE_MANAGE_STATUS = "/admin/plugins/notificationstore/status/manage_status.html";
    private static final String TEMPLATE_CREATE_STATUS = "/admin/plugins/notificationstore/status/create_status.html";
    private static final String TEMPLATE_MODIFY_STATUS = "/admin/plugins/notificationstore/status/modify_status.html";

    // Parameters
    private static final String PARAMETER_ID_STATUS = "id";
    private static final String PARAMETER_GENERIC_STATUS = "genstatus";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_STATUS = "notificationstore.manage_status.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_STATUS = "notificationstore.modify_status.pageTitle";
    private static final String PROPERTY_PAGE_TITLE_CREATE_STATUS = "notificationstore.create_status.pageTitle";

    // Markers

    private static final String MARK_STATUS = "status";
    private static final String MARK_GENERIC_STATUS_LIST = "generic_status_list";

    private static final String JSP_MANAGE_STATUS = "jsp/admin/plugins/notificationstore/ManageStatus.jsp";

    // Properties
    private static final String MESSAGE_CONFIRM_REMOVE_STATUS = "notificationstore.message.confirmRemoveStatus";

    // Validations
    private static final String VALIDATION_ATTRIBUTES_PREFIX = "notificationstore.model.entity.status.attribute.";

    // Views
    private static final String VIEW_MANAGE_STATUS = "manageStatus";
    private static final String VIEW_CREATE_STATUS = "createStatus";
    private static final String VIEW_MODIFY_STATUS = "modifyStatus";

    // Actions
    private static final String ACTION_CREATE_STATUS = "createStatus";
    private static final String ACTION_MODIFY_STATUS = "modifyStatus";
    private static final String ACTION_REMOVE_STATUS = "removeStatus";
    private static final String ACTION_CONFIRM_REMOVE_STATUS = "confirmRemoveStatus";

    // Infos
    private static final String INFO_STATUS_CREATED = "notificationstore.info.status.created";
    private static final String INFO_STATUS_UPDATED = "notificationstore.info.status.updated";
    private static final String INFO_STATUS_REMOVED = "notificationstore.info.status.removed";

    // Errors
    private static final String ERROR_RESOURCE_NOT_FOUND = "Resource not found";

    // Session variable to store working values
    private TemporaryStatus _status;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_STATUS, defaultView = true )
    public String getManageStatus( HttpServletRequest request )
    {
        _status = null;

        fillModelWithSearchParamsAndResult( request, JSP_MANAGE_STATUS );

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_STATUS, TEMPLATE_MANAGE_STATUS, _model );
    }

    /**
     * Get Items from Ids list
     * 
     * @param listIds
     * @return the populated list of items corresponding to the id List
     */
    @Override
    List<TemporaryStatus> getItemsFromIds( List<Integer> listIds )
    {
        List<TemporaryStatus> listStatus = TemporaryStatusService.getInstance( ).getStatusListByIds( listIds );

        // keep original order
        return listStatus.stream( ).sorted( Comparator.comparingInt( notif -> listIds.indexOf( notif.getId( ) ) ) ).collect( Collectors.toList( ) );
    }

    /**
     * Returns the form to create a status
     *
     * @param request
     *            The Http request
     * @return the html code of the status form
     */
    @View( VIEW_CREATE_STATUS )
    public String getCreateStatus( HttpServletRequest request )
    {
        _status = ( _status != null ) ? _status : new TemporaryStatus( );

        Map<String, Object> model = getModel( );
        model.put( MARK_STATUS, _status );
        model.put( MARK_GENERIC_STATUS_LIST, NotificationStoreUtils.getEnumGenericStatusRefList( ) );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_CREATE_STATUS ) );

        return getPage( PROPERTY_PAGE_TITLE_CREATE_STATUS, TEMPLATE_CREATE_STATUS, model );
    }

    /**
     * Process the data capture form of a new status
     *
     * @param request
     *            The Http Request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_CREATE_STATUS )
    public String doCreateStatus( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _status, request, getLocale( ) );

        populateGenericStatus( request );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_CREATE_STATUS ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _status, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirectView( request, VIEW_CREATE_STATUS );
        }

        TemporaryStatusService.getInstance( ).create( _status );
        addInfo( INFO_STATUS_CREATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_STATUS );
    }

    /**
     * Manages the removal form of a status whose identifier is in the http request
     *
     * @param request
     *            The Http request
     * @return the html code to confirm
     */
    @Action( ACTION_CONFIRM_REMOVE_STATUS )
    public String getConfirmRemoveStatus( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_STATUS ) );
        UrlItem url = new UrlItem( getActionUrl( ACTION_REMOVE_STATUS ) );
        url.addParameter( PARAMETER_ID_STATUS, nId );

        String strMessageUrl = AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_STATUS, url.getUrl( ), AdminMessage.TYPE_CONFIRMATION );

        return redirect( request, strMessageUrl );
    }

    /**
     * Handles the removal form of a status
     *
     * @param request
     *            The Http request
     * @return the jsp URL to display the form to manage status
     */
    @Action( ACTION_REMOVE_STATUS )
    public String doRemoveStatus( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_STATUS ) );

        TemporaryStatusService.getInstance( ).remove( nId );
        addInfo( INFO_STATUS_REMOVED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_STATUS );
    }

    /**
     * Returns the form to update info about a status
     *
     * @param request
     *            The Http request
     * @return The HTML form to update info
     */
    @View( VIEW_MODIFY_STATUS )
    public String getModifyStatus( HttpServletRequest request )
    {
        int nId = Integer.parseInt( request.getParameter( PARAMETER_ID_STATUS ) );

        if ( _status == null || ( _status.getId( ) != nId ) )
        {
            Optional<TemporaryStatus> optStatus = TemporaryStatusService.getInstance( ).findByPrimaryKey( nId );
            _status = optStatus.orElseThrow( ( ) -> new AppException( ERROR_RESOURCE_NOT_FOUND ) );
        }

        Map<String, Object> model = getModel( );
        model.put( MARK_STATUS, _status );
        model.put( MARK_GENERIC_STATUS_LIST, NotificationStoreUtils.getEnumGenericStatusRefList( ) );
        model.put( SecurityTokenService.MARK_TOKEN, SecurityTokenService.getInstance( ).getToken( request, ACTION_MODIFY_STATUS ) );

        return getPage( PROPERTY_PAGE_TITLE_MODIFY_STATUS, TEMPLATE_MODIFY_STATUS, model );
    }

    /**
     * Process the change form of a status
     *
     * @param request
     *            The Http request
     * @return The Jsp URL of the process result
     * @throws AccessDeniedException
     */
    @Action( ACTION_MODIFY_STATUS )
    public String doModifyStatus( HttpServletRequest request ) throws AccessDeniedException
    {
        populate( _status, request, getLocale( ) );

        populateGenericStatus( request );

        if ( !SecurityTokenService.getInstance( ).validate( request, ACTION_MODIFY_STATUS ) )
        {
            throw new AccessDeniedException( "Invalid security token" );
        }

        // Check constraints
        if ( !validateBean( _status, VALIDATION_ATTRIBUTES_PREFIX ) )
        {
            return redirect( request, VIEW_MODIFY_STATUS, PARAMETER_ID_STATUS, _status.getId( ) );
        }

        TemporaryStatusService.getInstance( ).update( _status );
        addInfo( INFO_STATUS_UPDATED, getLocale( ) );
        resetListId( );

        return redirectView( request, VIEW_MANAGE_STATUS );
    }

    /**
     * Populate generic status
     * 
     * @param request
     */
    private void populateGenericStatus( HttpServletRequest request )
    {
        String strGenericStatus = request.getParameter( PARAMETER_GENERIC_STATUS );

        if ( StringUtils.isNotEmpty( strGenericStatus ) )
        {
            _status.setGenericStatus( EnumGenericStatus.valueOf( strGenericStatus ) );
        }
    }

    /**
     * getItemIdsList
     */
    @Override
    protected List<Integer> getItemIdsList( HashMap<String, String> _mapFilterCriteria, String strOrderByColumn, String strSortMode )
    {
        return TemporaryStatusService.getInstance( ).searchStatusIdsList( _mapFilterCriteria, strOrderByColumn, strSortMode );
    }

}
