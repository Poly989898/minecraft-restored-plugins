package com.nexomc.nexo.compatibilities.worldguard;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.utils.PluginUtils;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0017\n\u0002\u0010\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u001c\u001a\u00020\u001dR\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR\u001c\u0010\n\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\u0007\"\u0004\b\f\u0010\tR\u001c\u0010\r\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u0007\"\u0004\b\u000f\u0010\tR\u001c\u0010\u0010\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0007\"\u0004\b\u0012\u0010\tR\u001c\u0010\u0013\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0007\"\u0004\b\u0015\u0010\tR\u001c\u0010\u0016\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0017\u0010\u0007\"\u0004\b\u0018\u0010\tR\u001c\u0010\u0019\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u0007\"\u0004\b\u001b\u0010\t¨\u0006\u001e"},
   d2 = {"Lcom/nexomc/nexo/compatibilities/worldguard/NexoWorldguardFlags;", "", "<init>", "()V", "FURNITURE_INTERACT_FLAG", "Lcom/sk89q/worldguard/protection/flags/StateFlag;", "getFURNITURE_INTERACT_FLAG", "()Lcom/sk89q/worldguard/protection/flags/StateFlag;", "setFURNITURE_INTERACT_FLAG", "(Lcom/sk89q/worldguard/protection/flags/StateFlag;)V", "FURNITURE_SIT_FLAG", "getFURNITURE_SIT_FLAG", "setFURNITURE_SIT_FLAG", "FURNITURE_TOGGLE_LIGHT_FLAG", "getFURNITURE_TOGGLE_LIGHT_FLAG", "setFURNITURE_TOGGLE_LIGHT_FLAG", "FURNITURE_ROTATE_FLAG", "getFURNITURE_ROTATE_FLAG", "setFURNITURE_ROTATE_FLAG", "FURNITURE_OPEN_STORAGE_FLAG", "getFURNITURE_OPEN_STORAGE_FLAG", "setFURNITURE_OPEN_STORAGE_FLAG", "FURNITURE_CLICKACTION_FLAG", "getFURNITURE_CLICKACTION_FLAG", "setFURNITURE_CLICKACTION_FLAG", "FURNITURE_CYCLE_STATE_FLAG", "getFURNITURE_CYCLE_STATE_FLAG", "setFURNITURE_CYCLE_STATE_FLAG", "registerFlags", "", "com.nexomc:core"}
)
public final class NexoWorldguardFlags {
   @NotNull
   public static final NexoWorldguardFlags INSTANCE = new NexoWorldguardFlags();
   @Nullable
   private static StateFlag FURNITURE_INTERACT_FLAG;
   @Nullable
   private static StateFlag FURNITURE_SIT_FLAG;
   @Nullable
   private static StateFlag FURNITURE_TOGGLE_LIGHT_FLAG;
   @Nullable
   private static StateFlag FURNITURE_ROTATE_FLAG;
   @Nullable
   private static StateFlag FURNITURE_OPEN_STORAGE_FLAG;
   @Nullable
   private static StateFlag FURNITURE_CLICKACTION_FLAG;
   @Nullable
   private static StateFlag FURNITURE_CYCLE_STATE_FLAG;

   private NexoWorldguardFlags() {
   }

   @Nullable
   public final StateFlag getFURNITURE_INTERACT_FLAG() {
      return FURNITURE_INTERACT_FLAG;
   }

   public final void setFURNITURE_INTERACT_FLAG(@Nullable StateFlag stateFlag) {
      FURNITURE_INTERACT_FLAG = stateFlag;
   }

   @Nullable
   public final StateFlag getFURNITURE_SIT_FLAG() {
      return FURNITURE_SIT_FLAG;
   }

   public final void setFURNITURE_SIT_FLAG(@Nullable StateFlag stateFlag) {
      FURNITURE_SIT_FLAG = stateFlag;
   }

   @Nullable
   public final StateFlag getFURNITURE_TOGGLE_LIGHT_FLAG() {
      return FURNITURE_TOGGLE_LIGHT_FLAG;
   }

   public final void setFURNITURE_TOGGLE_LIGHT_FLAG(@Nullable StateFlag stateFlag) {
      FURNITURE_TOGGLE_LIGHT_FLAG = stateFlag;
   }

   @Nullable
   public final StateFlag getFURNITURE_ROTATE_FLAG() {
      return FURNITURE_ROTATE_FLAG;
   }

   public final void setFURNITURE_ROTATE_FLAG(@Nullable StateFlag stateFlag) {
      FURNITURE_ROTATE_FLAG = stateFlag;
   }

   @Nullable
   public final StateFlag getFURNITURE_OPEN_STORAGE_FLAG() {
      return FURNITURE_OPEN_STORAGE_FLAG;
   }

   public final void setFURNITURE_OPEN_STORAGE_FLAG(@Nullable StateFlag stateFlag) {
      FURNITURE_OPEN_STORAGE_FLAG = stateFlag;
   }

   @Nullable
   public final StateFlag getFURNITURE_CLICKACTION_FLAG() {
      return FURNITURE_CLICKACTION_FLAG;
   }

   public final void setFURNITURE_CLICKACTION_FLAG(@Nullable StateFlag stateFlag) {
      FURNITURE_CLICKACTION_FLAG = stateFlag;
   }

   @Nullable
   public final StateFlag getFURNITURE_CYCLE_STATE_FLAG() {
      return FURNITURE_CYCLE_STATE_FLAG;
   }

   public final void setFURNITURE_CYCLE_STATE_FLAG(@Nullable StateFlag stateFlag) {
      FURNITURE_CYCLE_STATE_FLAG = stateFlag;
   }

   public final void registerFlags() {
      NexoWorldguardFlags nexoWorldguardFlags2 = this;

      Object obj;
      try {
         NexoWorldguardFlags nexoWorldguardFlags3 = nexoWorldguardFlags2;
         int value = 0;
         obj = ResultShim.ok(new StateFlag("nexo-furniture-interact", false));
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      StateFlag stateFlag = (StateFlag)(ResultShim.isFailure(obj) ? null : obj);
      if (stateFlag != null) {
         FURNITURE_INTERACT_FLAG = stateFlag;
         nexoWorldguardFlags2 = this;

         try {
            NexoWorldguardFlags nexoWorldguardFlags4 = nexoWorldguardFlags2;
            int value2 = 0;
            obj = ResultShim.ok(new StateFlag("nexo-furniture-sit", false));
         } catch (Throwable ex) {
            obj = ResultShim.ok(ResultKt.createFailure(ex));
         }

         stateFlag = (StateFlag)(ResultShim.isFailure(obj) ? null : obj);
         if (stateFlag != null) {
            FURNITURE_SIT_FLAG = stateFlag;
            nexoWorldguardFlags2 = this;

            try {
               NexoWorldguardFlags nexoWorldguardFlags5 = nexoWorldguardFlags2;
               int value3 = 0;
               obj = ResultShim.ok(new StateFlag("nexo-furniture-toggle-light", false));
            } catch (Throwable ex2) {
               obj = ResultShim.ok(ResultKt.createFailure(ex2));
            }

            stateFlag = (StateFlag)(ResultShim.isFailure(obj) ? null : obj);
            if (stateFlag != null) {
               FURNITURE_TOGGLE_LIGHT_FLAG = stateFlag;
               nexoWorldguardFlags2 = this;

               try {
                  NexoWorldguardFlags nexoWorldguardFlags6 = nexoWorldguardFlags2;
                  int value4 = 0;
                  obj = ResultShim.ok(new StateFlag("nexo-furniture-rotate", false));
               } catch (Throwable ex3) {
                  obj = ResultShim.ok(ResultKt.createFailure(ex3));
               }

               stateFlag = (StateFlag)(ResultShim.isFailure(obj) ? null : obj);
               if (stateFlag != null) {
                  FURNITURE_ROTATE_FLAG = stateFlag;
                  nexoWorldguardFlags2 = this;

                  try {
                     NexoWorldguardFlags nexoWorldguardFlags7 = nexoWorldguardFlags2;
                     int value5 = 0;
                     obj = ResultShim.ok(new StateFlag("nexo-furniture-open-storage", false));
                  } catch (Throwable ex4) {
                     obj = ResultShim.ok(ResultKt.createFailure(ex4));
                  }

                  stateFlag = (StateFlag)(ResultShim.isFailure(obj) ? null : obj);
                  if (stateFlag != null) {
                     FURNITURE_OPEN_STORAGE_FLAG = stateFlag;
                     nexoWorldguardFlags2 = this;

                     try {
                        NexoWorldguardFlags nexoWorldguardFlags8 = nexoWorldguardFlags2;
                        int value6 = 0;
                        obj = ResultShim.ok(new StateFlag("nexo-furniture-run-actions", false));
                     } catch (Throwable ex5) {
                        obj = ResultShim.ok(ResultKt.createFailure(ex5));
                     }

                     stateFlag = (StateFlag)(ResultShim.isFailure(obj) ? null : obj);
                     if (stateFlag != null) {
                        FURNITURE_CLICKACTION_FLAG = stateFlag;
                        nexoWorldguardFlags2 = this;

                        try {
                           NexoWorldguardFlags nexoWorldguardFlags9 = nexoWorldguardFlags2;
                           int value7 = 0;
                           obj = ResultShim.ok(new StateFlag("nexo-furniture-cycle-state", false));
                        } catch (Throwable ex6) {
                           obj = ResultShim.ok(ResultKt.createFailure(ex6));
                        }

                        stateFlag = (StateFlag)(ResultShim.isFailure(obj) ? null : obj);
                        if (stateFlag != null) {
                           FURNITURE_CYCLE_STATE_FLAG = stateFlag;
                           if (PluginUtils.INSTANCE.isEnabled("WorldGuard")) {
                              Bukkit.getLogger().warning("Failed to register NexoWorldGuardFlags: WorldGuard is not enabled");
                           } else {
                              NexoWorldguardFlags nexoWorldguardFlags = this;

                              Object result;
                              try {
                                 nexoWorldguardFlags2 = nexoWorldguardFlags;
                                 int value8 = 0;
                                 FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
                                 registry.register((Flag)FURNITURE_INTERACT_FLAG);
                                 registry.register((Flag)FURNITURE_SIT_FLAG);
                                 registry.register((Flag)FURNITURE_TOGGLE_LIGHT_FLAG);
                                 registry.register((Flag)FURNITURE_ROTATE_FLAG);
                                 registry.register((Flag)FURNITURE_OPEN_STORAGE_FLAG);
                                 registry.register((Flag)FURNITURE_CLICKACTION_FLAG);
                                 registry.register((Flag)FURNITURE_CYCLE_STATE_FLAG);
                                 result = ResultShim.ok(Unit.INSTANCE);
                              } catch (Throwable ex7) {
                                 result = ResultShim.ok(ResultKt.createFailure(ex7));
                              }

                              Throwable throwable = ResultShim.exceptionOrNull(result);
                              if (throwable != null) {
                                 Throwable it = throwable;
                                 int value9 = 0;
                                 it.printStackTrace();
                              }

                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
