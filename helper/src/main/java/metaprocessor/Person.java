package metaprocessor;

import metaprocessor.annotations.Metadata;
import metaprocessor.helper.MetadataContainer;

@Metadata
public class Person implements Metadatable<Person>{

	private String name;

	private double age;

	public void setAge(double age) {
		this.age = age;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getAge() {
		return age;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public MetadataContainer<Person> getMetadata() {
		return new Person_(this);
	}
}
