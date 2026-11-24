package net.Indyuce.mmocore.gui.social.party;

import io.lumine.mythic.lib.gui.Navigator;
import io.lumine.mythic.lib.gui.editable.EditableInventory;
import io.lumine.mythic.lib.gui.editable.GeneratedInventory;
import io.lumine.mythic.lib.gui.editable.item.InventoryItem;
import io.lumine.mythic.lib.gui.editable.item.PhysicalItem;
import io.lumine.mythic.lib.gui.editable.item.SimpleItem;
import io.lumine.mythic.lib.gui.editable.placeholder.Placeholders;
import java.util.UUID;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.util.input.ChatInput;
import net.Indyuce.mmocore.api.util.input.PlayerInput;
import net.Indyuce.mmocore.api.util.math.format.DelayFormat;
import net.Indyuce.mmocore.party.provided.Party;
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

public class EditablePartyView extends EditableInventory {
   private static final NamespacedKey UUID_NAMESPACEDKEY = new NamespacedKey(MMOCore.plugin, "Uuid");

   public EditablePartyView() {
      super("party-view");
   }

   @Nullable
   public InventoryItem<?> resolveItem(@NotNull String function, @NotNull ConfigurationSection config) {
      if (function.equalsIgnoreCase("member")) {
         return new EditablePartyView.MemberItem(config);
      } else if (function.equalsIgnoreCase("leave")) {
         return new EditablePartyView.LeaveButton(config);
      } else {
         return function.equalsIgnoreCase("invite") ? new EditablePartyView.InviteItem(config) : null;
      }
   }

   public GeneratedInventory newInventory(PlayerData data) {
      return new EditablePartyView.PartyViewInventory(data);
   }

   public class InviteItem extends SimpleItem<EditablePartyView.PartyViewInventory> {
      public InviteItem(ConfigurationSection config) {
         super(config);
      }

      public void onClick(@NotNull EditablePartyView.PartyViewInventory inv, @NotNull InventoryClickEvent event) {
         Party party = (Party)inv.playerData.getParty();
         if (party.getMembers().size() >= MMOCore.plugin.configManager.maxPartyPlayers) {
            ConfigMessage.fromKey("party-is-full").send(inv.playerData);
            inv.getPlayer().playSound(inv.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
         } else {
            new ChatInput(
               inv.getPlayer(),
               PlayerInput.InputType.PARTY_INVITE,
               inv,
               input -> {
                  Player target = Bukkit.getPlayer(input);
                  if (target == null) {
                     ConfigMessage.fromKey("not-online-player", "player", input).send(inv.playerData);
                     inv.getPlayer().playSound(inv.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                  } else {
                     long remaining = party.getLastInvite(target) + 120000L - System.currentTimeMillis();
                     if (remaining > 0L) {
                        ConfigMessage.fromKey("party-invite-cooldown", "player", target.getName(), "cooldown", new DelayFormat().format(remaining))
                           .send(inv.playerData);
                     } else {
                        PlayerData targetData = PlayerData.get(target);
                        if (party.hasMember(target)) {
                           ConfigMessage.fromKey("already-in-party", "player", target.getName()).send(inv.playerData);
                           inv.getPlayer().playSound(inv.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                        } else {
                           int levelDifference = Math.abs(targetData.getLevel() - party.getLevel());
                           if (levelDifference > MMOCore.plugin.configManager.maxPartyLevelDifference) {
                              ConfigMessage.fromKey("high-level-difference", "player", target.getName(), "diff", String.valueOf(levelDifference))
                                 .send(inv.playerData);
                              inv.getPlayer().playSound(inv.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                           } else {
                              party.sendInvite(inv.playerData, targetData);
                              ConfigMessage.fromKey("sent-party-invite", "player", target.getName()).send(inv.playerData);
                              inv.getPlayer().playSound(inv.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
                              inv.open();
                           }
                        }
                     }
                  }
               }
            );
         }
      }
   }

   public class LeaveButton extends SimpleItem<EditablePartyView.PartyViewInventory> {
      public LeaveButton(ConfigurationSection config) {
         super(config);
      }

      public void onClick(@NotNull EditablePartyView.PartyViewInventory inv, @NotNull InventoryClickEvent event) {
         Party party = (Party)inv.playerData.getParty();
         party.removeMember(inv.playerData);
         inv.getPlayer().playSound(inv.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
         inv.getPlayer().closeInventory();
      }
   }

   public static class MemberDisplayItem extends PhysicalItem<EditablePartyView.PartyViewInventory> {
      public MemberDisplayItem(EditablePartyView.MemberItem memberItem, ConfigurationSection config) {
         super(memberItem, config);
      }

      public boolean hasDifferentDisplay() {
         return true;
      }

      public Placeholders getPlaceholders(EditablePartyView.PartyViewInventory inv, int n) {
         Party party = (Party)inv.playerData.getParty();
         PlayerData member = party.getMembers().get(n);
         Placeholders holders = new Placeholders();
         if (member.isOnline()) {
            holders.register("name", member.getPlayer().getName());
         }

         holders.register("class", member.getProfess().getName());
         holders.register("level", member.getLevel());
         holders.register("since", new DelayFormat(2).format(System.currentTimeMillis() - member.getLastLogin()));
         return holders;
      }

      @NotNull
      public Player getEffectivePlayer(EditablePartyView.PartyViewInventory inv, int n) {
         return ((Party)inv.playerData.getParty()).getMembers().get(n).getPlayer();
      }

      public ItemStack getDisplayedItem(EditablePartyView.PartyViewInventory inv, int n) {
         Player member = this.getEffectivePlayer(inv, n);
         ItemStack disp = super.getDisplayedItem(inv, n);
         ItemMeta meta = disp.getItemMeta();
         meta.getPersistentDataContainer().set(EditablePartyView.UUID_NAMESPACEDKEY, PersistentDataType.STRING, member.getUniqueId().toString());
         if (meta instanceof SkullMeta) {
            inv.asyncUpdate(this, n, disp, current -> {
               ((SkullMeta)meta).setOwningPlayer(member);
               current.setItemMeta(meta);
            });
         }

         disp.setItemMeta(meta);
         return disp;
      }
   }

   public static class MemberItem extends InventoryItem<EditablePartyView.PartyViewInventory> {
      private final InventoryItem<EditablePartyView.PartyViewInventory> empty;
      private final EditablePartyView.MemberDisplayItem member;

      public MemberItem(ConfigurationSection config) {
         super(config);
         Validate.notNull(config.contains("empty"), "Could not load empty config");
         Validate.notNull(config.contains("member"), "Could not load member config");
         this.empty = new SimpleItem(config.getConfigurationSection("empty"));
         this.member = new EditablePartyView.MemberDisplayItem(this, config.getConfigurationSection("member"));
      }

      public ItemStack getDisplayedItem(EditablePartyView.PartyViewInventory inv, int n) {
         Party party = (Party)inv.playerData.getParty();
         return party.getMembers().size() > n ? this.member.getDisplayedItem(inv, n) : this.empty.getDisplayedItem(inv, n);
      }

      public boolean hasDifferentDisplay() {
         return true;
      }

      public void onClick(@NotNull EditablePartyView.PartyViewInventory inv, @NotNull InventoryClickEvent event) {
         Party party = (Party)inv.playerData.getParty();
         if (party.getOwner().equals(inv.playerData)) {
            String uuidTag = (String)event.getCurrentItem()
               .getItemMeta()
               .getPersistentDataContainer()
               .get(EditablePartyView.UUID_NAMESPACEDKEY, PersistentDataType.STRING);
            if (uuidTag != null && !uuidTag.isEmpty()) {
               OfflinePlayer target = Bukkit.getOfflinePlayer(UUID.fromString(uuidTag));
               if (!target.equals(inv.getPlayer())) {
                  party.removeMember(PlayerData.get(target));
                  ConfigMessage.fromKey("kick-from-party", "player", target.getName()).send(inv.playerData);
                  inv.getPlayer().playSound(inv.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
               }
            }
         }
      }
   }

   public class PartyViewInventory extends GeneratedInventory {
      private final PlayerData playerData;

      public PartyViewInventory(PlayerData playerData) {
         super(new Navigator(playerData.getMMOPlayerData()), EditablePartyView.this);
         this.playerData = playerData;
      }

      @NotNull
      public String getRawName() {
         Party party = (Party)this.playerData.getParty();
         return this.guiName
            .replace("{max}", String.valueOf(MMOCore.plugin.configManager.maxPartyPlayers))
            .replace("{players}", String.valueOf(party.getMembers().size()));
      }
   }
}
