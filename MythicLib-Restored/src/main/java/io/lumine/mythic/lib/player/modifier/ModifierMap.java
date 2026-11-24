package io.lumine.mythic.lib.player.modifier;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

public abstract class ModifierMap<T extends PlayerModifier> {
   private final MMOPlayerData playerData;
   private final Map<UUID, T> modifiers = new HashMap<>();

   public ModifierMap(MMOPlayerData playerData) {
      this.playerData = playerData;
   }

   @NotNull
   public MMOPlayerData getPlayerData() {
      return this.playerData;
   }

   @NotNull
   public Collection<T> getModifiers() {
      return this.modifiers.values();
   }

   @NotNull
   public List<T> isolateModifiers(@NotNull EquipmentSlot hand) {
      ArrayList<T> isolated = new ArrayList<>();

      for (T modifier : this.getModifiers()) {
         if (hand.isCompatible(modifier)) {
            isolated.add(modifier);
         }
      }

      return isolated;
   }

   @Nullable
   public T addModifier(T modifier) {
      return this.modifiers.put(modifier.getUniqueId(), modifier);
   }

   @Nullable
   public T removeModifier(UUID uuid) {
      T removed = this.modifiers.remove(uuid);
      if (removed != null && removed instanceof io.lumine.mythic.lib.util.Closeable) {
         ((io.lumine.mythic.lib.util.Closeable)removed).close();
      }

      return removed;
   }

   public void removeModifiers(String key) {
      Iterator<T> iterator = this.modifiers.values().iterator();

      while (iterator.hasNext()) {
         T skill = iterator.next();
         if (skill.getKey().equals(key)) {
            iterator.remove();
            if (skill instanceof io.lumine.mythic.lib.util.Closeable) {
               ((io.lumine.mythic.lib.util.Closeable)skill).close();
            }
         }
      }
   }
}
