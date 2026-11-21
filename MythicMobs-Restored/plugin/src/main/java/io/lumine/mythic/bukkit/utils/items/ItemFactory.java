package io.lumine.mythic.bukkit.utils.items;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.lumine.mythic.bukkit.utils.annotation.NonnullByDefault;
import io.lumine.mythic.bukkit.utils.config.LineConfig;
import io.lumine.mythic.bukkit.utils.gson.GsonProvider;
import io.lumine.mythic.bukkit.utils.items.nbt.jnbt.CompoundTagBuilder;
import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.bukkit.utils.menu.PaperComponentHelper;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.numbers.RandomInt;
import io.lumine.mythic.bukkit.utils.serialize.Base64Util;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.bukkit.utils.uuid.DashedUUIDs;
import io.lumine.mythic.bukkit.utils.version.MinecraftVersions;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.bukkit.utils.version.wrappers.VersionWrapper;
import io.papermc.paper.datacomponent.DataComponentTypes;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.profile.PlayerTextures;

@NonnullByDefault
public class ItemFactory {
   private ItemStack itemStack;
   private boolean clone = true;
   private boolean batching = false;
   private boolean batchMetaResolved = false;
   private ItemMeta batchMeta;

   public static ItemFactory of(Material material) {
      return new ItemFactory(new ItemStack(material));
   }

   public static ItemFactory of(ItemStack itemStack) {
      return new ItemFactory(itemStack);
   }

   public static ItemFactory of(String line) {
      return of(LineConfig.of(line));
   }

   public static ItemFactory of(LineConfig lc) {
      Material material = Material.STONE;
      String mat = lc.getKey();
      if (mat.contains(" ")) {
         mat = mat.split(" ")[0];
      }

      try {
         material = Material.valueOf(mat.toUpperCase());
      } catch (Exception e) {
         Log.info("Invalid item material {0} on line {1}", mat, lc.getLine());
      }

      ItemFactory factory = of(material);
      String display = lc.getString(new String[]{"name", "display", "n", "d"}, null);
      int data = lc.getInteger(new String[]{"data"}, 0);
      int model = lc.getInteger(new String[]{"model"}, 0);
      int amount = lc.getInteger(new String[]{"amount", "a"}, 1);
      String lore = lc.getString(new String[]{"lore", "l"}, null);
      String enchantments = lc.getString(new String[]{"enchantments", "enchants", "ench", "e"}, null);
      String potionEffects = lc.getString(new String[]{"potioneffects", "peffects", "potion", "pe"}, null);
      String color = lc.getString(new String[]{"color", "c", "potioncolor", "pcolor", "pc"}, null);
      String skullOwner = lc.getString(new String[]{"skullowner"}, null);
      String skullTexture = lc.getString(new String[]{"skulltexture"}, null);
      if (display != null) {
         if (display.startsWith("\"")) {
            display = display.substring(1, display.length() - 1);
         }

         display = LineConfig.parseSpecialChars(display);
         factory.name(display);
      }

      if (data > 0) {
         factory.data((byte)data);
      }

      if (model > 0) {
         factory.model(model);
      }

      if (amount != 1) {
         factory.amount(amount);
      }

      if (lore != null) {
         try {
            List<String> loreList = new ArrayList<>();

            for (String s : lore.split(",")) {
               String lores = s.substring(1, s.length() - 1);
               lores = LineConfig.parseSpecialChars(lores);
               loreList.add(lores);
            }

            factory.lore(loreList);
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }

      if (enchantments != null) {
         try {
            for (String s : enchantments.split(",")) {
               try {
                  String[] split = s.split(":");
                  String enchantName = split[0];
                  Enchantment ench;
                  if (enchantName.equalsIgnoreCase("random")) {
                     ench = Enchantment.values()[Numbers.randomInt(Enchantment.values().length)];
                  } else {
                     ench = Enchantment.getByName(enchantName);
                  }

                  int level = new RandomInt(split[1]).get();
                  factory.enchant(ench, level);
               } catch (Exception ex2) {
                  Log.error("Badly configured enchantment");
                  ex2.printStackTrace();
               }
            }
         } catch (Exception ex3) {
            ex3.printStackTrace();
         }
      }

      if (potionEffects != null) {
         try {
            for (String s : potionEffects.split(",")) {
               try {
                  String[] split = s.split(":");
                  PotionEffectType type = PotionEffectType.getByName(split[0].toUpperCase());
                  int duration = Integer.parseInt(split[1]);
                  int amplifier = 0;
                  if (split.length > 2) {
                     amplifier = Integer.parseInt(split[2]);
                  }

                  factory.potionEffect(type, duration, amplifier);
               } catch (Exception ex4) {
                  Log.error("Badly configured potion effect " + s);
                  ex4.printStackTrace();
               }
            }
         } catch (Exception ex5) {
            ex5.printStackTrace();
         }
      }

      if (color != null) {
         factory.color(color);
      }

      if (skullOwner != null) {
         factory.skullOwner(skullOwner);
      }

      return factory;
   }

   protected ItemFactory() {
      this.itemStack = new ItemStack(Material.STONE);
   }

   protected ItemFactory(ItemStack itemStack) {
      this.itemStack = Objects.requireNonNull(itemStack, "itemStack");
   }

   public ItemFactory transform(Consumer<ItemStack> is) {
      is.accept(this.itemStack);
      return this;
   }

   public ItemFactory transformMeta(Consumer<ItemMeta> meta) {
      if (this.batching) {
         if (!this.batchMetaResolved) {
            this.batchMeta = this.itemStack.getItemMeta();
            this.batchMetaResolved = true;
         }

         if (this.batchMeta != null) {
            meta.accept(this.batchMeta);
         }

         return this;
      } else {
         ItemMeta m = this.itemStack.getItemMeta();
         if (m != null) {
            meta.accept(m);
            this.itemStack.setItemMeta(m);
         }

         return this;
      }
   }

   public ItemFactory beginBatch() {
      this.batching = true;
      this.batchMetaResolved = false;
      this.batchMeta = null;
      return this;
   }

   public ItemFactory endBatch() {
      if (this.batching) {
         if (this.batchMetaResolved && this.batchMeta != null) {
            this.itemStack.setItemMeta(this.batchMeta);
         }

         this.batching = false;
         this.batchMetaResolved = false;
         this.batchMeta = null;
      }

      return this;
   }

   public ItemFactory name(String name) {
      return ServerVersion.isPaper() && ServerVersion.isAfterOrEq(MinecraftVersions.v1_16)
         ? this.transformMeta(meta -> meta.displayName(Text.parse(name)))
         : this.transformMeta(meta -> meta.setDisplayName(Text.colorizeLegacy(name)));
   }

   public ItemFactory type(Material material) {
      return this.transform(itemStack -> itemStack.setType(material));
   }

   public ItemFactory lore(String line) {
      return ServerVersion.isPaper() && ServerVersion.isAfterOrEq(MinecraftVersions.v1_16) ? this.transformMeta(meta -> {
         List<Component> lore = (List<Component>)(meta.getLore() == null ? new ArrayList<>() : meta.lore());
         lore.add(Text.parse(line));
         meta.lore(lore);
      }) : this.transformMeta(meta -> {
         List<String> lore = (List<String>)(meta.getLore() == null ? new ArrayList<>() : meta.getLore());
         lore.add(Text.colorize(line));
         meta.setLore(lore);
      });
   }

   public ItemFactory lore(String... lines) {
      return ServerVersion.isPaper() && ServerVersion.isAfterOrEq(MinecraftVersions.v1_16) ? this.transformMeta(meta -> {
         List<Component> lore = (List<Component>)(meta.getLore() == null ? new ArrayList<>() : meta.lore());

         for (String line : lines) {
            lore.add(Text.parse(line));
         }

         meta.lore(lore);
      }) : this.transformMeta(meta -> {
         List<String> lore = (List<String>)(meta.getLore() == null ? new ArrayList<>() : meta.getLore());

         for (String line : lines) {
            lore.add(Text.colorize(line));
         }

         meta.setLore(lore);
      });
   }

   public ItemFactory lore(Iterable<String> lines) {
      return ServerVersion.isPaper() && ServerVersion.isAfterOrEq(MinecraftVersions.v1_16) ? this.transformMeta(meta -> {
         List<Component> lore = (List<Component>)(meta.getLore() == null ? new ArrayList<>() : meta.lore());

         for (String line : lines) {
            lore.add(Text.parse(line));
         }

         meta.lore(lore);
      }) : this.transformMeta(meta -> {
         List<String> lore = (List<String>)(meta.getLore() == null ? new ArrayList<>() : meta.getLore());

         for (String line : lines) {
            lore.add(Text.colorize(line));
         }

         meta.setLore(lore);
      });
   }

   public ItemFactory lore(Function<List<String>, Iterable<String>> linesFunc) {
      Iterable<String> lines = linesFunc.apply(Lists.newArrayList());
      return ServerVersion.isPaper() && ServerVersion.isAfterOrEq(MinecraftVersions.v1_16) ? this.transformMeta(meta -> {
         List<Component> lore = (List<Component>)(meta.getLore() == null ? new ArrayList<>() : meta.lore());

         for (String line : lines) {
            lore.add(Text.parse(line));
         }

         meta.lore(lore);
      }) : this.transformMeta(meta -> {
         List<String> lore = (List<String>)(meta.getLore() == null ? new ArrayList<>() : meta.getLore());

         for (String line : lines) {
            lore.add(Text.colorize(line));
         }

         meta.setLore(lore);
      });
   }

   public ItemFactory clearLore() {
      return this.transformMeta(meta -> meta.setLore(new ArrayList()));
   }

   public ItemFactory prependLore(String line) {
      return ServerVersion.isPaper() && ServerVersion.isAfterOrEq(MinecraftVersions.v1_16) ? this.transformMeta(meta -> {
         List<Component> lore = (List<Component>)(meta.getLore() == null ? new ArrayList<>() : meta.lore());
         lore.add(0, Text.parse(line));
         meta.lore(lore);
      }) : this.transformMeta(meta -> {
         List<String> lore = (List<String>)(meta.getLore() == null ? new ArrayList<>() : meta.getLore());
         lore.add(0, Text.colorize(line));
         meta.setLore(lore);
      });
   }

   public ItemFactory prependLore(String... lines) {
      return ServerVersion.isPaper() && ServerVersion.isAfterOrEq(MinecraftVersions.v1_16) ? this.transformMeta(meta -> {
         List<Component> lore = (List<Component>)(meta.getLore() == null ? new ArrayList<>() : meta.lore());
         List<Component> extraLore = new ArrayList<>();

         for (String line : lines) {
            extraLore.add(Text.parse(line));
         }

         extraLore.addAll(lore);
         meta.lore(extraLore);
      }) : this.transformMeta(meta -> {
         List<String> lore = (List<String>)(meta.getLore() == null ? new ArrayList<>() : meta.getLore());
         List<String> extraLore = new ArrayList<>();

         for (String line : lines) {
            extraLore.add(Text.colorize(line));
         }

         extraLore.addAll(lore);
         meta.setLore(extraLore);
      });
   }

   public ItemFactory prependLore(Iterable<String> lines) {
      return ServerVersion.isPaper() && ServerVersion.isAfterOrEq(MinecraftVersions.v1_16) ? this.transformMeta(meta -> {
         List<Component> lore = (List<Component>)(meta.getLore() == null ? new ArrayList<>() : meta.lore());
         List<Component> extraLore = new ArrayList<>();

         for (String line : lines) {
            extraLore.add(Text.parse(line));
         }

         extraLore.addAll(lore);
         meta.lore(extraLore);
      }) : this.transformMeta(meta -> {
         List<String> lore = (List<String>)(meta.getLore() == null ? new ArrayList<>() : meta.getLore());
         List<String> extraLore = new ArrayList<>();

         for (String line : lines) {
            extraLore.add(Text.colorize(line));
         }

         extraLore.addAll(lore);
         meta.setLore(extraLore);
      });
   }

   public ItemFactory prependLore(Function<List<String>, Iterable<String>> linesFunc) {
      return this.prependLore(linesFunc.apply(Lists.newArrayList()));
   }

   public ItemFactory setLore(String line) {
      return ServerVersion.isPaper() && ServerVersion.isAfterOrEq(MinecraftVersions.v1_16) ? this.transformMeta(meta -> {
         List<Component> lore = new ArrayList<>();
         lore.add(Text.parse(line));
         meta.lore(lore);
      }) : this.transformMeta(meta -> {
         List<String> lore = new ArrayList<>();
         lore.add(Text.colorize(line));
         meta.setLore(lore);
      });
   }

   public ItemFactory setLore(String... lines) {
      return ServerVersion.isPaper() && ServerVersion.isAfterOrEq(MinecraftVersions.v1_16) ? this.transformMeta(meta -> {
         List<Component> lore = new ArrayList<>();

         for (String line : lines) {
            lore.add(Text.parse(line));
         }

         meta.lore(lore);
      }) : this.transformMeta(meta -> {
         List<String> lore = new ArrayList<>();

         for (String line : lines) {
            lore.add(Text.colorize(line));
         }

         meta.setLore(lore);
      });
   }

   public ItemFactory setLore(Iterable<String> lines) {
      return ServerVersion.isPaper() && ServerVersion.isAfterOrEq(MinecraftVersions.v1_16) ? this.transformMeta(meta -> {
         List<Component> lore = new ArrayList<>();

         for (String line : lines) {
            lore.add(Text.parse(line));
         }

         meta.lore(lore);
      }) : this.transformMeta(meta -> {
         List<String> lore = new ArrayList<>();

         for (String line : lines) {
            lore.add(Text.colorize(line));
         }

         meta.setLore(lore);
      });
   }

   public ItemFactory setLore(Function<List<String>, Iterable<String>> linesFunc) {
      Iterable<String> lines = linesFunc.apply(Lists.newArrayList());
      return ServerVersion.isPaper() && ServerVersion.isAfterOrEq(MinecraftVersions.v1_16) ? this.transformMeta(meta -> {
         List<Component> lore = new ArrayList<>();

         for (String line : lines) {
            lore.add(Text.parse(line));
         }

         meta.lore(lore);
      }) : this.transformMeta(meta -> {
         List<String> lore = new ArrayList<>();

         for (String line : lines) {
            lore.add(Text.colorize(line));
         }

         meta.setLore(lore);
      });
   }

   public ItemFactory durability(int durability) {
      return ServerVersion.isBefore(MinecraftVersions.v1_15)
         ? this.transform(itemStack -> itemStack.setDurability((short)durability))
         : this.transformMeta(meta -> {
            if (meta instanceof Damageable) {
               ((Damageable)meta).setDamage(durability);
            }
         });
   }

   public ItemFactory data(int data) {
      return this.durability(data);
   }

   public ItemFactory amount(int amount) {
      return this.transform(itemStack -> {
         if (amount > itemStack.getMaxStackSize()) {
            itemStack.setData(DataComponentTypes.MAX_STACK_SIZE, 99);
         }

         itemStack.setAmount(amount);
      });
   }

   @Deprecated
   public ItemFactory model(int model) {
      return this.transformMeta(meta -> meta.setCustomModelData(model));
   }

   public ItemFactory itemModel(String model) {
      if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_3) && model != null) {
         PaperComponentHelper.setItemModelComponent(this.itemStack, model);
      }

      return this;
   }

   public ItemFactory itemModel(NamespacedKey model) {
      if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_3) && model != null) {
         PaperComponentHelper.setItemModelComponent(this.itemStack, model);
      }

      return this;
   }

   public ItemFactory tooltipStyle(NamespacedKey style) {
      if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_3) && style != null) {
         PaperComponentHelper.setTooltipStyleComponent(this.itemStack, style);
      }

      return this;
   }

   public ItemFactory customModelData(int model) {
      return this.transformMeta(meta -> meta.setCustomModelData(model));
   }

   public ItemFactory enchant(Enchantment enchantment, int level) {
      return this.transform(itemStack -> {
         if (itemStack.getType() != Material.ENCHANTED_BOOK) {
            itemStack.addUnsafeEnchantment(enchantment, level);
         } else {
            EnchantmentStorageMeta esm = (EnchantmentStorageMeta)itemStack.getItemMeta();
            esm.addStoredEnchant(enchantment, level, true);
            itemStack.setItemMeta(esm);
         }
      });
   }

   public ItemFactory enchant(Enchantment enchantment) {
      return this.enchant(enchantment, 1);
   }

   public ItemFactory clearEnchantments() {
      return this.transform(itemStack -> itemStack.getEnchantments().keySet().forEach(itemStack::removeEnchantment));
   }

   public ItemFactory glint(boolean enabled) {
      return this.transformMeta(meta -> meta.setEnchantmentGlintOverride(enabled));
   }

   public ItemFactory potionEffect(PotionEffectType type, int duration, int amplifier) {
      return this.transformMeta(meta -> {
         if (meta instanceof PotionMeta pMeta) {
            PotionEffect effect = new PotionEffect(type, duration, amplifier);
            pMeta.addCustomEffect(effect, true);
         }
      });
   }

   public ItemFactory potionEffect(PotionEffectType type, int duration) {
      return this.potionEffect(type, duration, 0);
   }

   public ItemFactory clearPotionEffects() {
      return this.transformMeta(meta -> {
         if (meta instanceof PotionMeta pMeta) {
            pMeta.clearCustomEffects();
         }
      });
   }

   public ItemFactory color(String colorCode) {
      return this.color(Chroma.of(colorCode));
   }

   public ItemFactory color(Chroma color) {
      return this.transformMeta(meta -> {
         if (meta instanceof PotionMeta pMeta) {
            pMeta.setColor(color.toBukkitColor());
         } else if (meta instanceof LeatherArmorMeta pMeta) {
            pMeta.setColor(color.toBukkitColor());
         }
      });
   }

   public ItemFactory flag(Collection<ItemFlag> flags) {
      return this.transformMeta(meta -> flags.forEach(flag -> meta.addItemFlags(new ItemFlag[]{flag})));
   }

   public ItemFactory unflag(Collection<ItemFlag> flags) {
      return this.transformMeta(meta -> flags.forEach(flag -> meta.removeItemFlags(new ItemFlag[]{flag})));
   }

   public ItemFactory flag(ItemFlag... flags) {
      return this.transformMeta(meta -> meta.addItemFlags(flags));
   }

   public ItemFactory unflag(ItemFlag... flags) {
      return this.transformMeta(meta -> meta.removeItemFlags(flags));
   }

   public ItemFactory hideAttributes() {
      return this.flag(ItemFlag.values());
   }

   public ItemFactory showAttributes() {
      return this.unflag(ItemFlag.values());
   }

   public ItemFactory color(Color color) {
      return this.transform(itemStack -> {
         Material type = itemStack.getType();
         if (type == Material.LEATHER_BOOTS || type == Material.LEATHER_CHESTPLATE || type == Material.LEATHER_HELMET || type == Material.LEATHER_LEGGINGS) {
            LeatherArmorMeta meta = (LeatherArmorMeta)itemStack.getItemMeta();
            meta.setColor(color);
            itemStack.setItemMeta(meta);
         }
      });
   }

   public ItemFactory breakable(boolean flag) {
      return this.transformMeta(meta -> meta.setUnbreakable(!flag));
   }

   public ItemFactory skullOwner(String owner) {
      return VersionWrapper.isSupported() ? this.nbt(tag -> tag.putString("SkullOwner", owner)) : this.transformMeta(meta -> {
         if (meta instanceof SkullMeta im) {
            im.setOwner(owner);
         }
      });
   }

   public ItemFactory skullTexture(String skinTexture) {
      try {
         String decoded = Base64Util.decodeString(skinTexture);
         if (decoded.isEmpty()) {
            return this;
         } else {
            GsonProvider.parser();
            JsonObject obj = JsonParser.parseString(decoded).getAsJsonObject();
            String skinUrl = obj.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
            return this.skullTexture(URI.create(skinUrl));
         }
      } catch (Throwable e) {
         e.printStackTrace();
         return this;
      }
   }

   public ItemFactory skullTexture(URI url) {
      return this.transformMeta(meta -> {
         if (meta instanceof SkullMeta skullMeta) {
            if (ServerVersion.isPaper()) {
               PlayerProfile profile = Bukkit.createProfileExact(DashedUUIDs.getUUIDFromString(url.toString()), null);

               try {
                  PlayerTextures textures = profile.getTextures();
                  textures.setSkin(url.toURL());
                  profile.setTextures(textures);
               } catch (Throwable e) {
                  e.printStackTrace();
               }

               skullMeta.setPlayerProfile(profile);
            } else {
               org.bukkit.profile.PlayerProfile profile = Bukkit.createPlayerProfile(DashedUUIDs.getUUIDFromString(url.toString()), null);

               try {
                  PlayerTextures textures = profile.getTextures();
                  textures.setSkin(url.toURL());
                  profile.setTextures(textures);
               } catch (Throwable ex) {
                  ex.printStackTrace();
               }

               skullMeta.setOwnerProfile(profile);
            }
         }
      });
   }

   public ItemFactory apply(Consumer<ItemFactory> consumer) {
      consumer.accept(this);
      return this;
   }

   public ItemFactory pdc(Consumer<PersistentDataContainer> consumer) {
      return this.transformMeta(meta -> consumer.accept(meta.getPersistentDataContainer()));
   }

   public ItemFactory nbt(Consumer<CompoundTagBuilder> consumer) {
      VersionWrapper version = VersionWrapper.get();
      if (version == null) {
         Log.error("ItemFactory NBT Modification not supported on this version");
         return this;
      } else {
         CompoundTagBuilder tag = version.getItemHandler().getCompoundTag(this.itemStack).createBuilder();
         consumer.accept(tag);
         this.itemStack = version.getItemHandler().setCompoundTag(this.itemStack, tag.build());
         return this;
      }
   }

   public ItemStack peek() {
      return this.itemStack;
   }

   public ItemStack build() {
      return this.itemStack.clone();
   }
}
