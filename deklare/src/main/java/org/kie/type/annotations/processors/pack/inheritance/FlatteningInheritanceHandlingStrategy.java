package org.kie.type.annotations.processors.pack.inheritance;

import org.jboss.forge.roaster.model.Field;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.kie.type.annotations.InheritanceMode;
import org.kie.type.model.TypeDescriptor;

import java.util.LinkedHashMap;
import java.util.List;

public class FlatteningInheritanceHandlingStrategy extends InheritanceHandlingStrategy {

    @Override
    public InheritanceMode getHandledMode() {
        return InheritanceMode.FLAT;
    }

    @Override
    public void reshapeModel( LinkedHashMap<String, TypeDescriptor> types ) {
        for ( String typeName : types.keySet() ) {
            TypeDescriptor td = types.get( typeName );

            assignParent( td.getName(), null );

            getAssignedFields( td.getName() ).addAll( getRequiredFields( td.getName() ) );
        }
    }


}
