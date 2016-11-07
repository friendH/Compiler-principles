package lexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {

	 public static void main(String[] args) throws IOException {  
	        Lexer lexer = new Lexer();  
	         
	        while (lexer.getReaderState() == false) {  
	            lexer.scan();  
	        }  
	          
	        /* 保存相关信息 */
	        lexer.saveTokens();  
	        lexer.saveSymbolsTable();
		 
		/*char s = 'a';
		s = Character.toLowerCase(s) ;
		System.out.println(s);*/
	          
	    }  

}
