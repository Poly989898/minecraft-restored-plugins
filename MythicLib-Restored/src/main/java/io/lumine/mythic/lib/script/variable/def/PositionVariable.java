package io.lumine.mythic.lib.script.variable.def;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.script.variable.SimpleVariableRegistry;
import io.lumine.mythic.lib.script.variable.Variable;
import io.lumine.mythic.lib.script.variable.VariableMetadata;
import io.lumine.mythic.lib.script.variable.VariableRegistry;
import io.lumine.mythic.lib.util.Position;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.util.Vector;

@VariableMetadata(name = "position")
public class PositionVariable extends Variable<Position> {
   public static final SimpleVariableRegistry<Position> VARIABLE_REGISTRY = new SimpleVariableRegistry<>();

   public PositionVariable(String name, Position position) {
      super(name, position);
   }

   public PositionVariable(String name, Location loc) {
      super(name, new Position(loc));
   }

   public PositionVariable(String name, World world, Vector vec) {
      super(name, new Position(world, vec));
   }

   private static Biome getBiomeAt(Position loc) {
      return loc.getWorld().getBiome(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
   }

   @Override
   public VariableRegistry<Variable<Position>> getVariableRegistry() {
      return VARIABLE_REGISTRY;
   }

   static {
      VARIABLE_REGISTRY.registerVariable("x", var -> new DoubleVariable("temp", var.getX()));
      VARIABLE_REGISTRY.registerVariable("y", var -> new DoubleVariable("temp", var.getY()));
      VARIABLE_REGISTRY.registerVariable("z", var -> new DoubleVariable("temp", var.getZ()));
      VARIABLE_REGISTRY.registerVariable("yaw", var -> new DoubleVariable("temp", UtilityMethods.getYawPitch(var.toVector())[0]));
      VARIABLE_REGISTRY.registerVariable("pitch", var -> new DoubleVariable("temp", UtilityMethods.getYawPitch(var.toVector())[1]));
      VARIABLE_REGISTRY.registerVariable("length", var -> new DoubleVariable("temp", var.length()), "norm", "len");
      VARIABLE_REGISTRY.registerVariable("world", var -> new WorldVariable("temp", var.getWorld()));
      VARIABLE_REGISTRY.registerVariable("biome", var -> new StringVariable("temp", getBiomeAt(var).name()));
      VARIABLE_REGISTRY.registerVariable("altitude", var -> new DoubleVariable("temp", UtilityMethods.getAltitude(var.toLocation())));
   }
}
