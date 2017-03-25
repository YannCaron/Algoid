/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.cyann.al.generator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 <p>
 @author CARONYN
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Section {

	SectionType type() default SectionType.text;

	Lang lang() default Lang.en;

	String content() default "";

	Class<?>[] links() default {};

	Class<?> factory() default Object.class;

}
