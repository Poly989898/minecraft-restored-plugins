package io.lumine.mythic.core.skills.placeholders.metakeywords;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface MythicMetaKeywordAttribute {
   int index() default 0;
}
