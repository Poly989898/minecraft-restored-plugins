package fr.phoenixdevt.mmoprofiles.bukkit.gui.lib;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

/** One configured GUI item: its slots, its look and what happens when it is clicked. */
public abstract class InventoryItem<T extends GeneratedInventory> {
   private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.builder().character('&').hexColors().build();
   private static final LegacyComponentSerializer SECTION = LegacyComponentSerializer.legacySection();
   private final List<Integer> slots;
   private final Material material;
   private final String name;
   private final List<String> lore;
   private final String texture;

   public InventoryItem(ConfigurationSection config) {
      this.slots = config == null ? List.of() : config.getIntegerList("slots");
      Material mat = config == null ? null : Material.matchMaterial(config.getString("item", "STONE"));
      this.material = mat == null ? Material.STONE : mat;
      this.name = config == null ? null : config.getString("name");
      this.lore = config == null ? List.of() : config.getStringList("lore");
      this.texture = config == null ? null : config.getString("texture");
   }

   public List<Integer> getSlots() {
      return this.slots;
   }

   public boolean hasDifferentDisplay() {
      return false;
   }

   public Placeholders getPlaceholders(T inv, int n) {
      return new Placeholders();
   }

   public ItemStack getDisplayedItem(T inv, int n) {
      Placeholders holders = this.getPlaceholders(inv, n);
      ItemStack stack = new ItemStack(this.material);
      ItemMeta meta = stack.getItemMeta();
      if (meta == null) {
         return stack;
      }

      if (this.name != null) {
         meta.displayName(colored(holders.apply(this.name)));
      }

      if (!this.lore.isEmpty()) {
         List<Component> lines = new ArrayList<>();
         for (String line : this.lore) {
            lines.add(colored(holders.apply(line)));
         }
         meta.lore(lines);
      }

      if (this.texture != null && meta instanceof SkullMeta skull) {
         PlayerProfile profile = Bukkit.createProfile(UUID.nameUUIDFromBytes(this.texture.getBytes()), null);
         profile.setProperty(new ProfileProperty("textures", this.texture));
         skull.setPlayerProfile(profile);
      }

      stack.setItemMeta(meta);
      return stack;
   }

   public void onClick(T inv, InventoryClickEvent event) {
   }

   /** Converts '&' colour codes (and '&#rrggbb') to a component without the default italics. */
   public static Component colored(String text) {
      return LEGACY.deserialize(text).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
   }

   public static String legacyTitle(String text) {
      return SECTION.serialize(LEGACY.deserialize(text));
   }
}
