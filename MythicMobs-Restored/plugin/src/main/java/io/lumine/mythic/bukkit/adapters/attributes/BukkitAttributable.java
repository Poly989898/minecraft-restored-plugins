package io.lumine.mythic.bukkit.adapters.attributes;

import io.lumine.mythic.bukkit.adapters.BukkitAttribute;

public interface BukkitAttributable {
   ImmutableAttributeInstance getAttribute(BukkitAttribute bukkitAttribute);

   default ImmutableAttributeInstance getAttribute(String attribute) {
      BukkitAttribute bukkitAttribute = BukkitAttribute.get(attribute);
      return bukkitAttribute == null ? null : this.getAttribute(bukkitAttribute);
   }
}
