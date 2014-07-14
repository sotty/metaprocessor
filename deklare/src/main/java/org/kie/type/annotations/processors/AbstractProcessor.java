package org.kie.type.annotations.processors;

import org.jboss.forge.roaster.model.AnnotationTarget;
import org.jboss.forge.roaster.model.ValuePair;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.AnnotationTargetSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaEnumSource;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.kie.type.model.TypeDescriptor;

import javax.annotation.Generated;
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class AbstractProcessor<T extends Annotation>
        implements Processor<T> {

    @Override
    public void processSource( TypeDescriptor descriptor ) {
        for ( JavaSource tgt : descriptor.getTargetASTs() ) {
            if ( tgt.isClass() ) {
                processClass( descriptor.getSourceAST(), (JavaClassSource) tgt, descriptor );
            } else if ( tgt.isInterface() ) {
                processInterface( descriptor.getSourceAST(), (JavaInterfaceSource) tgt, descriptor );
            } else if ( tgt.isEnum() ) {
                processEnum( descriptor.getSourceAST(), (JavaEnumSource) tgt, descriptor );
            }
        }
    }

    @Override
    public void preProcessSource( JavaClassSource source ) {
        // manage annotations
    }

    @Override
    public abstract JavaSource processClass( JavaClassSource source, JavaClassSource target, TypeDescriptor descr );

    @Override
    public abstract JavaSource processInterface( JavaClassSource source, JavaInterfaceSource target, TypeDescriptor descr );

    @Override
    public abstract JavaSource processEnum( JavaClassSource source, JavaEnumSource target, TypeDescriptor descr );

    @Override
    public LinkedHashMap<String, TypeDescriptor> processPackage( LinkedHashMap<String, TypeDescriptor> types, org.jboss.forge.roaster.model.Annotation ann ) {
        return types;
    }

    @Override
    public abstract Class<T> getTargetAnnotation();

    @Override
    public boolean isGenerator() {
        return false;
    }

    @Override
    public boolean isPackageAnnotation() {
        return false;
    }

    @Override
    public boolean isClassAnnotation() {
        return false;
    }

    @Override
    public boolean isFieldAnnotation() {
        return false;
    }

    protected void markAsGenerated( JavaSource tgt ) {
        tgt.addAnnotation( Generated.class );
    }

    protected void copyAnnotations( AnnotationTarget<JavaClassSource> src, AnnotationTargetSource<? extends JavaSource,?> tgt ) {
        for ( org.jboss.forge.roaster.model.Annotation<JavaClassSource> ann : src.getAnnotations() ) {
            AnnotationSource<? extends JavaSource> tgtAnn = tgt.addAnnotation( ann.getQualifiedName() );
            copyAnnotationToTarget( ann, tgtAnn );
        }
    }

    protected void copyAnnotationToTarget( org.jboss.forge.roaster.model.Annotation<JavaClassSource> ann, AnnotationSource<? extends JavaSource> tgtAnn ) {
        if ( ann.isSingleValue() ) {
            if ( ann.getLiteralValue() != null ) {
                tgtAnn.setLiteralValue( ann.getLiteralValue() );
            } else if ( ann.getClassArrayValue() != null ) {
                tgtAnn.setClassArrayValue( ann.getClassArrayValue() );
            } else if ( ann.getClassValue() != null ) {
                tgtAnn.setClassValue( ann.getClassValue() );
            } else if ( ann.getLiteralValue() != null ) {
                tgtAnn.setLiteralValue( ann.getLiteralValue() );
            } else if ( ann.getStringArrayValue() != null ) {
                tgtAnn.setStringArrayValue( ann.getStringArrayValue() );
            } else if ( ann.getStringValue() != null ) {
                tgtAnn.setStringValue( ann.getStringValue() );
            }
        } else {
            List<ValuePair> values = ann.getValues();
            for ( ValuePair val : values ) {
                String key = val.getName();
                if ( ann.getLiteralValue( key ) != null ) {
                    tgtAnn.setLiteralValue( key, ann.getLiteralValue( key ) );
                } else if ( ann.getClassArrayValue( key ) != null ) {
                    tgtAnn.setClassArrayValue( key, ann.getClassArrayValue( key ) );
                } else if ( ann.getClassValue( key ) != null ) {
                    tgtAnn.setClassValue( key, ann.getClassValue( key ) );
                } else if ( ann.getLiteralValue( key ) != null ) {
                    tgtAnn.setLiteralValue( key, ann.getLiteralValue( key ) );
                } else if ( ann.getStringArrayValue( key ) != null ) {
                    tgtAnn.setStringArrayValue( key, ann.getStringArrayValue( key ) );
                } else if ( ann.getStringValue( key ) != null ) {
                    tgtAnn.setStringValue( key, ann.getStringValue( key ) );
                }
            }
        }

    }


}
