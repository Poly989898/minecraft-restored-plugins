package io.lumine.mythic.core.mobs;

import io.lumine.mythic.api.mobs.EggManager;
import io.lumine.mythic.api.mobs.MobManager;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EggExecutor extends ReloadableModule<MythicBukkit> implements EggManager {
   public EggExecutor(MythicBukkit plugin, MobManager mobManager) {
      super(plugin);
   }

   public void load(MythicBukkit mythicBukkit) {
   }

   @Override
   public void unload() {
   }

   public boolean giveMythicEgg(MythicMob type, Player player, int amount) {
      BukkitItemStack egg = this.createMythicEgg(type);
      ItemStack bukkitEgg = egg.amount(amount).getItemStack();
      player.getInventory().addItem(new ItemStack[]{bukkitEgg});
      return true;
   }

   public BukkitItemStack createMythicEgg(MythicMob type) {
      return (BukkitItemStack)type.getEgg();
   }

   public MythicMob getMythicMobFromEgg(ItemStack eggItem) {
      CompoundTag eggTag = MythicBukkit.inst().getVolatileCodeHandler().getItemHandler().getNBTData(eggItem);
      if (eggTag.containsKey("MYTHIC_EGG")) {
         String mmType = eggTag.getString("MYTHIC_EGG");
         return MythicBukkit.inst().getMobManager().getMythicMob(mmType).orElse(null);
      } else {
         return null;
      }
   }

   @Deprecated
   public MythicMob getMythicMobFromEgg(String s) {
      for (MythicMob MM : MythicBukkit.inst().getMobManager().getMobTypes()) {
         String search = "" + ChatColor.DARK_GRAY + ChatColor.ITALIC + MM.getInternalName();
         if (search.equals(s)) {
            return MM;
         }
      }

      return null;
   }
}
