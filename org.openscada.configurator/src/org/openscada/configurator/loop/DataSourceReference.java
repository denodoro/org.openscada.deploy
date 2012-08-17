package org.openscada.configurator.loop;

public class DataSourceReference
{
    private final String id;

    private final String type;

    public DataSourceReference ( final String type, final String id )
    {
        this.type = type;
        this.id = id;
    }

    public String getType ()
    {
        return this.type;
    }

    public String getId ()
    {
        return this.id;
    }

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( this.id == null ? 0 : this.id.hashCode () );
        result = prime * result + ( this.type == null ? 0 : this.type.hashCode () );
        return result;
    }

    @Override
    public boolean equals ( final Object obj )
    {
        if ( this == obj )
        {
            return true;
        }
        if ( obj == null )
        {
            return false;
        }
        if ( ! ( obj instanceof DataSourceReference ) )
        {
            return false;
        }
        final DataSourceReference other = (DataSourceReference)obj;
        if ( this.id == null )
        {
            if ( other.id != null )
            {
                return false;
            }
        }
        else if ( !this.id.equals ( other.id ) )
        {
            return false;
        }
        if ( this.type == null )
        {
            if ( other.type != null )
            {
                return false;
            }
        }
        else if ( !this.type.equals ( other.type ) )
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString ()
    {
        return this.type + "@" + this.id;
    }

}
