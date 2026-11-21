package io.lumine.mythic.core.utils.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MythicAuraComponent {
   String name() default "";

   String[] aliases() default {};

   String description() default "";
}
