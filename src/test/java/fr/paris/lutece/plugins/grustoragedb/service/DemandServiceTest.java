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
package fr.paris.lutece.plugins.grustoragedb.service;

import org.junit.Test;

import fr.paris.lutece.plugins.grubusiness.business.customer.Customer;
import fr.paris.lutece.plugins.grubusiness.business.demand.Demand;
import fr.paris.lutece.plugins.grubusiness.business.mock.MockActionListenerEnum;
import fr.paris.lutece.plugins.grubusiness.business.mock.MockDemandDAO;
import fr.paris.lutece.plugins.grubusiness.business.mock.MockDemandListener;
import fr.paris.lutece.plugins.grubusiness.business.mock.MockNotificationDAO;
import fr.paris.lutece.plugins.grubusiness.business.mock.MockNotificationListener;
import fr.paris.lutece.plugins.notificationstore.service.DemandService;
import junit.framework.TestCase;

/**
 *
 */
public class DemandServiceTest extends TestCase
{
    private MockDemandDAO _demandDAO;
    private MockNotificationDAO _notificationDAO;
    private MockDemandListener _demandListener;
    private MockNotificationListener _notificationListener;

    /**
     * init des services et objet
     */
    public DemandServiceTest( )
    {
        super( );
        _demandDAO = new MockDemandDAO( );
        _notificationDAO = new MockNotificationDAO( );
        _demandListener = new MockDemandListener( );
        _notificationListener = new MockNotificationListener( );
    }

    @Test
    public void testDemandServiceWithoutListener( )
    {

        DemandService serviceTest = new DemandService( );

        serviceTest.setDemandDao( _demandDAO );
        serviceTest.setNotificationDao( _notificationDAO );

        // Ici on a besoin que des infos pour les DAOs
        // Demand.typeId
        // Demand.id
        // Demand.reference
        // Demand.Customer.id
        // Notification.Demand avec la Demand comme ci dessus

        Customer customer1 = new Customer( );
        customer1.setCustomerId( "cust_1" );

        Demand demand1 = new Demand( );
        demand1.setTitle( "type_1" );
        demand1.setId( "id_1" );
        demand1.setReference( "ref_1" );
        demand1.setCustomer( customer1 );

        Demand demand2 = new Demand( );
        demand2.setTitle( "type_2" );
        demand2.setId( "id_2" );
        demand2.setReference( "ref_2" );
        demand2.setCustomer( customer1 );

        // creation demand1
        serviceTest.create( demand1 );
        assertFalse( _demandListener.listenAndConsume( MockActionListenerEnum.CREATE, demand1 ) );
        assertFalse( _demandListener.listenAndConsume( MockActionListenerEnum.UPDATE, demand1 ) );
        assertFalse( _demandListener.listenAndConsume( MockActionListenerEnum.DELETE, demand1 ) );
        assertEquals( serviceTest.findByCustomerId( customer1.getCustomerId( ) ).size( ), 1 );
        assertNotNull( serviceTest.findByPrimaryKey( demand1.getId( ), demand1.getTypeId( ), customer1.getCustomerId( ) ) );
        assertNull( serviceTest.findByPrimaryKey( demand2.getId( ), demand2.getTypeId( ), customer1.getCustomerId( ) ) );
        assertEquals( serviceTest.findByReference( demand1.getReference( ) ).size( ), 1 );
        assertEquals( serviceTest.findByReference( demand2.getReference( ) ).size( ), 0 );

        // creation demand2
        serviceTest.create( demand2 );
        assertFalse( _demandListener.listenAndConsume( MockActionListenerEnum.CREATE, demand2 ) );
        assertFalse( _demandListener.listenAndConsume( MockActionListenerEnum.UPDATE, demand2 ) );
        assertFalse( _demandListener.listenAndConsume( MockActionListenerEnum.DELETE, demand2 ) );
        assertEquals( serviceTest.findByCustomerId( customer1.getCustomerId( ) ).size( ), 2 );
        assertNotNull( serviceTest.findByPrimaryKey( demand1.getId( ), demand1.getTypeId( ), customer1.getCustomerId( ) ) );
        assertNotNull( serviceTest.findByPrimaryKey( demand2.getId( ), demand2.getTypeId( ), customer1.getCustomerId( ) ) );
        assertEquals( serviceTest.findByReference( demand1.getReference( ) ).size( ), 1 );
        assertEquals( serviceTest.findByReference( demand2.getReference( ) ).size( ), 1 );

        // suppression demand1
        serviceTest.remove( demand1.getId( ), demand1.getTypeId( ), customer1.getCustomerId( ) );
        assertFalse( _demandListener.listenAndConsume( MockActionListenerEnum.CREATE, demand1 ) );
        assertFalse( _demandListener.listenAndConsume( MockActionListenerEnum.UPDATE, demand1 ) );
        assertFalse( _demandListener.listenAndConsume( MockActionListenerEnum.DELETE, demand1 ) );
        assertEquals( serviceTest.findByCustomerId( customer1.getCustomerId( ) ).size( ), 1 );
        assertNull( serviceTest.findByPrimaryKey( demand1.getId( ), demand1.getTypeId( ), customer1.getCustomerId( ) ) );
        assertNotNull( serviceTest.findByPrimaryKey( demand2.getId( ), demand2.getTypeId( ), customer1.getCustomerId( ) ) );
        assertEquals( serviceTest.findByReference( demand1.getReference( ) ).size( ), 0 );
        assertEquals( serviceTest.findByReference( demand2.getReference( ) ).size( ), 1 );

        // maj ref sur demand2, attention la cle primaire n'est pas modifiable
        String strOldRef = demand2.getReference( );
        demand2.setReference( "ref_new_2" );
        serviceTest.update( demand2 );
        assertFalse( _demandListener.listenAndConsume( MockActionListenerEnum.CREATE, demand2 ) );
        assertFalse( _demandListener.listenAndConsume( MockActionListenerEnum.UPDATE, demand2 ) );
        assertFalse( _demandListener.listenAndConsume( MockActionListenerEnum.DELETE, demand2 ) );
        assertEquals( serviceTest.findByCustomerId( customer1.getCustomerId( ) ).size( ), 1 );
        assertNull( serviceTest.findByPrimaryKey( demand1.getId( ), demand1.getTypeId( ), customer1.getCustomerId( ) ) );
        assertNotNull( serviceTest.findByPrimaryKey( demand2.getId( ), demand2.getTypeId( ), customer1.getCustomerId( ) ) );
        assertEquals( serviceTest.findByReference( demand1.getReference( ) ).size( ), 0 );
        assertEquals( serviceTest.findByReference( demand2.getReference( ) ).size( ), 1 );
        assertEquals( serviceTest.findByReference( strOldRef ).size( ), 0 );
    }

    @Test
    public void testDemandServiceWithListener( )
    {
        // TODO : refactor test

        /*
         * List<IDemandListener> listDemandListener = new ArrayList<IDemandListener>( ); listDemandListener.add( _demandListener ); List<INotificationListener>
         * listNotificationListener = new ArrayList<INotificationListener>( ); listNotificationListener.add( _notificationListener );
         * 
         * DemandService serviceTest = new DemandService( ); serviceTest.setDemandDao(_demandDAO); serviceTest.setNotificationDao(_notificationDAO);
         * 
         * SpringContextService.getContext( ).registerBean( MockDemandListener.class ); serviceTest.setDemandListeners( listDemandListener );
         * serviceTest.setNotificationListeners( listNotificationListener );
         * 
         * 
         * // Ici on a besoin que des infos pour les DAOs // Demand.typeId // Demand.id // Demand.reference // Demand.Customer.id // Notification.Demand avec la
         * Demand comme ci dessus
         * 
         * Customer customer1 = new Customer( ); customer1.setId( "cust_1" );
         * 
         * Demand demand1 = new Demand( ); demand1.setTitle( "type_1" ); demand1.setId( "id_1" ); demand1.setReference( "ref_1" ); demand1.setCustomer(
         * customer1 );
         * 
         * Demand demand2 = new Demand( ); demand2.setTitle( "type_2" ); demand2.setId( "id_2" ); demand2.setReference( "ref_2" ); demand2.setCustomer(
         * customer1 );
         * 
         * // creation demand1 serviceTest.create( demand1 ); assertTrue( _demandListener.listenAndConsume( MockActionListenerEnum.CREATE, demand1 ) );
         * assertFalse( _demandListener.listenAndConsume( MockActionListenerEnum.UPDATE, demand1 ) ); assertFalse( _demandListener.listenAndConsume(
         * MockActionListenerEnum.DELETE, demand1 ) ); assertEquals( serviceTest.findByCustomerId( customer1.getId( ) ).size( ), 1 ); assertNotNull(
         * serviceTest.findByPrimaryKey( demand1.getId( ), demand1.getTypeId( ) ) ); assertNull( serviceTest.findByPrimaryKey( demand2.getId( ),
         * demand2.getTypeId( ) ) ); assertEquals( serviceTest.findByReference( demand1.getReference( ) ).size( ), 1 ); assertEquals(
         * serviceTest.findByReference( demand2.getReference( ) ).size( ), 0 );
         * 
         * // creation demand2 serviceTest.create( demand2 ); assertTrue( _demandListener.listenAndConsume( MockActionListenerEnum.CREATE, demand2 ) );
         * assertFalse( _demandListener.listenAndConsume( MockActionListenerEnum.UPDATE, demand2 ) ); assertFalse( _demandListener.listenAndConsume(
         * MockActionListenerEnum.DELETE, demand2 ) ); assertEquals( serviceTest.findByCustomerId( customer1.getId( ) ).size( ), 2 ); assertNotNull(
         * serviceTest.findByPrimaryKey( demand1.getId( ), demand1.getTypeId( ) ) ); assertNotNull( serviceTest.findByPrimaryKey( demand2.getId( ),
         * demand2.getTypeId( ) ) ); assertEquals( serviceTest.findByReference( demand1.getReference( ) ).size( ), 1 ); assertEquals(
         * serviceTest.findByReference( demand2.getReference( ) ).size( ), 1 );
         * 
         * // suppression demand1 serviceTest.remove( demand1.getId( ), demand1.getTypeId( ) ); assertFalse( _demandListener.listenAndConsume(
         * MockActionListenerEnum.CREATE, demand1 ) ); assertFalse( _demandListener.listenAndConsume( MockActionListenerEnum.UPDATE, demand1 ) ); assertTrue(
         * _demandListener.listenAndConsume( MockActionListenerEnum.DELETE, demand1 ) ); assertEquals( serviceTest.findByCustomerId( customer1.getId( ) ).size(
         * ), 1 ); assertNull( serviceTest.findByPrimaryKey( demand1.getId( ), demand1.getTypeId( ) ) ); assertNotNull( serviceTest.findByPrimaryKey(
         * demand2.getId( ), demand2.getTypeId( ) ) ); assertEquals( serviceTest.findByReference( demand1.getReference( ) ).size( ), 0 ); assertEquals(
         * serviceTest.findByReference( demand2.getReference( ) ).size( ), 1 );
         * 
         * // maj ref sur demand2, attention la cle primaire n'est pas modifiable String strOldRef = demand2.getReference( ); demand2.setReference( "ref_new_2"
         * ); serviceTest.update( demand2 ); assertFalse( _demandListener.listenAndConsume( MockActionListenerEnum.CREATE, demand2 ) ); assertTrue(
         * _demandListener.listenAndConsume( MockActionListenerEnum.UPDATE, demand2 ) ); assertFalse( _demandListener.listenAndConsume(
         * MockActionListenerEnum.DELETE, demand2 ) ); assertEquals( serviceTest.findByCustomerId( customer1.getId( ) ).size( ), 1 ); assertNull(
         * serviceTest.findByPrimaryKey( demand1.getId( ), demand1.getTypeId( ) ) ); assertNotNull( serviceTest.findByPrimaryKey( demand2.getId( ),
         * demand2.getTypeId( ) ) ); assertEquals( serviceTest.findByReference( demand1.getReference( ) ).size( ), 0 ); assertEquals(
         * serviceTest.findByReference( demand2.getReference( ) ).size( ), 1 ); assertEquals( serviceTest.findByReference( strOldRef ).size( ), 0 );
         * 
         */
    }

}
