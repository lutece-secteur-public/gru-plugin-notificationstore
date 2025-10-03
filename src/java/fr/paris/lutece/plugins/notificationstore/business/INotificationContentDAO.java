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
package fr.paris.lutece.plugins.notificationstore.business;

import fr.paris.lutece.plugins.grubusiness.business.notification.EnumNotificationType;
import fr.paris.lutece.portal.service.plugin.Plugin;
import java.util.List;

/**
 * INotificationContentDAO Interface
 */

public interface INotificationContentDAO
{

    /**
     * Insert a new record in the table.
     * 
     * @param notificationContent
     *            instance of the NotificationContent object to inssert
     * @param plugin
     *            the Plugin
     */

    void insert( NotificationContent notificationContent, Plugin plugin );

    /**
     * Update the record in the table
     * 
     * @param notificationContent
     *            the reference of the NotificationContent
     * @param plugin
     *            the Plugin
     */

    void store( NotificationContent notificationContent, Plugin plugin );

    /**
     * Update nofications status
     * 
     * @param nNewStatusId
     *            the new status id
     * @param nTemporaryStatusId
     *            This nTemporaryStatusId is allows to filter on the notifications to be updated
     */

    void updateStatusId( int nNewStatusId, int nTemporaryStatusId, Plugin plugin );

    /**
     * Delete a record from the table
     * 
     * @param nIdNotificationContent
     *            int identifier of the NotificationContent to delete
     * @param plugin
     *            the Plugin
     */

    void delete( int nIdNotificationContent, Plugin plugin );

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Load the data from the table
     * 
     * @param strId
     *            The identifier of the notificationContent
     * @param plugin
     *            the Plugin
     * @return The instance of the notificationContent
     */

    NotificationContent load( int nKey, Plugin plugin );

    /**
     * Load the data of all the notificationContent objects and returns them as a List
     * 
     * @param plugin
     *            the Plugin
     * @return The List which contains the data of all the notificationContent objects
     */

    List<NotificationContent> selectNotificationContentsList( Plugin plugin );

    /**
     * Load the data by id notification and returns them as a List
     * 
     * @param nIdNotification
     *            the id notification
     * @return The List which contains the data of all the notificationContent objects
     */

    List<NotificationContent> selectNotificationContentsByIdNotification( int nIdNotification, Plugin plugin );

    /**
     * Load the data by id notification and notification type
     * 
     * @param nIdNotification
     *            the id notification
     * @param listNotificationType
     *            the list of notification types
     * @return The List which contains the data of all the notificationContent objects
     */

    List<NotificationContent> selectNotificationContentsByIdAndTypeNotification( int nIdNotification, List<EnumNotificationType> listNotificationType,
            Plugin plugin );

}
