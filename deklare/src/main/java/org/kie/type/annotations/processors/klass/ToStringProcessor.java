package org.kie.type.annotations.processors.klass;

import org.jboss.forge.roaster.model.Block;
import org.jboss.forge.roaster.model.Field;
import org.jboss.forge.roaster.model.expressions.ExpressionFactory;
import org.jboss.forge.roaster.model.expressions.OperatorExpression;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaEnumSource;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.jboss.forge.roaster.model.statements.InvokeStatement;
import org.kie.type.annotations.Fields;
import org.kie.type.annotations.Key;
import org.kie.type.annotations.ToString;
import org.kie.type.model.TypeDescriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ToStringProcessor extends ClassAnnotationProcessor<ToString> {


    @Override
    public JavaSource processClass( JavaClassSource source, JavaClassSource target, TypeDescriptor descr ) {
        MethodSource<JavaClassSource> m = target.addMethod()
                .setName( "toString" )
                .setReturnType( String.class )
                .setPublic();

        Block<JavaClassSource,MethodSource<JavaClassSource>> body = m.setBody();
            body.addDeclare().setVariable( "sb", StringBuilder.class ).initDefault();

            body.addInvoke().method( "append" )
                    .on().variableRef( "sb" ).noMore()
                    .addArgument()
                        .literal( source.getName() + " { " ).noMore()
                .done();

        List<Field<JavaClassSource>> fields = filterFields( target );
            int N = fields.size() - 1;
            for ( int j = 0; j <= N; j++ ) {
                Field<JavaClassSource> f = fields.get( j );
                ExpressionFactory<JavaClassSource, OperatorExpression<JavaClassSource, InvokeStatement<JavaClassSource, Block<JavaClassSource, MethodSource<JavaClassSource>>>>> builder = body.addInvoke()
                        .onVariable( "sb" )
                        .method( "append" )
                        .addArgument()
                                .operator( "+" ).addArgument()
                                        .literal( f.getName() ).addArgument()
                                        .literal( " = " ).addArgument();
                if ( f.getType().getArrayDimensions() == 0 ) {
                    builder .field( f.getName() ).addArgument()
                            .literal( j < N ? ", " : "" );
                } else {
                    builder.invoke()
                                .on().classLiteral( Arrays.class ).noMore()
                                .method( "toString" )
                                .addArgument().field( f.getName() ).noMore().addArgument()
                            .literal( j < N ? ", " : "" );
                }
            }

            body.addInvoke().onVariable( "sb" ).method( "append" )
                    .addArgument()
                        .literal( " } " )
                    .noMore()
                .done();

            body.addReturn().variable( "sb" );
        return target;
    }

    @Override
    public JavaSource processInterface( JavaClassSource source, JavaInterfaceSource target, TypeDescriptor descr ) {
        return target;
    }

    @Override
    public JavaSource processEnum( JavaClassSource source, JavaEnumSource target, TypeDescriptor descr ) {
        throw new UnsupportedOperationException( "TODO" );
    }

    @Override
    public Class<ToString> getTargetAnnotation() {
        return ToString.class;
    }

    @Override
    public boolean isClassProcessable( JavaSource source ) {
        return source.hasAnnotation( ToString.class.getName() );
    }

    private List<Field<JavaClassSource>> filterFields( JavaClassSource target ) {
        List<Field<JavaClassSource>> chosenFields = new ArrayList<Field<JavaClassSource>>( target.getFields().size() );

        Fields policy = Fields.ALL_INHERITED;
        AnnotationSource<JavaClassSource> ts = target.getAnnotation( ToString.class );
        if ( ts != null) {
            if ( ts.hasValue( "include" ) ) {
                policy = ts.getEnumValue( Fields.class, "include" );
            }
        }

        switch ( policy ) {
            case NONE:
                break;
            case LOCAL:
                chosenFields.addAll( target.getFields() );
                break;
            case KEYS:
            case KEYS_INHERITED:
                //TODO //FIXME DO inheritance properly
                for ( Field f : target.getFields() ) {
                    if ( f.hasAnnotation( Key.class ) ) {
                        chosenFields.add( f );
                    }
                }
                break;
            case ALL_INHERITED:
                default:
                chosenFields.addAll( target.getFields() );
        }

        return chosenFields;
    }


}
