package litebans;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class TarArchiveEntry implements ArchiveEntry, TarConstants {
   private static final TarArchiveEntry[] TAR_ARCHIVE_ENTRY_ARRAY = new TarArchiveEntry[0];
   public static final long VALUE_MINUS_1_2 = -1L;
   private String name = "";
   private final boolean preserveAbsolutePath;
   private int intValue39;
   private long longValue7 = 0L;
   private long longValue8 = 0L;
   private long size = 0L;
   private long modTime;
   private boolean checkSumOK;
   private byte byteValue;
   private String linkName = "";
   private String magic = "ustar\u0000";
   private String version = "00";
   private String userName;
   private String groupName = "";
   private int devMajor = 0;
   private int devMinor = 0;
   private List sparseHeaders;
   private boolean extended;
   private long realSize;
   private boolean paxGnuSparse;
   private boolean paxGnu1xSparse = false;
   private boolean starSparse;
   private final File file;
   private final Map extraPaxHeaders = new HashMap();
   public static final int VALUE_31 = 31;
   public static final int VALUE_16877 = 16877;
   public static final int VALUE_33188 = 33188;
   public static final int VALUE_1000 = 1000;

   private TarArchiveEntry(boolean flag) {
      String property = System.getProperty("user.name", "");
      if (property.length() > 31) {
         property = property.substring(0, 31);
      }

      this.userName = property;
      this.file = null;
      this.preserveAbsolutePath = flag;
   }

   public TarArchiveEntry(byte[] bytes, ZipEncoding zipEncoding, boolean flag) {
      this(false);
      this.parseTarHeader(bytes, zipEncoding, false, flag);
   }

   public boolean checkTarArchiveEntry(TarArchiveEntry tarArchiveEntry) {
      return tarArchiveEntry != null && this.getName().equals(tarArchiveEntry.getName());
   }

   @Override
   public boolean equals(Object obj) {
      return obj != null && this.getClass() == obj.getClass() ? this.checkTarArchiveEntry((TarArchiveEntry)obj) : false;
   }

   @Override
   public int hashCode() {
      return this.getName().hashCode();
   }

   @Override
   public String getName() {
      return this.name;
   }

   public void setName(String text) {
      this.name = normalizeFileName(text, this.preserveAbsolutePath);
   }

   public void setLinkName(String text) {
      this.linkName = text;
   }

   public void setLongValue7(long value) {
      this.longValue7 = value;
   }

   public void setLongValue8(long value) {
      this.longValue8 = value;
   }

   public void setText10(String text) {
      this.userName = text;
   }

   public void setText11(String text) {
      this.groupName = text;
   }

   public void setModTime(long value) {
      this.modTime = value / 1000L;
   }

   @Override
   public long getSize() {
      return this.size;
   }

   public void setSparseHeaders(List list) {
      this.sparseHeaders = list;
   }

   public List getSparseHeaders() {
      return this.sparseHeaders;
   }

   public boolean isPaxGnu1xSparse() {
      return this.paxGnu1xSparse;
   }

   public void setSize(long value) {
      if (value < 0L) {
         throw new IllegalArgumentException("Size is out of range: " + value);
      }

      this.size = value;
   }

   public void setDevMajor(int value) {
      if (value < 0) {
         throw new IllegalArgumentException("Major device number is out of range: " + value);
      }

      this.devMajor = value;
   }

   public void setDevMinor(int value) {
      if (value < 0) {
         throw new IllegalArgumentException("Minor device number is out of range: " + value);
      }

      this.devMinor = value;
   }

   public boolean isFlag14() {
      return this.extended;
   }

   public long getRealSize() {
      return !this.isSparse() ? this.size : this.realSize;
   }

   public boolean isGnuSparse() {
      return this.isOldGnuSparse() || this.isPaxGnuSparse();
   }

   public boolean isOldGnuSparse() {
      return this.byteValue == 83;
   }

   public boolean isPaxGnuSparse() {
      return this.paxGnuSparse;
   }

   public boolean isStarSparse() {
      return this.starSparse;
   }

   public boolean isGnuLongLinkEntry() {
      return this.byteValue == 75;
   }

   public boolean isGnuLongNameEntry() {
      return this.byteValue == 76;
   }

   public boolean isPaxHeader() {
      return this.byteValue == 120 || this.byteValue == 88;
   }

   public boolean isGlobalPaxHeader() {
      return this.byteValue == 103;
   }

   @Override
   public boolean isDirectory() {
      if (this.file != null) {
         return this.file.isDirectory();
      } else {
         return this.byteValue == 53 ? true : !this.isPaxHeader() && !this.isGlobalPaxHeader() && this.getName().endsWith("/");
      }
   }

   public boolean isSparse() {
      return this.isGnuSparse() || this.isStarSparse();
   }

   void updateFromPaxHeaders(Map map) {
      for (Entry entry : (Iterable<Entry>) map.entrySet()) {
         String key = (String)entry.getKey();
         String value = (String)entry.getValue();
         this.processPaxHeader(key, value, map);
      }
   }

   private final void processPaxHeader(String text, String text2, Map map) {
      switch (text) {
         case "path":
            this.setName(text2);
            break;
         case "linkpath":
            this.setLinkName(text2);
            break;
         case "gid":
            this.setLongValue8(Long.parseLong(text2));
            break;
         case "gname":
            this.setText11(text2);
            break;
         case "uid":
            this.setLongValue7(Long.parseLong(text2));
            break;
         case "uname":
            this.setText10(text2);
            break;
         case "size":
            this.setSize(Long.parseLong(text2));
            break;
         case "mtime":
            this.setModTime((long)(Double.parseDouble(text2) * 1000.0));
            break;
         case "SCHILY.devminor":
            this.setDevMinor(Integer.parseInt(text2));
            break;
         case "SCHILY.devmajor":
            this.setDevMajor(Integer.parseInt(text2));
            break;
         case "GNU.sparse.size":
            this.fillGnuSparse0Data(map);
            break;
         case "GNU.sparse.realsize":
            this.fillGnuSparse1Data(map);
            break;
         case "SCHILY.filetype":
            if ("sparse".equals(text2)) {
               this.fillStarSparseData(map);
            }
            break;
         default:
            this.extraPaxHeaders.put(text, text2);
      }
   }

   private final void parseTarHeader(byte[] bytes, ZipEncoding zipEncoding, boolean flag, boolean flag2) {
      int value = 0;
      this.name = flag ? ArchiveUtil7.parseName(bytes, value, 100) : ArchiveUtil7.parseName(bytes, value, 100, zipEncoding);
      value += 100;
      this.intValue39 = (int)this.parseOctalOrBinary(bytes, value, 8, flag2);
      value += 8;
      this.longValue7 = (int)this.parseOctalOrBinary(bytes, value, 8, flag2);
      value += 8;
      this.longValue8 = (int)this.parseOctalOrBinary(bytes, value, 8, flag2);
      value += 8;
      this.size = ArchiveUtil7.parseOctalOrBinary(bytes, value, 12);
      value += 12;
      this.modTime = this.parseOctalOrBinary(bytes, value, 12, flag2);
      value += 12;
      this.checkSumOK = ArchiveUtil7.verifyCheckSum(bytes);
      value += 8;
      this.byteValue = bytes[value++];
      this.linkName = flag ? ArchiveUtil7.parseName(bytes, value, 100) : ArchiveUtil7.parseName(bytes, value, 100, zipEncoding);
      value += 100;
      this.magic = ArchiveUtil7.parseName(bytes, value, 6);
      value += 6;
      this.version = ArchiveUtil7.parseName(bytes, value, 2);
      value += 2;
      this.userName = flag ? ArchiveUtil7.parseName(bytes, value, 32) : ArchiveUtil7.parseName(bytes, value, 32, zipEncoding);
      value += 32;
      this.groupName = flag ? ArchiveUtil7.parseName(bytes, value, 32) : ArchiveUtil7.parseName(bytes, value, 32, zipEncoding);
      value += 32;
      if (this.byteValue != 51 && this.byteValue != 52) {
         value += 16;
      } else {
         this.devMajor = (int)this.parseOctalOrBinary(bytes, value, 8, flag2);
         value += 8;
         this.devMinor = (int)this.parseOctalOrBinary(bytes, value, 8, flag2);
         value += 8;
      }

      int evaluateType2 = this.evaluateType(bytes);
      switch (evaluateType2) {
         case 2:
            value += 12;
            value += 12;
            value += 12;
            value += 4;
            value++;
            this.sparseHeaders = new ArrayList();

            for (int i = 0; i < 4; i++) {
               TarArchiveStructSparse sparse = ArchiveUtil7.parseSparse(bytes, value + i * 24);
               if (sparse.getLongValue() > 0L || sparse.getLongValue2() > 0L) {
                  this.sparseHeaders.add(sparse);
               }
            }

            value += 96;
            this.extended = ArchiveUtil7.parseBoolean(bytes, value);
            this.realSize = ArchiveUtil7.parseOctal(bytes, ++value, 12);
            value += 12;
            break;
         case 3:
         default:
            String text = flag ? ArchiveUtil7.parseName(bytes, value, 155) : ArchiveUtil7.parseName(bytes, value, 155, zipEncoding);
            if (this.isDirectory() && !this.name.endsWith("/")) {
               this.name = this.name + "/";
            }

            if (text.length() > 0) {
               this.name = text + "/" + this.name;
            }
            break;
         case 4:
            String text2 = flag ? ArchiveUtil7.parseName(bytes, value, 131) : ArchiveUtil7.parseName(bytes, value, 131, zipEncoding);
            if (text2.length() > 0) {
               this.name = text2 + "/" + this.name;
            }
      }
   }

   private final long parseOctalOrBinary(byte[] bytes, int value, int value2, boolean flag) {
      if (flag) {
         try {
            return ArchiveUtil7.parseOctalOrBinary(bytes, value, value2);
         } catch (IllegalArgumentException e) {
            return -1L;
         }
      } else {
         return ArchiveUtil7.parseOctalOrBinary(bytes, value, value2);
      }
   }

   private static final String normalizeFileName(String text, boolean flag) {
      if (!flag) {
         String lowerCase = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
         if (lowerCase != null) {
            if (lowerCase.startsWith("windows")) {
               if (text.length() > 2) {
                  char charAt2 = text.charAt(0);
                  char charAt3 = text.charAt(1);
                  if (charAt3 == ':' && (charAt2 >= 'a' && charAt2 <= 'z' || charAt2 >= 'A' && charAt2 <= 'Z')) {
                     text = text.substring(2);
                  }
               }
            } else if (lowerCase.contains("netware")) {
               int indexOf2 = text.indexOf(58);
               if (indexOf2 != -1) {
                  text = text.substring(indexOf2 + 1);
               }
            }
         }
      }

      text = text.replace(File.separatorChar, '/');

      while (!flag && text.startsWith("/")) {
         text = text.substring(1);
      }

      return text;
   }

   private final int evaluateType(byte[] bytes) {
      if (ArchiveUtil2.checkText("ustar ", bytes, 257, 6)) {
         return 2;
      } else if (ArchiveUtil2.checkText("ustar\u0000", bytes, 257, 6)) {
         return ArchiveUtil2.checkText("tar\u0000", bytes, 508, 4) ? 4 : 3;
      } else {
         return 0;
      }
   }

   void fillGnuSparse0Data(Map map) {
      this.paxGnuSparse = true;
      this.realSize = Integer.parseInt((String)map.get("GNU.sparse.size"));
      if (map.containsKey("GNU.sparse.name")) {
         this.name = (String)map.get("GNU.sparse.name");
      }
   }

   void fillGnuSparse1Data(Map map) {
      this.paxGnuSparse = true;
      this.paxGnu1xSparse = true;
      this.realSize = Integer.parseInt((String)map.get("GNU.sparse.realsize"));
      this.name = (String)map.get("GNU.sparse.name");
   }

   void fillStarSparseData(Map map) {
      this.starSparse = true;
      if (map.containsKey("SCHILY.realsize")) {
         this.realSize = Long.parseLong((String)map.get("SCHILY.realsize"));
      }
   }
}
