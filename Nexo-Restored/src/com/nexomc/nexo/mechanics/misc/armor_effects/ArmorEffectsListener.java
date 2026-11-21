package com.nexomc.nexo.mechanics.misc.armor_effects;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import kotlin.Metadata;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u001c\u0010\u0004\u001a\u00020\u0005*\u00020\u0006H\u0007b\u000e\b\u0007\u0012\n\b\b\u0012\u0006\b\n0\t8\n¨\u0006\u000b"},
   d2 = {"Lcom/nexomc/nexo/mechanics/misc/armor_effects/ArmorEffectsListener;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "onItemEquipped", "", "Lcom/destroystokyo/paper/event/player/PlayerArmorChangeEvent;", "Lorg/bukkit/event/EventHandler;", "priority", "Lorg/bukkit/event/EventPriority;", "HIGHEST", "com.nexomc:core"}
)
public final class ArmorEffectsListener implements Listener {
   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public final void onItemEquipped(@NotNull PlayerArmorChangeEvent playerArmorChangeEvent) {
      ArmorEffectsMechanic.Companion companion = ArmorEffectsMechanic.Companion;
      Player player = playerArmorChangeEvent.getPlayer();
      ItemStack oldItem = playerArmorChangeEvent.getOldItem();
      companion.removeEffects(player, oldItem);
      companion = ArmorEffectsMechanic.Companion;
      player = playerArmorChangeEvent.getPlayer();
      companion.addEffects(player);
   }
}
