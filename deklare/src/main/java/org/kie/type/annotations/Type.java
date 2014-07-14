package org.kie.type.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( value = ElementType.TYPE )
@Retention( value = RetentionPolicy.RUNTIME )
public @interface Type {

    public boolean asInterface() default true;

    public boolean asClass() default true;

    public boolean asEnum() default false;

}
