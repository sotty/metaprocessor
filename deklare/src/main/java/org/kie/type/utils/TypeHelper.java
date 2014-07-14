package org.kie.type.utils;

import org.jboss.forge.roaster.model.Field;
import org.jboss.forge.roaster.model.JavaType;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;

public class TypeHelper {

    public static String getter( String fieldName, String type ) {
        if ( boolean.class.getName().equals( type ) ) {
            return "is" + capitalize( fieldName );
        }
        return "get" + capitalize( fieldName );
    }

    public static String setter( String fieldName, String type ) {
        return "set" + capitalize( fieldName );
    }

    public static String capitalize( String fieldName ) {
        return fieldName.substring( 0, 1 ).toUpperCase() + fieldName.substring( 1 );
    }

    public static String getTypeName( Field<? extends JavaSource> field ) {
        return field.getType().isPrimitive() ? field.getType().getName() : field.getType().getQualifiedName();
    }

}
