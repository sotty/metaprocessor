package metaprocessor;

import metaprocessor.annotations.Metadata;
import metaprocessor.helper.MetadataContainer;
import metaprocessor.helper.Metadatable;

import java.util.List;

//@Metadata
public class Person implements Metadatable<Person> {

	private String name;
	private double age;
    private List<String> addresses;

    /* ADDED! */
    Person_ meta = new Person_( this );

    @Override
    public Person_ getMetadata() {
        return meta;
    }

    public Person() { }
    
    public Person( String name, double age ) {
        this.name = name;
        this.age = age;
    }

    public Person( String name, double age, List<String> addresses ) {
        this.name = name;
        this.age = age;
        this.addresses = addresses;
    }


    public double getAge() {
        return age;
    }

    public void setAge( double age ) {        
		this.age = age;
	}

    public String getName() {
        return name;
    }

    public void setName( String name ) {
		this.name = name;
	}

    public List<String> getAddresses() {
        return addresses;
    }

    public void setAddresses( List<String> addresses ) {
        this.addresses = addresses;
    }

}
