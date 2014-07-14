package org.kie.type.annotations.processors.pack.inheritance;

import org.jboss.forge.roaster.model.Field;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.kie.type.annotations.InheritanceMode;
import org.kie.type.model.TypeDescriptor;
import org.kie.type.utils.HierarchySorter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class InheritanceHandlingStrategy {

    public abstract InheritanceMode getHandledMode();

    private Map<String,List<Field>> requiredFields = new HashMap<String,List<Field>>();

    private Map<String,List<Field>> assignedFields = new HashMap<String,List<Field>>();
    private Map<String,String> assignedParents = new HashMap<String,String>();


    public void analyzeModel( LinkedHashMap<String, TypeDescriptor> types ) {

        for ( TypeDescriptor td : types.values() ) {
            String typeName = td.getName();
            List<Field> fieldsForType = new ArrayList<Field>();
            JavaClassSource source = types.get( typeName ).getSourceAST();
            for ( String ancestor : source.getInterfaces() ) {
                fieldsForType.addAll( getRequiredFields( ancestor ) );
            }
            fieldsForType.addAll( source.getFields() );
            requiredFields.put( typeName, fieldsForType );
        }
    }

    protected List<Field> getRequiredFields( String name ) {
        return requiredFields.get( name );
    }

    public List<Field> getAssignedFields( String name ) {
        if ( ! assignedFields.containsKey( name ) ) {
            assignedFields.put( name, new ArrayList<Field>() );
        }
        return assignedFields.get( name );
    }

    public String getAssignedParents( String name ) {
        return assignedParents.get( name );
    }

    protected void assignParent( String child, String parent ) {
        assignedParents.put( child, parent );
    }

    public abstract void reshapeModel( LinkedHashMap<String, TypeDescriptor> types );

}
