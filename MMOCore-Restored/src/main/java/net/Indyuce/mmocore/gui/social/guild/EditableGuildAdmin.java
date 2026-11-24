package net.Indyuce.mmocore.gui.social.guild;

import io.lumine.mythic.lib.gui.Navigator;
import io.lumine.mythic.lib.gui.editable.EditableInventory;
import io.lumine.mythic.lib.gui.editable.GeneratedInventory;
import io.lumine.mythic.lib.gui.editable.item.InventoryItem;
import io.lumine.mythic.lib.gui.editable.item.PhysicalItem;
import io.lumine.mythic.lib.gui.editable.item.SimpleItem;
import io.lumine.mythic.lib.gui.editable.placeholder.Placeholders;
import java.util.List;
import java.util.UUID;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.util.input.ChatInput;
import net.Indyuce.mmocore.api.util.input.PlayerInput;
import net.Indyuce.mmocore.api.util.math.format.DelayFormat;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class EditableGuildAdmin extends EditableInventory {
   private static final NamespacedKey UUID_NAMESPACEDKEY = new NamespacedKey(MMOCore.plugin, "Uuid");

   public EditableGuildAdmin() {
      super("guild-admin");
   }

   @Nullable
   public InventoryItem<?> resolveItem(@NotNull String function, @NotNull ConfigurationSection config) {
      if (function.equalsIgnoreCase("member")) {
         return new EditableGuildAdmin.MemberItem(config);
      } else if (function.equalsIgnoreCase("leave")) {
         return new EditableGuildAdmin.LeaveItem(config);
      } else {
         return function.equalsIgnoreCase("invite") ? new EditableGuildAdmin.InviteItem(config) : null;
      }
   }

   public GeneratedInventory newInventory(PlayerData data) {
      return new EditableGuildAdmin.GuildViewInventory(data);
   }

   public class GuildViewInventory extends GeneratedInventory {
      private final int max = this.getEditable().getByFunction("member").getSlots().size();
      private final PlayerData playerData;
      private final boolean isGuildOwner;
      private List<UUID> members;

      public GuildViewInventory(PlayerData playerData) {
         super(new Navigator(playerData.getMMOPlayerData()), EditableGuildAdmin.this);
         this.playerData = playerData;
         this.isGuildOwner = playerData.getGuild().getOwner().equals(playerData.getUniqueId());
      }

      public void open() {
         this.members = this.playerData.getGuild().listMembers();
         super.open();
      }

      @NotNull
      public String getRawName() {
         return this.guiName.replace("{max}", String.valueOf(this.max)).replace("{players}", String.valueOf(this.playerData.getGuild().countMembers()));
      }
   }

   public class InviteItem extends SimpleItem<EditableGuildAdmin.GuildViewInventory> {
      public InviteItem(ConfigurationSection config) {
         super(config);
      }

      public void onClick(@NotNull EditableGuildAdmin.GuildViewInventory inv, @NotNull InventoryClickEvent event) {
         if (inv.playerData.getGuild().countMembers() >= inv.max) {
            ConfigMessage.fromKey("guild-is-full").send(inv.playerData);
            inv.getPlayer().playSound(inv.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
         } else {
            new ChatInput(
               inv.getPlayer(),
               PlayerInput.InputType.GUILD_INVITE,
               inv,
               input -> {
                  Player target = Bukkit.getPlayer(input);
                  if (target == null) {
                     ConfigMessage.fromKey("not-online-player", "player", input).send(inv.playerData);
                     inv.getPlayer().playSound(inv.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                     inv.open();
                  } else {
                     long remaining = inv.playerData.getGuild().getLastInvite(target) + 120000L - System.currentTimeMillis();
                     if (remaining > 0L) {
                        ConfigMessage.fromKey("guild-invite-cooldown", "player", target.getName(), "cooldown", new DelayFormat().format(remaining))
                           .send(inv.playerData);
                        inv.open();
                     } else {
                        PlayerData targetData = PlayerData.get(target);
                        if (inv.playerData.getGuild().hasMember(target.getUniqueId())) {
                           ConfigMessage.fromKey("already-in-guild", "player", target.getName()).send(inv.playerData);
                           inv.getPlayer().playSound(inv.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                           inv.open();
                        } else {
                           inv.playerData.getGuild().sendGuildInvite(inv.playerData, targetData);
                           ConfigMessage.fromKey("sent-guild-invite", "player", target.getName()).send(inv.playerData);
                           inv.getPlayer().playSound(inv.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                           inv.open();
                        }
                     }
                  }
               }
            );
         }
      }
   }

   public class LeaveItem extends SimpleItem<EditableGuildAdmin.GuildViewInventory> {
      public LeaveItem(ConfigurationSection config) {
         super(config);
      }

      public void onClick(@NotNull EditableGuildAdmin.GuildViewInventory inv, @NotNull InventoryClickEvent event) {
         inv.playerData.getGuild().removeMember(inv.playerData.getUniqueId());
         inv.getPlayer().playSound(inv.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
         inv.getPlayer().closeInventory();
      }
   }

   public static class MemberDisplayItem extends PhysicalItem<EditableGuildAdmin.GuildViewInventory> {
      public MemberDisplayItem(EditableGuildAdmin.MemberItem memberItem, ConfigurationSection config) {
         super(memberItem, config);
      }

      public boolean hasDifferentDisplay() {
         return true;
      }

      public Placeholders getPlaceholders(EditableGuildAdmin.GuildViewInventory inv, int n) {
         PlayerData member = PlayerData.get(inv.members.get(n));
         Placeholders holders = new Placeholders();
         if (member.isOnline()) {
            holders.register("name", member.getPlayer().getName());
         }

         holders.register("class", member.getProfess().getName());
         holders.register("level", member.getLevel() + "");
         holders.register("since", new DelayFormat(2).format(System.currentTimeMillis() - member.getLastLogin()));
         return holders;
      }

      public ItemStack getDisplayedItem(EditableGuildAdmin.GuildViewInventory inv, int n) {
         UUID uuid = inv.members.get(n);
         OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
         ItemStack disp = super.getDisplayedItem(inv, n);
         ItemMeta meta = disp.getItemMeta();
         meta.getPersistentDataContainer().set(EditableGuildAdmin.UUID_NAMESPACEDKEY, PersistentDataType.STRING, uuid.toString());
         if (meta instanceof SkullMeta && offlinePlayer != null) {
            inv.asyncUpdate(this, n, disp, current -> {
               ((SkullMeta)meta).setOwningPlayer(offlinePlayer);
               current.setItemMeta(meta);
            });
         }

         disp.setItemMeta(meta);
         return disp;
      }
   }

   public static class MemberItem extends InventoryItem<EditableGuildAdmin.GuildViewInventory> {
      private final InventoryItem<EditableGuildAdmin.GuildViewInventory> empty;
      private final EditableGuildAdmin.MemberDisplayItem member;

      public MemberItem(ConfigurationSection config) {
         super(config);
         Validate.notNull(config.contains("empty"), "Could not load empty config");
         Validate.notNull(config.contains("member"), "Could not load member config");
         this.empty = new SimpleItem(config.getConfigurationSection("empty"));
         this.member = new EditableGuildAdmin.MemberDisplayItem(this, config.getConfigurationSection("member"));
      }

      public ItemStack getDisplayedItem(EditableGuildAdmin.GuildViewInventory inv, int n) {
         return inv.playerData.getGuild().countMembers() > n ? this.member.getDisplayedItem(inv, n) : this.empty.getDisplayedItem(inv, n);
      }

      public boolean hasDifferentDisplay() {
         return true;
      }

      public void onClick(@NotNull EditableGuildAdmin.GuildViewInventory inv, @NotNull InventoryClickEvent event) {
         if (inv.isGuildOwner) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(
               UUID.fromString(
                  (String)event.getCurrentItem()
                     .getItemMeta()
                     .getPersistentDataContainer()
                     .get(EditableGuildAdmin.UUID_NAMESPACEDKEY, PersistentDataType.STRING)
               )
            );
            if (!target.equals(inv.getPlayer())) {
               inv.playerData.getGuild().removeMember(target.getUniqueId());
               ConfigMessage.fromKey("kick-from-guild", "player", target.getName()).send(inv.playerData);
               inv.getPlayer().playSound(inv.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
            }
         }
      }
   }
}
