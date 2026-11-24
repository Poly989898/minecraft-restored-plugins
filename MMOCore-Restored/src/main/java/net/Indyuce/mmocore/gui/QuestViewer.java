package net.Indyuce.mmocore.gui;

import io.lumine.mythic.lib.gui.Navigator;
import io.lumine.mythic.lib.gui.editable.EditableInventory;
import io.lumine.mythic.lib.gui.editable.GeneratedInventory;
import io.lumine.mythic.lib.gui.editable.item.InventoryItem;
import io.lumine.mythic.lib.gui.editable.item.PhysicalItem;
import io.lumine.mythic.lib.gui.editable.item.SimpleItem;
import io.lumine.mythic.lib.gui.editable.item.builtin.NextPageItem;
import io.lumine.mythic.lib.gui.editable.item.builtin.PreviousPageItem;
import io.lumine.mythic.lib.gui.editable.placeholder.Placeholders;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigMessage;
import net.Indyuce.mmocore.api.SoundEvent;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.quest.Quest;
import net.Indyuce.mmocore.api.util.math.format.DelayFormat;
import net.Indyuce.mmocore.experience.Profession;
import org.apache.commons.lang.Validate;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuestViewer extends EditableInventory {
   private static final NamespacedKey QUEST_ID_KEY = new NamespacedKey(MMOCore.plugin, "quest_id");

   public QuestViewer() {
      super("quest-list");
   }

   @Nullable
   public InventoryItem<?> resolveItem(@NotNull String function, @NotNull ConfigurationSection config) {
      if (function.equals("quest")) {
         return new QuestViewer.QuestItem(config);
      } else if (function.equals("previous")) {
         return new PreviousPageItem(config);
      } else {
         return function.equals("next") ? new NextPageItem(config) : null;
      }
   }

   public GeneratedInventory newInventory(PlayerData data) {
      return new QuestViewer.QuestInventory(data);
   }

   public class QuestInventory extends GeneratedInventory {
      private final List<Quest> quests = new ArrayList<>(MMOCore.plugin.questManager.getAll());
      private final PlayerData playerData;

      public QuestInventory(PlayerData playerData) {
         super(new Navigator(playerData.getMMOPlayerData()), QuestViewer.this);
         this.playerData = playerData;
         this.enablePagination(QuestViewer.this.getByFunction("quest").getSlots().size());
      }

      public int getMaxPage() {
         return this.computeMaxPage(this.quests.size());
      }
   }

   public class QuestItem extends PhysicalItem<QuestViewer.QuestInventory> {
      private final SimpleItem<QuestViewer.QuestInventory> noQuest;
      private final SimpleItem<QuestViewer.QuestInventory> locked;
      private final String mainHit;
      private final String mainNotHit;
      private final String professionHit;
      private final String professionNotHit;
      private final SimpleDateFormat dateFormat;

      public QuestItem(ConfigurationSection config) {
         super(config);
         Validate.isTrue(config.contains("no-quest"), "Could not load config 'no-quest'");
         Validate.isTrue(config.contains("locked"), "Could not load config 'locked'");
         this.locked = new SimpleItem(config.getConfigurationSection("locked"));
         this.noQuest = new SimpleItem(config.getConfigurationSection("no-quest"));
         Validate.isTrue(config.contains("date-format"), "Could not find date-format");
         this.dateFormat = new SimpleDateFormat(config.getString("date-format"));
         Validate.notNull(this.mainHit = config.getString("level-requirement.main.hit"), "Could not load 'level-requirement.main.hit'");
         Validate.notNull(this.mainNotHit = config.getString("level-requirement.main.not-hit"), "Could not load 'level-requirement.main.not-hit'");
         Validate.notNull(this.professionHit = config.getString("level-requirement.profession.hit"), "Could not load 'level-requirement.profession.hit'");
         Validate.notNull(
            this.professionNotHit = config.getString("level-requirement.profession.not-hit"), "Could not load 'level-requirement.profession.not-hit'"
         );
      }

      public boolean hasDifferentDisplay() {
         return true;
      }

      public void preprocessLore(@NotNull QuestViewer.QuestInventory inv, int n, @NotNull List<String> lore) {
         int index = inv.getPageIndex(n);
         if (index < inv.quests.size()) {
            Quest quest = inv.quests.get(index);
            int loreIndex = lore.indexOf("{lore}");
            if (loreIndex >= 0) {
               lore.remove(loreIndex);

               for (int j = 0; j < quest.getLore().size(); j++) {
                  lore.add(loreIndex + j, quest.getLore().get(j));
               }
            }

            int reqCount = quest.countLevelRestrictions();
            boolean started = inv.playerData.getQuestData().hasCurrent(quest);
            boolean completed = inv.playerData.getQuestData().hasFinished(quest);
            boolean cooldown = completed && inv.playerData.getQuestData().checkCooldownAvailability(quest);
            int i = 0;

            while (i < lore.size()) {
               String next = lore.get(i);
               if (next.startsWith("{level_req}")) {
                  if (reqCount < 1) {
                     lore.remove(i);
                  } else {
                     lore.set(i++, next.replace("{level_req}", ""));
                  }
               } else if (next.startsWith("{started}")) {
                  if (!started) {
                     lore.remove(i);
                  } else {
                     lore.set(i++, next.replace("{started}", ""));
                  }
               } else if (next.startsWith("{!started}")) {
                  if (started) {
                     lore.remove(i);
                  } else {
                     lore.set(i++, next.replace("{!started}", ""));
                  }
               } else if (next.startsWith("{completed}")) {
                  if (!completed) {
                     lore.remove(i);
                  } else {
                     lore.set(i++, next.replace("{completed}", ""));
                  }
               } else if (next.startsWith("{completed_cannot_redo}")) {
                  if (completed && !quest.isRedoable()) {
                     lore.set(i++, next.replace("{completed_cannot_redo}", ""));
                  } else {
                     lore.remove(i);
                  }
               } else if (next.startsWith("{completed_can_redo}")) {
                  if (cooldown && quest.isRedoable()) {
                     lore.set(i++, next.replace("{completed_can_redo}", ""));
                  } else {
                     lore.remove(i);
                  }
               } else if (next.startsWith("{completed_delay}")) {
                  if (completed && !cooldown) {
                     lore.set(i++, next.replace("{completed_delay}", ""));
                  } else {
                     lore.remove(i);
                  }
               } else {
                  i++;
               }
            }

            loreIndex = lore.indexOf("{level_requirements}");
            if (loreIndex >= 0) {
               lore.remove(loreIndex);
               i = quest.getLevelRestriction(null);
               if (i > 0) {
                  lore.add(loreIndex, (inv.playerData.getLevel() >= i ? this.mainHit : this.mainNotHit).replace("{level}", i + ""));
               }

               for (Profession profession : quest.getLevelRestrictions()) {
                  int required = quest.getLevelRestriction(profession);
                  lore.add(
                     loreIndex + (i > 0 ? 1 : 0),
                     (inv.playerData.getCollectionSkills().getLevel(profession) >= required ? this.professionHit : this.professionNotHit)
                        .replace("{level}", required + "")
                        .replace("{profession}", profession.getName())
                  );
               }
            }
         }
      }

      public ItemStack getDisplayedItem(QuestViewer.QuestInventory inv, int n) {
         int index = inv.getPageIndex(n);
         if (index >= inv.quests.size()) {
            return this.noQuest.getDisplayedItem(inv, n);
         }

         Quest quest = inv.quests.get(index);
         if (quest.hasParent() && !inv.playerData.getQuestData().checkParentAvailability(quest)) {
            return this.locked.getDisplayedItem(inv, n);
         }

         ItemStack item = super.getDisplayedItem(inv, n);
         ItemMeta meta = item.getItemMeta();
         meta.getPersistentDataContainer().set(QuestViewer.QUEST_ID_KEY, PersistentDataType.STRING, quest.getId());
         item.setItemMeta(meta);
         return item;
      }

      public Placeholders getPlaceholders(QuestViewer.QuestInventory inv, int n) {
         Quest quest = inv.quests.get(inv.getPageIndex(n));
         PlayerData data = inv.playerData;
         Placeholders holders = new Placeholders();
         holders.register("name", quest.getName());
         holders.register("total_level_req", quest.getLevelRestrictions().size() + (quest.getLevelRestriction(null) > 0 ? 1 : 0));
         holders.register(
            "current_level_req",
            (data.getLevel() >= quest.getLevelRestriction(null) ? 1 : 0)
               + quest.getLevelRestrictions()
                  .stream()
                  .filter(type -> data.getCollectionSkills().getLevel(type) >= quest.getLevelRestriction(type))
                  .collect(Collectors.toSet())
                  .size()
         );
         if (data.getQuestData().hasCurrent(quest)) {
            holders.register("objective", data.getQuestData().getCurrent().getFormattedLore());
            holders.register("progress", (int)((double)data.getQuestData().getCurrent().getObjectiveNumber() / quest.getObjectives().size() * 100.0));
         }

         if (data.getQuestData().hasFinished(quest)) {
            holders.register("date", this.dateFormat.format(data.getQuestData().getFinishDate(quest)));
            holders.register("delay", new DelayFormat(2).format(data.getQuestData().getDelayFeft(quest)));
         }

         return holders;
      }

      public void onClick(@NotNull QuestViewer.QuestInventory inv, @NotNull InventoryClickEvent event) {
         String questId = (String)event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(QuestViewer.QUEST_ID_KEY, PersistentDataType.STRING);
         if (questId != null && !questId.equals("")) {
            Quest quest = MMOCore.plugin.questManager.get(questId);
            if (inv.playerData.getQuestData().hasCurrent()) {
               if (inv.playerData.getQuestData().hasCurrent(quest)) {
                  if (event.getClick() == ClickType.RIGHT) {
                     inv.playerData.getQuestData().start(null);
                     MMOCore.plugin.soundManager.getSound(SoundEvent.CANCEL_QUEST).playTo(inv.getPlayer());
                     ConfigMessage.fromKey("cancel-quest").send(inv.playerData);
                     inv.open();
                  }
               } else {
                  ConfigMessage.fromKey("already-on-quest").send(inv.playerData);
               }
            } else {
               int level;
               if (inv.playerData.getLevel() < (level = quest.getLevelRestriction(null))) {
                  ConfigMessage.fromKey("quest-level-restriction", "level", "Lvl", "count", level + "").send(inv.playerData);
               } else {
                  for (Profession profession : quest.getLevelRestrictions()) {
                     if (inv.playerData.getCollectionSkills().getLevel(profession) < (level = quest.getLevelRestriction(profession))) {
                        ConfigMessage.fromKey("quest-level-restriction", "level", profession.getName() + " Lvl", "count", level + "").send(inv.playerData);
                        return;
                     }
                  }

                  if (inv.playerData.getQuestData().hasFinished(quest)) {
                     if (!quest.isRedoable()) {
                        ConfigMessage.fromKey("cant-redo-quest").send(inv.playerData);
                        return;
                     }

                     if (!inv.playerData.getQuestData().checkCooldownAvailability(quest)) {
                        ConfigMessage.fromKey("quest-cooldown", "delay", new DelayFormat(2).format(inv.playerData.getQuestData().getDelayFeft(quest)))
                           .send(inv.playerData);
                        return;
                     }
                  }

                  ConfigMessage.fromKey("start-quest", "quest", quest.getName()).send(inv.playerData);
                  MMOCore.plugin.soundManager.getSound(SoundEvent.START_QUEST).playTo(inv.getPlayer());
                  inv.playerData.getQuestData().start(quest);
                  inv.open();
               }
            }
         }
      }
   }
}
