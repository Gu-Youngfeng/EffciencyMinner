package cn.edu.whu.cstar.timer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class RepsUtilier {
	
//	public static void main(String[] args) throws Exception {
//		List<CrashNode> lsCrash = getSingleCrash("src/main/resources/crashrep/io_mutants.txt");
//		for(CrashNode crash: lsCrash){
//			crash.showBasicInfo();
//		}
//		getFeatures("src/main/resources/crashrep/io_mutants.txt", "src/main/resources/projs/Commons-io-2.5_parent/");
//						
//	}
	
	/**
	 * <p>To extract features from <b>Stack Trace</b> (xx_mutants.txt) and <b>Buggy Source Code</b> (xx_parent/). Note that,</p>
	 * <li>xx_mutants.txt can be either the list of crash or single crash.</li>
	 * <li>the original 89 features are from paper "Does the fault reside in stack trace?"</li>
	 * @param path path of stack trace
	 * @param proj path of source code
	 * @throws Exception
	 */
	public static void getFeatures(String path, String proj){
		List<CrashNode> lsCrash = new ArrayList<CrashNode>();
		try {
			lsCrash = getSingleCrash(path);
		} catch (Exception e) {			
			e.printStackTrace();
		}
		System.out.println("[project]: " + proj);
		System.out.println("[size   ]: " + lsCrash.size());
		for(CrashNode scrash: lsCrash){ // for each crash node
			
			/** Features from Stack Trace **/
			showSTFeatures(scrash); // features from stack trace: ST01~ST09
			SRCAnalyzer srcAzer = new SRCAnalyzer(proj);
			srcAzer.showSRCFeatures(); //features from project: ST10~ST11
			
			/** Features from Source Code 
			 *  1. top-class
			 * **/
			String topClsName = scrash.getTopClassName(); // top class name
			String topMedName = scrash.getTopMethodName(); // top method name
			int topMedLine = scrash.getTopMethodLine(); // top method line
			
			CLSAnalyzer clsAzer1 = new CLSAnalyzer(proj, topClsName);
			clsAzer1.showCLSFeatures(); //features from top class: CT01~CT06

			MEDAnalyzer medAzer1;
			try {
				medAzer1 = new MEDAnalyzer(proj, topClsName, topMedName, topMedLine);
				medAzer1.showMEDFeatures();//features from top method: CT07~CT23, AT01~AT16
			} catch (Exception e) {
				System.out.println("[TOP METHOD NOT FOUND!]");
				e.printStackTrace();
			}			
			
			/**
			 *  2. bottom-class
			 * **/
			String botClsName = scrash.getBottomClassName(); // bottom class name
			String botMedName = scrash.getBottomMethodName(); // bottom method name
			int botMedLine = scrash.getBottomMethodLine(); // bottom method line
			
			CLSAnalyzer clsAzer2 = new CLSAnalyzer(proj, topClsName);
			clsAzer2.showCLSFeatures(); //features from bottom class: CB01~CB06
			
			MEDAnalyzer medAzer2;
			try {
				medAzer2 = new MEDAnalyzer(proj, botClsName, botMedName, botMedLine);
				medAzer2.showMEDFeatures();//features from bottom method: BT07~CB23, AB01~AB16
			} catch (Exception e) { 
				/** If the method is not found in medAzer2 (medAzer2=null)
				 *  there is possible that the method is inherited, and no related source in medAzer2
				 * **/
				String bot2ClsName = scrash.getBottom2ClassName(); // last 2 bottom class name
				String bot2MedName = scrash.getBottom2MethodName(); // bottom method name
				int bot2MedLine = scrash.getBottom2MethodLine(); // bottom class name
//				System.out.println("[BOTTOM METHOD NOT FOUND!]");
				try {
					medAzer2 = new MEDAnalyzer(proj, bot2ClsName, bot2MedName, bot2MedLine);
					medAzer2.showMEDFeatures();//features from bottom method: BT07~CB23, AB01~AB16
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
			}
						
			System.out.println(""); // break line for next crash
		}
	}
	
	/**
	 * <p>to return the list of single crash</p>
	 * @param path
	 * @return
	 * @throws Exception 
	 */
	public static List<CrashNode> getSingleCrash(String path) throws Exception{
		List<CrashNode> lsCrash = new ArrayList<CrashNode>();
		
		// split the xx_mutants.txt in "crashrep"
		File file = new File(path);
		if(!file.exists()){
			System.out.println("[ERROR]: No such file! " + path);
			return null;
		}
		
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		
		String str = "";
		List<String> singleCrash = new ArrayList<String>();
		while((str=br.readLine())!=null){
			
			if(str.contains("(Unknown Source)")){ // we cannot solve the UNKONWN SOURCE in stack trace
				continue;
			}
			
			if(str.startsWith("MUTATIONID:<<")){ // end of single crash
				singleCrash.add(str);
				CrashNode crash = new CrashNode(singleCrash);
				lsCrash.add(crash);
				singleCrash.clear();
			}else if(!str.trim().equals("") && !str.equals("BUG1")){
				singleCrash.add(str);
			}
		}
		
		br.close();
		fr.close();
		
		return lsCrash;
	}
	
	/**<p>Extract ST01 - ST09 from the stack trace. </p>
	 * @param crash
	 * */
	public static void showSTFeatures(CrashNode crash){
		/**ST01: Type of the exception in the crash*/
		int exceptName = crash.getType();
		/**ST02: Number of frames of the stack trace*/
		int loc = crash.getLOC();
		/**ST03: Number of classes in the stack trace*/
		int classNum = crash.getClassNum();
		/**ST04: Number of methods in the stack trace*/
		int methodNum = crash.getMethodNum();
		/**ST05: Whether an overloaded method exists in the stack trace*/
		int isOverLoaded = crash.getisOverLoaded()?0:1;
		/**ST06: Length of the name in the top class*/
		int lenTopClassName = crash.getTopClassName().length();
		/**ST07: Length of the name in the top function*/
		int lenTopMethodName = crash.getTopMethodName().length();
		/**ST08: Length of the name in the bottom class*/
		int lenBottomClassName = crash.getBottomClassName().length();
		/**ST09: Length of the name in the bottom function*/
		int lenBottomMethodName = crash.getBottomMethodName().length();
			
		System.out.print(exceptName + "," + loc + "," + classNum + "," + methodNum + "," + isOverLoaded
				+ "," + lenTopClassName + "," + lenTopMethodName + "," + lenBottomClassName + "," + lenBottomMethodName + ",");
			
	}

}
