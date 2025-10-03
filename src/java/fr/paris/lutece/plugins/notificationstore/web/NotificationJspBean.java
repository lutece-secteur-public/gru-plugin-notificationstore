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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.grubusiness.business.notification.Notification;
import fr.paris.lutece.plugins.grubusiness.business.notification.NotificationFilter;
import fr.paris.lutece.plugins.notificationstore.business.DemandTypeHome;
import fr.paris.lutece.plugins.notificationstore.business.NotificationHome;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.html.AbstractPaginator;

@Controller( controllerJsp = "ManageNotification.jsp", controllerPath = "jsp/admin/plugins/notificationstore/", right = "DEMAND_MANAGEMENT" )
public class NotificationJspBean extends AbstractManageDemandJspBean<Integer, Notification>
{

    private static final long serialVersionUID = 1L;

    // Templates
    private static final String TEMPLATE_MANAGE_NOTIFICATION = "/admin/plugins/notificationstore/manage_notification.html";

    private static final String MARK_NOTIFICATION_LIST = "notification_list";
    private static final String MARK_DEMAND_TYPE_ID_LIST = "demand_type_id_list";
    private static final String MARK_DEMAND_ID = "demand_id";
    private static final String MARK_DEMAND_TYPE_ID = "demand_type_id";
    private static final String MARK_START_DATE = "start_date";
    private static final String MARK_END_DATE = "end_date";

    private static final String JSP_MANAGE_NOTIFICATIONS = "jsp/admin/plugins/notificationstore/ManageNotification.jsp";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_NOTIFICATION = "notificationstore.manage_notification.pageTitle";

    // Views
    private static final String VIEW_MANAGE_NOTIFICATION = "manageNotification";
    private static final String VIEW_COMPRESS_NOTIFICATION = "compressNotificationWithCare";

    // Parameters
    private static final String PARAMETER_DEMAND_ID = "demand_id";
    private static final String PARAMETER_DEMAND_TYPE_ID = "demand_type_id";
    private static final String PARAMETER_NOTIFICATION_DATE = "notification_date";
    private static final String PARAMETER_START_DATE = "start_date";
    private static final String PARAMETER_END_DATE = "end_date";

    // instance variables
    private ReferenceList _listDemandTypeId;
    private List<Integer> _listNotificationId = new ArrayList<>( );
    private NotificationFilter _currentFilter;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_NOTIFICATION, defaultView = true )
    public String getManageNotification( HttpServletRequest request )
    {
        // init demand type Ids for select
        if ( _listDemandTypeId == null )
        {
            _listDemandTypeId = DemandTypeHome.getDemandTypesReferenceList( );
        }

        // initial call (no pagination)
        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null || _listNotificationId.isEmpty( ) )
        {
            // new search...
            _currentFilter = new NotificationFilter( );
            long lNotificationDate = -1;

            if ( !StringUtils.isEmpty( request.getParameter( PARAMETER_DEMAND_ID ) ) )
            {
                _currentFilter.setDemandId( request.getParameter( PARAMETER_DEMAND_ID ) );
            }

            if ( !StringUtils.isEmpty( request.getParameter( PARAMETER_DEMAND_TYPE_ID ) ) )
            {
                _currentFilter.setDemandTypeId( request.getParameter( PARAMETER_DEMAND_TYPE_ID ) );
            }

            if ( !StringUtils.isEmpty( request.getParameter( PARAMETER_NOTIFICATION_DATE ) ) )
            {
                lNotificationDate = Long.parseLong( request.getParameter( PARAMETER_NOTIFICATION_DATE ) );
            }

            if ( !StringUtils.isEmpty( request.getParameter( PARAMETER_START_DATE ) ) )
            {
                String strStartDate = request.getParameter( PARAMETER_START_DATE );
                Date dStartDate = DateUtil.parseIsoDate( strStartDate );
                if ( dStartDate != null )
                {
                    _currentFilter.setStartDate( dStartDate.getTime( ) );
                }
            }

            if ( !StringUtils.isEmpty( request.getParameter( PARAMETER_END_DATE ) ) )
            {
                String strEndDate = request.getParameter( PARAMETER_END_DATE );
                Date dEndDate = DateUtil.parseIsoDate( strEndDate );
                if ( dEndDate != null )
                {
                    _currentFilter.setEndDate( dEndDate.getTime( ) );
                }
            }

            if ( _currentFilter.containsDemandId( ) && _currentFilter.containsDemandTypeId( ) && lNotificationDate > 0 )
            {
                List<Notification> listNotification = NotificationHome.findByNotification( _currentFilter.getDemandId( ), _currentFilter.getDemandTypeId( ),
                        lNotificationDate );

                _listNotificationId = listNotification.stream( ).map( Notification::getId ).collect( Collectors.toList( ) );

            }
            else if ( _currentFilter.containsDemandId( ) || _currentFilter.containsDemandTypeId( ) || _currentFilter.containsStartDate( )
                    || _currentFilter.containsEndDate( ) )
            {
                _listNotificationId = NotificationHome.findIdsByFilter( _currentFilter );
            }
        }

        Map<String, Object> model = getPaginatedListModel( request, MARK_NOTIFICATION_LIST, _listNotificationId, JSP_MANAGE_NOTIFICATIONS );

        model.put( MARK_DEMAND_TYPE_ID_LIST, _listDemandTypeId );

        if ( !StringUtils.isEmpty( _currentFilter.getDemandId( ) ) )
        {
            model.put( MARK_DEMAND_ID, _currentFilter.getDemandId( ) );
        }
        if ( !StringUtils.isBlank( _currentFilter.getDemandTypeId( ) ) )
        {
            model.put( MARK_DEMAND_TYPE_ID, _currentFilter.getDemandTypeId( ) );
        }
        if ( _currentFilter.getStartDate( ) > 0 )
        {
            model.put( MARK_START_DATE, new Date( _currentFilter.getStartDate( ) ) );
        }
        if ( _currentFilter.getEndDate( ) > 0 )
        {
            model.put( MARK_END_DATE, new Date( _currentFilter.getEndDate( ) ) );
        }

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_NOTIFICATION, TEMPLATE_MANAGE_NOTIFICATION, model );
    }

    /**
     * compress data can be used to compress existing data (or decompress) by setting the properties compress=true / decompress=false !!! warning : call it with
     * care !!!
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_COMPRESS_NOTIFICATION )
    public String getCompressNotification( HttpServletRequest request )
    {
        List<Integer> listNotificationId = NotificationHome.findIdsByFilter( _currentFilter );

        for ( Integer id : listNotificationId )
        {
            Optional<Notification> optNotif = NotificationHome.getById( id );
            if ( optNotif.isPresent( ) )
            {
                NotificationHome.remove( optNotif.get( ).getId( ) );
                NotificationHome.create( optNotif.get( ) );
            }
        }

        return getManageNotification( request );
    }

    @Override
    List<Notification> getItemsFromIds( List<Integer> listIds )
    {

        List<Notification> listNotification = NotificationHome.getByIds( listIds );

        // keep original order
        return listNotification.stream( ).sorted( Comparator.comparingInt( notif -> listIds.indexOf( notif.getId( ) ) ) ).collect( Collectors.toList( ) );
    }

}
