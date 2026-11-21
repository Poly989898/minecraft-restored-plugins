package com.nexomc.nexo.mechanics.furniture.rotatable;

import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.furniture.FurnitureFactory;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\b\u0018\u0000 \u00172\u00020\u0001:\u0001\u0017B\u001b\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003¢\u0006\u0004\b\u0005\u0010\u0006B\u0011\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0001¢\u0006\u0004\b\u0005\u0010\u0007J\u0010\u0010\u000b\u001a\u00020\u00032\b\u0010\f\u001a\u0004\u0018\u00010\rJ\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\u001d\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003HÆ\u0001J\u0014\u0010\u0011\u001a\u00020\u00032\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u0013\u001a\u00020\u0014HÖ\u0081\u0004J\n\u0010\u0015\u001a\u00020\u0016HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\t¨\u0006\u0018"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/rotatable/Rotatable;", "", "rotatable", "", "onSneak", "<init>", "(ZZ)V", "(Ljava/lang/Object;)V", "getRotatable", "()Z", "getOnSneak", "shouldRotate", "player", "Lorg/bukkit/entity/Player;", "component1", "component2", "copy", "equals", "other", "hashCode", "", "toString", "", "Companion", "com.nexomc:core"}
)
public final class Rotatable {
   @NotNull
   public static final Companion Companion = new Companion();
   private final boolean rotatable;
   private final boolean onSneak;
   @NotNull
   private static final List<String> rotationGamemodes;

   public Rotatable(boolean rotatable, boolean onSneak) {
      this.rotatable = rotatable;
      this.onSneak = onSneak;
   }

   public final boolean getRotatable() {
      return this.rotatable;
   }

   public final boolean getOnSneak() {
      return this.onSneak;
   }

   public Rotatable(@NotNull Object rotatable) {
      com.nexomc.nexo.mechanics.MechanicFactory mechanicFactory;
      /* this-alias removed */
      Object obj = rotatable;
      if (!(rotatable instanceof Boolean)) {
         obj = null;
      }

      Boolean flag = (Boolean)obj;
      boolean flag2;
      if (flag != null) {
         flag2 = flag;
      } else {
         Object obj2 = rotatable;
         if (!(rotatable instanceof ConfigurationSection)) {
            obj2 = null;
         }

         Boolean flag3 = (ConfigurationSection)obj2 != null ? ((ConfigurationSection)obj2).getBoolean("rotatable") : null;
         if (flag3 != null) {
            flag2 = flag3;
         } else {
            Object obj3 = rotatable;
            if (!(rotatable instanceof Map)) {
               obj3 = null;
            }

            Boolean flag4 = (Map)obj3 != null ? (Boolean)((Map)obj3).get("rotatable") : null;
            flag2 = flag4 != null ? flag4 : false;
         }
      }

      Object obj4 = rotatable;
      if (!(rotatable instanceof ConfigurationSection)) {
         obj4 = null;
      }

      ConfigurationSection configurationSection = (ConfigurationSection)obj4;
      boolean flag5;
      if (configurationSection != null) {
         flag5 = configurationSection.getBoolean("on_sneak");
      } else {
         Object obj5 = rotatable;
         if (!(rotatable instanceof Map)) {
            obj5 = null;
         }

         Boolean flag6 = (Map)obj5 != null ? (Boolean)((Map)obj5).get("on_sneak") : null;
         if (flag6 != null) {
            flag5 = flag6;
         } else {
            MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
            boolean flag7 = flag2;

            block: {
               for(MechanicFactory it : (java.lang.Iterable<MechanicFactory>)(Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()) {
                  int value = 0;
                  MechanicFactory mechanicFactory2 = it;
                  if (!(it instanceof FurnitureFactory)) {
                     mechanicFactory2 = null;
                  }

                  it = (FurnitureFactory)mechanicFactory2;
                  if (it != null) {
                     mechanicFactory = it;
                     break block;
                  }
               }

               mechanicFactory = null;
            }

            MechanicFactory mechanicFactory3 = mechanicFactory;
            /* this-alias removed */
            flag2 = flag7;
            flag6 = (FurnitureFactory)mechanicFactory3 != null ? ((FurnitureFactory)mechanicFactory3).getDefaultRotatableOnSneak() : null;
            flag5 = flag6 != null ? flag6 : false;
         }
      }

      this(flag2, flag5);
   }

   public final boolean shouldRotate(@Nullable Player player) {
      if (player != null && !rotationGamemodes.contains(player.getGameMode().name())) {
         return false;
      } else {
         return this.rotatable && this.onSneak == (player != null ? player.isSneaking() : false);
      }
   }

   public final boolean component1() {
      return this.rotatable;
   }

   public final boolean component2() {
      return this.onSneak;
   }

   @NotNull
   public final Rotatable copy(boolean rotatable, boolean onSneak) {
      return new Rotatable(rotatable, onSneak);
   }

   @NotNull
   public String toString() {
      return "Rotatable(rotatable=" + this.rotatable + ", onSneak=" + this.onSneak + ")";
   }

   public int hashCode() {
      int result = Boolean.hashCode(this.rotatable);
      result = result * 31 + Boolean.hashCode(this.onSneak);
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof Rotatable)) {
         return false;
      } else {
         Rotatable rotatable2 = (Rotatable)other;
         if (this.rotatable != rotatable2.rotatable) {
            return false;
         } else {
            return this.onSneak == rotatable2.onSneak;
         }
      }
   }

   public Rotatable() {
      this(false, defaultOnSneak());
   }

   private static boolean defaultOnSneak() {
      for (com.nexomc.nexo.mechanics.MechanicFactory factory : com.nexomc.nexo.mechanics.MechanicsManager.INSTANCE.getFACTORIES_BY_MECHANIC_ID().values()) {
         if (factory instanceof com.nexomc.nexo.mechanics.furniture.FurnitureFactory furnitureFactory) {
            return furnitureFactory.getDefaultRotatableOnSneak();
         }
      }
      return false;
   }

   static {
      rotationGamemodes = Settings.ALLOWED_GAMEMODES_FOR_ROTATION.toStringList();
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R:\u0010\u0004\u001a.\u0012\f\u0012\n \u0007*\u0004\u0018\u00010\u00060\u0006 \u0007*\u0015\u0012\f\u0012\n \u0007*\u0004\u0018\u00010\u00060\u00060\t¢\u0006\u0002\b\b0\u0005¢\u0006\u0002\b\bX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\n"},
      d2 = {"Lcom/nexomc/nexo/mechanics/furniture/rotatable/Rotatable$Companion;", "", "<init>", "()V", "rotationGamemodes", "", "", "kotlin.jvm.PlatformType", "Lorg/jetbrains/annotations/NotNull;", "", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }
   }
}
