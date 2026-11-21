package io.lumine.mythic.core.skills.placeholders.metakeywords;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MythicMetaKeyword {
   String name();

   String[] aliases() default {};
}
