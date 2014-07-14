package org.kie.type.model;

import org.jboss.forge.roaster.model.Annotation;
import org.jboss.forge.roaster.model.JavaPackageInfo;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaPackageInfoSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.kie.type.annotations.processors.Processor;
import org.kie.type.annotations.processors.ProcessorRegistry;
import org.kie.type.annotations.processors.klass.TypeProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PackageDescriptor {

    private String packageName;
    private LinkedHashMap<String,TypeDescriptor> types;
    private JavaPackageInfo packageInfo;

    public PackageDescriptor( String packageName, LinkedHashMap<String, TypeDescriptor> types ) {
        this.packageName = packageName;
        this.types = types;
    }

    public String getPackageName() {
        return packageName;
    }

    public Map<String, TypeDescriptor> getTypes() {
        return types;
    }

    public void add( String name, TypeDescriptor descr ) {
        types.put( name, descr );
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder( "PackageDescriptor{ " + packageName + "} \n" );
        for ( TypeDescriptor d : types.values() ) {
            for ( JavaSource js : d.getTargetASTs() ) {
                s.append( js ).append( "\n\n" );
            }
        }
        return s.toString();
    }

    public void init( ProcessorRegistry registry ) {

        for ( TypeDescriptor type : types.values() ) {
            preinitType( type, registry );
        }

        for ( TypeDescriptor type : types.values() ) {
            new TypeProcessor().processSource( type );
        }

        for ( Object o : this.getPackageInfo().getAnnotations() ) {
            Annotation ann = (Annotation) o;
            if ( registry.canManage( ann.getQualifiedName() ) ) {
                Processor p = registry.getProcessorForAnnotation( ann.getQualifiedName() );
                if ( p.isPackageAnnotation() ) {
                    p.processPackage( types, ann );
                }
            }
        }

        for ( TypeDescriptor type : types.values() ) {
            initType( type, registry );
        }
    }


    private void preinitType( TypeDescriptor type, ProcessorRegistry registry ) {
        JavaClassSource src = type.getSourceAST();

        for ( Annotation ann : src.getAnnotations() ) {
            if ( registry.canManage( ann.getQualifiedName() ) ) {
                Processor<? extends java.lang.annotation.Annotation> p = registry.getProcessorForAnnotation( ann.getQualifiedName() );
                p.preProcessSource( src );
            }
        }
    }

    private void initType( TypeDescriptor type, ProcessorRegistry registry ) {

        JavaClassSource src = type.getSourceAST();
        List<Processor<? extends java.lang.annotation.Annotation>> processors = new ArrayList<Processor<? extends java.lang.annotation.Annotation>>();

        for ( Annotation ann : src.getAnnotations() ) {
            if ( registry.canManage( ann.getQualifiedName() ) ) {
                Processor<? extends java.lang.annotation.Annotation> p = registry.getProcessorForAnnotation( ann.getQualifiedName() );
                if ( p.getClass() == TypeProcessor.class ) {
                    // already applied by default
                    continue;
                }
                processors.add( p );
            }
        }

        Collections.sort( processors, new Comparator<Processor<? extends java.lang.annotation.Annotation>>() {
            @Override
            public int compare( Processor<? extends java.lang.annotation.Annotation> t, Processor<? extends java.lang.annotation.Annotation> o ) {
                if ( t.isGenerator() ) {
                    return o.isGenerator() ? 0 : 1;
                } else if ( o.isGenerator() ) {
                    return -1;
                }

                if ( t.isPackageAnnotation() ) {
                    return o.isPackageAnnotation() ? 0 : 1;
                } else if ( o.isPackageAnnotation() ) {
                    return -1;
                }

                if ( t.isClassAnnotation() ) {
                    return o.isClassAnnotation() ? 0 : 1;
                } else if ( o.isClassAnnotation() ) {
                    return -1;
                }

                if ( t.isFieldAnnotation() ) {
                    return o.isFieldAnnotation() ? 0 : 1;
                }
                return 0;
            }
        } );

        for ( Processor p : processors ) {
            if ( ! p.isGenerator() ) {
                p.processSource( type );
            }
        }

    }

    public void setPackageInfo( JavaPackageInfo packageInfo ) {
        this.packageInfo = packageInfo;
    }

    public JavaPackageInfo getPackageInfo() {
        return packageInfo;
    }

    public void merge( PackageDescriptor descr ) {
        throw new UnsupportedOperationException( "TODO" );
    }
}
