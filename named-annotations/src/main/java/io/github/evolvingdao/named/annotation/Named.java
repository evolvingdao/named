package io.github.evolvingdao.named.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Named {

	String DEFAULT_BUNDLE = "Names";
	String DEFAULT_KEY = "Class Simple Name";

	String bundle() default Named.DEFAULT_BUNDLE;

	String key() default Named.DEFAULT_KEY;

}
