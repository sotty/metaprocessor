package org.kie.type.annotations.processors.klass;

import org.jboss.forge.roaster.model.source.JavaSource;
import org.kie.type.annotations.processors.AbstractProcessor;

import java.lang.annotation.Annotation;

public abstract class ClassAnnotationProcessor<T extends Annotation> extends AbstractProcessor<T> {

    @Override
    public boolean isPackageAnnotation() {
        return true;
    }

    public abstract boolean isClassProcessable( JavaSource source );
}
