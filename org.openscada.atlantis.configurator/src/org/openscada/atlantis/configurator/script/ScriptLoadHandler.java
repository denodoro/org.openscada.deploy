package org.openscada.atlantis.configurator.script;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.openscada.atlantis.configurator.Configuration;
import org.openscada.atlantis.configurator.GenericMasterConfiguration;
import org.openscada.atlantis.configurator.common.RowHandler;
import org.openscada.deploy.iolist.model.FormulaInput;
import org.openscada.deploy.iolist.model.Item;
import org.openscada.deploy.iolist.model.ModelFactory;
import org.openscada.deploy.iolist.model.ScriptItem;
import org.openscada.deploy.iolist.model.ScriptOutput;

public class ScriptLoadHandler implements RowHandler
{

    private final Collection<ScriptItem> items = new ArrayList<ScriptItem> ();

    private final File scriptBase;

    public ScriptLoadHandler ( final GenericMasterConfiguration cfg, final File scriptBase )
    {
        this.scriptBase = scriptBase;
    }

    @Override
    public void handleRow ( final int rowNumber, final Map<String, String> rowData )
    {
        final String alias = rowData.get ( "ITEM_ID" );
        final String scriptEngine = rowData.get ( "SCRIPT_ENGINE" );
        final String initScript = getScript ( rowData.get ( "INIT_SCRIPT" ) );
        final String updateScript = getScript ( rowData.get ( "UPDATE_SCRIPT" ) );
        final String timeoutScript = getScript ( rowData.get ( "TIMER_SCRIPT" ) );
        final String timerString = rowData.get ( "TIMER" );
        final String description = rowData.get ( "DESCRIPTION" );

        Long timer;
        if ( timerString != null && timerString.length () > 0 )
        {
            timer = (long)Double.parseDouble ( timerString );
        }
        else
        {
            timer = null;
        }

        /*
        final Map<String, String> dataSources = new HashMap<String, String> ();
        {
            // get inputs
            final String dataSource = rowData.get ( "INPUTS" );
            final String[] toks = dataSource.split ( "[, \n\r]+" );
            for ( int i = 0; i < toks.length; i++ )
            {
                final String[] subToks = toks[i].split ( "=", 2 );
                if ( subToks.length > 1 )
                {
                    dataSources.put ( subToks[0], subToks[1] );
                }
                else
                {
                    dataSources.put ( "source" + i, toks[i] );
                }
            }
        }

        final Set<String> outputs = new HashSet<String> ();
        {
            // get outputs
            final String[] toks = rowData.get ( "OUTPUTS" ).split ( "[, \n\t]+" );
            outputs.addAll ( Arrays.asList ( toks ) );
        }*/

        //  final String id = alias + ".script";

        // this.cfg.addScript ( id, scriptEngine, dataSources, outputs, initScript, updateScript, timeoutScript, timer );

        final ScriptItem item = createItem ( alias );

        item.setScriptEngine ( scriptEngine );
        item.setInitScript ( initScript );
        item.setUpdateScript ( updateScript );
        item.setTimerScript ( timeoutScript );
        item.setTimerPeriod ( timer );
        item.setDescription ( description );

        {
            // get outputs
            final String dataSource = rowData.get ( "OUTPUTS" );
            if ( dataSource != null && !dataSource.isEmpty () )
            {
                final String[] toks = dataSource.split ( "[, \n\r]+" );
                for ( int i = 0; i < toks.length; i++ )
                {
                    final String[] subToks = toks[i].split ( "=", 2 );
                    final ScriptOutput output = ModelFactory.eINSTANCE.createScriptOutput ();
                    if ( subToks.length > 1 )
                    {
                        output.setDatasourceId ( subToks[1] );
                        output.setName ( subToks[0] );
                    }
                    else
                    {
                        output.setDatasourceId ( toks[i] );
                        output.setName ( toks[i] );
                    }
                    item.getOutputs ().add ( output );
                }
            }
        }

        {
            // get inputs
            final String dataSource = rowData.get ( "INPUTS" );
            if ( dataSource != null && !dataSource.isEmpty () )
            {
                final String[] toks = dataSource.split ( "[, \n\r]+" );
                for ( int i = 0; i < toks.length; i++ )
                {
                    final String[] subToks = toks[i].split ( "=", 2 );
                    final FormulaInput input = ModelFactory.eINSTANCE.createFormulaInput ();
                    if ( subToks.length > 1 )
                    {
                        input.setDatasourceId ( subToks[1] );
                        input.setName ( subToks[0] );
                    }
                    else
                    {
                        input.setDatasourceId ( toks[i] );
                        input.setName ( "source" + i );
                    }
                    item.getInputs ().add ( input );
                }
            }
        }

        this.items.add ( item );
    }

    private String getScript ( final String string )
    {
        if ( string == null )
        {
            return null;
        }

        if ( !string.startsWith ( "@" ) )
        {
            return string;
        }

        try
        {
            return Configuration.loadFromFile ( new File ( this.scriptBase, string.substring ( 1 ) ) );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException ( e );
        }
    }

    private ScriptItem createItem ( final String alias )
    {
        final ScriptItem item = ModelFactory.eINSTANCE.createScriptItem ();
        item.setAlias ( alias );
        item.setDevice ( null );
        item.setName ( alias + ".script" );
        item.setLocalHighAvailable ( true );
        item.setLocalLowAvailable ( true );
        item.setLocalHighHighAvailable ( true );
        item.setLocalLowLowAvailable ( true );
        item.setSystem ( "SCRIPT" );
        item.setComponent ( "" );
        item.setDevice ( "" );
        item.setLocation ( "" );
        item.setDescription ( "" );
        return item;
    }

    public Collection<? extends Item> getItems ()
    {
        return this.items;
    }

}
