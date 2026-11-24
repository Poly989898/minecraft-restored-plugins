package net.Indyuce.mmoitems.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Deprecated
@Retention(RetentionPolicy.RUNTIME)
public @interface VersionDependant {
   int[] version();
}
