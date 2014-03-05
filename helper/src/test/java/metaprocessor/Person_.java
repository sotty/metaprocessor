package metaprocessor;

import metaprocessor.helper.MetadataContainer;
import metaprocessor.helper.Metadatable;
import metaprocessor.helper.PropertyLiteral;

import java.util.List;

public class Person_ extends MetadataContainer<Person> {

    public static class Person_Modify extends Modify<Person> {

        public Person_Modify( Person p ) {
            super( p );
        }

        public Person_Modify name( String newName ) {
            addTask( new ModifyTask<Person, String>( name, newName ) );
            return this;
        }

        public Person_Modify age( double newAge ) {
            addTask( new ModifyTask<Person, Double>( age, newAge ) );
            return this;
        }

        public Person_Modify addresses( List newAddresses ) {
            addTask( new ModifyTask<Person, List>( addresses, newAddresses ) );
            return this;
        }

    }

    public static final PropertyLiteral<Person,String> name = new PropertyLiteral<Person,String>( 0, "name", "java:" + Person.class.getName() + "#" + "name" ) {
        public String get( Person o ) { return o.getName(); }
        public void set( Person o, String value ) { o.setName( value ); }
    };
    public static final PropertyLiteral<Person,Double> age = new PropertyLiteral<Person,Double>( 1, "age", "java:" + Person.class.getName() + "#" + "age" ) {
        public Double get( Person o ) { return o.getAge(); }
        public void set( Person o, Double value ) { o.setAge( value ); }
    };
    public static final PropertyLiteral<Person,List> addresses = new PropertyLiteral<Person,List>( 2, "addresses", "java:" + Person.class.getName() + "#" + "addresses" ) {
        public List get( Person o ) { return o.getAddresses(); }
        public void set( Person o, List value ) { o.setAddresses( value ); }
    };


    {
        properties = new PropertyLiteral[] { name, age, addresses };
    }

    public static Person newInstance() {
        return new Person();
    }

    public static Person newInstance( String n, double a ) {
        Person p = new Person();

        name.set( p, n );
        age.set( p, a );

        return p;
    }

    public Person_Modify modify( Person p ) {
        return new Person_Modify( p );
    }

    public Person_( Person p ) {
        super( p, 3 );
    }

    public static Person_ getMeta( Person person ) {
        return person.getMetadata();
    }

}
