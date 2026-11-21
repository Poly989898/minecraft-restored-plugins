package litebans;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public final class ProxyResultSet implements ResultSet {
   public final Void obtainVoid() {
      throw new UnsupportedOperationException();
   }

   @Override
   public int findColumn(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public NClob getNClob(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public NClob getNClob(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateNString(int value, @Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateNString(@Nullable String text, @Nullable String text2) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateBinaryStream(int value, @Nullable InputStream inputStream, int value2) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateBinaryStream(@Nullable String text, @Nullable InputStream inputStream, int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateBinaryStream(int value, @Nullable InputStream inputStream, long value2) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateBinaryStream(@Nullable String text, @Nullable InputStream inputStream, long value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateBinaryStream(int value, @Nullable InputStream inputStream) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateBinaryStream(@Nullable String text, @Nullable InputStream inputStream) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Statement getStatement() {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateTimestamp(int value, @Nullable Timestamp timestamp) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateTimestamp(@Nullable String text, @Nullable Timestamp timestamp) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateNCharacterStream(int value, @Nullable Reader reader, long value2) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateNCharacterStream(@Nullable String text, @Nullable Reader reader, long value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateNCharacterStream(int value, @Nullable Reader reader) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateNCharacterStream(@Nullable String text, @Nullable Reader reader) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateInt(int value, int value2) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateInt(@Nullable String text, int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void moveToInsertRow() {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Date getDate(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Date getDate(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Date getDate(int value, @Nullable Calendar calendar) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Date getDate(@Nullable String text, @Nullable Calendar calendar) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public SQLWarning getWarnings() {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void beforeFirst() {
   }

   @Override
   public void close() {
   }

   @Override
   public void updateFloat(int value, float number) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateFloat(@Nullable String text, float number) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public boolean getBoolean(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public boolean getBoolean(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public boolean isFirst() {
      return false;
   }

   @Override
   public BigDecimal getBigDecimal(int value, int value2) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public BigDecimal getBigDecimal(@Nullable String text, int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public BigDecimal getBigDecimal(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public BigDecimal getBigDecimal(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateBytes(int value, @Nullable byte[] bytes) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateBytes(@Nullable String text, @Nullable byte[] bytes) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public boolean isLast() {
      return false;
   }

   @Override
   public void insertRow() {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Time getTime(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Time getTime(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Time getTime(int value, @Nullable Calendar calendar) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Time getTime(@Nullable String text, @Nullable Calendar calendar) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public boolean rowDeleted() {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public boolean last() {
      return false;
   }

   @Override
   public boolean isAfterLast() {
      return false;
   }

   @Override
   public boolean relative(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public boolean absolute(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public SQLXML getSQLXML(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public SQLXML getSQLXML(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Object unwrap(@Nullable Class class2) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public boolean next() {
      return false;
   }

   @Override
   public float getFloat(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public float getFloat(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public boolean wasNull() {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public int getRow() {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public boolean first() {
      return false;
   }

   @Override
   public void updateAsciiStream(int value, @Nullable InputStream inputStream, int value2) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateAsciiStream(@Nullable String text, @Nullable InputStream inputStream, int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateAsciiStream(int value, @Nullable InputStream inputStream, long value2) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateAsciiStream(@Nullable String text, @Nullable InputStream inputStream, long value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateAsciiStream(int value, @Nullable InputStream inputStream) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateAsciiStream(@Nullable String text, @Nullable InputStream inputStream) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public URL getURL(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public URL getURL(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateShort(int value, short value2) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateShort(@Nullable String text, short value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public int getType() {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateNClob(int value, @Nullable NClob nClob) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateNClob(@Nullable String text, @Nullable NClob nClob) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateNClob(int value, @Nullable Reader reader, long value2) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateNClob(@Nullable String text, @Nullable Reader reader, long value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateNClob(int value, @Nullable Reader reader) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateNClob(@Nullable String text, @Nullable Reader reader) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateRef(int value, @Nullable Ref ref) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateRef(@Nullable String text, @Nullable Ref ref) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateObject(int value, @Nullable Object obj, int value2) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateObject(int value, @Nullable Object obj) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateObject(@Nullable String text, @Nullable Object obj, int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateObject(@Nullable String text, @Nullable Object obj) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void setFetchSize(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void afterLast() {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateLong(int value, long value2) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateLong(@Nullable String text, long value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Blob getBlob(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Blob getBlob(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateClob(int value, @Nullable Clob clob) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateClob(@Nullable String text, @Nullable Clob clob) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateClob(int value, @Nullable Reader reader, long value2) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateClob(@Nullable String text, @Nullable Reader reader, long value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateClob(int value, @Nullable Reader reader) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateClob(@Nullable String text, @Nullable Reader reader) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public byte getByte(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public byte getByte(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public String getString(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public String getString(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateSQLXML(int value, @Nullable SQLXML sqlxml) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateSQLXML(@Nullable String text, @Nullable SQLXML sqlxml) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateDate(int value, @Nullable Date date) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateDate(@Nullable String text, @Nullable Date date) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public int getHoldability() {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Object getObject(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Object getObject(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Object getObject(int value, @Nullable Map map) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Object getObject(@Nullable String text, @Nullable Map map) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Object getObject(int value, @Nullable Class class2) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Object getObject(@Nullable String text, @Nullable Class class2) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public boolean previous() {
      return false;
   }

   @Override
   public void updateDouble(int value, double number) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateDouble(@Nullable String text, double number) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public long getLong(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public long getLong(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Clob getClob(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Clob getClob(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateBlob(int value, @Nullable Blob blob) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateBlob(@Nullable String text, @Nullable Blob blob) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateBlob(int value, @Nullable InputStream inputStream, long value2) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateBlob(@Nullable String text, @Nullable InputStream inputStream, long value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateBlob(int value, @Nullable InputStream inputStream) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateBlob(@Nullable String text, @Nullable InputStream inputStream) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateByte(int value, byte b) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateByte(@Nullable String text, byte b) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateRow() {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void deleteRow() {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public boolean isClosed() {
      return true;
   }

   @Override
   public String getNString(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public String getNString(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public String getCursorName() {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Array getArray(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Array getArray(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void cancelRowUpdates() {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateString(int value, @Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateString(@Nullable String text, @Nullable String text2) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void setFetchDirection(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public int getFetchSize() {
      return 0;
   }

   @Override
   public Reader getCharacterStream(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Reader getCharacterStream(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public boolean isBeforeFirst() {
      return true;
   }

   @Override
   public void updateBoolean(int value, boolean flag) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateBoolean(@Nullable String text, boolean flag) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void refreshRow() {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public boolean rowUpdated() {
      return false;
   }

   @Override
   public void updateBigDecimal(int value, @Nullable BigDecimal bigDecimal) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateBigDecimal(@Nullable String text, @Nullable BigDecimal bigDecimal) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public short getShort(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public short getShort(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public InputStream getAsciiStream(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public InputStream getAsciiStream(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateTime(int value, @Nullable Time time) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateTime(@Nullable String text, @Nullable Time time) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Timestamp getTimestamp(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Timestamp getTimestamp(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Timestamp getTimestamp(int value, @Nullable Calendar calendar) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Timestamp getTimestamp(@Nullable String text, @Nullable Calendar calendar) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Ref getRef(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Ref getRef(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void moveToCurrentRow() {
   }

   @Override
   public int getConcurrency() {
      return 1007;
   }

   @Override
   public void updateRowId(int value, @Nullable RowId rowId) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateRowId(@Nullable String text, @Nullable RowId rowId) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Reader getNCharacterStream(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public Reader getNCharacterStream(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateArray(int value, @Nullable Array array) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateArray(@Nullable String text, @Nullable Array array) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public byte[] getBytes(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public byte[] getBytes(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public double getDouble(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public double getDouble(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public InputStream getUnicodeStream(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public InputStream getUnicodeStream(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public boolean rowInserted() {
      return false;
   }

   @Override
   public boolean isWrapperFor(@Nullable Class class2) {
      return false;
   }

   @Override
   public int getInt(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public int getInt(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateNull(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateNull(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public RowId getRowId(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public RowId getRowId(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void clearWarnings() {
   }

   @Override
   public ResultSetMetaData getMetaData() {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public InputStream getBinaryStream(int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public InputStream getBinaryStream(@Nullable String text) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateCharacterStream(int value, @Nullable Reader reader, int value2) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateCharacterStream(@Nullable String text, @Nullable Reader reader, int value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateCharacterStream(int value, @Nullable Reader reader, long value2) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateCharacterStream(@Nullable String text, @Nullable Reader reader, long value) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateCharacterStream(int value, @Nullable Reader reader) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public void updateCharacterStream(@Nullable String text, @Nullable Reader reader) {
      this.obtainVoid();
      throw new CommandAbortException();
   }

   @Override
   public int getFetchDirection() {
      return 1000;
   }
}
