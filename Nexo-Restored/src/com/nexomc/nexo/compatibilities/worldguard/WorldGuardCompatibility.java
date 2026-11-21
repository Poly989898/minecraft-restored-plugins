package com.nexomc.nexo.compatibilities.worldguard;

import com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent;
import com.nexomc.nexo.compatibilities.CompatibilityProvider;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import java.util.Collection;
import kotlin.Metadata;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Event.Result;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007¢\u0006\u0004\b\u0003\u0010\u0004J&\u0010\u0005\u001a\u00020\u0006*\u00020\u0007H\u0007b\u0018\b\b\u0012\n\b\t\u0012\u0006\b\n0\n8\u000b\u0012\b\b\f\u0012\u0004\b\u0007\u0010\u0002J\u0018\u0010\r\u001a\u00020\u000e*\u0004\u0018\u00010\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u000eH\u0002¨\u0006\u0011"},
   d2 = {"Lcom/nexomc/nexo/compatibilities/worldguard/WorldGuardCompatibility;", "Lcom/nexomc/nexo/compatibilities/CompatibilityProvider;", "Lcom/sk89q/worldguard/bukkit/WorldGuardPlugin;", "<init>", "()V", "onInteract", "", "Lcom/nexomc/nexo/api/events/furniture/NexoFurnitureInteractEvent;", "Lorg/bukkit/event/EventHandler;", "priority", "Lorg/bukkit/event/EventPriority;", "LOWEST", "ignoreCancelled", "toResult", "Lorg/bukkit/event/Event$Result;", "Lcom/sk89q/worldguard/protection/flags/StateFlag$State;", "default", "com.nexomc:core"}
)
public final class WorldGuardCompatibility extends CompatibilityProvider<WorldGuardPlugin> {
   @EventHandler(
      priority = EventPriority.LOWEST,
      ignoreCancelled = true
   )
   public final void onInteract(@NotNull NexoFurnitureInteractEvent nexoFurnitureInteractEvent) {
      WorldGuardPlatform platform = WorldGuard.getInstance().getPlatform();
      WorldGuardPlugin plugin = this.getPlugin();
      if (plugin != null) {
         LocalPlayer localPlayer2 = plugin.wrapPlayer(nexoFurnitureInteractEvent.getPlayer());
         if (localPlayer2 != null) {
            LocalPlayer localPlayer = localPlayer2;
            World world = BukkitAdapter.adapt(nexoFurnitureInteractEvent.getBaseEntity().getWorld());
            if (platform.getSessionManager().hasBypass(localPlayer, world)) {
               return;
            }

            RegionQuery query = platform.getRegionContainer().createQuery();
            Location location = BukkitAdapter.adapt(nexoFurnitureInteractEvent.getBaseEntity().getLocation());
            StateFlag[] stateFlags = new StateFlag[]{Flags.INTERACT};
            nexoFurnitureInteractEvent.setUseFurniture(this.toResult(query.queryState(location, localPlayer, stateFlags), nexoFurnitureInteractEvent.getUseFurniture()));
            StateFlag[] stateFlags2 = new StateFlag[]{NexoWorldguardFlags.INSTANCE.getFURNITURE_INTERACT_FLAG()};
            StateFlag.State state = query.queryState(location, localPlayer, stateFlags2);
            if (state != null) {
               StateFlag.State it = state;
               int value = 0;
               nexoFurnitureInteractEvent.setUseFurniture(this.toResult(it, nexoFurnitureInteractEvent.getUseFurniture()));
            }

            stateFlags2 = new StateFlag[]{NexoWorldguardFlags.INSTANCE.getFURNITURE_TOGGLE_LIGHT_FLAG()};
            state = query.queryState(location, localPlayer, stateFlags2);
            if (state != null) {
               StateFlag.State flagState = state;
               boolean condition = nexoFurnitureInteractEvent.getMechanic().getLight().getToggleable();
               if (condition) {
                  int value2 = 0;
                  nexoFurnitureInteractEvent.setCanToggleLight(this.toResult(flagState, nexoFurnitureInteractEvent.getCanToggleLight()));
               }
            }

            stateFlags2 = new StateFlag[]{NexoWorldguardFlags.INSTANCE.getFURNITURE_SIT_FLAG()};
            state = query.queryState(location, localPlayer, stateFlags2);
            if (state != null) {
               StateFlag.State flagState = state;
               boolean hasSeats = nexoFurnitureInteractEvent.getMechanic().getHasSeats();
               if (hasSeats) {
                  int value3 = 0;
                  nexoFurnitureInteractEvent.setCanSit(this.toResult(flagState, nexoFurnitureInteractEvent.getCanSit()));
               }
            }

            stateFlags2 = new StateFlag[]{NexoWorldguardFlags.INSTANCE.getFURNITURE_OPEN_STORAGE_FLAG()};
            state = query.queryState(location, localPlayer, stateFlags2);
            if (state != null) {
               StateFlag.State state2 = state;
               boolean storage = nexoFurnitureInteractEvent.getMechanic().isStorage();
               if (storage) {
                  int value4 = 0;
                  nexoFurnitureInteractEvent.setCanOpenStorage(this.toResult(state2, nexoFurnitureInteractEvent.getCanOpenStorage()));
               }
            }

            stateFlags2 = new StateFlag[]{NexoWorldguardFlags.INSTANCE.getFURNITURE_CLICKACTION_FLAG()};
            state = query.queryState(location, localPlayer, stateFlags2);
            if (state != null) {
               StateFlag.State state3 = state;
               boolean flag = !((Collection)nexoFurnitureInteractEvent.getMechanic().getClickActions()).isEmpty();
               if (flag) {
                  int value5 = 0;
                  nexoFurnitureInteractEvent.setCanRunAction(this.toResult(state3, nexoFurnitureInteractEvent.getCanRunAction()));
               }
            }

            stateFlags2 = new StateFlag[]{NexoWorldguardFlags.INSTANCE.getFURNITURE_CYCLE_STATE_FLAG()};
            state = query.queryState(location, localPlayer, stateFlags2);
            if (state != null) {
               StateFlag.State it = state;
               int value6 = 0;
               nexoFurnitureInteractEvent.setCanCycleState(this.toResult(it, nexoFurnitureInteractEvent.getCanCycleState()));
            }

            stateFlags2 = new StateFlag[]{NexoWorldguardFlags.INSTANCE.getFURNITURE_ROTATE_FLAG()};
            state = query.queryState(location, localPlayer, stateFlags2);
            if (state != null) {
               StateFlag.State state4 = state;
               boolean flag2 = nexoFurnitureInteractEvent.getMechanic().getRotatable().shouldRotate(nexoFurnitureInteractEvent.getPlayer());
               if (flag2) {
                  int value7 = 0;
                  nexoFurnitureInteractEvent.setCanRotate(this.toResult(state4, nexoFurnitureInteractEvent.getCanRotate()));
               }
            }

            return;
         }
      }

   }

   private final Event.Result toResult(StateFlag.State state, Event.Result result) {
      Event.Result result2;
      switch (state == null ? -1 : WorldGuardCompatibility.WhenMappings.$EnumSwitchMapping$0[state.ordinal()]) {
         case 1 -> result2 = Result.ALLOW;
         case 2 -> result2 = Result.DENY;
         default -> result2 = result;
      }

      return result2;
   }

   public static final class WhenMappings {
      public static final int[] $EnumSwitchMapping$0;
      static {
         int[] a_0 = new int[com.sk89q.worldguard.protection.flags.StateFlag.State.values().length];
         try { a_0[com.sk89q.worldguard.protection.flags.StateFlag.State.ALLOW.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         try { a_0[com.sk89q.worldguard.protection.flags.StateFlag.State.DENY.ordinal()] = 2; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$0 = a_0;
      }
   }
}
