package com.nexomc.nexo.utils.wrappers;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.utils.AdventureUtilsKt;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.VersionUtil;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.attribute.AttributeModifierDisplay;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u00182\u00020\u0001:\u0001\u0018B+\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t¢\u0006\u0004\b\n\u0010\u000bJ\u000e\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015J\n\u0010\u0016\u001a\u0004\u0018\u00010\u0017H\u0002R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0019"},
   d2 = {"Lcom/nexomc/nexo/utils/wrappers/AttributeWrapper;", "", "attribute", "Lorg/bukkit/attribute/Attribute;", "modifier", "Lorg/bukkit/attribute/AttributeModifier;", "slot", "Lorg/bukkit/inventory/EquipmentSlotGroup;", "display", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/attribute/Attribute;Lorg/bukkit/attribute/AttributeModifier;Lorg/bukkit/inventory/EquipmentSlotGroup;Lorg/bukkit/configuration/ConfigurationSection;)V", "getAttribute", "()Lorg/bukkit/attribute/Attribute;", "getModifier", "()Lorg/bukkit/attribute/AttributeModifier;", "getSlot", "()Lorg/bukkit/inventory/EquipmentSlotGroup;", "addToBuilder", "", "builder", "Lio/papermc/paper/datacomponent/item/ItemAttributeModifiers$Builder;", "attributeDisplay", "Lio/papermc/paper/datacomponent/item/attribute/AttributeModifierDisplay;", "Companion", "com.nexomc:core"}
)
public final class AttributeWrapper {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final Attribute attribute;
   @NotNull
   private final AttributeModifier modifier;
   @NotNull
   private final EquipmentSlotGroup slot;
   @Nullable
   private final ConfigurationSection display;

   public AttributeWrapper(@NotNull Attribute attribute, @NotNull AttributeModifier modifier, @NotNull EquipmentSlotGroup slot, @Nullable ConfigurationSection display) {
      super();
      this.attribute = attribute;
      this.modifier = modifier;
      this.slot = slot;
      this.display = display;
   }

   @NotNull
   public final Attribute getAttribute() {
      return this.attribute;
   }

   @NotNull
   public final AttributeModifier getModifier() {
      return this.modifier;
   }

   @NotNull
   public final EquipmentSlotGroup getSlot() {
      return this.slot;
   }

   public final void addToBuilder(@NotNull ItemAttributeModifiers.Builder builder) {
      AttributeModifierDisplay display = this.attributeDisplay();
      if (VersionUtil.atleast("1.21.6") && display != null) {
         builder.addModifier(this.attribute, this.modifier, this.slot, display);
      } else {
         builder.addModifier(this.attribute, this.modifier, this.slot);
      }

   }

   private final AttributeModifierDisplay attributeDisplay() {
        AttributeModifierDisplay attributeModifierDisplay;
        String string;
        if (!VersionUtil.atleast("1.21.6")) {
            return null;
        }
        ConfigurationSection configurationSection = this.display;
        String string2 = string = configurationSection != null ? NexoYamlKt.stringOrNull(configurationSection, "type") : null;
        if (string != null) {
            int n = -1;
            switch (string.hashCode()) {
                case -1217487446: {
                    if (!string.equals("hidden")) break;
                    n = 1;
                    break;
                }
                case 108404047: {
                    if (!string.equals("reset")) break;
                    n = 2;
                    break;
                }
                case 529996748: {
                    if (!string.equals("override")) break;
                    n = 3;
                    break;
                }
            }
            switch (n) {
                case 1: {
                    AttributeModifierDisplay.Hidden hidden = AttributeModifierDisplay.hidden();
                    Intrinsics.checkNotNullExpressionValue((Object)hidden, "hidden(...)");
                    attributeModifierDisplay = (AttributeModifierDisplay)hidden;
                    return attributeModifierDisplay;
                }
                case 2: {
                    AttributeModifierDisplay.Default default_ = AttributeModifierDisplay.reset();
                    Intrinsics.checkNotNullExpressionValue((Object)default_, "reset(...)");
                    attributeModifierDisplay = (AttributeModifierDisplay)default_;
                    return attributeModifierDisplay;
                }
                case 3: {
                    AttributeModifierDisplay.OverrideText overrideText = AttributeModifierDisplay.override(((ComponentLike)AdventureUtilsKt.deserialize(NexoYamlKt.stringOrEmpty(this.display, "text"))));
                    Intrinsics.checkNotNullExpressionValue((Object)overrideText, "override(...)");
                    attributeModifierDisplay = (AttributeModifierDisplay)overrideText;
                    return attributeModifierDisplay;
                }
            }
        }
        AttributeModifierDisplay.Default default_ = AttributeModifierDisplay.reset();
        Intrinsics.checkNotNullExpressionValue((Object)default_, "reset(...)");
        attributeModifierDisplay = (AttributeModifierDisplay)default_;
        return attributeModifierDisplay;
    }

   @JvmStatic
   @Nullable
   public static final Attribute fromString(@Nullable String attribute) {
      return Companion.fromString(attribute);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001d\u0010\u0004\u001a\t\u0018\u00010\u0005¢\u0006\u0002\b\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\bH\u0007b\u0002\b\t¨\u0006\n"},
      d2 = {"Lcom/nexomc/nexo/utils/wrappers/AttributeWrapper$Companion;", "", "<init>", "()V", "fromString", "Lorg/bukkit/attribute/Attribute;", "Lorg/jspecify/annotations/Nullable;", "attribute", "", "Lkotlin/jvm/JvmStatic;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @JvmStatic
      @Nullable
      public final Attribute fromString(@Nullable String attribute) {
         Companion companion = this;

         Object obj;
         try {
            Companion companion2 = companion;
            int value = 0;
            CharSequence charSequence = (CharSequence)attribute;
            Attribute attribute2;
            if (charSequence == null || charSequence.length() == 0) {
               attribute2 = null;
            } else {
               Registry registry = Registry.ATTRIBUTE;
               String str = attribute.toLowerCase(Locale.ROOT);
               attribute2 = (Attribute)registry.get(NamespacedKey.minecraft(StringsKt.removePrefix(StringsKt.removePrefix(str, (CharSequence)"player_"), (CharSequence)"generic_")));
            }

            obj = ResultShim.ok(attribute2);
         } catch (Throwable e) {
            obj = ResultShim.ok(ResultKt.createFailure(e));
         }

         return (Attribute)(ResultShim.isFailure(obj) ? null : obj);
      }
   }
}
