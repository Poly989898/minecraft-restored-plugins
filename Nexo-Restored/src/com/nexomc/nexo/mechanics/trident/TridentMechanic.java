package com.nexomc.nexo.mechanics.trident;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.mechanics.Mechanic;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.RegistryUtilsKt;
import com.nexomc.nexo.utils.StringUtilsKt;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.RegistryKey;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.kyori.adventure.key.Key;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.ItemDisplay.ItemDisplayTransform;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\b\u0018\u0000 (2\u00020\u0001:\u0003()*B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007R\u0013\u0010\b\u001a\u0004\u0018\u00010\t¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0013\u0010\f\u001a\u0004\u0018\u00010\r¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0015\u0010\u0010\u001a\u00060\u0011j\u0002`\u0012¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0015\u001a\u00020\u0016¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\u0019\u001a\u00020\u001a¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0011\u0010\u001d\u001a\u00020\u001e¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 R\"\u0010!\u001a\t\u0018\u00010\"¢\u0006\u0002\b#8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b&\u0010'\u001a\u0004\b$\u0010%¨\u0006+"},
   d2 = {"Lcom/nexomc/nexo/mechanics/trident/TridentMechanic;", "Lcom/nexomc/nexo/mechanics/Mechanic;", "factory", "Lcom/nexomc/nexo/mechanics/trident/TridentFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lcom/nexomc/nexo/mechanics/trident/TridentFactory;Lorg/bukkit/configuration/ConfigurationSection;)V", "itemModel", "Lnet/kyori/adventure/key/Key;", "getItemModel", "()Lnet/kyori/adventure/key/Key;", "model", "", "getModel", "()Ljava/lang/String;", "transform", "Lorg/bukkit/entity/ItemDisplay$ItemDisplayTransform;", "Lcom/nexomc/nexo/utils/FurnitureTransform;", "getTransform", "()Lorg/bukkit/entity/ItemDisplay$ItemDisplayTransform;", "rotation", "Lorg/joml/Vector2f;", "getRotation", "()Lorg/joml/Vector2f;", "damage", "", "getDamage", "()D", "sounds", "Lcom/nexomc/nexo/mechanics/trident/TridentMechanic$TridentSounds;", "getSounds", "()Lcom/nexomc/nexo/mechanics/trident/TridentMechanic$TridentSounds;", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "Lorg/jetbrains/annotations/NotNull;", "getItemStack", "()Lorg/bukkit/inventory/ItemStack;", "itemStack$delegate", "Lkotlin/Lazy;", "Companion", "TridentSounds", "TridentSound", "com.nexomc:core"}
)
public final class TridentMechanic extends Mechanic {
   @NotNull
   public static final Companion Companion = new Companion();
   @Nullable
   private final Key itemModel;
   @Nullable
   private final String model;
   @NotNull
   private final ItemDisplay.ItemDisplayTransform transform;
   @NotNull
   private final Vector2f rotation;
   private final double damage;
   @NotNull
   private final TridentSounds sounds;
   @NotNull
   private final Lazy itemStack$delegate;
   @NotNull
   private static final Lazy<Registry<Sound>> soundRegistry$delegate = LazyKt.lazy(TridentMechanic::soundRegistry_delegate$lambda$0);

   public TridentMechanic(@NotNull TridentFactory factory, @NotNull ConfigurationSection section) {
      super(factory, section);
      Key key = NexoYamlKt.key(section, "thrown_item_model");
      if (key == null) {
         key = NexoYamlKt.key(NexoYamlKt.getRootSection(section), "Components.item_model");
      }

      this.itemModel = key;
      this.model = section.getString("thrown_item", StringUtilsKt.appendIfMissing(NexoYamlKt.getRootId(section), "_throwing"));
      String str = "display_transform";
      ItemDisplay.ItemDisplayTransform itemDisplayTransform = (ItemDisplay.ItemDisplayTransform)EnumUtils.getEnum(ItemDisplay.ItemDisplayTransform.class, NexoYamlKt.stringOrNull(section, str));
      if (itemDisplayTransform == null) {
         itemDisplayTransform = ItemDisplayTransform.NONE;
      }

      this.transform = itemDisplayTransform;
      this.rotation = NexoYamlKt.vector2f(section, "rotation", 0.0F);
      this.damage = section.getDouble("damage", (double)1.0F);
      TridentMechanic tridentMechanic = this;
      ConfigurationSection configurationSection = NexoYamlKt.section(section, "sounds");
      TridentSounds tridentSounds;
      if (configurationSection != null) {
         ConfigurationSection p0 = configurationSection;
         int value = 0;
         tridentSounds = new TridentSounds(p0);
         tridentMechanic = this;
      } else {
         tridentSounds = new TridentSounds(new com.nexomc.nexo.mechanics.trident.TridentMechanic.TridentSound(org.bukkit.Sound.ITEM_TRIDENT_THROW, 1.0F, 1.0F), new com.nexomc.nexo.mechanics.trident.TridentMechanic.TridentSound(org.bukkit.Sound.ITEM_TRIDENT_HIT, 1.0F, 1.0F), new com.nexomc.nexo.mechanics.trident.TridentMechanic.TridentSound(org.bukkit.Sound.ITEM_TRIDENT_HIT_GROUND, 1.0F, 1.0F), new com.nexomc.nexo.mechanics.trident.TridentMechanic.TridentSound(org.bukkit.Sound.ITEM_TRIDENT_RETURN, 1.0F, 1.0F));
      }

      this.sounds = tridentSounds;
      this.itemStack$delegate = LazyKt.lazy((() -> TridentMechanic.itemStack_delegate$lambda$0(this, section)));
   }

   @Nullable
   public final Key getItemModel() {
      return this.itemModel;
   }

   @Nullable
   public final String getModel() {
      return this.model;
   }

   @NotNull
   public final ItemDisplay.ItemDisplayTransform getTransform() {
      return this.transform;
   }

   @NotNull
   public final Vector2f getRotation() {
      return this.rotation;
   }

   public final double getDamage() {
      return this.damage;
   }

   @NotNull
   public final TridentSounds getSounds() {
      return this.sounds;
   }

   @Nullable
   public final ItemStack getItemStack() {
      Lazy lazy = this.itemStack$delegate;
      return (ItemStack)lazy.getValue();
   }

   private static final ItemStack itemStack_delegate$lambda$0(TridentMechanic tridentMechanic, ConfigurationSection section) {
      ItemStack itemStack;
      if (tridentMechanic.itemModel != null) {
         ItemStack itemStack2 = ItemStack.of(Material.PAPER);
         int value = 0;
         itemStack2.setData(DataComponentTypes.ITEM_MODEL, tridentMechanic.itemModel);
         itemStack = itemStack2;
      } else {
         ItemBuilder itemBuilder = NexoItems.itemFromId(tridentMechanic.model);
         if (itemBuilder != null) {
            itemStack = itemBuilder.build();
            if (itemStack != null) {
               return itemStack;
            }
         }

         itemBuilder = NexoItems.itemFromId(NexoYamlKt.getRootId(section));
         itemStack = itemBuilder != null ? itemBuilder.build() : null;
      }

      return itemStack;
   }

   private static final Registry soundRegistry_delegate$lambda$0() {
      RegistryKey registryKey = RegistryKey.SOUND_EVENT;
      return RegistryUtilsKt.toRegistry(registryKey);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001c\u0010\f\u001a\u00020\r*\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0006H\u0002R&\u0010\u0004\u001a\r\u0012\t\u0012\u00070\u0006¢\u0006\u0002\b\u00070\u00058BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\n\u0010\u000b\u001a\u0004\b\b\u0010\t¨\u0006\u0012"},
      d2 = {"Lcom/nexomc/nexo/mechanics/trident/TridentMechanic$Companion;", "", "<init>", "()V", "soundRegistry", "Lorg/bukkit/Registry;", "Lorg/bukkit/Sound;", "Lkotlin/jvm/internal/EnhancedNullability;", "getSoundRegistry", "()Lorg/bukkit/Registry;", "soundRegistry$delegate", "Lkotlin/Lazy;", "tridentSound", "Lcom/nexomc/nexo/mechanics/trident/TridentMechanic$TridentSound;", "Lorg/bukkit/configuration/ConfigurationSection;", "id", "", "sound", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      private final Registry<Sound> getSoundRegistry() {
         Lazy lazy = TridentMechanic.soundRegistry$delegate;
         return (Registry)lazy.getValue();
      }

      private final TridentSound tridentSound(ConfigurationSection configurationSection2, String id, Sound sound) {
         ConfigurationSection configurationSection = NexoYamlKt.section(configurationSection2, id);
         TridentSound tridentSound;
         if (configurationSection != null) {
            ConfigurationSection p0 = configurationSection;
            int value = 0;
            tridentSound = new TridentSound(p0);
         } else {
            String str = NexoYamlKt.stringOrNull(configurationSection2, id);
            if (str != null) {
               String p0 = str;
               int value2 = 0;
               tridentSound = new TridentSound(p0);
            } else {
               String p0 = this.getSoundRegistry().getKeyOrThrow((org.bukkit.Sound)(Object)sound).asString();
               int value3 = 0;
               tridentSound = new TridentSound(p0);
            }
         }

         return tridentSound;
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B/\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003¢\u0006\u0004\b\u0007\u0010\bB\u0011\b\u0016\u0012\u0006\u0010\t\u001a\u00020\n¢\u0006\u0004\b\u0007\u0010\u000bJ\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0003HÆ\u0003J1\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u0003HÆ\u0001J\u0014\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u0019\u001a\u00020\u001aHÖ\u0081\u0004J\n\u0010\u001b\u001a\u00020\u001cHÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\r¨\u0006\u001d"},
      d2 = {"Lcom/nexomc/nexo/mechanics/trident/TridentMechanic$TridentSounds;", "", "_throw", "Lcom/nexomc/nexo/mechanics/trident/TridentMechanic$TridentSound;", "hit", "hitGround", "_return", "<init>", "(Lcom/nexomc/nexo/mechanics/trident/TridentMechanic$TridentSound;Lcom/nexomc/nexo/mechanics/trident/TridentMechanic$TridentSound;Lcom/nexomc/nexo/mechanics/trident/TridentMechanic$TridentSound;Lcom/nexomc/nexo/mechanics/trident/TridentMechanic$TridentSound;)V", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "get_throw", "()Lcom/nexomc/nexo/mechanics/trident/TridentMechanic$TridentSound;", "getHit", "getHitGround", "get_return", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toString", "", "com.nexomc:core"}
   )
   public static final class TridentSounds {
      @NotNull
      private final TridentSound _throw;
      @NotNull
      private final TridentSound hit;
      @NotNull
      private final TridentSound hitGround;
      @NotNull
      private final TridentSound _return;

      public TridentSounds(@NotNull TridentSound _throw, @NotNull TridentSound hit, @NotNull TridentSound hitGround, @NotNull TridentSound _return) {
         super();
         this._throw = _throw;
         this.hit = hit;
         this.hitGround = hitGround;
         this._return = _return;
      }

      @NotNull
      public final TridentSound get_throw() {
         return this._throw;
      }

      @NotNull
      public final TridentSound getHit() {
         return this.hit;
      }

      @NotNull
      public final TridentSound getHitGround() {
         return this.hitGround;
      }

      @NotNull
      public final TridentSound get_return() {
         return this._return;
      }

      public TridentSounds(@NotNull ConfigurationSection section) {
         Companion companion = TridentMechanic.Companion;
         Sound sound = Sound.ITEM_TRIDENT_THROW;
         TridentSound tridentSound = companion.tridentSound(section, "throw", sound);
         Companion companion2 = TridentMechanic.Companion;
         Sound sound2 = Sound.ITEM_TRIDENT_HIT;
         TridentSound tridentSound2 = companion2.tridentSound(section, "hit", sound2);
         Companion companion3 = TridentMechanic.Companion;
         Sound sound3 = Sound.ITEM_TRIDENT_HIT_GROUND;
         TridentSound tridentSound3 = companion3.tridentSound(section, "hit_ground", sound3);
         Companion companion4 = TridentMechanic.Companion;
         Sound sound4 = Sound.ITEM_TRIDENT_RETURN;
         this(tridentSound, tridentSound2, tridentSound3, companion4.tridentSound(section, "return", sound4));
      }

      @NotNull
      public final TridentSound component1() {
         return this._throw;
      }

      @NotNull
      public final TridentSound component2() {
         return this.hit;
      }

      @NotNull
      public final TridentSound component3() {
         return this.hitGround;
      }

      @NotNull
      public final TridentSound component4() {
         return this._return;
      }

      @NotNull
      public final TridentSounds copy(@NotNull TridentSound _throw, @NotNull TridentSound hit, @NotNull TridentSound hitGround, @NotNull TridentSound _return) {
         return new TridentSounds(_throw, hit, hitGround, _return);
      }

      @NotNull
      public String toString() {
         return "TridentSounds(_throw=" + this._throw + ", hit=" + this.hit + ", hitGround=" + this.hitGround + ", _return=" + this._return + ")";
      }

      public int hashCode() {
         int result = this._throw.hashCode();
         result = result * 31 + this.hit.hashCode();
         result = result * 31 + this.hitGround.hashCode();
         result = result * 31 + this._return.hashCode();
         return result;
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof TridentSounds)) {
            return false;
         } else {
            TridentSounds tridentSounds = (TridentSounds)other;
            if (!Intrinsics.areEqual(this._throw, tridentSounds._throw)) {
               return false;
            } else if (!Intrinsics.areEqual(this.hit, tridentSounds.hit)) {
               return false;
            } else if (!Intrinsics.areEqual(this.hitGround, tridentSounds.hitGround)) {
               return false;
            } else {
               return Intrinsics.areEqual(this._return, tridentSounds._return);
            }
         }
      }

      public TridentSounds() {
         this(new com.nexomc.nexo.mechanics.trident.TridentMechanic.TridentSound(org.bukkit.Sound.ITEM_TRIDENT_THROW, 1.0F, 1.0F), new com.nexomc.nexo.mechanics.trident.TridentMechanic.TridentSound(org.bukkit.Sound.ITEM_TRIDENT_HIT, 1.0F, 1.0F), new com.nexomc.nexo.mechanics.trident.TridentMechanic.TridentSound(org.bukkit.Sound.ITEM_TRIDENT_HIT_GROUND, 1.0F, 1.0F), new com.nexomc.nexo.mechanics.trident.TridentMechanic.TridentSound(org.bukkit.Sound.ITEM_TRIDENT_RETURN, 1.0F, 1.0F));
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005¢\u0006\u0004\b\u0007\u0010\bB!\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\t\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005¢\u0006\u0004\b\u0007\u0010\nB\u0011\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0007\u0010\u000bB\u0011\b\u0016\u0012\u0006\u0010\f\u001a\u00020\r¢\u0006\u0004\b\u0007\u0010\u000eJ\u000e\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0017J\t\u0010\u0018\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0019\u001a\u00020\u0005HÆ\u0003J\t\u0010\u001a\u001a\u00020\u0005HÆ\u0003J'\u0010\u001b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0005HÆ\u0001J\u0014\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u001f\u001a\u00020 HÖ\u0081\u0004J\n\u0010!\u001a\u00020\u0003HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0012¨\u0006\""},
      d2 = {"Lcom/nexomc/nexo/mechanics/trident/TridentMechanic$TridentSound;", "", "sound", "", "volume", "", "pitch", "<init>", "(Ljava/lang/String;FF)V", "Lorg/bukkit/Sound;", "(Lorg/bukkit/Sound;FF)V", "(Ljava/lang/String;)V", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getSound", "()Ljava/lang/String;", "getVolume", "()F", "getPitch", "playSound", "", "location", "Lorg/bukkit/Location;", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "com.nexomc:core"}
   )
   public static final class TridentSound {
      @NotNull
      private final String sound;
      private final float volume;
      private final float pitch;

      public TridentSound(@NotNull String sound, float volume, float pitch) {
         super();
         this.sound = sound;
         this.volume = volume;
         this.pitch = pitch;
      }

      @NotNull
      public final String getSound() {
         return this.sound;
      }

      public final float getVolume() {
         return this.volume;
      }

      public final float getPitch() {
         return this.pitch;
      }

      public TridentSound(@NotNull Sound sound, float volume, float pitch) {
         String asString = TridentMechanic.Companion.getSoundRegistry().getKeyOrThrow((org.bukkit.Sound)(Object)sound).asString();
         this(asString, volume, pitch);
      }

      public TridentSound(@NotNull String sound) {
         this(sound, 1.0F, 1.0F);
      }

      public TridentSound(@NotNull ConfigurationSection section) {
         String str = NexoYamlKt.stringOrEmpty(section, "sound");
         Float number = NexoYamlKt.floatOrNull(section, "volume");
         float value = number != null ? number : 1.0F;
         Float number2 = NexoYamlKt.floatOrNull(section, "pitch");
         this(str, value, number2 != null ? number2 : 1.0F);
      }

      public final void playSound(@NotNull Location location) {
         location.getWorld().playSound(location, this.sound, this.volume, this.pitch);
      }

      @NotNull
      public final String component1() {
         return this.sound;
      }

      public final float component2() {
         return this.volume;
      }

      public final float component3() {
         return this.pitch;
      }

      @NotNull
      public final TridentSound copy(@NotNull String sound, float volume, float pitch) {
         return new TridentSound(sound, volume, pitch);
      }

      @NotNull
      public String toString() {
         return "TridentSound(sound=" + this.sound + ", volume=" + this.volume + ", pitch=" + this.pitch + ")";
      }

      public int hashCode() {
         int result = this.sound.hashCode();
         result = result * 31 + Float.hashCode(this.volume);
         result = result * 31 + Float.hashCode(this.pitch);
         return result;
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof TridentSound)) {
            return false;
         } else {
            TridentSound tridentSound = (TridentSound)other;
            if (!Intrinsics.areEqual(this.sound, tridentSound.sound)) {
               return false;
            } else if (Float.compare(this.volume, tridentSound.volume) != 0) {
               return false;
            } else {
               return Float.compare(this.pitch, tridentSound.pitch) == 0;
            }
         }
      }
   }
}
