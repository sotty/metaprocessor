package metaprocessor.helper;

public abstract class PropertyLiteral<T extends Metadatable, R> {

    public final int index;
    public final String name;

    private final String key;

    public PropertyLiteral( int index, String name, String key ) {
        this.index = index;
        this.name = name;
        this.key = key;
    }


    public abstract R get( T o );

    public abstract void set( T o, R value );

    public void unSet( T o )  {
        o.getMetadata().set( this, false );
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        PropertyLiteral that = (PropertyLiteral) o;

        if ( !key.equals( that.key ) ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
