package io.lumine.mythic.bukkit.utils.maven;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.annotation.Nonnull;

@Documented
@Repeatable(MavenLibraries.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MavenLibrary {
   @Nonnull
   String groupId();

   @Nonnull
   String artifactId();

   @Nonnull
   String version();

   @Nonnull
   Repository repo() default @Repository(
   url = "https://repo1.maven.org/maven2"
);
}
