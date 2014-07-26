package org.kie.type.annotations.processors.attr;

import org.jboss.forge.roaster.model.Field;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaEnumSource;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;
import org.kie.type.annotations.Setter;
import org.kie.type.model.TypeDescriptor;
import org.kie.type.utils.TypeHelper;

public class SetterProcessor extends FieldAnnotationProcessor<Setter> {

    protected void processEnumField( JavaClassSource source, JavaEnumSource target, TypeDescriptor descr, Field<JavaClassSource> field ) {

    }

    protected void processClassField( JavaClassSource source, JavaClassSource target, TypeDescriptor descr, Field<JavaClassSource> field ) {
        target.addMethod()
                .setPublic()
                .setBody()
                    .addAssign().setFieldLeftExpression( field.getName() ).setVariableRightExpression( field.getName() )
                .closeBlock()
                .setName( TypeHelper.setter( field.getName(), field.getType().getQualifiedName() ) )
                .addParameter( TypeHelper.getTypeName( field ), field.getName() );
    }

    protected void processInterfaceField( JavaClassSource source, JavaInterfaceSource target, TypeDescriptor descr, Field<JavaClassSource> field ) {
        target.addMethod()
                .setPublic()
                .setName( TypeHelper.setter( field.getName(), field.getType().getQualifiedName() ) )
                .addParameter( TypeHelper.getTypeName( field ), field.getName() );
    }

    @Override
    public Class<Setter> getTargetAnnotation() {
        return Setter.class;
    }

    protected boolean isFieldProcessable( Field<JavaClassSource> field, JavaClassSource source ) {
        return field.hasAnnotation( getTargetAnnotation() ) || source.hasAnnotation( getTargetAnnotation() );
    }

}
