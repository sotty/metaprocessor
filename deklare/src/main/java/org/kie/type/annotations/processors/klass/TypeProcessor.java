package org.kie.type.annotations.processors.klass;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.Annotation;
import org.jboss.forge.roaster.model.AnnotationTarget;
import org.jboss.forge.roaster.model.Field;
import org.jboss.forge.roaster.model.ValuePair;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.AnnotationTargetSource;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaEnumSource;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.kie.type.annotations.Type;
import org.kie.type.annotations.processors.klass.ClassAnnotationProcessor;
import org.kie.type.model.TypeDescriptor;

import java.util.List;

public class TypeProcessor extends ClassAnnotationProcessor<Type> {

    @Override
    public void processSource( TypeDescriptor descr ) {
        JavaClassSource src = descr.getSourceAST();
        AnnotationSource<JavaClassSource> type = src.getAnnotation( Type.class );
        if ( type != null ) {
            boolean asInterface = Boolean.valueOf( type.getLiteralValue( "asInterface" ) );
            if ( asInterface ) {
                createInterfaceTarget( descr );
            }
            boolean asClass = Boolean.valueOf( type.getLiteralValue( "asClass" ) );
            if ( asClass ) {
                createClassTarget( descr, ! asInterface );
            }
            boolean asEnum = Boolean.valueOf( type.getLiteralValue( "asEnum" ) );
            if ( asEnum ) {
                createEnumTarget( descr );
            }
        } else {
            processSourceAsDefault( descr );
        }
    }

    private void processSourceAsDefault( TypeDescriptor descr ) {
        createClassTarget( descr, false );
        createInterfaceTarget( descr );
    }

    private void createEnumTarget( TypeDescriptor descr ) {
        throw new UnsupportedOperationException( "TODO" );
    }

    private void createInterfaceTarget( TypeDescriptor descr ) {
        JavaClassSource src = descr.getSourceAST();
        JavaInterfaceSource tgtIfs = Roaster.create( JavaInterfaceSource.class );
        markAsGenerated( tgtIfs );

        tgtIfs.setName( src.getName() );
        tgtIfs.setPackage( src.getPackage() );
        tgtIfs.setPublic();
        for ( String x : src.getInterfaces() ) {
            tgtIfs.addInterface( x );
        }
        copyAnnotations( src, tgtIfs );

        descr.addTargetAST( tgtIfs );
    }

    private void createClassTarget( TypeDescriptor descr, boolean classOnly ) {
        JavaClassSource src = descr.getSourceAST();
        JavaClassSource tgtKls = Roaster.create( JavaClassSource.class );
        markAsGenerated( tgtKls );

        tgtKls.setName( classOnly ? src.getName() : src.getName() + "Impl" );
        tgtKls.setPackage( src.getPackage() );
        tgtKls.setPublic();
        for ( String x : src.getInterfaces() ) {
            tgtKls.addInterface( x );
        }
        if ( ! classOnly ) {
            tgtKls.addInterface( src.getQualifiedName() );
        }
        tgtKls.setSuperType( src.getSuperType() );
        copyAnnotations( src, tgtKls );

        for ( Field<JavaClassSource> fld : src.getFields() ) {
            FieldSource<JavaClassSource> tgtField = tgtKls
                    .addField()
                    .setProtected()
                    .setName( fld.getName() )
                    .setType( fld.getType().getName() );
            copyAnnotations( fld, tgtField );
        }

        descr.addTargetAST( tgtKls );
    }

    @Override
    public JavaSource processClass( JavaClassSource source, JavaClassSource target, TypeDescriptor descr ) {
        return null;
    }

    @Override
    public JavaSource processInterface( JavaClassSource source, JavaInterfaceSource target, TypeDescriptor descr ) {
        return null;
    }

    @Override
    public JavaSource processEnum( JavaClassSource source, JavaEnumSource target, TypeDescriptor descr ) {
        return null;
    }

    @Override
    public Class<Type> getTargetAnnotation() {
        return Type.class;
    }

    @Override
    public boolean isClassProcessable( JavaSource source ) {
        return source.hasAnnotation( getTargetAnnotation() );
    }

    @Override
    public boolean isGenerator() {
        return true;
    }
}
