package com.nexomc.nexo.mechanics.furniture.compatibility;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import com.nexomc.nexo.utils.logs.Logs;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.collections.SetsKt;
import me.vagdedes.spartan.api.PlayerViolationEvent;
import me.vagdedes.spartan.system.Enums;
import me.vagdedes.spartan.system.Enums.HackType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0007\u001a\u00020\b*\u00020\tH\u0007b\u0002\b\nR\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000b"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/compatibility/SpartanCompatibility;", "Lorg/bukkit/event/Listener;", "<init>", "()V", "furnitureHackTypes", "", "Lme/vagdedes/spartan/system/Enums$HackType;", "onFurnitureHitbox", "", "Lme/vagdedes/spartan/api/PlayerViolationEvent;", "Lorg/bukkit/event/EventHandler;", "com.nexomc:core"}
)
public final class SpartanCompatibility implements Listener {
   @NotNull
   private final Set<Enums.HackType> furnitureHackTypes;

   public SpartanCompatibility() {
      Logs.logSuccess("Initializing Spartan-AntiCheat Hook!", false);
      SpartanCompatibility spartanCompatibility = this;

      Object obj2;
      try {
         SpartanCompatibility spartanCompatibility2 = spartanCompatibility;
         int value = 0;
         Enums.HackType[] hackTypes = new Enums.HackType[]{HackType.Exploits, HackType.IrregularMovements};
         obj2 = ResultShim.ok(SetsKt.setOf(hackTypes));
      } catch (Throwable e) {
         obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Object obj = obj2;
      Set emptySet = SetsKt.emptySet();
      this.furnitureHackTypes = (Set)(ResultShim.isFailure(obj) ? emptySet : obj);
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
