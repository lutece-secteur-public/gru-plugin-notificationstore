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

/**
 * This is the business class for the object NotificationContent
 */
public class NotificationContent
{
    // Variables declarations
    private int _nId;
    private int _strIdNotification;
    private String _strNotificationType;
    private Integer _strIdTemporaryStatus;
    private Integer _strStatusId;
    private String _strFileKey;
    private String _strFileStore;

    /**
     * Returns the Id
     * 
     * @return The Id
     */
    public int getId( )
    {
        return _nId;
    }

    /**
     * Sets the Id
     * 
     * @param nId
     *            The Id
     */
    public void setId( int nId )
    {
        _nId = nId;
    }

    /**
     * @return the _strIdNotification
     */
    public int getIdNotification( )
    {
        return _strIdNotification;
    }

    /**
     * @param strIdNotification
     *            the strIdNotification to set
     */
    public void setIdNotification( int strIdNotification )
    {
        this._strIdNotification = strIdNotification;
    }

    /**
     * Returns the NotificationType
     * 
     * @return The NotificationType
     */
    public String getNotificationType( )
    {
        return _strNotificationType;
    }

    /**
     * Sets the NotificationType
     * 
     * @param strNotificationType
     *            The NotificationType
     */
    public void setNotificationType( String strNotificationType )
    {
        _strNotificationType = strNotificationType;
    }

    /**
     * @return the _strIdTemporaryStatus
     */
    public Integer getIdTemporaryStatus( )
    {
        return _strIdTemporaryStatus;
    }

    /**
     * @param strIdTemporaryStatus
     *            the _strIdTemporaryStatus to set
     */
    public void setIdTemporaryStatus( Integer strIdTemporaryStatus )
    {
        this._strIdTemporaryStatus = strIdTemporaryStatus;
    }

    /**
     * @return the _strStatusId
     */
    public Integer getStatusId( )
    {
        return _strStatusId;
    }

    /**
     * @param strStatusId
     *            the _strStatusId to set
     */
    public void setStatusId( Integer strStatusId )
    {
        this._strStatusId = strStatusId;
    }

    /**
     * @return the _strFileKey
     */
    public String getFileKey( )
    {
        return _strFileKey;
    }

    /**
     * @param strFileKey
     *            the _strFileKey to set
     */
    public void setFileKey( String strFileKey )
    {
        this._strFileKey = strFileKey;
    }

    /**
     * @return the _strFileStore
     */
    public String getFileStore( )
    {
        return _strFileStore;
    }

    /**
     * @param strFileStore
     *            the _strFileStore to set
     */
    public void setFileStore( String strFileStore )
    {
        this._strFileStore = strFileStore;
    }

}
