package io.lumine.mythic.core.drops;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.DropManager;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.ICommandDrop;
import io.lumine.mythic.api.drops.IDrop;
import io.lumine.mythic.api.drops.IIntangibleDrop;
import io.lumine.mythic.api.drops.IItemDrop;
import io.lumine.mythic.api.drops.ILocationDrop;
import io.lumine.mythic.api.drops.IMessagingDrop;
import io.lumine.mythic.api.volatilecode.virtual.PacketEntity;
import io.lumine.mythic.api.volatilecode.virtual.PacketItemDisplay;
import io.lumine.mythic.api.volatilecode.virtual.PacketTextDisplay;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitParticle;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.items.ItemFactory;
import io.lumine.mythic.bukkit.utils.plugin.ReloadableModule;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import io.lumine.mythic.bukkit.utils.text.Text;
import io.lumine.mythic.bukkit.utils.version.ServerVersion;
import io.lumine.mythic.core.config.IOHandler;
import io.lumine.mythic.core.config.IOLoader;
import io.lumine.mythic.core.config.MythicConfigImpl;
import io.lumine.mythic.core.config.MythicLineConfigImpl;
import io.lumine.mythic.core.constants.ConfigFileType;
import io.lumine.mythic.core.constants.MobKeys;
import io.lumine.mythic.core.drops.droppables.CommandDrop;
import io.lumine.mythic.core.drops.droppables.CustomDrop;
import io.lumine.mythic.core.drops.droppables.DropTableDrop;
import io.lumine.mythic.core.drops.droppables.ExperienceDrop;
import io.lumine.mythic.core.drops.droppables.HeroesExperienceDrop;
import io.lumine.mythic.core.drops.droppables.ItemDrop;
import io.lumine.mythic.core.drops.droppables.ItemVariableDrop;
import io.lumine.mythic.core.drops.droppables.McMMODrop;
import io.lumine.mythic.core.drops.droppables.MythicDropsDrop;
import io.lumine.mythic.core.drops.droppables.MythicItemDrop;
import io.lumine.mythic.core.drops.droppables.MythicMobDrop;
import io.lumine.mythic.core.drops.droppables.NothingDrop;
import io.lumine.mythic.core.drops.droppables.PhatLootsDrop;
import io.lumine.mythic.core.drops.droppables.SkillAPIDrop;
import io.lumine.mythic.core.drops.droppables.VanillaDropTableDrop;
import io.lumine.mythic.core.drops.droppables.VanillaItemDrop;
import io.lumine.mythic.core.drops.droppables.VaultDrop;
import io.lumine.mythic.core.glow.GlowColor;
import io.lumine.mythic.core.glow.GlowManager;
import io.lumine.mythic.core.items.LegacyItemConverter;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.tracker.DamageRecord;
import io.lumine.mythic.core.packs.PackExecutor;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

public class DropExecutor extends ReloadableModule<MythicBukkit> implements DropManager {
   private static final Pattern POSITION_PLACEHOLDER_PATTERN = Pattern.compile("<(\\d+)\\.(name|damage)>");
   private static final Pattern PROCESS_MSG_PLACEHOLDER_PATTERN = Pattern.compile("%(\\d+)\\.(name|damage)%");
   @Getter
   private PityManager pityManager = new DefaultPityManager();
   private Map<String, DropTable> dropTables = new ConcurrentHashMap<>();
   @Getter
   private Map<UUID, UUID> itemPickupMap = new ConcurrentHashMap<>();
   @Getter
   private Map<UUID, PacketEntity> itemVfxBeams = new ConcurrentHashMap<>();

   public DropExecutor(MythicBukkit plugin) {
      super(plugin, false);
      if (ServerVersion.isPaper()) {
         Events.subscribe(PlayerAttemptPickupItemEvent.class).filter(event -> this.itemPickupMap.containsKey(event.getItem().getUniqueId())).handler(event -> {
            Item item = event.getItem();
            Player player = event.getPlayer();
            UUID uuid = this.itemPickupMap.get(item.getUniqueId());
            if (uuid != null && !uuid.equals(player.getUniqueId())) {
               event.setCancelled(true);
            } else {
               this.itemPickupMap.remove(item.getUniqueId());
               if (this.itemVfxBeams.containsKey(item.getUniqueId())) {
                  this.itemVfxBeams.get(item.getUniqueId()).destroy();
                  this.itemVfxBeams.remove(item.getUniqueId());
               }

               ItemStack itemStack = event.getItem().getItemStack();
               if (itemStack.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(MythicBukkit.inst(), "fancydrop"), PersistentDataType.STRING)) {
                  ItemMeta meta = itemStack.getItemMeta();
                  PersistentDataContainer pdc = meta.getPersistentDataContainer();
                  pdc.remove(new NamespacedKey(MythicBukkit.inst(), "fancydrop"));
                  itemStack.setItemMeta(meta);
               }
            }
         });
      }

      Events.subscribe(PlayerPickupItemEvent.class).handler(event -> {
         Item item = event.getItem();
         ItemStack itemStack = item.getItemStack();
         if (itemStack.hasItemMeta()) {
            ItemMeta meta = itemStack.getItemMeta();
            if (meta.getPersistentDataContainer().has(MobKeys.FANCY_DROP)) {
               meta.getPersistentDataContainer().remove(MobKeys.FANCY_DROP);
               itemStack.setItemMeta(meta);
               item.setItemStack(itemStack);
            }
         }
      }).bindWith(this);
   }

   public void load(MythicBukkit plugin) {
      this.loadDropTables();
   }

   public void registerPityManager(PityManager manager) {
      this.pityManager = manager;
   }

   public void loadDropTables() {
      MythicLogger.log("Loading Drop Tables...");
      IOLoader<MythicBukkit> defaultDroptables = new IOLoader<>(MythicBukkit.inst(), "ExampleDropTables.yml", "DropTables");
      List<File> droptableFiles = IOHandler.getAllFiles(defaultDroptables.getFile().getParent());
      droptableFiles.addAll(this.getPlugin().getPackManager().getConfigFiles(ConfigFileType.DROP_TABLE).stream().map(PackExecutor.ConfigFile::file).toList());
      List<IOLoader<MythicBukkit>> droptableLoaders = IOHandler.getSaveLoad(MythicBukkit.inst(), droptableFiles, "DropTables");
      this.dropTables.clear();

      for (IOLoader<MythicBukkit> sl : droptableLoaders) {
         for (String s : sl.getCustomConfig().getConfigurationSection("").getKeys(false)) {
            if (sl.getCustomConfig().getStringList(s + ".Drops") != null) {
               String file = sl.getFile().getName();
               MythicConfig mc = new MythicConfigImpl(s, sl.getCustomConfig());
               DropTable dt = new DropTable(this, file, s, mc);
               this.dropTables.put(s, dt);
            }
         }
      }

      this.plugin.getClock().runSecondPass();
   }

   @Override
   public void unload() {
      this.dropTables.clear();
   }

   public void queueAfterLoad(Runnable r) {
      this.plugin.getClock().queueAfterLoad(r);
   }

   public void queueSecondPass(Runnable r) {
      this.plugin.getClock().queueSecondPass(r);
   }

   @Override
   public Optional<DropTable> getDropTable(String name) {
      return Optional.ofNullable(this.dropTables.getOrDefault(name, null));
   }

   @Override
   public Collection<DropTable> getDropTables() {
      return this.dropTables.values();
   }

   public Drop getDrop(String fileName, String drop) {
      if (drop.contains("}")) {
         int firstOpen = drop.indexOf("{");
         int firstClose = drop.indexOf("}");
         int firstSpace = drop.indexOf(" ");
         if (firstOpen > -1 && firstClose > firstOpen && (firstSpace == -1 || firstOpen < firstSpace)) {
            String sp1 = drop.substring(0, firstClose);
            String sp2 = drop.substring(firstClose);
            String ns = sp1.replace(" ", "") + sp2;
            drop = ns;
         }
      }

      String[] s = drop.split(" ");
      String name = null;
      MythicLineConfig mlc = new MythicLineConfigImpl(fileName, s[0]);
      if (s[0].contains("{")) {
         name = s[0].substring(0, s[0].indexOf("{"));
      } else {
         name = s[0];
      }

      String oName = name.toUpperCase();
      byte oData = 0;
      if (name.contains(":")) {
         oName = name.split(":")[0].toUpperCase();

         try {
            oData = Byte.valueOf(name.split(":")[1]);
         } catch (Exception e) {
         }

         name = name.split(":")[0];
      }

      Material material = null;

      try {
         material = oData == 0 ? Material.valueOf(oName) : LegacyItemConverter.getMaterial(oName, oData);
         if (material == null && oData > 0) {
            material = Material.valueOf(oName);
         }
      } catch (Exception ex2) {
         material = LegacyItemConverter.getMaterial(oName, oData);
         if (material == null && oData > 0) {
            try {
               material = Material.valueOf(oName);
            } catch (Exception ex3) {
            }
         }
      } finally {
         if (material != null && material != Material.AIR) {
            name = material.toString();
         }
      }

      try {
         if (Material.matchMaterial(name) != null) {
            return new VanillaItemDrop(drop, mlc, name);
         } else if (this.getPlugin().getDropManager().getDropTable(name).isPresent()) {
            return new DropTableDrop(drop, name, mlc);
         } else if (this.getPlugin().getItemManager().getItem(name).isPresent()) {
            return new MythicItemDrop(drop, name, mlc);
         } else {
            String ex = name.toUpperCase();

            return (switch (ex) {
               case "NOTHING", "NONE", "NULL" -> new NothingDrop(drop, mlc);
               case "COMMAND", "CMD" -> new CommandDrop(drop, mlc);
               case "EXPERIENCE", "EXP", "XP" -> new ExperienceDrop(drop, mlc);
               case "MYTHICMOB", "MYTHICMOBS", "MM" -> new MythicMobDrop(drop, mlc);
               case "VAULT", "MONEY", "CURRENCY" -> new VaultDrop(drop, mlc);
               case "HEROESEXP", "HEROES-EXP" -> new HeroesExperienceDrop(drop, mlc);
               case "MCMMOEXP", "MCMMO-EXP" -> new McMMODrop(drop, mlc);
               case "MYTHICDROP", "MD" -> new MythicDropsDrop(drop, mlc);
               case "PHATLOOT", "PHATLOOTS", "PL" -> new PhatLootsDrop(drop, mlc);
               case "SKILLAPIEXP", "SKILLAPI-EXP" -> new SkillAPIDrop(drop, mlc);
               case "VANILLALOOTTABLE", "VANILLADROPTABLE" -> new VanillaDropTableDrop(drop, mlc);
               case "ITEMVARIABLE", "ITEMVAR" -> new ItemVariableDrop(drop, mlc);
               default -> new CustomDrop(name, drop, mlc);
            });
         }
      } catch (Exception ex4) {
         return new InvalidDrop(drop, mlc);
      }
   }

   public static void Drop(Location loc, int exp, List<ItemStack> drops) {
      for (ItemStack is : drops) {
         loc.getWorld().dropItemNaturally(loc, is);
      }

      if (exp != 0) {
         int i = exp % 4;
         int per = (exp - exp % 4) / 4;

         for (int y = 0; y < 4; y++) {
            ExperienceOrb eo = (ExperienceOrb)loc.getWorld().spawnEntity(loc, EntityType.EXPERIENCE_ORB);
            eo.setExperience(per);
         }

         if (i != 0) {
            ExperienceOrb eo = (ExperienceOrb)loc.getWorld().spawnEntity(loc, EntityType.EXPERIENCE_ORB);
            eo.setExperience(i);
         }
      }
   }

   public void handleEntityLootDrops(EntityDeathEvent event, LootBag lootBag, DropMetadata metadata, ActiveMob am) {
      Map<IMessagingDrop, Double> messagingDrops = new HashMap<>();
      LivingEntity killer = this.getKiller(event);
      AbstractEntity aKiller;
      if (metadata.getCause().isPresent()) {
         aKiller = metadata.getCause().get();
      } else {
         aKiller = killer == null ? null : BukkitAdapter.adapt(killer);
      }

      AbstractEntity killedEntity = BukkitAdapter.adapt(event.getEntity());
      int experience = 0;
      boolean fancy = false;

      for (Drop type : lootBag.getDrops()) {
         double amount = type.getAmount();
         IDrop drop;
         if (type instanceof CustomDrop customDrop) {
            if (customDrop.getDrop().isEmpty()) {
               continue;
            }

            drop = customDrop.getDrop().get();
         } else {
            drop = type;
         }

         if (drop instanceof IItemDrop itemDrop) {
            switch (am.getType().getDropMethod()) {
               case FANCY:
                  fancy = true;
                  if (aKiller != null && aKiller.isPlayer()) {
                     this.handleFancyDrops(drop, amount, am, metadata, event, aKiller);
                  }
               case CHEST:
                  break;
               default:
                  ItemStack stack = BukkitAdapter.adapt(itemDrop.getDrop(metadata, amount));
                  if (stack == null) {
                     MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Drop produced no item, skipping: {0}", type.getLine());
                  } else {
                     event.getDrops().add(stack);
                  }
            }
         } else if (drop instanceof ExperienceDrop) {
            experience = (int)(experience + amount);
         } else if (drop instanceof IIntangibleDrop intangibleDrop) {
            if (aKiller != null && aKiller.isPlayer()) {
               intangibleDrop.giveDrop(aKiller.asPlayer(), metadata, amount);
            }
         } else if (drop instanceof ILocationDrop locationDrop) {
            locationDrop.drop(killedEntity.getLocation(), metadata, amount);
         } else if (drop instanceof ICommandDrop) {
            metadata.setAmount((int)amount);
            if (aKiller != null && aKiller.isPlayer()) {
               ((ICommandDrop)drop).dispatchCommand(aKiller.asPlayer(), metadata, amount);
            } else {
               ((ICommandDrop)drop).dispatchCommand(null, metadata, amount);
            }
         }

         if (drop instanceof IMessagingDrop) {
            messagingDrops.merge((IMessagingDrop)drop, amount, (n, o) -> n + o);
         }
      }

      if (aKiller != null && aKiller.isPlayer()) {
         if (!messagingDrops.isEmpty()) {
            for (Entry<IMessagingDrop, Double> m : messagingDrops.entrySet()) {
               String message = m.getKey().getRewardMessage(metadata, m.getValue());
               if (message != null) {
                  aKiller.asPlayer().sendMessage(message);
               }
            }
         }

         DamageRecord record = am.getDamageRecord();
         if (record != null) {
            if (am.getType().isDropsPerPlayer()) {
               if (am.getType().getShowChatLeaderboardOnDeath()) {
                  List<Component> parsed = this.processMessages(am.getType().getFancyDropChatMessage(), record, aKiller.getUniqueId(), this.pityManager, am);
                  Player player = Bukkit.getPlayer(aKiller.getUniqueId());
                  if (player != null) {
                     parsed.forEach(line -> Text.sendMessage(player, line));
                  }
               }

               if (am.getType().getShowHologramLeaderboardOnDeath()) {
                  List<Component> parsed = this.processMessages(am.getType().getFancyDropHologramMessage(), record, aKiller.getUniqueId(), this.pityManager, am);
                  PacketTextDisplay packetTextDisplay = PacketTextDisplay.create().textComponent(parsed).cullingDistance(256).build(am.getLocation());
                  if (Bukkit.getPlayer(aKiller.getUniqueId()) != null && Bukkit.getPlayer(aKiller.getUniqueId()).isOnline()) {
                     packetTextDisplay.getRenderer().spawn(aKiller.asPlayer());
                     Schedulers.async().runLater(packetTextDisplay::destroy, am.getType().getHologramLeaderboardTimeout().intValue());
                  }
               }
            } else {
               if (am.getType().getShowChatLeaderboardOnDeath()) {
                  for (UUID killerUUID : record.getDamagers()) {
                     List<Component> parsed = this.processMessages(am.getType().getFancyDropChatMessage(), record, killerUUID, this.pityManager, am);
                     Player player = Bukkit.getPlayer(killerUUID);
                     if (player != null) {
                        parsed.forEach(line -> Text.sendMessage(player, line));
                     }
                  }
               }

               if (am.getType().getShowHologramLeaderboardOnDeath()) {
                  for (UUID killerUUIDx : record.getDamagers()) {
                     List<Component> parsed = this.processMessages(am.getType().getFancyDropHologramMessage(), record, killerUUIDx, this.pityManager, am);
                     PacketTextDisplay packetTextDisplay = PacketTextDisplay.create().textComponent(parsed).cullingDistance(256).build(am.getLocation());
                     Player mobDamager = Bukkit.getPlayer(killerUUIDx);
                     if (mobDamager != null && mobDamager.isOnline()) {
                        packetTextDisplay.getRenderer().spawn(BukkitAdapter.adapt(mobDamager));
                        Schedulers.async().runLater(packetTextDisplay::destroy, am.getType().getHologramLeaderboardTimeout().intValue());
                     }
                  }
               }
            }
         }
      }

      if (experience > 0) {
         event.setDroppedExp(experience);
      }
   }

   private void handleFancyDrops(IDrop drop, double amount, ActiveMob am, DropMetadata metadata, EntityDeathEvent event, AbstractEntity killer) {
      ItemStack itemStack = BukkitAdapter.adapt(((IItemDrop)drop).getDrop(metadata, amount));
      if (itemStack == null) {
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Drop produced no item, skipping: {0}", drop);
      } else {
         Player playerKiller = (Player)BukkitAdapter.adapt(killer);
         if (drop instanceof ItemDrop itemDrop) {
            boolean perPlayerDrops = am.getType().isDropsPerPlayer();
            DamageRecord damageRecord = am.getDamageRecord();
            if (damageRecord != null) {
               int place = damageRecord.getPlace(killer.getUniqueId());
               boolean didEnoughDamage = damageRecord.didEnoughDamage(killer.getUniqueId(), itemDrop.getMinDamage());
               if (!perPlayerDrops || didEnoughDamage && place <= itemDrop.getRequiredPlacement()) {
                  if (perPlayerDrops) {
                     double minPercent = am.getType().getDropsPerPlayerRequiredDamagePercent();
                     if (minPercent > 0.0 && killer.isPlayer()) {
                        double damagePercent = damageRecord.getDamageDonePercent(killer.asPlayer());
                        if (damagePercent < minPercent) {
                           return;
                        }
                     }
                  }

                  Location deathLocation = BukkitAdapter.adapt(am.getLocation());
                  World deathWorld = deathLocation.getWorld();
                  GlowManager glowManager = MythicBukkit.inst().getGlowManager();
                  ItemMeta meta = itemStack.getItemMeta();
                  PersistentDataContainer pdc = meta.getPersistentDataContainer();
                  pdc.set(MobKeys.FANCY_DROP, PersistentDataType.STRING, UUID.randomUUID().toString());
                  itemStack.setItemMeta(meta);
                  deathWorld.dropItemNaturally(
                     deathLocation,
                     itemStack,
                     item -> {
                        boolean clientSide = am.getType().isDropsPerPlayer()
                           && itemDrop.getClientSideDropsEnabled().orElse(am.getType().getDropsAreClientsideByDefault());
                        if (am.getType().isDropsPerPlayer()) {
                           this.itemPickupMap.put(item.getUniqueId(), playerKiller.getUniqueId());
                           if (clientSide) {
                              item.setVisibleByDefault(false);
                              playerKiller.showEntity(MythicBukkit.inst(), item);
                           }
                        }

                        if (itemDrop.getLootsplosionEnabled().orElse(am.getType().getDropsDoLootsplosion())) {
                           ThreadLocalRandom rand = ThreadLocalRandom.current();
                           double randomX = (rand.nextDouble() * 0.5 - 0.25) * 1.4;
                           double randomY = (rand.nextDouble() * 0.25 + 0.25) * 1.4;
                           double randomZ = (rand.nextDouble() * 0.5 - 0.25) * 1.4;
                           Vector velocity = new Vector(randomX, randomY, randomZ);
                           item.setVelocity(velocity);
                        }

                        if (itemDrop.getItemGlowEnabled().orElse(am.getType().getDropsGlowByDefault())) {
                           GlowColor color;
                           if (itemDrop.getItemGlow() != null) {
                              color = itemDrop.getItemGlow();
                           } else {
                              color = GlowColor.WHITE;
                           }

                           Runnable glowRunnable = () -> {
                              if (clientSide) {
                                 glowManager.setColor(BukkitAdapter.adapt(item), color, BukkitAdapter.adapt(playerKiller));
                                 glowManager.setGlowing(BukkitAdapter.adapt(item), true, BukkitAdapter.adapt(playerKiller));
                              } else {
                                 glowManager.setColor(BukkitAdapter.adapt(item), color, this.getPlugin().getBootstrap().getOnlinePlayers());
                                 glowManager.setGlowing(BukkitAdapter.adapt(item), true, this.getPlugin().getBootstrap().getOnlinePlayers());
                              }
                           };
                           if (ServerVersion.isFolia()) {
                              Schedulers.of(item).runLater(glowRunnable, 1L);
                           } else {
                              Schedulers.sync().runLater(glowRunnable, 1L);
                           }
                        }

                        if (itemDrop.getItemVFXEnabled().orElse(am.getType().getDropsHaveVFXByDefault())) {
                           ItemFactory vfxItem;
                           if (itemDrop.getItemVfxMaterial() == null) {
                              vfxItem = ItemFactory.of(am.getType().getDropsItemVFXMaterial()).model(am.getType().getDropsItemVFXModel());
                           } else {
                              vfxItem = ItemFactory.of(Material.valueOf(itemDrop.getItemVfxMaterial())).model(itemDrop.getItemVfxData());
                           }

                           if (itemDrop.getItemVfxModel() != null) {
                              vfxItem.itemModel(itemDrop.getItemVfxModel());
                           }

                           if (itemDrop.getVfxColor() != null) {
                              vfxItem.color(itemDrop.getVfxColor());
                           }

                           PacketItemDisplay packetItemDisplay = PacketItemDisplay.create()
                              .item(vfxItem.build())
                              .billboard(itemDrop.getDisplayBillboarding())
                              .viewRange(200.0F)
                              .build(BukkitAdapter.adapt(item.getLocation()));
                           if (clientSide) {
                              packetItemDisplay.getRenderer().spawn(BukkitAdapter.adapt(playerKiller));
                           } else {
                              packetItemDisplay.getRenderer().spawn(() -> this.getPlugin().getBootstrap().getOnlinePlayers());
                           }

                           packetItemDisplay.getRenderer().mountEntity(BukkitAdapter.adapt(item));
                           this.itemVfxBeams.put(item.getUniqueId(), packetItemDisplay);
                           Schedulers.async().runRepeating(task -> {
                              if (!item.isDead() && task.getTimesRan() <= 10000) {
                                 packetItemDisplay.getRenderer().updateRenderedPlayers();
                                 packetItemDisplay.getRenderer().mountEntity(BukkitAdapter.adapt(item));
                                 packetItemDisplay.getRenderer().update();
                              } else {
                                 if (this.itemVfxBeams.containsKey(item.getUniqueId())) {
                                    this.itemVfxBeams.get(item.getUniqueId()).destroy();
                                    this.itemVfxBeams.remove(item.getUniqueId());
                                 }

                                 task.terminate();
                              }
                           }, 1L, 1L);
                        }

                        if (itemDrop.getItemBeamEnabled().orElse(am.getType().getDropsHaveBeamByDefault())) {
                           Schedulers.async().runRepeating(task -> {
                              if (!item.isDead() && task.getTimesRan() <= 1000) {
                                 Color colorx;
                                 if (itemDrop.getItemBeam() != null) {
                                    colorx = itemDrop.getItemBeam().toBukkitColor();
                                 } else {
                                    colorx = Chroma.Dyes.WHITE.getColor().toBukkitColor();
                                 }

                                 if (clientSide) {
                                    this.createParticleBeamAboveItem(playerKiller, item.getLocation(), colorx);
                                 } else {
                                    for (Player player : Bukkit.getOnlinePlayers()) {
                                       if (player.getWorld() == item.getWorld() && !(player.getLocation().distance(item.getLocation()) > 50.0)) {
                                          this.createParticleBeamAboveItem(player, item.getLocation(), colorx);
                                       }
                                    }
                                 }
                              } else {
                                 task.terminate();
                              }
                           }, 2L, 2L);
                        }

                        if (itemDrop.getHologramNameEnabled().orElse(am.getType().getDropsShowNameByDefault())) {
                           item.setCustomNameVisible(true);
                           if (drop instanceof MythicItemDrop mythicItemDrop) {
                              String display = mythicItemDrop.getItem().getDisplayName();
                              item.customName(MiniMessage.miniMessage().deserialize(display));
                           } else {
                              item.customName(itemStack.displayName());
                           }
                        }
                     }
                  );
                  if (itemDrop.isResetPity()) {
                     this.pityManager.resetPity(playerKiller.getUniqueId(), itemDrop.getPityCategory());
                  }

                  this.pityManager.addPity(playerKiller.getUniqueId(), itemDrop.getPityModifier(), itemDrop.getPityCategory());
               }
            }
         } else {
            event.getDrops().add(itemStack);
         }
      }
   }

   public LivingEntity getKiller(EntityDeathEvent event) {
      EntityDamageEvent entityDamageEvent = event.getEntity().getLastDamageCause();
      if (entityDamageEvent != null && !entityDamageEvent.isCancelled() && entityDamageEvent instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
         Entity damager = entityDamageByEntityEvent.getDamager();
         if (damager instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof LivingEntity livingEntity) {
               return livingEntity;
            }

            return event.getEntity().getKiller();
         }

         if (damager instanceof LivingEntity livingEntity) {
            return livingEntity;
         }
      }

      return event.getEntity().getKiller();
   }

   private void createParticleBeamAboveItem(Player player, Location itemLocation, Color color) {
      for (int i = 0; i < 20; i++) {
         Location particleLocation = itemLocation.clone().add(0.0, 1.0 + i * 0.1, 0.0);
         DustOptions dustOptions = new DustOptions(color, 1.0F);
         player.spawnParticle(BukkitParticle.DUST.getBukkitParticle(), particleLocation, 1, 0.0, 0.0, 0.0, 1.0, dustOptions);
      }
   }

   private String replacePlaceholders(String message, DamageRecord record, UUID playerUuid, PityManager pityManager, ActiveMob am) {
      Player player = Bukkit.getPlayer(playerUuid);
      if (player == null) {
         return message;
      } else {
         int playerRank = record.getPlace(playerUuid);
         double playerDamage = record.getDamagingPlayers().get(playerUuid).getTotalDamage();
         double playerPity = pityManager.getPity(playerUuid);
         String mobName = am.getDisplayName();
         message = message.replace("<mob.name>", mobName != null ? mobName : am.getMobType());
         message = message.replace("<mob.hp>", Integer.toString((int)am.getType().getHealth().get()));
         message = message.replace("<player.rank>", Integer.toString(playerRank));
         message = message.replace("<player.damage>", String.format("%.2f", playerDamage));
         message = message.replace("<pity>", Double.toString(playerPity));
         Matcher matcher = POSITION_PLACEHOLDER_PATTERN.matcher(message);

         while (matcher.find()) {
            int position = Integer.parseInt(matcher.group(1));
            String placeholderType = matcher.group(2);
            UUID damagerUuid = record.getDamagerInPlace(position);
            if (damagerUuid == null) {
               return null;
            }

            Player damagerPlayer = Bukkit.getPlayer(damagerUuid);
            if (damagerPlayer == null) {
               return null;
            }

            if ("name".equals(placeholderType)) {
               String damagerName = damagerPlayer.getName();
               message = message.replace("<" + position + ".name>", damagerName);
            } else if ("damage".equals(placeholderType)) {
               double damagerDamage = record.getDamageInPlace(position);
               message = message.replace("<" + position + ".damage>", String.format("%.2f", damagerDamage));
            }
         }

         return message;
      }
   }

   private List<Component> processMessages(List<String> messages, DamageRecord record, UUID playerUuid, PityManager pityManager, ActiveMob am) {
      List<Component> parsed = new ArrayList<>();

      for (String s : messages) {
         boolean validLine = true;
         Matcher matcher = PROCESS_MSG_PLACEHOLDER_PATTERN.matcher(s);

         while (matcher.find()) {
            int placeholderIndex = Integer.parseInt(matcher.group(1));
            if (placeholderIndex > record.getDamagingPlayers().size()) {
               validLine = false;
               break;
            }
         }

         if (validLine) {
            s = this.replacePlaceholders(s, record, playerUuid, pityManager, am);
            if (s != null) {
               parsed.add(MiniMessage.miniMessage().deserialize(s));
            }
         }
      }

      return parsed;
   }
}
