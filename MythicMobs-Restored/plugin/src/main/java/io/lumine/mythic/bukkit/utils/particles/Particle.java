package io.lumine.mythic.bukkit.utils.particles;

import io.lumine.mythic.bukkit.utils.logging.Log;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public enum Particle {
   EXPLOSION_NORMAL("poof", "explode", "explosion", "explosion_small"),
   EXPLOSION_LARGE("largeexplode", "largeexplosion"),
   EXPLOSION_HUGE("explosion_emitter", "hugeexplode", "hugeexplosion"),
   FIREWORKS_SPARK("firework", "fireworksspark"),
   WATER_BUBBLE("bubble"),
   WATER_SPLASH("splash"),
   WATER_WAKE("fishing", "wake"),
   SUSPENDED("underwater"),
   SUSPENDED_DEPTH("underwater", "depthsuspend"),
   CRIT("crit"),
   CRIT_MAGIC("enchanted_hit", "magiccrit"),
   SMOKE_NORMAL("smoke"),
   SMOKE_LARGE("large_smoke", "largesmoke"),
   SPELL("effect"),
   SPELL_INSTANT("instant_effect", "instantSpell"),
   SPELL_MOB("entity_effect", "mobSpell"),
   SPELL_MOB_AMBIENT("ambient_entity_effect", "mobSpellAmbient"),
   SPELL_WITCH("witch", "witchMagic"),
   DRIP_WATER("dripping_water", "dripWater"),
   DRIP_LAVA("dripping_lava", "dripLava"),
   VILLAGER_ANGRY("angry_villager", "angryVillager"),
   VILLAGER_HAPPY("happy_villager", "happyVillager"),
   TOWN_AURA("mycelium", "townaura"),
   NOTE("note"),
   PORTAL("portal"),
   ENCHANTMENT_TABLE("enchant", "enchantmenttable", "enchantingtable"),
   FLAME("flame"),
   LAVA("lava"),
   CLOUD("cloud"),
   REDSTONE("dust", "reddust"),
   SNOWBALL("item_snowball", "snowballpoof"),
   SNOW_SHOVEL("item_snowball", "snowshovel"),
   SLIME("item_slime"),
   HEART("heart"),
   BARRIER("barrier"),
   ITEM_CRACK("item", "iconcrack", "itemcrack"),
   BLOCK_CRACK("block", "blockcrack"),
   BLOCK_DUST("dust", "blockdust"),
   WATER_DROP("rain", "droplet"),
   MOB_APPEARANCE("elder_guardian", "mobappearance"),
   DRAGON_BREATH("dragon_breath", "dragonbreath"),
   END_ROD("end_rod", "endRod"),
   DAMAGE_INDICATOR("damage_indicator", "damageIndicator"),
   SWEEP_ATTACK("sweep_attack", "sweepAttack"),
   FALLING_DUST("falling_dust", "fallingDust"),
   TOTEM("totem_of_undying"),
   SPIT("spit"),
   SQUID_INK("squid_ink", "squidink"),
   BUBBLE_POP("bubble_pop", "bubblepop"),
   CURRENT_DOWN("current_down", "currentdown"),
   BUBBLE_COLUMN_UP("bubble_column_up", "bubblecolumn", "bubble_column"),
   NAUTILUS("nautilus"),
   DOLPHIN("dolphin"),
   COMPOSTER("composter"),
   FALLING_LAVA("fallinglava", "falling_lava"),
   FALLING_WATER("fallingwater", "falling_water"),
   FLASH("flash"),
   LANDING_LAVA("landinglava", "landing_lava"),
   SNEEZE("sneeze"),
   CAMPFIRE_COSY_SMOKE("campfire_cosy_smoke", "campfire_cosy", "campfire_cozy_smoke", "campfire_cozy", "campfire"),
   CAMPFIRE_SIGNAL_SMOKE("campfire_signal_smoke", "campfire_signal"),
   DRIPPING_HONEY("drippinghoney"),
   FALLING_HONEY("fallinghoney"),
   FALLING_NECTAR("fallingnectar"),
   LANDING_HONEY("landinghoney"),
   ASH("ash"),
   WHITE_ASH(),
   CRIMSON_SPORE("crimson_spore", "crimson_spores", "crimsonspore", "crimsonspores"),
   SOUL_FIRE_FLAME("soul_fire_flame", "soulfireflame", "soulfire", "soulflame"),
   WARPED_SPORE("warped_spore", "warped_spores", "warpedspore", "warpedspores"),
   DRIPPING_OBSIDIAN_TEAR("dropping_obsidian_tear", "drippingobsidiantear"),
   FALLING_OBSIDIAN_TEAR("falling_obsidian_tear", "fallingobsidiantear"),
   LANDING_OBSIDIAN_TEAR("landing_obsidian_tear", "landingobsidiantear"),
   SOUL("soul"),
   DRIPPING_DRIPSTONE_LAVA("drippingdripstonelava", "droppingdripstonelava"),
   DRIPPING_DRIPSTONE_WATER("drippingdripstonewater", "droppingdripstonewater"),
   DUST_COLOR_TRANSITION("dustcolortransition", "dustcolor"),
   ELECTRIC_SPARK("electricspark", "spark"),
   FALLING_DRIPSTONE_LAVA(),
   FALLING_DRIPSTONE_WATER(),
   GLOW(),
   GLOW_SQUID_INK("glowingink", "glowink", "glowsquid"),
   LIGHT(),
   REVERSE_PORTAL(),
   SCRAPE(),
   SMALL_FLAME(),
   VIBRATION(),
   WAX_OFF(),
   WAX_ON(),
   FALLING_SPORE_BLOSSOM("fallingsporeblossom"),
   SPORE_BLOSSOM_AIR("sporeblossomair"),
   BLOCK_MARKER("blockmarker"),
   SCULK_SOUL("skulksoul"),
   SHRIEK(),
   SCULK_CHARGE("sculkcharge"),
   SCULK_CHARGE_POP("sculkchargepop"),
   SONIC_BOOM("sonicboom"),
   CHERRY_LEAVES("cherryleaves"),
   DRIPPING_CHERRY_LEAVES("drippingcherryleaves"),
   FALLING_CHERRY_LEAVES("fallingcherryleaves"),
   LANDING_CHERRY_LEAVES("landingcherryleaves"),
   EGG_CRACK("eggcrack");

   private static final Map<String, Particle> PARTICLE_ALIASES = new HashMap<>();
   private final String[] aliases;

   public static Particle fromString(String key) {
      Particle particle = PARTICLE_ALIASES.getOrDefault(key.toUpperCase(), null);
      if (particle == null) {
         Log.error("Particle '" + key + "' is not supported by this version.");
         return CLOUD;
      } else {
         return particle;
      }
   }

   private Particle(String... aliases) {
      this.aliases = aliases;
   }

   public ParticleBuilder create() {
      return ParticleBuilder.of(this);
   }

   public org.bukkit.Particle toBukkitParticle() {
      return org.bukkit.Particle.valueOf(this.toString());
   }

   public boolean requiresData() {
      return !this.toBukkitParticle().getDataType().equals(Void.class);
   }

   public boolean isDustParticle() {
      return this.toBukkitParticle().getDataType() == DustOptions.class;
   }

   public boolean validateData(Object obj) {
      org.bukkit.Particle particle = this.toBukkitParticle();
      if (particle.getDataType().equals(Void.class)) {
         return false;
      } else if (particle.getDataType().equals(ItemStack.class)) {
         return obj instanceof ItemStack;
      } else if (particle.getDataType() == BlockData.class) {
         return obj instanceof BlockData;
      } else if (particle.getDataType() == MaterialData.class) {
         return obj instanceof MaterialData;
      } else {
         return particle.getDataType() == DustOptions.class ? obj instanceof DustOptions : true;
      }
   }

   public String[] getAliases() {
      return this.aliases;
   }

   static {
      for (Particle particle : values()) {
         PARTICLE_ALIASES.put(particle.toString(), particle);

         for (String alias : particle.getAliases()) {
            PARTICLE_ALIASES.put(alias.toUpperCase(), particle);
         }
      }
   }
}
