package net.Indyuce.mmocore.gui.social.guild;

import io.lumine.mythic.lib.gui.Navigator;
import io.lumine.mythic.lib.gui.editable.EditableInventory;
import io.lumine.mythic.lib.gui.editable.GeneratedInventory;
import io.lumine.mythic.lib.gui.editable.item.InventoryItem;
import io.lumine.mythic.lib.gui.editable.item.PhysicalItem;
import io.lumine.mythic.lib.gui.editable.item.SimpleItem;
import io.lumine.mythic.lib.gui.editable.item.builtin.NextPageItem;
import io.lumine.mythic.lib.gui.editable.item.builtin.PreviousPageItem;
import io.lumine.mythic.lib.gui.editable.placeholder.Placeholders;
import java.util.List;
import java.util.UUID;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.util.MMOCoreUtils;
import net.Indyuce.mmocore.api.util.input.ChatInput;
import net.Indyuce.mmocore.api.util.input.PlayerInput;
import net.Indyuce.mmocore.api.util.math.format.DelayFormat;
import net.Indyuce.mmocore.manager.data.OfflinePlayerData;
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

public class EditableGuildView extends EditableInventory {
   private static final NamespacedKey UUID_NAMESPACEDKEY = new NamespacedKey(MMOCore.plugin, "Uuid");

   public EditableGuildView() {
      super("guild-view");
   }

   @Nullable
   public InventoryItem<?> resolveItem(@NotNull String function, @NotNull ConfigurationSection config) {
      if (function.equalsIgnoreCase("member")) {
         return new EditableGuildView.MemberItem(config);
      } else if (function.equalsIgnoreCase("next")) {
         return new NextPageItem(config);
      } else if (function.equalsIgnoreCase("previous")) {
         return new PreviousPageItem(config);
      } else if (function.equalsIgnoreCase("disband")) {
         return new EditableGuildView.DisbandItem(config);
      } else if (function.equalsIgnoreCase("invite")) {
         return new EditableGuildView.InviteItem(config);
      } else {
         return function.equalsIgnoreCase("leave") ? new EditableGuildView.LeaveItem(config) : null;
      }
   }

   public GeneratedInventory newInventory(PlayerData data) {
      return new EditableGuildView.GuildViewInventory(data);
   }

   public class DisbandItem extends SimpleItem<EditableGuildView.GuildViewInventory> {
      public DisbandItem(ConfigurationSection config) {
         super(config);
      }

      public void onClick(@NotNull EditableGuildView.GuildViewInventory inv, @NotNull InventoryClickEvent event) {
         if (inv.isGuildOwner) {
            MMOCore.plugin.nativeGuildManager.unregisterGuild(inv.playerData.getGuild());
            inv.getPlayer().playSound(inv.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
            inv.getPlayer().closeInventory();
         }
      }

      public boolean isDisplayed(@NotNull EditableGuildView.GuildViewInventory inv) {
         return inv.isGuildOwner;
      }
   }

   public class GuildViewInventory extends GeneratedInventory {
      private final int maxpages;
      private final PlayerData playerData;
      private final boolean isGuildOwner;
      private List<UUID> members;

      public GuildViewInventory(PlayerData playerData) {
         super(new Navigator(playerData.getMMOPlayerData()), EditableGuildView.this);
         this.maxpages = (playerData.getGuild().countMembers() + 20) / this.getEditable().getByFunction("member").getSlots().size();
         this.playerData = playerData;
         this.isGuildOwner = playerData.getGuild().getOwner().equals(playerData.getUniqueId());
         this.enablePagination(EditableGuildView.this.getByFunction("member").getSlots().size());
      }

      public void open() {
         this.members = this.playerData.getGuild().listMembers();
         super.open();
      }

      public int getMaxPage() {
         return this.computeMaxPage(this.members.size());
      }

      @NotNull
      public String getRawName() {
         return this.guiName
            .replace("{online_players}", String.valueOf(this.playerData.getGuild().countOnlineMembers()))
            .replace("{page}", String.valueOf(this.page))
            .replace("{maxpages}", String.valueOf(this.maxpages))
            .replace("{players}", String.valueOf(this.playerData.getGuild().countMembers()))
            .replace("{tag}", this.playerData.getGuild().getTag())
            .replace("{name}", this.playerData.getGuild().getName());
      }
   }

   public class InviteItem extends SimpleItem<EditableGuildView.GuildViewInventory> {
      public InviteItem(ConfigurationSection config) {
         super(config);
      }

      public boolean isDisplayed(@NotNull EditableGuildView.GuildViewInventory inv) {
         return inv.isGuildOwner;
      }

      public void onClick(@NotNull EditableGuildView.GuildViewInventory inv, @NotNull InventoryClickEvent event) {
         if (inv.isGuildOwner) {
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
                        if (inv.playerData.getGuild().hasMember(targetData.getUniqueId())) {
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

   public class LeaveItem extends SimpleItem<EditableGuildView.GuildViewInventory> {
      public LeaveItem(ConfigurationSection config) {
         super(config);
      }

      public void onClick(@NotNull EditableGuildView.GuildViewInventory inv, @NotNull InventoryClickEvent event) {
         inv.playerData.getGuild().removeMember(inv.playerData.getUniqueId());
         inv.getPlayer().playSound(inv.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
         inv.getPlayer().closeInventory();
      }
   }

   public static class MemberDisplayItem extends PhysicalItem<EditableGuildView.GuildViewInventory> {
      public MemberDisplayItem(EditableGuildView.MemberItem memberItem, ConfigurationSection config) {
         super(memberItem, config);
      }

      public boolean hasDifferentDisplay() {
         return true;
      }

      @NotNull
      public OfflinePlayer getEffectivePlayer(EditableGuildView.GuildViewInventory inv, int n) {
         return Bukkit.getOfflinePlayer(inv.members.get(n));
      }

      @NotNull
      public Placeholders getPlaceholders(EditableGuildView.GuildViewInventory inv, int n) {
         UUID uuid = inv.members.get(n);
         OfflinePlayer player = this.getEffectivePlayer(inv, n);
         Placeholders holders = new Placeholders();
         OfflinePlayerData offline = OfflinePlayerData.get(uuid);
         holders.register("name", MMOCoreUtils.isInvalid(player) ? "???" : player.getName());
         holders.register("class", offline.getProfess().getName());
         holders.register("level", offline.getLevel());
         holders.register("since", new DelayFormat(2).format(System.currentTimeMillis() - offline.getLastLogin()));
         return holders;
      }

      public ItemStack getDisplayedItem(EditableGuildView.GuildViewInventory inv, int n) {
         UUID uuid = inv.members.get(n);
         ItemStack disp = super.getDisplayedItem(inv, n);
         ItemMeta meta = disp.getItemMeta();
         meta.getPersistentDataContainer().set(EditableGuildView.UUID_NAMESPACEDKEY, PersistentDataType.STRING, uuid.toString());
         if (meta instanceof SkullMeta) {
            inv.asyncUpdate(this, n, disp, current -> {
               ((SkullMeta)meta).setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
               current.setItemMeta(meta);
            });
         }

         disp.setItemMeta(meta);
         return disp;
      }

      public void onClick(@NotNull EditableGuildView.GuildViewInventory inv, @NotNull InventoryClickEvent event) {
         if (inv.isGuildOwner) {
            if (event.getClick() == ClickType.RIGHT) {
               String tag = (String)event.getCurrentItem()
                  .getItemMeta()
                  .getPersistentDataContainer()
                  .get(EditableGuildView.UUID_NAMESPACEDKEY, PersistentDataType.STRING);
               if (tag != null && !tag.isEmpty()) {
                  OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(tag));
                  if (!target.equals(inv.getPlayer())) {
                     inv.playerData.getGuild().removeMember(target.getUniqueId());
                     ConfigMessage.fromKey("kick-from-guild", "player", target.getName()).send(inv.playerData);
                     inv.getPlayer().playSound(inv.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                  }
               }
            }
         }
      }
   }

   public class MemberItem extends SimpleItem<EditableGuildView.GuildViewInventory> {
      private final InventoryItem<EditableGuildView.GuildViewInventory> empty;
      private final EditableGuildView.MemberDisplayItem member;

      public MemberItem(ConfigurationSection config) {
         super(config);
         Validate.notNull(config.contains("empty"), "Could not load empty config");
         Validate.notNull(config.contains("member"), "Could not load member config");
         this.empty = new SimpleItem(config.getConfigurationSection("empty"));
         this.member = new EditableGuildView.MemberDisplayItem(this, config.getConfigurationSection("member"));
      }

      public ItemStack getDisplayedItem(EditableGuildView.GuildViewInventory inv, int n) {
         int index = inv.getPageIndex(n);
         return inv.playerData.getGuild().countMembers() > index ? this.member.getDisplayedItem(inv, index) : this.empty.getDisplayedItem(inv, index);
      }

      public boolean hasDifferentDisplay() {
         return true;
      }
   }
}
