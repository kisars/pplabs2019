package ru.spbstu.telematics.java;
import org.junit.Test;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;

public class MainTest 
{

   @Test
   public void Test1() 
   {
	   boolean testStatus = true;
	   String fileName = "testFile.txt";
	   Main.main(new String[] {"touch", fileName});
	   File testFile = new File(fileName);
	   if(testFile.exists())	   
		   testFile.delete();	   
	   else
		   testStatus = false;	
	   Assert.assertEquals(testStatus, true);
   }

   @Test
   public void Test2() throws IOException
   {
	   boolean testStatus = true;
	   String fileName = "Test";
	   File testFile = new File(fileName);
	   testFile.mkdir();
	   fileName = "Test/Test1";
	   testFile = new File(fileName);
	   testFile.mkdir();
	   fileName = "Test/Test1/Test1.txt";
	   testFile = new File(fileName);
	   testFile.createNewFile();
	   fileName = "Test/Test1/Test2.txt";
	   testFile = new File(fileName);
	   testFile.createNewFile();
	   fileName = "Test/Test1.txt";
	   testFile = new File(fileName);
	   testFile.createNewFile();
	   fileName = "Test/Test2.txt";
	   testFile = new File(fileName);
	   testFile.createNewFile();
	   fileName = "Test/Test2";
	   testFile = new File(fileName);
	   testFile.mkdir();
	   Main.main(new String[] {"rm", "Test"});
	   testFile = new File("Test");
	   if(testFile.exists())
		   testStatus = false;
	   Assert.assertEquals(testStatus, true);
   }
   @Test
   public void Test3() throws IOException
   {
	   boolean testStatus = true;
	   String fileName = "Test.txt";
	   File testFile = new File(fileName);
	   testFile.createNewFile();
	   Main.main(new String[] {"rm", "Test.txt"});
	   testFile = new File("Test.txt");
	   if(testFile.exists())
		   testStatus = false;
	   Assert.assertEquals(testStatus, true);
   }

}