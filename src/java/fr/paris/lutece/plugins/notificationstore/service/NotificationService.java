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
package fr.paris.lutece.plugins.notificationstore.service;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import fr.paris.lutece.plugins.grubusiness.business.notification.ReassignNotificationsRequest;
import fr.paris.lutece.plugins.notificationstore.business.DemandHome;
import fr.paris.lutece.plugins.notificationstore.business.NotificationHome;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.paris.lutece.plugins.grubusiness.business.customer.Customer;
import fr.paris.lutece.plugins.grubusiness.business.demand.Demand;
import fr.paris.lutece.plugins.grubusiness.business.demand.IDemandServiceProvider;
import fr.paris.lutece.plugins.grubusiness.business.demand.TemporaryStatus;
import fr.paris.lutece.plugins.grubusiness.business.notification.Event;
import fr.paris.lutece.plugins.grubusiness.business.notification.Notification;
import fr.paris.lutece.plugins.grubusiness.business.notification.NotificationEvent;
import fr.paris.lutece.plugins.grubusiness.business.notification.NotificationFilter;
import fr.paris.lutece.plugins.grubusiness.business.notification.StatusMessage;
import fr.paris.lutece.plugins.grubusiness.business.web.rs.EnumGenericStatus;
import fr.paris.lutece.plugins.grubusiness.service.notification.INotifierServiceProvider;
import fr.paris.lutece.plugins.grubusiness.service.notification.NotificationException;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import fr.paris.lutece.plugins.notificationstore.utils.NotificationStoreConstants;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

public class NotificationService
{

    // Bean names
    private static final String BEAN_STORAGE_SERVICE = "notificationstore.demandService";

    // Other constants
    private static final String RESPONSE_OK = "{ \"acknowledge\" : { \"status\": \"received\" } }";

    private static final String TYPE_DEMAND = "DEMAND";
    private static final String TYPE_NOTIFICATION = "NOTIFICATION";
    private static final String STATUS_WARNING = "WARNING";
    private static final String STATUS_ERROR = "ERROR";
    private static final String STATUS_FAILED = "FAILED";
    private static final String STATUS_SUCCESS = "SUCCESS";

    private static final String TYPE_NOTIFICATION_GUICHET = "GUICHET";
    private static final String TYPE_NOTIFICATION_AGENT = "AGENT";
    private static final String TYPE_MERGE_NOTIFICATIONS = "MERGE";

    // Messages
    private static final String WARNING_DEMAND_ID_MANDATORY = "Notification Demand_id field is mandatory";
    private static final String WARNING_DEMAND_TYPE_ID_MANDATORY = "Notification Demand_type_id field is mandatory";
    private static final String WARNING_CUSTOMER_IDS_MANDATORY = "Valid user connection id or customer id is mandatory";
    private static final String WARNING_CUSTOMER_NOT_FOUND = "User not found in the identityStore";
    private static final String ERROR_IDENTITYSTORE = "An error occured while retrieving user from identityStore";
    private static final String MESSAGE_MISSING_MANDATORY_FIELD = "Missing value";
    private static final String MESSAGE_MISSING_DEMAND_ID = "Demand Id and Demand type Id are mandatory";
    private static final String MESSAGE_MISSING_USER_ID = "Valid user connection id or customer id is mandatory";
    private static final String MESSAGE_INCORRECT_USER = "Incorrect User Ids";
    private static final String WARNING_INCORRECT_DEMAND_TYPE_ID = "Demand Type Id not found";

    private static final String PROPERTY_STORE_EVEN_CUSTOMER_ID_NOT_EXISTS = "notificationstore.notification.store.storeEventCustomerIdDoesNotExists";

    // instance variables
    private static IDemandServiceProvider _demandService;
    private static NotificationService _instance;
    private static List<INotifierServiceProvider> _notifiers;

    private static ObjectMapper _mapper = new ObjectMapper( );

    /**
     * private constructor
     */
    private NotificationService( )
    {
    }

    /**
     * get unique instance of the service
     * 
     * @return the notification service
     */
    public static NotificationService instance( )
    {
        if ( _instance == null )
        {
            _instance = new NotificationService( );
            _demandService = SpringContextService.getBean( BEAN_STORAGE_SERVICE );
            _notifiers = SpringContextService.getBeansOfType( INotifierServiceProvider.class );

            _mapper.configure( DeserializationFeature.UNWRAP_ROOT_VALUE, true );
            _mapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );
        }

        return _instance;
    }

    /**
     * process Notification
     * 
     * @param notification
     */
    public Response newNotification( String strJson )
    {
        List<StatusMessage> warnings = new ArrayList<>( );

        try
        {
            // parse json
            Notification notification = getNotificationFromJson( strJson );

            // control customer
            boolean customerExists = processCustomer( notification, warnings );

            // check Notification
            checkNotification( notification, warnings );

            // store notification only if :
            //  * customer_id is not empty
            //  * AND ( customer exists OR notification can be store even if not exists ) 
            boolean storeEvenCustomerIfNotExists  = AppPropertiesService.getPropertyBoolean( PROPERTY_STORE_EVEN_CUSTOMER_ID_NOT_EXISTS, false );
            boolean customerIdNotEmpty = notification.getDemand( ).getCustomer( ) != null && !StringUtils.isEmpty( notification.getDemand( ).getCustomer( ).getCustomerId( ) ) ;
            
            if ( customerIdNotEmpty && ( customerExists || storeEvenCustomerIfNotExists ) )
            {
                store( notification );
            }

            // add event (success, or failure if customer not found for example)
            addEvents( notification, warnings );

            // forward notification to registred notifiers (if exists)
            forward( notification );

        }
        catch( JsonParseException ex )
        {
            return fail( ex, Response.Status.BAD_REQUEST );
        }
        catch( JsonMappingException | NullPointerException | NotificationException ex )
        {
            return fail( ex, Response.Status.BAD_REQUEST );
        }
        catch( IOException ex )
        {
            return fail( ex, Response.Status.INTERNAL_SERVER_ERROR );
        }
        catch( IdentityStoreException ex )
        {
            return fail( ex, Response.Status.INTERNAL_SERVER_ERROR );
        }
        catch( Exception ex )
        {
            return fail( ex, Response.Status.INTERNAL_SERVER_ERROR );
        }

        // success
        if ( warnings.isEmpty( ) )
        {
            return success( );
        }
        else
        {
            return successWithWarnings( warnings );
        }
    }

    /**
     * Notification check
     * 
     * @param notification
     * @param warnings
     */
    private void checkNotification( Notification notification, List<StatusMessage> warnings )
    {
        // notification should be associated to a demand id
        if ( StringUtils.isBlank( notification.getDemand( ).getId( ) ) )
        {
            StatusMessage msg = new StatusMessage( TYPE_DEMAND, STATUS_WARNING, MESSAGE_MISSING_MANDATORY_FIELD, WARNING_DEMAND_ID_MANDATORY );
            warnings.add( msg );
        }

        // notification should be associated to a demand type id
        if ( StringUtils.isBlank( notification.getDemand( ).getTypeId( ) ) )
        {
            StatusMessage msg = new StatusMessage( TYPE_DEMAND, STATUS_WARNING, MESSAGE_MISSING_MANDATORY_FIELD, WARNING_DEMAND_TYPE_ID_MANDATORY );
            warnings.add( msg );
        }

        // check if demand type id exists
        if ( !_demandService.getDemandType( notification.getDemand( ).getTypeId( ) ).isPresent( ) )
        {
            StatusMessage msg = new StatusMessage( TYPE_DEMAND, STATUS_WARNING, MESSAGE_MISSING_DEMAND_ID, WARNING_INCORRECT_DEMAND_TYPE_ID );
            warnings.add( msg );
        }
    }

    /**
     * Process customer data
     * 
     * @param notification
     * @param warnings
     * @throws IdentityStoreException
     */
    private boolean processCustomer( Notification notification, List<StatusMessage> warnings ) throws IdentityStoreException
    {

        Customer customer = CustomerProvider.instance( ).decrypt( notification.getDemand( ) );

        // use connection id as customer id (if set in properties, and the other ids are empty)
        if ( AppPropertiesService.getPropertyBoolean( NotificationStoreConstants.PROPERTY_CONSIDER_GUID_AS_CUSTOMER_ID, false )
                && StringUtils.isEmpty( customer.getId( ) ) && StringUtils.isEmpty( customer.getCustomerId( ) )
                && !StringUtils.isEmpty( customer.getConnectionId( ) ) )
        {
            customer.setCustomerId( customer.getConnectionId( ) );
        }

        // check customer identity
        if ( CustomerProvider.instance( ).hasIdentityService( ) )
        {
            // check customer ids
            if ( customer == null || ( !CustomerProvider.isCustomerIdValid( customer.getCustomerId( ) )
                    && !CustomerProvider.isCustomerIdValid( customer.getId( ) ) && !CustomerProvider.isConnectionIdValid( customer.getConnectionId( ) ) ) )
            {
                StatusMessage msg = new StatusMessage( TYPE_DEMAND, STATUS_WARNING, MESSAGE_INCORRECT_USER, WARNING_CUSTOMER_IDS_MANDATORY );
                warnings.add( msg );
                return false;
            }

            // set customer id if provided by ID customer attribute (if valid)
            if ( !CustomerProvider.isCustomerIdValid( customer.getCustomerId( ) ) && CustomerProvider.isCustomerIdValid( customer.getId( ) ) )
            {
                customer.setCustomerId( customer.getId( ) );
            }

            try
            {
                // search identity
                Customer customerResult = CustomerProvider.instance( ).get( customer.getConnectionId( ), customer.getCustomerId( ) );
                if ( customerResult != null )
                {
                    // could be different (in case of consolidated identities for example)
                    customer.setCustomerId( customerResult.getCustomerId( ) );
                }
                else
                {
                    // add a warning : the identity does not exists (incorrect ids, identity deleted...)
                    // A customer must have a valid customer_id of connection_id
                    StatusMessage msg = new StatusMessage( TYPE_DEMAND, STATUS_WARNING, MESSAGE_INCORRECT_USER, WARNING_CUSTOMER_NOT_FOUND );
                    warnings.add( msg );
                    return false;
                }
            }
            catch ( Exception e )
            {
                customer = null;
                AppLogService.error( "An error occured while accessing IdentityStore", e );
                StatusMessage msg = new StatusMessage( TYPE_DEMAND, STATUS_ERROR, MESSAGE_INCORRECT_USER, ERROR_IDENTITYSTORE );
                warnings.add( msg );
                
                throw new IdentityStoreException ( "An error occured while accessing IdentityStore", e);
            }
        }

        /*
         * // reset customer ??? if ( !warnings.isEmpty( ) ) { customer = new Customer( ); customer.setConnectionId( StringUtils.EMPTY );
         * customer.setCustomerId( StringUtils.EMPTY ); }
         */

        notification.getDemand( ).setCustomer( customer );

        // default 
        return true;
    }

    /**
     * Link a notification to another
     *
     * @param strJson
     * @return the response
     */
    public Response reassignNotifications( String strJson )
    {
        try
        {
            _mapper.configure( DeserializationFeature.UNWRAP_ROOT_VALUE, false );
            ReassignNotificationsRequest request = _mapper.readValue( strJson, ReassignNotificationsRequest.class );
            _mapper.configure( DeserializationFeature.UNWRAP_ROOT_VALUE, true );

            AppLogService.debug( "notificationstore / ReassignNotificationsRequest - Received strJson : " + strJson );

            if ( !CustomerProvider.isCustomerIdValid( request.getOldCustomerId( ) ) || !CustomerProvider.isCustomerIdValid( request.getNewCustomerId( ) ) )
            {
                return fail( new Exception( "Invalid CUIDs" ), Response.Status.BAD_REQUEST );
            }

            NotificationFilter filter = new NotificationFilter( );
            filter.setCustomerId( request.getOldCustomerId( ) );
            List<Notification> listNotifsToReassign = NotificationHome.getByFilter( filter );

            if ( listNotifsToReassign.size( ) == 0 )
            {
                return ok( );
            }

            DemandHome.reassignDemands( request.getOldCustomerId( ), request.getNewCustomerId( ) );
            NotificationHome.reassignNotifications( request.getOldCustomerId( ), request.getNewCustomerId( ) );

            // generate events (for history)
            for ( Notification notif : listNotifsToReassign )
            {
                notif.getDemand( ).getCustomer( ).setCustomerId( request.getNewCustomerId( ) );
                addMergeEvent( notif, request );
            }
        }
        catch( JsonParseException ex )
        {
            return fail( ex, Response.Status.BAD_REQUEST );
        }
        catch( JsonMappingException | NullPointerException ex )
        {
            return fail( ex, Response.Status.BAD_REQUEST );
        }
        catch( IOException ex )
        {
            return fail( ex, Response.Status.INTERNAL_SERVER_ERROR );
        }
        catch( Exception ex )
        {
            return fail( ex, Response.Status.INTERNAL_SERVER_ERROR );
        }
        return success( );
    }

    /**
     * store a notification event
     * 
     * @param strJson
     * @return the response
     */
    public Response newNotificationEvent( String strJson )
    {
        try
        {
            NotificationEvent notificationEvent = _mapper.readValue( strJson, NotificationEvent.class );
            AppLogService.debug( "notificationstore / notificationEvent - Received strJson : " + strJson );

            store( notificationEvent );

        }
        catch( JsonParseException ex )
        {
            return fail( ex, Response.Status.BAD_REQUEST );
        }
        catch( JsonMappingException | NullPointerException ex )
        {
            return fail( ex, Response.Status.BAD_REQUEST );
        }
        catch( IOException ex )
        {
            return fail( ex, Response.Status.INTERNAL_SERVER_ERROR );
        }
        catch( Exception ex )
        {
            return fail( ex, Response.Status.INTERNAL_SERVER_ERROR );
        }

        return success( );

    }

    /**
     * Stores a notification and the associated demand
     * 
     * @param notification
     *            the notification to store
     */
    private void store( NotificationEvent notificationEvent )
    {
        // set customer id if provided by ID customer attribute (if valid)

        if ( notificationEvent.getDemand( ) != null && notificationEvent.getDemand( ).getCustomer( ) != null
                && !CustomerProvider.isCustomerIdValid( notificationEvent.getDemand( ).getCustomer( ).getCustomerId( ) )
                && CustomerProvider.isCustomerIdValid( notificationEvent.getDemand( ).getCustomer( ).getId( ) ) )
        {
            notificationEvent.getDemand( ).getCustomer( ).setCustomerId( notificationEvent.getDemand( ).getCustomer( ).getId( ) );
        }

        _demandService.create( notificationEvent );
    }

    /**
     * Stores a notification and the associated demand
     * 
     * @param notification
     *            the notification to store
     */
    private void store( Notification notification )
    {
        Demand demand = _demandService.findByPrimaryKey( notification.getDemand( ).getId( ), notification.getDemand( ).getTypeId( ),
                notification.getDemand( ).getCustomer( ).getCustomerId( ) );

        if ( demand == null || ( demand.getCustomer( ) != null && demand.getCustomer( ).getCustomerId( ) != null
                && !demand.getCustomer( ).getCustomerId( ).equals( notification.getDemand( ).getCustomer( ).getCustomerId( ) ) ) )
        {
            demand = new Demand( );

            demand.setId( notification.getDemand( ).getId( ) );
            demand.setTypeId( notification.getDemand( ).getTypeId( ) );
            demand.setSubtypeId( notification.getDemand( ).getSubtypeId( ) );
            demand.setReference( notification.getDemand( ).getReference( ) );
            demand.setCreationDate( notification.getDate( ) );
            demand.setMaxSteps( notification.getDemand( ).getMaxSteps( ) );
            demand.setCurrentStep( notification.getDemand( ).getCurrentStep( ) );
            demand.setStatusId( getNewDemandStatusIdFromNotification( notification ) );
            demand.setMetaData( notification.getDemand( ).getMetaData( ) );

            Customer customerDemand = new Customer( );
            customerDemand.setCustomerId( notification.getDemand( ).getCustomer( ).getId( ) );
            customerDemand.setCustomerId( notification.getDemand( ).getCustomer( ).getCustomerId( ) );
            customerDemand.setConnectionId( notification.getDemand( ).getCustomer( ).getConnectionId( ) );
            demand.setCustomer( customerDemand );

            // create demand
            _demandService.create( demand );
        }
        else
        {
            // update demand status
            demand.setCurrentStep( notification.getDemand( ).getCurrentStep( ) );

            demand.setModifyDate( notification.getDate( ) );

            int nNewStatusId = getNewDemandStatusIdFromNotification( notification );

            demand.setStatusId ( nNewStatusId );
            
            EnumGenericStatus oldStatus = EnumGenericStatus.getByStatusId( demand.getStatusId( ) );
            EnumGenericStatus newStatus = EnumGenericStatus.getByStatusId( nNewStatusId );

            // Demand opened to closed
            if ( oldStatus != null && newStatus != null && !oldStatus.isFinalStatus( ) && newStatus.isFinalStatus( ) )
            {
                demand.setClosureDate( notification.getDate( ) );
            }

            // Demand closed to opened
            if ( oldStatus != null && newStatus != null && oldStatus.isFinalStatus( ) && !newStatus.isFinalStatus( ) )
            {
                demand.setClosureDate( 0 );
            }

            _demandService.update( demand );
        }
        notification.setDemand( demand );

        // create notification
        _demandService.create( notification );
    }

    /**
     * Values and store the NotificationEvent object if failure
     * 
     * @param notification
     * @param warnings
     * @param strMessage
     */
    private void addEvents( Notification notification, List<StatusMessage> warnings )
    {
        // no event
        if ( warnings.isEmpty( ) )
        {
            return;
        }

        Event event = new Event( );
        event.setEventDate( notification.getDate( ) );

        if ( notification.getMyDashboardNotification( ) != null )
        {
            event.setType( TYPE_NOTIFICATION_GUICHET );
        }
        else
        {
            event.setType( TYPE_NOTIFICATION_AGENT );
        }

        event.setMessage( generateEventMessage( notification, warnings ) );
        event.setStatus( STATUS_FAILED );

        NotificationEvent notificationEvent = new NotificationEvent( );
        notificationEvent.setEvent( event );
        notificationEvent.setMsgId( StringUtils.EMPTY );
        notificationEvent.setDemand( notification.getDemand( ) );
        notificationEvent.setNotificationDate( notification.getDate( ) );

        store( notificationEvent );
    }

    /**
     * Values and store the NotificationEvent object if failure
     * 
     * @param notification
     * @param warnings
     * @param strMessage
     */
    private void addMergeEvent( Notification notification, ReassignNotificationsRequest request )
    {
        Event event = new Event( );
        event.setEventDate( notification.getDate( ) );

        event.setType( TYPE_MERGE_NOTIFICATIONS );

        event.setMessage( "Merged CUID : " + request.getOldCustomerId( ) + "\nConsolidated CUID : " + request.getNewCustomerId( ) );
        event.setStatus( STATUS_WARNING );

        NotificationEvent notificationEvent = new NotificationEvent( );
        notificationEvent.setEvent( event );
        notificationEvent.setMsgId( StringUtils.EMPTY );
        notificationEvent.setDemand( notification.getDemand( ) );
        notificationEvent.setNotificationDate( notification.getDate( ) );

        store( notificationEvent );
    }

    /**
     * Build an error response
     * 
     * @param strMessage
     *            The error message
     * @param ex
     *            An exception
     * @return The response
     */
    private Response successWithWarnings( List<StatusMessage> warnings )
    {
        StringBuilder strWarnings = new StringBuilder( "[" );

        if ( warnings != null )
        {
            for ( StatusMessage msg : warnings )
            {
                strWarnings.append( msg.asJson( ) ).append( "," );
            }

            // remove last ","
            strWarnings.setLength( strWarnings.length( ) - 1 );
        }

        strWarnings.append( "]" );

        String strResponse = "{ \"acknowledge\" : { \"status\": \"warning\", \"warnings\" : " + strWarnings.toString( ) + " } }";

        return Response.status( Response.Status.CREATED ).entity( strResponse ).build( );
    }

    /**
     * success case
     * 
     * @return a successful response
     */
    private Response success( )
    {
        return Response.status( Response.Status.CREATED ).entity( RESPONSE_OK ).build( );
    }

    /**
     * ok case
     * 
     * @return a successful response
     */
    private Response ok( )
    {
        return Response.status( Response.Status.OK ).entity( RESPONSE_OK ).build( );
    }

    /**
     * Build an error response
     * 
     * @param strMessage
     *            The error message
     * @param ex
     *            An exception
     * @return The response
     */
    private Response fail( Throwable ex, Status httpStatus )
    {
        StringBuilder strMsg = new StringBuilder( "[" );

        if ( ex != null )
        {
            AppLogService.error( ex.getMessage( ), ex );
            strMsg.append( new StatusMessage( TYPE_NOTIFICATION, STATUS_ERROR, ex.toString( ), ex.getMessage( ) ).asJson( ) );
        }

        strMsg.append( "]" );
        String strError = "{ \"acknowledge\" : { \"status\": \"error\", \"errors\" : " + strMsg + " } }";

        return Response.status( httpStatus ).entity( strError ).build( );
    }

    /**
     * Generates the error message
     * 
     * @param notification
     * @param strResponseStatus
     * @param strErrorMessage
     * @return
     */
    private String generateEventMessage( Notification notification, List<StatusMessage> warnings )
    {
        StringBuilder message = new StringBuilder( );
        message.append( "WARNINGS\n" );
        message.append( "\n" );
        message.append( "Demande id : " ).append( notification.getDemand( ).getId( ) ).append( "\n" );
        message.append( "Demande Type id : " ).append( notification.getDemand( ).getTypeId( ) ).append( "\n" );
        message.append( "Notification date : " ).append( Instant.ofEpochSecond( notification.getDate( ) ) ).append( "\n" );
        message.append( "\n" );

        if ( notification.getDemand( ).getCustomer( ) != null )
        {
            message.append( "Customer id: " ).append( notification.getDemand( ).getCustomer( ).getCustomerId( ) ).append( "\n" );
            message.append( "Connection id: " ).append( notification.getDemand( ).getCustomer( ).getConnectionId( ) ).append( "\n" );
        }
        message.append( "-------------------------\n" );

        for ( StatusMessage s : warnings )
        {
            message.append( "Type : " ).append( s.getType( ) ).append( "\n" );
            message.append( "Status : " ).append( s.getStatus( ) ).append( "\n" );
            message.append( "Message : " ).append( s.getStrMessage( ) ).append( "\n" );
            message.append( "Reason : " ).append( s.getReason( ) ).append( "\n" );
            message.append( "-------------------------\n" );

        }

        return message.toString( );
    }

    /**
     * Calculates the generic status id for new notifications.
     * 
     * @param notification
     * @return
     */
    private int getNewDemandStatusIdFromNotification( Notification notification )
    {
        // consider first the status sent in the demand
        if ( notification.getDemand( ) != null && notification.getDemand( ).getStatusId( ) > 0
                && EnumGenericStatus.exists( notification.getDemand( ).getStatusId( ) ) )
        {
            return notification.getDemand( ).getStatusId( );
        }

        // Otherwise, consider the MyDashBoard notification status id
        if ( notification.getMyDashboardNotification( ) != null )
        {
            if ( notification.getMyDashboardNotification( ).getStatusId( ) > 0
                    && EnumGenericStatus.exists( notification.getMyDashboardNotification( ).getStatusId( ) ) )
            {
                return notification.getMyDashboardNotification( ).getStatusId( );
            }

            // Otherwise, try to guess the status id from the label
            Optional<TemporaryStatus> status = _demandService.getStatusByLabel( notification.getMyDashboardNotification( ).getStatusText( ) );
            if ( status.isPresent( ) && status.get( ).getGenericStatus( ) != null )
            {
                return status.get( ).getGenericStatus( ).getStatusId( );
            }
        }

        // default
        return -1;
    }

    /**
     * parse json
     * 
     * @param strJson
     * @return the notification
     * @throws JsonMappingException
     * @throws JsonProcessingException
     */
    private Notification getNotificationFromJson( String strJson ) throws JsonMappingException, JsonProcessingException
    {
        AppLogService.debug( "notificationstore / notification - Received strJson : " + strJson );

        // Format from JSON
        ObjectMapper mapper = new ObjectMapper( );
        mapper.configure( DeserializationFeature.UNWRAP_ROOT_VALUE, true );
        mapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );

        Notification notification = mapper.readValue( strJson, Notification.class );

        return notification;
    }

    /**
     * call the registred notifyers
     * 
     * @param notification
     * @throws NotificationException
     */
    public void forward( Notification notification ) throws NotificationException
    {

        for ( INotifierServiceProvider notifier : _notifiers )
        {
            notifier.process( notification );
        }

    }
}
