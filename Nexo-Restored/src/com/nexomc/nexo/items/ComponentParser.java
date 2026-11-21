package com.nexomc.nexo.items;

import com.nexomc.nexo.decomp.ResultShim;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.commands.DyeCommandKt;
import com.nexomc.nexo.compatibilities.mythiccrucible.WrappedCrucibleItem;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.utils.DurationUtilsKt;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.RangeUtilsKt;
import com.nexomc.nexo.utils.RegistryUtils;
import com.nexomc.nexo.utils.RegistryUtilsKt;
import com.nexomc.nexo.utils.VersionUtil;
import com.nexomc.nexo.utils.logs.Logs;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.item.AttackRange;
import io.papermc.paper.datacomponent.item.BlocksAttacks;
import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.datacomponent.item.Enchantable;
import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.datacomponent.item.FoodProperties;
import io.papermc.paper.datacomponent.item.KineticWeapon;
import io.papermc.paper.datacomponent.item.PiercingWeapon;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import io.papermc.paper.datacomponent.item.SwingAnimation;
import io.papermc.paper.datacomponent.item.Tool;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import io.papermc.paper.datacomponent.item.UseCooldown;
import io.papermc.paper.datacomponent.item.UseEffects;
import io.papermc.paper.datacomponent.item.UseRemainder;
import io.papermc.paper.datacomponent.item.Weapon;
import io.papermc.paper.datacomponent.item.SwingAnimation.Animation;
import io.papermc.paper.datacomponent.item.blocksattacks.DamageReduction;
import io.papermc.paper.datacomponent.item.blocksattacks.ItemDamageFunction;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import io.papermc.paper.registry.set.RegistrySet;
import io.papermc.paper.registry.tag.Tag;
import io.papermc.paper.registry.tag.TagKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.ranges.ClosedFloatingPointRange;
import kotlin.ranges.ClosedRange;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import kotlin.time.Duration;
import kotlin.time.DurationUnit;
import net.Indyuce.mmoitems.MMOItems;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.util.TriState;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.JukeboxSong;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.block.BlockType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u000b\u0018\u0000 \u00162\u00020\u0001:\u0001\u0016B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0006\u0010\u000b\u001a\u00020\fJ\b\u0010\r\u001a\u00020\fH\u0002J\u0018\u0010\u000e\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\u00052\u0006\u0010\u0010\u001a\u00020\u0003H\u0002J\b\u0010\u0011\u001a\u00020\fH\u0002J\b\u0010\u0012\u001a\u00020\fH\u0002J\b\u0010\u0013\u001a\u00020\fH\u0002J\b\u0010\u0014\u001a\u00020\fH\u0002J\b\u0010\u0015\u001a\u00020\fH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0017"},
   d2 = {"Lcom/nexomc/nexo/items/ComponentParser;", "", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "itemBuilder", "Lcom/nexomc/nexo/items/ItemBuilder;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;Lcom/nexomc/nexo/items/ItemBuilder;)V", "componentSection", "itemId", "", "parseComponents", "", "parseKineticWeaponComponent", "parseUseRemainderComponent", "item", "remainderSection", "parseEquippableComponent", "parseFoodComponent", "parseToolComponent", "parseWeaponComponent", "parseBlocksAttacksComponent", "Companion", "com.nexomc:core"}
)
public final class ComponentParser {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final ItemBuilder itemBuilder;
   @Nullable
   private final ConfigurationSection componentSection;
   @NotNull
   private final String itemId;

   public ComponentParser(@NotNull ConfigurationSection section, @NotNull ItemBuilder itemBuilder) {
      super();
      this.itemBuilder = itemBuilder;
      this.componentSection = NexoYamlKt.section(section, "Components");
      String name = section.getName();
      this.itemId = name;
   }

   public final void parseComponents() {
      kotlin.ranges.ClosedFloatingPointRange closedFloatingPointRange;
      kotlin.ranges.ClosedFloatingPointRange closedFloatingPointRange2;
      if (this.componentSection != null) {
         this.itemBuilder.setUnsetComponents$com_nexomc_core(NexoYamlKt.keyListOrSingle(this.componentSection, "unset_components", (String)null));
         Iterable iterable = (Iterable)NexoYamlKt.sectionListOrSingle(this.componentSection, "can_place_on");
         if (CollectionsKt.count(iterable) > 0) {
            List it = (List)iterable;
            int value2 = 0;
            NMSHandlers.handler().itemUtils().canPlaceOnComponent(this.itemBuilder, it);
         }

         iterable = (Iterable)NexoYamlKt.sectionListOrSingle(this.componentSection, "can_break");
         if (CollectionsKt.count(iterable) > 0) {
            List it = (List)iterable;
            int value3 = 0;
            NMSHandlers.handler().itemUtils().canBreakComponent(this.itemBuilder, it);
         }

         Map map;
         Map map2;
         block: {
            map2 = this.itemBuilder.getCustomDataMap();
            ConfigurationSection configurationSection = NexoYamlKt.section(this.componentSection, "custom_data");
            if (configurationSection != null) {
               map = NexoYamlKt.toMap(configurationSection);
               if (map != null) {
                  break block;
               }
            }

            map = MapsKt.emptyMap();
         }

         map2.putAll(map);
         if (this.componentSection.contains("max_stack_size")) {
            this.itemBuilder.maxStackSize(RangesKt.coerceIn(this.componentSection.getInt("max_stack_size"), 1, 99));
         }

         ItemBuilder itemBuilder2 = this.itemBuilder;
         Key key = NexoYamlKt.key(this.componentSection, "instrument");
         MusicInstrument musicInstrument;
         if (key != null) {
            Key key5 = key;
            RegistryKey registryKey = RegistryKey.INSTRUMENT;
            Registry registry3 = RegistryUtilsKt.toRegistry(registryKey);
            ItemBuilder itemBuilder3 = itemBuilder2;
            int value20 = 0;
            musicInstrument = (MusicInstrument)registry3.get(key5);
            itemBuilder2 = itemBuilder3;
         } else {
            musicInstrument = null;
         }

         itemBuilder2.instrument(musicInstrument);
         if (this.componentSection.contains("enchantment_glint_override")) {
            this.itemBuilder.setEnchantmentGlintOverride(NexoYamlKt.triState(this.componentSection, "enchantment_glint_override"));
         }

         if (this.componentSection.contains("max_damage")) {
            this.itemBuilder.setMaxDamage(RangesKt.coerceAtLeast(this.componentSection.getInt("max_damage"), 1));
         }

         if (this.componentSection.contains("rarity")) {
            ConfigurationSection p0 = this.componentSection;
            String str3 = "rarity";
            int value21 = 0;
            this.itemBuilder.setRarity((ItemRarity)EnumUtils.getEnum(ItemRarity.class, NexoYamlKt.stringOrNull(p0, str3)));
         }

         block: {
            this.parseFoodComponent();
            this.parseToolComponent();
            this.itemBuilder.setPaintingVariant(NexoYamlKt.key(this.componentSection, "painting_variant"));
            this.itemBuilder.setTooltipStyle(NexoYamlKt.key(this.componentSection, "tooltip_style"));
            this.itemBuilder.setItemModel(NexoYamlKt.key(this.componentSection, "item_model"));
            Key key2 = NexoYamlKt.key(this.componentSection, "jukebox_playable");
            if (key2 != null) {
               Key deathProtection = key2;
               RegistryKey registryKey2 = RegistryKey.JUKEBOX_SONG;
               Registry p0 = RegistryUtilsKt.toRegistry(registryKey2);
               int thisCollection = 0;
               JukeboxSong jukeboxSong = (JukeboxSong)p0.get(deathProtection);
               if (jukeboxSong != null) {
                  JukeboxSong cooldownSection = jukeboxSong;
                  ItemBuilder itemBuilder4 = this.itemBuilder;
                  thisCollection = 0;
                  itemBuilder4.setJukeboxPlayable(cooldownSection);
                  break block;
               }
            }

            Object obj = null;
         }

         ConfigurationSection configurationSection2 = NexoYamlKt.section(this.componentSection, "use_remainder");
         if (configurationSection2 != null) {
            ConfigurationSection it = configurationSection2;
            int value4 = 0;
            this.parseUseRemainderComponent(this.itemBuilder, it);
            Unit unit = Unit.INSTANCE;
            unit = Unit.INSTANCE;
         } else {
            Object obj2 = null;
         }

         configurationSection2 = NexoYamlKt.section(this.componentSection, "death_protection");
         if (configurationSection2 != null) {
            ConfigurationSection deathProtection = configurationSection2;
            int value5 = 0;
            NMSHandlers.handler().itemUtils().deathProtectionComponent(this.itemBuilder, deathProtection);
            Unit unit2 = Unit.INSTANCE;
            unit2 = Unit.INSTANCE;
         } else {
            Object obj3 = null;
         }

         configurationSection2 = NexoYamlKt.section(this.componentSection, "use_cooldown");
         UseCooldown useCooldown;
         if (configurationSection2 != null) {
            ConfigurationSection cooldownSection = configurationSection2;
            int value6 = 0;
            useCooldown = (UseCooldown)UseCooldown.useCooldown(DurationUtilsKt.toFloat_VtjQ1oo(NexoYamlKt.duration(cooldownSection, "duration", DurationUnit.SECONDS), DurationUnit.SECONDS)).cooldownGroup(NexoYamlKt.key(cooldownSection, "group", "nexo:" + NexoItems.idFromItem(this.itemBuilder))).build();
         } else {
            useCooldown = null;
         }

         UseCooldown p0 = useCooldown;
         ItemBuilder damageResistantKey = this.itemBuilder;
         int value7 = 0;
         damageResistantKey.setUseCooldownComponent(p0);
         Key key3 = NexoYamlKt.key(this.componentSection, "damage_resistant");
         if (key3 != null) {
            Key damageResistantKey_17 = key3;
            value7 = 0;
            ItemBuilder itemBuilder5 = this.itemBuilder;
            RegistryUtils registryUtils = RegistryUtils.INSTANCE;
            RegistryKey registryKey3 = RegistryKey.DAMAGE_TYPE;
            itemBuilder5.setDamageResistant(DamageResistant.damageResistant(registryUtils.getTag(registryKey3, damageResistantKey_17)));
         } else {
            Object obj4 = null;
         }

         ConfigurationSection configurationSection3 = NexoYamlKt.section(this.componentSection, "consumable");
         if (configurationSection3 != null) {
            ConfigurationSection consumableSection = configurationSection3;
            value7 = 0;
            NMSHandlers.handler().itemUtils().consumableComponent(this.itemBuilder, consumableSection);
            Unit unit3 = Unit.INSTANCE;
            unit3 = Unit.INSTANCE;
         } else {
            Object obj5 = null;
         }

         this.parseEquippableComponent();
         if (this.componentSection.contains("enchantable")) {
            this.itemBuilder.setEnchantable(Enchantable.enchantable(RangesKt.coerceAtLeast(this.componentSection.getInt("enchantable"), 1)));
         }

         if (this.componentSection.contains("glider")) {
            this.itemBuilder.setGlider(this.componentSection.getBoolean("glider"));
         }

         NMSHandlers.handler().itemUtils().repairableComponent(this.itemBuilder, NexoYamlKt.keyListOrSingle(this.componentSection, "repairable", (String)null));
         configurationSection3 = NexoYamlKt.section(this.componentSection, "profile");
         if (configurationSection3 != null) {
            ConfigurationSection profile = configurationSection3;
            value7 = 0;
            ResolvableProfile.Builder resolvableProfile = ResolvableProfile.resolvableProfile();
            ResolvableProfile.Builder resolvable = resolvableProfile;
            String name = NexoYamlKt.stringOrNull(profile, "name");
            String str = NexoYamlKt.stringOrNull(profile, "uuid");
            UUID uuid2;
            if (str != null) {
               String p0_16 = str;
               int value8 = 0;
               uuid2 = UUID.fromString(p0_16);
            } else {
               uuid2 = null;
            }

            UUID uuid = uuid2;
            Iterable iterable2 = (Iterable)CollectionsKt.plus((Collection)NexoYamlKt.sectionListOrSingle(profile, "property"), (Iterable)NexoYamlKt.sectionListOrSingle(profile, "properties"));
            Iterator iterator = iterable2.iterator();

            while(true) {
               if (!iterator.hasNext()) {
                  this.itemBuilder.setProfile$com_nexomc_core((ResolvableProfile)resolvable.name(name).uuid(uuid).build());
                  Unit unit4 = Unit.INSTANCE;
                  break;
               }

               Object element = iterator.next();
               ConfigurationSection property = (ConfigurationSection)element;
               int value9 = 0;
               String str2 = NexoYamlKt.stringOrNull(property, "name");
               if (str2 == null) {
                  Object obj6 = null;
                  break;
               }

               String name_15 = str2;
               str2 = NexoYamlKt.stringOrNull(property, "value");
               if (str2 == null) {
                  Object obj7 = null;
                  break;
               }

               String value = str2;
               String signature = NexoYamlKt.stringOrNull(property, "signature");
               resolvable.addProperty(new ProfileProperty(name_15, value, signature));
            }
         } else {
            Object obj8 = null;
         }

         configurationSection3 = NexoYamlKt.section(this.componentSection, "custom_model_data");
         if (configurationSection3 != null) {
            ConfigurationSection cmdSection = configurationSection3;
            value7 = 0;
            io.papermc.paper.datacomponent.item.CustomModelData.Builder customModelData = io.papermc.paper.datacomponent.item.CustomModelData.customModelData();
            Iterable p0_14 = (Iterable)NexoYamlKt.stringListOrSingle(cmdSection, "color", (String)null);
            io.papermc.paper.datacomponent.item.CustomModelData.Builder builder2 = customModelData;
            int value22 = 0;
            Collection destination = (Collection)(new ArrayList());

            for(Object obj11 : p0_14) {
               int value10 = 0;
               String it = (String)obj11;
               int value11 = 0;
               Color color = DyeCommandKt.toColor(it);
               if (color != null) {
                  Object obj12 = color;
                  int value12 = 0;
                  destination.add(obj12);
               }
            }

            customModelData = builder2.addColors((List)destination);
            p0_14 = (Iterable)NexoYamlKt.stringListOrSingle(cmdSection, "float", (String)null);
            builder2 = customModelData;
            value22 = 0;
            destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(p0_14, 10)));

            for(Object item : p0_14) {
               String str4 = (String)item;
               int value13 = 0;
               destination.add(Float.parseFloat(str4));
            }

            customModelData = builder2.addFloats((List)destination).addStrings(NexoYamlKt.stringListOrSingle(cmdSection, "string", (String)null));
            p0_14 = (Iterable)NexoYamlKt.stringListOrSingle(cmdSection, "flag", (String)null);
            builder2 = customModelData;
            value22 = 0;
            destination = (Collection)(new ArrayList());

            for(Object obj13 : p0_14) {
               int value14 = 0;
               String it = (String)obj13;
               int value15 = 0;
               Boolean flag = StringsKt.toBooleanStrictOrNull(it);
               if (flag != null) {
                  Object obj14 = flag;
                  int value16 = 0;
                  destination.add(obj14);
               }
            }

            customModelData = builder2.addFlags((List)destination);
            io.papermc.paper.datacomponent.item.CustomModelData.Builder cmd = customModelData;
            this.itemBuilder.setCustomModelData((io.papermc.paper.datacomponent.item.CustomModelData)cmd.build());
         } else {
            Object obj9 = null;
         }

         List list = NexoYamlKt.keyListOrNull(this.componentSection, "tooltip_display");
         if (list != null) {
            List displayList = list;
            value7 = 0;
            if (VersionUtil.atleast("1.21.5")) {
               Registry registry2 = RegistryAccess.registryAccess().getRegistry(RegistryKey.DATA_COMPONENT_TYPE);
               Registry registry = registry2;
               ItemBuilder itemBuilder6 = this.itemBuilder;
               TooltipDisplay.Builder tooltipDisplay = TooltipDisplay.tooltipDisplay();
               Iterable iterable3 = (Iterable)displayList;
               TooltipDisplay.Builder builder3 = tooltipDisplay;
               ItemBuilder itemBuilder13 = itemBuilder6;
               int value23 = 0;
               Collection collection = (Collection)(new ArrayList());

               for(Object obj15 : iterable3) {
                  int value17 = 0;
                  Key p0_13 = (Key)obj15;
                  int value18 = 0;
                  DataComponentType dataComponentType = (DataComponentType)registry.get(p0_13);
                  if (dataComponentType != null) {
                     Object obj16 = dataComponentType;
                     int value19 = 0;
                     collection.add(obj16);
                  }
               }

               List list2 = (List)collection;
               Collection collection2 = (Collection)list2;
               value23 = 0;
               DataComponentType[] dataComponentTypes = (DataComponentType[])collection2.toArray(new DataComponentType[0]);
               itemBuilder13.setTooltipDisplay((TooltipDisplay)builder3.addHiddenComponents((DataComponentType[])Arrays.copyOf(dataComponentTypes, dataComponentTypes.length)).build());
            } else {
               this.itemBuilder.setHiddenComponents(displayList);
            }
         } else {
            Object obj10 = null;
         }

         if (!VersionUtil.INSTANCE.below("1.21.5")) {
            this.itemBuilder.setBreakSound$com_nexomc_core(NexoYamlKt.key(this.componentSection, "break_sound"));
            this.parseWeaponComponent();
            this.parseBlocksAttacksComponent();
            if (!VersionUtil.INSTANCE.below("1.21.11")) {
               ItemBuilder itemBuilder7 = this.itemBuilder;
               ConfigurationSection configurationSection4 = NexoYamlKt.section(this.componentSection, "attack_range");
               AttackRange attackRange;
               if (configurationSection4 != null) {
                  ConfigurationSection section;
                  ItemBuilder itemBuilder8;
                  block: {
                     section = configurationSection4;
                     itemBuilder8 = itemBuilder7;
                     value7 = 0;
                     ClosedFloatingPointRange closedFloatingPointRange3 = NexoYamlKt.floatRangeOrNull(section, "min_reach", "max_reach", "reach");
                     if (closedFloatingPointRange3 != null) {
                        closedFloatingPointRange = RangeUtilsKt.coerceIn(closedFloatingPointRange3, 0.0F, 64.0F);
                        if (closedFloatingPointRange != null) {
                           break block;
                        }
                     }

                     closedFloatingPointRange = RangesKt.rangeTo(0.0F, 3.0F);
                  }

                  ClosedFloatingPointRange reach;
                  block: {
                     reach = closedFloatingPointRange;
                     ClosedFloatingPointRange closedFloatingPointRange4 = NexoYamlKt.floatRangeOrNull(section, "min_creative_reach", "max_creative_reach", "creative_reach");
                     if (closedFloatingPointRange4 != null) {
                        closedFloatingPointRange2 = RangeUtilsKt.coerceIn(closedFloatingPointRange4, 0.0F, 64.0F);
                        if (closedFloatingPointRange2 != null) {
                           break block;
                        }
                     }

                     closedFloatingPointRange2 = RangesKt.rangeTo(0.0F, 5.0F);
                  }

                  ClosedFloatingPointRange creativeReach = closedFloatingPointRange2;
                  Float number = NexoYamlKt.floatOrNull(section, "hitbox_margin");
                  float margin = number != null ? RangesKt.coerceIn((float)(number), 0.0F, 1.0F) : 0.3F;
                  number = NexoYamlKt.floatOrNull(section, "mob_factor");
                  float factor = number != null ? RangesKt.coerceIn((float)(number), 0.0F, 2.0F) : 1.0F;
                  attackRange = (AttackRange)AttackRange.attackRange().hitboxMargin(margin).mobFactor(factor).minCreativeReach(((Number)creativeReach.getStart()).floatValue()).maxCreativeReach(((Number)creativeReach.getEndInclusive()).floatValue()).minReach(((Number)reach.getStart()).floatValue()).maxReach(((Number)reach.getEndInclusive()).floatValue()).build();
                  itemBuilder7 = itemBuilder8;
               } else {
                  attackRange = null;
               }

               itemBuilder7.setAttackRange$com_nexomc_core(attackRange);
               this.parseKineticWeaponComponent();
               itemBuilder7 = this.itemBuilder;
               ConfigurationSection configurationSection5 = NexoYamlKt.section(this.componentSection, "piercing_weapon");
               PiercingWeapon piercingWeapon;
               if (configurationSection5 != null) {
                  ConfigurationSection section = configurationSection5;
                  ItemBuilder itemBuilder9 = itemBuilder7;
                  value7 = 0;
                  piercingWeapon = (PiercingWeapon)PiercingWeapon.piercingWeapon().sound(NexoYamlKt.key(section, "sound")).hitSound(NexoYamlKt.key(section, "hit_sound")).dismounts(section.getBoolean("dismounts")).dealsKnockback(section.getBoolean("deals_knockback", true)).build();
                  itemBuilder7 = itemBuilder9;
               } else {
                  piercingWeapon = null;
               }

               itemBuilder7.setPiercingWeapon$com_nexomc_core(piercingWeapon);
               itemBuilder7 = this.itemBuilder;
               Float number2 = NexoYamlKt.floatOrNull(this.componentSection, "minimum_attack_charge");
               itemBuilder7.setMinimumAttackCharge$com_nexomc_core(number2 != null ? RangesKt.coerceIn((float)(number2), 0.0F, 1.0F) : null);
               itemBuilder7 = this.itemBuilder;
               ConfigurationSection configurationSection6 = NexoYamlKt.section(this.componentSection, "swing_animation");
               SwingAnimation swingAnimation;
               if (configurationSection6 != null) {
                  ConfigurationSection section = configurationSection6;
                  ItemBuilder itemBuilder10 = itemBuilder7;
                  value7 = 0;
                  SwingAnimation.Builder builder = SwingAnimation.swingAnimation();
                  Duration duration = NexoYamlKt.durationOrNull(section, "duration", (DurationUnit)null);
                  builder = builder.duration(duration != null ? RangesKt.coerceAtLeast(DurationUtilsKt.getTicks_LRDsOJo(duration.unbox_impl()), 0) : 6);
                  String str5 = "type";
                  int value24 = 0;
                  SwingAnimation.Animation animation = (SwingAnimation.Animation)EnumUtils.getEnum(SwingAnimation.Animation.class, NexoYamlKt.stringOrNull(section, str5));
                  if (animation == null) {
                     animation = Animation.WHACK;
                  }

                  swingAnimation = (SwingAnimation)builder.type(animation).build();
                  itemBuilder7 = itemBuilder10;
               } else {
                  swingAnimation = null;
               }

               itemBuilder7.setSwingAnimation$com_nexomc_core(swingAnimation);
               itemBuilder7 = this.itemBuilder;
               ConfigurationSection configurationSection7 = NexoYamlKt.section(this.componentSection, "use_effects");
               UseEffects useEffects;
               if (configurationSection7 != null) {
                  ConfigurationSection p0_12 = configurationSection7;
                  ItemBuilder itemBuilder11 = itemBuilder7;
                  value7 = 0;
                  useEffects = (UseEffects)UseEffects.useEffects().canSprint(p0_12.getBoolean("can_sprint", false)).interactVibrations(p0_12.getBoolean("interact_vibrations", true)).speedMultiplier(((Number)RangesKt.coerceIn(NexoYamlKt.float_(p0_12, "speed_multiplier", 0.2F), 0.0F, 1.0F)).floatValue()).build();
                  itemBuilder7 = itemBuilder11;
               } else {
                  useEffects = null;
               }

               itemBuilder7.setUseEffects$com_nexomc_core(useEffects);
               itemBuilder7 = this.itemBuilder;
               Key key4 = NexoYamlKt.key(this.componentSection, "damage_type");
               DamageType damageType;
               if (key4 != null) {
                  Key damageResistantKey_11 = key4;
                  RegistryKey registryKey4 = RegistryKey.DAMAGE_TYPE;
                  Registry p0_10 = RegistryUtilsKt.toRegistry(registryKey4);
                  ItemBuilder itemBuilder12 = itemBuilder7;
                  int registry = 0;
                  damageType = (DamageType)p0_10.get(damageResistantKey_11);
                  itemBuilder7 = itemBuilder12;
               } else {
                  damageType = null;
               }

               itemBuilder7.setDamageType$com_nexomc_core(damageType);
            }
         }
      }
   }

   private final void parseKineticWeaponComponent() {
      ConfigurationSection configurationSection = this.componentSection;
      if (configurationSection != null) {
         configurationSection = NexoYamlKt.section(configurationSection, "kinetic_weapon");
         if (configurationSection != null) {
            ConfigurationSection section = configurationSection;
            ItemBuilder itemBuilder2 = this.itemBuilder;
            KineticWeapon.Builder kineticWeapon = KineticWeapon.kineticWeapon();
            Duration duration = NexoYamlKt.durationOrNull(section, "contact_cooldown", (DurationUnit)null);
            kineticWeapon = kineticWeapon.contactCooldownTicks(duration != null ? RangesKt.coerceAtLeast(DurationUtilsKt.getTicks_LRDsOJo(duration.unbox_impl()), 0) : 10);
            duration = NexoYamlKt.durationOrNull(section, "delay", (DurationUnit)null);
            kineticWeapon = kineticWeapon.delayTicks(duration != null ? RangesKt.coerceAtLeast(DurationUtilsKt.getTicks_LRDsOJo(duration.unbox_impl()), 0) : 0);
            Float number = NexoYamlKt.floatOrNull(section, "forward_movement");
            kineticWeapon = kineticWeapon.forwardMovement(number != null ? number : 0.0F);
            number = NexoYamlKt.floatOrNull(section, "damage_multiplier");
            kineticWeapon = kineticWeapon.damageMultiplier(number != null ? number : 1.0F).sound(NexoYamlKt.key(section, "sound")).hitSound(NexoYamlKt.key(section, "hit_sound"));
            ConfigurationSection configurationSection2 = NexoYamlKt.section(section, "dismount_conditions");
            KineticWeapon.Condition condition;
            if (configurationSection2 != null) {
               ConfigurationSection p0 = configurationSection2;
               KineticWeapon.Builder builder = kineticWeapon;
               ItemBuilder itemBuilder3 = itemBuilder2;
               int value = 0;
               KineticWeapon.Condition condition2 = parseKineticWeaponComponent$condition(p0);
               itemBuilder2 = itemBuilder3;
               kineticWeapon = builder;
               condition = condition2;
            } else {
               condition = null;
            }

            kineticWeapon = kineticWeapon.dismountConditions(condition);
            ConfigurationSection configurationSection3 = NexoYamlKt.section(section, "knockback_conditions");
            KineticWeapon.Condition condition3;
            if (configurationSection3 != null) {
               ConfigurationSection p0 = configurationSection3;
               KineticWeapon.Builder builder2 = kineticWeapon;
               ItemBuilder itemBuilder4 = itemBuilder2;
               int value2 = 0;
               KineticWeapon.Condition condition4 = parseKineticWeaponComponent$condition(p0);
               itemBuilder2 = itemBuilder4;
               kineticWeapon = builder2;
               condition3 = condition4;
            } else {
               condition3 = null;
            }

            kineticWeapon = kineticWeapon.knockbackConditions(condition3);
            ConfigurationSection configurationSection4 = NexoYamlKt.section(section, "damage_conditions");
            KineticWeapon.Condition condition5;
            if (configurationSection4 != null) {
               ConfigurationSection p0 = configurationSection4;
               KineticWeapon.Builder builder3 = kineticWeapon;
               ItemBuilder itemBuilder5 = itemBuilder2;
               int value3 = 0;
               KineticWeapon.Condition condition6 = parseKineticWeaponComponent$condition(p0);
               itemBuilder2 = itemBuilder5;
               kineticWeapon = builder3;
               condition5 = condition6;
            } else {
               condition5 = null;
            }

            itemBuilder2.setKineticWeapon$com_nexomc_core((KineticWeapon)kineticWeapon.damageConditions(condition5).build());
            return;
         }
      }

   }

   private final void parseUseRemainderComponent(ItemBuilder item, ConfigurationSection remainderSection) {
      io.papermc.paper.datacomponent.item.UseRemainder useRemainder;
      ItemStack itemStack;
      if (remainderSection.contains("nexo_item")) {
         block: {
            ItemBuilder itemBuilder2 = NexoItems.itemFromId(remainderSection.getString("nexo_item"));
            if (itemBuilder2 != null) {
               ItemStack itemStack2 = itemBuilder2.build();
               if (itemStack2 != null) {
                  ItemStack p0 = itemStack2;
                  ItemUpdater.Companion companion = ItemUpdater.Companion;
                  int value = 0;
                  itemStack = companion.updateItem(p0);
                  break block;
               }
            }

            itemStack = null;
         }
      } else if (remainderSection.contains("crucible_item")) {
         itemStack = (new WrappedCrucibleItem(remainderSection.getString("crucible_item"), true)).build();
      } else if (remainderSection.contains("mmoitems_id") && remainderSection.isString("mmoitems_type")) {
         itemStack = MMOItems.plugin.getItem(remainderSection.getString("mmoitems_type"), remainderSection.getString("mmoitems_id"));
      } else if (remainderSection.contains("minecraft_type")) {
         Material material = Material.getMaterial(NexoYamlKt.stringOrEmpty(remainderSection, "minecraft_type"));
         if (material == null) {
            return;
         }

         itemStack = ItemStack.of(material);
      } else {
         itemStack = remainderSection.getItemStack("minecraft_item");
      }

      block: {
         ItemStack itemStack3 = itemStack;
         if (itemStack3 != null) {
            ItemStack p0 = itemStack3.asQuantity(remainderSection.getInt("amount", 1));
            if (p0 != null) {
               int value2 = 0;
               useRemainder = UseRemainder.useRemainder(p0);
               break block;
            }
         }

         useRemainder = null;
      }

      UseRemainder useItem = useRemainder;
      item.setUseRemainder(useItem);
   }

   private final void parseEquippableComponent() {
      net.kyori.adventure.key.Key key;
      ConfigurationSection configurationSection = this.componentSection;
      if (configurationSection != null) {
         configurationSection = NexoYamlKt.section(configurationSection, "equippable");
         if (configurationSection != null) {
            ConfigurationSection section = configurationSection;
            com.nexomc.nexo.utils.EnumUtils enumUtils = com.nexomc.nexo.utils.EnumUtils.INSTANCE;
            String str = NexoYamlKt.stringOrEmpty(section, "slot");
            Object[] objs = EquipmentSlot.values();
            int value6 = 0;
            int value = objs.length;

            Enum enumValue2;
            while(true) {
               if (value6 >= value) {
                  enumValue2 = null;
                  break;
               }

               Object element = objs[value6];
               Enum enumValue4 = (Enum)element;
               int value2 = 0;
               if (StringsKt.equals(enumValue4.name(), str, true)) {
                  enumValue2 = (Enum)element;
                  break;
               }

               ++value6;
            }

            Enum enumValue3 = (Enum)enumValue2;
            if ((Enum)enumValue2 == null) {
               int destination = 0;
               Logs.logWarn("Error parsing equippable-component in " + this.itemId + "...", false);
               Logs.logWarn("Invalid \"slot\"-value " + section.getString("slot"), false);
               Iterable iterable2 = (Iterable)ComponentParser.EntriesMappings.entries$0;
               Logs.logWarn("Valid values are: " + CollectionsKt.joinToString(iterable2, (CharSequence)", ", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", (Function1)null), false);
               return;
            }

            EquipmentSlot slot = (EquipmentSlot)enumValue3;
            Equippable.Builder builder = Equippable.equippable(slot);
            Equippable.Builder equippable = builder;
            Collection collection = (Collection)NexoYamlKt.stringListOrSingle(section, "allowed_entity_types", (String)null);
            Object obj;
            if (collection.isEmpty()) {
               obj = NexoYamlKt.stringListOrSingle(section, "allowed_entity_type", (String)null);
            } else {
               obj = collection;
            }

            Iterable iterable3 = (Iterable)obj;
            Collection collection2 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable3, 10)));
            value6 = 0;

            for(Object item : iterable3) {
               block: {
                  String it = (String)item;
                  int value3 = 0;
                  EntityType entityType = (EntityType)EnumUtils.getEnum(EntityType.class, it);
                  if (entityType != null) {
                     key = entityType.key();
                     if (key != null) {
                        break block;
                     }
                  }

                  key = Key.key(it);
               }

               collection2.add(key);
            }

            iterable3 = (Iterable)((List)collection2);
            if (CollectionsKt.count(iterable3) > 0) {
               List it = (List)iterable3;
               int value4 = 0;
               RegistryKey registryKey = RegistryKey.ENTITY_TYPE;
               Iterable iterable = (Iterable)it;
               RegistryKey registryKey2 = RegistryKey.ENTITY_TYPE;
               RegistryKey registryKey3 = registryKey2;
               RegistryKey registryKey4 = registryKey;
               Collection collection3 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

               for(Object obj2 : iterable) {
                  Key p0 = (Key)obj2;
                  int value5 = 0;
                  collection3.add(registryKey3.typedKey(p0));
               }

               List list = (List)collection3;
               equippable.allowedEntities(RegistrySet.keySet(registryKey4, (Iterable)list));
            }

            equippable.damageOnHurt(section.getBoolean("damage_on_hurt", this.componentSection.getBoolean("glider", this.itemBuilder.getType() != Material.ELYTRA)));
            equippable.dispensable(section.getBoolean("dispensable", true)).swappable(section.getBoolean("swappable", true));
            equippable.assetId(NexoYamlKt.key(section, "asset_id")).cameraOverlay(NexoYamlKt.key(section, "camera_overlay"));
            Sound defaultSound = this.componentSection.getBoolean("glider", this.itemBuilder.getType() != Material.ELYTRA) ? Sound.ITEM_ARMOR_EQUIP_ELYTRA : Sound.ITEM_ARMOR_EQUIP_GENERIC;
            Key key2 = NexoYamlKt.key(section, "equip_sound");
            if (key2 == null) {
               RegistryKey registryKey5 = RegistryKey.SOUND_EVENT;
               NamespacedKey namespacedKey = RegistryUtilsKt.toRegistry(registryKey5).getKey((Keyed)defaultSound);
               key2 = (Key)namespacedKey;
            }

            equippable.equipSound(key2);
            if (VersionUtil.atleast("1.21.5")) {
               equippable.equipOnInteract(section.getBoolean("equip_on_interact", false));
            }

            if (VersionUtil.atleast("1.21.6")) {
               equippable.canBeSheared(section.getBoolean("can_be_sheared", StringsKt.contains((CharSequence)NexoYamlKt.getRootId(section), (CharSequence)"harness", false)));
               key2 = NexoYamlKt.key(section, "shear_sound");
               if (key2 == null) {
                  RegistryKey registryKey6 = RegistryKey.SOUND_EVENT;
                  NamespacedKey namespacedKey2 = RegistryUtilsKt.toRegistry(registryKey6).getKey((Keyed)Sound.ITEM_SHEARS_SNIP);
                  key2 = (Key)namespacedKey2;
               }

               equippable.shearSound(key2);
            }

            this.itemBuilder.setEquippableComponent((Equippable)equippable.build());
            return;
         }
      }

   }

   private final void parseFoodComponent() {
      ConfigurationSection configurationSection = this.componentSection;
      if (configurationSection != null) {
         configurationSection = NexoYamlKt.section(configurationSection, "food");
         if (configurationSection != null) {
            ConfigurationSection foodSection = configurationSection;
            this.itemBuilder.setFoodComponent((FoodProperties)FoodProperties.food().nutrition(foodSection.getInt("nutrition", 0)).saturation(NexoYamlKt.float_(foodSection, "saturation", 0.0F)).canAlwaysEat(foodSection.getBoolean("can_always_eat")).build());
            return;
         }
      }

   }

   private final void parseToolComponent() {
      ConfigurationSection configurationSection = this.componentSection;
      if (configurationSection != null) {
         configurationSection = NexoYamlKt.section(configurationSection, "tool");
         if (configurationSection != null) {
            ConfigurationSection toolSection = configurationSection;
            Tool.Builder builder = Tool.tool().damagePerBlock(RangesKt.coerceAtLeast(toolSection.getInt("damage_per_block", 1), 0)).defaultMiningSpeed(RangesKt.coerceAtLeast(NexoYamlKt.float_(toolSection, "default_mining_speed", 1.0F), 0.0F));
            Tool.Builder tool = builder;
            Iterable iterable = (Iterable)NexoYamlKt.sectionList(toolSection, "rules");

            for(Object element : iterable) {
               ConfigurationSection section = (ConfigurationSection)element;
               int value = 0;
               float speed = NexoYamlKt.float_(section, "speed", 1.0F);
               TriState triState = TriState.byBoolean(section.getBoolean("correct_for_drops"));
               TriState correctForDrops = triState;
               Iterable iterable2 = (Iterable)NexoYamlKt.enumListOrSingle(section, "materials", Material.class, "material");
               Collection destination = (Collection)(new ArrayList());

               for(Object obj2 : iterable2) {
                  Material p0 = (Material)obj2;
                  if (p0.isBlock()) {
                     destination.add(obj2);
                  }
               }

               iterable2 = (Iterable)((List)destination);
               destination = (Collection)(new ArrayList());

               for(Object obj3 : iterable2) {
                  int value2 = 0;
                  Material it = (Material)obj3;
                  int value3 = 0;
                  BlockType asBlockType = it.asBlockType();
                  Key key = asBlockType != null ? asBlockType.key() : null;
                  if (key != null) {
                     Object obj4 = key;
                     int value4 = 0;
                     destination.add(obj4);
                  }
               }

               List materialKeys = (List)destination;
               if (!((Collection)materialKeys).isEmpty()) {
                  RegistryKey registryKey = RegistryKey.BLOCK;
                  Iterable iterable3 = (Iterable)materialKeys;
                  RegistryKey registryKey2 = RegistryKey.BLOCK;
                  RegistryKey registryKey5 = registryKey2;
                  RegistryKey registryKey3 = registryKey;
                  Collection collection = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable3, 10)));
                  int value7 = 0;

                  for(Object item : iterable3) {
                     Key p0 = (Key)item;
                     int value5 = 0;
                     collection.add(registryKey5.typedKey(p0));
                  }

                  RegistryKeySet registryKeySet = RegistrySet.keySet(registryKey3, (Iterable)((List)collection));
                  RegistryKeySet blocks = registryKeySet;
                  tool.addRule(Tool.rule(blocks, speed, correctForDrops));
               }

               iterable2 = (Iterable)NexoYamlKt.namespacedKeyListOrSingle(section, "tags", "tag");

               for(Object obj5 : iterable2) {
                  NamespacedKey tagKey = (NamespacedKey)obj5;
                  int value8 = 0;
                  ComponentParser componentParser = this;

                  Object obj;
                  try {
                     ComponentParser componentParser2 = componentParser;
                     int value6 = 0;
                     RegistryUtils registryUtils = RegistryUtils.INSTANCE;
                     RegistryKey registryKey4 = RegistryKey.BLOCK;
                     obj = ResultShim.ok(registryUtils.getTag(registryKey4, (Key)tagKey));
                  } catch (Throwable e) {
                     obj = ResultShim.ok(ResultKt.createFailure(e));
                  }

                  Tag tag = (Tag)(ResultShim.isFailure(obj) ? null : obj);
                  if (tag != null) {
                     Tag blocks = tag;
                     tool.addRule(Tool.rule((RegistryKeySet)blocks, speed, correctForDrops));
                  }
               }
            }

            this.itemBuilder.setToolComponent((Tool)tool.build());
            return;
         }
      }

   }

   private final void parseWeaponComponent() {
      ConfigurationSection configurationSection = this.componentSection;
      if (configurationSection != null) {
         configurationSection = NexoYamlKt.section(configurationSection, "weapon");
         if (configurationSection != null) {
            ConfigurationSection weaponSection = configurationSection;
            Object obj = Weapon.weapon().itemDamagePerAttack(weaponSection.getInt("damage_per_attack", 1)).disableBlockingForSeconds(DurationUtilsKt.toFloat_VtjQ1oo(((Duration)RangesKt.coerceAtLeast(Duration.box_impl(NexoYamlKt.duration(weaponSection, "disable_blocking", DurationUnit.SECONDS)), Duration.box_impl(Duration.Companion.getZERO_UwyO8pc()))).unbox_impl(), DurationUnit.SECONDS)).build();
            Weapon weapon = (Weapon)obj;
            this.itemBuilder.setWeaponComponent(weapon);
            return;
         }
      }

   }

   private final void parseBlocksAttacksComponent() {
      ConfigurationSection configurationSection = this.componentSection;
      if (configurationSection != null) {
         configurationSection = NexoYamlKt.section(configurationSection, "blocks_attacks");
         if (configurationSection != null) {
            ConfigurationSection blocksSection = configurationSection;
            BlocksAttacks.Builder builder = BlocksAttacks.blocksAttacks().blockDelaySeconds(DurationUtilsKt.toFloat_VtjQ1oo(((Duration)RangesKt.coerceAtLeast(Duration.box_impl(NexoYamlKt.duration(blocksSection, "block_delay", DurationUnit.SECONDS)), Duration.box_impl(Duration.Companion.getZERO_UwyO8pc()))).unbox_impl(), DurationUnit.SECONDS)).disableCooldownScale(RangesKt.coerceAtLeast(NexoYamlKt.float_(blocksSection, "disable_cooldown_scale", 1.0F), 0.0F)).blockSound(NexoYamlKt.key(blocksSection, "block_sound")).disableSound(NexoYamlKt.key(blocksSection, "disable_sound"));
            Key key2 = NexoYamlKt.key(blocksSection, "bypassed_by");
            io.papermc.paper.registry.set.RegistryKeySet registryKeySet;
            if (key2 != null) {
               Key key3 = key2;
               RegistryKey registryKey = RegistryKey.DAMAGE_TYPE;
               RegistryKey registryKey2 = registryKey;
               BlocksAttacks.Builder builder2 = builder;
               int value = 0;
               registryKeySet = RegistryUtils.INSTANCE.getTag(registryKey2, key3);
               builder = builder2;
            } else {
               registryKeySet = null;
            }

            builder = builder.bypassedBy(registryKeySet);
            BlocksAttacks.Builder blocksAttacks = builder;
            ConfigurationSection configurationSection2 = NexoYamlKt.section(blocksSection, "item_damage");
            if (configurationSection2 != null) {
               ConfigurationSection itemDamageSection = configurationSection2;
               int element = 0;
               ItemDamageFunction.Builder builder3 = ItemDamageFunction.itemDamageFunction().base(NexoYamlKt.float_(itemDamageSection, "base", 1.0F)).factor(NexoYamlKt.float_(itemDamageSection, "factor", 1.0F)).threshold(RangesKt.coerceAtLeast(NexoYamlKt.float_(itemDamageSection, "threshold", 0.0F), 0.0F));
               ItemDamageFunction.Builder itemDamage = builder3;
               blocksAttacks.itemDamage((ItemDamageFunction)itemDamage.build());
            }

            Iterable iterable = (Iterable)NexoYamlKt.sectionListOrSingle(blocksSection, "damage_reductions");

            for(Object obj : iterable) {
               ConfigurationSection reductionSection = (ConfigurationSection)obj;
               int value2 = 0;
               DamageReduction.Builder builder4 = DamageReduction.damageReduction().base(NexoYamlKt.float_(reductionSection, "base", 1.0F)).factor(NexoYamlKt.float_(reductionSection, "factor", 1.0F)).horizontalBlockingAngle(RangesKt.coerceAtLeast(NexoYamlKt.float_(reductionSection, "horizontal_blocking", 90.0F), 0.0F));
               DamageReduction.Builder damageReduction = builder4;
               Iterable iterable2 = (Iterable)NexoYamlKt.stringListOrSingle(reductionSection, "type", (String)null);
               int value7 = 0;
               Collection destination = (Collection)(new ArrayList());

               for(Object obj2 : iterable2) {
                  int value3 = 0;
                  String it = (String)obj2;
                  int value4 = 0;
                  NamespacedKey namespacedKey = NamespacedKey.fromString(it);
                  TypedKey typedKey;
                  if (namespacedKey == null) {
                     typedKey = null;
                  } else {
                     NamespacedKey key = namespacedKey;
                     org.bukkit.Tag tag2 = Bukkit.getTag("damage_types", key, DamageType.class);
                     net.kyori.adventure.key.Keyed tag = tag2 != null ? (net.kyori.adventure.key.Keyed)tag2 : (net.kyori.adventure.key.Keyed)key;
                     typedKey = RegistryKey.DAMAGE_TYPE.typedKey(tag.key());
                  }

                  if (typedKey != null) {
                     Object obj3 = typedKey;
                     int value5 = 0;
                     destination.add(obj3);
                  }
               }

               iterable2 = (Iterable)((List)destination);
               value7 = 0;
               if (CollectionsKt.count(iterable2) > 0) {
                  List it = (List)iterable2;
                  int value6 = 0;
                  RegistryKey registryKey3 = RegistryKey.DAMAGE_TYPE;
                  damageReduction.type(RegistryUtilsKt.toKeySet(registryKey3, (Iterable)it));
               }

               blocksAttacks.addDamageReduction((DamageReduction)damageReduction.build());
            }

            this.itemBuilder.setBlocksAttacksComponent((BlocksAttacks)blocksAttacks.build());
            return;
         }
      }

   }

   private static final KineticWeapon.Condition parseKineticWeaponComponent$condition(ConfigurationSection section) {
      int maxDuration = RangesKt.coerceAtLeast(DurationUtilsKt.getTicks_LRDsOJo(NexoYamlKt.duration(section, "max_duration", (DurationUnit)null)), 0);
      float minSpeed = NexoYamlKt.float_(section, "min_speed", 0.0F);
      float minRelativeSpeed = NexoYamlKt.float_(section, "min_relative_speed", 0.0F);
      KineticWeapon.Condition condition = KineticWeapon.condition(maxDuration, minSpeed, minRelativeSpeed);
      return condition;
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t¨\u0006\n"},
      d2 = {"Lcom/nexomc/nexo/items/ComponentParser$Companion;", "", "<init>", "()V", "setSlot", "Lio/papermc/paper/datacomponent/item/Equippable$Builder;", "equippable", "Lio/papermc/paper/datacomponent/item/Equippable;", "slot", "Lorg/bukkit/inventory/EquipmentSlot;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final Equippable.Builder setSlot(@NotNull Equippable equippable, @NotNull EquipmentSlot slot) {
         Equippable.Builder builder2 = Equippable.equippable(slot).allowedEntities(equippable.allowedEntities()).swappable(equippable.swappable()).damageOnHurt(equippable.damageOnHurt()).equipSound(equippable.equipSound()).dispensable(equippable.dispensable()).assetId(equippable.assetId()).cameraOverlay(equippable.cameraOverlay());
         boolean condition = VersionUtil.atleast("1.21.5");
         Equippable.Builder builder;
         if (condition) {
            int value = 0;
            builder = builder2.equipOnInteract(equippable.equipOnInteract());
         } else {
            builder = builder2;
         }

         builder2 = builder;
         condition = VersionUtil.atleast("1.21.6");
         if (condition) {
            int value2 = 0;
            builder = builder2.canBeSheared(equippable.canBeSheared()).shearSound(equippable.shearSound());
         } else {
            builder = builder2;
         }

         return builder;
      }
   }

   public static final class EntriesMappings {
      public static final kotlin.enums.EnumEntries entries$0 = kotlin.enums.EnumEntriesKt.enumEntries(org.bukkit.inventory.EquipmentSlot.values());
   }
}
