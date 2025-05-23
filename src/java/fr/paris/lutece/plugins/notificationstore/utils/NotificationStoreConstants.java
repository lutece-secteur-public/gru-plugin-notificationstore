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
package fr.paris.lutece.plugins.notificationstore.utils;

public class NotificationStoreConstants
{
    // REST CONSTANTS
    public static final String PLUGIN_NAME = "notificationstore";

    public static final String PATH_DEMAND = "/demand";
    public static final String PATH_NOTIFICATION = "/notification";
    public static final String PATH_NOTIFICATION_EVENT = "/notificationEvent";
    public static final String PATH_DEMAND_TYPE = "/demandType";
    public static final String PATH_DEMAND_STATUS = "/status";
    public static final String PATH_GENERIC_STATUS = "/genericStatus";
    public static final String PATH_CATEGORY = "/category";
    public static final String PATH_REASSIGN = "/reassign";

    public static final String PATH_LIST = "/list";
    public static final String PATH_TYPE_NOTIFICATION = "notificationType";
    public static final String PATH_ID = "/{" + NotificationStoreConstants.ID + "}";
    public static final String PATH_CUSTOMER_ID = "/{" + NotificationStoreConstants.QUERY_PARAM_CUSTOMER_ID + "}";
    
    public static final String ID = "id";

    public static final String QUERY_PARAM_INDEX = "index";
    public static final String QUERY_PARAM_LIMIT = "limitResult";
    public static final String QUERY_PARAM_ID_DEMAND_TYPE = "idDemandType";
    public static final String QUERY_PARAM_CUSTOMER_ID = "customerId";
    public static final String QUERY_PARAM_NOTIFICATION_TYPE = "notificationType";
    public static final String QUERY_PARAM_ID_DEMAND = "idDemand";
    public static final String QUERY_PARAM_LIST_STATUS = "listStatus";
    public static final String QUERY_PARAM_CATEGORY_CODE = "categoryCode";
    public static final String QUERY_PARAM_DIRECT_MODE = "direct";
    
    //Demand type attributes
    public static final String DEMANDTYPE_ATTRIBUTE_ID_DEMAND_TYPE = "id_demand_type";
    public static final String DEMANDTYPE_ATTRIBUTE_LABEL = "label";
    public static final String DEMANDTYPE_ATTRIBUTE_URL = "url";
    public static final String DEMANDTYPE_ATTRIBUTE_APP_CODE = "app_code";
    public static final String DEMANDTYPE_ATTRIBUTE_CATEGORY = "category";
    
    //Demand category attributes
    public static final String DEMANDCATEGORY_ATTRIBUTE_CODE = "code";
    public static final String DEMANDCATEGORY_ATTRIBUTE_LABEL = "label";
    public static final String DEMANDCATEGORY_ATTRIBUTE_DEFAULT = "isDefault";
    
    // EXIT STATUS
    public static final String STATUS_201 = "{" + "\"status\":" + "\"201\"" + "}";
    public static final String STATUS_404 = "{" + "\"status\":" + "\"404\"" + "}";

    // constants
    public static final int DEFAULT_INT = -1;
    public static final String VERSION_PATH_V3 = "/v3";

    // PROPERTIES
    /** The Constant LIMIT_DEMAND_API_REST. */
    public static final String LIMIT_DEMAND_API_REST = "notificationstore.api.rest.limit.demand";
    public static final String PROPERTY_COMPRESS_NOTIFICATION = "notificationstore.notification.compress";
    public static final String PROPERTY_CONSIDER_GUID_AS_CUSTOMER_ID = "notificationstore.notification.considerGuidAsCuid";

    // MESSAGE
    public static final String MESSAGE_ERROR_DEMAND = "Parameter customerId is mandatory";
    public static final String MESSAGE_ERROR_STATUS = "Parameters customerId and listStatus are mandatory ( can separated status by ,)";
    public static final String MESSAGE_ERROR_NOTIF = "Parameters idDemand, customerId and idDemandType are mandatory ";
    public static final String MESSAGE_ALL_REQUIRED = "All attributes are required";
    public static final String MESSAGE_ERROR_NOT_FOUND_RESOURCE = "Resource not found";
    public static final String MESSAGE_ERROR_DEMAND_TYPE_ID_USED = "This DemandTypeId is used by existing notifications";
    public static final String MESSAGE_ERROR_BAD_REQUEST_EMPTY_PARAMETER = "Empty parameter";
    
    // Constants
    public static final String CHARECTER_REGEXP_FILTER = "[^\\p{L}\\p{M}\\p{N}\\p{P}\\p{Z}\\p{Cf}\\p{Cs}\\p{Sm}\\p{Sc}\\s]";
    
    
    //FileStoreProvider
    public static final String FILE_STORE_PROVIDER = "notificationStoreDatabaseFileStoreProvider";

}
