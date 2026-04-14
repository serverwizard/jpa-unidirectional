package annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OneToMany {
    String mappedBy();

    FetchType fetch() default FetchType.LAZY;

    CascadeType[] cascade() default {};

    boolean orphanRemoval() default false;
}
