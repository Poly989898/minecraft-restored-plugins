package com.nexomc.nexo.recipes.listeners;

import com.nexomc.libs.morepersistentdatatypes.DataType;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.recipes.CustomRecipe;
import com.nexomc.nexo.utils.EventUtils;
import com.nexomc.nexo.utils.ItemUtils;
import io.papermc.paper.event.player.PlayerStonecutterRecipeSelectEvent;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.time.Duration;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Furnace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.CrafterCraftEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000®\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0004\u0018\u0000 ?2\u00020\u0001:\u0001?B-\u0012\u0014\b\u0002\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00050\u0003\u0012\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007¢\u0006\u0004\b\t\u0010\nJ\u001a\u0010\u000b\u001a\u00020\f*\u00020\rH\u0007b\f\b\u000e\u0012\b\b\u000f\u0012\u0004\b\u0007\u0010\u0002J\u0010\u0010\u0010\u001a\u00020\f*\u00020\u0011H\u0007b\u0002\b\u000eJ&\u0010\u0012\u001a\u00020\f*\u00020\u0013H\u0007b\u0018\b\u000e\u0012\b\b\u000f\u0012\u0004\b\u0007\u0010\u0002\u0012\n\b\u0014\u0012\u0006\b\n0\u00158\u0016J\u001a\u0010\"\u001a\u00020\f*\u00020\rH\u0007b\f\b\u000e\u0012\b\b\u000f\u0012\u0004\b\u0007\u0010\u0002J\u0010\u0010#\u001a\u00020\f*\u00020$H\u0007b\u0002\b\u000eJ\u001a\u0010%\u001a\u00020\f*\u00020&H\u0007b\f\b\u000e\u0012\b\b\u000f\u0012\u0004\b\u0007\u0010\u0002J\u0010\u0010'\u001a\u00020\f*\u00020(H\u0007b\u0002\b\u000eJ\u0010\u0010)\u001a\u00020\f*\u00020*H\u0007b\u0002\b\u000eJ\u000e\u0010+\u001a\u0004\u0018\u00010,*\u00020-H\u0002J&\u0010.\u001a\u00020\f*\u00020/H\u0007b\u0018\b\u000e\u0012\b\b\u000f\u0012\u0004\b\u0007\u0010\u0002\u0012\n\b\u0014\u0012\u0006\b\n0\u00158\u0016J\u0010\u00100\u001a\u00020\f*\u000201H\u0007b\u0002\b\u000eJ\u0006\u00102\u001a\u00020\fJ\u0018\u00103\u001a\u00020\f2\u0006\u00104\u001a\u00020\b2\b\u00105\u001a\u0004\u0018\u00010\u0005J\u000e\u00106\u001a\u00020\f2\u0006\u00104\u001a\u00020\bJ\u0014\u00107\u001a\b\u0012\u0004\u0012\u00020\b082\u0006\u00109\u001a\u00020:J\u0019\u0010;\u001a\b\u0012\u0004\u0012\u00020\u00050<2\u0006\u00109\u001a\u00020:¢\u0006\u0002\u0010=J\u0018\u0010>\u001a\u00020 2\u0006\u00109\u001a\u00020:2\b\u00104\u001a\u0004\u0018\u00010\bR\u001a\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00050\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u0013\u0010\u0017\u001a\u00070\u0018¢\u0006\u0002\b\u0019X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u001a\u001a\u000e\u0012\u0004\u0012\u00020\u001c\u0012\u0004\u0012\u00020\u001d0\u001bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u001dX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020 X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020 X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006@"},
   d2 = {"Lcom/nexomc/nexo/recipes/listeners/RecipeEventManager;", "Lorg/bukkit/event/Listener;", "permissionsPerRecipe", "Lit/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;", "Lnet/kyori/adventure/key/Key;", "", "whitelistedRecipes", "Lit/unimi/dsi/fastutil/objects/ObjectOpenHashSet;", "Lcom/nexomc/nexo/recipes/CustomRecipe;", "<init>", "(Lit/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;Lit/unimi/dsi/fastutil/objects/ObjectOpenHashSet;)V", "onTrade", "", "Lorg/bukkit/event/inventory/InventoryClickEvent;", "Lorg/bukkit/event/EventHandler;", "ignoreCancelled", "onCrafter", "Lorg/bukkit/event/block/CrafterCraftEvent;", "onCrafted", "Lorg/bukkit/event/inventory/PrepareItemCraftEvent;", "priority", "Lorg/bukkit/event/EventPriority;", "HIGHEST", "FURNACE_OWNER", "Lorg/bukkit/NamespacedKey;", "Lorg/jetbrains/annotations/Nullable;", "furnaceCooldowns", "", "Lorg/bukkit/Location;", "", "cooldownDelay", "requireRecipePermission", "", "trackFurnacePlayer", "onFurnaceInput", "onStart", "Lorg/bukkit/event/inventory/FurnaceStartSmeltEvent;", "onBurn", "Lorg/bukkit/event/inventory/FurnaceBurnEvent;", "onTakeOut", "Lorg/bukkit/event/inventory/FurnaceExtractEvent;", "onDoneSmelt", "Lorg/bukkit/event/inventory/FurnaceSmeltEvent;", "furnacePlayer", "Lorg/bukkit/entity/Player;", "Lorg/bukkit/block/Furnace;", "onGrindstone", "Lio/papermc/paper/event/player/PlayerStonecutterRecipeSelectEvent;", "onJoin", "Lorg/bukkit/event/player/PlayerJoinEvent;", "resetRecipes", "addPermissionRecipe", "recipe", "permission", "whitelistRecipe", "permittedRecipes", "", "sender", "Lorg/bukkit/command/CommandSender;", "permittedRecipeNames", "", "(Lorg/bukkit/command/CommandSender;)[Ljava/lang/String;", "hasPermission", "Companion", "com.nexomc:core"}
)
public final class RecipeEventManager implements Listener {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final Object2ObjectOpenHashMap<Key, String> permissionsPerRecipe;
   @NotNull
   private final ObjectOpenHashSet<CustomRecipe> whitelistedRecipes;
   @NotNull
   private final NamespacedKey FURNACE_OWNER;
   @NotNull
   private final Map<Location, Long> furnaceCooldowns;
   private final long cooldownDelay;
   private final boolean requireRecipePermission;
   private final boolean trackFurnacePlayer;
   @Nullable
   private static RecipeEventManager instance;

   public RecipeEventManager(@NotNull Object2ObjectOpenHashMap<Key, String> permissionsPerRecipe, @NotNull ObjectOpenHashSet<CustomRecipe> whitelistedRecipes) {
      super();
      this.permissionsPerRecipe = permissionsPerRecipe;
      this.whitelistedRecipes = whitelistedRecipes;
      NamespacedKey namespacedKey = NamespacedKey.fromString("nexo:furnace_owner");
      this.FURNACE_OWNER = namespacedKey;
      this.furnaceCooldowns = (Map)(new LinkedHashMap());
      this.cooldownDelay = Duration.getInWholeMilliseconds_impl(Settings.RECIPES_FURNACE_COOLDOWN_DURATION.toDuration_UwyO8pc());
      this.requireRecipePermission = Settings.RECIPES_REQUIRE_PLAYER_IF_PERMISSION.toBool();
      this.trackFurnacePlayer = Settings.RECIPES_KEEP_TRACK_OF_PLAYER_FURNACE.toBool();
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public final void onTrade(@NotNull InventoryClickEvent inventoryClickEvent) {
      Inventory inventory2 = inventoryClickEvent.getInventory();
      MerchantInventory merchantInventory = inventory2 instanceof MerchantInventory ? (MerchantInventory)inventory2 : null;
      if ((inventory2 instanceof MerchantInventory ? (MerchantInventory)inventory2 : null) != null) {
         MerchantInventory inventory = merchantInventory;
         MerchantRecipe merchantRecipe = inventory.getSelectedRecipe();
         if (merchantRecipe != null) {
            MerchantRecipe merchantRecipe2 = merchantRecipe;
            int value = 0;
            merchantRecipe = inventoryClickEvent.getSlot() == 2 ? merchantRecipe2 : null;
            if (merchantRecipe != null) {
               MerchantRecipe selectedRecipe = merchantRecipe;
               ItemStack item = inventory.getItem(0);
               List ingredients = selectedRecipe.getIngredients();
               boolean first = NexoItems.isSameId(item, (ItemStack)CollectionsKt.getOrNull(ingredients, 0));
               item = inventory.getItem(1);
               ingredients = selectedRecipe.getIngredients();
               boolean second = NexoItems.isSameId(item, (ItemStack)CollectionsKt.getOrNull(ingredients, 1));
               if (!first || !second) {
                  inventoryClickEvent.setCancelled(true);
               }

               return;
            }
         }

      }
   }

   @EventHandler
   public final void onCrafter(@NotNull CrafterCraftEvent crafterCraftEvent) {
      if (((Map)this.permissionsPerRecipe).containsKey(crafterCraftEvent.getRecipe().key())) {
         crafterCraftEvent.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onCrafted(@NotNull PrepareItemCraftEvent prepareItemCraftEvent) {
      boolean flag;
      boolean flag2;
      HumanEntity humanEntity = prepareItemCraftEvent.getView().getPlayer();
      Player player2 = humanEntity instanceof Player ? (Player)humanEntity : null;
      if ((humanEntity instanceof Player ? (Player)humanEntity : null) != null) {
         Player player = player2;
         Object recipe = prepareItemCraftEvent.getRecipe();
         Object obj = recipe;
         if (!(recipe instanceof Keyed)) {
            obj = null;
         }

         Keyed keyed = (Keyed)obj;
         if ((Keyed)obj != null) {
            NamespacedKey key = keyed.getKey();
            if (key != null) {
               NamespacedKey recipeKey = key;
               boolean isValidDyeRecipe = Intrinsics.areEqual(recipeKey, Key.key("minecraft:armor_dye"));
               ItemStack[] matrix = prepareItemCraftEvent.getInventory().getMatrix();
               Object[] objs = matrix;
               int value = 0;
               int value8 = objs.length;

               while(true) {
                  if (value >= value8) {
                     flag = false;
                     break;
                  }

                  Object element = objs[value];
                  ItemStack it = (ItemStack)element;
                  int value2 = 0;
                  if (!ItemUtils.INSTANCE.isAllowedInVanillaRecipes(it)) {
                     flag = true;
                     break;
                  }

                  ++value;
               }

               boolean notAllowedInMatrix = flag;
               if (Intrinsics.areEqual(recipeKey.getNamespace(), "minecraft") && !isValidDyeRecipe && notAllowedInMatrix) {
                  prepareItemCraftEvent.getInventory().setResult((ItemStack)null);
               } else if (notAllowedInMatrix && !Intrinsics.areEqual(recipeKey.getNamespace(), "minecraft")) {
                  boolean flag3;
                  Recipe r = prepareItemCraftEvent.getRecipe();
                  List list;
                  if (r instanceof ShapedRecipe) {
                     Iterable iterable = (Iterable)((ShapedRecipe)r).getChoiceMap().values();
                     value8 = 0;
                     Collection destination = (Collection)(new ArrayList());

                     for(Object obj2 : iterable) {
                        if (obj2 instanceof RecipeChoice.ExactChoice) {
                           destination.add(obj2);
                        }
                     }

                     list = (List)destination;
                  } else if (!(r instanceof ShapelessRecipe)) {
                     if (r instanceof CookingRecipe) {
                        RecipeChoice inputChoice = ((CookingRecipe)r).getInputChoice();
                        list = CollectionsKt.listOfNotNull(inputChoice instanceof RecipeChoice.ExactChoice ? (RecipeChoice.ExactChoice)inputChoice : null);
                     } else {
                        list = CollectionsKt.emptyList();
                     }
                  } else {
                     list = ((ShapelessRecipe)r).getChoiceList();
                     Iterable iterable2 = (Iterable)list;
                     value8 = 0;
                     Collection collection = (Collection)(new ArrayList());

                     for(Object obj3 : iterable2) {
                        if (obj3 instanceof RecipeChoice.ExactChoice) {
                           collection.add(obj3);
                        }
                     }

                     list = (List)collection;
                  }

                  List ingredientChoices = list;
                  ItemStack[] itemStacks = prepareItemCraftEvent.getInventory().getMatrix();
                  Object[] objs2 = itemStacks;
                  value8 = 0;
                  int value9 = 0;
                  int value10 = objs2.length;

                  while(true) {
                     if (value9 >= value10) {
                        flag3 = false;
                        break;
                     }

                     Object obj4 = objs2[value9];
                     ItemStack item = (ItemStack)obj4;
                     int value3 = 0;
                     boolean flag4;
                     if (item != null && NexoItems.exists(item)) {
                        block: {
                           if (!ItemUtils.INSTANCE.isAllowedInVanillaRecipes(item)) {
                              Iterable iterable3 = (Iterable)ingredientChoices;
                              if (iterable3 instanceof Collection && ((Collection)iterable3).isEmpty()) {
                                 flag4 = true;
                              } else {
                                 Iterator iterator = iterable3.iterator();

                                 while(true) {
                                    if (!iterator.hasNext()) {
                                       flag4 = true;
                                       break;
                                    }

                                    Object obj5 = iterator.next();
                                    RecipeChoice.ExactChoice it = (RecipeChoice.ExactChoice)obj5;
                                    int value4 = 0;
                                    if (it.test(item)) {
                                       flag4 = false;
                                       break;
                                    }
                                 }
                              }

                              if (flag4) {
                                 flag4 = true;
                                 break block;
                              }
                           }

                           flag4 = false;
                        }
                     } else {
                        flag4 = false;
                     }

                     if (flag4) {
                        flag3 = true;
                        break;
                     }

                     ++value9;
                  }

                  boolean hasDisallowedNexo = flag3;
                  if (hasDisallowedNexo) {
                     prepareItemCraftEvent.getInventory().setResult((ItemStack)null);
                  }
               }

               CustomRecipe customRecipe = CustomRecipe.Companion.fromRecipe(prepareItemCraftEvent.getRecipe());
               if (!this.hasPermission((CommandSender)player, customRecipe)) {
                  prepareItemCraftEvent.getInventory().setResult((ItemStack)null);
               }

               if (prepareItemCraftEvent.getInventory().getResult() != null && prepareItemCraftEvent.getRecipe() != null) {
                  matrix = prepareItemCraftEvent.getInventory().getMatrix();
                  Iterable iterable4;
                  NexoItems nexoItems = NexoItems.INSTANCE;
                  value8 = 0;
                  int value5 = 0;
                  int p0 = matrix.length;

                  while(true) {
                     if (value5 >= p0) {
                        flag2 = true;
                        break;
                     }

                     Object obj6 = matrix[value5];
                     ItemStack p0_2 = (ItemStack)obj6;
                     int value6 = 0;
                     if (NexoItems.exists(p0_2)) {
                        flag2 = false;
                        break;
                     }

                     ++value5;
                  }

                  if (!flag2) {
                     if (customRecipe != null && !isValidDyeRecipe) {
                        iterable4 = (Iterable)this.whitelistedRecipes;
                        CustomRecipe customRecipe2 = customRecipe;
                        value8 = 0;
                        boolean flag5;
                        if (iterable4 instanceof Collection && ((Collection)iterable4).isEmpty()) {
                           flag5 = true;
                        } else {
                           Iterator iterator2 = iterable4.iterator();

                           while(true) {
                              if (!iterator2.hasNext()) {
                                 flag5 = true;
                                 break;
                              }

                              Object obj7 = iterator2.next();
                              int value7 = 0;
                              if (customRecipe2.equals(obj7)) {
                                 flag5 = false;
                                 break;
                              }
                           }
                        }

                        if (!flag5) {
                           prepareItemCraftEvent.getInventory().setResult(customRecipe.getResult());
                           return;
                        }
                     }

                     return;
                  }
               }

               return;
            }
         }

      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public final void onFurnaceInput(@NotNull InventoryClickEvent inventoryClickEvent) {
      InventoryHolder holder = inventoryClickEvent.getInventory().getHolder();
      Furnace furnace2 = holder instanceof Furnace ? (Furnace)holder : null;
      if ((holder instanceof Furnace ? (Furnace)holder : null) != null) {
         Furnace furnace = furnace2;
         if (inventoryClickEvent.getRawSlot() == 0 || inventoryClickEvent.isShiftClick() && inventoryClickEvent.getCurrentItem() != null) {
            if (this.requireRecipePermission && this.trackFurnacePlayer) {
               furnace.getPersistentDataContainer().set(this.FURNACE_OWNER, DataType.UUID, inventoryClickEvent.getWhoClicked().getUniqueId());
               furnace.update();
            }
         }
      }
   }

   @EventHandler
   public final void onStart(@NotNull FurnaceStartSmeltEvent furnaceStartSmeltEvent) {
      Object state = furnaceStartSmeltEvent.getBlock().getState();
      Object obj = state;
      if (!(state instanceof Furnace)) {
         obj = null;
      }

      Furnace furnace2 = (Furnace)obj;
      if (furnace2 != null) {
         Furnace furnace = furnace2;
         Player player = this.furnacePlayer(furnace);
         if (!Intrinsics.areEqual(furnaceStartSmeltEvent.getRecipe().getKey().getNamespace(), "minecraft")) {
            if (player == null && ((Map)this.permissionsPerRecipe).containsKey(furnaceStartSmeltEvent.getRecipe().getKey()) && this.requireRecipePermission) {
               furnace.setCookTimeTotal(Integer.MAX_VALUE);
            } else if (player != null) {
               CommandSender commandSender = (CommandSender)player;
               CustomRecipe customRecipe = CustomRecipe.Companion.fromRecipe((Recipe)furnaceStartSmeltEvent.getRecipe());
               if (customRecipe == null) {
                  return;
               }

               if (!this.hasPermission(commandSender, customRecipe)) {
                  furnace.setCookTimeTotal(Integer.MAX_VALUE);
               }
            }

            furnace.update();
         }
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public final void onBurn(@NotNull FurnaceBurnEvent furnaceBurnEvent) {
      boolean flag;
      Long cooldown = (Long)this.furnaceCooldowns.get(furnaceBurnEvent.getBlock().getLocation());
      if (cooldown != null && System.currentTimeMillis() - cooldown < this.cooldownDelay) {
         EventUtils.INSTANCE.returnCancelled((Cancellable)furnaceBurnEvent);
      } else {
         Object state = furnaceBurnEvent.getBlock().getState();
         Object obj = state;
         if (!(state instanceof Furnace)) {
            obj = null;
         }

         Furnace furnace2 = (Furnace)obj;
         if (furnace2 != null) {
            Furnace furnace = furnace2;
            if (furnace.getCookTimeTotal() == Integer.MAX_VALUE) {
               this.furnaceCooldowns.put(furnaceBurnEvent.getBlock().getLocation(), System.currentTimeMillis());
               EventUtils.INSTANCE.returnCancelled((Cancellable)furnaceBurnEvent);
            } else {
               ItemStack smelting = furnace.getInventory().getSmelting();
               Iterable iterable = (Iterable)this.whitelistedRecipes;
               Iterator iterator = iterable.iterator();

               CustomRecipe customRecipe;
               while(true) {
                  if (!iterator.hasNext()) {
                     customRecipe = null;
                     break;
                  }

                  Object element;
                  block: {
                     element = iterator.next();
                     CustomRecipe r = (CustomRecipe)element;
                     int value = 0;
                     if (((Collection)r.getIngredients()).size() == 1) {
                        Iterable ingredients = (Iterable)r.getIngredients();
                        boolean flag2;
                        if (ingredients instanceof Collection && ((Collection)ingredients).isEmpty()) {
                           flag2 = true;
                        } else {
                           block2: {
                              for(Object obj2 : ingredients) {
                                 RecipeChoice i = (RecipeChoice)obj2;
                                 int value2 = 0;
                                 if (smelting != null) {
                                    int value3 = 0;
                                    flag2 = Intrinsics.areEqual(i != null ? i.test(smelting) : null, true);
                                 } else {
                                    flag2 = false;
                                 }

                                 if (!flag2) {
                                    flag2 = false;
                                    break block2;
                                 }
                              }

                              flag2 = true;
                           }
                        }

                        if (flag2) {
                           flag = true;
                           break block;
                        }
                     }

                     flag = false;
                  }

                  if (flag) {
                     customRecipe = (CustomRecipe)element;
                     break;
                  }
               }

               CustomRecipe customRecipe2 = (CustomRecipe)customRecipe;
               if (customRecipe2 != null) {
                  CustomRecipe recipe = customRecipe2;
                  Player player = this.furnacePlayer(furnace);
                  if (!Intrinsics.areEqual(recipe.getKey().namespace(), "minecraft")) {
                     if (player == null && ((Map)this.permissionsPerRecipe).containsKey(recipe.getKey()) && this.requireRecipePermission) {
                        furnaceBurnEvent.setCancelled(true);
                     } else if (player != null && !this.hasPermission((CommandSender)player, recipe)) {
                        furnaceBurnEvent.setCancelled(true);
                     }

                     if (furnaceBurnEvent.isCancelled()) {
                        this.furnaceCooldowns.put(furnaceBurnEvent.getBlock().getLocation(), System.currentTimeMillis());
                     }

                  }
               }
            }
         }
      }
   }

   @EventHandler
   public final void onTakeOut(@NotNull FurnaceExtractEvent furnaceExtractEvent) {
      this.furnaceCooldowns.remove(furnaceExtractEvent.getBlock().getLocation());
      if (this.trackFurnacePlayer) {
         Object state = furnaceExtractEvent.getBlock().getState();
         Object obj = state;
         if (!(state instanceof Furnace)) {
            obj = null;
         }

         Furnace furnace2 = (Furnace)obj;
         if (furnace2 != null) {
            Furnace furnace = furnace2;
            if (furnace.getInventory().getSmelting() == null) {
               furnace.getPersistentDataContainer().remove(this.FURNACE_OWNER);
               furnace.update();
            }

         }
      }
   }

   @EventHandler
   public final void onDoneSmelt(@NotNull FurnaceSmeltEvent furnaceSmeltEvent) {
      if (this.trackFurnacePlayer && furnaceSmeltEvent.getSource().getAmount() == 1) {
         Object state = furnaceSmeltEvent.getBlock().getState();
         Object obj = state;
         if (!(state instanceof Furnace)) {
            obj = null;
         }

         Furnace furnace = (Furnace)obj;
         if (furnace != null) {
            PersistentDataContainer persistentDataContainer = furnace.getPersistentDataContainer();
            if (persistentDataContainer != null) {
               persistentDataContainer.remove(this.FURNACE_OWNER);
            }
         }
      }

   }

   private final Player furnacePlayer(Furnace furnace) {
      Object obj;
      if (!this.trackFurnacePlayer) {
         List viewers = furnace.getInventory().getViewers();
         obj = CollectionsKt.firstOrNull(viewers);
      } else {
         block: {
            UUID it = (UUID)furnace.getPersistentDataContainer().get(this.FURNACE_OWNER, DataType.UUID);
            if (it != null) {
               int value = 0;
               Player player = Bukkit.getPlayer(it);
               OfflinePlayer offlinePlayer = player != null ? (OfflinePlayer)player : Bukkit.getOfflinePlayer(it);
               if (offlinePlayer != null) {
                  obj = offlinePlayer;
                  break block;
               }
            }

            List list = furnace.getInventory().getViewers();
            obj = CollectionsKt.firstOrNull(list);
         }
      }

      Object obj2 = obj;
      return obj2 instanceof Player ? (Player)obj2 : null;
   }

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public final void onGrindstone(@NotNull PlayerStonecutterRecipeSelectEvent playerStonecutterRecipeSelectEvent) {
      Player player = playerStonecutterRecipeSelectEvent.getPlayer();
      if (!this.hasPermission((CommandSender)player, CustomRecipe.Companion.fromRecipe((Recipe)playerStonecutterRecipeSelectEvent.getStonecuttingRecipe()))) {
         playerStonecutterRecipeSelectEvent.setCancelled(true);
      }

      if (Intrinsics.areEqual(playerStonecutterRecipeSelectEvent.getStonecuttingRecipe().getKey().getNamespace(), "minecraft") && !ItemUtils.INSTANCE.isAllowedInVanillaRecipes(playerStonecutterRecipeSelectEvent.getStonecutterInventory().getInputItem())) {
         playerStonecutterRecipeSelectEvent.setCancelled(true);
      }

   }

   @EventHandler
   public final void onJoin(@NotNull PlayerJoinEvent playerJoinEvent) {
      if (Settings.ADD_RECIPES_TO_BOOK.toBool()) {
         Player player = playerJoinEvent.getPlayer();
         Player player2 = playerJoinEvent.getPlayer();
         Iterable iterable = (Iterable)this.permittedRecipes((CommandSender)player2);
         Player player3 = player;
         Collection destination = (Collection)(new ArrayList());

         for(Object element : iterable) {
            int value = 0;
            CustomRecipe p0 = (CustomRecipe)element;
            int value2 = 0;
            NamespacedKey namespacedKey = p0.getNamespacedKey();
            if (namespacedKey != null) {
               Object it = namespacedKey;
               int value3 = 0;
               destination.add(it);
            }
         }

         player3.discoverRecipes((Collection)((List)destination));
      }
   }

   public final void resetRecipes() {
      this.permissionsPerRecipe.clear();
      this.whitelistedRecipes.clear();
      if (Settings.RESET_RECIPES.toBool()) {
         Bukkit.recipeIterator().forEachRemaining((a0_ -> RecipeEventManager.resetRecipes$lambda$0((Recipe)a0_)));
         Bukkit.getServer().getPotionBrewer().resetPotionMixes();
      }

   }

   public final void addPermissionRecipe(@NotNull CustomRecipe recipe, @Nullable String permission) {
      Map map = (Map)this.permissionsPerRecipe;
      Key key = recipe.getKey();
      if (permission != null) {
         map.put(key, permission);
      }
   }

   public final void whitelistRecipe(@NotNull CustomRecipe recipe) {
      ((Collection)this.whitelistedRecipes).add(recipe);
   }

   @NotNull
   public final List<CustomRecipe> permittedRecipes(@NotNull CommandSender sender) {
      Iterable iterable = (Iterable)this.whitelistedRecipes;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         CustomRecipe customRecipe = (CustomRecipe)element;
         int value = 0;
         if (!((Map)this.permissionsPerRecipe).containsKey(customRecipe.getKey()) || this.hasPermission(sender, customRecipe)) {
            destination.add(element);
         }
      }

      return (List)destination;
   }

   @NotNull
   public final String[] permittedRecipeNames(@NotNull CommandSender sender) {
      Iterable iterable = (Iterable)this.permittedRecipes(sender);
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         CustomRecipe it = (CustomRecipe)item;
         int value = 0;
         destination.add(it.getName());
      }

      Collection collection = (Collection)((List)destination);
      return (String[])collection.toArray(new String[0]);
   }

   public final boolean hasPermission(@NotNull CommandSender sender, @Nullable CustomRecipe recipe) {
      String str = (String)this.permissionsPerRecipe.get(recipe != null ? recipe.getKey() : null);
      boolean flag;
      if (str != null) {
         String p0 = str;
         int value = 0;
         flag = sender.hasPermission(p0);
      } else {
         flag = true;
      }

      return flag;
   }

   public RecipeEventManager() {
      this(new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap(), new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet());
   }

   private static final Unit resetRecipes$lambda$0(Recipe it) {
      Keyed keyed = it instanceof Keyed ? (Keyed)it : null;
      if ((it instanceof Keyed ? (Keyed)it : null) != null) {
         NamespacedKey namespacedKey = keyed.getKey();
         if (namespacedKey != null) {
            NamespacedKey r = namespacedKey;
            int value = 0;
            namespacedKey = Intrinsics.areEqual(r.getNamespace(), "nexo") ? r : null;
            if (namespacedKey != null) {
               NamespacedKey key = namespacedKey;
               Bukkit.removeRecipe(key, false);
               return Unit.INSTANCE;
            }
         }
      }

      return Unit.INSTANCE;
   }

   private static final void resetRecipes$lambda$1(Function1 tmp0, Object p0) {
      tmp0.invoke(p0);
   }

   @JvmStatic
   @NotNull
   public static final RecipeEventManager instance() {
      return Companion.instance();
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\f\u0010\u0004\u001a\u00020\u0005H\u0007b\u0002\b\u0006R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0007"},
      d2 = {"Lcom/nexomc/nexo/recipes/listeners/RecipeEventManager$Companion;", "", "<init>", "()V", "instance", "Lcom/nexomc/nexo/recipes/listeners/RecipeEventManager;", "Lkotlin/jvm/JvmStatic;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @JvmStatic
      @NotNull
      public final RecipeEventManager instance() {
         if (RecipeEventManager.instance == null) {
            RecipeEventManager.instance = new RecipeEventManager(new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap(), new it.unimi.dsi.fastutil.objects.ObjectOpenHashSet());
         }

         RecipeEventManager recipeEventManager = RecipeEventManager.instance;
         return recipeEventManager;
      }
   }
}
