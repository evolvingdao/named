package io.github.evolvingdao.named.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface DifferentKey {

	String value();

}
