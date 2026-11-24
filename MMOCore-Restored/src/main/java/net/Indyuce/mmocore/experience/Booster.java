package net.Indyuce.mmocore.experience;

import java.util.Objects;
import java.util.UUID;

public class Booster {
   private final UUID uuid = UUID.randomUUID();
   private final long date = System.currentTimeMillis();
   private final Profession profession;
   private final double extra;
   private final String author;
   private long length;

   public Booster(double extra, long length) {
      this(null, null, extra, length);
   }

   public Booster(String author, double extra, long length) {
      this(author, null, extra, length);
   }

   public Booster(String author, Profession profession, double extra, long length) {
      this.author = author;
      this.length = length * 1000L;
      this.profession = profession;
      this.extra = extra;
   }

   public UUID getUniqueId() {
      return this.uuid;
   }

   public double getExtra() {
      return this.extra;
   }

   public boolean hasAuthor() {
      return this.author != null;
   }

   public String getAuthor() {
      return this.author;
   }

   public long getCreationDate() {
      return this.date;
   }

   public boolean hasProfession() {
      return this.profession != null;
   }

   public Profession getProfession() {
      return this.profession;
   }

   public boolean isTimedOut() {
      return this.date + this.length < System.currentTimeMillis();
   }

   public long getLeft() {
      return Math.max(0L, this.date + this.length - System.currentTimeMillis());
   }

   public long getLength() {
      return this.length;
   }

   public void addLength(long length) {
      this.length += length;
   }

   public boolean canStackWith(Booster booster) {
      return this.extra == booster.extra && Objects.equals(this.profession, booster.profession);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Booster booster = (Booster)o;
         return Objects.equals(this.uuid, booster.uuid);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.uuid);
   }
}
