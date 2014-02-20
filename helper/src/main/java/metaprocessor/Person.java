package metaprocessor;

import metaprocessor.annotations.Metadata;

@Metadata
public class Person {

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
}
