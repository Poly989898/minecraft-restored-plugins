package com.nexomc.nexo.mechanics.misc.armor_effects;

import com.nexomc.libs.folialib.wrapper.task.WrappedBukkitTask;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.mechanics.Mechanic;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.utils.DurationUtilsKt;
import com.nexomc.nexo.utils.NexoYamlKt;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.time.Duration;
import kotlin.time.DurationKt;
import kotlin.time.DurationUnit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u00142\u00020\u0001:\u0001\u0014B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0002\u001a\u00020\u0003H\u0016J\u0014\u0010\u000f\u001a\u0004\u0018\u00010\u000e2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u0016J\u0014\u0010\u000f\u001a\u0004\u0018\u00010\u000e2\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013H\u0016R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u0013\u0010\b\u001a\u00020\t¢\u0006\n\n\u0002\u0010\f\u001a\u0004\b\n\u0010\u000b¨\u0006\u0015"},
   d2 = {"Lcom/nexomc/nexo/mechanics/misc/armor_effects/ArmorEffectsFactory;", "Lcom/nexomc/nexo/mechanics/MechanicFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "armorEffectTask", "Lcom/nexomc/nexo/mechanics/misc/armor_effects/ArmorEffectsTask;", "delay", "Lkotlin/time/Duration;", "getDelay-UwyO8pc", "()J", "J", "parse", "Lcom/nexomc/nexo/mechanics/misc/armor_effects/ArmorEffectsMechanic;", "getMechanic", "itemID", "", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "Companion", "com.nexomc:core"}
)
public final class ArmorEffectsFactory extends MechanicFactory {
   @NotNull
   public static final Companion Companion = new Companion();
   @Nullable
   private ArmorEffectsTask armorEffectTask;
   private final long delay;
   private static ArmorEffectsFactory instance;

   public ArmorEffectsFactory(@NotNull ConfigurationSection section) {
      super(section);
      Duration duration = NexoYamlKt.durationOrNull(section, "delay", (DurationUnit)null);
      long value;
      if (duration != null) {
         value = duration.unbox_impl();
      } else {
         Duration.Companion companion = Duration.Companion;
         value = DurationKt.toDuration(1, DurationUnit.SECONDS);
      }

      this.delay = value;
      instance = this;
      Listener[] listeners = new Listener[]{new ArmorEffectsListener()};
      this.registerListeners(listeners);
   }

   public final long getDelay_UwyO8pc/* $FF was: getDelay-UwyO8pc*/() {
      return this.delay;
   }

   @NotNull
   public ArmorEffectsMechanic parse(@NotNull ConfigurationSection section) {
      ArmorEffectsMechanic armorEffectsMechanic = new ArmorEffectsMechanic(this, section);
      Mechanic p0 = armorEffectsMechanic;
      int value = 0;
      this.addToImplemented(p0);
      ArmorEffectsMechanic mechanic = armorEffectsMechanic;
      ArmorEffectsTask armorEffectsTask = this.armorEffectTask;
      if (armorEffectsTask != null) {
         armorEffectsTask.cancel();
      }

      this.armorEffectTask = new ArmorEffectsTask();
      ArmorEffectsTask armorEffectsTask2 = this.armorEffectTask;
      WrappedBukkitTask wrappedTask = new WrappedBukkitTask(armorEffectsTask2.runTaskTimer((Plugin)NexoPlugin.Companion.instance(), 0L, DurationUtilsKt.getInWholeTicks_LRDsOJo(this.delay)));
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      ArmorEffectsFactory armorEffectsFactory = instance;
      if (armorEffectsFactory == null) {
         Intrinsics.throwUninitializedPropertyAccessException("instance");
         armorEffectsFactory = null;
      }

      mechanicsManager.registerTask(armorEffectsFactory.getMechanicID(), wrappedTask);
      return mechanic;
   }

   @Nullable
   public ArmorEffectsMechanic getMechanic(@Nullable String itemID) {
      Mechanic mechanic = super.getMechanic(itemID);
      return mechanic instanceof ArmorEffectsMechanic ? (ArmorEffectsMechanic)mechanic : null;
   }

   @Nullable
   public ArmorEffectsMechanic getMechanic(@Nullable ItemStack itemStack) {
      Mechanic mechanic = super.getMechanic(itemStack);
      return mechanic instanceof ArmorEffectsMechanic ? (ArmorEffectsMechanic)mechanic : null;
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0004\u001a\u00020\u0005R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.¢\u0006\u0002\n\u0000¨\u0006\u0006"},
      d2 = {"Lcom/nexomc/nexo/mechanics/misc/armor_effects/ArmorEffectsFactory$Companion;", "", "<init>", "()V", "instance", "Lcom/nexomc/nexo/mechanics/misc/armor_effects/ArmorEffectsFactory;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final ArmorEffectsFactory instance() {
         ArmorEffectsFactory armorEffectsFactory = ArmorEffectsFactory.instance;
         if (armorEffectsFactory == null) {
            Intrinsics.throwUninitializedPropertyAccessException("instance");
            armorEffectsFactory = null;
         }

         return armorEffectsFactory;
      }
   }
}
