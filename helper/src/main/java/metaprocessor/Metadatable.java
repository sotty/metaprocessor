package metaprocessor;

import metaprocessor.helper.MetadataContainer;

public interface Metadatable <T>{

	public MetadataContainer<T> getMetadata();
	
}
