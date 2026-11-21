package io.lumine.mythic.core.utils.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MythicPlaceholder {
   String placeholder() default "";

   String[] aliases() default {};

   String description() default "";

   String version() default "4.0";

   boolean premium() default false;

   short usedPlaceholderArguments() default 0;
}
