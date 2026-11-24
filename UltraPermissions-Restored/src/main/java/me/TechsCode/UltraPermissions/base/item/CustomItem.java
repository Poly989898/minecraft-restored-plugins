package me.TechsCode.UltraPermissions.base.item;

import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.base.legacy.SkinTexture;
import me.TechsCode.UltraPermissions.base.legacy.utils.MinecraftVersion;
import me.TechsCode.UltraPermissions.base.visual.Text;
import org.apache.commons.codec.binary.Base64;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;

public class CustomItem implements Serializable {
   public static Gson gson = new Gson();
   private ItemStack is;

   public CustomItem(Material material) {
      this(new ItemStack(material));
   }

   public CustomItem(XMaterial xMaterial) {
      this(xMaterial.getAsItemStack().orElse(new ItemStack(Material.STONE)));
   }

   public CustomItem(XMaterial xMaterial, boolean flag) {
      this(xMaterial.getAsItemStack().orElse(new ItemStack(Material.STONE)), flag);
   }

   public CustomItem(ItemStack item) {
      this(item, true);
   }

   public CustomItem(ItemStack item, boolean flag) {
      Preconditions.checkArgument(item != null, "ItemStack cannot be null");
      this.is = item.clone();
      if (flag) {
         this.showAllAttributes(false);
      }
   }

   public CustomItem material(Material material) {
      this.is.setType(material);
      return this;
   }

   public CustomItem skull(String texture) {
      if (this.is.getItemMeta() != null && this.is.getItemMeta() instanceof SkullMeta) {
         SkullMeta itemMeta = (SkullMeta)this.is.getItemMeta();
         itemMeta.setOwner(texture);
         this.is.setItemMeta(itemMeta);
      }

      return this;
   }

   public CustomItem setSkullTexture(SkinTexture skinTexture) {
      if (skinTexture == null) {
         return this;
      }

      if (skinTexture.getUrl() != null && !skinTexture.getUrl().equals("null")) {
         if (this.is.hasItemMeta() && this.is.getItemMeta() instanceof SkullMeta) {
            SkullMeta itemMeta = (SkullMeta)this.is.getItemMeta();
            this.setTextures(itemMeta, skinTexture);
            this.is.setItemMeta(itemMeta);
         }

         return this;
      } else {
         return this;
      }
   }

   private void setTextures(SkullMeta skullMeta, SkinTexture skinTexture) {
      String json = String.format("{'textures':{'SKIN':{'url':'%s'}}}", skinTexture.getUrl());
      String encoded = new String(Base64.encodeBase64(json.getBytes()), StandardCharsets.UTF_8);

      try {
         Method declaredMethod = Bukkit.class.getDeclaredMethod("createPlayerProfile", UUID.class, String.class);
         declaredMethod.setAccessible(true);
         Class returnType = declaredMethod.getReturnType();
         Method setProfileMethod = skullMeta.getClass().getDeclaredMethod("setOwnerProfile", returnType);
         setProfileMethod.setAccessible(true);
         Method getTexturesMethod = returnType.getDeclaredMethod("getTextures");
         getTexturesMethod.setAccessible(true);
         Object obj = declaredMethod.invoke(null, UUID.randomUUID(), "skull");
         PlayerTextures playerTextures = (PlayerTextures)getTexturesMethod.invoke(obj);
         playerTextures.setSkin(new URL(skinTexture.getUrl()));
         setProfileMethod.invoke(skullMeta, obj);
      } catch (Throwable exception) {
         try {
            GameProfile gameProfile = new GameProfile(new UUID(encoded.substring(encoded.length() - 20).hashCode(), encoded.substring(encoded.length() - 10).hashCode()), "legacy");

            try {
               gameProfile.getProperties().put("textures", new Property("textures", encoded));
            } catch (Throwable propertyException) {
               return;
            }

            try {
               Method declaredMethod = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
               declaredMethod.setAccessible(true);
               declaredMethod.invoke(skullMeta, gameProfile);
               return;
            } catch (Throwable profileMethodException) {
               try {
                  Field declaredField = skullMeta.getClass().getDeclaredField("profile");
                  declaredField.setAccessible(true);
                  declaredField.set(skullMeta, gameProfile);
               } catch (Throwable profileFieldException) {
               }
            }
         } catch (Throwable legacyProfileException) {
         }
      }
   }

   public String getSkullOwner() {
      if (this.is.getItemMeta() != null && this.is.getItemMeta() instanceof SkullMeta) {
         SkullMeta itemMeta = (SkullMeta)this.is.getItemMeta();
         return itemMeta.getOwner();
      } else {
         return null;
      }
   }

   public CustomItem name() {
      return this.name("\u00a7r");
   }

   public CustomItem name(String name) {
      if (this.is.getItemMeta() != null) {
         ItemMeta itemMeta = this.is.getItemMeta();
         itemMeta.setDisplayName(name);
         this.is.setItemMeta(itemMeta);
      }

      return this;
   }

   public CustomItem amount(int amount) {
      this.is.setAmount(amount);
      return this;
   }

   public CustomItem lore(String... texts) {
      this.lore(Arrays.asList(texts));
      return this;
   }

   public CustomItem lore(List<String> texts) {
      if (this.is.getItemMeta() != null) {
         ItemMeta itemMeta = this.is.getItemMeta();
         itemMeta.setLore(texts);
         this.is.setItemMeta(itemMeta);
      }

      return this;
   }

   public CustomItem addEnchantment(Enchantment enchantment, int count) {
      this.is.addUnsafeEnchantment(enchantment, count);
      return this;
   }

   public CustomItem removeEnchantment(Enchantment enchantment) {
      this.is.removeEnchantment(enchantment);
      return this;
   }

   public CustomItem appendLoreBeginning(String... texts) {
      ArrayList list = new ArrayList();
      list.addAll(Arrays.asList(texts));
      list.addAll(this.getLore());
      this.lore(list);
      return this;
   }

   public CustomItem appendLore(List<String> texts) {
      return this.appendLore(texts.toArray(new String[0]));
   }

   public CustomItem appendLore(String... texts) {
      ArrayList list = new ArrayList<>(this.getLore());
      list.addAll(Arrays.asList(texts));
      this.lore(list);
      return this;
   }

   public CustomItem setDamage(int damage) {
      if (this.is.getItemMeta() == null) {
         return this;
      }

      if (XMaterial.isDamageable(this.is.getType()) && MinecraftVersion.getServersVersion().isAboveOrEqual(MinecraftVersion.V1_13_R1)) {
         Damageable itemMeta = (Damageable)this.is.getItemMeta();
         itemMeta.setDamage(damage);
         this.is.setItemMeta(itemMeta);
      } else {
         this.is.setDurability((short)damage);
      }

      return this;
   }

   public CustomItem addModelId(int modelId) {
      if (MinecraftVersion.getServersVersion().isAboveOrEqual(MinecraftVersion.V1_13_R1) && this.is.getItemMeta() != null) {
         ItemMeta itemMeta = this.is.getItemMeta();
         itemMeta.setCustomModelData(modelId);
         this.is.setItemMeta(itemMeta);
      }

      return this;
   }

   public int getModelId() {
      if (MinecraftVersion.getServersVersion().isAboveOrEqual(MinecraftVersion.V1_13_R1) && this.is.getItemMeta() != null) {
         ItemMeta itemMeta = this.is.getItemMeta();
         return itemMeta.getCustomModelData();
      } else {
         return 0;
      }
   }

   public XMaterial getMaterial() {
      return XMaterial.fromItem(this.is);
   }

   public Material getBukkitMaterial() {
      return this.is.getType();
   }

   public String getName() {
      return this.is.getItemMeta() != null
         ? (
            MinecraftVersion.getServersVersion().isAboveOrEqual(MinecraftVersion.V1_16_R3)
               ? Text.color(this.is.getItemMeta().getDisplayName())
               : this.is.getItemMeta().getDisplayName()
         )
         : "Not defined";
   }

   public int getAmount() {
      return this.is.getAmount();
   }

   public ArrayList<String> getLore() {
      return this.is.getItemMeta() != null && this.is.getItemMeta().hasLore() ? new ArrayList<>(this.is.getItemMeta().getLore()) : new ArrayList<>();
   }

   public Map<Enchantment, Integer> getEnchants() {
      return this.is.getEnchantments();
   }

   public CustomItem setPlaceholders(HashMap<String, String> map) {
      for (Entry entry : map.entrySet()) {
         String key = (String)entry.getKey();
         String text = (String)entry.getValue();
         this.replace(key, text);
      }

      return this;
   }

   public CustomItem replace(String search, String replacement) {
      if (this.getName() != null) {
         this.name(this.getName().replace(search, replacement));
      }

      if (this.getSkullOwner() != null) {
         this.skull(this.getSkullOwner().replace(search, replacement));
      }

      this.lore(this.getLore().stream().map(line -> line.replace(search, replacement)).collect(Collectors.toList()));
      return this;
   }

   public CustomItem showEnchantments(boolean show) {
      if (this.is.getItemMeta() != null) {
         ItemMeta itemMeta = this.is.getItemMeta();
         if (show) {
            itemMeta.removeItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
         } else {
            itemMeta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
         }

         this.is.setItemMeta(itemMeta);
      }

      return this;
   }

   public CustomItem showAllAttributes(boolean show) {
      if (this.is.getItemMeta() != null) {
         ItemMeta itemMeta = this.is.getItemMeta();
         if (show) {
            itemMeta.removeItemFlags(ItemFlag.values());
         } else {
            itemMeta.addItemFlags(ItemFlag.values());
         }

         this.is.setItemMeta(itemMeta);
      }

      return this;
   }

   public boolean equals(CustomItem customItem) {
      return this.serialize().equals(customItem.serialize());
   }

   public boolean isSimilar(CustomItem customItem, boolean flag, boolean matchName, boolean matchLore) {
      if (customItem == null) {
         return false;
      }

      if (flag && this.getBukkitMaterial() != customItem.getBukkitMaterial()) {
         return false;
      }

      if (matchName && this.getName() != null && customItem.getName() != null && !this.getName().equals(customItem.getName())) {
         return false;
      }

      if (matchLore) {
         if (this.getLore().size() != customItem.getLore().size()) {
            return false;
         }

         int index = 0;

         for (String text : this.getLore()) {
            if (!customItem.getLore().get(index).equals(text)) {
               return false;
            }

            index++;
         }
      }

      return true;
   }

   public JsonObject serialize() {
      JsonObject jsonObject = new JsonObject();

      try {
         jsonObject.addProperty("base64", ItemSerializer.itemTo64(this.is));
      } catch (IllegalStateException exception) {
         exception.printStackTrace();
      }

      return jsonObject;
   }

   public static CustomItem deserialize(JsonObject jsonObject) {
      if (jsonObject.has("base64")) {
         try {
            ItemStack item = ItemSerializer.itemFrom64(jsonObject.get("base64").getAsString());
            return new CustomItem(item, false);
         } catch (IOException exception) {
            exception.printStackTrace();
         }
      }

      if (jsonObject.toString().equals("{}")) {
         return new CustomItem(XMaterial.STONE);
      }

      Type type = (new TypeToken<Map<String, Object>>() {}).getType();
      Map map = null;

      try {
         map = (Map)gson.fromJson(jsonObject.getAsJsonObject("itemInfo").toString(), type);
      } catch (Exception exception) {
         exception.printStackTrace();
      }

      if (jsonObject.has("meta")) {
         Map<String, Object> metaMap = (Map<String, Object>)gson.fromJson(jsonObject.getAsJsonObject("meta").toString(), type);

         for (Entry<String, Object> entry : metaMap.entrySet()) {
            if (entry.getValue() instanceof Number) {
               entry.setValue(convertNumber(entry.getValue()));
            }
         }

         HashMap enchantMap = new HashMap();
         if (metaMap.containsKey("enchants")) {
            String text = metaMap.get("enchants").toString().replace("{", "").replace("}", "").replace("\"", "");

            for (String enchantEntry : text.split(",")) {
               String[] texts = enchantEntry.split("=");
               Enchantment enchantment = Enchantment.getByName(texts[0]);
               int count = Integer.parseInt(texts[1].replace(".0", ""));
               if (enchantment != null) {
                  enchantMap.put(enchantment, count);
               }
            }
         }

         metaMap.put("enchants", enchantMap);
         map.put("meta", ConfigurationSerialization.deserializeObject(metaMap, ConfigurationSerialization.getClassByAlias("ItemMeta")));
      }

      return new CustomItem(ItemStack.deserialize(map), false);
   }

   private static Number convertNumber(Object obj) {
      Number number = (Number)obj;
      if (number instanceof Long value && value == value.intValue()) {
         return value.intValue();
      } else if (number instanceof Float floatValue && floatValue == floatValue.intValue()) {
         return floatValue.intValue();
      } else {
         return number instanceof Double doubleValue && doubleValue == doubleValue.intValue() ? doubleValue.intValue() : number;
      }
   }

   public CustomItem clone() {
      return new CustomItem(this.is.clone(), false);
   }

   public ItemStack get() {
      return this.is.clone();
   }

   public void setBukkitStack(ItemStack item) {
      this.is = item;
   }
}
