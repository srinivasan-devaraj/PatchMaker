/*
package util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;

public class JarDiffCalculator {

	public static void main(String[] args) {
		String workspaceJarPath = "/home/local/ZOHOCORP/srini-10093/Work/jar";
		String buildPath = "/media/srini-10093/6aff8ee8-6a8f-49df-ba36-fde6bf26534d/Work/Build/NTP_SecondRound/AppManager15";
		
		List<File> buildPathJars = new ArrayList<File>();
		getAllJarsFullPath(buildPath,buildPathJars);
		
		List<String> workspaceJars = new ArrayList<String>();
		getAllJarsName(workspaceJarPath,workspaceJars);
		
		System.out.println("Total Jars in Build Path : "+ buildPathJars.size()+"\nTotal Jars in Workspace : "+workspaceJars.size()+"\n");
		List<File> mismatchedJars = new ArrayList<>();
		
		for(File file : buildPathJars) {
			String buildJarFileName = file.getName();
			
			if(workspaceJars.contains(buildJarFileName)) {
				//System.out.println(buildJarFileName);
				String buildmd5 = null, workingmd5 = null;
				try (InputStream is = Files.newInputStream(Paths.get(file.toString()))) {
					buildmd5 = DigestUtils.md5Hex(is);
				}catch(Exception e) {
					e.printStackTrace();
				}
				try (InputStream is = Files.newInputStream(Paths.get(workspaceJarPath+File.separator+buildJarFileName))) {
					workingmd5 = DigestUtils.md5Hex(is);
				}catch(Exception e) {
					e.printStackTrace();
				}
				if(!buildmd5.equals(workingmd5)) {
					System.out.println("Updated Jar : "+buildJarFileName);
					mismatchedJars.add(file);
				}
			}else {
				System.out.println("Newly added Jar : "+file.getName());
				mismatchedJars.add(file);
			}
		}
		
		System.out.println("\nTotal Jars needs to copy : "+mismatchedJars.size()+"\n");
		for(File file : mismatchedJars) {
			File destinationFile = new File(workspaceJarPath+File.separator+file.getName());
			if(destinationFile.exists()){
				destinationFile.delete();
			}
			try {
				Files.copy(file.toPath(),destinationFile.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("File Copied to : "+destinationFile.getPath());
		}
	}

	private static void getAllJarsName(String buildPath, List<String> workspaceJars) {
		  File directory = new File(buildPath);
		    // Get all files from a directory.
		    File[] fList = directory.listFiles();
		    if(fList != null)
		        for (File file : fList) {      
		            if (file.isFile()) {
		            	String ext = FilenameUtils.getExtension(file.getName());
		            	if(ext.equals("jar")) {
		            		workspaceJars.add(file.getName());
		            	}
		            } else if (file.isDirectory()) {
		            	getAllJarsName(file.getAbsolutePath(), workspaceJars);
		            }
		        }
	}

	public static void getAllJarsFullPath(String directoryName, List<File> files) {
	    File directory = new File(directoryName);
	    // Get all files from a directory.
	    File[] fList = directory.listFiles();
	    if(fList != null)
	        for (File file : fList) {      
	            if (file.isFile()) {
	            	String ext = FilenameUtils.getExtension(file.getName());
	            	if(ext.equals("jar")) {
	            		files.add(file);
	            	}
	            } else if (file.isDirectory()) {
	            	getAllJarsFullPath(file.getAbsolutePath(), files);
	            }
	        }
	    }
}    
*/
