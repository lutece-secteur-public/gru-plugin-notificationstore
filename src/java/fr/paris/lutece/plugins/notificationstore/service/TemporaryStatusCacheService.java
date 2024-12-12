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
package fr.paris.lutece.plugins.notificationstore.service;

import java.util.List;

import fr.paris.lutece.plugins.grubusiness.business.demand.TemporaryStatus;
import fr.paris.lutece.plugins.notificationstore.business.TemporaryStatusHome;
import fr.paris.lutece.portal.service.cache.AbstractCacheableService;

/**
 * 
 * TemporaryStatusCacheService
 *
 */
public class TemporaryStatusCacheService extends AbstractCacheableService
{
    private static final String                SERVICE_NAME         = "temporaryStatusCacheService";
    private static final String                KEY_TEMPORARY_STATUS = "[temporaryStatus]";

    private static TemporaryStatusCacheService _singleton;

    /**
     * Private constructor
     */
    private TemporaryStatusCacheService( )
    {
        initCache( );
    }

    /**
     * Public constructor
     * 
     * @return instance
     */
    public static TemporaryStatusCacheService getInstance( )
    {
        if ( _singleton == null )
        {
            _singleton = new TemporaryStatusCacheService( );
        }
        return _singleton;
    }

    @Override
    public String getName( )
    {
        return SERVICE_NAME;
    }

    /**
     * Gets list temporary status
     * 
     * @return list of temporary status
     */
    public List<TemporaryStatus> getList( )
    {
        List<TemporaryStatus> listTemporaryStatus = ( List<TemporaryStatus> ) getFromCache( KEY_TEMPORARY_STATUS );

        if ( listTemporaryStatus == null || listTemporaryStatus.isEmpty( ) )
        {
            listTemporaryStatus = TemporaryStatusHome.getStatusList( );

            putInCache( KEY_TEMPORARY_STATUS, listTemporaryStatus );
        }

        return listTemporaryStatus;
    }

    /**
     * Remove cache
     */
    public void removeCache( )
    {
        removeKey( KEY_TEMPORARY_STATUS );
    }
}
