/*
 * Copyright (c) 2002-2023, Mairie de Paris
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.paris.lutece.plugins.grubusiness.business.customer.Customer;
import fr.paris.lutece.plugins.grubusiness.business.demand.Demand;
import fr.paris.lutece.plugins.grubusiness.business.demand.DemandStatus;
import fr.paris.lutece.plugins.grubusiness.business.demand.IDemandServiceProvider;
import fr.paris.lutece.plugins.grubusiness.business.notification.Event;
import fr.paris.lutece.plugins.grubusiness.business.notification.Notification;
import fr.paris.lutece.plugins.grubusiness.business.notification.NotificationEvent;
import fr.paris.lutece.plugins.grubusiness.business.notification.StatusMessage;
import fr.paris.lutece.plugins.grubusiness.business.web.rs.EnumGenericStatus;
import fr.paris.lutece.plugins.grubusiness.service.notification.INotifyerServiceProvider;
import fr.paris.lutece.plugins.grubusiness.service.notification.NotificationException;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityNotFoundException;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;

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
	private static final String TYPE_GUICHET = "GUICHET";	

    // Messages
    private static final String WARNING_DEMAND_ID_MANDATORY = "Notification Demand_id field is mandatory";
    private static final String WARNING_DEMAND_TYPE_ID_MANDATORY = "Notification Demand_type_id field is mandatory";
    private static final String WARNING_CUSTOMER_ID_MANDATORY = "Notification connection_id field is mandatory";
    private static final String MESSAGE_MISSING_MANDATORY_FIELD = "Missing value";
    private static final String MESSAGE_MISSING_DEMAND_ID = "Demand Id and Demand type Id are mandatory";
    private static final String MESSAGE_MISSING_USER_ID = "User connection id is mandatory";
    private static final String MESSAGE_INCORRECT_DEMAND_ID = "Demand Type Id not found";

	// instance variables
	private static IDemandServiceProvider _demandService;
	private static NotificationService _instance;
	private static List<INotifyerServiceProvider> _notifyers ;

	/**
	 * private constructor
	 */
	private NotificationService( ) 
	{		
	}

	/**
	 * get unique instance of the service
	 * @return the notification service
	 */
	public static NotificationService instance( )
	{
		if ( _instance == null )
		{
			_instance = new NotificationService( );    		
			_demandService = SpringContextService.getBean( BEAN_STORAGE_SERVICE ); 
			_notifyers = SpringContextService.getBeansOfType( INotifyerServiceProvider.class ) ;
		}

		return _instance;
	}




	/**
	 * process Notification
	 * 
	 * @param notification
	 */
	public Response newNotification(String strJson)
	{
		List<StatusMessage> warnings = new ArrayList<>( );

		try
		{
			// parse json
			Notification notification = getNotificationFromJson( strJson );

			// control customer
			processCustomer( notification );

			// store any notification whatever its content
			store( notification );

			// check Notification
			checkNotification( notification, warnings );
			
			// create Event if a MyDashboard type of notification exists 
			if ( notification.getMyDashboardNotification( ) != null ) 
			{
				addMydashboardNotificationEvent( notification );
			}

			// forward notification to registred notifyers (if exists)
			forward( notification );

		}
		catch( JsonParseException ex )
		{
			return fail( ex, Response.Status.BAD_REQUEST );
		}
		catch( JsonMappingException | NullPointerException | NotificationException ex )
		{
			return fail( ex, Response.Status.BAD_REQUEST  );
		}
		catch( IOException ex )
		{
			return fail( ex, Response.Status.INTERNAL_SERVER_ERROR  );
		}
		catch( Exception ex )
		{
			return fail( ex, Response.Status.INTERNAL_SERVER_ERROR  );
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
	private void checkNotification(Notification notification, List<StatusMessage> warnings) {
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

		// notification should be associated to a customer id
		if ( notification.getDemand( ).getCustomer( ) != null 
				&& StringUtils.isBlank( notification.getDemand( ).getCustomer( ).getConnectionId( ) ) )
		{
			StatusMessage msg = new StatusMessage( TYPE_DEMAND, STATUS_WARNING, MESSAGE_MISSING_MANDATORY_FIELD, WARNING_CUSTOMER_ID_MANDATORY );
			warnings.add( msg );
		}
		
	}

	/**
	 * Process customer data
	 * @param notification
	 * @throws IdentityStoreException
	 */
	private void processCustomer(Notification notification) throws IdentityStoreException {
		
		Customer customerEncrypted = notification.getDemand( ).getCustomer( );

		if ( CustomerProvider.instance( ).hasIdentityService( ) )
		{
			Customer customer = CustomerProvider.instance( ).decrypt( customerEncrypted, notification.getDemand( ) );
			
			if ( StringUtils.isEmpty( customer.getCustomerId( ) ) && !StringUtils.isEmpty( customer.getId( ) ) )
			{
				customer.setCustomerId( customer.getId( ) );
			}
			
			try
			{
				if ( customer != null )
				{
					if ( StringUtils.isEmpty( customer.getCustomerId( ) ) && StringUtils.isNotEmpty( customer.getConnectionId() ) )
					{
						// search by connection id (GUID)
						Customer customerTmp = CustomerProvider.instance( ).get( customer.getConnectionId( ), StringUtils.EMPTY );
						if ( customerTmp != null )
						{
							customer.setCustomerId( customerTmp.getCustomerId( ) );
						}
						else
						{
							// reset customer (a valid customer must have at least a customer_id or a connection_id)
							customer = null;
							AppLogService.debug( "Customer not found with connection_id : " + customer.getConnectionId( ) );
						}
					}
				}
			}
			catch( IdentityStoreException e )
			{
				customer = null;
				AppLogService.error( "An error occured while accessing IdentityStore", e  );
			}

			if ( customer == null )
			{
				customer = new Customer( );
				customer.setConnectionId( StringUtils.EMPTY );
				customer.setCustomerId( StringUtils.EMPTY );
				notification.getDemand().setCustomer( customer );				
			}

			notification.getDemand( ).setCustomer( customer );
		}
		else
		{
			notification.getDemand( ).setCustomer( customerEncrypted );
		}
		
	}

	/**
	 * store a notification event
	 * 
	 * @param strJson
	 * @return the response
	 */
	public Response newNotificationEvent(String strJson) 
	{
		try
		{
			// Format from JSON
			ObjectMapper mapper = new ObjectMapper( );
			mapper.configure( DeserializationFeature.UNWRAP_ROOT_VALUE, true );
			mapper.configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false );

			NotificationEvent notificationEvent = mapper.readValue( strJson, NotificationEvent.class );
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
			return fail( ex, Response.Status.INTERNAL_SERVER_ERROR  );
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
        Demand demand = _demandService.findByPrimaryKey( notification.getDemand( ).getId( ), 
        		notification.getDemand( ).getTypeId( ),
        		notification.getDemand( ).getCustomer( ).getCustomerId( ));
                
        if ( demand == null || 
        		( demand.getCustomer( ) != null && demand.getCustomer( ).getCustomerId( ) != null 
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
            demand.setStatusId( -1 );

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

            int nNewStatusId = getNewDemandStatusIdFromNotification( notification );
            
            EnumGenericStatus oldStatus = EnumGenericStatus.getByStatusId( demand.getStatusId( ) );
            EnumGenericStatus newStatus = EnumGenericStatus.getByStatusId( nNewStatusId );

            // Demand opened to closed
            if ( oldStatus != null && newStatus != null 
            		&& !oldStatus.isFinalStatus( ) && newStatus.isFinalStatus( ) )                  
            {
                demand.setClosureDate( notification.getDate( ) );
            }

            // Demand closed to opened
            if ( oldStatus != null && newStatus != null 
            		&& oldStatus.isFinalStatus( ) && !newStatus.isFinalStatus( ) )                  
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
	 * Check notification
	 * @param notif
	 * @return The message error
	 */
	private String checkNotification( Notification notif )
	{        
		// check if connection id is present
		if ( notif.getDemand( ) == null || notif.getDemand( ).getCustomer( ) == null || StringUtils.isBlank( notif.getDemand( ).getCustomer( ).getConnectionId( ) ) )
		{
			return generateErrorMessage( notif, Response.Status.PRECONDITION_FAILED, MESSAGE_MISSING_USER_ID );
		}

		// check if Demand remote id and demand type id are present
		if ( StringUtils.isBlank( notif.getDemand( ).getId( ) ) || StringUtils.isBlank( notif.getDemand( ).getTypeId( ) ) )
		{
			return generateErrorMessage( notif, Response.Status.PRECONDITION_FAILED, MESSAGE_MISSING_DEMAND_ID );
		}

		// check id demand_type_id is numeric
		if ( !StringUtils.isNumeric( notif.getDemand( ).getTypeId( ) ) )
		{
			return generateErrorMessage( notif, Response.Status.PRECONDITION_FAILED, MESSAGE_INCORRECT_DEMAND_ID );
		}

		// check if demand type id exists
		if ( !_demandService.getDemandType( Integer.parseInt( notif.getDemand( ).getTypeId( ) ) ).isPresent( ) )
		{
			return generateErrorMessage( notif, Response.Status.PRECONDITION_FAILED, MESSAGE_INCORRECT_DEMAND_ID );
		}

		return StringUtils.EMPTY;
	}

	/**
	 * Values and store the NotificationEvent object if failure
	 * @param notification
	 * @param strMessage
	 */
	private void addMydashboardNotificationEvent ( Notification notification  )
	{
		if ( notification.getMyDashboardNotification( ) != null )
		{
			Event event = new Event( );
			event.setType( TYPE_GUICHET );
			event.setEventDate( notification.getDate( ) );
			
			String strMessage = checkNotification( notification ); 

			if( StringUtils.isNotEmpty( strMessage ))
			{
				event.setMessage( strMessage );
				event.setStatus( STATUS_FAILED );
			}
			else
			{
				event.setStatus( STATUS_SUCCESS );
			}
			
			NotificationEvent notificationEvent = new NotificationEvent( );
			notificationEvent.setEvent( event );
			notificationEvent.setMsgId( StringUtils.EMPTY );   
			notificationEvent.setDemand( notification.getDemand( ) );
			notificationEvent.setNotificationDate( notification.getDate( ) );
			
			store( notificationEvent );
		}
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

		if (warnings != null)
		{
			for ( StatusMessage msg : warnings )
			{
				strWarnings.append( msg.asJson( ) ).append( "," );
			}

			// remove last ","
			strWarnings.setLength( strWarnings.length( ) - 1);
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
			strMsg.append( new StatusMessage( TYPE_NOTIFICATION, STATUS_ERROR, ex.toString( ) , ex.getMessage( ) ).asJson( ) );
		}

		strMsg.append( "]" );                
		String strError = "{ \"acknowledge\" : { \"status\": \"error\", \"errors\" : " + strMsg + " } }";

		return Response.status( httpStatus ).entity( strError ).build( );
	}

	/**
	 * Generates the error message
	 * @param notification
	 * @param strResponseStatus
	 * @param strErrorMessage
	 * @return
	 */
	private String generateErrorMessage ( Notification notification, Status strResponseStatus,  String strErrorMessage)
	{
		StringBuilder message = new StringBuilder();
		message.append( "\n" );
		message.append( "Demande id " + notification.getDemand( ).getId( ) + "\n" );
		message.append( "Notification id " + notification.getId( ) + "\n" );
		message.append( "Status: " + strResponseStatus.getStatusCode( ) + " " + strResponseStatus.getReasonPhrase( ) + "\n" );
		message.append( "Error: " + strErrorMessage  + "\n" );

		return message.toString( );
	}

    /**
     * Calculates the generic status id for new notifications.
     * @param notification
     * @return
     */
    private int getNewDemandStatusIdFromNotification( Notification notification )
    {                
        if ( notification.getMyDashboardNotification( ) != null )
        {
        	// consider first if the status is sent by the demand
            if ( notification.getDemand( ) != null 
                    && EnumGenericStatus.exists( notification.getDemand( ).getStatusId( ) )  )
            {
                return notification.getDemand( ).getStatusId( );
            }
            
            // consider the notification status id
            if ( EnumGenericStatus.exists( notification.getMyDashboardNotification( ).getStatusId( ) ) )
            {
                return notification.getMyDashboardNotification( ).getStatusId( );
            } 
            
	        Optional<DemandStatus> status = _demandService.getStatusByLabel( notification.getMyDashboardNotification( ).getStatusText( ) );
	        if ( status.isPresent( ) && status.get( ).getGenericStatus( ) != null )
	        {
	            return status.get( ).getGenericStatus( ).getStatusId( );
	        }
	        
	        return -1;
        }
        
        // default
        return notification.getDemand( ).getStatusId( );
    }
    
    /**
     * parse json 
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
        
        for ( INotifyerServiceProvider notifyer : _notifyers )
        {
            notifyer.process( notification );
        }
        
    } 
}
