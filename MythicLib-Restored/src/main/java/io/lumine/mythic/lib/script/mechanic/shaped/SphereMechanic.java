package io.lumine.mythic.lib.script.mechanic.shaped;

import io.lumine.mythic.lib.script.Script;
import io.lumine.mythic.lib.script.mechanic.type.LocationMechanic;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.util.DoubleFormula;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class SphereMechanic extends LocationMechanic {
   private final DoubleFormula radius;
   private final DoubleFormula points;
   private final Script onTick;

   public SphereMechanic(ConfigObject config) {
      super(config);
      config.validateKeys("tick", "points");
      this.onTick = config.getScript("tick");
      this.radius = config.getDoubleFormula("radius", DoubleFormula.constant(2.0));
      this.points = config.getDoubleFormula("points");
   }

   @Override
   public void cast(SkillMetadata meta, Location loc) {
      double r = this.radius.evaluate(meta);
      double N = this.points.evaluate(meta);
      double a = (Math.PI * 4) * r * r / N;
      double d = Math.sqrt(a);
      double M_nu = Math.round(Math.PI / d);
      double d_nu = Math.PI / M_nu;
      double d_phi = a / d_nu;

      for (int m = 0; m < M_nu; m++) {
         double nu = Math.PI * (m + 0.5) / M_nu;
         double M_phi = Math.round((Math.PI * 2) * Math.sin(nu) / d_phi);

         for (int n = 0; n < M_phi; n++) {
            double phi = (Math.PI * 2) * n / M_phi;
            Location interm = loc.clone().add(this.toVector(nu, phi).multiply(r));
            this.onTick.cast(meta.clone(interm));
         }
      }
   }

   private Vector toVector(double nu, double phi) {
      return new Vector(Math.sin(nu) * Math.cos(phi), Math.sin(nu) * Math.sin(phi), Math.cos(nu));
   }
}
