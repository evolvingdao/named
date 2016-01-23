package io.github.evolvingdao.named.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Named {

	public static final String DEFAULT_BUNDLE = "Names";
	public static final String DEFAULT_KEY = "Class Simple Name";

	String bundle() default DEFAULT_BUNDLE;

	String key() default DEFAULT_KEY;

}
