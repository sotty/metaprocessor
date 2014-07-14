package org.kie.type;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenSource;
import org.junit.Test;
import org.kie.type.annotations.processors.ProcessorRegistry;
import org.kie.type.model.DataModel;
import org.kie.types.DeclaresLexer;
import org.kie.types.DeclaresParser;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ParserTest {

    @Test
    public void testAnnotationIntegration() {
        DeclaresParser parser = getParser( "/test1.ktd" );
        DeclaresBuilder builder = new DeclaresBuilder();
        builder.visit( parser.compilationUnit() );

        DataModel model = new DataModel();
        model.addPackage( builder.getPackage().getPackageName(), builder.getPackage() );

        model.process( new ProcessorRegistry() );

        System.out.println( model.toString() );

    }

    @Test
    public void testBasicParsing() {
        DeclaresParser parser = getParser( "/test1.ktd" );
        DeclaresBuilder builder = new DeclaresBuilder();
        builder.visit( parser.compilationUnit() );
        assertEquals( 3, builder.getPackage().getTypes().size() );
    }

    protected DeclaresParser getParser( String src ) {
        InputStream inputStream = ParserTest.class.getResourceAsStream( src );

        TokenSource source;
        try {
            source = new DeclaresLexer( new ANTLRInputStream( inputStream ) );
            return new DeclaresParser( new CommonTokenStream( source ) );
        } catch ( IOException e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }
        return null;
    }

}
