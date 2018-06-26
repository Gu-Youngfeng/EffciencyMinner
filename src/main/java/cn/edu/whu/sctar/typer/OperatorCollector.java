package cn.edu.whu.sctar.typer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * <p>This file is used to count the mutants' number in each different kinds of operators/mutators.</p>
 * <li>a) the MutationInfo.txt should be included in this project</li>
 * <li>b) the default is the 7 operator provided by PIT</li>
 * 
 * @author yongfeng
 */
public class OperatorCollector {

	public static void main(String[] args) throws Exception {
		
		/** statistical results in MutationInfo.txt */
		getMutationInfo("src/main/resources/MutationInfo.txt");
		
	}
	
	
	/***
	 * <p>to count the number of mutants in 7 default operators in MutationInfo.txt.</p>
	 * @param path path of MutationInfo.txt
	 * @throws IOException
	 */
	public static void getMutationInfo(String path) throws IOException{
		File file = new File(path);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String str = "";
		Map<Integer, Integer> mapCollects = new HashMap<Integer, Integer>();
		
		while((str=br.readLine())!=null) {
			if(str.split(",").length <=2 ) { continue;}
				
			String mutStr = getInformsByFileMutLine(str)[3];
			
			int operator_id = getOperatorID(mutStr);
			
			if(mapCollects.containsKey(operator_id)){
				mapCollects.replace(operator_id, mapCollects.get(operator_id)+1);
			}else{
				mapCollects.put(operator_id, 1);
			}
			
		}
		
		br.close();
		
		for(Integer keyy: mapCollects.keySet()){
			System.out.printf("[operator]: %-30s [mutants]: %d\n",keyy, mapCollects.get(keyy));
		}
	}
	
	
	/***
	 * <p>return the operator type by given mutation details</p>
	 * @param mutDetails mutation details
	 * @return operator type [0~6]
	 */
	public static int getOperatorID(String mutDetails){
		
		int operator_id = -1;
		
		String reg = "(.*) \\(\\d+\\)";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(mutDetails);
		String operator = "";
		if(matcher.find()){
			 operator = matcher.group(1);
//			 System.out.println(operator);
		}
		
		/**Void method call mutator*/
		if(operator.startsWith("removed call to ")){
//			operator = "Void method call mutator";
			operator_id = 6;
		}
		/**Return value mutator*/
		else if(operator.startsWith("replaced return of ") && operator.contains(" value with value + 1 for")){
//			operator = "Return value mutator";
			operator_id = 5;
		}else if(operator.startsWith("replaced return of ") && operator.contains(" value with -(x + 1) for")){
//			operator = "Return value mutator";
			operator_id = 5;
		}else if(operator.startsWith("replaced return of ") && operator.contains("sized value with (x == 0 ? 1 : 0)")){
//			operator = "Return value mutator";
			operator_id = 5;
		}else if(operator.startsWith("mutated return of Object value for")){
//			operator = "Return value mutator";
			operator_id = 5;
		}
		/**Math mutator*/
		else if(operator.startsWith("Replaced ") && (operator.contains(" addition with subtraction") || operator.contains(" subtraction with addition")
				|| operator.contains(" division with multiplication") || operator.contains(" multiplication with division")
				|| operator.contains(" modulus with multiplication") || operator.contains(" multiplication with modulus"))){
//			operator = "Math mutator";
			operator_id = 3;
		}else if(operator.startsWith("Replaced ") && (operator.contains(" XOR with AND") || operator.contains(" bitwise AND with OR") 
				 || operator.contains(" bitwise OR with AND"))){
//			operator = "Math mutator";
			operator_id = 3;
		}else if(operator.startsWith("Replaced ") && (operator.contains(" Shift Right with Shift Left") || operator.contains("Shift Left with Shift Right"))){
//			operator = "Math mutator";
			operator_id = 3;
		}
		/**Increment mutator*/
		else if(operator.startsWith("Changed increment from ")){
//			operator = "Increment mutator";
			operator_id = 1;
		}
		/**Conditional negating mutator*/
		else if(operator.equals("negated conditional")){
//			operator = "Conditional negating mutator";
			operator_id = 4;
		}
		/**Conditional boundary mutator*/
		else if(operator.equals("changed conditional boundary")){
//			operator = "Conditional boundary mutator";
			operator_id = 0;
		}
		/**Negatives invert mutator*/
		else if(operator.equals("removed negation")){
//			operator = "Negatives invert mutator";
			operator_id = 2;
		}
		/**Other mutator if it's necessary*/

		
		return operator_id;
	}
	
	/***
	 * <p>to get class, method, line number, mutation details from the mutation line in Mutation.txt, e.g.,</p>
	 * <pre>--------------------------------------------------------------------
	 *Collection_4.1_mutant_ArrayStack_11, org.apache.commons.collections4.ArrayStack, pop : 122 -> negated conditional (2)
	 *--------------------------------------------------------------------</pre>
	 * <table>
	 * <tr><td><b>[class]:</b>   </td> <td>org.apache.commons.collections4.ArrayStack</td></tr>
	 * <tr><td><b>[method]:</b>  </td> <td>pop</td></tr>
	 * <tr><td><b>[line]:</b>    </td> <td>122</td></tr>
	 * <tr><td><b>[details]:</b> </td> <td>negated conditional (2)</td></tr>
	 * </table>
	 * @param mutLine mutation line in Mutation.txt
	 * @return informs [0]: class, [1]: method, [2]: line number, [3]: detail
	 */
	public static String[] getInformsByFileMutLine(String mutLine){
		
		String[] informs = new String[4];
		
		String regex = "(.*), (.*), (.*) : (\\d*) -> (.*)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(mutLine);
		if(matcher.find()){
			String cls = matcher.group(2);
			String med = matcher.group(3);
			String lin = matcher.group(4);
			String mut = matcher.group(5);
			
			informs[0] = cls;
			informs[1] = med;
			informs[2] = lin;
			informs[3] = mut;
		}
		
		return informs;
	}

}
