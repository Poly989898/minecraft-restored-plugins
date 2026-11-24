package io.lumine.mythic.lib.player.modifier;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import java.util.Objects;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public abstract class PlayerModifier {
   private final UUID uniqueId;
   private final ModifierSource source;
   private final EquipmentSlot slot;
   private final String key;

   public PlayerModifier(@NotNull String key, @NotNull EquipmentSlot slot, @NotNull ModifierSource source) {
      this(UUID.randomUUID(), key, slot, source);
   }

   public PlayerModifier(@NotNull UUID uniqueId, @NotNull String key, @NotNull EquipmentSlot slot, @NotNull ModifierSource source) {
      this.uniqueId = uniqueId;
      this.key = key;
      this.slot = slot;
      this.source = source;
   }

   @NotNull
   public UUID getUniqueId() {
      return this.uniqueId;
   }

   @NotNull
   public String getKey() {
      return this.key;
   }

   @NotNull
   public EquipmentSlot getSlot() {
      return this.slot;
   }

   @NotNull
   public ModifierSource getSource() {
      return this.source;
   }

   public abstract void register(@NotNull MMOPlayerData var1);

   public abstract void unregister(@NotNull MMOPlayerData var1);

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         PlayerModifier that = (PlayerModifier)o;
         return this.uniqueId.equals(that.uniqueId);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.uniqueId);
   }
}
