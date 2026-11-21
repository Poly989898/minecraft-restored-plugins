package io.lumine.mythic.core.utils.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(MythicFields.class)
public @interface MythicField {
   String name() default "";

   String[] aliases() default {};

   String description() default "";

   String version() default "";

   String defValue() default "";

   boolean premium() default false;
}
