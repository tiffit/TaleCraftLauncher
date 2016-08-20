package tiffit.launcher.downloadable;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class Downloadable{
  private final URL url;
  private final File target;
  private final boolean forceDownload;
  private final Proxy proxy;
  private long startTime;
  protected int numAttempts;
  private long expectedSize;
  private long endTime;
  
  public Downloadable(Proxy proxy, URL remoteFile, File localFile, boolean forceDownload){
    this.proxy = proxy;
    this.url = remoteFile;
    this.target = localFile;
    this.forceDownload = forceDownload;
  }
  
  public long getExpectedSize(){
    return this.expectedSize;
  }
  
  public void setExpectedSize(long expectedSize){
    this.expectedSize = expectedSize;
  }
  
  public static String getDigest(File file, String algorithm, int hashLength){
    DigestInputStream stream = null;
    try
    {
      stream = new DigestInputStream(new FileInputStream(file), MessageDigest.getInstance(algorithm));
      byte[] buffer = new byte[65536];
      int read;
      do{
        read = stream.read(buffer);
      } while (read > 0);
    }
    catch (Exception ignored)
    {
      return null;
    }
    finally
    {
      closeSilently(stream);
    }
    return String.format("%1$0" + hashLength + "x", new Object[] { new BigInteger(1, stream.getMessageDigest().digest()) });
  }
  
  public abstract String download()
    throws IOException;
  
  protected void updateExpectedSize(HttpURLConnection connection)
  {
    if (this.expectedSize == 0L){
      setExpectedSize(connection.getContentLength());
    }
  }
  
  protected HttpURLConnection makeConnection(URL url)
    throws IOException
  {
    HttpURLConnection connection = (HttpURLConnection)url.openConnection(this.proxy);
    
    connection.setUseCaches(false);
    connection.setDefaultUseCaches(false);
    connection.setRequestProperty("Cache-Control", "no-store,max-age=0,no-cache");
    connection.setRequestProperty("Expires", "0");
    connection.setRequestProperty("Pragma", "no-cache");
    connection.setConnectTimeout(5000);
    connection.setReadTimeout(30000);
    
    return connection;
  }
  
  public URL getUrl()
  {
    return this.url;
  }
  
  public File getTarget()
  {
    return this.target;
  }
  
  public boolean shouldIgnoreLocal()
  {
    return this.forceDownload;
  }
  
  public int getNumAttempts()
  {
    return this.numAttempts;
  }
  
  public Proxy getProxy()
  {
    return this.proxy;
  }
  
  public static void closeSilently(Closeable closeable)
  {
    if (closeable != null) {
      try
      {
        closeable.close();
      }
      catch (IOException ignored) {}
    }
  }
  
  public static String copyAndDigest(InputStream inputStream, OutputStream outputStream, String algorithm, int hashLength)
    throws IOException
  {
    MessageDigest digest;
    try
    {
      digest = MessageDigest.getInstance(algorithm);
    }
    catch (NoSuchAlgorithmException e)
    {
      closeSilently(inputStream);
      closeSilently(outputStream);
      throw new RuntimeException("Missing Digest." + algorithm, e);
    }
    byte[] buffer = new byte[65536];
    try
    {
      int read = inputStream.read(buffer);
      while (read >= 1)
      {
        digest.update(buffer, 0, read);
        outputStream.write(buffer, 0, read);
        read = inputStream.read(buffer);
      }
    }
    finally
    {
      closeSilently(inputStream);
      closeSilently(outputStream);
    }
    return String.format("%1$0" + hashLength + "x", new Object[] { new BigInteger(1, digest.digest()) });
  }
  
  protected void ensureFileWritable(File target)
  {
    if ((target.getParentFile() != null) && (!target.getParentFile().isDirectory()))
    {
      if ((!target.getParentFile().mkdirs()) && 
        (!target.getParentFile().isDirectory())) {
        throw new RuntimeException("Could not create directory " + target.getParentFile());
      }
    }
    if ((target.isFile()) && (!target.canWrite())) {
      throw new RuntimeException("Do not have write permissions for " + target + " - aborting!");
    }
  }
  
  public long getStartTime()
  {
    return this.startTime;
  }
  
  public void setStartTime(long startTime)
  {
    this.startTime = startTime;
  }
  
  public String getStatus()
  {
    return "Downloading " + getTarget().getName();
  }
  
  public long getEndTime()
  {
    return this.endTime;
  }
  
  public void setEndTime(long endTime)
  {
    this.endTime = endTime;
  }
}
