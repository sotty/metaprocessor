package metaprocessor;

import metaprocessor.annotations.Metadata;
import metaprocessor.helper.MetadataContainer;
import metaprocessor.helper.Metadatable;

import java.util.List;

@Metadata
public class Pet implements Metadatable<Pet> {

	private String name;
	private double age;

    public Pet() { }

    public Pet( String name, double age ) {
        this.name = name;
        this.age = age;
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

    @Override
    public Pet_ getMetadata() {
        return null;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        Pet pet = (Pet) o;

        if ( Double.compare( pet.age, age ) != 0 ) return false;
        if ( name != null ? !name.equals( pet.name ) : pet.name != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = name != null ? name.hashCode() : 0;
        temp = Double.doubleToLongBits( age );
        result = 31 * result + (int) ( temp ^ ( temp >>> 32 ) );
        return result;
    }

    @Override
    public String toString() {
        return "Pet{" +
               "name='" + name + '\'' +
               ", age=" + age +
               '}';
    }
}
