package ru.spbstu.telematics.java;
import java.io.File;
import java.io.IOException;

public class Main 
{
	public static void main(String[] args)
    {
		if(!(args.length == 2))
			System.out.println("Wrong amount of arguments,try again!");
		else 
		{
			String fileName = args[1];
			File destinationFile = new File(fileName);
			switch (args[0]) 
			{
				case "touch": 					
					try {
						destinationFile.createNewFile();
					} catch (IOException e) {
						System.out.println("Wrong path or filename,try again!");
					}
					//System.out.println(destinationFile.getAbsolutePath());
					//System.out.println(destinationFile.exists());
					break;
					
				case "rm":
					 Rm(destinationFile);
					break;
				default:
					System.out.println("Wrong command,try use 'rm' or 'touch'!");
					break;
			}
    
		}
    }
	public static void Rm(File f)
	{
		if (f.isDirectory())
		{
			File[] filesInDir=f.listFiles();
			 int fileCount=filesInDir.length;
			 for(int i = 0; i < fileCount; i++)
				 Rm(filesInDir[i]);
			 if(!f.delete())
					System.out.println("Directory "+f.getAbsolutePath()+" doesn't exist!");
		}
		if (f.isFile())
			if(!f.delete())
				System.out.println("File "+f.getAbsolutePath()+" doesn't exist!");
	}
}