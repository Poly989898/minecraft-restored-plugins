package net.Indyuce.mmoitems.comp.enchants;

import io.lumine.mythicenchants.MythicEnchants;
import io.lumine.mythicenchants.enchants.MythicEnchant;
import io.lumine.mythicenchants.util.LoreParser;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MythicEnchantsSupport implements EnchantPlugin<MythicEnchant> {
   private final MythicEnchants manager = (MythicEnchants)Bukkit.getPluginManager().getPlugin("MythicEnchants");

   @NotNull
   @Override
   public String getNamespace() {
      return "mythicenchants";
   }

   @Nullable
   public MythicEnchant transfer(@NotNull Enchantment var1) {
      return (MythicEnchant)this.manager.getEnchantManager().toMythicEnchantment(var1).orElse(null);
   }

   public void handleEnchant(@NotNull ItemStackBuilder var1, @NotNull MythicEnchant var2, int var3) {
      if (!var1.getMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS)) {
         var1.getLore().insert(0, LoreParser.formatEnchantment(var2, var3));
      }
   }
}
