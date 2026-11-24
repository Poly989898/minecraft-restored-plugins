package fr.phoenixdevt.mmoprofiles.bukkit.gui;

import fr.phoenixdevt.mmoprofiles.bukkit.MMOProfiles;
import fr.phoenixdevt.mmoprofiles.bukkit.manager.InventoryManager;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.PlayerProfileImpl;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.ProfileListImpl;
import fr.phoenixdevt.mmoprofiles.bukkit.profile.placeholder.PlaceholderRequestImpl;
import fr.phoenixdevt.mmoprofiles.bukkit.utils.ChatInput;
import fr.phoenixdevt.mmoprofiles.bukkit.utils.message.Message;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import fr.phoenixdevt.mmoprofiles.bukkit.gui.lib.Navigator;
import fr.phoenixdevt.mmoprofiles.bukkit.gui.lib.EditableInventory;
import fr.phoenixdevt.mmoprofiles.bukkit.gui.lib.GeneratedInventory;
import fr.phoenixdevt.mmoprofiles.bukkit.gui.lib.InventoryItem;
import fr.phoenixdevt.mmoprofiles.bukkit.gui.lib.PhysicalItem;
import fr.phoenixdevt.mmoprofiles.bukkit.gui.lib.SimpleItem;
import fr.phoenixdevt.mmoprofiles.bukkit.gui.lib.NextPageItem;
import fr.phoenixdevt.mmoprofiles.bukkit.gui.lib.PreviousPageItem;
import fr.phoenixdevt.mmoprofiles.bukkit.gui.lib.Placeholders;
import fr.phoenixdevt.mmoprofiles.bukkit.utils.Attributes;
import io.lumine.mythic.lib.version.VersionUtils;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ProfileListViewer extends EditableInventory {
   private String removalName;
   private final Map<String, Function<ConfigurationSection, InventoryItem<?>>> ITEM_REGISTRY = Map.of(
      "profile",
      x$0 -> new ProfileListViewer.ProfileItem(x$0),
      "removal",
      x$0 -> new ProfileListViewer.RemovalItem(x$0),
      "next-page",
      NextPageItem::new,
      "previous-page",
      PreviousPageItem::new
   );
   private static final NamespacedKey EMPTY_SLOT_KEY = new NamespacedKey(MMOProfiles.plugin, "empty_slot");
   private static final NamespacedKey SLOT_NUMBER_KEY = new NamespacedKey(MMOProfiles.plugin, "slot_number");

   public ProfileListViewer() {
      super("profile-list");
   }

   @NotNull
   public ProfileListViewer.Generated generate(@NotNull ProfileListImpl playerData) {
      return new ProfileListViewer.Generated(new Navigator(playerData.getMMOPlayerData()), playerData, this);
   }

   public void reload(@NotNull JavaPlugin plugin, @NotNull ConfigurationSection config) {
      super.reload(plugin, config);
      this.removalName = config.getString("removal_name", "Profile Removal");
   }

   public InventoryItem<?> resolveItem(String function, ConfigurationSection config) {
      Function<ConfigurationSection, InventoryItem<?>> type = this.ITEM_REGISTRY.get(function);
      return type != null ? type.apply(config) : null;
   }

   class EmptySlotItem extends SimpleItem<ProfileListViewer.Generated> {
      public EmptySlotItem(ConfigurationSection config) {
         super(config);
      }

      public Placeholders getPlaceholders(ProfileListViewer.Generated inv, int n) {
         Placeholders holders = new Placeholders();
         holders.register("max_profiles", inv.playerData.getProfileSlots());
         holders.register("current_profiles", inv.playerData.getProfiles().size());
         return holders;
      }
   }

   class ExistingProfileItem extends PhysicalItem<ProfileListViewer.Generated> {
      private final InventoryItem<ProfileListViewer.Generated> parent;

      public ExistingProfileItem(@NotNull ConfigurationSection config, InventoryItem<ProfileListViewer.Generated> parent) {
         super(config);
         this.parent = parent;
      }

      public Placeholders getPlaceholders(ProfileListViewer.Generated inv, int n) {
         int index = (inv.page - 1) * inv.perPage + n;
         final PlayerProfileImpl profile = inv.playerData.getProfiles().get(index);
         Placeholders holders = new Placeholders() {
            @NotNull
            public String parsePlaceholder(@NotNull String key) {
               return key.startsWith("attribute_")
                  ? String.valueOf(profile.getAttributes().getValue(Attributes.adapt(UtilityMethods.enumName(key.substring(10)))))
                  : super.parsePlaceholder(key);
            }
         };
         holders.register("profile_name", profile.getName());
         holders.register("profile_uuid", profile.getUniqueId());
         holders.register("last_time_played", MMOProfiles.plugin.configManager.dateFormat.format(new Date(profile.getLastTimePlayed())));
         holders.register("time_played", UtilityMethods.formatDelay(profile.getTimePlayed()));
         holders.register("slot", String.valueOf(n + 1));
         holders.register("health", MythicLib.plugin.getMMOConfig().decimal.format(profile.getHealth()));
         holders.register("food_level", String.valueOf(profile.getFoodLevel()));
         holders.register("saturation", MythicLib.plugin.getMMOConfig().decimal.format(profile.getSaturation()));
         holders.register("level", String.valueOf(profile.getLevel()));
         holders.register("exp", MythicLib.plugin.getMMOConfig().decimal.format(profile.getExperience()));
         holders.register("balance", MythicLib.plugin.getMMOConfig().decimal.format(profile.getBalance()));
         holders.register("air_level", String.valueOf(profile.getAirLevel()));
         holders.register("world", profile.findLastLocation().getWorld().getName());
         holders.register(
            "spawn_world", profile.getRespawnLocationMap().hasCurrent() ? profile.getRespawnLocationMap().getCurrentWrapped().getWorldName() : "-"
         );
         List<PlaceholderRequestImpl> requests = profile.getPlaceholderCache().sendRequests(inv.playerData, profile);
         if (requests != null) {
            requests.forEach(req -> req.thenRun(() -> {
               if (profile.getPlaceholderCache().isOpen() && !inv.getNavigator().isClosed() && inv.getNavigator().peek().equals(inv)) {
                  inv.getNavigator().getLastBukkitOpened().setItem((Integer)this.getSlots().get(n), this.parent.getDisplayedItem(inv, n));
               }
            }));
         }

         profile.getPlaceholderCache().fillPlaceholders(holders);
         return holders;
      }
   }

   public class Generated extends GeneratedInventory {
      private final ProfileListImpl playerData;
      private final int maxPage;
      private final int perPage;
      private final int userSlots;
      private boolean removal = false;

      public Generated(Navigator navigator, ProfileListImpl playerData, EditableInventory editable) {
         super(navigator, editable);
         this.playerData = playerData;
         this.perPage = this.getEditable().getByFunction("profile").getSlots().size();
         this.userSlots = playerData.getProfileSlots();
         this.maxPage = (int)Math.ceil((double)MMOProfiles.plugin.configManager.maxSlots / this.perPage);
         navigator.blockClosing();
      }

      public long getCloseTimeOut() {
         return MMOProfiles.plugin.configManager.guiCloseTimeout;
      }

      public int getMaxPage() {
         return this.maxPage;
      }

      @NotNull
      public String getRawName() {
         return this.removal ? ProfileListViewer.this.removalName : this.guiName;
      }

      private void chooseProfile(@NotNull PlayerProfileImpl profile) {
         this.getNavigator().unblockClosing();
         VersionUtils.getOpen(this.player).close();
         this.playerData.applyProfile(profile);
      }
   }

   class LockedSlotItem extends SimpleItem<ProfileListViewer.Generated> {
      public LockedSlotItem(ConfigurationSection config) {
         super(config);
      }

      public Placeholders getPlaceholders(ProfileListViewer.Generated inv, int n) {
         int index = (inv.page - 1) * inv.perPage + n;
         Placeholders holders = new Placeholders();
         holders.register("rank", MMOProfiles.plugin.configManager.slotToRank[index].getName());
         return holders;
      }
   }

   public class ProfileItem extends InventoryItem<ProfileListViewer.Generated> {
      private final ProfileListViewer.ExistingProfileItem existingProfileItem;
      private final ProfileListViewer.EmptySlotItem emptySlotItem;
      private final ProfileListViewer.LockedSlotItem lockedSlotItem;
      @Nullable
      private final SimpleItem<ProfileListViewer.Generated> noSlotItem;
      private final boolean removeOnRightClick;

      public ProfileItem(ConfigurationSection config) {
         super(config);
         this.existingProfileItem = ProfileListViewer.this.new ExistingProfileItem(config, this);
         this.emptySlotItem = ProfileListViewer.this.new EmptySlotItem(config.getConfigurationSection("empty-slot"));
         this.lockedSlotItem = ProfileListViewer.this.new LockedSlotItem(config.getConfigurationSection("locked-slot"));
         this.noSlotItem = config.contains("no-slot") ? new SimpleItem(config.getConfigurationSection("no-slot")) : null;
         this.removeOnRightClick = config.getBoolean("remove_on_right_click", true);
      }

      public boolean hasDifferentDisplay() {
         return true;
      }

      public ItemStack getDisplayedItem(ProfileListViewer.Generated inv, int n) {
         int index = (inv.page - 1) * inv.perPage + n;
         int activeSlots = inv.playerData.getProfiles().size();
         if (index >= MMOProfiles.plugin.configManager.maxSlots) {
            return this.noSlotItem == null ? null : this.noSlotItem.getDisplayedItem(inv, n);
         } else if (index >= inv.userSlots) {
            return this.lockedSlotItem.getDisplayedItem(inv, n);
         } else if (index < activeSlots) {
            ItemStack stack = this.existingProfileItem.getDisplayedItem(inv, n);
            ItemMeta meta = stack.getItemMeta();
            meta.getPersistentDataContainer().set(ProfileListViewer.SLOT_NUMBER_KEY, PersistentDataType.INTEGER, index);
            stack.setItemMeta(meta);
            return stack;
         } else {
            ItemStack stack = this.emptySlotItem.getDisplayedItem(inv, n);
            ItemMeta meta = stack.getItemMeta();
            meta.getPersistentDataContainer().set(ProfileListViewer.EMPTY_SLOT_KEY, PersistentDataType.INTEGER, index);
            stack.setItemMeta(meta);
            return stack;
         }
      }

      public void onClick(@NotNull ProfileListViewer.Generated inv, @NotNull InventoryClickEvent event) {
         PersistentDataContainer pdc = event.getCurrentItem().getItemMeta().getPersistentDataContainer();
         if (pdc.has(ProfileListViewer.EMPTY_SLOT_KEY)) {
            inv.getNavigator().unblockClosing();
            if (MMOProfiles.plugin.configManager.profileNames) {
               Message.SET_PROFILE_NAME_ASK.format().send(inv.getPlayer());
               new ChatInput(inv.playerData, inv, (chatInput, input) -> {
                  for (PlayerProfileImpl prof : inv.playerData.getProfiles()) {
                     if (prof.getName().equals(input)) {
                        Message.PROFILE_WITH_SAME_NAME.format().send(inv.getPlayer());
                        return false;
                     }
                  }

                  chatInput.killInventory();
                  inv.playerData.createProfile(input).thenAccept(UtilityMethods.sync(MMOProfiles.plugin, inv.playerData::applyProfile));
                  return true;
               }, chatInput -> {
                  inv.getNavigator().blockClosing();
                  inv.getNavigator().openLast();
               });
            } else {
               inv.playerData
                  .createProfile("Profile n" + (inv.playerData.getProfiles().size() + 1))
                  .thenAccept(UtilityMethods.sync(MMOProfiles.plugin, x$0 -> inv.chooseProfile(x$0)));
            }
         } else {
            Integer index = (Integer)pdc.get(ProfileListViewer.SLOT_NUMBER_KEY, PersistentDataType.INTEGER);
            if (index != null) {
               List<PlayerProfileImpl> playerProfiles = inv.playerData.getProfiles();
               PlayerProfileImpl playerProfile = playerProfiles.get(index);
               if (!inv.removal && (!this.removeOnRightClick || event.getClick() != ClickType.RIGHT)) {
                  if (event.getClick() == ClickType.LEFT || !this.removeOnRightClick) {
                     inv.chooseProfile(playerProfile);
                  }
               } else {
                  InventoryManager.CONFIRM_DELETION.generate(inv, inv.playerData, playerProfile).open();
               }
            }
         }
      }
   }

   class RemovalItem extends SimpleItem<ProfileListViewer.Generated> {
      public RemovalItem(ConfigurationSection config) {
         super(config);
      }

      public void onClick(@NotNull ProfileListViewer.Generated inv, @NotNull InventoryClickEvent event) {
         inv.removal = !inv.removal;
         inv.open();
      }
   }
}
