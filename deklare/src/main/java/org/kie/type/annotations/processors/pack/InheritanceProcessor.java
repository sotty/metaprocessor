package org.kie.type.annotations.processors.pack;

import org.jboss.forge.roaster.model.Annotation;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaEnumSource;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.kie.type.annotations.InheritanceMode;
import org.kie.type.annotations.InheritanceModel;
import org.kie.type.annotations.processors.pack.inheritance.AsIsInheritanceHandlingStrategy;
import org.kie.type.annotations.processors.pack.inheritance.FlatteningInheritanceHandlingStrategy;
import org.kie.type.annotations.processors.pack.inheritance.InheritanceHandlingStrategy;
import org.kie.type.model.TypeDescriptor;

import java.util.LinkedHashMap;

public class InheritanceProcessor extends PackageAnnotationProcessor<InheritanceModel> {

    private InheritanceHandlingStrategy shaper;

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
    public LinkedHashMap<String, TypeDescriptor> processPackage( LinkedHashMap<String, TypeDescriptor> types,
                                                                 org.jboss.forge.roaster.model.Annotation ann ) {
        shaper = initStrategy( ann );
        shaper.analyzeModel( types );
        shaper.reshapeModel( types );
        return types;
    }

    private InheritanceHandlingStrategy initStrategy( Annotation ann ) {
        InheritanceMode mode;
        if ( ann == null ) {
            mode = InheritanceMode.DEFAULT;
        } else {
            mode = (InheritanceMode) ann.getEnumValue( InheritanceMode.class );
        }
        switch ( mode ) {
            case VARIANT:
                throw new UnsupportedOperationException( "TODO" );
            case AREA:
                throw new UnsupportedOperationException( "TODO" );
            case OPTIMIZE:
                throw new UnsupportedOperationException( "TODO" );
            case FLAT:
                return new FlatteningInheritanceHandlingStrategy();
            case DEFAULT:
            default:
                return new AsIsInheritanceHandlingStrategy();
        }
    }

    @Override
    public Class<InheritanceModel> getTargetAnnotation() {
        return InheritanceModel.class;
    }


}
