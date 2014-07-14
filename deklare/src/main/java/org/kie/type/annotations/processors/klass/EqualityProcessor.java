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
                    .openBody().doReturn().invoke().on( System.class.getName() ).method( "identityHashCode" ).args().self();
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
        Block<JavaClassSource,MethodSource<JavaClassSource>> body = m.openBody();

            body.doDeclare().type( int.class ).name( "code" ).init().literal( 1 ).noMore().done();

            for ( Field field : filterFields( target ) ) {
                ExpressionFactory<JavaClassSource, ? extends Argument<JavaClassSource,AssignStatement<JavaClassSource,Block<JavaClassSource,MethodSource<JavaClassSource>>>>>
                        factory = body.doAssign()
                        .toVar( "code" )
                        .expr().operator( "+" ).args()
                            .operator( "*" ).args().
                                literal( 31 ).next()
                                .var( "result" )
                            .noMore().next();  // ....

                if ( "boolean".equals( field.getType().getName() ) ) {
                    factory.ternary()
                            .condition().field( field.getName() ).noMore()
                            .yes().literal( 1231 ).noMore()
                            .no().literal( 1237 );
                } else if ( "long".equals( field.getType().getName() ) ) {
                    factory.cast().as( int.class ).expr()
                            .operator( "^" ).args()
                                .field( field.getName() ).next()
                                .operator( ">>>" ).args()
                                    .field( field.getName() ).next()
                                    .literal( 32 );
                    // attr_hash ::== (int) (longAttr ^ (longAttr >>> 32))
                } else if ( "float".equals( field.getType().getName() ) ) {
                    factory.invoke().on( Float.class ).method( "floatToIntBits" ).args().field( field.getName() );
                    // attr_hash ::== Float.floatToIntBits( floatAttr );
                } else if ( "double".equals( field.getType().getName() ) ) {
                    factory.cast().as( int.class ).expr()
                        .operator( "^" ).args()
                            .invoke().on( Double.class ).method( "doubleToLongBits" ).args().field( field.getName() ).noMore().next()
                            .operator( ">>>" ).args()
                                .invoke().on( Double.class ).method( "doubleToLongBits" ).args().field( field.getName() ).noMore().next()
                                .literal( 32 );
                    // attr_hash ::== (int) (Double.doubleToLongBits( doubleAttr ) ^ (Double.doubleToLongBits( doubleAttr ) >>> 32));
                } else if ( field.getType().isArray() ) {
                    factory.ternary()
                            .condition().operator( "==" ).args()
                                .field( field.getName() ).next()
                                .nil().noMore().noMore()
                            .yes().zero( int.class ).noMore()
                            .no().invoke()
                                .on().klass( Arrays.class ).noMore()
                                .method( "hashCode" ).args().field( field.getName() );

                } else if ( ! field.getType().isPrimitive() ) {
                    // attr_hash ::== ((objAttr == null) ? 0 : objAttr.hashCode());
                        factory.ternary()
                            .condition().operator( "==" ).args()
                                .field( field.getName() ).next()
                                .nil().noMore().noMore()
                            .yes().zero( int.class ).noMore()
                            .no().invoke()
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
        Block<JavaClassSource,MethodSource<JavaClassSource>> body = m.openBody();

        // if ( this == obj ) return true;
        body.doIf().condition().operator( "==" ).args().self().next().var( "obj" ).noMore().noMore()
                .thenDo().doReturn().yes();

        // if ( obj == null ) return false;
        body.doIf().condition().operator( "==" ).args().nil().next().var( "obj" ).noMore().noMore()
                .thenDo().doReturn().no();

        // if ( getClass() != obj.getClass() ) return false;
        body.doIf().condition().operator( "!=" ).args()
                    .invoke().method( "getClass" ).next()
                    .invoke().on().var( "obj" ).noMore().method( "getClass" ).noMore().noMore()
                .thenDo().doReturn().yes();

        // final <classname> other = (<classname>) obj;
        body.doDeclare().type( target.getName() ).name( "other" )
                .init().cast().as( target.getName() ).expr().var( "obj" );

        for ( Field field : filterFields( target ) ) {
            if ( field.getType().isPrimitive() && field.getType().getArrayDimensions() == 0 ) {
                // if <attr> != obj.<attr> return false;
                    body.doIf().condition().operator( "!=" ).args()
                            .field( field.getName() ).next()
                            .var( "obj" ).dot().field( field.getName() ).noMore().noMore()
                        .thenDo().doReturn().no();
            } else if ( field.getType().getArrayDimensions() > 0 ) {
                    // if ( ! Arrays.equals( this.<attr>, obj.<attr> ) ) return false;
                    body.doIf().condition().not().invoke()
                            .on().klass( Arrays.class ).noMore()
                            .method( "equals" )
                            .args().field( field.getName() )
                                .next().var( "obj" ).dot().field( field.getName() ).noMore().noMore()
                        .thenDo().doReturn().no();
            } else {
                // if ( this.<attr> == null && other.<attr> != null ||
                //      this.<attr> != null && ! this.<attr>.equals( other.<attr> ) ) return false;
                    body.doIf().condition().operator( "||" ).args()
                            .operator( "&&" ).args()
                                .operator( "==" ).args().field( field.getName() ).next().nil().noMore()
                                .next()
                                .operator( "!=" ).args().var( "obj" ).dot().field( field.getName() ).next().nil().noMore()
                                .noMore()
                            .next()
                            .operator( "&&" ).args()
                                .operator( "!=" ).args().field( field.getName() ).next().nil().noMore()
                                .next()
                                .not().invoke()
                                        .on().field( field.getName() ).noMore()
                                        .method( "equals" )
                                        .args().var( "obj" ).dot().field( field.getName() ).noMore().noMore()
                                .noMore()
                            .noMore()
                        .thenDo().doReturn().no();
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


