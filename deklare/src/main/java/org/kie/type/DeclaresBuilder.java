package org.kie.type;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaPackageInfoSource;
import org.kie.type.annotations.Type;
import org.kie.type.model.PackageDescriptor;
import org.kie.type.model.TypeDescriptor;
import org.kie.types.DeclaresBaseVisitor;
import org.kie.types.DeclaresParser;
import org.kie.types.DeclaresVisitor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DeclaresBuilder extends DeclaresBaseVisitor implements DeclaresVisitor {

    private static final Object KIE_ANNOTATION_PACKAGE = Type.class.getPackage().getName();

    private TypeDescriptor currentType;

    private PackageDescriptor model;

    private Map<String, String> imports = new HashMap<String, String>();


    public PackageDescriptor getPackage() {
        return model;
    }

    @Override
    public Object visitPackageDeclaration( @NotNull DeclaresParser.PackageDeclarationContext ctx ) {
        model = new PackageDescriptor( ctx.qualifiedName().getText(), new LinkedHashMap<String, TypeDescriptor>() );

        JavaPackageInfoSource packInfo =  Roaster.parse( JavaPackageInfoSource.class, "package " + ctx.qualifiedName().getText() );
        for ( DeclaresParser.AnnotationContext ac : ctx.annotation() ) {
            AnnotationSource as = packInfo.addAnnotation( resolveAnnotation( ac.annotationName().getText() ) );
            buildAnnotation( ac, as );
        }

        model.setPackageInfo( packInfo );
        return super.visitPackageDeclaration( ctx );
    }

    @Override
    public Object visitImportDeclaration( @NotNull DeclaresParser.ImportDeclarationContext ctx ) {
        boolean star = detectImportStar( ctx );
        if ( ! star ) {
            String imp = resolve( ctx.qualifiedName().getText() );
            String simpleName = imp.substring( imp.lastIndexOf( "." ) + 1 );
            imports.put( simpleName, imp );
            return imp;
        } else {
            // TODO I don't think I can resolve * at compile time safely..
            return null;
        }
    }

    private boolean detectImportStar( @NotNull DeclaresParser.ImportDeclarationContext ctx ) {
        for ( int j = 0; j < ctx.getChildCount(); j++ ) {
            if ( "*".equals( ctx.getChild( j ).getText() ) ) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object visitTypeDeclaration( @NotNull DeclaresParser.TypeDeclarationContext ctx ) {
        String name = ctx.Identifier().getText();
        JavaClassSource jt = Roaster.parse( JavaClassSource.class, "public class " + name + " {}" );
        jt.setPackage( model.getPackageName() );

        TypeDescriptor descr = new TypeDescriptor( name );
        descr.setSourceAST( jt );
        currentType = descr;

        if ( ctx.typeList() != null ) {
            for ( DeclaresParser.TypeContext tc : ctx.typeList().type() ) {
                String itf = tc.classOrInterfaceType().getText();
                jt.addInterface( resolve ( itf ) );
            }
        }

        for ( DeclaresParser.AnnotationContext ac : ctx.annotation() ) {
            AnnotationSource as = jt.addAnnotation( resolveAnnotation( ac.annotationName().getText() ) );
            fillAnnotation( ac, as );
        }

        model.add( name, descr );

        if ( ctx.typeBody() != null ) {
            visitTypeBody( ctx.typeBody() );
        }

        return jt;
    }


    private void fillAnnotation( DeclaresParser.AnnotationContext ac, AnnotationSource as ) {
        buildAnnotation( ac, as );
        currentType.addAnnotation( as );
    }

    private void buildAnnotation( DeclaresParser.AnnotationContext ac, AnnotationSource as ) {
        if ( ac.elementValuePairs() != null ) {
            for ( DeclaresParser.ElementValuePairContext ep : ac.elementValuePairs().elementValuePair() ) {
                as.setLiteralValue( ep.Identifier().getText(), ep.elementValue().getText() );
            }
        } else if ( ac.elementValue() != null ) {
            as.setLiteralValue( ac.elementValue().getText() );
        }
    }


    @Override
    public Object visitTypeBody( @NotNull DeclaresParser.TypeBodyContext ctx ) {
        for ( DeclaresParser.MemberDeclarationContext mb : ctx.memberDeclaration() ) {
            String type = resolve( mb.fieldDeclaration().type().getText() );
            String name = mb.fieldDeclaration().variableDeclarator().variableDeclaratorId().getText();
            FieldSource fs = currentType.getSourceAST().addField();
            fs.setName( name );
            fs.setType( type );
            fs.setPrivate();


            for ( DeclaresParser.AnnotationContext ac : mb.fieldDeclaration().annotation() ) {
                AnnotationSource as = fs.addAnnotation( resolveAnnotation( ac.annotationName().getText() ) );
                fillAnnotation( ac, as );
            }
        }

        return currentType;
    }

    private String resolveAnnotation( String name ) {
        if ( name.contains( "." ) ) {
            return name;
        }
        if ( imports.containsKey( name ) ) {
            return imports.get( name );
        } else {
            if ( isKieAnnotation( name ) ) {
                return KIE_ANNOTATION_PACKAGE + "." + name;
            } else {
                //return model.getPackageName() + "." + name;
                return name;
            }
        }
    }

    private boolean isKieAnnotation( String name ) {
        try {
            return Class.forName( KIE_ANNOTATION_PACKAGE + "." + name ) != null;
        } catch ( ClassNotFoundException e ) {
            return false;
        }
    }

    protected String resolve( String name ) {
        if ( name.contains( "." ) ) {
            return name;
        }
        if ( isPrimitive( name ) ) {
            return name;
        }

        if ( imports.containsKey( name ) ) {
            return imports.get( name );
        } else {
            //return model.getPackageName() + "." + name;
            return name;
        }
    }

    private boolean isPrimitive( String name ) {
        return "boolean".equals( name )
               ||   "char".equals( name )
               ||   "byte".equals( name )
               ||   "short".equals( name )
               ||   "int".equals( name )
               ||   "long".equals( name )
               ||   "float".equals( name )
               ||   "double".equals( name );
    }



}
