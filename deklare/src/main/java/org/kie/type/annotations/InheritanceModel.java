package org.kie.type.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( value = ElementType.PACKAGE )
@Retention( value = RetentionPolicy.SOURCE )
public @interface InheritanceModel {

    InheritanceMode value()      default InheritanceMode.DEFAULT;

}
