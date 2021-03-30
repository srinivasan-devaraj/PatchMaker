import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class FindAllFile {

	@SuppressWarnings("deprecation")
	public static JSONArray getAllData() {
		JSONArray overallResult = new JSONArray();
		try{
			File file = new File(System. getProperty("user.dir"));
			File shFile = new File(file.getAbsoluteFile()+File.separator+"getAllTempFile.sh");
			if(shFile.exists()) {
				shFile.delete();
			}
			shFile.createNewFile();
			FileUtils.writeStringToFile(shFile, "#!/bin/bash\nfind / -iname startApplicationsManager.sh > /tmp/PatchMakerFindAllTemp.txt");
			String path = "sh "+shFile.getAbsolutePath();
			String[] cmd = {"/bin/bash","-c",path};

			ProcessBuilder processBuilder = new ProcessBuilder(cmd);
			Process p = processBuilder.inheritIO().start();
			p.waitFor();
			
			File tempFile = new File("/tmp/PatchMakerFindAllTemp.txt");
			String output = FileUtils.readFileToString(tempFile);
			
			//Cleanup
			if(tempFile.exists()) {
				tempFile.delete();
			}
			if(shFile.exists()) {
				shFile.delete();
			}
			System.out.println("\n\n\n\n\n\n\n\n ----- Valid Files -----\n\n");
			
			for(String line : output.split("\n")) {
				if(isValidPath(line)) {
					System.out.println(line);
					overallResult.put(isBuildRunning(line));
				}
			}
			System.out.println("\n\n\n\n\n\n\n\n ----- OverallJSON -----\n\n");
			System.out.println(overallResult);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return overallResult;
	}
	public static void main(String[] args) {
		getAllData();
	}
	
	private static boolean isValidPath(String path) {
		try {
			File file = new File(path);
			File parentFile = file.getParentFile();
			String parentName = parentFile.getName();
			if(parentName.toLowerCase().startsWith("appmanager")) {
				return true;
			}
			File propFile = new File(parentName+File.separator+"conf"+File.separator+"AMServer.properties");
			if(!propFile.exists()) {
				return false;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private static JSONObject isBuildRunning(String path) throws IOException {
		String host;
		int port,timeout = 10;
		File file = new File(path);
		File appmanagerDir = file.getParentFile();
		JSONObject jsonObj = new JSONObject();
		
		
		Properties amServerProp = readPropertiesFile(appmanagerDir.getAbsolutePath()+File.separator+"conf"+File.separator+"AMServer.properties");
		Properties aboutProp = readPropertiesFile(appmanagerDir.getAbsolutePath()+File.separator+"conf"+File.separator+"About.properties");
		host = amServerProp.getProperty("am.appmanager.hostname");
		port = Integer.parseInt(amServerProp.getProperty("am.webserver.port"));
		try {
			jsonObj.put("BuildPath", appmanagerDir.getAbsolutePath());
			jsonObj.put("IsRunning",pingHost(host, port, timeout));
			jsonObj.put("Edition", amServerProp.getProperty("am.server.type"));
			jsonObj.put("DBType", amServerProp.getProperty("am.dbserver.type"));
			jsonObj.put("IsDynamic", isDynamicBuild(appmanagerDir));
			jsonObj.put("Version", aboutProp.getProperty("product.build.number"));
			jsonObj.put("Host", host);
			jsonObj.put("Port", ""+port);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObj;
	}
	
	public static boolean isDynamicBuild(File appmanagerDir) {
		File jspDir = new File(appmanagerDir.getAbsoluteFile()+File.separator+"working"+File.separator+"jsp");
		if(jspDir.exists()) {
			return true;
		}
		return false;
	}

	public static boolean pingHost(String host, int port, int timeout) {
	    try (Socket socket = new Socket()) {
	        socket.connect(new InetSocketAddress(host, port), timeout);
	        return true;
	    } catch (IOException e) {
	        return false; // Either timeout or unreachable or failed DNS lookup.
	    }
	}
	public static Properties readPropertiesFile(String fileName){
	      FileInputStream fis = null;
	      Properties prop = new Properties();
	      try {
	         fis = new FileInputStream(fileName);
	         prop = new Properties();
	         prop.load(fis);
	      } catch(FileNotFoundException fnfe) {
	         fnfe.printStackTrace();
	      } catch(IOException ioe) {
	         ioe.printStackTrace();
	      } catch(Exception e){
	    	  e.printStackTrace();
	      }finally {
	         try {
				fis.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      }
	      return prop;
	   }
}
