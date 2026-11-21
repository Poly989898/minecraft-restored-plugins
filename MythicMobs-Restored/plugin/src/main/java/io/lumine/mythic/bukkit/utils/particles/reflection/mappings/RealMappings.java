package io.lumine.mythic.bukkit.utils.particles.reflection.mappings;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public record RealMappings(@NotNull Collection<RealMappings.RealClassMapping> classes) implements Mappings {
   @Override
   public Collection<? extends Mappings.ClassMapping> getClasses() {
      return this.classes;
   }

   public record RealClassMapping(
      @NotNull String original,
      @NotNull String mapped,
      @NotNull List<RealMappings.RealClassMapping.RealFieldMapping> fields,
      @NotNull List<RealMappings.RealClassMapping.RealMethodMapping> methods
   ) implements Mappings.ClassMapping {
      @NotNull
      @Override
      public String getOriginalName() {
         return this.original;
      }

      @NotNull
      @Override
      public String getMappedName() {
         return this.mapped;
      }

      @Override
      public Collection<? extends Mappings.ClassMapping.FieldMapping> getFields() {
         return this.fields;
      }

      @Override
      public Collection<? extends Mappings.ClassMapping.MethodMapping> getMethods() {
         return this.methods;
      }

      public record RealFieldMapping(@NotNull String original, @NotNull String mapped) implements Mappings.ClassMapping.FieldMapping {
         @NotNull
         @Override
         public String getOriginalName() {
            return this.original;
         }

         @NotNull
         @Override
         public String getMappedName() {
            return this.mapped;
         }
      }

      public record RealMethodMapping(@NotNull String original, @NotNull String mapped, @NotNull Type[] parameterTypes)
         implements Mappings.ClassMapping.MethodMapping {
         @NotNull
         @Override
         public String getOriginalName() {
            return this.original;
         }

         @NotNull
         @Override
         public String getMappedName() {
            return this.mapped;
         }

         @NotNull
         @Override
         public Type[] getParameterTypes() {
            return this.parameterTypes;
         }
      }
   }
}
