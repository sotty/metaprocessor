package metaprocessor.helper;

import java.lang.reflect.Field;
import java.util.ArrayList;




public class MetadataContainer {

	ArrayList<Double> degreeArray;
	ArrayList<Boolean> isSetArray;
	ArrayList<Class<?>> typeArray;
	ArrayList<String> positionArray;
	
	public MetadataContainer(Object metadatableObject) {
		for (Field field : metadatableObject.getClass().getDeclaredFields()) {
			positionArray.add(field.getName());
			typeArray.add(field.getClass());
		}
	}
	
	public MetadataContainer() {
		// TODO Auto-generated constructor stub
	}
	
	public Double getDegree(String property){
		return degreeArray.get(positionArray.indexOf(property));
	}
	
	public void setDegree(String property, Double degree){
		degreeArray.add(positionArray.indexOf(property), degree);
	}
	
	public Boolean getIsSet(String property){
		return isSetArray.get(positionArray.indexOf(property));
	}
	
	public void setIsSet(String property, Boolean isSet){
		isSetArray.add(positionArray.indexOf(property), isSet);
	}
	
	public Class<?> getType(String property){
		return typeArray.get(positionArray.indexOf(property));
	}
	
	public void setType(String property, Class<?> type){
		typeArray.add(positionArray.indexOf(property), type);
	}
	
	public Integer getPosition(String property){
		return positionArray.indexOf(property);
	}
	
	
}
