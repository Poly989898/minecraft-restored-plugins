package com.nexomc.nexo.mechanics.furniture.compatibility;

import ai.idealistic.vacan.abstraction.check.CheckEnums;
import ai.idealistic.vacan.abstraction.check.CheckEnums.HackType;
import ai.idealistic.vacan.api.PlayerViolationEvent;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import com.nexomc.nexo.utils.logs.Logs;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.SetsKt;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0007\u001a\u00020\b*\u00020\tH\u0007b\u0002\b\nR\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000b"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/compatibility/VacanCompatibility;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "furnitureHackTypes", "", "Lai/idealistic/vacan/abstraction/check/CheckEnums$HackType;", "onFurnitureHitbox", "", "Lai/idealistic/vacan/api/PlayerViolationEvent;", "Lorg/bukkit/event/EventHandler;", "com.nexomc:core"}
)
public final class VacanCompatibility implements Listener {
   @NotNull
   private final Set<CheckEnums.HackType> furnitureHackTypes;

   public VacanCompatibility() {
      Logs.logSuccess("Initializing Vacan-AntiCheat Hook!", false);
      CheckEnums.HackType[] hackTypes = new CheckEnums.HackType[]{HackType.EXPLOITS, HackType.IRREGULAR_MOVEMENTS};
      this.furnitureHackTypes = SetsKt.setOf(hackTypes);
   }

   @EventHandler
   public final void onFurnitureHitbox(@NotNull PlayerViolationEvent playerViolationEvent) {
      if (this.furnitureHackTypes.contains(playerViolationEvent.getHackType())) {
         IFurniturePacketManager.Companion companion = IFurniturePacketManager.Companion;
         Player player = playerViolationEvent.getPlayer();
         if (companion.standingOnFurniture(player)) {
            playerViolationEvent.setCancelled(true);
         }

      }
   }
}
