package org.openscada.atlantis.configurator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import javax.script.ScriptException;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.openscada.atlantis.configurator.common.DataLoader;
import org.openscada.atlantis.configurator.summary.SumLoader;
import org.openscada.deploy.iolist.model.Item;
import org.openscada.deploy.iolist.utils.SpreadSheetPoiHelper;

public class Application implements IApplication
{

    @Override
    public Object start ( final IApplicationContext context ) throws Exception
    {
        final String[] args = (String[])context.getArguments ().get ( IApplicationContext.APPLICATION_ARGS );

        try
        {
            process ( args );
        }
        catch ( final DuplicateItemsException e )
        {
            System.err.println ( "Duplicate items:" );
            for ( final Item item : e.getItems () )
            {
                System.err.println ( String.format ( " -> '%s' (%s)", item.getAlias (), item.getDebugInformation () ) );
            }
        }

        return null;
    }

    @Override
    public void stop ()
    {
    }

    private static void process ( final String[] args ) throws Exception
    {
        final LinkedList<String> arguments = new LinkedList<String> ( Arrays.asList ( args ) );

        final File scriptDBase = new File ( arguments.pop () );
        final File base = new File ( arguments.pop () );
        final File scriptBase = new File ( base, "script/js" );
        final File generatedBase = new File ( base, "generated" );

        final Configuration cfg = new Configuration ();

        final String outFile = arguments.pop ();
        final String basicsFile = arguments.pop ();

        System.out.println ( "** 0 - Loading scripts" );
        System.out.println ( "*** 0a - Script Loader" );
        arguments.push ( org.openscada.atlantis.configurator.summary.ScriptLoader.loadScripts ( cfg, new File ( base, "input/formulas.ods" ), new File ( generatedBase, "IOList-generated-script.xls" ) ) );
        System.out.println ( "*** 0b - Formulas Loader" );
        arguments.push ( ScriptLoader.loadScript ( cfg, new File ( base, "input/PARSERformulas" ), scriptBase, new File ( generatedBase, "IOList-generated-tams-script.xls" ) ) );
        System.out.println ( "*** 0c - Summary groups" );
        arguments.push ( SumLoader.convertGroups ( cfg, new File ( base, "input/summary.ods" ), new File ( generatedBase, "IOList-generated-sum.xls" ) ) );

        arguments.push ( processNetwork ( System.getProperty ( "prefix", "XXX" ), base, generatedBase ) );

        final Collection<String> overrides = new LinkedList<String> ();

        System.out.println ( "** 1 - Loading data" );
        System.out.println ( "*** 1a - Loading basic configuration" );
        loadBasics ( cfg, new File ( basicsFile ) );

        System.out.println ( "*** 1b - Loading data files" );
        for ( final String file : arguments )
        {
            if ( !file.contains ( "override" ) )
            {
                System.out.println ( " ** Loading: " + file );
                cfg.addItems ( SpreadSheetPoiHelper.loadExcel ( file ) );
            }
            else
            {
                overrides.add ( file );
            }
        }

        System.out.println ( "*** 1c - Apply overrides" );
        for ( final String file : overrides )
        {
            cfg.applyOverrides ( SpreadSheetPoiHelper.loadExcel ( file ) );
        }

        System.out.println ( "*** 1d - Apply script overrides" );
        applyScriptOverrides ( scriptDBase, cfg );

        System.out.println ( "** 3 - Process" );
        cfg.process ();

        System.out.println ( "** 4 - Output" );
        System.out.println ( " **  Base Dir: " + outFile );
        cfg.write ( new File ( outFile ) );

        cfg.close ();
    }

    private static void loadBasics ( final Configuration cfg, final File file ) throws IOException
    {
        System.out.println ( "Loading basics from: " + file );
        final DataLoader loader = new DataLoader ( file );

        System.out.println ( "**** 1aa - Loading basic configuration - Connections" );
        loader.load ( 0, new ConnectionHandler ( cfg ) );

        System.out.println ( "**** 1ab - Loading basic configuration - Authorizations" );
        loader.load ( 1, new AuthorizationHandler ( cfg ) );

        System.out.println ( "**** 1ac - Loading basic configuration - JMS Monitors" );
        loader.load ( 2, new JmsHandler ( cfg ) );
    }

    private static String processNetwork ( final String prefix, final File base, final File generatedBase ) throws Exception
    {
        System.out.println ( "*** 0d - Processing network" );
        final File outListFile = new File ( generatedBase, "IOList-generated-exec.xls" );
        final File sourceFile = new File ( base, "input/network.ods" );
        final File xmlOutFile = new File ( generatedBase, "network.xml" );
        org.openscada.atlantis.configurator.exec.Application.process ( prefix, outListFile.getAbsolutePath (), sourceFile.getAbsolutePath (), xmlOutFile.getAbsolutePath () );

        return outListFile.getAbsolutePath ();
    }

    private static void applyScriptOverrides ( final File base, final Configuration cfg ) throws FileNotFoundException, ScriptException
    {
        for ( final File file : base.listFiles () )
        {
            cfg.applyScriptOverride ( file );
        }
    }
}