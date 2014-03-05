package metaprocessor.helper;

public interface Metadatable<T extends Metadatable> {

	public MetadataContainer<T> getMetadata();
	
}
