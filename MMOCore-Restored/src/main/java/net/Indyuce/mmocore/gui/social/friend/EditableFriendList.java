package net.Indyuce.mmocore.gui.social.friend;

import io.lumine.mythic.lib.gui.Navigator;
import io.lumine.mythic.lib.gui.editable.EditableInventory;
import io.lumine.mythic.lib.gui.editable.GeneratedInventory;
import io.lumine.mythic.lib.gui.editable.item.InventoryItem;
import io.lumine.mythic.lib.gui.editable.item.PhysicalItem;
import io.lumine.mythic.lib.gui.editable.item.SimpleItem;
import io.lumine.mythic.lib.gui.editable.item.builtin.NextPageItem;
import io.lumine.mythic.lib.gui.editable.item.builtin.PreviousPageItem;
import io.lumine.mythic.lib.gui.editable.placeholder.ErrorPlaceholders;
import io.lumine.mythic.lib.gui.editable.placeholder.Placeholders;
import java.util.UUID;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.api.player.PlayerActivity;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.util.MMOCoreUtils;
import net.Indyuce.mmocore.api.util.input.ChatInput;
import net.Indyuce.mmocore.api.util.input.PlayerInput;
import net.Indyuce.mmocore.api.util.math.format.DelayFormat;
import net.Indyuce.mmocore.manager.InventoryManager;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EditableFriendList extends EditableInventory {
   private static final NamespacedKey UUID_NAMESPACEDKEY = new NamespacedKey(MMOCore.plugin, "Uuid");

   public EditableFriendList() {
      super("friend-list");
   }

   @Nullable
   public InventoryItem<?> resolveItem(@NotNull String function, @NotNull ConfigurationSection config) {
      if (function.equals("friend")) {
         return new EditableFriendList.FriendItem(config);
      } else if (function.equals("previous")) {
         return new PreviousPageItem(config);
      } else if (function.equals("next")) {
         return new NextPageItem(config);
      } else {
         return function.equals("request") ? new EditableFriendList.RequestItem(config) : null;
      }
   }

   public GeneratedInventory newInventory(PlayerData data) {
      return new EditableFriendList.FriendListInventory(data);
   }

   class FriendItem extends SimpleItem<EditableFriendList.FriendListInventory> {
      private final EditableFriendList.OnlineFriendItem online;
      private final EditableFriendList.OfflineFriendItem offline;

      public FriendItem(ConfigurationSection config) {
         super(config);
         Validate.notNull(config.contains("online"), "Could not load online config");
         Validate.notNull(config.contains("offline"), "Could not load offline config");
         this.online = EditableFriendList.this.new OnlineFriendItem(this, config.getConfigurationSection("online"));
         this.offline = EditableFriendList.this.new OfflineFriendItem(this, config.getConfigurationSection("offline"));
      }

      public ItemStack getDisplayedItem(EditableFriendList.FriendListInventory inv, int n) {
         if (inv.playerData.getFriends().size() <= n) {
            return super.getDisplayedItem(inv, n);
         }

         OfflinePlayer friend = Bukkit.getOfflinePlayer(inv.playerData.getFriends().get(n));
         ItemStack disp = (friend.isOnline() ? this.online : this.offline).getDisplayedItem(inv, n);
         ItemMeta meta = disp.getItemMeta();
         meta.getPersistentDataContainer().set(EditableFriendList.UUID_NAMESPACEDKEY, PersistentDataType.STRING, friend.getUniqueId().toString());
         if (meta instanceof SkullMeta) {
            inv.asyncUpdate(this, n, disp, current -> {
               ((SkullMeta)meta).setOwningPlayer(friend);
               current.setItemMeta(meta);
            });
         }

         disp.setItemMeta(meta);
         return disp;
      }

      public boolean hasDifferentDisplay() {
         return true;
      }

      public void onClick(@NotNull EditableFriendList.FriendListInventory inv, @NotNull InventoryClickEvent event) {
         if (event.getClick() == ClickType.RIGHT) {
            String tag = (String)event.getCurrentItem()
               .getItemMeta()
               .getPersistentDataContainer()
               .get(EditableFriendList.UUID_NAMESPACEDKEY, PersistentDataType.STRING);
            if (tag != null && !tag.isEmpty()) {
               InventoryManager.FRIEND_REMOVAL.newInventory(inv, Bukkit.getOfflinePlayer(UUID.fromString(tag))).open();
            }
         }
      }
   }

   class FriendListInventory extends GeneratedInventory {
      private final PlayerData playerData;

      public FriendListInventory(PlayerData playerData) {
         super(new Navigator(playerData.getMMOPlayerData()), EditableFriendList.this);
         this.playerData = playerData;
      }

      public PlayerData getPlayerData() {
         return this.playerData;
      }
   }

   class OfflineFriendItem extends PhysicalItem<EditableFriendList.FriendListInventory> {
      public OfflineFriendItem(EditableFriendList.FriendItem parent, ConfigurationSection config) {
         super(parent, config);
      }

      public boolean hasDifferentDisplay() {
         return true;
      }

      @NotNull
      public OfflinePlayer getEffectivePlayer(EditableFriendList.FriendListInventory inv, int n) {
         return Bukkit.getOfflinePlayer(inv.playerData.getFriends().get(n));
      }

      @NotNull
      public Placeholders getPlaceholders(EditableFriendList.FriendListInventory inv, int n) {
         OfflinePlayer friend = this.getEffectivePlayer(inv, n);
         if (MMOCoreUtils.isInvalid(friend)) {
            return new ErrorPlaceholders();
         }

         Placeholders holders = new Placeholders();
         holders.register("name", friend.getName());
         holders.register("last_seen", new DelayFormat(2).format(System.currentTimeMillis() - friend.getLastPlayed()));
         return holders;
      }
   }

   class OnlineFriendItem extends SimpleItem<EditableFriendList.FriendListInventory> {
      public OnlineFriendItem(EditableFriendList.FriendItem parent, ConfigurationSection config) {
         super(parent, config);
      }

      public boolean hasDifferentDisplay() {
         return true;
      }

      @NotNull
      public OfflinePlayer getEffectivePlayer(EditableFriendList.FriendListInventory inv, int n) {
         return Bukkit.getOfflinePlayer(inv.playerData.getFriends().get(n));
      }

      @NotNull
      public Placeholders getPlaceholders(EditableFriendList.FriendListInventory inv, int n) {
         PlayerData friendData = PlayerData.get(this.getEffectivePlayer(inv, n));
         Placeholders holders = new Placeholders();
         if (friendData.isOnline()) {
            holders.register("name", friendData.getPlayer().getName());
         }

         holders.register("class", friendData.getProfess().getName());
         holders.register("level", friendData.getLevel());
         holders.register("online_since", new DelayFormat(2).format(System.currentTimeMillis() - friendData.getLastLogin()));
         return holders;
      }
   }

   class RequestItem extends SimpleItem<EditableFriendList.FriendListInventory> {
      public RequestItem(ConfigurationSection config) {
         super(config);
      }

      public void onClick(@NotNull EditableFriendList.FriendListInventory inv, @NotNull InventoryClickEvent event) {
         long remaining = inv.playerData.getActivityTimeOut(PlayerActivity.FRIEND_REQUEST);
         if (remaining > 0L) {
            ConfigMessage.fromKey("friend-request-cooldown", "cooldown", new DelayFormat().format(remaining)).send(inv.playerData);
         } else {
            new ChatInput(inv.getPlayer(), PlayerInput.InputType.FRIEND_REQUEST, inv, input -> {
               Player target = Bukkit.getPlayer(input);
               if (target == null) {
                  ConfigMessage.fromKey("not-online-player", "player", input).send(inv.playerData);
                  inv.getPlayer().playSound(inv.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                  inv.open();
               } else if (inv.playerData.hasFriend(target.getUniqueId())) {
                  ConfigMessage.fromKey("already-friends", "player", target.getName()).send(inv.playerData);
                  inv.getPlayer().playSound(inv.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                  inv.open();
               } else if (inv.playerData.getUniqueId().equals(target.getUniqueId())) {
                  ConfigMessage.fromKey("cant-request-to-yourself").send(inv.playerData);
                  inv.getPlayer().playSound(inv.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                  inv.open();
               } else {
                  inv.playerData.sendFriendRequest(PlayerData.get(target));
                  ConfigMessage.fromKey("sent-friend-request", "player", target.getName()).send(inv.playerData);
                  inv.getPlayer().playSound(inv.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                  inv.open();
               }
            });
         }
      }
   }
}
