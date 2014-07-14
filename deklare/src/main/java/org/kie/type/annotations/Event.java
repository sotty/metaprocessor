package org.kie.type.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( value = ElementType.TYPE )
@Retention( value = RetentionPolicy.CLASS )
public @interface Event {

    String timestamp()  default "";
    String duration()   default "";
    String expires()    default "";

}
