package edu.berkeley.cs.succinct.buffers;

import edu.berkeley.cs.succinct.StorageMode;
import edu.berkeley.cs.succinct.SuccinctCoreTest;
import edu.berkeley.cs.succinct.util.SuccinctConfiguration;

import java.io.*;

public class SuccinctBuffer2Test extends SuccinctCoreTest {

  private char[] data;
  private String testFileRaw = this.getClass().getResource("/test_file_utf8").getFile();
  private String testFileSuccinct =
    this.getClass().getResource("/test_file_utf8").getFile() + ".succinct";
  private String testFileSuccinctMin =
    this.getClass().getResource("/test_file_utf8").getFile() + ".min.succinct";
  private String testFileSA = this.getClass().getResource("/test_file_utf8.sa").getFile();
  private String testFileISA = this.getClass().getResource("/test_file_utf8.isa").getFile();
  private String testFileNPA = this.getClass().getResource("/test_file_utf8.npa").getFile();

  /**
   * Set up test.
   *
   * @throws Exception
   */
  public void setUp() throws Exception {
    super.setUp();

    File inputFile = new File(testFileRaw);

    data = new char[(int) inputFile.length()];
    InputStreamReader inputReader = new InputStreamReader(new FileInputStream(inputFile), "UTF8");
    inputReader.read(data, 0, data.length);
    sCore = new SuccinctBuffer(data);

  }

  @Override protected DataInputStream getNPAInputStream() throws FileNotFoundException {
    return new DataInputStream(new FileInputStream(new File(testFileNPA)));
  }

  @Override protected DataInputStream getSAInputStream() throws FileNotFoundException {
    return new DataInputStream(new FileInputStream(new File(testFileSA)));
  }

  @Override protected DataInputStream getISAInputStream() throws FileNotFoundException {
    return new DataInputStream(new FileInputStream(new File(testFileISA)));
  }

  /**
   * Test method: void readObject(ObjectInputStream ois)
   * Test method: void writeObject(ObjectOutputStream oos)
   *
   * @throws Exception
   */
  public void testSerializeDeserialize() throws Exception {

    // Serialize data
    FileOutputStream fOut = new FileOutputStream(testFileSuccinct);
    ObjectOutputStream oos = new ObjectOutputStream(fOut);
    oos.writeObject(sCore);
    oos.close();

    // Deserialize data
    FileInputStream fIn = new FileInputStream(testFileSuccinct);
    ObjectInputStream ois = new ObjectInputStream(fIn);
    SuccinctBuffer sCoreRead = (SuccinctBuffer) ois.readObject();
    ois.close();

    assertNotNull(sCoreRead);
    assertEquals(sCore.getOriginalSize(), sCoreRead.getOriginalSize());
    for (int i = 0; i < sCore.getOriginalSize(); i++) {
      assertEquals(sCore.lookupNPA(i), sCoreRead.lookupNPA(i));
      assertEquals(sCore.lookupSA(i), sCoreRead.lookupSA(i));
      assertEquals(sCore.lookupISA(i), sCoreRead.lookupISA(i));
    }
  }

  /**
   * Test method: void writeToFile(String path)
   * Test method: void memoryMap(String path)
   *
   * @throws Exception
   */
  public void testMemoryMap() throws Exception {

    ((SuccinctBuffer) sCore).writeToFile(testFileSuccinctMin);
    SuccinctBuffer sCoreRead = new SuccinctBuffer(testFileSuccinctMin, StorageMode.MEMORY_MAPPED);

    assertNotNull(sCoreRead);
    assertEquals(sCore.getOriginalSize(), sCoreRead.getOriginalSize());
    for (int i = 0; i < sCore.getOriginalSize(); i++) {
      assertEquals(sCore.lookupNPA(i), sCoreRead.lookupNPA(i));
      assertEquals(sCore.lookupSA(i), sCoreRead.lookupSA(i));
      assertEquals(sCore.lookupISA(i), sCoreRead.lookupISA(i));
    }
  }

  /**
   * Test method: void writeToFile(String path)
   * Test method: void readFromFile(String path)
   *
   * @throws Exception
   */
  public void testReadFromFile() throws Exception {

    ((SuccinctBuffer) sCore).writeToFile(testFileSuccinctMin);
    SuccinctBuffer sCoreRead = new SuccinctBuffer(testFileSuccinctMin, StorageMode.MEMORY_ONLY);

    assertNotNull(sCoreRead);
    assertEquals(sCore.getOriginalSize(), sCoreRead.getOriginalSize());
    for (int i = 0; i < sCore.getOriginalSize(); i++) {
      assertEquals(sCore.lookupNPA(i), sCoreRead.lookupNPA(i));
      assertEquals(sCore.lookupSA(i), sCoreRead.lookupSA(i));
      assertEquals(sCore.lookupISA(i), sCoreRead.lookupISA(i));
    }
  }

  /**
   * Test method: void construct(char[] data)
   * Test method: void readFromFile(String path)
   *
   * @throws Exception
   */
  public void testConstruct() throws Exception {
    FileOutputStream fos = new FileOutputStream(testFileSuccinctMin);
    DataOutputStream os = new DataOutputStream(fos);
    SuccinctBuffer.construct(data, os, new SuccinctConfiguration());
    os.close();

    SuccinctBuffer sCoreRead = new SuccinctBuffer(testFileSuccinctMin, StorageMode.MEMORY_ONLY);

    assertNotNull(sCoreRead);
    assertEquals(sCore.getOriginalSize(), sCoreRead.getOriginalSize());
    for (int i = 0; i < sCore.getOriginalSize(); i++) {
      assertEquals(sCore.lookupNPA(i), sCoreRead.lookupNPA(i));
      assertEquals(sCore.lookupSA(i), sCoreRead.lookupSA(i));
      assertEquals(sCore.lookupISA(i), sCoreRead.lookupISA(i));
    }
  }
}
