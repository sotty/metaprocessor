package metaprocessor.codegenerator;

import metaprocessor.annotations.Metadata;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;
import org.mvel2.templates.res.Node;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MetaCodeGenerator {

    private CompiledTemplate metaClassTemplate;

    public MetaCodeGenerator() {
        InputStream src = MetaCodeGenerator.class.getResourceAsStream( "/metaclass.mvel" );
        metaClassTemplate = TemplateCompiler.compileTemplate( src, (Map<String, Class<? extends Node>>) null );
    }


	public void generateCode( Writer writer, TypeElement classElement )
			throws IOException {

        TemplateContext ctx = analyzeType( classElement );

        try {
            String source = (String) TemplateRuntime.execute( metaClassTemplate, ctx );
            writer.write( source );
        } catch ( Exception e ) {
            e.printStackTrace();
        }


	}

    private TemplateContext analyzeType( TypeElement classElement ) {

        TemplateContext ctx = new TemplateContext();
        ctx.packageQName = ( (PackageElement) classElement.getEnclosingElement() ).getQualifiedName().toString();
        ctx.classCoreQName = classElement.getQualifiedName().toString();
        ctx.classCoreName = classElement.getSimpleName().toString();

        Metadata meta = classElement.getAnnotation( Metadata.class );
        String metaClassName = meta.prefix() +
                               classElement.getSimpleName() +
                               meta.suffix();

        for ( Element sub : classElement.getEnclosedElements() ) {
            if ( sub.getKind() == ElementKind.FIELD ) {
                PropertyContext pctx = new PropertyContext();
                pctx.name = sub.getSimpleName().toString();
                pctx.index = ctx.properties.size();
                pctx.type = sub.asType().toString();

                ctx.properties.add( pctx );
            }
        }


        ctx.mclassName = metaClassName;

        return ctx;
    }

    public class TemplateContext {
        public String packageQName;
        public String mclassName;
        public String classCoreName;
        public String classCoreQName;

        public List<PropertyContext> properties = new LinkedList<PropertyContext>();

        public String getter( String name, String type ) {
            String prefix = boolean.class.getName().equals( "type" ) ? "is" : "get";
            return prefix + capitalize( name );
        }

        public String setter( String name ) {
            return "set" + capitalize( name );
        }

        public String capitalize( String name ) {
            return name.substring( 0, 1 ).toUpperCase() + name.substring( 1 );
        }

        public String box(String type) {
            if ( "byte".equals( type ) ) {
                return "java.lang.Byte";
            } else if ( "char".equals( type ) ) {
                return "java.lang.Character";
            } else if ( "double".equals( type ) ) {
                return "java.lang.Double";
            } else if ( "float".equals( type ) ) {
                return "java.lang.Float";
            } else if ( "int".equals( type ) ) {
                return "java.lang.Integer";
            } else if ( "long".equals( type ) ) {
                return "java.lang.Long";
            } else if ( "short".equals( type ) ) {
                return "java.lang.Short";
            } else if ( "boolean".equals( type ) ) {
                return "java.lang.Boolean";
            } else {
                return type;
            }
        }
    }

    public class PropertyContext {
        public String name;
        public String type;
        public int index;
    }

}