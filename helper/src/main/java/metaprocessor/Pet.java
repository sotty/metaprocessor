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
}
