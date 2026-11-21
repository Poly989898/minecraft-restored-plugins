package com.nexomc.nexo.items.customarmor;

import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.utils.KeyUtils;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.RegistryUtilsKt;
import io.papermc.paper.datacomponent.item.Equippable;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.set.RegistryKeySet;
import java.util.Collection;
import java.util.Iterator;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0014\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0016\n\u0002\u0018\u0002\n\u0002\b\u0015\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B»\u0001\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\b\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\t\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\f\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\r\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u000e\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u000f\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0010\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0011\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0012\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0013\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0014\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\u0015\u0010\u0016B\u0011\b\u0016\u0012\u0006\u0010\u0017\u001a\u00020\u0018¢\u0006\u0004\b\u0015\u0010\u0019B\u0011\b\u0016\u0012\u0006\u0010\u001a\u001a\u00020\u001b¢\u0006\u0004\b\u0015\u0010\u001cJ\u0018\u00100\u001a\u0004\u0018\u00010\u00032\u0006\u00101\u001a\u0002022\u0006\u00103\u001a\u00020\u0018J\u000b\u00104\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u00105\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u00106\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u00107\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u00108\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u00109\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010:\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010;\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010<\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010=\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010>\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010?\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010@\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010A\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010B\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010C\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010D\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010E\u001a\u0004\u0018\u00010\u0003HÆ\u0003Já\u0001\u0010F\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0013\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0014\u001a\u0004\u0018\u00010\u0003HÆ\u0001J\u0014\u0010G\u001a\u00020H2\b\u0010I\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010J\u001a\u00020KHÖ\u0081\u0004J\n\u0010L\u001a\u00020\u0018HÖ\u0081\u0004R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u001eR\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b \u0010\u001eR\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b!\u0010\u001eR\u0013\u0010\u0007\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u001eR\u0013\u0010\b\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b#\u0010\u001eR\u0013\u0010\t\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b$\u0010\u001eR\u0013\u0010\n\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b%\u0010\u001eR\u0013\u0010\u000b\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b&\u0010\u001eR\u0013\u0010\f\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b'\u0010\u001eR\u0013\u0010\r\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b(\u0010\u001eR\u0013\u0010\u000e\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b)\u0010\u001eR\u0013\u0010\u000f\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b*\u0010\u001eR\u0013\u0010\u0010\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b+\u0010\u001eR\u0013\u0010\u0011\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b,\u0010\u001eR\u0013\u0010\u0012\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b-\u0010\u001eR\u0013\u0010\u0013\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b.\u0010\u001eR\u0013\u0010\u0014\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b/\u0010\u001e¨\u0006M"},
   d2 = {"Lcom/nexomc/nexo/items/customarmor/CustomArmorTextures;", "", "layer1", "Lnet/kyori/adventure/key/Key;", "layer2", "elytra", "wolfArmor", "llamaArmor", "horseArmor", "camelSaddle", "donkeySaddle", "horseSaddle", "muleSaddle", "pigSaddle", "skeletonHorseSaddle", "striderSaddle", "zombieHorseSaddle", "harness", "nautilusSaddle", "camelHuskSaddle", "humanoidBaby", "<init>", "(Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;Lnet/kyori/adventure/key/Key;)V", "armorPrefix", "", "(Ljava/lang/String;)V", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "getLayer1", "()Lnet/kyori/adventure/key/Key;", "getLayer2", "getElytra", "getWolfArmor", "getLlamaArmor", "getHorseArmor", "getCamelSaddle", "getDonkeySaddle", "getHorseSaddle", "getMuleSaddle", "getPigSaddle", "getSkeletonHorseSaddle", "getStriderSaddle", "getZombieHorseSaddle", "getHarness", "getNautilusSaddle", "getCamelHuskSaddle", "getHumanoidBaby", "fromItem", "item", "Lcom/nexomc/nexo/items/ItemBuilder;", "itemId", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "copy", "equals", "", "other", "hashCode", "", "toString", "com.nexomc:core"}
)
public final class CustomArmorTextures {
   @Nullable
   private final Key layer1;
   @Nullable
   private final Key layer2;
   @Nullable
   private final Key elytra;
   @Nullable
   private final Key wolfArmor;
   @Nullable
   private final Key llamaArmor;
   @Nullable
   private final Key horseArmor;
   @Nullable
   private final Key camelSaddle;
   @Nullable
   private final Key donkeySaddle;
   @Nullable
   private final Key horseSaddle;
   @Nullable
   private final Key muleSaddle;
   @Nullable
   private final Key pigSaddle;
   @Nullable
   private final Key skeletonHorseSaddle;
   @Nullable
   private final Key striderSaddle;
   @Nullable
   private final Key zombieHorseSaddle;
   @Nullable
   private final Key harness;
   @Nullable
   private final Key nautilusSaddle;
   @Nullable
   private final Key camelHuskSaddle;
   @Nullable
   private final Key humanoidBaby;

   public CustomArmorTextures(@Nullable Key layer1, @Nullable Key layer2, @Nullable Key elytra, @Nullable Key wolfArmor, @Nullable Key llamaArmor, @Nullable Key horseArmor, @Nullable Key camelSaddle, @Nullable Key donkeySaddle, @Nullable Key horseSaddle, @Nullable Key muleSaddle, @Nullable Key pigSaddle, @Nullable Key skeletonHorseSaddle, @Nullable Key striderSaddle, @Nullable Key zombieHorseSaddle, @Nullable Key harness, @Nullable Key nautilusSaddle, @Nullable Key camelHuskSaddle, @Nullable Key humanoidBaby) {
      this.layer1 = layer1;
      this.layer2 = layer2;
      this.elytra = elytra;
      this.wolfArmor = wolfArmor;
      this.llamaArmor = llamaArmor;
      this.horseArmor = horseArmor;
      this.camelSaddle = camelSaddle;
      this.donkeySaddle = donkeySaddle;
      this.horseSaddle = horseSaddle;
      this.muleSaddle = muleSaddle;
      this.pigSaddle = pigSaddle;
      this.skeletonHorseSaddle = skeletonHorseSaddle;
      this.striderSaddle = striderSaddle;
      this.zombieHorseSaddle = zombieHorseSaddle;
      this.harness = harness;
      this.nautilusSaddle = nautilusSaddle;
      this.camelHuskSaddle = camelHuskSaddle;
      this.humanoidBaby = humanoidBaby;
   }

   @Nullable
   public final Key getLayer1() {
      return this.layer1;
   }

   @Nullable
   public final Key getLayer2() {
      return this.layer2;
   }

   @Nullable
   public final Key getElytra() {
      return this.elytra;
   }

   @Nullable
   public final Key getWolfArmor() {
      return this.wolfArmor;
   }

   @Nullable
   public final Key getLlamaArmor() {
      return this.llamaArmor;
   }

   @Nullable
   public final Key getHorseArmor() {
      return this.horseArmor;
   }

   @Nullable
   public final Key getCamelSaddle() {
      return this.camelSaddle;
   }

   @Nullable
   public final Key getDonkeySaddle() {
      return this.donkeySaddle;
   }

   @Nullable
   public final Key getHorseSaddle() {
      return this.horseSaddle;
   }

   @Nullable
   public final Key getMuleSaddle() {
      return this.muleSaddle;
   }

   @Nullable
   public final Key getPigSaddle() {
      return this.pigSaddle;
   }

   @Nullable
   public final Key getSkeletonHorseSaddle() {
      return this.skeletonHorseSaddle;
   }

   @Nullable
   public final Key getStriderSaddle() {
      return this.striderSaddle;
   }

   @Nullable
   public final Key getZombieHorseSaddle() {
      return this.zombieHorseSaddle;
   }

   @Nullable
   public final Key getHarness() {
      return this.harness;
   }

   @Nullable
   public final Key getNautilusSaddle() {
      return this.nautilusSaddle;
   }

   @Nullable
   public final Key getCamelHuskSaddle() {
      return this.camelHuskSaddle;
   }

   @Nullable
   public final Key getHumanoidBaby() {
      return this.humanoidBaby;
   }

   public CustomArmorTextures(@NotNull String armorPrefix) {
      this(Key.key(armorPrefix + "_armor_layer_1.png"), Key.key(armorPrefix + "_armor_layer_2.png"), Key.key(armorPrefix + "_elytra.png"), Key.key(armorPrefix + "_wolf_armor.png"), Key.key(armorPrefix + "_llama_armor.png"), Key.key(armorPrefix + "_horse_armor.png"), Key.key(armorPrefix + "_camel_saddle.png"), Key.key(armorPrefix + "_donkey_saddle.png"), Key.key(armorPrefix + "_horse_saddle.png"), Key.key(armorPrefix + "_mule_saddle.png"), Key.key(armorPrefix + "_pig_saddle.png"), Key.key(armorPrefix + "_skeleton_horse_saddle.png"), Key.key(armorPrefix + "_strider_saddle.png"), Key.key(armorPrefix + "_zombie_horse_saddle.png"), Key.key(armorPrefix + "_harness.png"), Key.key(armorPrefix + "_nautilus_saddle.png"), Key.key(armorPrefix + "_camel_husk_saddle.png"), Key.key(armorPrefix + "_armor_layer_baby.png"));
   }

   public CustomArmorTextures(@NotNull ConfigurationSection section) {
      Key key = NexoYamlKt.key(section, "layer1", "");
      key = key != null ? KeyUtils.INSTANCE.appendSuffix(key, ".png") : null;
      Key key2 = NexoYamlKt.key(section, "layer2", "");
      key2 = key2 != null ? KeyUtils.INSTANCE.appendSuffix(key2, ".png") : null;
      Key key3 = NexoYamlKt.key(section, "elytra", "");
      key3 = key3 != null ? KeyUtils.INSTANCE.appendSuffix(key3, ".png") : null;
      Key key4 = NexoYamlKt.key(section, "wolf_armor", "");
      key4 = key4 != null ? KeyUtils.INSTANCE.appendSuffix(key4, ".png") : null;
      Key key5 = NexoYamlKt.key(section, "llama_armor", "");
      key5 = key5 != null ? KeyUtils.INSTANCE.appendSuffix(key5, ".png") : null;
      Key key6 = NexoYamlKt.key(section, "horse_armor", "");
      key6 = key6 != null ? KeyUtils.INSTANCE.appendSuffix(key6, ".png") : null;
      Key key7 = NexoYamlKt.key(section, "camel_saddle", "");
      key7 = key7 != null ? KeyUtils.INSTANCE.appendSuffix(key7, ".png") : null;
      Key key8 = NexoYamlKt.key(section, "donkey_saddle", "");
      key8 = key8 != null ? KeyUtils.INSTANCE.appendSuffix(key8, ".png") : null;
      Key key9 = NexoYamlKt.key(section, "horse_saddle", "");
      key9 = key9 != null ? KeyUtils.INSTANCE.appendSuffix(key9, ".png") : null;
      Key key10 = NexoYamlKt.key(section, "mule_saddle", "");
      key10 = key10 != null ? KeyUtils.INSTANCE.appendSuffix(key10, ".png") : null;
      Key key11 = NexoYamlKt.key(section, "pig_saddle", "");
      key11 = key11 != null ? KeyUtils.INSTANCE.appendSuffix(key11, ".png") : null;
      Key key12 = NexoYamlKt.key(section, "skeleton_horse_saddle", "");
      key12 = key12 != null ? KeyUtils.INSTANCE.appendSuffix(key12, ".png") : null;
      Key key13 = NexoYamlKt.key(section, "strider_saddle", "");
      key13 = key13 != null ? KeyUtils.INSTANCE.appendSuffix(key13, ".png") : null;
      Key key14 = NexoYamlKt.key(section, "zombie_horse_saddle", "");
      key14 = key14 != null ? KeyUtils.INSTANCE.appendSuffix(key14, ".png") : null;
      Key key15 = NexoYamlKt.key(section, "harness", "");
      key15 = key15 != null ? KeyUtils.INSTANCE.appendSuffix(key15, ".png") : null;
      Key key16 = NexoYamlKt.key(section, "nautilus_saddle", "");
      key16 = key16 != null ? KeyUtils.INSTANCE.appendSuffix(key16, ".png") : null;
      Key key17 = NexoYamlKt.key(section, "camel_husk_saddle", "");
      key17 = key17 != null ? KeyUtils.INSTANCE.appendSuffix(key17, ".png") : null;
      Key key18 = NexoYamlKt.key(section, "humanoid_baby", "");
      this(key, key2, key3, key4, key5, key6, key7, key8, key9, key10, key11, key12, key13, key14, key15, key16, key17, key18 != null ? KeyUtils.INSTANCE.appendSuffix(key18, ".png") : null);
   }

   @Nullable
   public final Key fromItem(@NotNull ItemBuilder item, @NotNull String itemId) {
      Collection collection;
      block: {
         Equippable equippable = item.getEquippable();
         if (equippable != null) {
            RegistryKeySet registryKeySet = equippable.allowedEntities();
            if (registryKeySet != null) {
               RegistryKey registryKey = RegistryKey.ENTITY_TYPE;
               collection = registryKeySet.resolve(RegistryUtilsKt.toRegistry(registryKey));
               if (collection != null) {
                  break block;
               }
            }
         }

         collection = (Collection)CollectionsKt.emptyList();
      }

      Collection allowedEntities = collection;
      Equippable equippable2 = item.getEquippable();
      EquipmentSlot slot = equippable2 != null ? equippable2.slot() : null;
      Key key;
      if (!StringsKt.endsWith(itemId, "_elytra", false) && item.getType() != Material.ELYTRA && !item.isGlider().isTrue()) {
         if (slot == EquipmentSlot.BODY) {
            if (item.getType() != Material.WOLF_ARMOR && !StringsKt.endsWith(itemId, "_wolf_armor", false) && !allowedEntities.contains(EntityType.WOLF)) {
               if (!StringsKt.endsWith(itemId, "_llama_armor", false) && !allowedEntities.contains(EntityType.LLAMA)) {
                  if (!StringsKt.endsWith(itemId, "_horse_armor", false) && !allowedEntities.contains(EntityType.HORSE)) {
                     if (!StringsKt.endsWith(itemId, "_harness", false)) {
                        Iterable iterable = (Iterable)allowedEntities;
                        boolean flag;
                        if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
                           flag = false;
                        } else {
                           Iterator iterator = iterable.iterator();

                           while(true) {
                              if (!iterator.hasNext()) {
                                 flag = false;
                                 break;
                              }

                              Object element = iterator.next();
                              EntityType it = (EntityType)element;
                              int value = 0;
                              if (Intrinsics.areEqual(it.name(), "HAPPY_GHAST")) {
                                 flag = true;
                                 break;
                              }
                           }
                        }

                        if (!flag) {
                           key = null;
                           return key;
                        }
                     }

                     key = this.harness;
                  } else {
                     key = this.horseArmor;
                  }
               } else {
                  key = this.llamaArmor;
               }
            } else {
               key = this.wolfArmor;
            }
         } else {
            key = item.getType() == Material.SADDLE ? (!StringsKt.endsWith(itemId, "_camel_saddle", false) && !allowedEntities.contains(EntityType.CAMEL) ? (!StringsKt.endsWith(itemId, "_donkey_saddle", false) && !allowedEntities.contains(EntityType.DONKEY) ? (!StringsKt.endsWith(itemId, "_horse_saddle", false) && !allowedEntities.contains(EntityType.HORSE) ? (!StringsKt.endsWith(itemId, "_mule_saddle", false) && !allowedEntities.contains(EntityType.MULE) ? (!StringsKt.endsWith(itemId, "_pig_saddle", false) && !allowedEntities.contains(EntityType.PIG) ? (!StringsKt.endsWith(itemId, "_skeleton_horse_saddle", false) && !allowedEntities.contains(EntityType.SKELETON_HORSE) ? (!StringsKt.endsWith(itemId, "_strider_saddle", false) && !allowedEntities.contains(EntityType.STRIDER) ? (!StringsKt.endsWith(itemId, "_zombie_horse_saddle", false) && !allowedEntities.contains(EntityType.ZOMBIE_HORSE) ? (!StringsKt.endsWith(itemId, "_nautilus_saddle", false) && !allowedEntities.contains(EntityType.NAUTILUS) ? (!StringsKt.endsWith(itemId, "_camel_husk_saddle", false) && !allowedEntities.contains(EntityType.CAMEL_HUSK) ? null : this.camelHuskSaddle) : this.nautilusSaddle) : this.zombieHorseSaddle) : this.striderSaddle) : this.skeletonHorseSaddle) : this.pigSaddle) : this.muleSaddle) : this.horseSaddle) : this.donkeySaddle) : this.camelSaddle) : (slot != EquipmentSlot.HEAD && slot != EquipmentSlot.CHEST && slot != EquipmentSlot.FEET ? (slot == EquipmentSlot.LEGS ? this.layer2 : null) : this.layer1);
         }
      } else {
         key = this.elytra;
      }

      return key;
   }

   @Nullable
   public final Key component1() {
      return this.layer1;
   }

   @Nullable
   public final Key component2() {
      return this.layer2;
   }

   @Nullable
   public final Key component3() {
      return this.elytra;
   }

   @Nullable
   public final Key component4() {
      return this.wolfArmor;
   }

   @Nullable
   public final Key component5() {
      return this.llamaArmor;
   }

   @Nullable
   public final Key component6() {
      return this.horseArmor;
   }

   @Nullable
   public final Key component7() {
      return this.camelSaddle;
   }

   @Nullable
   public final Key component8() {
      return this.donkeySaddle;
   }

   @Nullable
   public final Key component9() {
      return this.horseSaddle;
   }

   @Nullable
   public final Key component10() {
      return this.muleSaddle;
   }

   @Nullable
   public final Key component11() {
      return this.pigSaddle;
   }

   @Nullable
   public final Key component12() {
      return this.skeletonHorseSaddle;
   }

   @Nullable
   public final Key component13() {
      return this.striderSaddle;
   }

   @Nullable
   public final Key component14() {
      return this.zombieHorseSaddle;
   }

   @Nullable
   public final Key component15() {
      return this.harness;
   }

   @Nullable
   public final Key component16() {
      return this.nautilusSaddle;
   }

   @Nullable
   public final Key component17() {
      return this.camelHuskSaddle;
   }

   @Nullable
   public final Key component18() {
      return this.humanoidBaby;
   }

   @NotNull
   public final CustomArmorTextures copy(@Nullable Key layer1, @Nullable Key layer2, @Nullable Key elytra, @Nullable Key wolfArmor, @Nullable Key llamaArmor, @Nullable Key horseArmor, @Nullable Key camelSaddle, @Nullable Key donkeySaddle, @Nullable Key horseSaddle, @Nullable Key muleSaddle, @Nullable Key pigSaddle, @Nullable Key skeletonHorseSaddle, @Nullable Key striderSaddle, @Nullable Key zombieHorseSaddle, @Nullable Key harness, @Nullable Key nautilusSaddle, @Nullable Key camelHuskSaddle, @Nullable Key humanoidBaby) {
      return new CustomArmorTextures(layer1, layer2, elytra, wolfArmor, llamaArmor, horseArmor, camelSaddle, donkeySaddle, horseSaddle, muleSaddle, pigSaddle, skeletonHorseSaddle, striderSaddle, zombieHorseSaddle, harness, nautilusSaddle, camelHuskSaddle, humanoidBaby);
   }

   @NotNull
   public String toString() {
      return "CustomArmorTextures(layer1=" + this.layer1 + ", layer2=" + this.layer2 + ", elytra=" + this.elytra + ", wolfArmor=" + this.wolfArmor + ", llamaArmor=" + this.llamaArmor + ", horseArmor=" + this.horseArmor + ", camelSaddle=" + this.camelSaddle + ", donkeySaddle=" + this.donkeySaddle + ", horseSaddle=" + this.horseSaddle + ", muleSaddle=" + this.muleSaddle + ", pigSaddle=" + this.pigSaddle + ", skeletonHorseSaddle=" + this.skeletonHorseSaddle + ", striderSaddle=" + this.striderSaddle + ", zombieHorseSaddle=" + this.zombieHorseSaddle + ", harness=" + this.harness + ", nautilusSaddle=" + this.nautilusSaddle + ", camelHuskSaddle=" + this.camelHuskSaddle + ", humanoidBaby=" + this.humanoidBaby + ")";
   }

   public int hashCode() {
      int result = this.layer1 == null ? 0 : this.layer1.hashCode();
      result = result * 31 + (this.layer2 == null ? 0 : this.layer2.hashCode());
      result = result * 31 + (this.elytra == null ? 0 : this.elytra.hashCode());
      result = result * 31 + (this.wolfArmor == null ? 0 : this.wolfArmor.hashCode());
      result = result * 31 + (this.llamaArmor == null ? 0 : this.llamaArmor.hashCode());
      result = result * 31 + (this.horseArmor == null ? 0 : this.horseArmor.hashCode());
      result = result * 31 + (this.camelSaddle == null ? 0 : this.camelSaddle.hashCode());
      result = result * 31 + (this.donkeySaddle == null ? 0 : this.donkeySaddle.hashCode());
      result = result * 31 + (this.horseSaddle == null ? 0 : this.horseSaddle.hashCode());
      result = result * 31 + (this.muleSaddle == null ? 0 : this.muleSaddle.hashCode());
      result = result * 31 + (this.pigSaddle == null ? 0 : this.pigSaddle.hashCode());
      result = result * 31 + (this.skeletonHorseSaddle == null ? 0 : this.skeletonHorseSaddle.hashCode());
      result = result * 31 + (this.striderSaddle == null ? 0 : this.striderSaddle.hashCode());
      result = result * 31 + (this.zombieHorseSaddle == null ? 0 : this.zombieHorseSaddle.hashCode());
      result = result * 31 + (this.harness == null ? 0 : this.harness.hashCode());
      result = result * 31 + (this.nautilusSaddle == null ? 0 : this.nautilusSaddle.hashCode());
      result = result * 31 + (this.camelHuskSaddle == null ? 0 : this.camelHuskSaddle.hashCode());
      result = result * 31 + (this.humanoidBaby == null ? 0 : this.humanoidBaby.hashCode());
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof CustomArmorTextures)) {
         return false;
      } else {
         CustomArmorTextures customArmorTextures = (CustomArmorTextures)other;
         if (!Intrinsics.areEqual(this.layer1, customArmorTextures.layer1)) {
            return false;
         } else if (!Intrinsics.areEqual(this.layer2, customArmorTextures.layer2)) {
            return false;
         } else if (!Intrinsics.areEqual(this.elytra, customArmorTextures.elytra)) {
            return false;
         } else if (!Intrinsics.areEqual(this.wolfArmor, customArmorTextures.wolfArmor)) {
            return false;
         } else if (!Intrinsics.areEqual(this.llamaArmor, customArmorTextures.llamaArmor)) {
            return false;
         } else if (!Intrinsics.areEqual(this.horseArmor, customArmorTextures.horseArmor)) {
            return false;
         } else if (!Intrinsics.areEqual(this.camelSaddle, customArmorTextures.camelSaddle)) {
            return false;
         } else if (!Intrinsics.areEqual(this.donkeySaddle, customArmorTextures.donkeySaddle)) {
            return false;
         } else if (!Intrinsics.areEqual(this.horseSaddle, customArmorTextures.horseSaddle)) {
            return false;
         } else if (!Intrinsics.areEqual(this.muleSaddle, customArmorTextures.muleSaddle)) {
            return false;
         } else if (!Intrinsics.areEqual(this.pigSaddle, customArmorTextures.pigSaddle)) {
            return false;
         } else if (!Intrinsics.areEqual(this.skeletonHorseSaddle, customArmorTextures.skeletonHorseSaddle)) {
            return false;
         } else if (!Intrinsics.areEqual(this.striderSaddle, customArmorTextures.striderSaddle)) {
            return false;
         } else if (!Intrinsics.areEqual(this.zombieHorseSaddle, customArmorTextures.zombieHorseSaddle)) {
            return false;
         } else if (!Intrinsics.areEqual(this.harness, customArmorTextures.harness)) {
            return false;
         } else if (!Intrinsics.areEqual(this.nautilusSaddle, customArmorTextures.nautilusSaddle)) {
            return false;
         } else if (!Intrinsics.areEqual(this.camelHuskSaddle, customArmorTextures.camelHuskSaddle)) {
            return false;
         } else {
            return Intrinsics.areEqual(this.humanoidBaby, customArmorTextures.humanoidBaby);
         }
      }
   }
}
