package metaprocessor;


import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class AppTest {


    @Test
    public void testPropertyLiteralTypeSafety() {
        Student p = new Student( "mark", 42 );

        String name = Person_.name.get( p );
        double age = Person_.age.get( p );
        List<String> addrs = Person_.addresses.get( p );

        // Using anything other than a Person will raise a compile-time error
        /* Person_.age.get( "fpp" ); */

        assertEquals( "mark", name );
        assertEquals( 42.0, age );
        assertEquals( null, addrs );

        p.setAddresses( Arrays.asList( "a", "b" ) );
        assertEquals( 2, Person_.addresses.get( p ).size() );

    }

    @Test
    public void testPropertyLiteralSet() {
        Student p = new Student( "mark", 42 );

        Person_.name.set( p, "john" );
        assertEquals( "john", p.getName() );

        Person_.name.unSet( p );
        assertFalse( p.getMetadata().isSet( Person_.name ) );
    }

    @Test
    @Ignore
    public void testIsSetOnConstructor() throws Exception {
        Student p = new Student( "john", 32 );
        Person_ ret = p.getMetadata();

        assertTrue( ret.isSet( ret.name ) );
        assertTrue( ret.isSet( ret.age ) );
        assertFalse( ret.isSet( ret.addresses ) );
	}

    @Test
    public void testDelayedModify() throws Exception {
        Student p = new Student( "john", 32 );
        Person_ ret = p.getMetadata();

        Person p2 = ret.modify( p ).name( "alan" ).age( 54 ).call();
        assertSame( p, p2 );
        assertEquals( "alan", p2.getName() );
        assertEquals( 54.0, p2.getAge() );
	}
}
