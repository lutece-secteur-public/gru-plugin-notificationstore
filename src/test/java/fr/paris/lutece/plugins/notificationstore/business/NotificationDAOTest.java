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
import fr.paris.lutece.plugins.grubusiness.business.notification.BackofficeNotification;
import fr.paris.lutece.plugins.grubusiness.business.notification.BroadcastNotification;
import fr.paris.lutece.plugins.grubusiness.business.notification.MyDashboardNotification;
import fr.paris.lutece.plugins.grubusiness.business.notification.EmailAddress;
import fr.paris.lutece.plugins.grubusiness.business.notification.EmailNotification;
import fr.paris.lutece.plugins.grubusiness.business.notification.INotificationDAO;
import fr.paris.lutece.plugins.grubusiness.business.notification.Notification;
import fr.paris.lutece.plugins.grubusiness.business.notification.NotificationFilter;
import fr.paris.lutece.plugins.grubusiness.business.notification.SMSNotification;
import fr.paris.lutece.test.LuteceTestCase;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Test class for the NotificatinoDAO
 *
 */
public class NotificationDAOTest extends LuteceTestCase
{
    private static final String DEMAND_ID_1 = "1";
    private static final String DEMAND_ID_2 = "2";
    private static final String DEMAND_TYPE_ID_1 = "1";
    private static final String DEMAND_TYPE_ID_2 = "2";
    private static final String DEMAND_SUBTYPE_ID_1 = "sub1";
    private static final String DEMAND_SUBTYPE_ID_2 = "sub2";
    private static final String DEMAND_REFERENCE_1 = "DemandReference1";
    private static final String DEMAND_REFERENCE_2 = "DemandReference2";
    private static final int DEMAND_STATUS_ID_1 = 1;
    private static final int DEMAND_STATUS_ID_2 = 2;
    private static final long NOTIFICATION_DATE_1 = 1L;
    private static final long NOTIFICATION_DATE_2 = 2L;
    private static final long NOTIFICATION_DATE_3 = 3L;
    private static final long NOTIFICATION_DATE_4 = 4L;
    private static final long NOTIFICATION_DATE_5 = 5L;
    private static final long NOTIFICATION_DATE_6 = 6L;
    private static final String BACKOFFICE_NOTIFICATION_MESSAGE_1 = "BackofficeMessage1";
    private static final String BACKOFFICE_NOTIFICATION_STATUS_TEXT_1 = "BackofficeStatusText1";
    private static final String SMS_NOTIFICATION_MESSAGE_1 = "SmsMessage1";
    private static final String SMS_NOTIFICATION_PHONE_NUMBER_1 = "SmsPhoneNumber1";
    private static final String CUSTOMER_ID_1 = "CustomerId1";
    private static final String CUSTOMER_ID_2 = "CustomerId2";
    private static final String CUSTOMER_EMAIL_NOTIFICATION_SENDER_EMAIL_1 = "CustomerEmailSenderEmail1";
    private static final String CUSTOMER_EMAIL_NOTIFICATION_SENDER_NAME_1 = "CustomerEmailSenderName1";
    private static final String CUSTOMER_EMAIL_NOTIFICATION_SUBJECT_1 = "CustomerEmailSubject1";
    private static final String CUSTOMER_EMAIL_NOTIFICATION_MESSAGE_1 = "CustomerEmailMessage1";
    private static final String CUSTOMER_EMAIL_NOTIFICATION_RECIPIENT_1 = "CustomerEmailRecipient1.1;EmailRecipient1.2";
    private static final String CUSTOMER_EMAIL_NOTIFICATION_COPIES_1 = "CustomerEmailCopies1.1;EmailCopies1.2";
    private static final String CUSTOMER_EMAIL_NOTIFICATION_BLIND_COPIES_1 = "CustomerEmailBlindCopies1.1;EmailBlindCopies1.2";
    private static final int MYDASHBOARD_NOTIFICATION_STATUS_ID_1 = 1;
    private static final String MYDASHBOARD_NOTIFICATION_STATUS_TEXT_1 = "MyDashboardStatusText1";
    private static final String MYDASHBOARD_NOTIFICATION_MESSAGE_1 = "MyDashboardMessage1";
    private static final String MYDASHBOARD_NOTIFICATION_SUBJECT_1 = "MyDashboardSubject1";
    private static final String MYDASHBOARD_NOTIFICATION_DATA_1 = "MyDashboardData1";
    private static final String MYDASHBOARD_NOTIFICATION_SENDER_NAME_1 = "MyDashboardSenderName1";
    private static final String BROADCAST_EMAIL_NOTIFICATION_SENDER_EMAIL_1 = "BroadcastEmailSenderEmail1";
    private static final String BROADCAST_EMAIL_NOTIFICATION_SENDER_NAME_1 = "BroadcastEmailSenderName1";
    private static final String BROADCAST_EMAIL_NOTIFICATION_SUBJECT_1 = "BroadcastEmailSubject1";
    private static final String BROADCAST_EMAIL_NOTIFICATION_MESSAGE_1 = "BroadcastEmailMessage1";
    private static final String EMAIL_ADDRESS_RECIPIENT_1 = "EmailAddressRecipient1";
    private static final String EMAIL_ADDRESS_RECIPIENT_2 = "EmailAddressRecipient2";
    private static final String EMAIL_ADDRESS_COPY_1 = "EmailAddressRecipient1";
    private static final String EMAIL_ADDRESS_COPY_2 = "EmailAddressRecipient2";
    private static final String EMAIL_ADDRESS_BLIND_COPY_1 = "EmailAddressRecipient1";
    private static final String EMAIL_ADDRESS_BLIND_COPY_2 = "EmailAddressRecipient2";
    private final IDemandDAO _demandDAO;
    private final INotificationDAO _notificationDAO;
    private final NotificationFilter _filterSMS;
    private final NotificationFilter _filterCustomerEmail;
    private final NotificationFilter _filterBackoffice;
    private final NotificationFilter _filterMyDashboard;
    private final NotificationFilter _filterBroadcast;

    /**
     * Constructor
     */
    public NotificationDAOTest( )
    {
        _notificationDAO = new NotificationDAO( );
        _demandDAO = new DemandDAO( );
        _filterSMS = new NotificationFilter( );
        _filterCustomerEmail = new NotificationFilter( );
        _filterBackoffice = new NotificationFilter( );
        _filterMyDashboard = new NotificationFilter( );
        _filterBroadcast = new NotificationFilter( );
    }

    /**
     * Test case 1
     */
    public void test1Business( )
    {
        // Initialize an object
        Demand demand = new Demand( );
        demand.setDemandId( DEMAND_ID_1 );
        demand.setTypeId( DEMAND_TYPE_ID_1 );
        demand.setSubtypeId( DEMAND_SUBTYPE_ID_1 );
        demand.setReference( DEMAND_REFERENCE_1 );
        demand.setStatusId( DEMAND_STATUS_ID_1 );

        Customer customer = new Customer( );
        customer.setId( CUSTOMER_ID_1 );
        demand.setCustomer( customer );

        _demandDAO.insert( demand );

        Notification notification = new Notification( );
        notification.setDemand( demand );
        notification.setDate( NOTIFICATION_DATE_1 );

        // Create test
        _notificationDAO.insert( notification );

        String strDemandId = notification.getDemand( ).getDemandId( );
        String strDemandTypeId = notification.getDemand( ).getTypeId( );
        NotificationFilter filterDemand = new NotificationFilter( );
        filterDemand.setDemandId( strDemandId );
        filterDemand.setDemandTypeId( strDemandTypeId );
        List<Notification> collectionNotificationStored = _notificationDAO.loadByDemand( strDemandId, strDemandTypeId );
        assertEquals( collectionNotificationStored.size( ), 1 );

        Iterator<Notification> iterator = collectionNotificationStored.iterator( );
        Notification notificationStored = iterator.next( );

        assertEquals( notificationStored.getDemand( ).getId( ), notification.getDemand( ).getId( ) );
        assertEquals( notificationStored.getDemand( ).getTypeId( ), notification.getDemand( ).getTypeId( ) );
        assertEquals( notificationStored.getDemand( ).getSubtypeId( ), notification.getDemand( ).getSubtypeId( ) );

        collectionNotificationStored = _notificationDAO.loadByFilter( filterDemand );
        assertEquals( collectionNotificationStored.size( ), 1 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterSMS );
        assertEquals( collectionNotificationStored.size( ), 0 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterCustomerEmail );
        assertEquals( collectionNotificationStored.size( ), 0 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterBackoffice );
        assertEquals( collectionNotificationStored.size( ), 0 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterMyDashboard );
        assertEquals( collectionNotificationStored.size( ), 0 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterBroadcast );
        assertEquals( collectionNotificationStored.size( ), 0 );

        notification.setDate( NOTIFICATION_DATE_2 );

        BackofficeNotification backofficeNotification = new BackofficeNotification( );
        backofficeNotification.setMessage( BACKOFFICE_NOTIFICATION_MESSAGE_1 );
        backofficeNotification.setStatusText( BACKOFFICE_NOTIFICATION_STATUS_TEXT_1 );
        notification.setBackofficeNotification( backofficeNotification );
        _notificationDAO.insert( notification );

        collectionNotificationStored = _notificationDAO.loadByDemand( strDemandId, strDemandTypeId );
        assertEquals( collectionNotificationStored.size( ), 2 );
        iterator = collectionNotificationStored.iterator( );
        notificationStored = iterator.next( );

        BackofficeNotification backofficeNotificationStored = notificationStored.getBackofficeNotification( );
        assertEquals( backofficeNotificationStored.getMessage( ), backofficeNotification.getMessage( ) );
        assertEquals( backofficeNotificationStored.getStatusText( ), backofficeNotification.getStatusText( ) );

        collectionNotificationStored = _notificationDAO.loadByFilter( filterDemand );
        assertEquals( collectionNotificationStored.size( ), 2 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterBackoffice );
        assertEquals( collectionNotificationStored.size( ), 1 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterSMS );
        assertEquals( collectionNotificationStored.size( ), 0 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterCustomerEmail );
        assertEquals( collectionNotificationStored.size( ), 0 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterMyDashboard );
        assertEquals( collectionNotificationStored.size( ), 0 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterBroadcast );
        assertEquals( collectionNotificationStored.size( ), 0 );

        notification.setDate( NOTIFICATION_DATE_3 );

        SMSNotification smsNotification = new SMSNotification( );
        smsNotification.setMessage( SMS_NOTIFICATION_MESSAGE_1 );
        smsNotification.setPhoneNumber( SMS_NOTIFICATION_PHONE_NUMBER_1 );
        notification.setSmsNotification( smsNotification );
        _notificationDAO.insert( notification );

        collectionNotificationStored = _notificationDAO.loadByDemand( strDemandId, strDemandTypeId );
        assertEquals( collectionNotificationStored.size( ), 3 );
        iterator = collectionNotificationStored.iterator( );
        notificationStored = iterator.next( );

        SMSNotification smsNotificationStored = notificationStored.getSmsNotification( );
        assertEquals( smsNotificationStored.getMessage( ), smsNotification.getMessage( ) );
        assertEquals( smsNotificationStored.getPhoneNumber( ), smsNotification.getPhoneNumber( ) );

        collectionNotificationStored = _notificationDAO.loadByFilter( filterDemand );
        assertEquals( collectionNotificationStored.size( ), 3 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterBackoffice );
        assertEquals( collectionNotificationStored.size( ), 2 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterSMS );
        assertEquals( collectionNotificationStored.size( ), 1 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterCustomerEmail );
        assertEquals( collectionNotificationStored.size( ), 0 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterMyDashboard );
        assertEquals( collectionNotificationStored.size( ), 0 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterBroadcast );
        assertEquals( collectionNotificationStored.size( ), 0 );

        notification.setDate( NOTIFICATION_DATE_4 );

        EmailNotification emailNotification = new EmailNotification( );
        emailNotification.setSenderEmail( CUSTOMER_EMAIL_NOTIFICATION_SENDER_EMAIL_1 );
        emailNotification.setSenderName( CUSTOMER_EMAIL_NOTIFICATION_SENDER_NAME_1 );
        emailNotification.setSubject( CUSTOMER_EMAIL_NOTIFICATION_SUBJECT_1 );
        emailNotification.setMessage( CUSTOMER_EMAIL_NOTIFICATION_MESSAGE_1 );
        emailNotification.setRecipient( CUSTOMER_EMAIL_NOTIFICATION_RECIPIENT_1 );
        emailNotification.setCc( CUSTOMER_EMAIL_NOTIFICATION_COPIES_1 );
        emailNotification.setBcc( CUSTOMER_EMAIL_NOTIFICATION_BLIND_COPIES_1 );
        notification.setEmailNotification( emailNotification );
        _notificationDAO.insert( notification );

        collectionNotificationStored = _notificationDAO.loadByDemand( strDemandId, strDemandTypeId );
        assertEquals( collectionNotificationStored.size( ), 4 );
        iterator = collectionNotificationStored.iterator( );
        notificationStored = iterator.next( );

        EmailNotification emailNotificationStored = notificationStored.getEmailNotification( );
        assertEquals( emailNotificationStored.getSenderEmail( ), emailNotification.getSenderEmail( ) );
        assertEquals( emailNotificationStored.getSenderName( ), emailNotification.getSenderName( ) );
        assertEquals( emailNotificationStored.getSubject( ), emailNotification.getSubject( ) );
        assertEquals( emailNotificationStored.getMessage( ), emailNotification.getMessage( ) );
        assertEquals( emailNotificationStored.getRecipient( ), emailNotification.getRecipient( ) );
        assertEquals( emailNotificationStored.getCc( ), emailNotification.getCc( ) );
        assertEquals( emailNotificationStored.getCci( ), emailNotification.getCci( ) );

        collectionNotificationStored = _notificationDAO.loadByFilter( filterDemand );
        assertEquals( collectionNotificationStored.size( ), 4 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterBackoffice );
        assertEquals( collectionNotificationStored.size( ), 3 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterSMS );
        assertEquals( collectionNotificationStored.size( ), 2 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterCustomerEmail );
        assertEquals( collectionNotificationStored.size( ), 1 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterMyDashboard );
        assertEquals( collectionNotificationStored.size( ), 0 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterBroadcast );
        assertEquals( collectionNotificationStored.size( ), 0 );

        notification.setDate( NOTIFICATION_DATE_5 );

        MyDashboardNotification myDashboardNotification = new MyDashboardNotification( );
        myDashboardNotification.setStatusId( MYDASHBOARD_NOTIFICATION_STATUS_ID_1 );
        myDashboardNotification.setStatusText( MYDASHBOARD_NOTIFICATION_STATUS_TEXT_1 );
        myDashboardNotification.setMessage( MYDASHBOARD_NOTIFICATION_MESSAGE_1 );
        myDashboardNotification.setSubject( MYDASHBOARD_NOTIFICATION_SUBJECT_1 );
        myDashboardNotification.setData( MYDASHBOARD_NOTIFICATION_DATA_1 );
        myDashboardNotification.setSenderName( MYDASHBOARD_NOTIFICATION_SENDER_NAME_1 );
        notification.setMyDashboardNotification( myDashboardNotification );
        _notificationDAO.insert( notification );

        collectionNotificationStored = _notificationDAO.loadByDemand( strDemandId, strDemandTypeId );
        assertEquals( collectionNotificationStored.size( ), 5 );
        iterator = collectionNotificationStored.iterator( );
        notificationStored = iterator.next( );

        MyDashboardNotification myDashboardNotificationStored = notificationStored.getMyDashboardNotification( );
        assertEquals( myDashboardNotificationStored.getStatusId( ), myDashboardNotification.getStatusId( ) );
        assertEquals( myDashboardNotificationStored.getStatusText( ), myDashboardNotification.getStatusText( ) );
        assertEquals( myDashboardNotificationStored.getMessage( ), myDashboardNotification.getMessage( ) );
        assertEquals( myDashboardNotificationStored.getSubject( ), myDashboardNotification.getSubject( ) );
        assertEquals( myDashboardNotificationStored.getData( ), myDashboardNotification.getData( ) );
        assertEquals( myDashboardNotificationStored.getSenderName( ), myDashboardNotification.getSenderName( ) );

        collectionNotificationStored = _notificationDAO.loadByFilter( filterDemand );
        assertEquals( collectionNotificationStored.size( ), 5 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterBackoffice );
        assertEquals( collectionNotificationStored.size( ), 4 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterSMS );
        assertEquals( collectionNotificationStored.size( ), 3 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterCustomerEmail );
        assertEquals( collectionNotificationStored.size( ), 2 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterMyDashboard );
        assertEquals( collectionNotificationStored.size( ), 1 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterBroadcast );
        assertEquals( collectionNotificationStored.size( ), 0 );

        notification.setDate( NOTIFICATION_DATE_6 );

        List<BroadcastNotification> listBroadcastNotifications = new ArrayList<BroadcastNotification>( );
        BroadcastNotification broadcastNotification = new BroadcastNotification( );
        broadcastNotification.setSenderEmail( BROADCAST_EMAIL_NOTIFICATION_SENDER_EMAIL_1 );
        broadcastNotification.setSenderName( BROADCAST_EMAIL_NOTIFICATION_SENDER_NAME_1 );
        broadcastNotification.setSubject( BROADCAST_EMAIL_NOTIFICATION_SUBJECT_1 );
        broadcastNotification.setMessage( BROADCAST_EMAIL_NOTIFICATION_MESSAGE_1 );

        List<EmailAddress> listEmailAddresses = new ArrayList<EmailAddress>( );
        EmailAddress emailAddress = new EmailAddress( );
        emailAddress.setAddress( EMAIL_ADDRESS_RECIPIENT_1 );
        listEmailAddresses.add( emailAddress );
        emailAddress = new EmailAddress( );
        emailAddress.setAddress( EMAIL_ADDRESS_RECIPIENT_2 );
        listEmailAddresses.add( emailAddress );
        broadcastNotification.setRecipient( listEmailAddresses );
        listEmailAddresses = new ArrayList<EmailAddress>( );
        emailAddress = new EmailAddress( );
        emailAddress.setAddress( EMAIL_ADDRESS_COPY_1 );
        listEmailAddresses.add( emailAddress );
        emailAddress = new EmailAddress( );
        emailAddress.setAddress( EMAIL_ADDRESS_COPY_2 );
        listEmailAddresses.add( emailAddress );
        broadcastNotification.setCc( listEmailAddresses );
        listEmailAddresses = new ArrayList<EmailAddress>( );
        emailAddress = new EmailAddress( );
        emailAddress.setAddress( EMAIL_ADDRESS_BLIND_COPY_1 );
        listEmailAddresses.add( emailAddress );
        emailAddress = new EmailAddress( );
        emailAddress.setAddress( EMAIL_ADDRESS_BLIND_COPY_2 );
        listEmailAddresses.add( emailAddress );
        broadcastNotification.setBcc( listEmailAddresses );
        listBroadcastNotifications.add( broadcastNotification );

        BroadcastNotification broadcastNotification2 = new BroadcastNotification( );
        broadcastNotification2.setSenderEmail( BROADCAST_EMAIL_NOTIFICATION_SENDER_EMAIL_1 );
        broadcastNotification2.setSenderName( BROADCAST_EMAIL_NOTIFICATION_SENDER_NAME_1 );
        broadcastNotification2.setSubject( BROADCAST_EMAIL_NOTIFICATION_SUBJECT_1 );
        broadcastNotification2.setMessage( BROADCAST_EMAIL_NOTIFICATION_MESSAGE_1 );
        broadcastNotification2.setRecipient( listEmailAddresses );
        broadcastNotification2.setCc( listEmailAddresses );
        broadcastNotification2.setBcc( listEmailAddresses );
        listBroadcastNotifications.add( broadcastNotification2 );

        notification.setBroadcastEmail( listBroadcastNotifications );
        _notificationDAO.insert( notification );

        collectionNotificationStored = _notificationDAO.loadByDemand( strDemandId, strDemandTypeId );
        assertEquals( collectionNotificationStored.size( ), 6 );
        iterator = collectionNotificationStored.iterator( );
        notificationStored = iterator.next( );

        List<BroadcastNotification> listBroadcastNotificationsStored = notificationStored.getBroadcastEmail( );
        assertEquals( listBroadcastNotificationsStored.size( ), 2 );

        BroadcastNotification broadcastNotificationStored = listBroadcastNotificationsStored.get( 0 );
        assertEquals( broadcastNotificationStored.getSenderEmail( ), broadcastNotification.getSenderEmail( ) );
        assertEquals( broadcastNotificationStored.getSenderName( ), broadcastNotification.getSenderName( ) );
        assertEquals( broadcastNotificationStored.getSubject( ), broadcastNotification.getSubject( ) );
        assertEquals( broadcastNotificationStored.getMessage( ), broadcastNotification.getMessage( ) );

        List<EmailAddress> listEmailAddressesStored = broadcastNotificationStored.getRecipient( );
        assertEquals( listEmailAddressesStored.size( ), 2 );
        listEmailAddressesStored = broadcastNotificationStored.getCc( );
        assertEquals( listEmailAddressesStored.size( ), 2 );
        listEmailAddressesStored = broadcastNotificationStored.getBcc( );
        assertEquals( listEmailAddressesStored.size( ), 2 );

        collectionNotificationStored = _notificationDAO.loadByFilter( filterDemand );
        assertEquals( collectionNotificationStored.size( ), 6 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterBackoffice );
        assertEquals( collectionNotificationStored.size( ), 5 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterSMS );
        assertEquals( collectionNotificationStored.size( ), 4 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterCustomerEmail );
        assertEquals( collectionNotificationStored.size( ), 3 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterMyDashboard );
        assertEquals( collectionNotificationStored.size( ), 2 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterBroadcast );
        assertEquals( collectionNotificationStored.size( ), 1 );

        _notificationDAO.deleteByDemand( strDemandId, strDemandTypeId );
        collectionNotificationStored = _notificationDAO.loadByDemand( strDemandId, strDemandTypeId );
        assertEquals( collectionNotificationStored.size( ), 0 );

        collectionNotificationStored = _notificationDAO.loadByFilter( filterDemand );
        assertEquals( collectionNotificationStored.size( ), 0 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterBackoffice );
        assertEquals( collectionNotificationStored.size( ), 0 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterSMS );
        assertEquals( collectionNotificationStored.size( ), 0 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterCustomerEmail );
        assertEquals( collectionNotificationStored.size( ), 0 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterMyDashboard );
        assertEquals( collectionNotificationStored.size( ), 0 );
        collectionNotificationStored = _notificationDAO.loadByFilter( _filterBroadcast );
        assertEquals( collectionNotificationStored.size( ), 0 );

        _demandDAO.delete( strDemandId, strDemandTypeId );
    }

    /**
     * Test case 2
     */
    public void test2Business( )
    {
        // Initialize an object
        Demand demand = new Demand( );
        demand.setDemandId( DEMAND_ID_1 );
        demand.setTypeId( DEMAND_TYPE_ID_1 );
        demand.setSubtypeId( DEMAND_SUBTYPE_ID_1 );
        demand.setReference( DEMAND_REFERENCE_1 );
        demand.setStatusId( DEMAND_STATUS_ID_1 );

        Customer customer = new Customer( );
        customer.setId( CUSTOMER_ID_1 );
        demand.setCustomer( customer );

        _demandDAO.insert( demand );

        Demand demand2 = new Demand( );
        demand2.setDemandId( DEMAND_ID_2 );
        demand2.setTypeId( DEMAND_TYPE_ID_2 );
        demand.setSubtypeId( DEMAND_SUBTYPE_ID_2 );
        demand2.setReference( DEMAND_REFERENCE_2 );
        demand2.setStatusId( DEMAND_STATUS_ID_2 );

        Customer customer2 = new Customer( );
        customer2.setId( CUSTOMER_ID_2 );
        demand2.setCustomer( customer2 );

        _demandDAO.insert( demand2 );

        Notification notification = new Notification( );
        notification.setDemand( demand );
        notification.setDate( NOTIFICATION_DATE_1 );
        _notificationDAO.insert( notification );

        notification = new Notification( );
        notification.setDemand( demand2 );
        notification.setDate( NOTIFICATION_DATE_2 );
        _notificationDAO.insert( notification );

        Collection<Notification> collectionNotificationStored = _notificationDAO.loadByDemand( DEMAND_ID_1, DEMAND_TYPE_ID_1 );
        assertEquals( collectionNotificationStored.size( ), 1 );

        Iterator<Notification> iterator = collectionNotificationStored.iterator( );
        Notification notificationStored = iterator.next( );
        assertSame( notificationStored.getDate( ), NOTIFICATION_DATE_1 );

        collectionNotificationStored = _notificationDAO.loadByDemand( DEMAND_ID_2, DEMAND_TYPE_ID_2 );
        assertEquals( collectionNotificationStored.size( ), 1 );
        iterator = collectionNotificationStored.iterator( );
        notificationStored = iterator.next( );
        assertSame( notificationStored.getDate( ), NOTIFICATION_DATE_2 );

        _notificationDAO.deleteByDemand( DEMAND_ID_1, DEMAND_TYPE_ID_1 );
        collectionNotificationStored = _notificationDAO.loadByDemand( DEMAND_ID_1, DEMAND_TYPE_ID_1 );
        assertEquals( collectionNotificationStored.size( ), 0 );

        _notificationDAO.deleteByDemand( DEMAND_ID_2, DEMAND_TYPE_ID_2 );
        collectionNotificationStored = _notificationDAO.loadByDemand( DEMAND_ID_2, DEMAND_TYPE_ID_2 );
        assertEquals( collectionNotificationStored.size( ), 0 );

        _demandDAO.delete( DEMAND_ID_1, DEMAND_TYPE_ID_1 );
        _demandDAO.delete( DEMAND_ID_2, DEMAND_TYPE_ID_2 );
    }
}
