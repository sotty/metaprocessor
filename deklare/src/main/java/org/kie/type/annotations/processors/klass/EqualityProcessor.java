package org.kie.type.annotations.processors.klass;

import org.jboss.forge.roaster.model.expressions.Argument;
import org.jboss.forge.roaster.model.Block;
import org.jboss.forge.roaster.model.expressions.ExpressionFactory;
import org.jboss.forge.roaster.model.Field;
import org.jboss.forge.roaster.model.expressions.OperatorExpression;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaEnumSource;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.jboss.forge.roaster.model.statements.AssignStatement;
import org.jboss.forge.roaster.model.statements.BlockStatement;
import org.kie.type.annotations.Equality;
import org.kie.type.annotations.Fields;
import org.kie.type.annotations.Key;
import org.kie.type.annotations.ToString;
import org.kie.type.model.TypeDescriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.jboss.forge.roaster.model.statements.Statements.*;
import static org.jboss.forge.roaster.model.expressions.Expressions.*;

public class EqualityProcessor extends ClassAnnotationProcessor<Equality> {


    @Override
    public JavaSource processClass( JavaClassSource source, JavaClassSource target, TypeDescriptor descr ) {
        AnnotationSource eq = source.getAnnotation( Equality.class );
        boolean byIdentity = Boolean.valueOf( eq.getLiteralValue( "byIdentity" ) );
        if ( byIdentity ) {
            target.addMethod()
                    .setName( "hashCode" )
                    .setReturnType( int.class )
                    .setPublic()
                    .setBody( newReturn().setReturn( invoke( "identityHashCode" ).setTarget( classLiteral( System.class ) ).addArgument( thisLiteral() ) ) );
        } else {
            generateEquals( source, target, descr );
            generateHashcode( source, target, descr );
        }
        return target;
    }

    private JavaSource generateHashcode( JavaClassSource source, JavaClassSource target, TypeDescriptor descr ) {
        MethodSource<JavaClassSource> m = target.addMethod()
                .setName( "hashCode" )
                .setReturnType( int.class )
                .setPublic();
        BlockStatement body = newBlock();
        m.setBody( body );

        body.addStatement( newDeclare().setVariable( int.class, "code" ).setInitExpression( literal( 1 ) ) );
        OperatorExpression factory;
        for ( Field field : filterFields( target ) ) {
                body.addStatement( newAssign().setLeft( var( "code" ) ).setRight(
                        factory = operator( "+" )
                                .addArgument( operator( "*" ).addArgument( literal( 31 ) ).addArgument( var( "result" ) ) )
                ) );

                if ( "boolean".equals( field.getType().getName() ) ) {
                    factory.addArgument( ternary()
                                                 .setCondition( field( field.getName() ) )
                                                 .setIfExpression( literal( 1231 ) )
                                                 .setElseExpression( literal( 1237 ) ) );
                } else if ( "long".equals( field.getType().getName() ) ) {
                    factory.addArgument( cast( int.class )
                            .operator( "^" ).addArgument( field( field.getName() ) )
                                            .addArgument( operator( ">>>" ).addArgument( field( field.getName() ) ).addArgument( literal( 32 ) ) ) );
                    // attr_hash ::== (int) (longAttr ^ (longAttr >>> 32))
                } else if ( "float".equals( field.getType().getName() ) ) {
                    factory.addArgument( invoke( "floatToIntBits" ).setTarget( classLiteral( Float.class ) ).addArgument( field( field.getName() ) ) );
                    // attr_hash ::== Float.floatToIntBits( floatAttr );
                } else if ( "double".equals( field.getType().getName() ) ) {
                    factory.addArgument( cast( int.class )
                            .operator( "^" )
                                .addArgument( invoke( "doubleToLongBits" ).setTarget( classLiteral( Double.class ) ).addArgument( field( field.getName() ) ) )
                                .addArgument( operator( ">>>" )
                                        .addArgument( invoke( "doubleToLongBits" ).setTarget( classLiteral( Double.class ) ).addArgument( field( field.getName() ) )
                                        .addArgument( literal( 32 ) ) ) ) );
                    // attr_hash ::== (int) (Double.doubleToLongBits( doubleAttr ) ^ (Double.doubleToLongBits( doubleAttr ) >>> 32));
                } else if ( field.getType().isArray() ) {
                    factory.addArgument( ternary()
                                            .setCondition( operator( "==" )
                                                                   .addArgument( field( field.getName() ) )
                                                                   .addArgument( nullLiteral() ) )
                                            .setIfExpression( zeroLiteral( int.class ) )
                                            .setElseExpression( invoke( "hashCode" ).setTarget( classLiteral( Arrays.class ) ).addArgument( field( field.getName() ) ) ) );

                } else if ( ! field.getType().isPrimitive() ) {
                    // attr_hash ::== ((objAttr == null) ? 0 : objAttr.hashCode());
                        factory.addArgument( ternary()
                                            .setCondition( operator( "==" )
                                                                   .addArgument( field( field.getName() ) )
                                                                   .addArgument( nullLiteral() ) )
                            .setIfExpression( zeroLiteral( int.class ) )
                            .setElseExpression( invoke( "hashCode" ).setTarget( field( field.getName() ) ) ) );
                } else {
                    factory.addArgument( field( field.getName() ) );
                }

            }


        return target;
    }

    private JavaSource generateEquals( JavaClassSource source, JavaClassSource target, TypeDescriptor descr ) {
        MethodSource<JavaClassSource> m = target.addMethod()
                .setName( "equals" )
                .setReturnType( boolean.class )
                .setPublic();
        m.addParameter( Object.class, "obj" );
        BlockStatement body = newBlock();
        m.setBody( body );

        // if ( this == obj ) return true;
        body.addStatement( newIf().setCondition( operator( "==" ).addArgument( thisLiteral() ).addArgument( var( "obj" ) ) )
                                    .setThen( newReturn().setReturn( literal( true ) ) ) );

        // if ( obj == null ) return false;
        body.addStatement( newIf().setCondition( operator( "==" ).addArgument( thisLiteral() ).addArgument( var( "obj" ) ) )
                                   .setThen( newReturn().setReturn( literal( false ) ) ) );

        // if ( getClass() != obj.getClass() ) return false;
        body.addStatement( newIf().setCondition( operator( "!=" )
                                                         .addArgument( invoke( "getClass" ) )
                                                         .addArgument( invoke( "getClass" ).setTarget( var( "obj" ) ) ) )
                                  .setThen( newReturn().setReturn( literal( true ) ) ) );

        // final <classname> other = (<classname>) obj;
        body.addStatement( newDeclare().setVariable( target.getName(), "other" ).setInitExpression( cast( target.getName() ).var( "obj" ) ) );

        for ( Field field : filterFields( target ) ) {
            if ( field.getType().isPrimitive() && field.getType().getArrayDimensions() == 0 ) {
                // if <attr> != obj.<attr> return false;
                    body.addStatement( newIf().setCondition( operator( "!=" )
                                                                     .addArgument( field( field.getName() ) )
                                                                     .addArgument( var( "obj" ).field( field.getName() ) ) )
                                               .setThen( newReturn().setReturn( literal( false ) ) ) );
            } else if ( field.getType().getArrayDimensions() > 0 ) {
                    // if ( ! Arrays.equals( this.<attr>, obj.<attr> ) ) return false;
                    body.addStatement( newIf().setCondition( not().invoke( "equals" )
                                                                     .setTarget( classLiteral( Arrays.class ) )
                                                                     .addArgument( field( field.getName() ) )
                                                                     .addArgument( var( "bj" ).field( field.getName() ) ) )
                                               .setThen( newReturn().setReturn( literal( false ) ) ) );
            } else {
                // if ( this.<attr> == null && other.<attr> != null ||
                //      this.<attr> != null && ! this.<attr>.equals( other.<attr> ) ) return false;
                    body.addStatement( newIf().setCondition( operator( "||" )
                                                                     .addArgument( operator( "&&" )
                                                                                           .addArgument( operator( "==" ).addArgument( field( field.getName() ) ).addArgument( nullLiteral() ) )
                                                                                           .addArgument( operator( "!=" ).addArgument( var( "obj" ).field( field.getName() ) ).addArgument( nullLiteral() ) ) )
                                                                     .addArgument( operator( "&&" )
                                                                                           .addArgument( operator( "!=" ).addArgument( field( field.getName() ) ).addArgument( nullLiteral() ) )
                                                                                          .addArgument( not().invoke( "equals" ).setTarget( field( field.getName() ) )
                                                                                                                 .addArgument( var( "obj" ).field( field.getName() ) ) ) ) )
                                               .setThen( newReturn().setReturn( literal( false ) ) ) );
            }
        }


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
    public Class<Equality> getTargetAnnotation() {
        return Equality.class;
    }

    @Override
    public boolean isClassProcessable( JavaSource source ) {
        return source.hasAnnotation( Equality.class.getName() );
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


