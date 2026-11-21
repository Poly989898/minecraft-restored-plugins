package io.lumine.mythic.core.logging;

import java.io.File;
import java.lang.StackWalker.Option;
import java.lang.StackWalker.StackFrame;
import java.lang.reflect.Method;
import java.security.CodeSource;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.Generated;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@Getter
@ToString
public class ClassInfo {
   @Nullable
   private final String jarName;
   @NotNull
   private final String className;
   @ToString.Exclude
   @NotNull
   private final String packageName;
   @ToString.Exclude
   @Nullable
   private final String implementationVendor;
   @ToString.Exclude
   @Nullable
   private final String implementationVersion;
   @ToString.Exclude
   @Nullable
   private final String implementationTitle;
   @ToString.Exclude
   private Method usedMethod;
   @ToString.Exclude
   private Class<?> usedClass;
   private static final String SEPARATOR = "=".repeat(55);
   private static final Set<String> WARNED = ConcurrentHashMap.newKeySet();

   private ClassInfo(@NotNull Class<?> clazz) {
      this.className = clazz.getName();
      this.packageName = clazz.getPackage().getName();
      CodeSource cs = clazz.getProtectionDomain().getCodeSource();
      if (cs != null) {
         String[] path = cs.getLocation().getFile().split(Pattern.quote(File.separator));
         if (path.length > 0) {
            this.jarName = path[path.length - 1];
         } else {
            this.jarName = cs.getLocation().getFile();
         }
      } else {
         this.jarName = null;
      }

      Package pkg = clazz.getPackage();
      if (pkg != null) {
         this.implementationVendor = pkg.getImplementationVendor();
         this.implementationTitle = pkg.getImplementationTitle();
         this.implementationVersion = pkg.getImplementationVersion();
      } else {
         this.implementationVendor = null;
         this.implementationTitle = null;
         this.implementationVersion = null;
      }
   }

   public ClassInfo setUsedClass(Class<?> clazz) {
      this.usedClass = clazz;
      return this;
   }

   public ClassInfo setUsedMethod(Method method) {
      this.usedMethod = method;
      return this;
   }

   public ClassInfo setUsedMethod(Class<?> clazz, Class<?> paramTypes) {
      try {
         this.usedMethod = clazz.getMethod(StackWalker.getInstance().walk(f -> f.skip(1L).findFirst().orElseThrow()).getMethodName(), paramTypes);
      } catch (NoSuchMethodException e) {
      }

      return this;
   }

   public static ClassInfo of(@NotNull Class<?> clazz) {
      return new ClassInfo(clazz);
   }

   public static ClassInfo ofCaller() {
      Class<?> caller = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE)
         .walk(
            frames -> frames.filter(f -> f.getDeclaringClass() != ClassInfo.class)
               .filter(f -> !f.getDeclaringClass().getName().startsWith("io.lumine.mythic."))
               .findFirst()
               .map(StackFrame::getDeclaringClass)
               .orElse(null)
         );
      return of(Objects.requireNonNullElse(caller, ClassInfo.MissingClassInfo.class));
   }

   public void warnClassIsUsingDeprecatedAPI(String replacement) {
      String key = this.className + "#" + (this.usedMethod != null ? this.usedMethod.getName() : "unknown");
      if (WARNED.add(key)) {
         MythicLogger.log(Level.WARNING, this.buildDeprecationWarning("A third-party plugin is using a deprecated MythicMobs API method.", replacement));
      }
   }

   private String buildDeprecationWarning(String context, String replacement) {
      return "\n"
         + SEPARATOR
         + "\n [DEPRECATED API] "
         + context
         + "\n  Plugin JAR : "
         + (this.jarName != null ? this.jarName : "unknown")
         + "\n  Caller      : "
         + this.className
         + (this.usedClass != null ? "\n  Deprecated Class  : " + this.usedClass.getName() : "")
         + (this.usedMethod != null ? "\n  Deprecated Method : " + this.formatMethod(this.usedMethod) : "")
         + "\n  Replace With: "
         + replacement
         + "\n  Action     : Notify the plugin author to update their MythicMobs integration.\n"
         + SEPARATOR;
   }

   private String formatMethod(Method method) {
      String params = Arrays.stream(method.getParameterTypes()).map(Class::getSimpleName).collect(Collectors.joining(", "));
      return method.getDeclaringClass().getSimpleName() + "#" + method.getName() + "(" + params + ")";
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof ClassInfo other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$jarName = this.getJarName();
         Object other$jarName = other.getJarName();
         if (this$jarName == null ? other$jarName == null : this$jarName.equals(other$jarName)) {
            Object this$className = this.getClassName();
            Object other$className = other.getClassName();
            if (this$className == null ? other$className == null : this$className.equals(other$className)) {
               Object this$packageName = this.getPackageName();
               Object other$packageName = other.getPackageName();
               if (this$packageName == null ? other$packageName == null : this$packageName.equals(other$packageName)) {
                  Object this$implementationVendor = this.getImplementationVendor();
                  Object other$implementationVendor = other.getImplementationVendor();
                  if (this$implementationVendor == null ? other$implementationVendor == null : this$implementationVendor.equals(other$implementationVendor)) {
                     Object this$implementationVersion = this.getImplementationVersion();
                     Object other$implementationVersion = other.getImplementationVersion();
                     if (this$implementationVersion == null
                        ? other$implementationVersion == null
                        : this$implementationVersion.equals(other$implementationVersion)) {
                        Object this$implementationTitle = this.getImplementationTitle();
                        Object other$implementationTitle = other.getImplementationTitle();
                        if (this$implementationTitle == null ? other$implementationTitle == null : this$implementationTitle.equals(other$implementationTitle)) {
                           Object this$usedMethod = this.getUsedMethod();
                           Object other$usedMethod = other.getUsedMethod();
                           if (this$usedMethod == null ? other$usedMethod == null : this$usedMethod.equals(other$usedMethod)) {
                              Object this$usedClass = this.getUsedClass();
                              Object other$usedClass = other.getUsedClass();
                              return this$usedClass == null ? other$usedClass == null : this$usedClass.equals(other$usedClass);
                           } else {
                              return false;
                           }
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof ClassInfo;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $jarName = this.getJarName();
      result = result * 59 + ($jarName == null ? 43 : $jarName.hashCode());
      Object $className = this.getClassName();
      result = result * 59 + ($className == null ? 43 : $className.hashCode());
      Object $packageName = this.getPackageName();
      result = result * 59 + ($packageName == null ? 43 : $packageName.hashCode());
      Object $implementationVendor = this.getImplementationVendor();
      result = result * 59 + ($implementationVendor == null ? 43 : $implementationVendor.hashCode());
      Object $implementationVersion = this.getImplementationVersion();
      result = result * 59 + ($implementationVersion == null ? 43 : $implementationVersion.hashCode());
      Object $implementationTitle = this.getImplementationTitle();
      result = result * 59 + ($implementationTitle == null ? 43 : $implementationTitle.hashCode());
      Object $usedMethod = this.getUsedMethod();
      result = result * 59 + ($usedMethod == null ? 43 : $usedMethod.hashCode());
      Object $usedClass = this.getUsedClass();
      return result * 59 + ($usedClass == null ? 43 : $usedClass.hashCode());
   }

   public static class MissingClassInfo extends ClassInfo {
      private MissingClassInfo(@NotNull Class<?> clazz) {
         super(clazz);
      }

      @Override
      public String toString() {
         return "It was impossible to find the searched class";
      }
   }
}
