package com.Zrips.CMI;

import com.Zrips.CMI.Containers.CMIUser;
import com.Zrips.CMI.Locale.CMILC;
import com.Zrips.CMI.Locale.Language;
import com.Zrips.CMI.Modules.AStand.ArmorStandManager;
import com.Zrips.CMI.Modules.Afk.AfkManager;
import com.Zrips.CMI.Modules.Alias.AliasManager;
import com.Zrips.CMI.Modules.Animations.AnimationManager;
import com.Zrips.CMI.Modules.Anvil.AnvilManager;
import com.Zrips.CMI.Modules.ArmorEffects.ArmorEffectManager;
import com.Zrips.CMI.Modules.AttachedCommands.CustomNBTManager;
import com.Zrips.CMI.Modules.Backup.CMIBackUpManager;
import com.Zrips.CMI.Modules.BlueMap.BlueMapManager;
import com.Zrips.CMI.Modules.BungeeCord.BungeeCordManager;
import com.Zrips.CMI.Modules.Chat.ChatBubbleManager;
import com.Zrips.CMI.Modules.Chat.ChatManager;
import com.Zrips.CMI.Modules.ChatFilter.ChatFilterManager;
import com.Zrips.CMI.Modules.ChatFormat.ChatFormatManager;
import com.Zrips.CMI.Modules.ChatTag.TagManager;
import com.Zrips.CMI.Modules.ChunkPreview.ChunkPreview;
import com.Zrips.CMI.Modules.CmdCooldown.CooldownManager;
import com.Zrips.CMI.Modules.CmdCost.CMICommandCostManager;
import com.Zrips.CMI.Modules.CmdWarmUp.WarmUpManager;
import com.Zrips.CMI.Modules.ConsoleFilter.ConsoleFilterManager;
import com.Zrips.CMI.Modules.CustomText.CTextManager;
import com.Zrips.CMI.Modules.DataBase.DBClassLoader;
import com.Zrips.CMI.Modules.DataBase.DBManager;
import com.Zrips.CMI.Modules.DataBase_NEW.DBManager_NEW;
import com.Zrips.CMI.Modules.DeathMessages.DeathMessageManager;
import com.Zrips.CMI.Modules.Dialogs.DialogManager;
import com.Zrips.CMI.Modules.DiscordSRV.DiscordSRVManager;
import com.Zrips.CMI.Modules.DynMap.DynMapManager;
import com.Zrips.CMI.Modules.DynamicSigns.SignManager;
import com.Zrips.CMI.Modules.Economy.EconomyManager;
import com.Zrips.CMI.Modules.Elytra.ElytraManager;
import com.Zrips.CMI.Modules.Enchants.EnchantManager;
import com.Zrips.CMI.Modules.EventActions.EventActionManager;
import com.Zrips.CMI.Modules.FindBiome.FindBiomeManager;
import com.Zrips.CMI.Modules.FlightCharge.FlightChargeManager;
import com.Zrips.CMI.Modules.GeoIP.LookupService;
import com.Zrips.CMI.Modules.Holograms.HologramManager;
import com.Zrips.CMI.Modules.Homes.HomeManager;
import com.Zrips.CMI.Modules.InteractiveCommand.CitizensManager;
import com.Zrips.CMI.Modules.InteractiveCommand.InteractiveCommandManager;
import com.Zrips.CMI.Modules.InvEndEditor.EnderChestManager;
import com.Zrips.CMI.Modules.InvRegEditor.RegChestManager;
import com.Zrips.CMI.Modules.Ips.IpManager;
import com.Zrips.CMI.Modules.Jail.JailManager;
import com.Zrips.CMI.Modules.Kits.KitsManager;
import com.Zrips.CMI.Modules.LightFix.LightFix;
import com.Zrips.CMI.Modules.Mirror.CMIMirrorManager;
import com.Zrips.CMI.Modules.NickName.NickNameManager;
import com.Zrips.CMI.Modules.Packets.PacketInjector;
import com.Zrips.CMI.Modules.Painting.PaintingManager;
import com.Zrips.CMI.Modules.Particl.ParticleManager;
import com.Zrips.CMI.Modules.Patrol.PatrolManager;
import com.Zrips.CMI.Modules.Permissions.PermissionsManager;
import com.Zrips.CMI.Modules.Placeholders.Placeholder;
import com.Zrips.CMI.Modules.PlayTime.PlayTimeManager;
import com.Zrips.CMI.Modules.PlayTimeRewards.PlayTimeRewardsManager;
import com.Zrips.CMI.Modules.PlayerCombat.PlayerCombatManager;
import com.Zrips.CMI.Modules.PlayerOptions.PlayerOptionsManager;
import com.Zrips.CMI.Modules.Portals.PortalManager;
import com.Zrips.CMI.Modules.Ranks.RankManager;
import com.Zrips.CMI.Modules.Recipes.RecipeManager;
import com.Zrips.CMI.Modules.Region.WorldManager;
import com.Zrips.CMI.Modules.Repair.RepairManager;
import com.Zrips.CMI.Modules.ReplaceBlock.ReplaceBlock;
import com.Zrips.CMI.Modules.SavedInv.SavedInventoryManager;
import com.Zrips.CMI.Modules.SavedItems.SavedItemManager;
import com.Zrips.CMI.Modules.Scan.CMIWorldChunkManager;
import com.Zrips.CMI.Modules.Scavenger.ScavengeManager;
import com.Zrips.CMI.Modules.Selection.SelectionManager;
import com.Zrips.CMI.Modules.Sheduler.SchedulerManager;
import com.Zrips.CMI.Modules.ShulkerBoxInventory.ShulkerBoxManager;
import com.Zrips.CMI.Modules.Skin.SkinManager;
import com.Zrips.CMI.Modules.SpawnerCharge.SpawnerChargeManager;
import com.Zrips.CMI.Modules.SpecializedCommands.SpecializedCommandManager;
import com.Zrips.CMI.Modules.Statistics.StatsManager;
import com.Zrips.CMI.Modules.TabList.TabListHeaderFooterHandler;
import com.Zrips.CMI.Modules.TabList.TabListManager;
import com.Zrips.CMI.Modules.Teleportations.RandomTeleportationManager;
import com.Zrips.CMI.Modules.Teleportations.TeleportHandler;
import com.Zrips.CMI.Modules.Teleportations.TeleportManager;
import com.Zrips.CMI.Modules.TimedCommands.TimedCommandManager;
import com.Zrips.CMI.Modules.Totems.TotemManager;
import com.Zrips.CMI.Modules.Vanish.VanishManager;
import com.Zrips.CMI.Modules.Votifier.VotifierManager;
import com.Zrips.CMI.Modules.Warnings.WarningManager;
import com.Zrips.CMI.Modules.Warps.WarpManager;
import com.Zrips.CMI.Modules.Worth.WorthManager;
import com.Zrips.CMI.NBT.NMS;
import com.Zrips.CMI.commands.CommandsHandler;
import com.Zrips.CMI.utils.CMIThread;
import com.Zrips.CMI.utils.ChunkFix;
import com.Zrips.CMI.utils.FileDownloader;
import com.Zrips.CMI.utils.Lag;
import com.Zrips.CMI.utils.Purge;
import com.Zrips.CMI.utils.TimeManager;
import com.Zrips.CMI.utils.UnloadChunks;
import com.Zrips.CMI.utils.Util;
import com.Zrips.CMI.utils.VersionChecker;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.Zrips.CMILib.Colors.CMIChatColor;
import net.Zrips.CMILib.Container.CommandType;
import net.Zrips.CMILib.FileHandler.ConfigReader;
import net.Zrips.CMILib.GUI.GUIManager;
import net.Zrips.CMILib.Locale.LC;
import net.Zrips.CMILib.Messages.CMIMessages;
import net.Zrips.CMILib.Util.Sorting;
import net.Zrips.CMILib.Version.Schedulers.CMIScheduler;
import net.Zrips.CMILib.Version.Schedulers.CMITask;
import net.Zrips.CMILib.Version.Version;
import net.Zrips.CMILib.commands.CMICommand;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class CMI extends JavaPlugin {
  public static final String settingsFolderName = "Settings";
  public static final String savesFolderName = "Saves";
  public static final String logsFolderName = "Logs";
  public static final String kitsFolderName = "Kits";
  public static final String dialogsFolderName = "Dialogs";
  public static final String translationsFolderName = "Translations";
  public static final String deathMessagesFolderName = "DeathMessages";
  boolean fullyLoaded = false;
  boolean serverLoaded = false;
  protected NMS nms;
  protected Reflections ref;
  private Placeholder Placeholder;
  private Permission vaultPerm = null;
  public static String bugReportLink =
      "https://github.com/Zrips/CMI/issues/new?assignees=&labels=bug+report&template=bug_report.md&title=";
  static long serverStartupTime = 0L;
  private String worldFolderPath;
  protected SavedItemManager SavedItemManager;
  protected BlueMapManager BlueMapManager;
  protected PlayTimeManager PlayTimeManager;
  protected CommandsHandler cManager;
  protected LightFix LightFixManager;
  protected ChunkPreview ChunkPreviewManager;
  protected DiscordSRVManager DiscordSRVManager;
  protected ConsoleFilterManager ConsoleFilterManager;
  protected Purge PurgeManager;
  protected CMIWorldChunkManager ScanManager;
  protected ChunkFix ChunkFixManager;
  protected ReplaceBlock replaceblock;
  protected UnloadChunks unloadchunks;
  protected ElytraManager ElytraManager;
  protected RepairManager RepairManager;
  protected SkinManager SkinManager;
  protected Util UtilManager;
  protected Config config;
  protected Language languageManager;
  protected ScavengeManager scavengeManager;
  protected VersionChecker versionCheckManager;
  protected PlayTimeRewardsManager PlayTimeRewardsManager;
  protected TimeManager TimeManager;
  protected CMIMirrorManager mirrorV2;
  protected KitsManager kits;
  protected SpawnerChargeManager charges;
  protected ParticleManager ParticleManager;
  protected FlightChargeManager FlightChargeManager;
  protected ChatBubbleManager ChatBubbleManager;
  protected ChatManager ChatManager;
  private ArmorEffectManager armorEffectManager;
  protected PaintingManager PaintingManager;
  protected VanishManager VanishManager;
  protected PlayerOptionsManager PlayerOptionsManager;
  protected InteractiveCommandManager InteractiveCommandManager;
  protected SpecializedCommandManager SpecializedCommandManager;
  protected ArmorStandManager ArmorStandManager;
  protected LookupService LookupService;
  protected DynMapManager DynMapManager;
  protected WorthManager WorthManager;
  protected JailManager JailManager;
  protected DialogManager DialogManager;
  protected TeleportHandler TeleportHandler;
  protected WarningManager WarningManager;
  protected PlayerCombatManager PlayerCombatManager;
  private EconomyManager economyManager;
  protected EnderChestManager EnderChestManager;
  protected RegChestManager RegChestManager;
  protected VotifierManager VotifierManager;
  protected CitizensManager CitizensManager;
  protected AfkManager AfkManager;
  protected TabListManager TabListManager;
  protected TabListHeaderFooterHandler TabListHandler;
  protected RecipeManager RecipeManager;
  protected PortalManager portalManager;
  protected SignManager signManager;
  protected HologramManager HologramManager;
  protected SelectionManager SelectionManager;
  protected CMICommandCostManager CMICommandCostManager;
  protected CooldownManager cooldownManager;
  protected WarmUpManager WarmUpManager;
  protected WorldManager regionManager;
  protected TotemManager totemManager;
  protected AnimationManager AnimationManager;
  protected RandomTeleportationManager RandomTeleportationManager;
  protected ChatFormatManager ChatFormatManager;
  protected ShulkerBoxManager shulkerBoxManager;
  protected SchedulerManager schedulerManager;
  protected EnchantManager EnchantManager;
  protected AnvilManager anvilManager;
  protected TimedCommandManager timedCommandManager;
  protected PermissionsManager permissionsManager;
  protected DeathMessageManager DeathMessageManager;
  protected TagManager TagManager;
  protected NickNameManager nickNameManager;
  protected HomeManager homeManager;
  protected WarpManager warpManager;
  protected IpManager ipManager;
  protected TeleportManager TeleportManager;
  protected StatsManager statsManager;
  protected AliasManager aliasManager;
  protected CTextManager cTextManager;
  protected EventActionManager eventActionManager;
  protected CustomNBTManager CustomNBTManager;
  protected FindBiomeManager findBiomeManager;
  protected ChatFilterManager chatFilterManager;
  protected GUIManager GUIManager;
  protected SavedInventoryManager SavedInventoryManager;
  protected PatrolManager PatrolManager;
  private DBManager dbManager;
  private DBManager_NEW dbManager_new;
  private RankManager rankManager;
  private Lag lagMeter;
  protected PlayerManager PM;
  protected Sorting Sorting;
  private TabComplete tab;
  private long timer = 0L;
  protected HashMap<String, List<String>> preFetchNames = new HashMap<>();
  protected HashMap<String, UUID> preFetchUUIDs = new HashMap<>();
  private static final UUID ServerUUID = new UUID(0L, 0L);
  private String s1 = "835824";
  private String s2 = "3742";
  private String s3 = "-2046491858";
  private String s4 = "10835824";
  private String s5 = "123456835824";
  protected static CMI instance;
  public String prefix = ChatColor.GOLD + "[CMI] " + ChatColor.DARK_AQUA;
  protected Scoreboard scoreboard = null;
  PacketInjector injector;
  private DBClassLoader classLoader;
  BungeeCordManager BungeeCordManager;
  static boolean shuttingdown = false;
  CMITask lagTask = null;

  public PacketInjector getPacketInjector() {
    return this.injector;
  }

  protected String getWorldFolderPath() {
    if (this.worldFolderPath == null) {
      this.worldFolderPath =
          Bukkit.getWorldContainer().getAbsolutePath()
              + File.separator
              + ((World) Bukkit.getWorlds().get(0)).getName()
              + File.separator;
    }

    return this.worldFolderPath;
  }

  public static CMI getInstance() {
    if (instance == null) {
      instance = (CMI) JavaPlugin.getPlugin(CMI.class);
    }

    return instance;
  }

  public DBClassLoader getDBClassloader() {
    return this.classLoader;
  }

  public void setDBClassloader() {
    this.classLoader = new DBClassLoader(this);
  }

  public HashMap<String, List<String>> preFetchNames() {
    return this.preFetchNames;
  }

  public HashMap<String, UUID> preFetchUUIDS() {
    return this.preFetchUUIDs;
  }

  public NMS getNMS() {
    return this.nms;
  }

  @Deprecated
  public Reflections getRef() {
    return this.getReflectionManager();
  }

  public Reflections getReflectionManager() {
    if (this.ref == null) {
      this.ref = new Reflections(this);
    }

    return this.ref;
  }

  public CMIMirrorManager getMirrorManager() {
    if (this.mirrorV2 == null) {
      this.mirrorV2 = new CMIMirrorManager(this);
    }

    return this.mirrorV2;
  }

  public DialogManager getDialogManager() {
    if (this.DialogManager == null) {
      this.DialogManager = new DialogManager(this);
    }

    return this.DialogManager;
  }

  public BlueMapManager getBlueMapManager() {
    if (this.BlueMapManager == null) {
      this.BlueMapManager = new BlueMapManager(this);
    }

    return this.BlueMapManager;
  }

  public ConsoleFilterManager getConsoleFilterManager() {
    if (this.ConsoleFilterManager == null) {
      this.ConsoleFilterManager = new ConsoleFilterManager(this);
    }

    return this.ConsoleFilterManager;
  }

  public LookupService getLookupService() {
    if (!this.isEnabled()) {
      return this.LookupService;
    }

    long longValue = System.currentTimeMillis();
    if (this.LookupService == null) {
      try {
        File file = new File(this.getDataFolder(), "GeoLiteCity.dat");
        if (file.isFile()) {
          this.LookupService = new LookupService(file, 2);
          if (file.isFile()) {
            this.loadMessage("-", "GeoLiteCity", System.currentTimeMillis() - longValue);
          }
        } else {
          if (this.getConfigManager().isAutoDownloadGeoLiteCity()) {
            new FileDownloader()
                .downloadUsingStream(
                    "https://www.zrips.net/wp-content/uploads/2019/02/GeoLiteCity.dat",
                    file.getPath());
          }

          file = new File(this.getDataFolder(), "GeoIP.dat");
          this.LookupService = new LookupService(file, 2);
          if (!file.isFile() && this.getConfigManager().isAutoDownloadGeoIp()) {
            new FileDownloader()
                .downloadUsingStream(
                    "https://www.zrips.net/wp-content/uploads/2019/02/GeoIP.dat", file.getPath());
          }

          if (file.isFile()) {
            this.loadMessage("-", "GeoIP", System.currentTimeMillis() - longValue);
          }
        }
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    }

    return this.LookupService;
  }

  public DiscordSRVManager getDiscordSRVManager() {
    if (this.DiscordSRVManager == null) {
      this.DiscordSRVManager = new DiscordSRVManager(this);
    }

    return this.DiscordSRVManager;
  }

  public ArmorStandManager getArmorStandManager() {
    if (this.ArmorStandManager == null) {
      this.ArmorStandManager = new ArmorStandManager(this);
    }

    return this.ArmorStandManager;
  }

  public PlayerCombatManager getPlayerCombatManager() {
    if (this.PlayerCombatManager == null) {
      this.PlayerCombatManager = new PlayerCombatManager(this);
    }

    return this.PlayerCombatManager;
  }

  public DynMapManager getDynMapManager() {
    if (this.DynMapManager == null) {
      this.DynMapManager = new DynMapManager(this);
    }

    return this.DynMapManager;
  }

  public WarningManager getWarningManager() {
    if (this.WarningManager == null) {
      this.WarningManager = new WarningManager(this);
    }

    return this.WarningManager;
  }

  public EnchantManager getEnchantManager() {
    if (this.EnchantManager == null) {
      this.EnchantManager = new EnchantManager(this);
    }

    return this.EnchantManager;
  }

  public ChatBubbleManager getChatBubbleManager() {
    if (this.ChatBubbleManager == null) {
      this.ChatBubbleManager = new ChatBubbleManager(this);
    }

    return this.ChatBubbleManager;
  }

  public VotifierManager getVotifierManager() {
    if (this.VotifierManager == null) {
      this.VotifierManager = new VotifierManager(this);
    }

    return this.VotifierManager;
  }

  public ChatManager getChatManager() {
    if (this.ChatManager == null) {
      this.ChatManager = new ChatManager(this);
    }

    return this.ChatManager;
  }

  public CitizensManager getCitizensManager() {
    if (this.CitizensManager == null) {
      this.CitizensManager = new CitizensManager(this);
    }

    return this.CitizensManager;
  }

  public SpecializedCommandManager getSpecializedCommandManager() {
    if (this.SpecializedCommandManager == null) {
      this.SpecializedCommandManager = new SpecializedCommandManager(this);
    }

    return this.SpecializedCommandManager;
  }

  public InteractiveCommandManager getInteractiveCommandManager() {
    if (this.InteractiveCommandManager == null) {
      this.InteractiveCommandManager = new InteractiveCommandManager(this);
    }

    return this.InteractiveCommandManager;
  }

  public ChatFormatManager getChatFormatManager() {
    if (this.ChatFormatManager == null) {
      this.ChatFormatManager = new ChatFormatManager(this);
    }

    return this.ChatFormatManager;
  }

  public KitsManager getKitsManager() {
    if (this.kits == null) {
      this.kits = new KitsManager(this);
    }

    return this.kits;
  }

  public SpawnerChargeManager getSpawnerChargesManager() {
    if (this.charges == null) {
      this.charges = new SpawnerChargeManager();
    }

    return this.charges;
  }

  public SkinManager getSkinManager() {
    if (this.SkinManager == null) {
      this.SkinManager = new SkinManager(this);
    }

    return this.SkinManager;
  }

  public RecipeManager getRecipeManager() {
    if (this.RecipeManager == null) {
      this.RecipeManager = new RecipeManager(this);
    }

    return this.RecipeManager;
  }

  public PaintingManager getPaintingManager() {
    if (this.PaintingManager == null) {
      this.PaintingManager = new PaintingManager(this);
    }

    return this.PaintingManager;
  }

  public VanishManager getVanishManager() {
    if (this.VanishManager == null) {
      this.VanishManager = new VanishManager(this);
    }

    return this.VanishManager;
  }

  public PlayerOptionsManager getPlayerOptionsManager() {
    if (this.PlayerOptionsManager == null) {
      this.PlayerOptionsManager = new PlayerOptionsManager(this);
    }

    return this.PlayerOptionsManager;
  }

  public ParticleManager getParticleManager() {
    if (this.ParticleManager == null) {
      this.ParticleManager = new ParticleManager(this);
    }

    return this.ParticleManager;
  }

  public DeathMessageManager getDeathMessageManager() {
    if (this.DeathMessageManager == null) {
      this.DeathMessageManager = new DeathMessageManager(this);
    }

    return this.DeathMessageManager;
  }

  public FlightChargeManager getFlightChargeManager() {
    if (this.FlightChargeManager == null) {
      this.FlightChargeManager = new FlightChargeManager(this);
    }

    return this.FlightChargeManager;
  }

  public Lag getLagMeter() {
    if (this.lagMeter == null) {
      this.lagMeter = new Lag();
    }

    return this.lagMeter;
  }

  public EnderChestManager getEnderChestManager() {
    if (this.EnderChestManager == null) {
      this.EnderChestManager = new EnderChestManager(this);
    }

    return this.EnderChestManager;
  }

  public RegChestManager getRegChestManager() {
    if (this.RegChestManager == null) {
      this.RegChestManager = new RegChestManager(this);
    }

    return this.RegChestManager;
  }

  public HologramManager getHologramManager() {
    if (this.HologramManager == null) {
      this.HologramManager = new HologramManager(this);
    }

    return this.HologramManager;
  }

  public EconomyManager getEconomyManager() {
    if (this.economyManager == null) {
      this.economyManager = new EconomyManager(this);
    }

    return this.economyManager;
  }

  public CMICommandCostManager getCommandCostManager() {
    if (this.CMICommandCostManager == null) {
      this.CMICommandCostManager = new CMICommandCostManager(this);
    }

    return this.CMICommandCostManager;
  }

  public CooldownManager getCooldownManager() {
    if (this.cooldownManager == null) {
      this.cooldownManager = new CooldownManager(this);
    }

    return this.cooldownManager;
  }

  public PortalManager getPortalManager() {
    if (this.portalManager == null) {
      this.portalManager = new PortalManager(this);
    }

    return this.portalManager;
  }

  public SignManager getSignManager() {
    if (this.signManager == null) {
      this.signManager = new SignManager(this);
    }

    return this.signManager;
  }

  public SelectionManager getSelectionManager() {
    if (this.SelectionManager == null) {
      this.SelectionManager = new SelectionManager(this);
    }

    return this.SelectionManager;
  }

  public ArmorEffectManager getArmorEffectManager() {
    if (this.armorEffectManager == null) {
      this.armorEffectManager = new ArmorEffectManager(this);
    }

    return this.armorEffectManager;
  }

  public WarmUpManager getWarmUpManager() {
    if (this.WarmUpManager == null) {
      this.WarmUpManager = new WarmUpManager(this);
    }

    return this.WarmUpManager;
  }

  public WorldManager getRegionManager() {
    if (this.regionManager == null) {
      this.regionManager = new WorldManager(this);
    }

    return this.regionManager;
  }

  public ShulkerBoxManager getShulkerBoxManager() {
    if (this.shulkerBoxManager == null) {
      this.shulkerBoxManager = new ShulkerBoxManager(this);
    }

    return this.shulkerBoxManager;
  }

  public TimedCommandManager getTimedCommandManager() {
    if (this.timedCommandManager == null) {
      this.timedCommandManager = new TimedCommandManager(this);
    }

    return this.timedCommandManager;
  }

  public PermissionsManager getPermissionsManager() {
    if (this.permissionsManager == null) {
      this.permissionsManager = new PermissionsManager(this);
    }

    return this.permissionsManager;
  }

  public NickNameManager getNickNameManager() {
    if (this.nickNameManager == null) {
      this.nickNameManager = new NickNameManager(this);
    }

    return this.nickNameManager;
  }

  public TagManager getTagManager() {
    if (this.TagManager == null) {
      this.TagManager = new TagManager(this);
    }

    return this.TagManager;
  }

  public WarpManager getWarpManager() {
    if (this.warpManager == null) {
      this.warpManager = new WarpManager(this);
    }

    return this.warpManager;
  }

  public IpManager getIpManager() {
    if (this.ipManager == null) {
      this.ipManager = new IpManager(this);
    }

    return this.ipManager;
  }

  public PlayTimeRewardsManager getPlayTimeRewardManager() {
    if (this.PlayTimeRewardsManager == null) {
      this.PlayTimeRewardsManager = new PlayTimeRewardsManager(this);
    }

    return this.PlayTimeRewardsManager;
  }

  public TeleportManager getTeleportManager() {
    if (this.TeleportManager == null) {
      this.TeleportManager = new TeleportManager(this);
    }

    return this.TeleportManager;
  }

  public TeleportHandler getTeleportHandler() {
    if (this.TeleportHandler == null) {
      this.TeleportHandler = new TeleportHandler(this);
    }

    return this.TeleportHandler;
  }

  public StatsManager getStatsManager() {
    if (this.statsManager == null) {
      this.statsManager = new StatsManager(this);
    }

    return this.statsManager;
  }

  public JailManager getJailManager() {
    if (this.JailManager == null) {
      this.JailManager = new JailManager(this);
    }

    return this.JailManager;
  }

  public WorthManager getWorthManager() {
    if (this.WorthManager == null) {
      this.WorthManager = new WorthManager(this);
    }

    return this.WorthManager;
  }

  public AliasManager getAliasManager() {
    if (this.aliasManager == null) {
      this.aliasManager = new AliasManager(this);
    }

    return this.aliasManager;
  }

  public AfkManager getAfkManager() {
    if (this.AfkManager == null) {
      this.AfkManager = new AfkManager(this);
    }

    return this.AfkManager;
  }

  public TabListHeaderFooterHandler getTabListHandler() {
    if (this.TabListHandler == null) {
      this.TabListHandler = new TabListHeaderFooterHandler(this);
    }

    return this.TabListHandler;
  }

  public TabListManager getTabListManager() {
    if (this.TabListManager == null) {
      this.TabListManager = new TabListManager(this);
    }

    return this.TabListManager;
  }

  public CTextManager getCTextManager() {
    if (this.cTextManager == null) {
      this.cTextManager = new CTextManager(this);
    }

    return this.cTextManager;
  }

  public CustomNBTManager getCustomNBTManager() {
    if (this.CustomNBTManager == null) {
      this.CustomNBTManager = new CustomNBTManager(this);
    }

    return this.CustomNBTManager;
  }

  public EventActionManager getEventActionManager() {
    if (this.eventActionManager == null) {
      this.eventActionManager = new EventActionManager(this);
    }

    return this.eventActionManager;
  }

  public HomeManager getHomeManager() {
    if (this.homeManager == null) {
      this.homeManager = new HomeManager(this);
    }

    return this.homeManager;
  }

  public SchedulerManager getSchedulerManager() {
    if (this.schedulerManager == null) {
      this.schedulerManager = new SchedulerManager(this);
    }

    return this.schedulerManager;
  }

  public AnvilManager getAnvilManager() {
    if (this.anvilManager == null) {
      this.anvilManager = new AnvilManager(this);
    }

    return this.anvilManager;
  }

  public TotemManager getTotemManager() {
    if (this.totemManager == null) {
      this.totemManager = new TotemManager(this);
    }

    return this.totemManager;
  }

  public AnimationManager getAnimationManager() {
    if (this.AnimationManager == null) {
      this.AnimationManager = new AnimationManager(this);
    }

    return this.AnimationManager;
  }

  public FindBiomeManager getFindBiomeManager() {
    if (this.findBiomeManager == null) {
      this.findBiomeManager = new FindBiomeManager(this);
    }

    return this.findBiomeManager;
  }

  public ChatFilterManager getChatFilterManager() {
    if (this.chatFilterManager == null) {
      this.chatFilterManager = new ChatFilterManager(this);
    }

    return this.chatFilterManager;
  }

  public SavedInventoryManager getSavedInventoryManager() {
    if (this.SavedInventoryManager == null) {
      this.SavedInventoryManager = new SavedInventoryManager(this);
    }

    return this.SavedInventoryManager;
  }

  public BungeeCordManager getBungeeCordManager() {
    if (this.BungeeCordManager == null) {
      this.BungeeCordManager = new BungeeCordManager(this);
    }

    return this.BungeeCordManager;
  }

  public PatrolManager getPatrolManager() {
    if (this.PatrolManager == null) {
      this.PatrolManager = new PatrolManager(this);
    }

    return this.PatrolManager;
  }

  public RankManager getRankManager() {
    if (this.rankManager == null) {
      this.rankManager = new RankManager(this);
    }

    return this.rankManager;
  }

  public PlayerManager getPlayerManager() {
    if (this.PM == null) {
      this.PM = new PlayerManager(this);
    }

    return this.PM;
  }

  public Sorting getSortingManager() {
    if (this.Sorting == null) {
      this.Sorting = new Sorting();
    }

    return this.Sorting;
  }

  public TimeManager getTimeManager() {
    if (this.TimeManager == null) {
      this.TimeManager = new TimeManager(this);
    }

    return this.TimeManager;
  }

  public VersionChecker getVersionCheckManager() {
    if (this.versionCheckManager == null) {
      this.versionCheckManager = new VersionChecker(this);
    }

    return this.versionCheckManager;
  }

  public CommandsHandler getCommandManager() {
    if (this.cManager == null) {
      this.cManager = new CommandsHandler(this);
    }

    return this.cManager;
  }

  public ElytraManager getElytraManager() {
    if (this.ElytraManager == null) {
      this.ElytraManager = new ElytraManager(this);
    }

    return this.ElytraManager;
  }

  public RepairManager getRepairManager() {
    if (this.RepairManager == null) {
      this.RepairManager = new RepairManager(this);
    }

    return this.RepairManager;
  }

  public RandomTeleportationManager getRandomTeleportationManager() {
    if (this.RandomTeleportationManager == null) {
      this.RandomTeleportationManager = new RandomTeleportationManager(this);
    }

    return this.RandomTeleportationManager;
  }

  public SavedItemManager getSavedItemManager() {
    if (this.SavedItemManager == null) {
      this.SavedItemManager = new SavedItemManager(this);
    }

    return this.SavedItemManager;
  }

  public PlayTimeManager getPlayTimeManager() {
    if (this.PlayTimeManager == null) {
      this.PlayTimeManager = new PlayTimeManager(this);
    }

    return this.PlayTimeManager;
  }

  public LightFix getLightFixManager() {
    if (this.LightFixManager == null) {
      this.LightFixManager = new LightFix(this);
    }

    return this.LightFixManager;
  }

  public ChunkPreview getChunkPreviewManager() {
    if (this.ChunkPreviewManager == null) {
      this.ChunkPreviewManager = new ChunkPreview(this);
    }

    return this.ChunkPreviewManager;
  }

  public CMIWorldChunkManager getScanManager() {
    if (this.ScanManager == null) {
      this.ScanManager = new CMIWorldChunkManager(this);
    }

    return this.ScanManager;
  }

  public ChunkFix getChunkFixManager() {
    if (this.ChunkFixManager == null) {
      this.ChunkFixManager = new ChunkFix(this);
    }

    return this.ChunkFixManager;
  }

  public ReplaceBlock getReplaceBlockManager() {
    if (this.replaceblock == null) {
      this.replaceblock = new ReplaceBlock(this);
    }

    return this.replaceblock;
  }

  public UnloadChunks getUnloadChunksManager() {
    if (this.unloadchunks == null) {
      this.unloadchunks = new UnloadChunks(this);
    }

    return this.unloadchunks;
  }

  public Purge getPurgeManager() {
    if (this.PurgeManager == null) {
      this.PurgeManager = new Purge(this);
    }

    return this.PurgeManager;
  }

  public Util getUtilManager() {
    if (this.UtilManager == null) {
      this.UtilManager = new Util(this);
    }

    return this.UtilManager;
  }

  public Config getConfigManager() {
    if (this.config == null) {
      this.config = new Config(this);
    }

    return this.config;
  }

  public ScavengeManager getScavengeManager() {
    if (this.scavengeManager == null) {
      this.scavengeManager = new ScavengeManager(this);
    }

    return this.scavengeManager;
  }

  public DBManager getDbManager() {
    if (this.dbManager == null) {
      this.dbManager = new DBManager(this);
    }

    return this.dbManager;
  }

  public DBManager_NEW getDbManager_NEW() {
    if (this.dbManager_new == null) {
      this.dbManager_new = new DBManager_NEW(this);
    }

    return this.dbManager_new;
  }

  public Language getLM() {
    if (this.languageManager == null) {
      this.initLM();
    }

    return this.languageManager;
  }

  public void initLM() {
    this.languageManager = new Language(this);
    this.languageManager.reload();
  }

  public static boolean isShuttingDown() {
    return shuttingdown;
  }

  public void onDisable() {
    shuttingdown = true;

    try {
      if (Config.UseFakeOperator && this.getPlayerManager().getFakeOperatorRaw() != null) {
        Player player = this.getPlayerManager().getFakeOperator();
        if (player.isOp()) {
          player.setOp(false);
        }
      }

      for (OfflinePlayer offlinePlayer : Bukkit.getOperators()) {
        if (offlinePlayer.getName().equalsIgnoreCase(this.getPlayerManager().getFakeUserName())) {
          offlinePlayer.setOp(false);
        }
      }
    } catch (Throwable throwable) {
    }

    try {
      this.getPlayTimeManager().updatePlayTimes();
    } catch (Throwable throwable2) {
    }

    try {
      this.getStatsManager().stop();
    } catch (Throwable throwable3) {
    }

    try {
      this.getHologramManager().hideAllHolograms();
    } catch (Throwable throwable4) {
    }

    try {
      this.getHologramManager().stop();
    } catch (Throwable throwable5) {
    }

    try {
      this.getArmorEffectManager().onDisable();
    } catch (Throwable throwable6) {
    }

    try {
      this.getEconomyManager().closeStream();
    } catch (Throwable throwable7) {
    }

    CMIBackUpManager.onStop();

    try {
      if (this.getDbManager() != null) {
        this.getDbManager().stop();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
          CMIUser cmiUser = this.getPlayerManager().getUser(onlinePlayer);
          cmiUser.setLastLogoff(System.currentTimeMillis());
          cmiUser.addForDelayedSave();
        }

        this.getDbManager().saveBatch(true);
      }
    } catch (Throwable throwable8) {
      throwable8.printStackTrace();
    }

    try {
      if (this.totemManager != null) {
        this.getTotemManager().hideAllBars();
      }
    } catch (Throwable throwable9) {
    }

    try {
      if (this.shulkerBoxManager != null) {
        this.getShulkerBoxManager().closeAll();
      }
    } catch (Throwable throwable10) {
    }

    try {
      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        CMIUser onlineUser = this.getPlayerManager().getUser(onlinePlayer);
        if (onlineUser != null) {
          onlineUser.hideBossBars();
          if (onlineUser.isSitting()) {
            this.getAnimationManager().removePlayerFromChair(onlinePlayer);
          }

          if (this.getAnimationManager().isRiding(onlinePlayer.getUniqueId())) {
            onlinePlayer.leaveVehicle();
          }
        }
      }
    } catch (Throwable throwable11) {
    }

    try {
      for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
        CMIUser onlineUser = this.getPlayerManager().getUser(onlinePlayer);
        if (onlineUser != null) {
          Team team = this.scoreboard.getTeam("CMINP" + onlineUser.getId());
          if (team != null) {
            try {
              team.unregister();
            } catch (Throwable throwable12) {
              throwable12.printStackTrace();
            }
          }
        }
      }
    } catch (Throwable throwable13) {
      throwable13.printStackTrace();
    }

    try {
      if (this.regionManager != null) {
        this.getRegionManager().cancelLoading();
      }
    } catch (Throwable throwable14) {
    }

    try {
      if (this.getInteractiveCommandManager().getSaveTask() != null) {
        this.getInteractiveCommandManager().getSaveTask().cancel();
        this.getInteractiveCommandManager().forceSave();
      }
    } catch (Throwable throwable15) {
    }

    try {
      this.getSignManager().stop();
    } catch (Throwable throwable16) {
    }

    try {
      this.getAfkManager().stop();
    } catch (Throwable throwable17) {
    }

    try {
      this.getPortalManager().stop();
    } catch (Throwable throwable18) {
    }

    try {
      this.getRankManager().stop();
    } catch (Throwable throwable19) {
    }

    try {
      this.getSchedulerManager().stop();
    } catch (Throwable throwable20) {
    }

    try {
      this.getTabListManager().stop();
    } catch (Throwable throwable21) {
    }

    try {
      this.getLookupService().close();
    } catch (Throwable throwable22) {
    }

    try {
      this.getWorthManager().closeStream();
    } catch (Throwable throwable23) {
    }

    try {
      this.getKitsManager().onDisable();
    } catch (Throwable throwable24) {
    }

    try {
      this.getWarpManager().onDisable();
    } catch (Throwable throwable25) {
    }

    try {
      if (this.AnimationManager != null) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
          this.getAnimationManager().removePlayerFromChair(onlinePlayer, false);
          if (this.getAnimationManager().isBeingRiden(onlinePlayer.getUniqueId())) {
            this.getAnimationManager().removePassengers(onlinePlayer, null);
          }
        }
      }
    } catch (Throwable throwable26) {
    }

    try {
      if (this.lagTask != null) {
        this.lagTask.cancel();
      }
    } catch (Throwable throwable27) {
    }

    try {
      this.getDbManager().closeConnections();
      this.consoleMessage("Closed db connections");
    } catch (Throwable throwable28) {
    }

    try {
      this.getEnchantManager().reEnableEnchantsOnDisable();
    } catch (Throwable throwable29) {
    }

    CMIThread.shutdown();
  }

  public Scoreboard getSB() {
    return this.scoreboard;
  }

  public boolean safeRenameFile(String string, String newPath) {
    File file = new File(this.getDataFolder(), string);
    if (file.exists() && file.isFile()) {
      File newFile = new File(this.getDataFolder(), newPath);
      if (!newFile.isFile()) {
        try {
          new ConfigReader(this, string).saveToBackup(false);
        } catch (Exception exception) {
          exception.printStackTrace();
        }

        if (file.renameTo(newFile)) {
          CMIMessages.consoleMessage(
              "  &2Successfully renamed/moved file! " + string + " -> " + newPath);
          return true;
        }
      }

      return false;
    } else {
      return false;
    }
  }

  public void renameDirectory(String string, String newPath) {
    File file = new File(string);
    if (file.exists() && file.isDirectory()) {
      File newDir = new File(newPath);
      if (file.renameTo(newDir)) {
        CMIMessages.consoleMessage("Successfully renamed folder!");
      }
    } else {
      CMIMessages.consoleMessage("Directory does not exist: " + string);
    }
  }

  public void Enabled(boolean flag) {
    this.setEnabled(flag);
  }

  public void onEnable() {
    // Spigot license check removed (phone-home); see NOTICE.md
    instance = this;

    try {
      String string = Version.getCurrent().name();
      if (Version.isMojangMappings()
          || Version.isPaperBranch() && Version.isCurrentEqualOrHigher(Version.v1_21_R6)) {
        string = "MojangMappings";
      }

      var nbtClass = Class.forName("com.Zrips.CMI.NBT." + string);
      if (NMS.class.isAssignableFrom(nbtClass)) {
        this.nms = (NMS) nbtClass.getConstructor().newInstance();
      } else {
        CMIMessages.consoleMessage(
            "Something went wrong, please note down version and contact author v:"
                + Version.getCurrent().name());
        this.setEnabled(false);
      }
    } catch (Throwable throwable) {
      CMIMessages.consoleMessage(
          "Your server version is not compatible with this plugins version! Plugin will be"
              + " disabled: "
              + Version.getCurrent().name());
      throwable.printStackTrace();
      this.setEnabled(false);
      return;
    }

    CMIOnEnable.init(this);
  }

  public void consoleMessage(String string) {
    ConsoleCommandSender consoleCommandSender = Bukkit.getConsoleSender();
    if (Config.monochromeConsole) {
      string = CMIChatColor.stripColor(string);
      if (this.fullyLoaded) {
        consoleCommandSender.sendMessage(
            CMIChatColor.stripColor(this.prefix) + (string == null ? null : string));
      } else {
        consoleCommandSender.sendMessage(string);
      }
    } else {
      if (this.fullyLoaded) {
        consoleCommandSender.sendMessage(
            this.prefix + (string == null ? null : CMIChatColor.translate(string)));
      } else {
        consoleCommandSender.sendMessage(CMIChatColor.translate("&3" + string));
      }
    }
  }

  public void loadMessage(Object object, String string, Long elapsedMillis) {
    if (this.isEnabled()) {
      this.consoleMessage(
          "&3Loaded (&f"
              + object
              + "&3) &7"
              + string
              + " &3into memory. &6Took &e"
              + elapsedMillis
              + "&6ms");
    }
  }

  @Deprecated
  public String getOffOn(Player player, Player other) {
    return CMILC.getOffOn(player, other);
  }

  @Deprecated
  public String getOffOn(Player player) {
    return CMILC.getOffOn(player);
  }

  @Deprecated
  public String getOffOn(boolean flag) {
    return CMILC.getOffOn(flag);
  }

  public void save(Player player) {
    if (player != null
        && !player.getName().equalsIgnoreCase(this.getPlayerManager().getFakeUserName())
        && !player.isOnline()) {
      player.saveData();
    }
  }

  public Player getTarget(
      CommandSender commandSender, String string, Object object, boolean flag, boolean notify) {
    return this.getTarget(commandSender, string, object.getClass().getSimpleName(), flag, notify);
  }

  public Player getTarget(CommandSender commandSender, String string, Object object) {
    return this.getTarget(commandSender, string, object.getClass().getSimpleName(), true, true);
  }

  public Player getTarget(CommandSender commandSender, String string, Object object, boolean flag) {
    return this.getTarget(commandSender, string, object.getClass().getSimpleName(), flag, true);
  }

  public Player getTarget(CommandSender commandSender, String string, String commandName) {
    return this.getTarget(commandSender, string, commandName, true, true);
  }

  public Player getTarget(
      CommandSender commandSender, String string, String commandName, boolean flag) {
    return this.getTarget(commandSender, string, commandName, flag, true);
  }

  public Player getTarget(
      CommandSender commandSender,
      String string,
      String commandName,
      boolean flag,
      boolean notify) {
    CMIUser cmiUser = this.getUser(commandSender, string, commandName, notify, flag);
    return cmiUser == null ? null : cmiUser.getPlayer();
  }

  public CMIUser getUser(
      CommandSender commandSender, String string, Object object, boolean flag, boolean notify) {
    return this.getUser(
        commandSender, string, object.getClass().getSimpleName(), flag, notify, true);
  }

  public CMIUser getUser(CommandSender commandSender, String string, Object object) {
    return this.getUser(commandSender, string, object.getClass().getSimpleName(), true, true, true);
  }

  public CMIUser getUser(CommandSender commandSender, String string, Object object, boolean flag) {
    return this.getUser(commandSender, string, object.getClass().getSimpleName(), true, flag, true);
  }

  public CMIUser getUser(CommandSender commandSender, String string, String commandName) {
    return this.getUser(commandSender, string, commandName, true, true, true);
  }

  public CMIUser getUser(
      CommandSender commandSender,
      String string,
      String commandName,
      boolean flag,
      boolean notify) {
    return this.getUser(commandSender, string, commandName, flag, notify, true);
  }

  public CMIUser getUser(
      CommandSender commandSender,
      String string,
      String commandName,
      boolean flag,
      boolean notify,
      boolean silent) {
    CMIUser cmiUser = null;
    if (string != null) {
      if (!string.equalsIgnoreCase(commandSender.getName()) && notify) {
        if (!PermissionsManager.CMIPerm.command_$1_others.hasPermission(
            commandSender, silent, commandName)) {
          return null;
        }

        cmiUser = this.getPlayerManager().getUser(string, true, false, false, false, true);
      } else {
        cmiUser = this.getPlayerManager().getUser(string, true, false, false, false, true);
      }
    } else if (commandSender instanceof Player) {
      cmiUser = this.getPlayerManager().getUser((Player) commandSender);
    }

    if (cmiUser == null) {
      if (flag) {
        if (commandSender instanceof Player) {
          LC.info_NoPlayer.sendMessage(commandSender, new Object[] {"[name]", string});
        } else {
          CMILC.sendMessage(
              commandSender,
              LC.info_NoPlayer.getLocale(new Object[0])
                  + " ("
                  + commandName
                  + " -> "
                  + string
                  + ")");
        }
      }

      return null;
    } else if (string != null
        && !cmiUser.getName().equalsIgnoreCase(string)
        && !cmiUser.getName().equalsIgnoreCase(commandSender.getName())
        && cmiUser.isOnline()
        && commandSender instanceof Player
        && !((Player) commandSender).canSee(cmiUser.getPlayer())) {
      if (commandSender instanceof Player) {
        LC.info_NoPlayer.sendMessage(commandSender, new Object[0]);
      } else {
        CMILC.sendMessage(
            commandSender,
            LC.info_NoPlayer.getLocale(new Object[0]) + " (" + commandName + " -> " + string + ")");
      }

      return null;
    } else {
      return cmiUser;
    }
  }

  @Deprecated
  public Player getPlayer(String string) {
    return CMIUser.getPlayer(string);
  }

  public Placeholder getPlaceholderAPIManager() {
    if (this.Placeholder == null) {
      this.Placeholder = new Placeholder(this);
    }

    return this.Placeholder;
  }

  public long getTimer() {
    return this.timer;
  }

  public void setTimer(long longValue) {
    this.timer = longValue;
  }

  public TabComplete getTab() {
    if (this.tab == null) {
      this.tab = new TabComplete(this);
    }

    return this.tab;
  }

  public int getViewRange(World world) {
    int value = Bukkit.getServer().getViewDistance();
    if (Version.isSpigot()) {
      try {
        value = Bukkit.spigot().getConfig().getInt("world-settings.default.view-distance", value);
        if (world != null
            && Bukkit.spigot()
                .getConfig()
                .isInt("world-settings." + world.getName() + ".view-distance")) {
          value =
              Bukkit.spigot()
                  .getConfig()
                  .getInt("world-settings." + world.getName() + ".view-distance", value);
        }
      } catch (Throwable throwable) {
        throwable.printStackTrace();
      }
    }

    return value;
  }

  @Deprecated
  public void performCommand(CommandSender commandSender, Object object) {
    this.performCommand(commandSender, object, "");
  }

  public void performCommand(CommandSender commandSender, Class<?> commandClass) {
    this.performCommand(
        commandSender, CommandsHandler.getLabel() + " " + commandClass.getSimpleName());
  }

  @Deprecated
  public void performCommand(CommandSender commandSender, Object object, String string) {
    this.performCommand(
        commandSender,
        CommandsHandler.getLabel()
            + " "
            + object.getClass().getSimpleName()
            + (string.isEmpty() ? "" : " " + string));
  }

  public void performCommand(CommandSender commandSender, Class<?> commandClass, String string) {
    this.performCommand(
        commandSender,
        CommandsHandler.getLabel()
            + " "
            + commandClass.getSimpleName()
            + (string.isEmpty() ? "" : " " + string));
  }

  public void performCommand(CommandSender commandSender, String string) {
    if (Version.isFolia()) {
      if (commandSender instanceof Player) {
        CMIScheduler.runAtLocation(
            getInstance(),
            ((Player) commandSender).getLocation(),
            () -> {
              if (((Player) commandSender).isOnline()) {
                CMICommand.performCommand(commandSender, string, CommandType.silent);
              }
            });
      } else {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), string);
      }
    } else {
      if (commandSender instanceof Player) {
        if (((Player) commandSender).isOnline()) {
          CMICommand.performCommand(commandSender, string, CommandType.silent);
        }
      } else {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), string);
      }
    }
  }

  public boolean isFullyLoaded() {
    return this.fullyLoaded;
  }

  public void setFullyLoaded(boolean flag) {
    this.fullyLoaded = flag;
  }

  public UUID getServerUUID() {
    return ServerUUID;
  }

  public long getServerStartupTime() {
    return serverStartupTime;
  }

  public Permission getVaultPerm() {
    return this.vaultPerm;
  }

  public void setVaultPerm(Permission value) {
    this.vaultPerm = value;
  }

  public boolean isServerLoaded() {
    return this.serverLoaded;
  }
}
