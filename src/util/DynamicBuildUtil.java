package util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import util.ZipUtil;


public class DynamicBuildUtil {
	
	public static void doEachTask(String buildPath, String zipFilePath) {
		String fs = File.separator;
		StringBuilder content = null;
		
		//Task1
		String filePath = buildPath + fs + "working"+ fs + "conf" + fs + "wrapper.conf";
		content = removeFirstCharacter(new File(filePath));
		writeFile(filePath, content);
		 
		//Task2
		String apacheFile = buildPath + fs + "working" + fs + "apache"+ fs + "tomcat" + fs + "conf" + fs + "web.xml";
		content = new StringBuilder();
		content = addMappedFile(new File(apacheFile));
		writeFile(apacheFile, content);
		
		//Task3
		String compilerPath = buildPath + fs + "working" + fs + "classes" + fs + "jdt-compiler.jar";
		renameCompiler(compilerPath);
		
		//Task4
		String webinfFile = buildPath + fs + "working" + fs + "WEB-INF" + fs + "backup" + fs +"web.xml";
		content = new StringBuilder();
		content = modifyTheWebInfFile(new File(webinfFile));
		writeFile(webinfFile, content);
		
		//Task5
		String extractPath = buildPath + fs + "working";
		try {
			System.out.println("Unzipped :: " + ZipUtil.unzip(zipFilePath,extractPath));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private static StringBuilder modifyTheWebInfFile(File file) {
		StringBuilder builder = new StringBuilder();
		BufferedReader buf = null;

		if (file.exists() && file.isFile()) {

			try {
				buf = new BufferedReader(new FileReader(file));
				String line = "";
				while ((line = buf.readLine()) != null) {
					
					if(line.contains("JspC")){
						line = line + "-->\n</web-app>";
						builder.append(line + "\n");
						break;
					}
					builder.append(line + "\n");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return builder;
	}
	
	private static void renameCompiler(String filePath) {

		File file = new File(filePath);
		if(file.exists() && file.isFile()){
			file.renameTo(new File(file.getParent() + File.separator + "Jdt-compiler_backup.jar"));
		}
	}
	
	
	private static StringBuilder addMappedFile(File file) {
		
		StringBuilder builder = new StringBuilder();
		BufferedReader buf = null;

		if (file.exists() && file.isFile()) {

			try {
				buf = new BufferedReader(new FileReader(file));
				String line = "";
				int count = -1;
				while ((line = buf.readLine()) != null) {				
					if(line.contains("<param-name>xpoweredBy</param-name>"))
					{
						count++;
					}	
					if(count != -1){
						count++;
					}
					if(count==3){
						line = line + "\n<init-param>\n<param-name>mappedfile</param-name>\n<param-value>false</param-value>\n</init-param>";						
					}
					builder.append(line + "\n");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return builder;
	}
	
	private static StringBuilder removeFirstCharacter(File file) {

		StringBuilder builder = new StringBuilder();
		BufferedReader buf = null;

		if (file.exists() && file.isFile()) {

			try {
				buf = new BufferedReader(new FileReader(file));
				String line = "";
				while ((line = buf.readLine()) != null) {
					
					if(line.contains("#wrapper.java.additional.29=-Dorg.apache.jasper.compiler.Parser.STRICT_QUOTE_ESCAPING=false") || 
							line.contains("#wrapper.java.additional.30=-Dorg.apache.jasper.compiler.Parser.STRICT_WHITESPACE=false"))
					{
						line = line.replaceFirst("#", "");
					}
					
					builder.append(line + "\n");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return builder;
	}
	
	
	private static boolean writeFile(String filePath, StringBuilder content) {
		boolean isFileCreated = false;
		Writer w = null;
		OutputStream out = null;
		try {
			File file = new File(filePath);
			out = new FileOutputStream(file);
			w = new OutputStreamWriter(out);
			w.write(content.toString());
			isFileCreated = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (w != null) {
					w.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return isFileCreated;
	}
}
