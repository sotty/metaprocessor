package org.kie.type.model;

import org.jboss.forge.roaster.model.Annotation;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TypeDescriptor {

    private JavaClassSource sourceAST;
    private List<JavaSource> targetASTs;
    private Set<String> annotations;
    private byte[] byteCode;

    private String name;

    public TypeDescriptor( String name ) {
        this.name = name;
        this.annotations = new HashSet<String>();
        this.targetASTs = new ArrayList<JavaSource>( 3 );
    }

    public JavaClassSource getSourceAST() {
        return sourceAST;
    }

    public void setSourceAST( JavaClassSource sourceAST ) {
        this.sourceAST = sourceAST;
    }

    public Set<String> getAnnotations() {
        return annotations;
    }

    public void setAnnotations( Set<String> annotations ) {
        this.annotations = annotations;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public void addAnnotation( Annotation ann ) {
        this.annotations.add( ann.getQualifiedName() );
    }

    public Collection<JavaSource> getTargetASTs() {
        return targetASTs;
    }

    public void addTargetAST( JavaSource targetAST ) {
        this.targetASTs.add( targetAST );
    }

    public byte[] getByteCode() {
        return byteCode;
    }

    public void setByteCode( byte[] byteCode ) {
        this.byteCode = byteCode;
    }

    @Override
    public String toString() {
        return "TypeDescriptor{" +
               "sourceAST=" + sourceAST +
               ", annotations=" + annotations +
               ", name='" + name + '\'' +
               '}';
    }
}
