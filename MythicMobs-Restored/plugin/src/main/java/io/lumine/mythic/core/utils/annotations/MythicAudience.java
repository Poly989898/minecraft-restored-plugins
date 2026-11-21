package io.lumine.mythic.core.utils.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MythicAudience {
   String name() default "";

   String[] aliases() default {};

   String author() default "";

   String description() default "";

   String version() default "4.11.1";

   boolean premium() default false;
}
