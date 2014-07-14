package org.kie.type.annotations.processors.attr;

import org.jboss.forge.roaster.model.Field;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaAnnotationSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaEnumSource;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.kie.type.annotations.processors.AbstractProcessor;
import org.kie.type.model.TypeDescriptor;

import java.lang.annotation.Annotation;

public abstract class FieldAnnotationProcessor<T extends Annotation> extends AbstractProcessor<T> {

    @Override
    public JavaSource processClass( JavaClassSource source, JavaClassSource target, TypeDescriptor descr ) {
        for ( Field<JavaClassSource> field : source.getFields() ) {
            if ( isFieldProcessable( field, source ) ) {
                processClassField( source, target, descr, field );
            }
        }
        return target;
    }

    @Override
    public JavaSource processInterface( JavaClassSource source, JavaInterfaceSource target, TypeDescriptor descr ) {
        for ( Field<JavaClassSource> field : source.getFields() ) {
            if ( isFieldProcessable( field, source ) ) {
                processInterfaceField( source, target, descr, field );
            }
        }
        return target;
    }

    @Override
    public JavaSource processEnum( JavaClassSource source, JavaEnumSource target, TypeDescriptor descr ) {
        for ( Field<JavaClassSource> field : source.getFields() ) {
            if ( isFieldProcessable( field, source ) ) {
                processEnumField( source, target, descr, field );
            }
        }
        return target;
    }

    protected boolean isFieldProcessable( Field<JavaClassSource> field, JavaClassSource source ) {
        return field.hasAnnotation( getTargetAnnotation() );
    }

    protected abstract void processEnumField( JavaClassSource source, JavaEnumSource target, TypeDescriptor descr, Field<JavaClassSource> field );

    protected abstract void processClassField( JavaClassSource source, JavaClassSource target, TypeDescriptor descr, Field<JavaClassSource> field );

    protected abstract void processInterfaceField( JavaClassSource source, JavaInterfaceSource target, TypeDescriptor descr, Field<JavaClassSource> field );

    @Override
    public boolean isFieldAnnotation() {
        return true;
    }

    @Override
    public void preProcessSource( JavaClassSource src ) {
        if ( src.hasAnnotation( getTargetAnnotation() ) ) {
            for ( Field f : src.getFields() ) {
                if ( ! f.hasAnnotation( getTargetAnnotation() ) ) {
                    copyAnnotationToTarget( src.getAnnotation( getTargetAnnotation() ), (( FieldSource ) f).addAnnotation( getTargetAnnotation() ) );
                }
            }
        }
    }
}
