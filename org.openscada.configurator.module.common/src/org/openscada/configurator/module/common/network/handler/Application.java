package org.openscada.configurator.module.common.network.handler;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.openscada.configuration.model.Project;
import org.openscada.configurator.data.DataLoaderOdfDom;
import org.openscada.configurator.module.common.network.NetworkModule;
import org.openscada.deploy.iolist.model.DataType;
import org.openscada.deploy.iolist.model.Item;
import org.openscada.deploy.iolist.model.ModelFactory;
import org.openscada.deploy.iolist.utils.SpreadSheetPoiHelper;

public class Application
{
    public static void process ( final Project project, final NetworkModule module )
    {
        try
        {
            final File generatedDir = new File ( FileLocator.toFileURL ( new URL ( project.getGeneratedDirectory () ) ).getFile () );

            final DataLoaderOdfDom loader = new DataLoaderOdfDom ( new File ( FileLocator.toFileURL ( new URL ( module.getNetworkFile () ) ).getFile () ) );

            final NetworkDeviceRowHandler handler = new NetworkDeviceRowHandler ();
            loader.load ( 0, handler );

            final File file;
            if ( module.getOverrideGeneratedFile () != null )
            {
                file = new File ( FileLocator.toFileURL ( new URL ( module.getOverrideGeneratedFile () ) ).getFile () );
            }
            else
            {
                file = new File ( generatedDir, "IOList-generated-exec.xls" );
            }
            SpreadSheetPoiHelper.writeSpreadsheet ( file, convertDevices ( module.getPrefix (), handler.getDevices () ) );

            project.getIoListFile ().add ( file.toURI ().toString () );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException ( e );
        }
    }

    private static List<Item> convertDevices ( final String prefix, final Set<NetworkDevice> devices )
    {
        final List<Item> items = new ArrayList<Item> ();

        for ( final NetworkDevice device : devices )
        {
            {
                final Item item = createDeviceItem ( device, null, null, device.getWarnLoss () / 100.0, device.getAlarmLoss () / 100.0 );
                item.setAlias ( String.format ( "%s.%s.%s.AVAIL.V", prefix, device.getLocation (), device.getComponent () ) ); //$NON-NLS-1$
                item.setName ( String.format ( "PING.values.%s.reach", device.getHostname () ) ); //$NON-NLS-1$
                item.setDescription ( String.format ( Messages.Application_PacketLoss_Description, device.getDescription () ) );
                item.setUnit ( "%" ); //$NON-NLS-1$
                items.add ( item );
            }
            {
                final Item item = createDeviceItem ( device, device.getWarnRtt (), device.getAlarmRtt (), null, null );
                item.setAlias ( String.format ( "%s.%s.%s.P_RT.V", prefix, device.getLocation (), device.getComponent () ) ); //$NON-NLS-1$
                item.setName ( String.format ( "PING.values.%s.rtt", device.getHostname () ) ); //$NON-NLS-1$
                item.setDescription ( String.format ( Messages.Application_RTT_Description, device.getDescription () ) );
                item.setUnit ( "ms" ); //$NON-NLS-1$
                items.add ( item );
            }

        }

        return items;
    }

    private static Item createDeviceItem ( final NetworkDevice device, final Double warnHigh, final Double alarmHigh, final Double warnLow, final Double alarmLow )
    {
        final Item item = ModelFactory.eINSTANCE.createItem ();
        item.setDevice ( "exec" ); //$NON-NLS-1$
        item.setSystem ( "NETWORK" ); //$NON-NLS-1$

        item.setDataType ( DataType.FLOAT );

        // TODO: allow hierarchy for network devices
        item.getHierarchy ().add ( device.getLocation () );
        item.getHierarchy ().add ( device.getComponent () );

        item.setLocalMin ( ModelFactory.eINSTANCE.createLevelMonitor () );
        item.setLocalLowLow ( ModelFactory.eINSTANCE.createLevelMonitor () );
        item.setLocalLow ( ModelFactory.eINSTANCE.createLevelMonitor () );
        item.setLocalHigh ( ModelFactory.eINSTANCE.createLevelMonitor () );
        item.setLocalHighHigh ( ModelFactory.eINSTANCE.createLevelMonitor () );
        item.setLocalMax ( ModelFactory.eINSTANCE.createLevelMonitor () );

        if ( warnLow != null )
        {
            item.getLocalLow ().setPreset ( warnLow );
        }
        if ( alarmLow != null )
        {
            item.getLocalLowLow ().setPreset ( alarmLow );
            item.getLocalLowLow ().setAck ( true );
        }
        if ( warnHigh != null )
        {
            item.getLocalHigh ().setPreset ( alarmLow );
        }
        if ( alarmHigh != null )
        {
            item.getLocalHighHigh ().setPreset ( alarmLow );
            item.getLocalHighHigh ().setAck ( true );
        }

        item.getLocalMin ().setPreset ( 0.0 );
        item.getLocalMin ().setAck ( true );

        return item;
    }
}