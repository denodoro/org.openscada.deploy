package org.openscada.atlantis.configurator;

import java.util.HashMap;
import java.util.Map;

import org.openscada.atlantis.configurator.common.RowHandler;

public class EventFilterHandler implements RowHandler
{
    private final Configuration configuration;

    public EventFilterHandler ( final Configuration configuration )
    {
        this.configuration = configuration;
    }

    @Override
    public void handleRow ( final int rowNumber, final Map<String, String> rowData )
    {
        final String id = rowData.get ( "ID" );

        if ( id == null || id.isEmpty () )
        {
            return;
        }

        final long priority = Long.parseLong ( rowData.get ( "PRIORITY" ), 10 );

        final Map<String, Object> properties = new HashMap<String, Object> ();

        final String type = rowData.get ( "TYPE" ).isEmpty () ? null : rowData.get ( "TYPE" );
        final String data = rowData.get ( "DATA" ).isEmpty () ? null : rowData.get ( "DATA" );

        if ( "static".equals ( type ) )
        {
            properties.put ( "value", Boolean.parseBoolean ( data ) );
        }
        else if ( "filter".equals ( type ) )
        {
            properties.put ( "filter", data );
        }
        else
        {
            throw new RuntimeException ( String.format ( "Invalid filter type '%s'", type ) );
        }

        this.configuration.addEventFilter ( id, priority, type, properties );
    }

}
