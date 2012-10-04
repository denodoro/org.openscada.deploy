/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.openscada.configuration.model.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.openscada.configuration.model.ConfiguratorPackage;
import org.openscada.configuration.model.Module;
import org.openscada.configuration.model.Project;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Project</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.openscada.configuration.model.impl.ProjectImpl#getJsonBase <em>Json Base</em>}</li>
 *   <li>{@link org.openscada.configuration.model.impl.ProjectImpl#getModules <em>Modules</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ProjectImpl extends EObjectImpl implements Project
{
    /**
     * The cached value of the '{@link #getJsonBase() <em>Json Base</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getJsonBase()
     * @generated
     * @ordered
     */
    protected EList<String> jsonBase;

    /**
     * The cached value of the '{@link #getModules() <em>Modules</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getModules()
     * @generated
     * @ordered
     */
    protected EList<Module> modules;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ProjectImpl ()
    {
        super ();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass ()
    {
        return ConfiguratorPackage.Literals.PROJECT;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<String> getJsonBase ()
    {
        if ( jsonBase == null )
        {
            jsonBase = new EDataTypeUniqueEList<String> ( String.class, this, ConfiguratorPackage.PROJECT__JSON_BASE );
        }
        return jsonBase;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<Module> getModules ()
    {
        if ( modules == null )
        {
            modules = new EObjectContainmentEList.Resolving<Module> ( Module.class, this, ConfiguratorPackage.PROJECT__MODULES );
        }
        return modules;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove ( InternalEObject otherEnd, int featureID, NotificationChain msgs )
    {
        switch ( featureID )
        {
            case ConfiguratorPackage.PROJECT__MODULES:
                return ( (InternalEList<?>)getModules () ).basicRemove ( otherEnd, msgs );
        }
        return super.eInverseRemove ( otherEnd, featureID, msgs );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet ( int featureID, boolean resolve, boolean coreType )
    {
        switch ( featureID )
        {
            case ConfiguratorPackage.PROJECT__JSON_BASE:
                return getJsonBase ();
            case ConfiguratorPackage.PROJECT__MODULES:
                return getModules ();
        }
        return super.eGet ( featureID, resolve, coreType );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings ( "unchecked" )
    @Override
    public void eSet ( int featureID, Object newValue )
    {
        switch ( featureID )
        {
            case ConfiguratorPackage.PROJECT__JSON_BASE:
                getJsonBase ().clear ();
                getJsonBase ().addAll ( (Collection<? extends String>)newValue );
                return;
            case ConfiguratorPackage.PROJECT__MODULES:
                getModules ().clear ();
                getModules ().addAll ( (Collection<? extends Module>)newValue );
                return;
        }
        super.eSet ( featureID, newValue );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset ( int featureID )
    {
        switch ( featureID )
        {
            case ConfiguratorPackage.PROJECT__JSON_BASE:
                getJsonBase ().clear ();
                return;
            case ConfiguratorPackage.PROJECT__MODULES:
                getModules ().clear ();
                return;
        }
        super.eUnset ( featureID );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet ( int featureID )
    {
        switch ( featureID )
        {
            case ConfiguratorPackage.PROJECT__JSON_BASE:
                return jsonBase != null && !jsonBase.isEmpty ();
            case ConfiguratorPackage.PROJECT__MODULES:
                return modules != null && !modules.isEmpty ();
        }
        return super.eIsSet ( featureID );
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString ()
    {
        if ( eIsProxy () )
            return super.toString ();

        StringBuffer result = new StringBuffer ( super.toString () );
        result.append ( " (jsonBase: " );
        result.append ( jsonBase );
        result.append ( ')' );
        return result.toString ();
    }

} //ProjectImpl
