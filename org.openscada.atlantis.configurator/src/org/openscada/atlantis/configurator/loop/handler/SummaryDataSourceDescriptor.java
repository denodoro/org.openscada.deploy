package org.openscada.atlantis.configurator.loop.handler;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openscada.atlantis.configurator.loop.DataSourceDescriptor;

public class SummaryDataSourceDescriptor extends DataSourceDescriptor
{

    private final Set<String> blacklist;

    private final boolean masterOnly;

    public SummaryDataSourceDescriptor ( final String id, final Set<String> blacklist, final boolean masterOnly )
    {
        super ( "datasource", id );
        this.blacklist = new HashSet<String> ( blacklist );
        this.masterOnly = masterOnly;
    }

    public Set<String> getBlacklist ()
    {
        return Collections.unmodifiableSet ( this.blacklist );
    }

    public boolean isMasterOnly ()
    {
        return this.masterOnly;
    }
}
