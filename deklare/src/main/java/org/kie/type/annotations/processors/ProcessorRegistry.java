package org.kie.type.annotations.processors;

import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaEnumSource;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.kie.type.annotations.Equality;
import org.kie.type.annotations.Getter;
import org.kie.type.annotations.InheritanceMode;
import org.kie.type.annotations.InheritanceModel;
import org.kie.type.annotations.Setter;
import org.kie.type.annotations.ToString;
import org.kie.type.annotations.processors.attr.GetterProcessor;
import org.kie.type.annotations.processors.attr.SetterProcessor;
import org.kie.type.annotations.processors.klass.EqualityProcessor;
import org.kie.type.annotations.processors.klass.ToStringProcessor;
import org.kie.type.annotations.processors.pack.InheritanceProcessor;
import org.kie.type.model.TypeDescriptor;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;


public class ProcessorRegistry {

    private Map<String, Class<? extends Processor<? extends Annotation>>> registry = new HashMap<String, Class<? extends Processor<? extends Annotation>>>();

    public ProcessorRegistry() {
        this.register( InheritanceModel.class, InheritanceProcessor.class );
        this.register( Getter.class, GetterProcessor.class );
        this.register( Setter.class, SetterProcessor.class );
        this.register( ToString.class, ToStringProcessor.class );
        this.register( Equality.class, EqualityProcessor.class );
    }

    public Processor<? extends Annotation> getProcessorForAnnotation( Class<? extends Annotation> annotation ) {
        try {
            return registry.get( annotation.getName() ).newInstance();
        } catch ( InstantiationException e ) {
            e.printStackTrace();
        } catch ( IllegalAccessException e ) {
            e.printStackTrace();
        }
        return null;
    }

    public Processor<? extends Annotation> getProcessorForAnnotation( String annotation ) {
        try {
            return registry.get( annotation ).newInstance();
        } catch ( InstantiationException e ) {
            e.printStackTrace();
        } catch ( IllegalAccessException e ) {
            e.printStackTrace();
        }
        return null;
    }

    public void register( Class<? extends Annotation> annotation, Class<? extends Processor<? extends Annotation>> processor ) {
        registry.put( annotation.getName(), processor );
    }

    public void register( String annotation, Class<? extends Processor<? extends Annotation>> processor ) {
        registry.put( annotation, processor );
    }

    public void unregister( Class<? extends Annotation> annotation ) {
        registry.remove( annotation.getName() );
    }

    public void unregister( String annotation ) {
        registry.remove( annotation );
    }


    public boolean canManage( Class<? extends Annotation> annotation ) {
        return registry.containsKey( annotation.getName() );
    }

    public boolean canManage( String annotation ) {
        return registry.containsKey( annotation );
    }
}
