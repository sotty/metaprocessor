package org.kie.type.annotations.processors.pack.inheritance;

import org.kie.type.annotations.InheritanceMode;
import org.kie.type.model.TypeDescriptor;

import java.util.LinkedHashMap;

public class AsIsInheritanceHandlingStrategy extends InheritanceHandlingStrategy {

    @Override
    public InheritanceMode getHandledMode() {
        return InheritanceMode.DEFAULT;
    }

    @Override
    public void reshapeModel( LinkedHashMap<String, TypeDescriptor> types ) {
        // do nothing by definition
    }
}

