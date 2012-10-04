/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.openscada.configurator.module.common.output.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;
import org.openscada.configuration.model.Module;
import org.openscada.configurator.module.common.output.OutputAverages;
import org.openscada.configurator.module.common.output.OutputGlobalSummaries;
import org.openscada.configurator.module.common.output.OutputItems;
import org.openscada.configurator.module.common.output.OutputPackage;
import org.openscada.configurator.module.common.output.OutputSummaries;
import org.openscada.configurator.module.common.output.WriteOutput;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.openscada.configurator.module.common.output.OutputPackage
 * @generated
 */
public class OutputAdapterFactory extends AdapterFactoryImpl
{
    /**
     * The cached model package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static OutputPackage modelPackage;

    /**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public OutputAdapterFactory ()
    {
        if ( modelPackage == null )
        {
            modelPackage = OutputPackage.eINSTANCE;
        }
    }

    /**
     * Returns whether this factory is applicable for the type of the object.
     * <!-- begin-user-doc -->
     * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
     * <!-- end-user-doc -->
     * @return whether this factory is applicable for the type of the object.
     * @generated
     */
    @Override
    public boolean isFactoryForType ( Object object )
    {
        if ( object == modelPackage )
        {
            return true;
        }
        if ( object instanceof EObject )
        {
            return ( (EObject)object ).eClass ().getEPackage () == modelPackage;
        }
        return false;
    }

    /**
     * The switch that delegates to the <code>createXXX</code> methods.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected OutputSwitch<Adapter> modelSwitch = new OutputSwitch<Adapter> () {
        @Override
        public Adapter caseOutputAverages ( OutputAverages object )
        {
            return createOutputAveragesAdapter ();
        }

        @Override
        public Adapter caseOutputItems ( OutputItems object )
        {
            return createOutputItemsAdapter ();
        }

        @Override
        public Adapter caseOutputSummaries ( OutputSummaries object )
        {
            return createOutputSummariesAdapter ();
        }

        @Override
        public Adapter caseOutputGlobalSummaries ( OutputGlobalSummaries object )
        {
            return createOutputGlobalSummariesAdapter ();
        }

        @Override
        public Adapter caseWriteOutput ( WriteOutput object )
        {
            return createWriteOutputAdapter ();
        }

        @Override
        public Adapter caseModule ( Module object )
        {
            return createModuleAdapter ();
        }

        @Override
        public Adapter defaultCase ( EObject object )
        {
            return createEObjectAdapter ();
        }
    };

    /**
     * Creates an adapter for the <code>target</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param target the object to adapt.
     * @return the adapter for the <code>target</code>.
     * @generated
     */
    @Override
    public Adapter createAdapter ( Notifier target )
    {
        return modelSwitch.doSwitch ( (EObject)target );
    }

    /**
     * Creates a new adapter for an object of class '{@link org.openscada.configurator.module.common.output.OutputAverages <em>Averages</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.openscada.configurator.module.common.output.OutputAverages
     * @generated
     */
    public Adapter createOutputAveragesAdapter ()
    {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.openscada.configurator.module.common.output.OutputItems <em>Items</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.openscada.configurator.module.common.output.OutputItems
     * @generated
     */
    public Adapter createOutputItemsAdapter ()
    {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.openscada.configurator.module.common.output.OutputSummaries <em>Summaries</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.openscada.configurator.module.common.output.OutputSummaries
     * @generated
     */
    public Adapter createOutputSummariesAdapter ()
    {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.openscada.configurator.module.common.output.OutputGlobalSummaries <em>Global Summaries</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.openscada.configurator.module.common.output.OutputGlobalSummaries
     * @generated
     */
    public Adapter createOutputGlobalSummariesAdapter ()
    {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.openscada.configurator.module.common.output.WriteOutput <em>Write Output</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.openscada.configurator.module.common.output.WriteOutput
     * @generated
     */
    public Adapter createWriteOutputAdapter ()
    {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.openscada.configuration.model.Module <em>Module</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.openscada.configuration.model.Module
     * @generated
     */
    public Adapter createModuleAdapter ()
    {
        return null;
    }

    /**
     * Creates a new adapter for the default case.
     * <!-- begin-user-doc -->
     * This default implementation returns null.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @generated
     */
    public Adapter createEObjectAdapter ()
    {
        return null;
    }

} //OutputAdapterFactory