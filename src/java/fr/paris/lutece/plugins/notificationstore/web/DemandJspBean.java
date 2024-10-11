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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.grubusiness.business.demand.Demand;
import fr.paris.lutece.plugins.grubusiness.business.notification.NotificationFilter;
import fr.paris.lutece.plugins.notificationstore.business.DemandHome;
import fr.paris.lutece.plugins.notificationstore.business.DemandTypeHome;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.date.DateUtil;
import fr.paris.lutece.util.html.AbstractPaginator;

@Controller( controllerJsp = "ManageDemand.jsp", controllerPath = "jsp/admin/plugins/notificationstore/", right = "DEMAND_MANAGEMENT" )
public class DemandJspBean extends AbstractManageDemandJspBean<Integer, Demand>
{

    private static final long serialVersionUID = 1L;

    // Templates
    private static final String TEMPLATE_MANAGE_DEMAND = "/admin/plugins/notificationstore/manage_demand.html";

    // Markers
    private static final String MARK_DEMAND_LIST = "demand_list";
    private static final String MARK_DEMAND_TYPE_ID_LIST = "demand_type_id_list";
    private static final String MARK_DEMAND_ID = "demand_id";
    private static final String MARK_DEMAND_TYPE_ID = "demand_type_id";
    private static final String MARK_START_DATE = "start_date";
    private static final String MARK_END_DATE = "end_date";

    // JSP
    private static final String JSP_MANAGE_DEMANDS = "jsp/admin/plugins/notificationstore/ManageDemand.jsp";

    // Properties for page titles
    private static final String PROPERTY_PAGE_TITLE_MANAGE_DEMAND = "notificationstore.manage_demand.pageTitle";

    // Views
    private static final String VIEW_MANAGE_DEMAND = "manageDemand";

    // Parameters
    private static final String PARAMETER_DEMAND_ID = "demand_id";
    private static final String PARAMETER_DEMAND_TYPE_ID = "demand_type_id";
    private static final String PARAMETER_START_DATE = "start_date";
    private static final String PARAMETER_END_DATE = "end_date";

    // instance variables
    private ReferenceList _listDemandTypeId;
    private List<Integer> _listDemanId = new ArrayList<>( );
    private NotificationFilter _currentFilter;

    /**
     * Build the Manage View
     * 
     * @param request
     *            The HTTP request
     * @return The page
     */
    @View( value = VIEW_MANAGE_DEMAND, defaultView = true )
    public String getManageDemand( HttpServletRequest request )
    {
        if ( _listDemandTypeId == null )
        {
            _listDemandTypeId = DemandTypeHome.getDemandTypesReferenceList( );
        }

        // initial call (no pagination)
        if ( request.getParameter( AbstractPaginator.PARAMETER_PAGE_INDEX ) == null || _listDemanId.isEmpty( ) )
        {
            _currentFilter = new NotificationFilter( );

            if ( !StringUtils.isEmpty( request.getParameter( PARAMETER_DEMAND_ID ) ) )
            {
                _currentFilter.setDemandId( request.getParameter( PARAMETER_DEMAND_ID ) );
            }

            if ( !StringUtils.isEmpty( request.getParameter( PARAMETER_DEMAND_TYPE_ID ) ) )
            {
                _currentFilter.setDemandTypeId( request.getParameter( PARAMETER_DEMAND_TYPE_ID ) );
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

            if ( _currentFilter.containsDemandId( ) || _currentFilter.containsDemandTypeId( ) || _currentFilter.containsStartDate( )
                    || _currentFilter.containsEndDate( ) )
            {
                // search demands
                _listDemanId = DemandHome.searchIdsByFilter( _currentFilter );
            }
        }

        Map<String, Object> model = getPaginatedListModel( request, MARK_DEMAND_LIST, _listDemanId, JSP_MANAGE_DEMANDS );

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

        return getPage( PROPERTY_PAGE_TITLE_MANAGE_DEMAND, TEMPLATE_MANAGE_DEMAND, model );
    }

    @Override
    List<Demand> getItemsFromIds( List<Integer> listIds )
    {

        List<Demand> listDemand = DemandHome.getByIds( listIds );

        // keep original order
        return listDemand.stream( ).sorted( Comparator.comparingInt( notif -> listIds.indexOf( notif.getId( ) ) ) ).collect( Collectors.toList( ) );

    }
}
