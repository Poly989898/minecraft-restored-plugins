package io.lumine.mythic.bukkit.utils.particles.reflection.mappings;

import io.lumine.mythic.bukkit.utils.particles.reflection.ReflectionAccessor;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;

public interface Mappings {
   Collection<? extends Mappings.ClassMapping> getClasses();

   @NotNull
   static String getStringForMethod(@NotNull String methodName, @NotNull Type... parameterTypes) {
      return methodName + Stream.of(parameterTypes).map(Type::getTypeName).collect(Collectors.joining(", ", "(", ")"));
   }

   public interface ClassMapping extends Mappings.MappedObject, Type {
      @NotNull
      @Override
      default String getTypeName() {
         return this.getOriginalName();
      }

      @NotNull
      default Mappings.ClassMapping.ClassArrayType getArrayType() {
         return new Mappings.ClassMapping.ClassArrayType(this);
      }

      Collection<? extends Mappings.ClassMapping.FieldMapping> getFields();

      Collection<? extends Mappings.ClassMapping.MethodMapping> getMethods();

      public record ClassArrayType(@NotNull Type componentMapping) implements Type {
         @NotNull
         @Override
         public String getTypeName() {
            return this.componentMapping().getTypeName() + "[]";
         }
      }

      public interface FieldMapping extends Mappings.MappedObject {
      }

      public interface MethodMapping extends Mappings.MappedObject {
         @NotNull
         Type[] getParameterTypes();

         default boolean isSameParameters(@NotNull Type[] types) {
            return ReflectionAccessor.areSameParameters(this.getParameterTypes(), types);
         }
      }
   }

   public interface MappedObject {
      @NotNull
      String getOriginalName();

      @NotNull
      String getMappedName();
   }
}
