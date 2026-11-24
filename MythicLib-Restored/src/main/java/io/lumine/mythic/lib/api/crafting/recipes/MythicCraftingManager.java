package io.lumine.mythic.lib.api.crafting.recipes;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.crafting.ingredients.MythicBlueprintInventory;
import io.lumine.mythic.lib.api.crafting.ingredients.MythicRecipeInventory;
import io.lumine.mythic.lib.api.crafting.outputs.MythicRecipeOutput;
import io.lumine.mythic.lib.api.crafting.recipes.vmp.CustomInventoryCheck;
import io.lumine.mythic.lib.api.crafting.recipes.vmp.VanillaInventoryMapping;
import io.lumine.mythic.lib.api.crafting.uifilters.EnchantmentUIFilter;
import io.lumine.mythic.lib.api.crafting.uifilters.IngredientUIFilter;
import io.lumine.mythic.lib.api.crafting.uifilters.RecipeUIFilter;
import io.lumine.mythic.lib.api.crafting.uifilters.VanillaUIFilter;
import io.lumine.mythic.lib.api.util.Ref;
import io.lumine.mythic.lib.version.VInventoryView;
import io.lumine.mythic.lib.version.VersionUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MythicCraftingManager implements Listener {
   public static final ItemStack AIR = new ItemStack(Material.AIR);
   static boolean hasRegistered;
   @NotNull
   private static final HashMap<String, MythicRecipe> loadedRecipes = new HashMap<>();
   @NotNull
   private static final HashMap<MythicRecipeStation, ArrayList<MythicRecipeBlueprint>> perStationRecipes = new HashMap<>();
   @NotNull
   private static final HashMap<String, ArrayList<MythicRecipeBlueprint>> perCustomRecipes = new HashMap<>();

   public static void log(String message) {
      MythicLib.plugin.getServer().getConsoleSender().sendMessage(MythicLib.plugin.parseColors(message));
   }

   public MythicCraftingManager() {
      if (!hasRegistered) {
         hasRegistered = true;
         VanillaUIFilter.register();
         EnchantmentUIFilter.register();
         IngredientUIFilter.register();
         RecipeUIFilter.register();
         VanillaInventoryMapping.registerAll();
      }
   }

   protected static boolean loadRecipe(@NotNull MythicRecipe recipe) {
      if (loadedRecipes.get(recipe.getName()) != null) {
         return false;
      }

      loadedRecipes.put(recipe.getName(), recipe);
      return true;
   }

   protected static void unloadRecipe(@NotNull MythicRecipe recipe) {
      loadedRecipes.remove(recipe.getName());
   }

   @Nullable
   public static MythicRecipe getLoadedRecipe(@NotNull String name) {
      return loadedRecipes.get(name);
   }

   @NotNull
   public static ArrayList<String> getLoadedRecipes() {
      return new ArrayList<>(loadedRecipes.keySet());
   }

   public static void deployBlueprint(@NotNull MythicRecipeBlueprint blueprint, @NotNull MythicRecipeStation forStation) {
      deployBlueprint(blueprint, forStation, null);
   }

   public static void deployBlueprint(@NotNull MythicRecipeBlueprint blueprint, @NotNull MythicRecipeStation forStation, @Nullable String custom) {
      ArrayList<MythicRecipeBlueprint> found = getStationRecipes(forStation, custom);
      found.add(blueprint);
      blueprint.registerAsDeployed(forStation);
      if (forStation == MythicRecipeStation.CUSTOM && custom != null) {
         perCustomRecipes.put(custom, found);
      } else {
         perStationRecipes.put(forStation, found);
      }
   }

   protected static void disableBlueprint(@NotNull MythicRecipeBlueprint blueprint, @NotNull MythicRecipeStation forStation) {
      disableBlueprint(blueprint, forStation, null);
   }

   protected static void disableBlueprint(@NotNull MythicRecipeBlueprint blueprint, @NotNull MythicRecipeStation forStation, @Nullable String custom) {
      ArrayList<MythicRecipeBlueprint> found = getStationRecipes(forStation, custom);
      found.remove(blueprint);
      if (forStation == MythicRecipeStation.CUSTOM && custom != null) {
         perCustomRecipes.put(custom, found);
      } else {
         perStationRecipes.put(forStation, found);
      }
   }

   @NotNull
   public static ArrayList<MythicRecipeBlueprint> getStationRecipes(@NotNull MythicRecipeStation station, @Nullable String custom) {
      return station == MythicRecipeStation.CUSTOM && custom != null
         ? perCustomRecipes.computeIfAbsent(custom, k -> new ArrayList<>())
         : perStationRecipes.computeIfAbsent(station, k -> new ArrayList<>());
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onCraftingStationUse(InventoryClickEvent event) {
      VInventoryView view = VersionUtils.getView(event);
      Inventory inven = view.getTopInventory();
      VanillaInventoryMapping mapping = VanillaInventoryMapping.getMappingFor(view);
      if (mapping != null && mapping.getIntendedStation() != null) {
         ArrayList<MythicRecipeBlueprint> liveRecipes = getStationRecipes(
            mapping.getIntendedStation(), mapping instanceof CustomInventoryCheck ? ((CustomInventoryCheck)mapping).getCustomStationKey() : null
         );
         if (liveRecipes.size() != 0) {
            this.computeCraftingAction(mapping, inven, liveRecipes, event.getViewers(), event);
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST)
   public void onCraftingStationUse(InventoryDragEvent event) {
      VInventoryView view = VersionUtils.getView(event);
      Inventory inven = view.getTopInventory();
      VanillaInventoryMapping mapping = VanillaInventoryMapping.getMappingFor(view);
      if (mapping != null && mapping.getIntendedStation() != null) {
         ArrayList<MythicRecipeBlueprint> liveRecipes = getStationRecipes(
            mapping.getIntendedStation(), mapping instanceof CustomInventoryCheck ? ((CustomInventoryCheck)mapping).getCustomStationKey() : null
         );
         if (liveRecipes.size() != 0) {
            boolean clickedTop = false;
            int cSl = 0;

            for (Integer slot : event.getRawSlots()) {
               if (slot < inven.getSize()) {
                  clickedTop = true;
                  if (mapping.isResultSlot(slot)) {
                     event.setCancelled(true);
                     return;
                  }

                  cSl = slot;
               }
            }

            if (clickedTop) {
               InventoryClickEvent tEvent = VersionUtils.invClickEvent(view, SlotType.CRAFTING, cSl, ClickType.LEFT, InventoryAction.PLACE_SOME);
               this.computeCraftingAction(mapping, inven, liveRecipes, event.getViewers(), tEvent);
            }
         }
      }
   }

   public void computeCraftingAction(
      @NotNull final VanillaInventoryMapping mapping,
      @NotNull final Inventory inven,
      @NotNull final ArrayList<MythicRecipeBlueprint> liveRecipes,
      @NotNull final List<HumanEntity> viewers,
      @NotNull final InventoryClickEvent event
   ) {
      boolean cResultSlot = mapping.isResultSlot(event.getRawSlot());
      if (cResultSlot) {
         MythicRecipeInventory resultInventory = mapping.getResultMythicInventory(inven);
         MythicCachedResult cache = MythicCachedResult.get(resultInventory.getFirst());
         if (cache == null) {
            return;
         }

         MythicRecipeOutput output = cache.getOperation().getResult();
         int times = cache.getTimes();
         if (!isCraftToCompletion(event)) {
            times = 1;
         }

         output.applyResult(resultInventory, mapping.extractFrom(inven), cache, event, mapping, times);
      }

      if (event.getAction() != InventoryAction.NOTHING || cResultSlot) {
         if (mapping.getIntendedInventory() == InventoryType.CHEST) {
            mapping.applyToResultInventory(inven, new MythicRecipeInventory(), false);
         }

         (new BukkitRunnable() {
            public void run() {
               ArrayList<Player> p = new ArrayList<>();

               for (HumanEntity e : viewers) {
                  if (e instanceof Player) {
                     p.add((Player)e);
                  }
               }

               boolean res = MythicCraftingManager.this.displayResult(mapping, inven, liveRecipes, p, event.getWhoClicked().getUniqueId(), event);
            }
         }).runTaskLater(MythicLib.plugin, 1L);
      }
   }

   public static boolean isCraftToCompletion(@NotNull InventoryClickEvent event) {
      return event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY || event.getAction() == InventoryAction.NOTHING && event.isShiftClick();
   }

   public boolean displayResult(
      @NotNull VanillaInventoryMapping mapping,
      @NotNull Inventory inven,
      @NotNull ArrayList<MythicRecipeBlueprint> liveRecipes,
      @NotNull ArrayList<Player> viewers,
      @NotNull UUID uuid,
      @NotNull InventoryClickEvent eventTrigger
   ) {
      MythicBlueprintInventory inventory = mapping.extractFrom(inven);

      for (MythicRecipeBlueprint blueprint : liveRecipes) {
         boolean permBlock = false;

         for (Player viewer : viewers) {
            if (viewer != null && !blueprint.checkPermissions(viewer)) {
               permBlock = true;
               break;
            }
         }

         if (!permBlock) {
            Ref<Integer> timesCrafted = new Ref<>();
            MythicBlueprintInventory finalBlueprint = blueprint.matches(inventory, timesCrafted);
            if (finalBlueprint != null) {
               MythicRecipeOutput output = blueprint.getResult();
               MythicRecipeInventory finalResult = output.applyDisplay(finalBlueprint, eventTrigger, mapping);
               int craftTimes = timesCrafted.getValue(1);
               new MythicCachedResult(uuid, finalBlueprint, blueprint, finalResult, craftTimes);
               if (!mapping.mainIsResult()) {
                  mapping.applyToResultInventory(inven, finalResult, false);
               }

               for (Player viewer : viewers) {
                  if (viewer != null) {
                     viewer.updateInventory();
                  }
               }

               return true;
            }
         }
      }

      return false;
   }
}
