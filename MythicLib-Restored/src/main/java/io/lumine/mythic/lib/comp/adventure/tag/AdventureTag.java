package io.lumine.mythic.lib.comp.adventure.tag;

import io.lumine.mythic.lib.comp.adventure.resolver.AdventureTagResolver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public abstract class AdventureTag {
   private final String name;
   private final List<String> aliases = new ArrayList<>();
   private final AdventureTagResolver resolver;
   private final boolean backwardsCompatible;
   private final boolean color;

   public AdventureTag(@NotNull String name, @NotNull AdventureTagResolver resolver, boolean backwardsCompatible, boolean color, @NotNull String... aliases) {
      this.name = name;
      this.resolver = resolver;
      this.backwardsCompatible = backwardsCompatible;
      this.aliases.addAll(Arrays.asList(aliases));
      this.color = color;
   }

   public AdventureTagResolver resolver() {
      return this.resolver;
   }

   public String name() {
      return this.name;
   }

   public boolean backwardsCompatible() {
      return this.backwardsCompatible;
   }

   public List<String> aliases() {
      return this.aliases;
   }

   public boolean color() {
      return this.color;
   }
}
