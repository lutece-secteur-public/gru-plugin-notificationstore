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
package fr.paris.lutece.plugins.notificationstore.web.rs.swagger;

/**
 * Rest Constants
 */
public final class SwaggerConstants
{
    public static final String API_PATH = "notificationstore/api";
    public static final String VERSION_PATH = "/v{" + SwaggerConstants.VERSION + "}";
    public static final String ID_PATH = "/{" + SwaggerConstants.ID + "}";
    public static final String FILE_NAME_PATH = "/{" + SwaggerConstants.FILE_NAME + ": .*\\.*}";
    public static final String FILE_NAME = "filename";
    public static final String VERSION = "version";
    public static final String ID = "id";
    public static final String SWAGGER_DIRECTORY_PATH = "/plugins/";
    public static final String SWAGGER_PATH = "/swagger";
    public static final String SWAGGER_VERSION_PATH = "/v";
    public static final String SWAGGER_REST_PATH = "rest/";
    public static final String SWAGGER_JSON = "/swagger.json";
    public static final String EMPTY_OBJECT = "{}";
    public static final String ERROR_NOT_FOUND_VERSION = "Version not found";
    public static final String ERROR_NOT_FOUND_RESOURCE = "Resource not found";
    public static final String ERROR_BAD_REQUEST_EMPTY_PARAMETER = "Empty parameter";
    

	public static final String QUERY_PARAM_ID_DEMAND_DESCRIPTION = "Demand id";
	public static final String QUERY_PARAM_ID_DEMAND_TYPE_DESCRIPTION = "Demand type id";
	public static final String QUERY_PARAM_INDEX_DESCRIPTION = "Index of pagination";
	public static final String QUERY_PARAM_LIMIT_DESCRIPTION = "Max of responses requested";
	public static final String QUERY_PARAM_CUSTOMER_ID_DESCRIPTION = "Id of the customer";
	public static final String QUERY_PARAM_NOTIFICATION_TYPE_DESCRIPTION = "Type of notification";
	public static final String QUERY_PARAM_LIST_STATUS_DESCRIPTION = "List of requested status";
	public static final String QUERY_PARAM_CATEGORY_CODE_DESCRIPTION = "List of requested category codes";
	public static final String QUERY_PARAM_DIRECT_MODE_DESCRIPTION = "(Deprecated)";
	public static final String QUERY_PARAM_ID_CATEGORY_DESCRIPTION = "Category id";

    /**
     * private constructor
     */
    private SwaggerConstants( )
    {

    }
}
