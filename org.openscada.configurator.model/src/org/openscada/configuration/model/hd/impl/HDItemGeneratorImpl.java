/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.openscada.configuration.model.hd.impl;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.openscada.configuration.model.AtlantisConfigurationSlot;
import org.openscada.configuration.model.Project;
import org.openscada.configuration.model.hd.ConfigurationSlot;
import org.openscada.configuration.model.hd.HDItemGenerator;
import org.openscada.configuration.model.hd.HdPackage;
import org.openscada.deploy.iolist.model.Item;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>HD Item Generator</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.openscada.configuration.model.hd.impl.HDItemGeneratorImpl#getHdSlot <em>Hd Slot</em>}</li>
 * <li>{@link org.openscada.configuration.model.hd.impl.HDItemGeneratorImpl#getDaSlot <em>Da Slot</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class HDItemGeneratorImpl extends EObjectImpl implements HDItemGenerator
{
    /**
     * The cached value of the '{@link #getHdSlot() <em>Hd Slot</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getHdSlot()
     * @generated
     * @ordered
     */
    protected ConfigurationSlot hdSlot;

    /**
     * The cached value of the '{@link #getDaSlot() <em>Da Slot</em>}' reference. <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getDaSlot()
     * @generated
     * @ordered
     */
    protected AtlantisConfigurationSlot daSlot;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected HDItemGeneratorImpl ()
    {
        super ();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass ()
    {
        return HdPackage.Literals.HD_ITEM_GENERATOR;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public ConfigurationSlot getHdSlot ()
    {
        if ( this.hdSlot != null && this.hdSlot.eIsProxy () )
        {
            final InternalEObject oldHdSlot = (InternalEObject)this.hdSlot;
            this.hdSlot = (ConfigurationSlot)eResolveProxy ( oldHdSlot );
            if ( this.hdSlot != oldHdSlot )
            {
                if ( eNotificationRequired () )
                {
                    eNotify ( new ENotificationImpl ( this, Notification.RESOLVE, HdPackage.HD_ITEM_GENERATOR__HD_SLOT, oldHdSlot, this.hdSlot ) );
                }
            }
        }
        return this.hdSlot;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public ConfigurationSlot basicGetHdSlot ()
    {
        return this.hdSlot;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setHdSlot ( final ConfigurationSlot newHdSlot )
    {
        final ConfigurationSlot oldHdSlot = this.hdSlot;
        this.hdSlot = newHdSlot;
        if ( eNotificationRequired () )
        {
            eNotify ( new ENotificationImpl ( this, Notification.SET, HdPackage.HD_ITEM_GENERATOR__HD_SLOT, oldHdSlot, this.hdSlot ) );
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public AtlantisConfigurationSlot getDaSlot ()
    {
        if ( this.daSlot != null && this.daSlot.eIsProxy () )
        {
            final InternalEObject oldDaSlot = (InternalEObject)this.daSlot;
            this.daSlot = (AtlantisConfigurationSlot)eResolveProxy ( oldDaSlot );
            if ( this.daSlot != oldDaSlot )
            {
                if ( eNotificationRequired () )
                {
                    eNotify ( new ENotificationImpl ( this, Notification.RESOLVE, HdPackage.HD_ITEM_GENERATOR__DA_SLOT, oldDaSlot, this.daSlot ) );
                }
            }
        }
        return this.daSlot;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public AtlantisConfigurationSlot basicGetDaSlot ()
    {
        return this.daSlot;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void setDaSlot ( final AtlantisConfigurationSlot newDaSlot )
    {
        final AtlantisConfigurationSlot oldDaSlot = this.daSlot;
        this.daSlot = newDaSlot;
        if ( eNotificationRequired () )
        {
            eNotify ( new ENotificationImpl ( this, Notification.SET, HdPackage.HD_ITEM_GENERATOR__DA_SLOT, oldDaSlot, this.daSlot ) );
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public void process ( final Project project )
    {
        for ( final Item item : getDaSlot ().getConfigurationData ().getItems () )
        {
            if ( item.getHdStorage () == null || item.getHdStorage ().isEmpty () )
            {
                continue;
            }

            System.out.println ( "Adding storage item: " + item );
            addStorage ( item );
        }
    }

    /**
     * @generated NOT
     * @param item
     */
    private void addStorage ( final Item item )
    {
        {
            final Map<String, String> data = new HashMap<String, String> ();
            final String id = item.getAlias () + ".item";
            data.put ( "connection.id", "master" );
            data.put ( "item.id", item.getAlias () );
            getHdSlot ().getConfigurationData ().addData ( "da.datasource.dataitem", id, data );
        }
        {
            final Map<String, String> data = new HashMap<String, String> ();
            final String id = item.getAlias () + ".hd";
            data.put ( "datasource.id", item.getAlias () + ".item" );
            getHdSlot ().getConfigurationData ().addData ( "historical.item.factory", id, data );
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object eGet ( final int featureID, final boolean resolve, final boolean coreType )
    {
        switch ( featureID )
        {
            case HdPackage.HD_ITEM_GENERATOR__HD_SLOT:
                if ( resolve )
                {
                    return getHdSlot ();
                }
                return basicGetHdSlot ();
            case HdPackage.HD_ITEM_GENERATOR__DA_SLOT:
                if ( resolve )
                {
                    return getDaSlot ();
                }
                return basicGetDaSlot ();
        }
        return super.eGet ( featureID, resolve, coreType );
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eSet ( final int featureID, final Object newValue )
    {
        switch ( featureID )
        {
            case HdPackage.HD_ITEM_GENERATOR__HD_SLOT:
                setHdSlot ( (ConfigurationSlot)newValue );
                return;
            case HdPackage.HD_ITEM_GENERATOR__DA_SLOT:
                setDaSlot ( (AtlantisConfigurationSlot)newValue );
                return;
        }
        super.eSet ( featureID, newValue );
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public void eUnset ( final int featureID )
    {
        switch ( featureID )
        {
            case HdPackage.HD_ITEM_GENERATOR__HD_SLOT:
                setHdSlot ( (ConfigurationSlot)null );
                return;
            case HdPackage.HD_ITEM_GENERATOR__DA_SLOT:
                setDaSlot ( (AtlantisConfigurationSlot)null );
                return;
        }
        super.eUnset ( featureID );
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public boolean eIsSet ( final int featureID )
    {
        switch ( featureID )
        {
            case HdPackage.HD_ITEM_GENERATOR__HD_SLOT:
                return this.hdSlot != null;
            case HdPackage.HD_ITEM_GENERATOR__DA_SLOT:
                return this.daSlot != null;
        }
        return super.eIsSet ( featureID );
    }

} //HDItemGeneratorImpl
