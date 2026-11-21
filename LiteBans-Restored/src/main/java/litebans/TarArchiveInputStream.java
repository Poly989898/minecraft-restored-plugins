package litebans;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TarArchiveInputStream extends ArchiveInputStream {
   private static final int VALUE_256 = 256;
   private final byte[] smallBuf = new byte[256];
   private final int eOFRecord;
   private final int recordSize;
   private boolean atEOF;
   private long longValue5;
   private long longValue6;
   private final InputStream inputStream3;
   private List sparseInputStreams;
   private int currentSparseInputStreamIndex;
   private TarArchiveEntry directory;
   private final ZipEncoding zipEncoding;
   final String encoding;
   private Map globalPaxHeaders = new HashMap();
   private final List sparseHeaders = new ArrayList();
   private final boolean lenient;

   public TarArchiveInputStream(InputStream inputStream) {
      this(inputStream, 10240, 512);
   }

   public TarArchiveInputStream(InputStream inputStream, String text) {
      this(inputStream, 10240, 512, text);
   }

   public TarArchiveInputStream(InputStream inputStream, int value, int value2) {
      this(inputStream, value, value2, null);
   }

   public TarArchiveInputStream(InputStream inputStream, int value, int value2, String text) {
      this(inputStream, value, value2, text, false);
   }

   public TarArchiveInputStream(InputStream inputStream, int value, int value2, String text, boolean flag) {
      this.inputStream3 = inputStream;
      this.atEOF = false;
      this.encoding = text;
      this.zipEncoding = ZipEncodingHelper.computeZipEncoding(text);
      this.eOFRecord = value2;
      this.recordSize = value;
      this.lenient = flag;
   }

   @Override
   public void close() {
      try {
         if (this.sparseInputStreams != null) {
            for (InputStream inputStream : (Iterable<InputStream>) this.sparseInputStreams) {
               inputStream.close();
            }
         }

         this.inputStream3.close();
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int available() {
      if (this.isDirectory()) {
         return 0;
      } else {
         return this.directory.getRealSize() - this.longValue6 > 2147483647L ? Integer.MAX_VALUE : (int)(this.directory.getRealSize() - this.longValue6);
      }
   }

   @Override
   public long skip(long value) {
      if (value > 0L && !this.isDirectory()) {
         long value2 = this.directory.getRealSize() - this.longValue6;
         long value3;
         if (!this.directory.isSparse()) {
            value3 = ArchiveUtil6.computeLongValue(this.inputStream3, Math.min(value, value2));
         } else {
            value3 = this.skipSparse(Math.min(value, value2));
         }

         this.handleLongValue2(value3);
         this.longValue6 += value3;
         return value3;
      } else {
         return 0L;
      }
   }

   private final long skipSparse(long value) {
      try {
         if (this.sparseInputStreams != null && this.sparseInputStreams.size() != 0) {
            long value2 = 0L;

            while (value2 < value && this.currentSparseInputStreamIndex < this.sparseInputStreams.size()) {
               InputStream inputStream = (InputStream)this.sparseInputStreams.get(this.currentSparseInputStreamIndex);
               value2 += inputStream.skip(value - value2);
               if (value2 < value) {
                  this.currentSparseInputStreamIndex++;
               }
            }

            return value2;
         } else {
            return this.inputStream3.skip(value);
         }
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public boolean markSupported() {
      return false;
   }

   @Override
   public synchronized void mark(int value) {
   }

   @Override
   public synchronized void reset() {
   }

   public TarArchiveEntry getNextTarEntry() {
      try {
         if (this.isAtEOF()) {
            return null;
         }

         if (this.directory != null) {
            ArchiveUtil6.computeLongValue(this, Long.MAX_VALUE);
            this.skipRecordPadding();
         }

         byte[] record2 = this.getRecord();
         if (record2 == null) {
            this.directory = null;
            return null;
         }

         try {
            this.directory = new TarArchiveEntry(record2, this.zipEncoding, this.lenient);
         } catch (IllegalArgumentException e2) {
            throw new IOException("Error detected parsing the header", e2);
         }

         this.longValue6 = 0L;
         this.longValue5 = this.directory.getSize();
         if (this.directory.isGnuLongLinkEntry()) {
            byte[] longNameData = this.getLongNameData();
            if (longNameData == null) {
               return null;
            }

            this.directory.setLinkName(this.zipEncoding.computeText(longNameData));
         }

         if (this.directory.isGnuLongNameEntry()) {
            byte[] longNameData2 = this.getLongNameData();
            if (longNameData2 == null) {
               return null;
            }

            this.directory.setName(this.zipEncoding.computeText(longNameData2));
         }

         if (this.directory.isGlobalPaxHeader()) {
            this.paxHeaders();
         }

         if (this.directory.isPaxHeader()) {
            this.perform2();
         } else if (!this.globalPaxHeaders.isEmpty()) {
            this.applyPaxHeadersToCurrentEntry(this.globalPaxHeaders, this.sparseHeaders);
         }

         if (this.directory.isOldGnuSparse()) {
            this.readGnuSparse();
         }

         this.longValue5 = this.directory.getSize();
         return this.directory;
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   private final void skipRecordPadding() {
      if (!this.isDirectory() && this.longValue5 > 0L && this.longValue5 % this.eOFRecord != 0L) {
         long value = this.longValue5 / this.eOFRecord + 1L;
         long value2 = value * this.eOFRecord - this.longValue5;
         long value3 = ArchiveUtil6.computeLongValue(this.inputStream3, value2);
         this.handleLongValue2(value3);
      }
   }

   protected byte[] getLongNameData() {
      try {
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

         int value;
         while ((value = this.read(this.smallBuf)) >= 0) {
            byteArrayOutputStream.write(this.smallBuf, 0, value);
         }

         this.getNextEntry();
         if (this.directory == null) {
            return null;
         }

         byte[] byteArray = byteArrayOutputStream.toByteArray();
         int length2 = byteArray.length;

         while (length2 > 0 && byteArray[length2 - 1] == 0) {
            length2--;
         }

         if (length2 != byteArray.length) {
            byte[] bytes = new byte[length2];
            System.arraycopy(byteArray, 0, bytes, 0, length2);
            byteArray = bytes;
         }

         return byteArray;
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   private final byte[] getRecord() {
      byte[] record2 = this.readRecord();
      this.setAtEOF(this.isEOFRecord(record2));
      if (this.isAtEOF() && record2 != null) {
         this.tryToConsumeSecondEOFRecord();
         this.consumeRemainderOfLastRecord();
         record2 = null;
      }

      return record2;
   }

   protected boolean isEOFRecord(byte[] bytes) {
      return bytes == null || ArchiveUtil2.checkBytes(bytes, this.eOFRecord);
   }

   protected byte[] readRecord() {
      byte[] bytes = new byte[this.eOFRecord];
      int value = ArchiveUtil6.computeIntValue(this.inputStream3, bytes);
      this.handleIntValue4(value);
      return value != this.eOFRecord ? null : bytes;
   }

   private final void paxHeaders() {
      this.globalPaxHeaders = this.parsePaxHeaders(this, this.sparseHeaders);
      this.getNextEntry();
   }

   private final void perform2() {
      List arrayList = new ArrayList();
      Map paxHeaders = this.parsePaxHeaders(this, arrayList);
      if (paxHeaders.containsKey("GNU.sparse.map")) {
         arrayList = this.computeList((String)paxHeaders.get("GNU.sparse.map"));
      }

      this.getNextEntry();
      this.applyPaxHeadersToCurrentEntry(paxHeaders, arrayList);
      if (this.directory.isPaxGnu1xSparse()) {
         List oldGnuSparse = this.readOldGnuSparse();
         this.directory.setSparseHeaders(oldGnuSparse);
      }

      this.buildSparseInputStreams();
   }

   private final List computeList(String text) {
      ArrayList arrayList = new ArrayList();
      String[] split2 = text.split(",");

      for (byte b = 0; b < split2.length; b += 2) {
         long long2 = Long.parseLong(split2[b]);
         long long3 = Long.parseLong(split2[b + 1]);
         arrayList.add(new TarArchiveStructSparse(long2, long3));
      }

      return arrayList;
   }

   private final List readOldGnuSparse() {
      ArrayList arrayList = new ArrayList();
      long value = 0L;
      long[] values = this.computeLongs(this.inputStream3);
      long value2 = values[0];
      long value3 = value + values[1];

      while (value2-- > 0L) {
         long[] values2 = this.computeLongs(this.inputStream3);
         long value4 = values2[0];
         long value5 = value3 + values2[1];
         long[] values3 = this.computeLongs(this.inputStream3);
         long value6 = values3[0];
         value3 = value5 + values3[1];
         arrayList.add(new TarArchiveStructSparse(value4, value6));
      }

      long value7 = this.eOFRecord - value3 % this.eOFRecord;
      ArchiveUtil6.computeLongValue(this.inputStream3, value7);
      return arrayList;
   }

   private final long[] computeLongs(InputStream inputStream) {
      try {
         long value = 0L;
         long value2 = 0L;

         int value3;
         while ((value3 = inputStream.read()) != 10) {
            value2++;
            if (value3 == -1) {
               throw new IOException("Unexpected EOF when reading parse information of 1.X PAX format");
            }

            value = value * 10L + (value3 - 48);
         }

         long value4 = value2 + 1L;
         return new long[]{value, value4};
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   Map parsePaxHeaders(InputStream inputStream, List list) {
      try {
         HashMap hashMap = new HashMap(this.globalPaxHeaders);
         Long boxed = null;

         int value;
         do {
            int value2 = 0;
            int value3 = 0;

            while ((value = inputStream.read()) != -1) {
               value3++;
               if (value == 10) {
                  break;
               }

               if (value == 32) {
                  ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                  while ((value = inputStream.read()) != -1) {
                     value3++;
                     if (value == 61) {
                        String string = byteArrayOutputStream.toString("UTF-8");
                        int value4 = value2 - value3;
                        if (value4 == 1) {
                           hashMap.remove(string);
                        } else {
                           byte[] bytes = new byte[value4];
                           int value5 = ArchiveUtil6.computeIntValue(inputStream, bytes);
                           if (value5 != value4) {
                              throw new IOException("Failed to read Paxheader. Expected " + value4 + " bytes, read " + value5);
                           }

                           String string2 = new String(bytes, 0, value4 - 1, "UTF-8");
                           hashMap.put(string, string2);
                           if (string.equals("GNU.sparse.offset")) {
                              if (boxed != null) {
                                 list.add(new TarArchiveStructSparse(boxed, 0L));
                              }

                              boxed = Long.valueOf(string2);
                           }

                           if (string.equals("GNU.sparse.numbytes")) {
                              if (boxed == null) {
                                 throw new IOException("Failed to read Paxheader.GNU.sparse.offset is expected before GNU.sparse.numbytes shows up.");
                              }

                              list.add(new TarArchiveStructSparse(boxed, Long.parseLong(string2)));
                              boxed = null;
                           }
                        }
                        break;
                     }

                     byteArrayOutputStream.write((byte)value);
                  }
                  break;
               }

               int value6 = value2 * 10;
               value2 = value6 + (value - 48);
            }
         } while (value != -1);

         if (boxed != null) {
            list.add(new TarArchiveStructSparse(boxed, 0L));
         }

         return hashMap;
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   private final void applyPaxHeadersToCurrentEntry(Map map, List list) {
      this.directory.updateFromPaxHeaders(map);
      this.directory.setSparseHeaders(list);
   }

   private final void readGnuSparse() {
      TarArchiveSparseEntry tarArchiveSparseEntry;
      if (this.directory.isFlag14()) {
         do {
            byte[] record2 = this.getRecord();
            if (record2 == null) {
               this.directory = null;
               break;
            }

            tarArchiveSparseEntry = new TarArchiveSparseEntry(record2);
            this.directory.getSparseHeaders().addAll(tarArchiveSparseEntry.getList2());
         } while (tarArchiveSparseEntry.isFlag10());
      }

      this.buildSparseInputStreams();
   }

   private final boolean isDirectory() {
      return this.directory != null && this.directory.isDirectory();
   }

   @Override
   public ArchiveEntry getNextEntry() {
      return this.getNextTarEntry();
   }

   private final void tryToConsumeSecondEOFRecord() {
      try {
         boolean flag = true;
         boolean markSupported2 = this.inputStream3.markSupported();
         if (markSupported2) {
            this.inputStream3.mark(this.eOFRecord);
         }

         try {
            flag = !this.isEOFRecord(this.readRecord());
         } finally {
            if (flag && markSupported2) {
               this.handleLongValue3(this.eOFRecord);
               this.inputStream3.reset();
            }
         }
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   @Override
   public int read(byte[] bytes, int value, int value2) {
      try {
         if (value2 == 0) {
            return 0;
         }

         if (this.isAtEOF() || this.isDirectory()) {
            return -1;
         }

         if (this.directory == null) {
            throw new IllegalStateException("No current tar entry");
         }

         if (!this.directory.isSparse()) {
            if (this.longValue6 >= this.longValue5) {
               return -1;
            }
         } else if (this.longValue6 >= this.directory.getRealSize()) {
            return -1;
         }

         int value3 = Math.min(value2, this.available());
         int value4;
         if (this.directory.isSparse()) {
            value4 = this.readSparse(bytes, value, value3);
         } else {
            value4 = this.inputStream3.read(bytes, value, value3);
         }

         if (value4 == -1) {
            if (value3 > 0) {
               throw new IOException("Truncated TAR archive");
            }

            this.setAtEOF(true);
         } else {
            this.handleIntValue4(value4);
            this.longValue6 += value4;
         }

         return value4;
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   private final int readSparse(byte[] bytes, int value, int value2) {
      try {
         if (this.sparseInputStreams != null && this.sparseInputStreams.size() != 0) {
            if (this.currentSparseInputStreamIndex >= this.sparseInputStreams.size()) {
               return -1;
            } else {
               InputStream inputStream = (InputStream)this.sparseInputStreams.get(this.currentSparseInputStreamIndex);
               int read2 = inputStream.read(bytes, value, value2);
               if (this.currentSparseInputStreamIndex == this.sparseInputStreams.size() - 1) {
                  return read2;
               } else if (read2 == -1) {
                  this.currentSparseInputStreamIndex++;
                  return this.readSparse(bytes, value, value2);
               } else if (read2 < value2) {
                  this.currentSparseInputStreamIndex++;
                  int sparse = this.readSparse(bytes, value + read2, value2 - read2);
                  return sparse == -1 ? read2 : read2 + sparse;
               } else {
                  return read2;
               }
            }
         } else {
            return this.inputStream3.read(bytes, value, value2);
         }
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }

   protected final boolean isAtEOF() {
      return this.atEOF;
   }

   protected final void setAtEOF(boolean flag) {
      this.atEOF = flag;
   }

   private final void consumeRemainderOfLastRecord() {
      long value = this.obtainLongValue2() % this.recordSize;
      if (value > 0L) {
         long value2 = ArchiveUtil6.computeLongValue(this.inputStream3, this.recordSize - value);
         this.handleLongValue2(value2);
      }
   }

   private final void buildSparseInputStreams() {
      try {
         this.currentSparseInputStreamIndex = -1;
         this.sparseInputStreams = new ArrayList();
         List sparseHeaders = this.directory.getSparseHeaders();
         if (sparseHeaders != null && sparseHeaders.size() > 1) {
            ArchiveComparatorImpl archiveComparator = new ArchiveComparatorImpl(this);
            Collections.sort(sparseHeaders, archiveComparator);
         }

         if (sparseHeaders != null) {
            ArchiveInputStreamSub archiveInputStreamSub = new ArchiveInputStreamSub(null);
            long value = 0L;

            for (TarArchiveStructSparse tarArchiveStructSparse : (Iterable<TarArchiveStructSparse>) sparseHeaders) {
               if (tarArchiveStructSparse.getLongValue() == 0L && tarArchiveStructSparse.getLongValue2() == 0L) {
                  break;
               }

               if (tarArchiveStructSparse.getLongValue() - value < 0L) {
                  throw new IOException("Corrupted struct sparse detected");
               }

               if (tarArchiveStructSparse.getLongValue() - value > 0L) {
                  this.sparseInputStreams.add(new ArchiveInputStreamSub2(archiveInputStreamSub, tarArchiveStructSparse.getLongValue() - value));
               }

               if (tarArchiveStructSparse.getLongValue2() > 0L) {
                  this.sparseInputStreams.add(new ArchiveInputStreamSub2(this.inputStream3, tarArchiveStructSparse.getLongValue2()));
               }

               value = tarArchiveStructSparse.getLongValue() + tarArchiveStructSparse.getLongValue2();
            }
         }

         if (this.sparseInputStreams.size() > 0) {
            this.currentSparseInputStreamIndex = 0;
         }
      } catch (IOException e) {
         throw SneakyThrow.<RuntimeException>rethrow(e);
      }
   }
}
