package org.openscada.atlantis.configurator.loop;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MultiSourceAttributeHandler extends NoOpHandler implements LoopHandler
{
    private final String attributePrefix;

    public MultiSourceAttributeHandler ( final String attributePrefix )
    {
        this.attributePrefix = attributePrefix;
    }

    @Override
    public Set<DataSourceDescriptor> getNode ( final String configurationId, final Map<String, Object> parameters )
    {
        final DataSourceDescriptor desc = createNode ( configurationId, parameters );

        return new HashSet<DataSourceDescriptor> ( Arrays.asList ( desc ) );
    }

    protected DataSourceDescriptor createNode ( final String configurationId, final Map<String, Object> parameters )
    {
        final DataSourceDescriptor desc = new DataSourceDescriptor ( configurationId );

        for ( final Map.Entry<String, Object> entry : parameters.entrySet () )
        {
            if ( !entry.getKey ().startsWith ( this.attributePrefix ) )
            {
                continue;
            }

            if ( ! ( entry.getValue () instanceof String ) )
            {
                continue;
            }

            if ( ( (String)entry.getValue () ).isEmpty () )
            {
                continue;
            }

            desc.addReference ( (String)entry.getValue () );
        }
        return desc;
    }

    @Override
    public boolean providesDescriptors ()
    {
        return true;
    }

}