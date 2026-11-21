package com.nexomc.nexo.mechanics.misc.armor_effects;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.mechanics.Mechanic;
import com.nexomc.nexo.utils.DurationUtilsKt;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.PotionUtils;
import com.nexomc.nexo.utils.customarmor.CustomArmorType;
import com.nexomc.nexo.utils.logs.Logs;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.IntIterator;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import kotlin.time.Duration;
import kotlin.time.DurationUnit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u0000 \u000f2\u00020\u0001:\u0001\u000fB\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u000e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0004\u001a\u00020\u0005R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f¨\u0006\u0010"},
   d2 = {"Lcom/nexomc/nexo/mechanics/misc/armor_effects/ArmorEffectsMechanic;", "Lcom/nexomc/nexo/mechanics/Mechanic;", "factory", "Lcom/nexomc/nexo/mechanics/misc/armor_effects/ArmorEffectsFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lcom/nexomc/nexo/mechanics/misc/armor_effects/ArmorEffectsFactory;Lorg/bukkit/configuration/ConfigurationSection;)V", "armorEffects", "Lit/unimi/dsi/fastutil/objects/ObjectOpenHashSet;", "Lcom/nexomc/nexo/mechanics/misc/armor_effects/ArmorEffect;", "getArmorEffects", "()Lit/unimi/dsi/fastutil/objects/ObjectOpenHashSet;", "registersEffectFromSection", "", "Companion", "com.nexomc:core"}
)
public final class ArmorEffectsMechanic extends Mechanic {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final ObjectOpenHashSet<ArmorEffect> armorEffects;
   @NotNull
   private static final IntRange ARMOR_SLOTS = new IntRange(36, 39);

   public ArmorEffectsMechanic(@NotNull ArmorEffectsFactory factory, @NotNull ConfigurationSection section) {
      super(factory, section);
      this.armorEffects = new ObjectOpenHashSet();
      Set keys = section.getKeys(false);
      Iterable iterable = (Iterable)keys;

      for(Object element : iterable) {
         String effect = (String)element;
         int value = 0;
         ConfigurationSection configurationSection = NexoYamlKt.section(section, effect);
         if (configurationSection != null) {
            ConfigurationSection p0 = configurationSection;
            int value2 = 0;
            this.registersEffectFromSection(p0);
         }
      }

   }

   @NotNull
   public final ObjectOpenHashSet<ArmorEffect> getArmorEffects() {
      return this.armorEffects;
   }

   public final void registersEffectFromSection(@NotNull ConfigurationSection section) {
      PotionUtils potionUtils = PotionUtils.INSTANCE;
      String name = section.getName();
      name = name.toLowerCase(Locale.ROOT);
      PotionEffectType potionEffectType = potionUtils.getEffectType(name);
      if (potionEffectType == null) {
         String str = section.getName();
         name = section.getCurrentPath();
         Logs.logError("Invalid potion effect: " + str + ", in " + StringsKt.substringBefore(name, ".", name) + "!", false);
      } else {
         PotionEffectType effectType = potionEffectType;
         Duration duration2 = NexoYamlKt.durationOrNull(section, "duration", (DurationUnit)null);
         long duration = duration2 != null ? duration2.unbox_impl() : ArmorEffectsFactory.Companion.instance().getDelay_UwyO8pc();
         int amplifier = section.getInt("amplifier", 0);
         boolean ambient = section.getBoolean("ambient", false);
         boolean particles = section.getBoolean("particles", true);
         boolean icon = section.getBoolean("icon", true);
         boolean requiresFullSet = section.getBoolean("requires_full_set", false);
         boolean resetOnUnequip = section.getBoolean("reset_on_unequip", Duration.isNegative_impl(duration));
         PotionEffect potionEffect = new PotionEffect(effectType, DurationUtilsKt.getTicks_LRDsOJo(duration), amplifier, ambient, particles, icon);
         ((Collection)this.armorEffects).add(new ArmorEffect(potionEffect, requiresFullSet, resetOnUnequip));
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tJ\u0016\u0010\n\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\fJ\u0014\u0010\r\u001a\u0004\u0018\u00010\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u000eH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0010"},
      d2 = {"Lcom/nexomc/nexo/mechanics/misc/armor_effects/ArmorEffectsMechanic$Companion;", "", "<init>", "()V", "ARMOR_SLOTS", "Lkotlin/ranges/IntRange;", "addEffects", "", "player", "Lorg/bukkit/entity/Player;", "removeEffects", "oldItem", "Lorg/bukkit/inventory/ItemStack;", "armorNameFromId", "", "itemId", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      public final void addEffects(@NotNull Player player) {
         Iterable iterable = (Iterable)ArmorEffectsMechanic.ARMOR_SLOTS;
         Iterator iterator = iterable.iterator();

         while(iterator.hasNext()) {
            int element = ((IntIterator)iterator).nextInt();
            int armorSlot = element;
            int value = 0;
            ItemStack item = player.getInventory().getItem(element);
            if (item != null) {
               ItemStack armorPiece = item;
               ArmorEffectsMechanic armorEffectsMechanic = ArmorEffectsFactory.Companion.instance().getMechanic(armorPiece);
               if (armorEffectsMechanic != null) {
                  ArmorEffectsMechanic mechanic = armorEffectsMechanic;
                  Iterable iterable2 = (Iterable)ArmorEffectsMechanic.ARMOR_SLOTS;
                  boolean flag;
                  if (iterable2 instanceof Collection && ((Collection)iterable2).isEmpty()) {
                     flag = true;
                  } else {
                     block: {
                        Iterator iterator2 = iterable2.iterator();

                        while(iterator2.hasNext()) {
                           int nextInt = ((IntIterator)iterator2).nextInt();
                           int value2 = 0;
                           if (nextInt == armorSlot) {
                              flag = true;
                           } else {
                              ItemStack slotItem = player.getInventory().getItem(nextInt);
                              flag = Intrinsics.areEqual(ArmorEffectsMechanic.Companion.armorNameFromId(mechanic.getItemID()), ArmorEffectsMechanic.Companion.armorNameFromId(NexoItems.idFromItem(slotItem)));
                           }

                           if (!flag) {
                              flag = false;
                              break block;
                           }
                        }

                        flag = true;
                     }
                  }

                  boolean usingFullSet = flag;
                  iterable2 = (Iterable)mechanic.getArmorEffects();

                  for(Object obj : iterable2) {
                     ArmorEffect it = (ArmorEffect)obj;
                     int value3 = 0;
                     if (!it.getRequiresFullSet() || usingFullSet) {
                        player.addPotionEffect(it);
                     }
                  }
               }
            }
         }

      }

      public final void removeEffects(@NotNull Player player, @NotNull ItemStack oldItem) {
         ArmorEffectsMechanic armorEffectsMechanic = ArmorEffectsFactory.Companion.instance().getMechanic(oldItem);
         if (armorEffectsMechanic != null) {
            ArmorEffectsMechanic mechanic = armorEffectsMechanic;
            Iterable iterable = (Iterable)mechanic.getArmorEffects();

            for(Object element : iterable) {
               ArmorEffect it = (ArmorEffect)element;
               int value = 0;
               if (it.getResetOnUnequip()) {
                  player.removePotionEffect(it.getEffect().getType());
               }
            }

         }
      }

      private final String armorNameFromId(String itemId) {
         String str;
         if (itemId != null) {
            CharSequence charSequence = (CharSequence)itemId;
            Regex itemIdRegex = CustomArmorType.Companion.getItemIdRegex();
            String str2 = "$1";
            str = itemIdRegex.replace(charSequence, str2);
         } else {
            str = null;
         }

         return str;
      }
   }
}
