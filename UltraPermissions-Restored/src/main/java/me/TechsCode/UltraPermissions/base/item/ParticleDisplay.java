package me.TechsCode.UltraPermissions.base.item;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Particle.DustTransition;
import org.bukkit.Particle.Spell;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;

public class ParticleDisplay {
   private static final boolean ISFLAT;
   private static final boolean SUPPORTS_DUST_TRANSITION;
   private static final boolean SUPPORTS_ALPHA_COLORS;
   private static final boolean SUPPORTS_SPELL_DATA;
   public static final Color[] NOTE_COLORS;
   @NotNull
   private static final XParticle DEFAULT_PARTICLE;
   public int count = 1;
   public double extra;
   public boolean force;
   @NotNull
   private XParticle particle;
   @Nullable
   private Location location;
   @Nullable
   private Location lastLocation;
   @NotNull
   private Vector offset;
   @Nullable
   private Vector particleDirection;
   @NotNull
   private Vector direction;
   @NotNull
   public List<List<ParticleDisplay.Rotation>> rotations;
   @Nullable
   private List<ParticleDisplay.Quaternion> cachedFinalRotationQuaternions;
   @Nullable
   private ParticleDisplay.ParticleData data;
   @Nullable
   private Consumer<ParticleDisplay.CalculationContext> preCalculation;
   @Nullable
   private Consumer<ParticleDisplay.CalculationContext> postCalculation;
   @Nullable
   private Function<Double, Double> onAdvance;
   @Nullable
   private Set<Player> players;

   public ParticleDisplay() {
      this.particle = DEFAULT_PARTICLE;
      this.offset = new Vector();
      this.direction = new Vector(0, 1, 0);
      this.rotations = new ArrayList<>();
   }

   @Deprecated
   @NotNull
   public static ParticleDisplay colored(@Nullable Location location, int red, int green, int blue, float value) {
      return of(XParticle.DUST).withLocation(location).withColor(red, green, blue, value);
   }

   @Nullable
   public Set<Player> getPlayers() {
      return this.players;
   }

   public ParticleDisplay onlyVisibleTo(Collection<Player> players) {
      if (players != null && !players.isEmpty()) {
         if (this.players == null) {
            this.players = Collections.newSetFromMap(new WeakHashMap<>());
         }

         this.players.addAll(players);
         return this;
      } else {
         this.players = null;
         return this;
      }
   }

   public ParticleDisplay onlyVisibleTo(Player... players) {
      if (players != null && players.length != 0) {
         if (this.players == null) {
            this.players = Collections.newSetFromMap(new WeakHashMap<>());
         }

         Collections.addAll(this.players, players);
         return this;
      } else {
         this.players = null;
         return this;
      }
   }

   @Deprecated
   @NotNull
   public static ParticleDisplay colored(Location location, @NotNull Color color, float value) {
      return of(XParticle.DUST).withLocation(location).withColor(color, value);
   }

   @Deprecated
   @NotNull
   public static ParticleDisplay simple(@Nullable Location location, @NotNull Particle particle) {
      Objects.requireNonNull(particle, "Cannot build ParticleDisplay with null particle");
      ParticleDisplay particleDisplay = new ParticleDisplay();
      particleDisplay.particle = XParticle.of(particle);
      particleDisplay.location = location;
      return particleDisplay;
   }

   @Deprecated
   @NotNull
   public static ParticleDisplay of(@NotNull Particle particle) {
      return of(XParticle.of(particle));
   }

   @NotNull
   public static ParticleDisplay of(@NotNull XParticle xParticle) {
      ParticleDisplay particleDisplay = new ParticleDisplay();
      particleDisplay.particle = xParticle;
      return particleDisplay;
   }

   @Deprecated
   @NotNull
   public static ParticleDisplay display(@NotNull Location location, @NotNull Particle particle) {
      Objects.requireNonNull(location, "Cannot display particle in null location");
      ParticleDisplay particleDisplay = simple(location, particle);
      particleDisplay.spawn();
      return particleDisplay;
   }

   public static ParticleDisplay fromConfig(@NotNull ConfigurationSection section) {
      return edit(new ParticleDisplay(), section);
   }

   private static int toInt(String text) {
      try {
         return Integer.parseInt(text);
      } catch (NumberFormatException exception) {
         return 0;
      }
   }

   private static double toDouble(String text) {
      try {
         return Double.parseDouble(text);
      } catch (NumberFormatException exception) {
         return 0.0;
      }
   }

   private static Color toColor(List<String> texts) {
      if (texts.size() == 1) {
         try {
            return Color.decode((String)texts.get(0));
         } catch (NumberFormatException exception) {
            return null;
         }
      } else if (texts.size() == 3) {
         return new Color(toInt((String)texts.get(0)), toInt((String)texts.get(1)), toInt((String)texts.get(2)));
      } else {
         return texts.size() == 4
            ? new Color(toInt((String)texts.get(0)), toInt((String)texts.get(1)), toInt((String)texts.get(2)), toInt((String)texts.get(3)))
            : null;
      }
   }

   private static List<String> split(@NotNull String text, char ch) {
      ArrayList list = new ArrayList(5);
      boolean flag = false;
      boolean afterSeparator = false;
      int length = text.length();
      int count = 0;

      for (int i = 0; i < length; i++) {
         if (text.charAt(i) == ch) {
            if (flag) {
               list.add(text.substring(count, i));
               flag = false;
               afterSeparator = true;
            }

            count = i + 1;
         } else {
            afterSeparator = false;
            flag = true;
         }
      }

      if (flag || afterSeparator) {
         list.add(text.substring(count, length));
      }

      return list;
   }

   @NotNull
   public static ParticleDisplay edit(@NotNull ParticleDisplay particleDisplay, @NotNull ConfigurationSection section) {
      Objects.requireNonNull(particleDisplay, "Cannot edit a null particle display");
      Objects.requireNonNull(section, "Cannot parse ParticleDisplay from a null config section");
      String particle = section.getString("particle");
      Optional<XParticle> optional = particle == null ? Optional.empty() : XParticle.of(particle);
      optional.ifPresent(xParticle -> particleDisplay.particle = xParticle);
      if (section.isSet("count")) {
         particleDisplay.withCount(section.getInt("count"));
      }

      if (section.isSet("extra")) {
         particleDisplay.withExtra(section.getDouble("extra"));
      }

      if (section.isSet("force")) {
         particleDisplay.forceSpawn(section.getBoolean("force"));
      }

      String offset = section.getString("offset");
      if (offset != null) {
         List list = split(offset.replace(" ", ""), ',');
         if (list.size() >= 3) {
            double x = toDouble((String)list.get(0));
            double y = toDouble((String)list.get(1));
            double z = toDouble((String)list.get(2));
            particleDisplay.offset(x, y, z);
         } else {
            double value = toDouble((String)list.get(0));
            particleDisplay.offset(value);
         }
      }

      String direction = section.getString("direction");
      if (direction != null) {
         List list = split(direction.replace(" ", ""), ',');
         if (list.size() >= 3) {
            double x = toDouble((String)list.get(0));
            double y = toDouble((String)list.get(1));
            double z = toDouble((String)list.get(2));
            particleDisplay.particleDirection(x, y, z);
         }
      }

      ConfigurationSection rotations = section.getConfigurationSection("rotations");
      if (rotations != null) {
         for (String key : rotations.getKeys(false)) {
            ConfigurationSection groupSection = rotations.getConfigurationSection(key);
            ArrayList list = new ArrayList();

            for (String rotationKey : groupSection.getKeys(false)) {
               ConfigurationSection rotationSection = groupSection.getConfigurationSection(rotationKey);
               double angle = rotationSection.getDouble("angle");
               String text = rotationSection.getString("vector").toUpperCase(Locale.ENGLISH).replace(" ", "");
               Vector vector;
               if (text.length() == 1) {
                  vector = ParticleDisplay.Axis.valueOf(text).vector;
               } else {
                  String[] texts = text.split(",");
                  vector = new Vector(
                     Math.toRadians(Double.parseDouble(texts[0])), Math.toRadians(Double.parseDouble(texts[1])), Math.toRadians(Double.parseDouble(texts[2]))
                  );
               }

               list.add(ParticleDisplay.Rotation.of(angle, vector));
            }

            particleDisplay.rotations.add(list);
         }
      }

      double value;
      if (section.isSet("size")) {
         value = section.getDouble("size");
         particleDisplay.extra = value;
      } else {
         value = 1.0;
      }

      String color = section.getString("color");
      String blockdata = section.getString("blockdata");
      String itemstack = section.getString("itemstack");
      String materialdata = section.getString("materialdata");
      if (color != null) {
         List list = split(color.replace(" ", ""), ',');
         if (list.size() <= 4 || list.size() == 6) {
            Color transitionColor = null;
            Color baseColor;
            if (list.size() != 2 && list.size() != 6) {
               baseColor = toColor(list);
            } else {
               int count = list.size() / 2;
               baseColor = toColor(list.subList(0, count));
               transitionColor = toColor(list.subList(count, list.size()));
            }

            if (baseColor != null) {
               if (transitionColor != null) {
                  particleDisplay.data = new ParticleDisplay.DustTransitionParticleColor(baseColor, transitionColor, value);
               } else {
                  particleDisplay.data = new ParticleDisplay.RGBParticleColor(baseColor);
               }
            }
         }
      } else if (blockdata != null) {
         Material material = Material.getMaterial(blockdata);
         if (material != null && material.isBlock()) {
            particleDisplay.data = new ParticleDisplay.ParticleBlockData(material.createBlockData());
         }
      } else if (itemstack != null) {
         Material material = Material.getMaterial(itemstack);
         if (material != null && material.isItem()) {
            particleDisplay.data = new ParticleDisplay.ParticleItemData(new ItemStack(material, 1));
         }
      } else if (materialdata != null) {
         Material material = Material.getMaterial(materialdata);
         if (material != null && material.isBlock()) {
            particleDisplay.data = new ParticleDisplay.ParticleMaterialData(material.getNewData((byte)0));
         }
      }

      return particleDisplay;
   }

   public static void serialize(ParticleDisplay particleDisplay, ConfigurationSection section) {
      section.set("particle", particleDisplay.particle.name());
      if (particleDisplay.count != 1) {
         section.set("count", particleDisplay.count);
      }

      if (particleDisplay.extra != 0.0) {
         section.set("extra", particleDisplay.extra);
      }

      if (particleDisplay.force) {
         section.set("force", true);
      }

      if (!isZero(particleDisplay.offset)) {
         Vector offset = particleDisplay.offset;
         section.set("offset", offset.getX() + ", " + offset.getY() + ", " + offset.getZ());
      }

      if (particleDisplay.particleDirection != null) {
         Vector particleDirection = particleDisplay.particleDirection;
         section.set("direction", particleDirection.getX() + ", " + particleDirection.getY() + ", " + particleDirection.getZ());
      }

      if (!particleDisplay.rotations.isEmpty()) {
         ConfigurationSection rotations = section.createSection("rotations");
         int count = 1;

         for (List<ParticleDisplay.Rotation> rotation : particleDisplay.rotations) {
            ConfigurationSection groupSection = rotations.createSection("group-" + count++);
            int rotationIndex = 1;

            for (ParticleDisplay.Rotation step : rotation) {
               ConfigurationSection rotationSection = groupSection.createSection(String.valueOf(rotationIndex++));
               rotationSection.set("angle", step.angle);
               Vector axis = step.axis;
               Optional first = Arrays.stream(ParticleDisplay.Axis.values()).filter(candidate -> candidate.vector.equals(axis)).findFirst();
               if (first.isPresent()) {
                  rotationSection.set("axis", ((ParticleDisplay.Axis)first.get()).name());
               } else {
                  rotationSection.set("axis", axis.getX() + ", " + axis.getY() + ", " + axis.getZ());
               }
            }
         }
      }

      if (particleDisplay.data != null) {
         particleDisplay.data.serialize(section);
      }
   }

   public static Vector rotateAround(@NotNull Vector vector, @NotNull ParticleDisplay.Axis axis, @NotNull Vector rotationVector) {
      Objects.requireNonNull(axis, "Cannot rotate around null axis");
      Objects.requireNonNull(rotationVector, "Rotation vector cannot be null");
      switch (axis) {
         case X:
            return rotateAround(vector, axis, rotationVector.getX());
         case Y:
            return rotateAround(vector, axis, rotationVector.getY());
         case Z:
            return rotateAround(vector, axis, rotationVector.getZ());
         default:
            throw new AssertionError("Unknown rotation axis: " + axis);
      }
   }

   public static Vector rotateAround(@NotNull Vector vector, double angleX, double angleY, double angleZ) {
      rotateAround(vector, ParticleDisplay.Axis.X, angleX);
      rotateAround(vector, ParticleDisplay.Axis.Y, angleY);
      rotateAround(vector, ParticleDisplay.Axis.Z, angleZ);
      return vector;
   }

   public static Vector rotateAround(@NotNull Vector vector, @NotNull ParticleDisplay.Axis axis, double value) {
      Objects.requireNonNull(vector, "Cannot rotate a null location");
      Objects.requireNonNull(axis, "Cannot rotate around null axis");
      if (value == 0.0) {
         return vector;
      }

      double cos = Math.cos(value);
      double sin = Math.sin(value);
      switch (axis) {
         case X:
            double xRotY = vector.getY() * cos - vector.getZ() * sin;
            double xRotZ = vector.getY() * sin + vector.getZ() * cos;
            return vector.setY(xRotY).setZ(xRotZ);
         case Y:
            double yRotX = vector.getX() * cos + vector.getZ() * sin;
            double yRotZ = vector.getX() * -sin + vector.getZ() * cos;
            return vector.setX(yRotX).setZ(yRotZ);
         case Z:
            double zRotX = vector.getX() * cos - vector.getY() * sin;
            double zRotY = vector.getX() * sin + vector.getY() * cos;
            return vector.setX(zRotX).setY(zRotY);
         default:
            throw new AssertionError("Unknown rotation axis: " + axis);
      }
   }

   public ParticleDisplay preCalculation(@Nullable Consumer<ParticleDisplay.CalculationContext> consumer) {
      this.preCalculation = consumer;
      return this;
   }

   public ParticleDisplay postCalculation(@Nullable Consumer<ParticleDisplay.CalculationContext> consumer) {
      this.postCalculation = consumer;
      return this;
   }

   public ParticleDisplay onAdvance(@Nullable Function<Double, Double> function) {
      this.onAdvance = function;
      return this;
   }

   public ParticleDisplay withParticle(@NotNull Particle particle) {
      return this.withParticle(XParticle.of(Objects.requireNonNull(particle, "Particle cannot be null")));
   }

   public ParticleDisplay withParticle(@NotNull XParticle xParticle) {
      this.particle = Objects.requireNonNull(xParticle, "Particle cannot be null");
      return this;
   }

   @NotNull
   public Vector getDirection() {
      return this.direction;
   }

   public void advanceInDirection(double value) {
      Objects.requireNonNull(this.direction, "Cannot advance with null direction");
      if (value != 0.0) {
         if (this.onAdvance != null) {
            value = this.onAdvance.apply(value);
         }

         this.location.add(this.direction.clone().multiply(value));
      }
   }

   public ParticleDisplay withDirection(@Nullable Vector vector) {
      this.direction = vector.clone().normalize();
      return this;
   }

   @NotNull
   public XParticle getParticle() {
      return this.particle;
   }

   public int getCount() {
      return this.count;
   }

   public double getExtra() {
      return this.extra;
   }

   @Nullable
   public ParticleDisplay.ParticleData getData() {
      return this.data;
   }

   public ParticleDisplay withData(ParticleDisplay.ParticleData particleData) {
      this.data = particleData;
      return this;
   }

   public ParticleDisplay withRawData(Object obj) {
      return this.withData(new ParticleDisplay.ConstantParticleData(obj));
   }

   @Override
   public String toString() {
      return "ParticleDisplay:[Particle="
         + this.particle
         + ", Count="
         + this.count
         + ", Offset:{"
         + this.offset.getX()
         + ", "
         + this.offset.getY()
         + ", "
         + this.offset.getZ()
         + "}, "
         + (
            this.location != null
               ? "Location:{" + this.location.getWorld().getName() + this.location.getX() + ", " + this.location.getY() + ", " + this.location.getZ() + "}, "
               : ""
         )
         + "Rotation:"
         + this.rotations
         + ", Extra="
         + this.extra
         + ", Force="
         + this.force
         + ", Data="
         + this.data;
   }

   @NotNull
   public ParticleDisplay withCount(int count) {
      this.count = count;
      return this;
   }

   @NotNull
   public ParticleDisplay withExtra(double value) {
      this.extra = value;
      return this;
   }

   @NotNull
   public ParticleDisplay forceSpawn(boolean flag) {
      this.force = flag;
      return this;
   }

   @NotNull
   public ParticleDisplay withColor(@NotNull Color color, float value) {
      this.data = new ParticleDisplay.RGBParticleColor(color);
      this.extra = value;
      return this;
   }

   @NotNull
   public ParticleDisplay withColor(@NotNull Color color) {
      return this.withColor(color, 1.0F);
   }

   @NotNull
   public ParticleDisplay withNoteColor(int count) {
      this.data = new ParticleDisplay.NoteParticleColor(count);
      return this;
   }

   @NotNull
   public ParticleDisplay withNoteColor(Note note) {
      return this.withNoteColor(note.getId());
   }

   @Deprecated
   @NotNull
   public ParticleDisplay withColor(float red, float green, float blue, float size) {
      return this.withColor(new Color((int)red, (int)green, (int)blue), size);
   }

   @NotNull
   public ParticleDisplay withTransitionColor(@NotNull Color color, float value, @NotNull Color targetColor) {
      this.data = new ParticleDisplay.DustTransitionParticleColor(color, targetColor, value);
      this.extra = value;
      return this;
   }

   @Deprecated
   @NotNull
   public ParticleDisplay withTransitionColor(float red, float green, float blue, float size, float endRed, float endGreen, float endBlue) {
      return this.withTransitionColor(new Color((int)red, (int)green, (int)blue), size, new Color((int)endRed, (int)endGreen, (int)endBlue));
   }

   @NotNull
   public ParticleDisplay withBlock(@NotNull BlockData blockData) {
      this.data = new ParticleDisplay.ParticleBlockData(blockData);
      return this;
   }

   @NotNull
   public ParticleDisplay withBlock(@NotNull MaterialData materialData) {
      this.data = new ParticleDisplay.ParticleMaterialData(materialData);
      return this;
   }

   @Experimental
   public ParticleDisplay withBlock(Block block) {
      if (this.particle.getDataType() == ParticleDisplay.ParticleMaterialData.class) {
         return this.withBlock(new MaterialData(block.getType(), block.getData()));
      } else if (this.particle.getDataType() == ParticleDisplay.ParticleBlockData.class) {
         return this.withBlock(block.getBlockData());
      } else {
         throw new UnsupportedOperationException("Particle '" + this.particle + "' doesn't support blocks: " + this.particle.getDataType());
      }
   }

   @NotNull
   public ParticleDisplay withItem(@NotNull ItemStack item) {
      this.data = new ParticleDisplay.ParticleItemData(item);
      return this;
   }

   @NotNull
   public Vector getOffset() {
      return this.offset;
   }

   @NotNull
   public Vector getParticleDirection() {
      return this.direction;
   }

   @NotNull
   public ParticleDisplay withEntity(@NotNull Entity entity) {
      return this.withLocationCaller(entity::getLocation);
   }

   @NotNull
   public ParticleDisplay withLocationCaller(@Nullable Callable<Location> callable) {
      this.preCalculation = calculationContext -> {
         try {
            calculationContext.location = (Location)callable.call();
         } catch (Exception exception) {
            throw new IllegalStateException("Failed to calculate location of particle: " + this, exception);
         }
      };
      return this;
   }

   @Nullable
   public Location getLocation() {
      return this.location;
   }

   public ParticleDisplay withLocation(@Nullable Location location) {
      this.location = location;
      return this;
   }

   @NotNull
   public ParticleDisplay face(@NotNull Entity entity) {
      return this.face(Objects.requireNonNull(entity, "Cannot face null entity").getLocation());
   }

   @NotNull
   public ParticleDisplay face(@NotNull Location location) {
      Objects.requireNonNull(location, "Cannot face null location");
      this.rotate(
         ParticleDisplay.Rotation.of(Math.toRadians(location.getYaw()), ParticleDisplay.Axis.Y),
         ParticleDisplay.Rotation.of(Math.toRadians(-location.getPitch()), ParticleDisplay.Axis.X)
      );
      this.direction = location.getDirection().clone().normalize();
      return this;
   }

   @Nullable
   public Location cloneLocation(double x, double y, double z) {
      return this.location == null ? null : cloneLocation(this.location).add(x, y, z);
   }

   @NotNull
   private static Location cloneLocation(@NotNull Location location) {
      return new Location(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
   }

   private static boolean isZero(@NotNull Vector vector) {
      return vector.getX() == 0.0 && vector.getY() == 0.0 && vector.getZ() == 0.0;
   }

   @NotNull
   public ParticleDisplay cloneWithLocation(double x, double y, double z) {
      ParticleDisplay particleDisplay = this.copy();
      if (this.location == null) {
         return particleDisplay;
      }

      particleDisplay.location.add(x, y, z);
      return particleDisplay;
   }

   @NotNull
   public ParticleDisplay copy() {
      ParticleDisplay particleDisplay = of(this.particle)
         .withDirection(this.direction)
         .withCount(this.count)
         .offset(this.offset.clone())
         .forceSpawn(this.force)
         .onlyVisibleTo(this.players)
         .preCalculation(this.preCalculation)
         .postCalculation(this.postCalculation);
      if (this.location != null) {
         particleDisplay.location = cloneLocation(this.location);
      }

      if (!this.rotations.isEmpty()) {
         particleDisplay.rotations = new ArrayList<>(this.rotations);
      }

      particleDisplay.data = this.data;
      return particleDisplay;
   }

   public static Vector getPrincipalAxesRotation(Location location) {
      return getPrincipalAxesRotation(location.getPitch(), location.getYaw(), 0.0F);
   }

   public static Vector getPrincipalAxesRotation(float yaw, float pitch, float roll) {
      return new Vector(Math.toRadians(yaw + 90.0F), Math.toRadians(-pitch), roll);
   }

   public static float[] getYawPitch(Vector vector) {
      double value = 6.283185307179586;
      double x = vector.getX();
      double z = vector.getZ();
      float pitch;
      float yaw;
      if (x == 0.0 && z == 0.0) {
         yaw = 0.0F;
         pitch = vector.getY() > 0.0 ? -90.0F : 90.0F;
      } else {
         double angle = Math.atan2(-x, z);
         yaw = (float)Math.toDegrees((angle + 6.283185307179586) % 6.283185307179586);
         double squaredX = NumberConversions.square(x);
         double squaredZ = NumberConversions.square(z);
         double horizontalDistance = Math.sqrt(squaredX + squaredZ);
         pitch = (float)Math.toDegrees(Math.atan(-vector.getY() / horizontalDistance));
      }

      return new float[]{yaw, pitch};
   }

   @NotNull
   public List<ParticleDisplay.Quaternion> getRotation(boolean flag) {
      if (this.rotations.isEmpty()) {
         return new ArrayList<>();
      }

      if (flag) {
         this.cachedFinalRotationQuaternions = null;
      }

      if (this.cachedFinalRotationQuaternions == null) {
         this.cachedFinalRotationQuaternions = new ArrayList<>();

         for (List<ParticleDisplay.Rotation> rotation : this.rotations) {
            ParticleDisplay.Quaternion quaternion = null;

            for (ParticleDisplay.Rotation step : rotation) {
               ParticleDisplay.Quaternion stepQuaternion = ParticleDisplay.Quaternion.rotation(step.angle, step.axis);
               if (quaternion == null) {
                  quaternion = stepQuaternion;
               } else {
                  quaternion = quaternion.mul(stepQuaternion);
               }
            }

            this.cachedFinalRotationQuaternions.add(quaternion);
         }
      }

      return this.cachedFinalRotationQuaternions;
   }

   @NotNull
   public ParticleDisplay rotate(double x, double y, double z) {
      return this.rotate(
         ParticleDisplay.Rotation.of(x, ParticleDisplay.Axis.X),
         ParticleDisplay.Rotation.of(y, ParticleDisplay.Axis.Y),
         ParticleDisplay.Rotation.of(z, ParticleDisplay.Axis.Z)
      );
   }

   public ParticleDisplay rotate(ParticleDisplay.Rotation... rotations) {
      Objects.requireNonNull(rotations, "Null rotations");
      if (rotations.length != 0) {
         List list = Arrays.stream(rotations).filter(rotation -> rotation.angle != 0.0).collect(Collectors.toList());
         if (!list.isEmpty()) {
            this.rotations.add(list);
            if (this.cachedFinalRotationQuaternions != null) {
               this.cachedFinalRotationQuaternions.clear();
            }
         }
      }

      return this;
   }

   public ParticleDisplay rotate(ParticleDisplay.Rotation rotation) {
      Objects.requireNonNull(rotation, "Null rotation");
      if (rotation.angle != 0.0) {
         this.rotations.add(Collections.singletonList(rotation));
         if (this.cachedFinalRotationQuaternions != null) {
            this.cachedFinalRotationQuaternions.clear();
         }
      }

      return this;
   }

   @Nullable
   public Location getLastLocation() {
      return this.lastLocation == null ? this.getLocation() : this.lastLocation;
   }

   @Nullable
   public Location finalizeLocation(@Nullable Vector vector) {
      ParticleDisplay.CalculationContext calculationContext = new ParticleDisplay.CalculationContext(this.location, vector);
      if (this.preCalculation != null) {
         this.preCalculation.accept(calculationContext);
      }

      if (!calculationContext.shouldSpawn) {
         return null;
      }

      Location location = calculationContext.location;
      if (location == null) {
         throw new IllegalStateException("Attempting to spawn particle when no location is set");
      }

      vector = calculationContext.local;
      if (vector != null && !this.rotations.isEmpty()) {
         for (ParticleDisplay.Quaternion quaternion : this.getRotation(false)) {
            vector = ParticleDisplay.Quaternion.rotate(vector, quaternion);
         }
      }

      location = cloneLocation(location);
      if (vector != null) {
         location.add(vector);
      }

      ParticleDisplay.CalculationContext context = new ParticleDisplay.CalculationContext(location, vector);
      if (this.postCalculation != null) {
         this.postCalculation.accept(context);
      }

      return !context.shouldSpawn ? null : location;
   }

   @NotNull
   public ParticleDisplay offset(double x, double y, double z) {
      return this.offset(new Vector(x, y, z));
   }

   @NotNull
   public ParticleDisplay offset(@NotNull Vector vector) {
      this.offset = Objects.requireNonNull(vector, "Particle offset cannot be null");
      return this;
   }

   @NotNull
   public ParticleDisplay offset(double value) {
      return this.offset(value, value, value);
   }

   @NotNull
   public ParticleDisplay particleDirection(double x, double y, double z) {
      return this.particleDirection(new Vector(x, y, z));
   }

   @NotNull
   public ParticleDisplay particleDirection(@Nullable Vector vector) {
      this.particleDirection = vector;
      if (vector != null && this.extra == 0.0) {
         this.extra = 1.0;
      }

      return this;
   }

   @NotNull
   public ParticleDisplay directional() {
      this.particleDirection = new Vector();
      return this;
   }

   public boolean isDirectional() {
      return this.particleDirection != null;
   }

   @Nullable
   public Location spawn() {
      return this.spawn(this.finalizeLocation(null));
   }

   @Nullable
   public Location spawn(@Nullable Vector vector) {
      return this.spawn(this.finalizeLocation(vector));
   }

   @Nullable
   public Location spawn(double x, double y, double z) {
      return this.spawn(this.finalizeLocation(new Vector(x, y, z)));
   }

   @Nullable
   public Location spawn(Location location) {
      if (location == null) {
         return null;
      }

      this.lastLocation = location;
      Particle particle = this.particle.get();
      Objects.requireNonNull(particle, () -> "Cannot spawn unsupported particle: " + particle);
      if (this.count == 0) {
         this.count = 1;
      }

      Object obj = null;
      if (this.data != null) {
         this.data = this.data.transform(this);
         Vector vector = this.data.offsetValues(this);
         if (vector != null) {
            this.spawnWithDataInOffset(particle, location, vector, null);
            return location;
         }

         obj = this.data.data(this);
         if (!particle.getDataType().isInstance(obj)) {
            obj = null;
         }
      }

      if (this.particleDirection != null) {
         this.spawnWithDataInOffset(particle, location, this.particleDirection, obj);
         return location;
      } else {
         this.spawnRaw(particle, location, this.count, this.offset, obj);
         return location;
      }
   }

   private void spawnWithDataInOffset(Particle particle, Location location, Vector vector, Object obj) {
      if (isZero(this.offset) && this.count < 2) {
         this.spawnRaw(particle, location, 0, vector, obj);
      } else {
         double x = this.offset.getX();
         double y = this.offset.getY();
         double z = this.offset.getZ();
         ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();

         for (int i = 0; i < this.count; i++) {
            double offsetX = x == 0.0 ? 0.0 : threadLocalRandom.nextGaussian() * x;
            double offsetY = y == 0.0 ? 0.0 : threadLocalRandom.nextGaussian() * y;
            double offsetZ = z == 0.0 ? 0.0 : threadLocalRandom.nextGaussian() * z;
            Location spawnLocation = cloneLocation(location).add(offsetX, offsetY, offsetZ);
            this.spawnRaw(particle, spawnLocation, 0, vector, obj);
         }
      }
   }

   private void spawnRaw(Particle particle, Location location, int count, Vector vector, Object obj) {
      double x = vector.getX();
      double y = vector.getY();
      double z = vector.getZ();
      double extra = this.extra;
      if (this.particle == XParticle.DUST || this.particle == XParticle.NOTE) {
         extra = 1.0;
      }

      if (ISFLAT && obj == null && particle.getDataType() != Void.class) {
         if (particle.getDataType() == Float.class) {
            obj = 1.0F;
         } else if (particle.getDataType() == org.bukkit.Color.class) {
            obj = randomColor();
         } else if (particle.getDataType() == DustOptions.class) {
            obj = new DustOptions(org.bukkit.Color.RED, 1.0F);
         } else if (SUPPORTS_SPELL_DATA && particle.getDataType() == Spell.class) {
            obj = new Spell(randomColor(), 1.0F);
         }
      }

      if (this.players != null && !this.players.isEmpty()) {
         for (Player toValue : this.players.toArray(new Player[0])) {
            toValue.spawnParticle(particle, location, count, x, y, z, extra, obj);
         }
      } else if (ISFLAT) {
         location.getWorld().spawnParticle(particle, location, count, x, y, z, extra, obj, this.force);
      } else {
         location.getWorld().spawnParticle(particle, location, count, x, y, z, extra, obj);
      }
   }

   public static int findNearestNoteColor(Color color) {
      double value = colorDistanceSquared(color, NOTE_COLORS[0]);
      int count = 0;

      for (int i = 1; i < NOTE_COLORS.length; i++) {
         double distance = colorDistanceSquared(color, NOTE_COLORS[i]);
         if (distance < value) {
            value = distance;
            count = i;
         }
      }

      return count;
   }

   public static double colorDistanceSquared(Color color, Color other) {
      int red = color.getRed();
      int otherRed = other.getRed();
      int redMean = red + otherRed >> 1;
      int redDiff = red - otherRed;
      int greenDiff = color.getGreen() - other.getGreen();
      int blueDiff = color.getBlue() - other.getBlue();
      return ((512 + redMean) * redDiff * redDiff >> 8) + 4 * greenDiff * greenDiff + ((767 - redMean) * blueDiff * blueDiff >> 8);
   }

   private static org.bukkit.Color randomColor() {
      return org.bukkit.Color.fromRGB(ThreadLocalRandom.current().nextInt(16777216));
   }

   static {
      boolean flag;
      try {
         World.class
            .getDeclaredMethod(
               "spawnParticle", Particle.class, Location.class, int.class, double.class, double.class, double.class, double.class, Object.class, boolean.class
            );
         flag = true;
      } catch (NoSuchMethodException exception) {
         flag = false;
      }

      ISFLAT = flag;

      boolean supportsDustTransition;
      try {
         DustTransition.class.getName();
         supportsDustTransition = true;
      } catch (NoClassDefFoundError exception) {
         supportsDustTransition = false;
      }

      SUPPORTS_DUST_TRANSITION = supportsDustTransition;

      boolean supportsAlphaColors;
      try {
         org.bukkit.Color.fromARGB(0);
         supportsAlphaColors = true;
      } catch (NoSuchMethodError exception) {
         supportsAlphaColors = false;
      }

      SUPPORTS_ALPHA_COLORS = supportsAlphaColors;

      boolean supportsSpellData;
      try {
         Spell.class.getName();
         supportsSpellData = true;
      } catch (NoClassDefFoundError exception) {
         supportsSpellData = false;
      }

      SUPPORTS_SPELL_DATA = supportsSpellData;
      NOTE_COLORS = new Color[]{
         new Color(7853824),
         new Color(9814016),
         new Color(11707648),
         new Color(13403648),
         new Color(14836992),
         new Color(15941888),
         new Color(16522752),
         new Color(16646159),
         new Color(16187443),
         new Color(15204442),
         new Color(13566083),
         new Color(11403433),
         new Color(8782028),
         new Color(5964007),
         new Color(2949369),
         new Color(133886),
         new Color(14326),
         new Color(26848),
         new Color(39612),
         new Color(50829),
         new Color(59736),
         new Color(64545),
         new Color(2096128),
         new Color(5892096),
         new Color(9748736)
      };
      DEFAULT_PARTICLE = XParticle.FLAME;
   }

   public enum Axis {
      X(new Vector(1, 0, 0)),
      Y(new Vector(0, 1, 0)),
      Z(new Vector(0, 0, 1));

      private final Vector vector;

      Axis(Vector vector) {
         this.vector = vector;
      }

      public Vector getVector() {
         return this.vector;
      }
   }

   public final class CalculationContext {
      private Location location;
      private Vector local;
      private boolean shouldSpawn = true;

      public CalculationContext(Location location, Vector vector) {
         this.location = location;
         this.local = vector;
      }

      @Nullable
      public Location getLocation() {
         return this.location;
      }

      @Nullable
      public Vector getLocal() {
         return this.local;
      }

      public void setLocal(Vector vector) {
         this.local = vector;
      }

      public void setLocation(Location location) {
         this.location = location;
      }

      public void dontSpawn() {
         this.shouldSpawn = false;
      }

      public ParticleDisplay getDisplay() {
         return ParticleDisplay.this;
      }
   }

   public static final class ConstantParticleData implements ParticleDisplay.ParticleData {
      private final Object data;

      private ConstantParticleData(Object obj) {
         this.data = obj;
      }

      @Override
      public Object data(ParticleDisplay particleDisplay) {
         return this.data;
      }

      @Override
      public void serialize(ConfigurationSection section) {
         section.set("data", this.data);
      }
   }

   public static class DustTransitionParticleColor implements ParticleDisplay.ParticleData {
      private final DustTransition dustTransition;

      public DustTransitionParticleColor(Color color, Color endColor, double value) {
         this.dustTransition = new DustTransition(
            org.bukkit.Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()),
            org.bukkit.Color.fromRGB(endColor.getRed(), endColor.getGreen(), endColor.getBlue()),
            (float)value
         );
      }

      @Override
      public Object data(ParticleDisplay particleDisplay) {
         return this.dustTransition;
      }

      @Override
      public void serialize(ConfigurationSection section) {
         StringJoiner stringJoiner = new StringJoiner(", ");
         org.bukkit.Color color = this.dustTransition.getColor();
         org.bukkit.Color toColor = this.dustTransition.getToColor();
         stringJoiner.add(Integer.toString(color.getRed()));
         stringJoiner.add(Integer.toString(color.getGreen()));
         stringJoiner.add(Integer.toString(color.getBlue()));
         stringJoiner.add(Integer.toString(toColor.getRed()));
         stringJoiner.add(Integer.toString(toColor.getGreen()));
         stringJoiner.add(Integer.toString(toColor.getBlue()));
         section.set("color", stringJoiner.toString());
      }
   }

   public static class NoteParticleColor implements ParticleDisplay.ParticleData {
      private final int note;

      public NoteParticleColor(int count) {
         this.note = count;
      }

      public NoteParticleColor(Note note) {
         this(note.getId());
      }

      @Override
      public Vector offsetValues(ParticleDisplay display) {
         return new Vector(this.note / 24.0, 0.0, 0.0);
      }

      @Override
      public Object data(ParticleDisplay particleDisplay) {
         return null;
      }

      @Override
      public void serialize(ConfigurationSection section) {
         section.set("color", this.note);
      }

      @Override
      public ParticleDisplay.ParticleData transform(ParticleDisplay display) {
         return display.particle == XParticle.NOTE ? this : new ParticleDisplay.RGBParticleColor(ParticleDisplay.NOTE_COLORS[this.note]);
      }
   }

   public static class ParticleBlockData implements ParticleDisplay.ParticleData {
      private final BlockData blockData;

      public ParticleBlockData(BlockData blockData) {
         this.blockData = blockData;
      }

      @Override
      public Object data(ParticleDisplay particleDisplay) {
         return this.blockData;
      }

      @Override
      public void serialize(ConfigurationSection section) {
         section.set("blockdata", this.blockData.getMaterial().name());
      }
   }

   public interface ParticleData {
      default Vector offsetValues(ParticleDisplay display) {
         return null;
      }

      Object data(ParticleDisplay particleDisplay);

      void serialize(ConfigurationSection section);

      default ParticleDisplay.ParticleData transform(ParticleDisplay display) {
         return this;
      }
   }

   public static class ParticleItemData implements ParticleDisplay.ParticleData {
      private final ItemStack item;

      public ParticleItemData(ItemStack item) {
         this.item = item;
      }

      @Override
      public Object data(ParticleDisplay particleDisplay) {
         return this.item;
      }

      @Override
      public void serialize(ConfigurationSection section) {
         section.set("itemstack", this.item.getType());
      }
   }

   public static class ParticleMaterialData implements ParticleDisplay.ParticleData {
      private final MaterialData materialData;

      public ParticleMaterialData(MaterialData materialData) {
         this.materialData = materialData;
      }

      @Override
      public Object data(ParticleDisplay particleDisplay) {
         return this.materialData;
      }

      @Override
      public void serialize(ConfigurationSection section) {
         section.set("materialdata", this.materialData.getItemType().name());
      }
   }

   public static class Quaternion {
      public final double w;
      public final double x;
      public final double y;
      public final double z;

      public Quaternion(double w, double x, double y, double z) {
         this.w = w;
         this.x = x;
         this.y = y;
         this.z = z;
      }

      public ParticleDisplay.Quaternion copy() {
         return new ParticleDisplay.Quaternion(this.w, this.x, this.y, this.z);
      }

      public static Vector rotate(Vector vector, ParticleDisplay.Quaternion quaternion) {
         return quaternion.mul(from(vector)).mul(quaternion.inverse()).toVector();
      }

      public static Vector rotate(Vector vector, Vector axis, double angle) {
         return rotate(vector, rotation(angle, axis));
      }

      public static ParticleDisplay.Quaternion from(Vector vector) {
         return new ParticleDisplay.Quaternion(0.0, vector.getX(), vector.getY(), vector.getZ());
      }

      public static ParticleDisplay.Quaternion rotation(double value, Vector vector) {
         vector = vector.normalize();
         value /= 2.0;
         double sinHalf = Math.sin(value);
         return new ParticleDisplay.Quaternion(Math.cos(value), vector.getX() * sinHalf, vector.getY() * sinHalf, vector.getZ() * sinHalf);
      }

      public String getInverseString() {
         double value = Math.acos(this.w);
         double angleDegrees = Math.toDegrees(value) * 2.0;
         double sinHalf = Math.sin(value);
         Vector vector = new Vector(this.x / sinHalf, this.y / sinHalf, this.z / sinHalf);
         return angleDegrees + ", " + vector.getX() + ", " + vector.getY() + ", " + vector.getZ();
      }

      public Vector toVector() {
         return new Vector(this.x, this.y, this.z);
      }

      public ParticleDisplay.Quaternion inverse() {
         double value = this.w * this.w + this.x * this.x + this.y * this.y + this.z * this.z;
         return new ParticleDisplay.Quaternion(this.w / value, -this.x / value, -this.y / value, -this.z / value);
      }

      public ParticleDisplay.Quaternion conjugate() {
         return new ParticleDisplay.Quaternion(this.w, -this.x, -this.y, -this.z);
      }

      public ParticleDisplay.Quaternion mul(ParticleDisplay.Quaternion quaternion) {
         double productW = quaternion.w * this.w - quaternion.x * this.x - quaternion.y * this.y - quaternion.z * this.z;
         double productX = quaternion.w * this.x + quaternion.x * this.w + quaternion.y * this.z - quaternion.z * this.y;
         double productY = quaternion.w * this.y - quaternion.x * this.z + quaternion.y * this.w + quaternion.z * this.x;
         double productZ = quaternion.w * this.z + quaternion.x * this.y - quaternion.y * this.x + quaternion.z * this.w;
         return new ParticleDisplay.Quaternion(productW, productX, productY, productZ);
      }

      public Vector mul(Vector vector) {
         double twoX = this.x * 2.0;
         double twoY = this.y * 2.0;
         double twoZ = this.z * 2.0;
         double xx = this.x * twoX;
         double yy = this.y * twoY;
         double zz = this.z * twoZ;
         double xy = this.x * twoY;
         double xz = this.x * twoZ;
         double yz = this.y * twoZ;
         double wx = this.w * twoX;
         double wy = this.w * twoY;
         double wz = this.w * twoZ;
         double resultX = (1.0 - (yy + zz)) * vector.getX() + (xy - wz) * vector.getY() + (xz + wy) * vector.getZ();
         double resultY = (xy + wz) * vector.getX() + (1.0 - (xx + zz)) * vector.getY() + (yz - wx) * vector.getZ();
         double resultZ = (xz - wy) * vector.getX() + (yz + wx) * vector.getY() + (1.0 - (xx + yy)) * vector.getZ();
         return new Vector(resultX, resultY, resultZ);
      }
   }

   public static class RGBParticleColor implements ParticleDisplay.ParticleData {
      private final Color color;

      public RGBParticleColor(Color color) {
         this.color = color;
      }

      public RGBParticleColor(int red, int green, int blue) {
         this(new Color(red, green, blue));
      }

      @Override
      public Vector offsetValues(ParticleDisplay display) {
         if (!ParticleDisplay.ISFLAT
            || display.particle == XParticle.ENTITY_EFFECT && display.particle.isSupported() && display.particle.get().getDataType() == Void.class) {
            double value = this.color.getRed() == 0 ? 1.401298464324817E-45 : this.color.getRed() / 255.0;
            return new Vector(value, this.color.getGreen() / 255.0, this.color.getBlue() / 255.0);
         } else {
            return null;
         }
      }

      @Override
      public Object data(ParticleDisplay particleDisplay) {
         if (ParticleDisplay.ISFLAT && particleDisplay.particle.isSupported()) {
            Class dataType = particleDisplay.particle.get().getDataType();
            if (dataType == Void.class) {
               return null;
            }

            float value = particleDisplay.extra == 0.0 ? 1.0F : (float)particleDisplay.extra;
            org.bukkit.Color color = org.bukkit.Color.fromRGB(this.color.getRed(), this.color.getGreen(), this.color.getBlue());
            if (ParticleDisplay.SUPPORTS_ALPHA_COLORS) {
               color = color.setAlpha(this.color.getAlpha());
            }

            if (dataType == org.bukkit.Color.class) {
               return color;
            } else if (dataType == DustOptions.class) {
               return new DustOptions(color, value);
            } else if (ParticleDisplay.SUPPORTS_DUST_TRANSITION && dataType == DustTransition.class) {
               return new DustTransition(color, color, value);
            } else {
               return ParticleDisplay.SUPPORTS_SPELL_DATA && dataType == Spell.class ? new Spell(color, value) : null;
            }
         } else {
            return null;
         }
      }

      @Override
      public void serialize(ConfigurationSection section) {
         StringJoiner stringJoiner = new StringJoiner(", ");
         stringJoiner.add(Integer.toString(this.color.getRed()));
         stringJoiner.add(Integer.toString(this.color.getGreen()));
         stringJoiner.add(Integer.toString(this.color.getBlue()));
         stringJoiner.add(Integer.toString(this.color.getAlpha()));
         section.set("color", stringJoiner.toString());
      }

      @Override
      public ParticleDisplay.ParticleData transform(ParticleDisplay display) {
         return display.particle == XParticle.NOTE ? new ParticleDisplay.NoteParticleColor(ParticleDisplay.findNearestNoteColor(this.color)) : this;
      }
   }

   public static class Rotation {
      public double angle;
      public Vector axis;

      public Rotation(double value, Vector vector) {
         this.angle = value;
         this.axis = vector;
      }

      public ParticleDisplay.Rotation copy() {
         return new ParticleDisplay.Rotation(this.angle, this.axis.clone());
      }

      public static ParticleDisplay.Rotation of(double value, Vector vector) {
         return new ParticleDisplay.Rotation(value, vector);
      }

      public static ParticleDisplay.Rotation of(double value, ParticleDisplay.Axis axis) {
         return new ParticleDisplay.Rotation(value, axis.vector);
      }
   }
}
