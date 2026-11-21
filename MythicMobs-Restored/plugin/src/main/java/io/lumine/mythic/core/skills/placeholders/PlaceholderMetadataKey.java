package io.lumine.mythic.core.skills.placeholders;

import lombok.Getter;


@Getter
public enum PlaceholderMetadataKey {
   ITEM_STACK("item"),
   MYTHIC_TYPE("mythic-type"),
   RANDOM_BIAS("random-bias"),
   STAT_DAMAGE_MODIFIER("stat-damage-modifier");

   private final String key;

   private PlaceholderMetadataKey(String key) {
      this.key = key;
   }
}
