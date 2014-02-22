package metaprocessor.helper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;




public class MetadataContainer<T> {

	protected T original;
	List<Double> degreeArray = new ArrayList<Double>();
	List<Boolean> isSetArray = new ArrayList<Boolean>();
	List<Class<?>> typeArray = new ArrayList<Class<?>>();
	List<String> positionArray = new ArrayList<String>();
	
	public MetadataContainer(T metadatableObject) {
		this.original = metadatableObject;
		for (Field field : metadatableObject.getClass().getDeclaredFields()) {
			positionArray.add(field.getName());
			typeArray.add(field.getClass());
		}
	}
	
	public MetadataContainer() {
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
