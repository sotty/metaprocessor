package org.kie.type.annotations.processors.pack;

import org.jboss.forge.roaster.model.source.JavaSource;
import org.kie.type.annotations.processors.AbstractProcessor;
import org.kie.type.annotations.processors.Processor;

import java.lang.annotation.Annotation;

public abstract class PackageAnnotationProcessor<T extends Annotation> extends AbstractProcessor<T> {

    @Override
    public boolean isPackageAnnotation() {
        return true;
    }

}
