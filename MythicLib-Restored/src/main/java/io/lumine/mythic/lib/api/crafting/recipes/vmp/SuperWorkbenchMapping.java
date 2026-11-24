package io.lumine.mythic.lib.api.crafting.recipes.vmp;

import io.lumine.mythic.lib.api.crafting.ingredients.MythicRecipeInventory;
import io.lumine.mythic.lib.api.crafting.recipes.MythicRecipeStation;
import io.lumine.mythic.lib.api.util.ItemFactory;
import io.lumine.mythic.lib.api.util.ui.FFPMythicLib;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackCategory;
import io.lumine.mythic.lib.api.util.ui.FriendlyFeedbackProvider;
import io.lumine.mythic.lib.api.util.ui.SilentNumbers;
import io.lumine.mythic.lib.version.VInventoryView;
import io.lumine.mythic.lib.version.VersionUtils;
import java.util.ArrayList;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SuperWorkbenchMapping extends VanillaInventoryMapping implements CustomInventoryCheck, CommandExecutor, Listener {
   @NotNull
   static final ArrayList<String> sNames = new ArrayList<>();
   public static final String SUPER_WORKBENCH = "§s§w§°§¡§v";
   public static SuperWorkbenchMapping SWB = new SuperWorkbenchMapping();
   public static final ItemStack SWB_EDGE = ItemFactory.of(Material.GRAY_STAINED_GLASS_PANE).name("§ʤ").model(3000).build();
   static ArrayList<Integer> edgeSlots = generateEdgeSlots();

   @Override
   public int getMainWidth(int slot) throws IllegalArgumentException {
      switch (slot) {
         case 1:
         case 10:
         case 19:
         case 28:
         case 37:
            return 0;
         case 2:
         case 11:
         case 20:
         case 29:
         case 38:
            return 1;
         case 3:
         case 12:
         case 21:
         case 30:
         case 39:
            return 2;
         case 4:
         case 13:
         case 22:
         case 31:
         case 40:
            return 3;
         case 5:
         case 14:
         case 23:
         case 32:
         case 41:
            return 4;
         case 6:
         case 7:
         case 8:
         case 9:
         case 15:
         case 16:
         case 17:
         case 18:
         case 24:
         case 25:
         case 26:
         case 27:
         case 33:
         case 34:
         case 35:
         case 36:
         default:
            if (slot < 45) {
               return -1;
            } else {
               throw this.outOfBounds(slot);
            }
      }
   }

   @Override
   public int getMainHeight(int slot) throws IllegalArgumentException {
      switch (slot) {
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
            return 0;
         case 6:
         case 7:
         case 8:
         case 9:
         case 15:
         case 16:
         case 17:
         case 18:
         case 24:
         case 25:
         case 26:
         case 27:
         case 33:
         case 34:
         case 35:
         case 36:
         default:
            if (slot < 45) {
               return 1;
            }

            throw this.outOfBounds(slot);
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
            return -1;
         case 19:
         case 20:
         case 21:
         case 22:
         case 23:
            return -2;
         case 28:
         case 29:
         case 30:
         case 31:
         case 32:
            return -3;
         case 37:
         case 38:
         case 39:
         case 40:
         case 41:
            return -4;
      }
   }

   @Override
   public int getMainSlot(int width, int height) throws IllegalArgumentException {
      switch (height) {
         case 0:
            switch (width) {
               case 0:
                  return 1;
               case 1:
                  return 2;
               case 2:
                  return 3;
               case 3:
                  return 4;
               case 4:
                  return 5;
            }
         case -1:
            switch (width) {
               case 0:
                  return 10;
               case 1:
                  return 11;
               case 2:
                  return 12;
               case 3:
                  return 13;
               case 4:
                  return 14;
            }
         case -2:
            switch (width) {
               case 0:
                  return 19;
               case 1:
                  return 20;
               case 2:
                  return 21;
               case 3:
                  return 22;
               case 4:
                  return 23;
            }
         case -3:
            switch (width) {
               case 0:
                  return 28;
               case 1:
                  return 29;
               case 2:
                  return 30;
               case 3:
                  return 31;
               case 4:
                  return 32;
            }
         case -4:
            switch (width) {
               case 0:
                  return 37;
               case 1:
                  return 38;
               case 2:
                  return 39;
               case 3:
                  return 40;
               case 4:
                  return 41;
            }
         default:
            throw this.outOfBounds(width, height);
      }
   }

   @Override
   public int getMainInventoryStart() {
      return 1;
   }

   @Override
   public int getMainInventoryWidth() {
      return 5;
   }

   @Override
   public int getMainInventoryHeight() {
      return 5;
   }

   @Override
   public int getResultWidth(int slot) throws IllegalArgumentException {
      if (slot == 25) {
         return 0;
      } else {
         throw this.outOfBounds(slot);
      }
   }

   @Override
   public int getResultHeight(int slot) throws IllegalArgumentException {
      if (slot == 25) {
         return 0;
      } else {
         throw this.outOfBounds(slot);
      }
   }

   @Override
   public int getResultSlot(int width, int height) throws IllegalArgumentException {
      if (width == 0 && height == 0) {
         return 25;
      } else {
         throw this.outOfBounds(width, height);
      }
   }

   @Override
   public int getResultInventoryStart() {
      return 25;
   }

   @Override
   public int getResultInventoryWidth() {
      return 1;
   }

   @Override
   public int getResultInventoryHeight() {
      return 1;
   }

   @Override
   public boolean isResultSlot(int slot) {
      return slot == 25;
   }

   @Override
   public int getSideWidth(@NotNull String side, int slot) throws IllegalArgumentException {
      throw this.sideInventoryException(side);
   }

   @Override
   public int getSideHeight(@NotNull String side, int slot) throws IllegalArgumentException {
      throw this.sideInventoryException(side);
   }

   @Override
   public int getSideSlot(@NotNull String side, int width, int height) throws IllegalArgumentException {
      throw this.sideInventoryException(side);
   }

   @Override
   public int getSideInventoryStart(@NotNull String side) throws IllegalArgumentException {
      throw this.sideInventoryException(side);
   }

   @Override
   public int getSideInventoryWidth(@NotNull String side) throws IllegalArgumentException {
      throw this.sideInventoryException(side);
   }

   @Override
   public int getSideInventoryHeight(@NotNull String side) throws IllegalArgumentException {
      throw this.sideInventoryException(side);
   }

   @Override
   public boolean applyToSideInventory(@NotNull Inventory inventory, @NotNull MythicRecipeInventory finalSide, @NotNull String sideKeyName, boolean amountOnly) {
      return false;
   }

   @Override
   public boolean mainIsResult() {
      return false;
   }

   @Nullable
   @Override
   public MythicRecipeStation getIntendedStation() {
      return MythicRecipeStation.WORKBENCH;
   }

   @NotNull
   @Override
   public InventoryType getIntendedInventory() {
      return InventoryType.CHEST;
   }

   @NotNull
   @Override
   public ArrayList<String> getSideInventoryNames() {
      return sNames;
   }

   @Override
   public boolean isTargetInventory(@NotNull VInventoryView view) {
      return view.getTopInventory().getSize() != 45 ? false : view.getTitle().startsWith("§s§w§°§¡§v");
   }

   @NotNull
   @Override
   public String getCustomStationKey() {
      return "swb";
   }

   public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
      Player target = null;
      boolean failure = false;
      FriendlyFeedbackProvider ffp = new FriendlyFeedbackProvider(FFPMythicLib.get());
      ffp.activatePrefix(true, "Super Workbench");
      if (args.length == 0) {
         if (sender instanceof Player) {
            target = (Player)sender;
         } else {
            failure = true;
            ffp.log(
               FriendlyFeedbackCategory.ERROR,
               "$fYou must specify a player when calling from the console. $bBy the way, you can download the free texture pack assets to make the glass borders look smooth at https://sites.google.com/view/gootilities/core-plugin-goop/containers/container-templates/edge-formations?authuser=0"
            );
         }
      } else {
         String name = args[0];
         UUID possibleUUID = SilentNumbers.UUIDParse(name);
         if (possibleUUID != null) {
            target = Bukkit.getPlayer(possibleUUID);
         }

         if (target == null) {
            target = Bukkit.getPlayerExact(name);
            if (target == null) {
               failure = true;
               ffp.log(FriendlyFeedbackCategory.ERROR, "Player $i{0}$b not found.", name);
            }
         }
      }

      if (!failure) {
         Inventory swb = getSuperWorkbench(target);
         target.openInventory(swb);
      } else if (sender instanceof Player) {
         ffp.sendAllTo((Player)sender);
      } else {
         ffp.sendAllTo(sender);
      }

      return false;
   }

   public static Inventory getSuperWorkbench(@NotNull Player player) {
      Inventory swb = Bukkit.createInventory(player, 45, "§s§w§°§¡§v" + InventoryType.WORKBENCH.getDefaultTitle());

      for (Integer i : getEdgeSlots()) {
         swb.setItem(i, SWB_EDGE);
      }

      return swb;
   }

   static ArrayList<Integer> generateEdgeSlots() {
      ArrayList<Integer> ret = new ArrayList<>();
      ret.add(0);
      ret.add(9);
      ret.add(18);
      ret.add(27);
      ret.add(36);
      ret.add(6);
      ret.add(15);
      ret.add(24);
      ret.add(33);
      ret.add(42);
      ret.add(7);
      ret.add(16);
      ret.add(34);
      ret.add(43);
      ret.add(8);
      ret.add(17);
      ret.add(26);
      ret.add(35);
      ret.add(44);
      return ret;
   }

   public static ArrayList<Integer> getEdgeSlots() {
      return edgeSlots;
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void OnClick(InventoryClickEvent event) {
      VInventoryView view = VersionUtils.getView(event);
      int size = view.getTopInventory().getSize();
      if (size == 45) {
         if (view.getPlayer() instanceof Player) {
            InventoryAction act = event.getAction();
            if (this.isTargetInventory(view)) {
               if (event.getRawSlot() >= view.getTopInventory().getSize()) {
                  if (act != InventoryAction.MOVE_TO_OTHER_INVENTORY || event.getCurrentItem() == null) {
                     return;
                  }

                  event.setCancelled(true);
                  ItemStack item = event.getCurrentItem();
                  ArrayList<Integer> vibes = new ArrayList<>();
                  int lastAir = -1;

                  for (int h = 0; h < this.getMainInventoryHeight(); h++) {
                     for (int w = 0; w < this.getMainInventoryWidth(); w++) {
                        int sl = this.getMainSlot(w, -h);
                        ItemStack observed = get(view.getTopInventory(), sl);
                        boolean aero = SilentNumbers.isAir(observed);
                        if (aero || observed.isSimilar(item) && observed.getAmount() < observed.getType().getMaxStackSize()) {
                           if (!aero) {
                              vibes.add(sl);
                           }

                           if (aero && lastAir < 0) {
                              lastAir = sl;
                           }
                        }
                     }
                  }

                  if (lastAir >= 0) {
                     vibes.add(lastAir);
                  }

                  boolean completion = false;
                  ItemStack viableStacker = event.getCurrentItem().clone();

                  for (Integer v : vibes) {
                     ItemStack observed = view.getTopInventory().getItem(v);
                     if (SilentNumbers.isAir(observed)) {
                        event.getCurrentItem().setAmount(0);
                        view.getTopInventory().setItem(v, viableStacker);
                        completion = true;
                        break;
                     }

                     int remainder = observed.getType().getMaxStackSize() - observed.getAmount();
                     int vsAmount = viableStacker.getAmount();
                     if (remainder >= viableStacker.getAmount()) {
                        viableStacker.setAmount(0);
                        event.getCurrentItem().setAmount(0);
                        observed.setAmount(observed.getAmount() + vsAmount);
                        view.getTopInventory().setItem(v, observed);
                        completion = true;
                        break;
                     }

                     viableStacker.setAmount(vsAmount - remainder);
                     observed.setAmount(observed.getAmount() + remainder);
                     view.getTopInventory().setItem(v, observed);
                  }

                  if (!completion) {
                     event.setCurrentItem(viableStacker);
                  }
               }

               if (getEdgeSlots().contains(event.getSlot())) {
                  event.setCancelled(true);
               }

               if (event.getSlot() == 25 && act != InventoryAction.PICKUP_ALL && act != InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                  event.setCancelled(true);
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void OnClick(InventoryDragEvent event) {
      VInventoryView view = VersionUtils.getView(event);
      int size = view.getTopInventory().getSize();
      if (size == 45) {
         if (view.getPlayer() instanceof Player) {
            if (this.isTargetInventory(view)) {
               for (Integer i : event.getRawSlots()) {
                  if (i == 25) {
                     event.setCancelled(true);
                     return;
                  }
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void OnInvenClose(InventoryCloseEvent event) {
      VInventoryView view = VersionUtils.getView(event);
      int size = view.getTopInventory().getSize();
      if (size == 45) {
         if (view.getPlayer() instanceof Player) {
            if (this.isTargetInventory(view)) {
               int z = 0;
               ArrayList<ItemStack> drops = new ArrayList<>();

               for (int s = this.getMainInventoryStart(); s < this.getMainInventorySize() + this.getMainInventoryStart() + z; s++) {
                  int w = this.getMainWidth(s);
                  int h = this.getMainHeight(s);
                  if (w >= 0 && h <= 0) {
                     ItemStack item = view.getTopInventory().getItem(s);
                     if (!SilentNumbers.isAir(item)) {
                        drops.add(item);
                     }
                  } else {
                     z++;
                  }
               }

               for (ItemStack drop : event.getPlayer().getInventory().addItem(drops.toArray(new ItemStack[0])).values()) {
                  if (!SilentNumbers.isAir(drop)) {
                     event.getPlayer().getWorld().dropItem(event.getPlayer().getLocation(), drop);
                  }
               }
            }
         }
      }
   }
}
