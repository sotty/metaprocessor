package org.kie.type.annotations.processors;

import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaEnumSource;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.kie.type.model.TypeDescriptor;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;

public interface Processor<T extends Annotation> {

    public void processSource( TypeDescriptor descriptor );

    public void preProcessSource( JavaClassSource source );

    public JavaSource processClass( JavaClassSource source, JavaClassSource target, TypeDescriptor descr );

    public JavaSource processInterface( JavaClassSource source, JavaInterfaceSource target, TypeDescriptor descr );

    public JavaSource processEnum( JavaClassSource source, JavaEnumSource target, TypeDescriptor descr );

    public LinkedHashMap<String, TypeDescriptor> processPackage( LinkedHashMap<String, TypeDescriptor> types, org.jboss.forge.roaster.model.Annotation ann );

    public Class<T> getTargetAnnotation();

    public boolean isGenerator();

    public boolean isPackageAnnotation();

    public boolean isClassAnnotation();

    public boolean isFieldAnnotation();

}
