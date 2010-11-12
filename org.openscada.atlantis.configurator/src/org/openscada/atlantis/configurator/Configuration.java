package org.openscada.atlantis.configurator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.openscada.atlantis.configurator.summary.SummaryGenerator;
import org.openscada.deploy.iolist.model.Item;
import org.openscada.deploy.iolist.model.SummaryItem;
import org.openscada.deploy.iolist.utils.GenericConfiguration;
import org.openscada.deploy.iolist.utils.SpreadSheetPoiHelper;
import org.openscada.utils.str.StringHelper;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class Configuration extends GenericConfiguration
{

    private final List<Item> items = new ArrayList<Item> ();

    private final PrintStream logStream;

    private static Integer maxItemLimit = Integer.getInteger ( "maxItemLimit", null );

    public Configuration () throws Exception
    {
        this.logStream = System.out;

        // event query

        addEventQuery ( "events.all", "(eventType=*)", 10000 );

        // monitor query

        addMonitorQuery ( "monitors.unsafe", "(status=UNSAFE)" );
        addMonitorQuery ( "monitors.inactive", "(status=INACTIVE)" );
        addMonitorQuery ( "monitors.not_ok", "(|(status=NOT_OK)(status=NOT_OK_NOT_AKN)(status=NOT_OK_AKN))" );
        addMonitorQuery ( "monitors.ack_required", "(|(status=NOT_AKN)(status=NOT_OK_NOT_AKN))" );
        addMonitorQuery ( "monitors.init", "(status=INIT)" );
        addMonitorQuery ( "monitors.all", "(status=*)" );

        // add ignore fields

        addIgnoreFields ( "org.openscada.da.manual", "value", "user", "reason", "timestamp" );
        addIgnoreFields ( "org.openscada.da.manual", "value", "user", "reason", "timestamp" );
        addIgnoreFields ( "org.openscada.da.master.common.block", "note", "active", "user", "timestamp" );

        addIgnoreFields ( "org.openscada.da.level.high", "preset", "active" );
        addIgnoreFields ( "org.openscada.da.level.highhigh", "preset", "active" );
        addIgnoreFields ( "org.openscada.da.level.low", "preset", "active" );
        addIgnoreFields ( "org.openscada.da.level.lowlow", "preset", "active" );
        addIgnoreFields ( "org.openscada.da.level.ceil", "preset", "active" );
        addIgnoreFields ( "org.openscada.da.level.floor", "preset", "active" );

        addIgnoreFields ( "org.openscada.da.scale.input", "active", "factor" );

        // summaries

        addSummary ( "error" );
        addSummary ( "alarm" );
        addSummary ( "manual" );
        addSummary ( "org.openscada.da.master.common.block.active" );

        addAEInfo ();
    }

    private void addAEInfo ()
    {
        final Map<String, Object> data = new HashMap<String, Object> ();
        data.put ( "prefix", "ae.server.info" );
        addData ( "ae.server.info", "ae.server.info.all", data );
    }

    private void addSummary ( final String string )
    {
        final Map<String, Object> data = new HashMap<String, Object> ();

        data.put ( "attribute", string );
        data.put ( "onlyMaster", true );

        addData ( "org.openscada.da.server.osgi.summary.attribute", "summary." + string, data );
    }

    public void addItems ( final List<Item> items )
    {
        if ( maxItemLimit == null )
        {
            this.items.addAll ( items );
        }
        else
        {
            for ( final Item item : items )
            {
                if ( this.items.size () > maxItemLimit )
                {
                    break;
                }
                this.items.add ( item );
            }
        }
    }

    private void addMonitorQuery ( final String id, final String filter )
    {
        final Map<String, Object> data = new HashMap<String, Object> ();
        data.put ( "filter", filter );
        addData ( "ae.monitor.query", id, data );
    }

    private void addEventQuery ( final String id, final String filter, final int size )
    {
        final Map<String, Object> data = new HashMap<String, Object> ();
        data.put ( "filter", filter );
        data.put ( "size", size );
        addData ( "org.openscada.ae.server.common.event.pool", id, data );
    }

    private void addAuthorizationScript ( final String id, final String script, final int priority )
    {
        addAuthorizationScript ( id, script, priority, null, null, null );
    }

    public void addAuthorizationScript ( final String id, final String script, final int priority, final String idFilter, final String typeFilter, final String actionFilter )
    {
        final Map<String, Object> data = new HashMap<String, Object> ();
        data.put ( "script", script );
        data.put ( "priority", priority );
        data.put ( "for.id", idFilter );
        data.put ( "for.type", typeFilter );
        data.put ( "for.action", actionFilter );
        addData ( "org.openscada.sec.provider.script.factory", id, data );
    }

    public void process ()
    {
        validate ();

        generateSummaryAlarms ();
        generateSummeryBlocks ();

        convertItems ();
    }

    private void generateSummeryBlocks ()
    {
        final Multimap<String, String> locMap = HashMultimap.create ();

        for ( final Item item : this.items )
        {
            if ( item.getLocation () == null || item.getComponent () == null || item.getLocation ().length () == 0 || item.getComponent ().length () == 0 )
            {
                continue;
            }

            final String loc = item.getLocation () + "-" + item.getComponent ();
            locMap.put ( loc, makeMasterId ( item ) );
        }

        for ( final String key : locMap.keySet () )
        {
            final Collection<String> values = locMap.get ( key );
            addBlock ( key + ".block", new ArrayList<String> ( values ) );
        }
    }

    private void addBlock ( final String blockId, final List<String> values )
    {
        if ( values.isEmpty () )
        {
            return;
        }

        Collections.sort ( values );

        final Map<String, Object> data = new HashMap<String, Object> ();

        data.put ( "master.id", StringHelper.join ( values, "," ) );

        addData ( "org.openscada.da.master.common.block", blockId, data );
    }

    private void generateSummaryAlarms ()
    {
        SummaryGenerator.generateSummaryAlarms ( this, this.items );
    }

    private String makeInternalItemId ( final Item item )
    {
        if ( item.getAlias () == null )
        {
            return item.getName ();
        }
        else
        {
            return item.getAlias ();
        }
    }

    private String makeMasterId ( final Item item )
    {
        return makeInternalItemId ( item ) + ".master";
    }

    private void convertItems ()
    {
        final Set<String> connections = new HashSet<String> ();

        for ( final Item item : this.items )
        {
            if ( item.getDevice () != null && !item.getDevice ().isEmpty () )
            {
                connections.add ( item.getDevice () );
            }

            final String itemId = item.getName ();

            final String internalItemId = makeInternalItemId ( item );
            final String masterId = makeMasterId ( item );

            String sourceId;

            if ( "ds".equalsIgnoreCase ( item.getDevice () ) )
            {
                sourceId = item.getName () + ".ds";
                addDSDataSource ( sourceId );
            }
            else if ( item.getDevice () != null && !"".equals ( item.getDevice () ) )
            {
                sourceId = internalItemId + ".source";

                addSource ( sourceId, itemId, item.getDevice ().toLowerCase () );
            }
            else
            {
                sourceId = item.getName ();
            }

            addMaster ( masterId, sourceId );
            addAlias ( internalItemId + ".alias", internalItemId, masterId, item.getDescription () );
            if ( item.isDefaultChain () )
            {
                addDefaultChain ( masterId );
            }

            final Map<String, String> attributes = new HashMap<String, String> ();
            attributes.put ( "location", item.getLocation () );
            attributes.put ( "component", item.getComponent () );
            if ( item.getDevice () != null )
            {
                attributes.put ( "hive", item.getDevice ().toUpperCase () );
            }
            if ( item.getSystem () != null )
            {
                attributes.put ( "system", item.getSystem ().toUpperCase () );
            }
            attributes.put ( "message", item.getDescription () );
            attributes.put ( "item", internalItemId );

            if ( item.isRemoteBool () )
            {
                addRemoteValueMonitor ( masterId + ".remote.monitor", masterId, "remote.ackRequired", "remote.ackRequired.timestamp", "ALM", attributes );
            }
            if ( item.isLocalBoolAvailable () )
            {
                String reference;
                if ( item.getLocalBool () == null )
                {
                    reference = null;
                }
                else
                {
                    reference = item.getLocalBool () ? "true" : "false";
                }

                addLocalMonitor ( masterId + ".local.monitor", masterId, reference, item.isLocalBoolAck (), item.getDescription (), attributes );
            }
            makeRemoteLevels ( item, masterId, attributes );
            makeLocalLevels ( item, masterId, attributes );

            if ( item.isEventCommand () )
            {
                addWriteLogger ( internalItemId + ".logger", masterId, attributes );
            }
            addNegate ( masterId + ".negate", masterId, false );

            if ( item.isLocalManual () )
            {
                addLocalManual ( masterId + ".manual", masterId, attributes );
            }

            if ( item.isLocalScaleAvailable () )
            {
                addLocalScale ( masterId + ".scale", masterId, item.getLocalScaleFactor (), item.getLocalScaleOffset (), attributes );
            }

            if ( item.isListMonitorPreset () )
            {
                addListMonitor ( masterId + ".listMonitor", masterId, item.isListMonitorListIsAlarm (), item.getListMonitorItems (), item.isListMonitorAckRequired (), item.getDescription (), attributes );
            }
        }

        validateConnections ( connections );
    }

    private void validateConnections ( final Set<String> connections )
    {
        System.out.flush ();
        final Map<String, Map<String, Object>> connnectionData = this.data.get ( "da.connection" );
        for ( final String device : connections )
        {
            if ( !connnectionData.containsKey ( device ) )
            {
                System.err.println ( String.format ( "Connection '%s' is undefined!", device ) );
            }
        }
        System.err.flush ();
    }

    private void addDSDataSource ( final String id )
    {
        final Map<String, Object> data = new HashMap<String, Object> ();

        addData ( "org.openscada.da.datasource.ds", id, data );
    }

    private void addLocalManual ( final String id, final String masterId, final Map<String, String> attributes )
    {
        final Map<String, Object> data = new HashMap<String, Object> ();

        data.put ( "master.id", masterId );

        applyInfoAttributes ( attributes, data );

        addData ( "org.openscada.da.manual", id, data );
    }

    public void addScript ( final String id, final String engine, final Map<String, String> dataSources, final Set<String> outputs, final String init, final String update, final String timerCommand, final Integer timer )
    {
        final Map<String, Object> data = new HashMap<String, Object> ();

        if ( engine != null )
        {
            data.put ( "engine", engine );
        }

        for ( final Map.Entry<String, String> entry : dataSources.entrySet () )
        {
            data.put ( "datasource." + entry.getKey (), entry.getValue () );
        }

        if ( init != null )
        {
            data.put ( "init", init );
        }
        if ( update != null )
        {
            data.put ( "updateCommand", update );
        }
        if ( outputs != null && !outputs.isEmpty () )
        {
            data.put ( "writeSources", StringHelper.join ( outputs, "," ) );
        }
        if ( timer != null )
        {
            data.put ( "timer", timer );
        }
        if ( timerCommand != null )
        {
            data.put ( "timerCommand", timerCommand );
        }

        addData ( "org.openscada.da.datasource.script", id, data );
    }

    public void addScript ( final String id, final String engine, final Map<String, String> dataSources, final File initFile, final File updateFile ) throws Exception
    {
        addScript ( id, engine, dataSources, null, loadFromFile ( initFile ), loadFromFile ( updateFile ), null, null );
    }

    static final String NL = System.getProperty ( "line.separator" );

    private static final String BOOLEAN_ALARM_MONITOR_FACTORY_ID = null;

    private static final String LIST_ALARM_MONITOR_FACTORY_ID = null;

    public static String loadFromFile ( final File file ) throws Exception
    {
        if ( file == null )
        {
            return null;
        }

        final BufferedReader reader = new BufferedReader ( new FileReader ( file ) );
        try
        {
            final StringBuilder sb = new StringBuilder ();
            String line;
            while ( ( line = reader.readLine () ) != null )
            {
                sb.append ( line );
                sb.append ( NL );
            }

            return sb.toString ();
        }
        finally
        {
            reader.close ();
        }
    }

    private void addLocalScale ( final String id, final String masterId, final Double localScaleFactor, final Double localScaleOffset, final Map<String, String> attributes )
    {
        final Map<String, Object> data = new HashMap<String, Object> ();

        data.put ( "master.id", masterId );
        data.put ( "active", "" + ( localScaleFactor != null || localScaleOffset != null ) );
        data.put ( "factor", "" + localScaleFactor != null ? localScaleFactor : "1.0" );
        data.put ( "offset", "" + localScaleOffset != null ? localScaleOffset : "0.0" );

        applyInfoAttributes ( attributes, data );

        addData ( "org.openscada.da.scale.input", id, data );
    }

    private void addNegate ( final String id, final String masterId, final boolean active )
    {
        final Map<String, Object> data = new HashMap<String, Object> ();

        data.put ( "master.id", masterId );
        data.put ( "active", "" + active );

        addData ( "org.openscada.da.negate.input", id, data );
    }

    private void addWriteLogger ( final String id, final String masterId, final Map<String, String> attributes )
    {
        final Map<String, Object> data = new HashMap<String, Object> ();

        data.put ( "master.id", masterId );
        data.put ( "source", masterId );
        data.put ( "logValue", true );
        data.put ( "logAttributes", false );
        data.put ( "logSubscription", true );

        applyInfoAttributes ( attributes, data );

        addData ( "org.openscada.ae.event.logger", id, data );
    }

    private void addSource ( final String id, final String itemId, final String connectionId )
    {
        final Map<String, Object> data = new HashMap<String, Object> ();

        data.put ( "item.id", itemId );
        data.put ( "connection.id", connectionId );

        addData ( "da.datasource.dataitem", id, data );
    }

    private void validate ()
    {
        checkForDuplicates ();
    }

    private void checkForDuplicates ()
    {
        final Set<String> alias = new HashSet<String> ();
        final Collection<Item> duplicates = new LinkedList<Item> ();
        for ( final Item item : this.items )
        {
            if ( alias.contains ( item.getAlias () ) )
            {
                duplicates.add ( item );
            }
            else
            {
                alias.add ( item.getAlias () );
            }
        }
        if ( !duplicates.isEmpty () )
        {
            throw new DuplicateItemsException ( duplicates );
        }
    }

    private void makeLocalLevels ( final Item item, final String masterId, final Map<String, String> attributes )
    {
        if ( item.getLocalMin () != null )
        {
            makeLocalLevel ( masterId, "floor", true, item.isLocalMinAck (), item.getLocalMin (), attributes );
        }
        if ( item.getLocalMax () != null )
        {
            makeLocalLevel ( masterId, "ceil", true, item.isLocalMaxAck (), item.getLocalMax (), attributes );
        }
        if ( item.isLocalHighHighAvailable () )
        {
            makeLocalLevel ( masterId, "highhigh", false, item.isLocalHighHighAck (), item.getLocalHighHighPreset (), attributes );
        }
        if ( item.isLocalHighAvailable () )
        {
            makeLocalLevel ( masterId, "high", false, item.isLocalHighAck (), item.getLocalHighPreset (), attributes );
        }
        if ( item.isLocalLowAvailable () )
        {
            makeLocalLevel ( masterId, "low", false, item.isLocalLowAck (), item.getLocalLowPreset (), attributes );
        }
        if ( item.isLocalLowLowAvailable () )
        {
            makeLocalLevel ( masterId, "lowlow", false, item.isLocalLowLowAck (), item.getLocalLowLowPreset (), attributes );
        }
    }

    private void makeRemoteLevels ( final Item item, final String masterId, final Map<String, String> attributes )
    {
        if ( item.isRemoteMax () )
        {
            makeRemoteLevel ( masterId, "ceil", "R-MAX", attributes );
        }
        if ( item.isRemoteHighHigh () )
        {
            makeRemoteLevel ( masterId, "highhigh", "R-HH", attributes );
        }
        if ( item.isRemoteHigh () )
        {
            makeRemoteLevel ( masterId, "high", "R-H", attributes );
        }
        if ( item.isRemoteLow () )
        {
            makeRemoteLevel ( masterId, "low", "R-L", attributes );
        }
        if ( item.isRemoteLowLow () )
        {
            makeRemoteLevel ( masterId, "lowlow", "R-LL", attributes );
        }
        if ( item.isRemoteMin () )
        {
            makeRemoteLevel ( masterId, "floor", "R-MIN", attributes );
        }
    }

    private void addLocalMonitor ( final String id, final String masterId, final String reference, final boolean ack, final String message, final Map<String, String> attributes )
    {
        final Map<String, Object> data = new HashMap<String, Object> ();

        if ( reference != null )
        {
            data.put ( "reference", reference );
            data.put ( "active", "true" );
        }
        else
        {
            data.put ( "active", "false" );
        }
        data.put ( "master.id", masterId );
        data.put ( "requireAck", "" + ack );
        if ( message != null )
        {
            data.put ( "message", message );
        }

        applyInfoAttributes ( attributes, data );

        addData ( BOOLEAN_ALARM_MONITOR_FACTORY_ID, id, data );
    }

    private void addListMonitor ( final String id, final String masterId, final boolean listIsAlarm, final Collection<String> referenceList, final boolean ack, final String message, final Map<String, String> attributes )
    {
        final Map<String, Object> data = new HashMap<String, Object> ();

        data.put ( "referenceList", StringHelper.join ( referenceList, "," ) );
        data.put ( "listIsAlarm", listIsAlarm );
        data.put ( "master.id", masterId );
        data.put ( "requireAck", "" + ack );

        if ( message != null )
        {
            data.put ( "message", message );
        }

        applyInfoAttributes ( attributes, data );

        addData ( LIST_ALARM_MONITOR_FACTORY_ID, id, data );
    }

    private void addRemoteValueMonitor ( final String id, final String masterId, final String attributeAck, final String attributeAckTimestamp, final String monitorType, Map<String, String> attributes )
    {
        final Map<String, Object> data = new HashMap<String, Object> ();

        if ( attributes == null )
        {
            attributes = Collections.emptyMap ();
        }

        attributes = new HashMap<String, String> ( attributes );

        attributes.put ( "monitorType", monitorType );
        applyInfoAttributes ( attributes, data );

        data.put ( "master.id", masterId );
        data.put ( "attribute.ack.name", attributeAck );
        data.put ( "attribute.ack.timestamp.name", attributeAckTimestamp );

        addData ( "ae.monitor.da.remote.booleanValueAlarm", id, data );
    }

    private void addDefaultChain ( final String masterId )
    {
        addSum ( masterId + ".error.phase1", masterId, "error", 600, "phase1" );

        addSum ( masterId + ".manual", masterId, "manual", null, null );
        addSum ( masterId + ".error.phase2", masterId, "error", null, "phase2" );
        addSum ( masterId + ".alarm", masterId, "alarm", null, null );
        addSum ( masterId + ".ackRequired", masterId, "ackRequired", null, null );
    }

    private void addSum ( final String id, final String masterId, final String type, final Integer priority, final String prefix )
    {
        final Map<String, Object> data = new HashMap<String, Object> ();
        data.put ( "master.id", masterId );
        if ( priority != null )
        {
            data.put ( "handlerPriority", "" + priority );
        }
        if ( prefix != null )
        {
            data.put ( "prefix", prefix );
        }
        addData ( String.format ( "da.master.handler.sum.%s", type ), id, data );
    }

    protected void addConnection ( final String id, final String connectionUri )
    {
        final Map<String, Object> data = new HashMap<String, Object> ();

        data.put ( "connection.uri", connectionUri );

        addData ( "da.connection", id, data );
    }

    public void addMaster ( final String id, final String dataSourceId )
    {
        final Map<String, Object> data = new HashMap<String, Object> ();

        data.put ( "datasource.id", dataSourceId );

        addData ( "master.item", id, data );
    }

    public void addAlias ( final String id, final String itemId, final String dataSourceId, final String description )
    {
        final Map<String, Object> data = new HashMap<String, Object> ();

        data.put ( "datasource.id", dataSourceId );
        data.put ( "item.id", itemId );
        if ( description != null )
        {
            data.put ( "description", description );
        }

        addData ( "da.dataitem.datasource", id, data );
    }

    private void makeLocalLevel ( final String masterId, final String type, final boolean error, final boolean requireAck, final Double preset, Map<String, String> attributes )
    {
        attributes = new HashMap<String, String> ( attributes );
        attributes.put ( "message", String.format ( "Level reached %s", type ) );
        addLocalLevelMonitor ( masterId + ".local.level." + type, error, requireAck, masterId, type, preset, attributes );
    }

    private void addLocalLevelMonitor ( final String id, final boolean error, final boolean requireAck, final String masterId, final String type, final Double preset, final Map<String, String> attributes )
    {
        final Map<String, Object> data = new HashMap<String, Object> ();

        data.put ( "master.id", masterId );

        if ( preset != null )
        {
            data.put ( "preset", preset );
            data.put ( "active", "true" );
        }
        else
        {
            data.put ( "active", "false" );
        }

        data.put ( "error", "" + error );
        data.put ( "requireAck", "" + requireAck );

        applyInfoAttributes ( attributes, data );

        addData ( "org.openscada.da.level." + type, id, data );
    }

    private void makeRemoteLevel ( final String masterId, final String type, final String monitorType, Map<String, String> attributes )
    {
        attributes = new HashMap<String, String> ( attributes );

        attributes.put ( "message", String.format ( "Level reached %s in PLC", type ) );
        attributes.put ( "monitorType", monitorType );
        addRemoteAttributeMonitor ( // 
        String.format ( "%s.remote.limit.%s", masterId, type ), masterId, // 
        String.format ( "remote.level.%s.alarm", type ), //  
        String.format ( "remote.level.%s.ackRequired", type ), //
        String.format ( "remote.level.%s.active", type ), //
        String.format ( "remote.level.%s.active.timestamp", type ), //
        String.format ( "remote.level.%s.ackRequired.timestamp", type ), //
        attributes );
    }

    private void addRemoteAttributeMonitor ( final String id, final String masterId, final String attributeValue, final String attributeAck, final String attributeActive, final String attributeTimestamp, final String attributeAckTimestamp, final Map<String, String> attributes )
    {
        final Map<String, Object> data = new HashMap<String, Object> ();

        data.put ( "master.id", masterId );
        data.put ( "attribute.value.name", attributeValue );
        data.put ( "attribute.ack.name", attributeAck );
        data.put ( "attribute.active.name", attributeActive );
        data.put ( "attribute.active.timestamp.name", attributeTimestamp );
        data.put ( "attribute.ack.timestamp.name", attributeAckTimestamp );

        applyInfoAttributes ( attributes, data );

        addData ( "ae.monitor.da.remote.booleanAttributeAlarm", id, data );
    }

    private void applyInfoAttributes ( final Map<String, String> attributes, final Map<String, Object> data )
    {
        if ( attributes == null )
        {
            return;
        }

        for ( final Map.Entry<String, String> entry : attributes.entrySet () )
        {
            data.put ( "info." + entry.getKey (), entry.getValue () );
        }
    }

    public void addSum ( final String id, List<SummaryItem> sources, final Set<String> groups )
    {
        // this.logStream.println ( String.format ( "Add summary group: %s for groups: '%s'", id, StringHelper.join ( groups, "," ) ) );

        final Map<String, Object> data = new HashMap<String, Object> ();

        sources = new ArrayList<SummaryItem> ( sources );

        Collections.sort ( sources, new Comparator<SummaryItem> () {
            @Override
            public int compare ( final SummaryItem object1, final SummaryItem object2 )
            {
                return object1.getDataSourceId ().compareTo ( object2.getDataSourceId () );
            }
        } );

        int i = 0;
        for ( final SummaryItem item : sources )
        {
            // this.logStream.println ( String.format ( "\tAdd item: %s as %s", item.getDataSourceId (), item.getType () ) );

            String value = item.getDataSourceId ();
            if ( item.getType () != null )
            {
                value += "#" + item.getType ();
            }
            data.put ( "datasource." + i, value );
            i++;
        }

        data.put ( "groups", StringHelper.join ( groups, "," ) );

        addData ( "org.openscada.da.datasource.sum", id, data );
    }

    public void close ()
    {
        this.logStream.close ();
    }

    @Override
    public void write ( final File baseDir ) throws Exception
    {
        SpreadSheetPoiHelper.writeSpreadsheet ( new File ( baseDir, "IOList-generated.xls" ).getAbsolutePath (), this.items );
        super.write ( baseDir );
    }

    public String getExtension ( final File file )
    {
        final String[] toks = file.getName ().split ( "\\." );
        return toks[toks.length - 1];
    }

    public void applyScriptOverride ( final File file ) throws FileNotFoundException, ScriptException
    {
        if ( file.isDirectory () )
        {
            return;
        }

        this.logStream.println ( "Running script: " + file );

        final ScriptEngineManager manager = new ScriptEngineManager ();

        final ScriptEngine engine = manager.getEngineByExtension ( getExtension ( file ) );
        final ScriptContext context = engine.getContext ();
        context.setAttribute ( "items", this.items.toArray (), ScriptContext.ENGINE_SCOPE );

        engine.eval ( new FileReader ( file ) );
    }

    public void applyOverrides ( final List<Item> overrides )
    {
        for ( final Item item : overrides )
        {
            this.logStream.println ( "Trying override: " + item );

            final Item origItem = findItemByAlias ( item );
            if ( origItem != null )
            {
                this.logStream.println ( "Overriding item: " + origItem );

                if ( item.getName () == null || "".equals ( item.getName () ) )
                {
                    this.logStream.println ( "Use original source name: " + origItem.getName () );
                    item.setName ( origItem.getName () );
                }

                // override definition
                this.items.remove ( origItem );
                this.items.add ( item );
            }
        }
    }

    private Item findItemByAlias ( final Item item )
    {
        for ( final Item origItem : this.items )
        {
            if ( origItem.getAlias () == null )
            {
                continue;
            }

            if ( origItem.getAlias ().equals ( item.getAlias () ) )
            {
                return origItem;
            }
        }
        return null;
    }

    public void addJmsMonitor ( final String id, final String filter, final Map<String, String> attributes )
    {
        final Map<String, Object> data = new HashMap<String, Object> ();

        data.put ( "filter", filter );
        applyInfoAttributes ( attributes, data );

        addData ( "ae.monitor.ae.event.external", id, data );
    }

}