package metaprocessor.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention( RetentionPolicy.SOURCE )
public @interface Metadata {

    String prefix() default "";

    String suffix() default "_";

}
