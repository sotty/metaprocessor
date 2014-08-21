package org.kie.type.annotations.processors.attr;

import org.jboss.forge.roaster.model.Field;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaEnumSource;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;
import org.kie.type.annotations.Getter;
import org.kie.type.model.TypeDescriptor;
import org.kie.type.utils.TypeHelper;

import static org.jboss.forge.roaster.model.statements.Statements.*;
import static org.jboss.forge.roaster.model.expressions.Expressions.*;

public class GetterProcessor extends FieldAnnotationProcessor<Getter> {

    protected void processEnumField( JavaClassSource source, JavaEnumSource target, TypeDescriptor descr, Field<JavaClassSource> field ) {

    }



    protected void processClassField( JavaClassSource source, JavaClassSource target, TypeDescriptor descr, Field<JavaClassSource> field ) {
        target.addMethod()
                .setPublic()
                .setBody( newReturn().setReturn( field( field.getName() ) ) )
                .setName( TypeHelper.getter( field.getName(), field.getType().getQualifiedName() ) )
                .addParameter( TypeHelper.getTypeName( field ), field.getName() );
    }

    protected void processInterfaceField( JavaClassSource source, JavaInterfaceSource target, TypeDescriptor descr, Field<JavaClassSource> field ) {
        target.addMethod()
                .setPublic()
                .setName( TypeHelper.getter( field.getName(), field.getType().getQualifiedName() ) )
                .addParameter( TypeHelper.getTypeName( field ), field.getName() );
    }

    @Override
    public Class<Getter> getTargetAnnotation() {
        return Getter.class;
    }

    protected boolean isFieldProcessable( Field<JavaClassSource> field, JavaClassSource source ) {
        return field.hasAnnotation( getTargetAnnotation() ) || source.hasAnnotation( getTargetAnnotation() );
    }

}
