package fr.paris.lutece.plugins.notificationstore.service;

import java.util.List;

import fr.paris.lutece.plugins.grubusiness.business.demand.DemandType;
import fr.paris.lutece.plugins.notificationstore.business.DemandTypeHome;

public class DemandTypeService
{

    /**
     * get demand types
     * 
     * @return list of demand types
     */
    public List<DemandType> getDemandTypes( )
    {
        return DemandTypeHome.getDemandTypesList( );
    }

}
