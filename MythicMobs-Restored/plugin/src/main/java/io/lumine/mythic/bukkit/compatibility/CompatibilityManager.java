package io.lumine.mythic.bukkit.compatibility;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

public class CompatibilityManager implements Terminable {
   @Getter
   private Optional<CosmeticsSupport> cosmetics = Optional.empty();
   @Getter
   private Optional<CrucibleSupport> crucible = Optional.empty();
   @Getter
   private Optional<MythicRPGSupport> rpg = Optional.empty();
   @Getter
   private Optional<CitizensSupport> citizens = Optional.empty();
   @Getter
   private Optional<CrashClaimSupport> crashClaim = Optional.empty();
   @Getter
   private Optional<GriefPreventionSupport> griefPrevention = Optional.empty();
   @Getter
   private Optional<LandsSupport> lands = Optional.empty();
   @Getter
   private Optional<PlotSquaredSupport> plotSquared = Optional.empty();
   @Getter
   private Optional<MPetCompat> miniaturePets = Optional.empty();
   @Getter
   private Optional<MMOItemsSupport> MMOItems = Optional.empty();
   @Getter
   private Optional<MythicLibSupport> mythicLib = Optional.empty();
   @Getter
   private Optional<NoCheatPlusSupport> noCheatPlus = Optional.empty();
   @Getter
   private Optional<OpenTerrainGeneratorSupport> openTerrainGenerator = Optional.empty();
   @Getter
   private Optional<PlaceholderAPISupport> placeholderAPI = Optional.empty();
   @Getter
   private Optional<ResidenceSupport> residence = Optional.empty();
   @Getter
   private Optional<VaultSupport> vault = Optional.empty();
   @Getter
   private Optional<WorldGuardSupport> worldGuard = Optional.empty();
   @Getter
   private Optional<AriKeysSupport> ariKeys = Optional.empty();
   @Getter
   private Optional<ShopGUIPlusSupport> shopGUIPlus = Optional.empty();
   private final List<ClaimSupport> claimSupports = new ArrayList<>();
   @Getter @Setter
   private Optional<AbstractModelEngineSupport> ModelEngine = Optional.empty();
   public static HeroesSupport Heroes = null;
   public static LibsDisguisesSupport LibsDisguises = null;
   public static mcMMOSupport mcMMO = null;
   public static MythicDropsSupport MythicDrops = null;
   public static PhatLootsSupport PhatLoots = null;
   public static SkillAPISupport SkillAPI = null;

   public CompatibilityManager(MythicBukkit core) {
      this.registerCompatibility("ModelEngine", () -> ModelEngineNavigationHook.register(core));
      this.registerCompatibility("Citizens", () -> this.citizens = Optional.of(new CitizensSupport()));
      this.registerCompatibility("AriKeys", () -> this.ariKeys = Optional.of(new AriKeysSupport(core)));
      this.registerClaimCompatibility("GriefPrevention", () -> new GriefPreventionSupport(core), support -> this.griefPrevention = Optional.of(support));
      this.registerCompatibility("Heroes", () -> Heroes = new HeroesSupport());
      this.registerClaimCompatibility("Lands", () -> new LandsSupport(core), support -> this.lands = Optional.of(support));
      this.registerCompatibility("LibsDisguises", () -> LibsDisguises = new LibsDisguisesSupport());
      this.registerCompatibility("mcMMO", () -> mcMMO = new mcMMOSupport());
      this.registerCompatibility("MiniaturePets", () -> this.miniaturePets = Optional.of(new MPetCompat()));
      this.registerCompatibility("MMOItems", () -> this.MMOItems = Optional.of(new MMOItemsSupport(core)));
      this.registerCompatibility("MythicDrops", () -> MythicDrops = new MythicDropsSupport());
      this.registerCompatibility("NoCheatPlus", () -> this.noCheatPlus = Optional.of(new NoCheatPlusSupport()));
      this.registerCompatibility("OpenTerrainGenerator", () -> this.openTerrainGenerator = Optional.of(new OpenTerrainGeneratorSupport()));
      this.registerCompatibility("PhatLoots", () -> PhatLoots = new PhatLootsSupport());
      this.registerCompatibility("PlaceholderAPI", () -> this.placeholderAPI = Optional.of(new PlaceholderAPISupport(core)));
      this.registerClaimCompatibility("CrashClaim", () -> new CrashClaimSupport(core), support -> this.crashClaim = Optional.of(support));
      this.registerClaimCompatibility("Residence", () -> new ResidenceSupport(core), support -> this.residence = Optional.of(support));
      this.registerClaimCompatibility("PlotSquared", () -> new PlotSquaredSupport(core), support -> this.plotSquared = Optional.of(support));
      this.registerCompatibility("SkillAPI", () -> SkillAPI = new SkillAPISupport());
      this.registerCompatibility("Vault", () -> this.vault = Optional.of(new VaultSupport()));
      this.registerCompatibility("WorldGuard", () -> this.worldGuard = Optional.of(new WorldGuardSupport()));
      this.registerCompatibility("ShopGUIPlus", () -> this.shopGUIPlus = Optional.of(new ShopGUIPlusSupport(core)));
   }

   @Override
   public void close() {
   }

   public boolean isLocationClaimed(AbstractLocation location) {
      for (ClaimSupport claimSupport : this.claimSupports) {
         if (claimSupport.isLocationClaimed(location)) {
            return true;
         }
      }

      return false;
   }

   public boolean isNearClaim(AbstractLocation location, int radius) {
      for (ClaimSupport claimSupport : this.claimSupports) {
         if (claimSupport.isNearClaim(location, radius)) {
            return true;
         }
      }

      return false;
   }

   private void registerCompatibility(String name, Runnable run) {
      try {
         if (Bukkit.getPluginManager().getPlugin(name) != null) {
            run.run();
            MythicLogger.log("Mythic " + name + " Support has been enabled!");
         }
      } catch (NoClassDefFoundError e) {
         MythicLogger.errorCompatibility(name, "Plugin not found/incompatible version");
         MythicLogger.handleMinorError(e);
      } catch (Exception ex) {
         MythicLogger.error("Failed to enable support for " + name + ". Is it up to date?");
         ex.printStackTrace();
      }
   }

   private <T extends ClaimSupport> void registerClaimCompatibility(String name, Supplier<T> supplier, Consumer<T> assign) {
      this.registerCompatibility(name, () -> {
         T support = supplier.get();
         assign.accept(support);
         this.claimSupports.add(support);
      });
   }

   public void setupCosmeticsBridge(CosmeticsSupport bridge) {
      this.cosmetics = Optional.of(bridge);
      MythicLogger.log("MythicCosmetics Support has been enabled!");
   }

   public void setupCrucibleBridge(CrucibleSupport bridge) {
      this.crucible = Optional.of(bridge);
      MythicLogger.log("MythicCrucible Support has been enabled!");
   }

   public void setupRPGBridge(MythicRPGSupport bridge) {
      this.rpg = Optional.of(bridge);
      MythicLogger.log("MythicRPG Support has been enabled!");
   }

   public void setupMMOBridge(MythicLibSupport bridge) {
      this.mythicLib = Optional.of(bridge);
      MythicLogger.log("MMO Plugin Support has been enabled!");
   }
}
