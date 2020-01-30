package com.link_intersystems.maven.mojo.parameter;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ FIELD })
@Documented
public @interface PropertyEditor {

	static interface DEFAULT extends PropertyEditorComponent<Object> {
	}

	String property() default "";

	String propertyEditorHint() default "";

	Class<? extends PropertyEditorComponent<?>> propertyEditorClass() default DEFAULT.class;

}
