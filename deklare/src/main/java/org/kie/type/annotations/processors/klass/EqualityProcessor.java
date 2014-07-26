package org.kie.type.annotations.processors.klass;

import org.jboss.forge.roaster.model.expressions.Argument;
import org.jboss.forge.roaster.model.Block;
import org.jboss.forge.roaster.model.expressions.ExpressionFactory;
import org.jboss.forge.roaster.model.Field;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaEnumSource;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.jboss.forge.roaster.model.statements.AssignStatement;
import org.kie.type.annotations.Equality;
import org.kie.type.annotations.Fields;
import org.kie.type.annotations.Key;
import org.kie.type.annotations.ToString;
import org.kie.type.model.TypeDescriptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
                    .setBody().addReturn().invoke().staticMethod( "identityHashCode", System.class ).addArgument().thisLiteral();
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
        Block<JavaClassSource,MethodSource<JavaClassSource>> body = m.setBody();

            body.addDeclare().setVariable( "code", int.class ).setInitExpression().literal( 1 ).noMore().done();

            for ( Field field : filterFields( target ) ) {
                ExpressionFactory<JavaClassSource, ? extends Argument<JavaClassSource,AssignStatement<JavaClassSource,Block<JavaClassSource,MethodSource<JavaClassSource>>>>>
                        factory = body.addAssign()
                        .setVariableLeftExpression( "code" )
                        .setRightExpression().operator( "+" ).addArgument()
                            .operator( "*" ).addArgument().
                                literal( 31 ).nextArgument()
                                .variableRef( "result" )
                            .noMore().nextArgument();  // ....

                if ( "boolean".equals( field.getType().getName() ) ) {
                    factory.ternary()
                            .setCondition().field( field.getName() ).noMore()
                            .setIfExpression().literal( 1231 ).noMore()
                            .setElseExpression().literal( 1237 );
                } else if ( "long".equals( field.getType().getName() ) ) {
                    factory.cast( int.class )
                            .operator( "^" ).addArgument()
                                .field( field.getName() ).nextArgument()
                                .operator( ">>>" ).addArgument()
                                    .field( field.getName() ).nextArgument()
                                    .literal( 32 );
                    // attr_hash ::== (int) (longAttr ^ (longAttr >>> 32))
                } else if ( "float".equals( field.getType().getName() ) ) {
                    factory.invoke().staticMethod( "floatToIntBits", Float.class ).addArgument().field( field.getName() );
                    // attr_hash ::== Float.floatToIntBits( floatAttr );
                } else if ( "double".equals( field.getType().getName() ) ) {
                    factory.cast( int.class )
                        .operator( "^" ).addArgument()
                            .invoke().staticMethod( "doubleToLongBits", Double.class ).addArgument().field( field.getName() ).noMore().nextArgument()
                            .operator( ">>>" ).addArgument()
                                .invoke().staticMethod( "doubleToLongBits", Double.class ).addArgument().field( field.getName() ).noMore().nextArgument()
                                .literal( 32 );
                    // attr_hash ::== (int) (Double.doubleToLongBits( doubleAttr ) ^ (Double.doubleToLongBits( doubleAttr ) >>> 32));
                } else if ( field.getType().isArray() ) {
                    factory.ternary()
                            .setCondition().operator( "==" ).addArgument()
                                .field( field.getName() ).nextArgument()
                                .nullLiteral().noMore().noMore()
                            .setIfExpression().zeroLiteral( int.class ).noMore()
                            .setElseExpression().invoke()
                                .on().classLiteral( Arrays.class ).noMore()
                                .method( "hashCode" ).addArgument().field( field.getName() );

                } else if ( ! field.getType().isPrimitive() ) {
                    // attr_hash ::== ((objAttr == null) ? 0 : objAttr.hashCode());
                        factory.ternary()
                            .setCondition().operator( "==" ).addArgument()
                                .field( field.getName() ).nextArgument()
                                .nullLiteral().noMore().noMore()
                            .setIfExpression().zeroLiteral( int.class ).noMore()
                            .setElseExpression().invoke()
                                    .on().field( field.getName() ).noMore()
                                    .method( "hashCode" );
                } else {
                    factory.field( field.getName() );
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
        Block<JavaClassSource,MethodSource<JavaClassSource>> body = m.setBody();

        // if ( this == obj ) return true;
        body.addIf().setCondition().operator( "==" ).addArgument().thisLiteral().nextArgument().variableRef( "obj" ).noMore().noMore()
                .setThenBlock().addReturn().trueLiteral();

        // if ( obj == null ) return false;
        body.addIf().setCondition().operator( "==" ).addArgument().nullLiteral().nextArgument().variableRef( "obj" ).noMore().noMore()
                .setThenBlock().addReturn().falseLiteral();

        // if ( getClass() != obj.getClass() ) return false;
        body.addIf().setCondition().operator( "!=" ).addArgument()
                    .invoke().method( "getClass" ).nextArgument()
                    .invoke().on().variableRef( "obj" ).noMore().method( "getClass" ).noMore().noMore()
                .setThenBlock().addReturn().trueLiteral();

        // final <classname> other = (<classname>) obj;
        body.addDeclare().setVariable( "other", target.getName() )
                .setInitExpression().cast( target.getName() ).variableRef( "obj" );

        for ( Field field : filterFields( target ) ) {
            if ( field.getType().isPrimitive() && field.getType().getArrayDimensions() == 0 ) {
                // if <attr> != obj.<attr> return false;
                    body.addIf().setCondition().operator( "!=" ).addArgument()
                            .field( field.getName() ).nextArgument()
                            .variableRef( "obj" ).dot().field( field.getName() ).noMore().noMore()
                        .setThenBlock().addReturn().falseLiteral();
            } else if ( field.getType().getArrayDimensions() > 0 ) {
                    // if ( ! Arrays.equals( this.<attr>, obj.<attr> ) ) return false;
                    body.addIf().setCondition().not().invoke()
                            .on().classLiteral( Arrays.class ).noMore()
                            .method( "equals" )
                            .addArgument().field( field.getName() )
                                .nextArgument().variableRef( "obj" ).dot().field( field.getName() ).noMore().noMore()
                        .setThenBlock().addReturn().falseLiteral();
            } else {
                // if ( this.<attr> == null && other.<attr> != null ||
                //      this.<attr> != null && ! this.<attr>.equals( other.<attr> ) ) return false;
                    body.addIf().setCondition().operator( "||" ).addArgument()
                            .operator( "&&" ).addArgument()
                                .operator( "==" ).addArgument().field( field.getName() ).nextArgument().nullLiteral().noMore()
                                .nextArgument()
                                .operator( "!=" ).addArgument().variableRef( "obj" ).dot().field( field.getName() ).nextArgument().nullLiteral().noMore()
                                .noMore()
                            .nextArgument()
                            .operator( "&&" ).addArgument()
                                .operator( "!=" ).addArgument().field( field.getName() ).nextArgument().nullLiteral().noMore()
                                .nextArgument()
                                .not().invoke()
                                        .on().field( field.getName() ).noMore()
                                        .method( "equals" )
                                        .addArgument().variableRef( "obj" ).dot().field( field.getName() ).noMore().noMore()
                                .noMore()
                            .noMore()
                        .setThenBlock().addReturn().falseLiteral();
            }
        }

        /*
                 // for each key field
            int count = 0;
            for ( FieldDefinition field : classDef.getFieldsDefinitions() ) {
                if ( field.isKey() ) {
                    count++;

                    Label goNext = new Label();

                    if ( BuildUtils.isPrimitive(field.getTypeName()) ) {
                        // if attr is primitive

                        // if ( this.<attr> != other.<booleanAttr> ) return false;
                        mv.visitVarInsn( Opcodes.ALOAD,
                                0 );


                        visitFieldOrGetter(mv, classDef, field);

                        mv.visitVarInsn(Opcodes.ALOAD,
                                2);

                        visitFieldOrGetter(mv, classDef, field);

                        if ( field.getTypeName().equals( "long" ) ) {
                            mv.visitInsn( Opcodes.LCMP );
                            mv.visitJumpInsn( Opcodes.IFEQ,
                                    goNext );
                        } else if ( field.getTypeName().equals( "double" ) ) {
                            mv.visitInsn( Opcodes.DCMPL );
                            mv.visitJumpInsn( Opcodes.IFEQ,
                                    goNext );
                        } else if ( field.getTypeName().equals( "float" ) ) {
                            mv.visitInsn( Opcodes.FCMPL );
                            mv.visitJumpInsn( Opcodes.IFEQ,
                                    goNext );
                        } else {
                            // boolean, byte, char, short, int
                            mv.visitJumpInsn( Opcodes.IF_ICMPEQ,
                                    goNext );
                        }
                        mv.visitInsn( Opcodes.ICONST_0 );
                        mv.visitInsn( Opcodes.IRETURN );
                    } else {
                        // if attr is not a primitive

                        // if ( this.<attr> == null && other.<attr> != null ||
                        //      this.<attr> != null && ! this.<attr>.equals( other.<attr> ) ) return false;
                        mv.visitVarInsn( Opcodes.ALOAD,
                                0 );

                        visitFieldOrGetter(mv, classDef, field);

                        Label secondIfPart = new Label();
                        mv.visitJumpInsn( Opcodes.IFNONNULL,
                                secondIfPart );

                        // if ( other.objAttr != null ) return false;
                        mv.visitVarInsn( Opcodes.ALOAD,
                                2 );

                        visitFieldOrGetter(mv, classDef, field);

                        Label returnFalse = new Label();
                        mv.visitJumpInsn( Opcodes.IFNONNULL,
                                returnFalse );

                        mv.visitLabel( secondIfPart );
                        mv.visitVarInsn( Opcodes.ALOAD,
                                0 );

                        visitFieldOrGetter(mv, classDef, field);

                        mv.visitJumpInsn( Opcodes.IFNULL,
                                goNext );

                        mv.visitVarInsn( Opcodes.ALOAD,
                                0 );

                        visitFieldOrGetter(mv, classDef, field);

                        mv.visitVarInsn( Opcodes.ALOAD,
                                2 );

                        visitFieldOrGetter(mv, classDef, field);

                        if ( ! BuildUtils.isArray( field.getTypeName() ) ) {
                            mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL,
                                    "java/lang/Object",
                                    "equals",
                                    "(Ljava/lang/Object;)Z" );
                        } else {
                            mv.visitMethodInsn( Opcodes.INVOKESTATIC,
                                    "java/util/Arrays",
                                    "equals",
                                    "(" +
                                            BuildUtils.arrayType( field.getTypeName() ) +
                                            BuildUtils.arrayType( field.getTypeName() ) +
                                    ")Z" );
                        }
                        mv.visitJumpInsn( Opcodes.IFNE,
                                goNext );

                        mv.visitLabel( returnFalse );
                        mv.visitInsn( Opcodes.ICONST_0 );
                        mv.visitInsn( Opcodes.IRETURN );
                    }
                    mv.visitLabel( goNext );
                }
            }
            if ( count > 0 ) {
                mv.visitInsn( Opcodes.ICONST_1 );
            } else {
                mv.visitInsn( Opcodes.ICONST_0 );
            }
            mv.visitInsn( Opcodes.IRETURN );
            Label lastLabel = null;

            mv.visitMaxs( 0,
                    0 );
            mv.visitEnd();
        }
        */

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


