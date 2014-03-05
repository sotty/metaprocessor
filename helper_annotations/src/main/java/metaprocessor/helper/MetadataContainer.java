package metaprocessor.helper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class MetadataContainer<T extends Metadatable> {

	protected T target;

	boolean[] isSet;
	Double[] degrees;

	public MetadataContainer( T metadatableObject, int numProperties ) {
		this.target = metadatableObject;
        this.isSet = new boolean[ numProperties ];
        this.degrees = new Double[ numProperties ];
	}

	public Double getDegree( int propertyIndex ) {
        return degrees[ propertyIndex ];
	}
	
	public void setDegree( int propertyIndex, Double degree ) {
	}
	
	public Boolean isSet( PropertyLiteral prop ) {
        return isSet[ prop.index ];
	}
	
	public void set( PropertyLiteral prop, Boolean set ) {
        isSet[ prop.index ] = set;
	}


    protected static PropertyLiteral[] properties;

    public static List<PropertyLiteral> properties() {
        return Collections.unmodifiableList( Arrays.asList( properties ) );
    }

    public static String[] propertyNames() {
        String[] names = new String[ properties.length ];
        for ( int j = 0; j < properties.length; j++ ) {
            names[ j ] = properties[ j ].name;
        }
        return names;
    }

    public static <T,R> PropertyLiteral getProperty( String name ) {
        for ( PropertyLiteral p : properties ) {
            if ( p.name.equals( name ) ) {
                return p;
            }
        }
        return null;
    }

    public static <T,R> PropertyLiteral getProperty( int index ) {
        return properties[ index ];
    }


    public abstract static class Modify<T extends Metadatable> {
        private T target;
        private ModifyTask<T,?> task;

        public Modify( T target ) {
            this.target = target;
        }

        public T call() {
            task.call( target );
            return target;
        }

        protected void addTask( ModifyTask<T,?> newTask ) {
            if ( task == null ) {
                task = newTask;
            } else {
                ModifyTask<T,?> lastTask = task;
                while ( task.nextTask != null ) {
                    lastTask = task.nextTask;
                }
                lastTask.nextTask = newTask;
            }
        }

        protected class ModifyTask<T extends Metadatable,R> {
            public PropertyLiteral<T,R> propertyLiteral;
            public R value;
            public ModifyTask<T,?> nextTask;

            public ModifyTask( PropertyLiteral<T,R> p, R val ) {
                propertyLiteral = p;
                value = val;
            }

            public void call( T target ) {
                propertyLiteral.set( target, value );
                if ( nextTask != null ) {
                    nextTask.call( target );
                }
            }
        }

    }
}
