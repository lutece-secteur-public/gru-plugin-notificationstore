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
package fr.paris.lutece.plugins.notificationstore.business;

import fr.paris.lutece.plugins.grubusiness.business.customer.Customer;
import fr.paris.lutece.plugins.grubusiness.business.demand.Demand;
import fr.paris.lutece.plugins.grubusiness.business.demand.IDemandDAO;
import fr.paris.lutece.plugins.grubusiness.business.notification.NotificationFilter;
import fr.paris.lutece.plugins.notificationstore.service.NotificationStorePlugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * This class provides Data Access methods for Demand objects stored in SQL database
 */
public final class DemandDAO implements IDemandDAO
{
    // Columns
    private static final String COLUMN_UID = "uid";
    private static final String COLUMN_DEMAND_ID = "id";
    private static final String COLUMN_TYPE_ID = "demand_type_id";
    private static final String COLUMN_SUBTYPE_ID = "subtype_id";
    private static final String COLUMN_REFERENCE = "reference";
    private static final String COLUMN_STATUS_ID = "status_id";
    private static final String COLUMN_CUSTOMER_ID = "customer_id";
    private static final String COLUMN_CREATION_DATE = "creation_date";
    private static final String COLUMN_CLOSURE_DATE = "closure_date";
    private static final String COLUMN_MAX_STEPS = "max_steps";
    private static final String COLUMN_CURRENT_STEP = "current_step";
    private static final String COLUMN_MODIFY_DATE = "modify_date";
    // SQL queries
    private static final String SQL_QUERY_DEMAND_ALL_FIELDS = " uid, id, demand_type_id, subtype_id, reference, status_id, customer_id, creation_date, closure_date, max_steps, current_step, modify_date";
    private static final String SQL_QUERY_DEMAND_ALL_FIELDS_WITH_NO_DEMAND_ID = " id, demand_type_id, subtype_id, reference, status_id, customer_id, creation_date, closure_date, max_steps, current_step, modify_date";
    private static final String SQL_QUERY_DEMAND_SELECT_BY_ID = "SELECT " + SQL_QUERY_DEMAND_ALL_FIELDS
            + " FROM notificationstore_demand WHERE uid = ? ";
    private static final String SQL_QUERY_DEMAND_SELECT_BY_DEMAND_ID = "SELECT " + SQL_QUERY_DEMAND_ALL_FIELDS
            + " FROM notificationstore_demand WHERE id = ? ";
    private static final String SQL_QUERY_DEMAND_SELECT_BY_DEMAND_ID_AND_TYPE_ID = "SELECT " + SQL_QUERY_DEMAND_ALL_FIELDS
            + " FROM notificationstore_demand WHERE id = ? AND  demand_type_id = ? ";
    private static final String SQL_QUERY_DEMAND_SELECT_ALL = "SELECT " + SQL_QUERY_DEMAND_ALL_FIELDS + " FROM notificationstore_demand";
    private static final String SQL_QUERY_DEMAND_SELECT_DEMAND_IDS = "SELECT uid FROM notificationstore_demand ";
    private static final String SQL_QUERY_DEMAND_SELECT_BY_IDS = SQL_QUERY_DEMAND_SELECT_ALL + " where uid in ( %s )";
    private static final String SQL_QUERY_DEMAND_INSERT = "INSERT INTO notificationstore_demand ( " + SQL_QUERY_DEMAND_ALL_FIELDS_WITH_NO_DEMAND_ID
            + " ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DEMAND_UPDATE = "UPDATE notificationstore_demand SET status_id = ?, customer_id = ?, closure_date = ?, current_step = ?, subtype_id = ?, modify_date = ? WHERE uid = ? AND demand_type_id = ?";
    private static final String SQL_QUERY_DEMAND_DELETE = "DELETE FROM notificationstore_demand WHERE id = ? AND demand_type_id = ? ";
    private static final String SQL_QUERY_DEMAND_DELETE_BY_UID = "DELETE FROM notificationstore_demand WHERE uid = ? ";
    private static final String SQL_QUERY_DEMAND_SELECT_BY_CUSTOMER_ID = "SELECT " + SQL_QUERY_DEMAND_ALL_FIELDS
            + " FROM notificationstore_demand WHERE customer_id = ?";
    private static final String SQL_QUERY_DEMAND_SELECT_BY_REFERENCE = "SELECT " + SQL_QUERY_DEMAND_ALL_FIELDS
            + " FROM notificationstore_demand WHERE reference = ?";

    private static final String SQL_QUERY_IDS_BY_CUSTOMER_ID_AND_DEMANDTYPE_ID = "SELECT distinct(gd.uid) "
            + " FROM notificationstore_demand gd, notificationstore_notification gn, notificationstore_notification_content gc "
            + " WHERE gd.id = gn.demand_id and gn.id = gc.notification_id " + " AND gd.customer_id = ? ";

    private static final String SQL_QUERY_IDS_BY_STATUS = "SELECT distinct(gd.uid) "
            + " FROM notificationstore_demand gd, notificationstore_notification gn, notificationstore_notification_content gc "
            + " WHERE gd.id = gn.demand_id and gn.id = gc.notification_id " + " AND gd.customer_id = ? " + " AND gc.status_id IN ( ";

    private static final String SQL_QUERY_FILTER_WHERE_BASE = " WHERE 1 ";
    private static final String SQL_FILTER_BY_DEMAND_ID = " AND id = ? ";
    private static final String SQL_FILTER_BY_DEMAND_TYPE_ID = " AND demand_type_id = ? ";
    private static final String SQL_FILTER_BY_START_DATE = " AND creation_date >= ? ";
    private static final String SQL_FILTER_BY_END_DATE = " AND creation_date <= ? ";
    private static final String SQL_FILTER_NOTIFICATION_TYPE = " AND gc.notification_type = ? ";
    private static final String SQL_QUERY_FILTER_ORDER = " ORDER BY uid ASC";
    private static final String SQL_QUERY_DATE_ORDER = " ORDER BY modify_date DESC";

    /**
     * {@inheritDoc}
     */
    @Override
    public Demand load( int nId )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DEMAND_SELECT_BY_ID, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nId );
            daoUtil.executeQuery( );
    
            Demand demand = null;
    
            if ( daoUtil.next( ) )
            {
                demand = dao2Demand( daoUtil );
            }
        
            return demand;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Demand> loadByCustomerId( String strCustomerId )
    {
        Collection<Demand> collectionDemands = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DEMAND_SELECT_BY_CUSTOMER_ID, NotificationStorePlugin.getPlugin( ) ) )
        {

            daoUtil.setString( 1, strCustomerId );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                collectionDemands.add( dao2Demand( daoUtil ) );
            }

        }

        return collectionDemands;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Demand> loadByIds( List<Integer> listIds )
    {
        List<Demand> listDemands = new ArrayList<>( );

        if ( listIds == null || listIds.isEmpty( ) )
            return listDemands;

        String sql = String.format( SQL_QUERY_DEMAND_SELECT_BY_IDS, listIds.stream( ).map( v -> "?" ).collect( Collectors.joining( ", " ) ) );

        try ( DAOUtil daoUtil = new DAOUtil( sql, NotificationStorePlugin.getPlugin( ) ) )
        {

            int index = 1;
            for ( Integer strId : listIds )
            {
                daoUtil.setInt( index++, strId );
            }

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                listDemands.add( dao2Demand( daoUtil ) );
            }

        }

        return listDemands;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Demand> loadByReference( String strReference )
    {
        Collection<Demand> collectionDemands = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DEMAND_SELECT_BY_REFERENCE, NotificationStorePlugin.getPlugin( ) ) )
        {

            daoUtil.setString( 1, strReference );
            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                collectionDemands.add( dao2Demand( daoUtil ) );
            }

        }

        return collectionDemands;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Demand> loadByFilter( NotificationFilter filter )
    {
        Collection<Demand> collectionDemands = new ArrayList<>( );
        StringBuilder sql = new StringBuilder( SQL_QUERY_DEMAND_SELECT_ALL + SQL_QUERY_FILTER_WHERE_BASE );

        buildSql( sql, filter );

        try ( DAOUtil daoUtil = new DAOUtil( sql.toString( ), NotificationStorePlugin.getPlugin( ) ) )
        {

            fillDao( daoUtil, filter );

            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                collectionDemands.add( dao2Demand( daoUtil ) );
            }

        }

        return collectionDemands;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> loadIdsByFilter( NotificationFilter filter )
    {
        List<Integer> listIds = new ArrayList<>( );
        StringBuilder sql = new StringBuilder( SQL_QUERY_DEMAND_SELECT_DEMAND_IDS + SQL_QUERY_FILTER_WHERE_BASE );

        buildSql( sql, filter );

        try ( DAOUtil daoUtil = new DAOUtil( sql.toString( ), NotificationStorePlugin.getPlugin( ) ) )
        {
            fillDao( daoUtil, filter );

            daoUtil.executeQuery( );
            while ( daoUtil.next( ) )
            {
                listIds.add( daoUtil.getInt( 1 ) );
            }

        }

        return listIds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Demand insert( Demand demand )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DEMAND_INSERT, Statement.RETURN_GENERATED_KEYS, NotificationStorePlugin.getPlugin( ) ) )
        {

            int nIndex = 1;

            daoUtil.setString( nIndex++, demand.getId( ) );
            daoUtil.setString( nIndex++, demand.getTypeId( ) );
            daoUtil.setString( nIndex++, demand.getSubtypeId( ) );
            daoUtil.setString( nIndex++, demand.getReference( ) );
            daoUtil.setInt( nIndex++, demand.getStatusId( ) );
            daoUtil.setString( nIndex++, demand.getCustomer( ).getId( ) );
            daoUtil.setTimestamp( nIndex++, demand.getCreationDate( ) > 0 ? new Timestamp( demand.getCreationDate( ) )  : null );
            daoUtil.setTimestamp( nIndex++, demand.getClosureDate( ) > 0 ? new Timestamp(demand.getClosureDate( ) ) : null );
            daoUtil.setInt( nIndex++, demand.getMaxSteps( ) );
            daoUtil.setInt( nIndex++, demand.getCurrentStep( ) );           
            daoUtil.setTimestamp( nIndex++, demand.getModifyDate( ) > 0 ? new Timestamp( demand.getModifyDate( ) ) : null );

            daoUtil.executeUpdate( );
            if ( daoUtil.nextGeneratedKey( ) )
            {
                demand.setUID( daoUtil.getGeneratedKeyInt( 1 ) );
            }
        }

        return demand;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public Demand store( Demand demand )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DEMAND_UPDATE, NotificationStorePlugin.getPlugin( ) ) )
        {
            int nIndex = 1;
    
            // update
            daoUtil.setInt( nIndex++, demand.getStatusId( ) );
            daoUtil.setString( nIndex++, demand.getCustomer( ).getId( ) );
            daoUtil.setTimestamp( nIndex++, demand.getClosureDate( ) > 0 ? new Timestamp(demand.getClosureDate( ) ) : null );
            daoUtil.setInt( nIndex++, demand.getCurrentStep( ) );
            daoUtil.setString( nIndex++, demand.getSubtypeId( ) );
            daoUtil.setTimestamp( nIndex++, demand.getModifyDate( ) > 0 ? new Timestamp( demand.getModifyDate( ) ) : null );
    
            // where primary_key
            daoUtil.setInt( nIndex++, demand.getUID( ) );
            daoUtil.setString( nIndex++, demand.getTypeId( ) );
    
            daoUtil.executeUpdate( );
    
            return demand;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete( String strDemandId, String strDemandTypeId )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DEMAND_DELETE, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.setString( 1, strDemandId );
            daoUtil.setString( 2, strDemandTypeId );
    
            daoUtil.executeUpdate( );
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByUid( int nUid )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DEMAND_DELETE_BY_UID, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.setInt( 1, nUid );
    
            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> loadAllIds( )
    {
        List<String> collectionIds = new ArrayList<>( );

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DEMAND_SELECT_ALL, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.executeQuery( );
    
            while ( daoUtil.next( ) )
            {
                collectionIds.add( String.valueOf( dao2Demand( daoUtil ).getId( ) ) );
            }
        
            return collectionIds;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Demand loadByDemandId( String strDemandId )
    {
        Demand demand = null;

        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DEMAND_SELECT_BY_DEMAND_ID, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.setString( 1, strDemandId );
            daoUtil.executeQuery( );
    
            while ( daoUtil.next( ) )
            {
                demand = dao2Demand( daoUtil );
                break;
            }
            return demand;
        }
    }
    
    @Override
    public Demand loadByDemandIdAndTypeId( String strDemandId, String strDemandTypeId )
    {
        Demand demand = null;

        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DEMAND_SELECT_BY_DEMAND_ID_AND_TYPE_ID, NotificationStorePlugin.getPlugin( ) ) )
        {
            daoUtil.setString( 1, strDemandId );
            daoUtil.setString( 2, strDemandTypeId );
            daoUtil.executeQuery( );
    
            while ( daoUtil.next( ) )
            {
                demand = dao2Demand( daoUtil );
                break;
            }
            return demand;
        }
    }

    /**
     * Converts data from DAO to a Demand object
     * 
     * @param daoUtil
     *            the DAO containing the data to convert
     * @return a Demand object
     */
    private Demand dao2Demand( DAOUtil daoUtil )
    {
        Demand demand = new Demand( );

        demand.setUID( daoUtil.getInt( COLUMN_UID ) );
        demand.setId( daoUtil.getString( COLUMN_DEMAND_ID ) );
        demand.setTypeId( daoUtil.getString( COLUMN_TYPE_ID ) );
        demand.setSubtypeId( daoUtil.getString( COLUMN_SUBTYPE_ID ) );
        demand.setStatusId( daoUtil.getInt( COLUMN_STATUS_ID ) );

        Customer customer = new Customer( );
        customer.setId( daoUtil.getString( COLUMN_CUSTOMER_ID ) );
        demand.setCustomer( customer );

        demand.setReference( daoUtil.getString( COLUMN_REFERENCE ) );
        demand.setCreationDate( daoUtil.getTimestamp( COLUMN_CREATION_DATE ) != null ? daoUtil.getTimestamp( COLUMN_CREATION_DATE ).getTime( ) : 0 );
        demand.setClosureDate( daoUtil.getTimestamp( COLUMN_CLOSURE_DATE ) != null ? daoUtil.getTimestamp( COLUMN_CLOSURE_DATE ).getTime( ) : 0 );
        demand.setMaxSteps( daoUtil.getInt( COLUMN_MAX_STEPS ) );
        demand.setCurrentStep( daoUtil.getInt( COLUMN_CURRENT_STEP ) );
        demand.setModifyDate( daoUtil.getTimestamp( COLUMN_MODIFY_DATE ) != null ? daoUtil.getTimestamp( COLUMN_MODIFY_DATE ).getTime( ) : 0 );

        return demand;
    }

    /**
     * build the sql with selected filters
     * 
     * @param sql
     * @param filter
     */
    private void buildSql( StringBuilder sql, NotificationFilter filter )
    {

        if ( filter.containsDemandId( ) )
        {
            sql.append( SQL_FILTER_BY_DEMAND_ID );
        }

        if ( filter.containsDemandTypeId( ) )
        {
            sql.append( SQL_FILTER_BY_DEMAND_TYPE_ID );
        }

        if ( filter.containsStartDate( ) )
        {
            sql.append( SQL_FILTER_BY_START_DATE );
        }

        if ( filter.containsEndDate( ) )
        {
            sql.append( SQL_FILTER_BY_END_DATE );
        }

        sql.append( SQL_QUERY_FILTER_ORDER );
    }

    /**
     * fill the dao with filter's values
     * 
     * @param daoUtil
     * @param filter
     */
    private void fillDao( DAOUtil daoUtil, NotificationFilter filter )
    {
        int i = 1;
        if ( filter.containsDemandId( ) )
        {
            daoUtil.setString( i++, filter.getDemandId( ) );
        }

        if ( filter.containsDemandTypeId( ) )
        {
            daoUtil.setString( i++, filter.getDemandTypeId( ) );
        }

        if ( filter.containsStartDate( ) )
        {
            daoUtil.setLong( i++, filter.getStartDate( ) );
        }

        if ( filter.containsEndDate( ) )
        {
            daoUtil.setLong( i++, filter.getEndDate( ) );
        }
    }

    @Override
    public List<Integer> loadIdsByCustomerIdAndIdDemandType( String strCustomerId, String strNotificationType, String strIdDemandType )
    {
        List<Integer> listIds = new ArrayList<>( );
        String strSql = SQL_QUERY_IDS_BY_CUSTOMER_ID_AND_DEMANDTYPE_ID;

        if ( StringUtils.isNotEmpty( strNotificationType ) )
        {
            strSql += SQL_FILTER_NOTIFICATION_TYPE;
        }

        if ( StringUtils.isNotEmpty( strIdDemandType ) )
        {
            strSql += SQL_FILTER_BY_DEMAND_TYPE_ID;
        }

        strSql += SQL_QUERY_DATE_ORDER;

        try ( DAOUtil daoUtil = new DAOUtil( strSql, NotificationStorePlugin.getPlugin( ) ) )
        {
            int nIndex = 1;
            daoUtil.setString( nIndex++, strCustomerId );

            if ( StringUtils.isNotEmpty( strNotificationType ) )
            {
                daoUtil.setString( nIndex++, strNotificationType );
            }

            if ( StringUtils.isNotEmpty( strIdDemandType ) )
            {
                daoUtil.setString( nIndex++, strIdDemandType );
            }

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                listIds.add( daoUtil.getInt( 1 ) );
            }

            return listIds;
        }
    }

    @Override
    public List<Integer> loadIdsByStatus( String strCustomerId, List<String> listStatus, String strNotificationType, String strIdDemandType )
    {
        List<Integer> listIds = new ArrayList<>( );
        String strQuery = SQL_QUERY_IDS_BY_STATUS;

        if ( !listStatus.isEmpty( ) )
        {
            strQuery += listStatus.stream( ).map( i -> "?" ).collect( Collectors.joining( "," ) ) + " ) ";
        }

        if ( StringUtils.isNotEmpty( strNotificationType ) )
        {
            strQuery += SQL_FILTER_NOTIFICATION_TYPE;
        }
        if ( StringUtils.isNotEmpty( strIdDemandType ) )
        {
            strQuery += SQL_FILTER_BY_DEMAND_TYPE_ID;
        }

        strQuery += SQL_QUERY_DATE_ORDER;

        try ( DAOUtil daoUtil = new DAOUtil( strQuery, NotificationStorePlugin.getPlugin( ) ) )
        {
            int nIndexIn = 1;
            daoUtil.setString( nIndexIn++, strCustomerId );

            for ( String strStatus : listStatus )
            {
                daoUtil.setString( nIndexIn, strStatus );
                nIndexIn++;
            }
            if ( StringUtils.isNotEmpty( strNotificationType ) )
            {
                daoUtil.setString( nIndexIn++, strNotificationType );
            }
            if ( StringUtils.isNotEmpty( strIdDemandType ) )
            {
                daoUtil.setString( nIndexIn++, strIdDemandType );
            }

            daoUtil.executeQuery( );

            while ( daoUtil.next( ) )
            {
                listIds.add( daoUtil.getInt( 1 ) );
            }

            return listIds;
        }
    }

}
