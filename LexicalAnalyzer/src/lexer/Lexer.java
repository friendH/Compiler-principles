package lexer;


import java.io.*;  
import java.util.*;  

public class Lexer {
	 public static int line = 1;     /* 记录行号 */  
	    char peek = ' ';        /* 下一个读入字符 */  
	    Hashtable<String, Word> words =   
	        new Hashtable<String, Word>();  
	    /* 符号表 */  
	    private Hashtable<Token, String> table =   
	        new Hashtable<Token, String>();  
	    /* token序列 */  
	    private List<String> tokens =   
	        new LinkedList<String> ();  
	    /* 读取文件变量 */  
	    BufferedReader reader = null;   
	    /* 保存当前是否读取到了文件的结尾  */  
	    private Boolean isEnd = false;  
	      
	    /* 是否读取到文件的结尾 */  
	    public Boolean getReaderState() {  
	        return this.isEnd;  
	    }  
	      
	    /* 保存存储在table中的 */  
	    public void saveSymbolsTable() throws IOException {  
	        FileWriter writer = new FileWriter("符号表.txt");  
	        writer.write("[符号]          [符号类型信息]\n");  
	        writer.write("\r\n");  
	          
	        Enumeration<Token> e = table.keys();  
	        while( e.hasMoreElements() ){  
	            Token token = (Token)e.nextElement();  
	            String desc = table.get(token);  
	              
	            /* 写入文件 */  
	            writer.write(token + "\t\t\t" + desc + "\r\n");  
	        }  
	              
	        writer.flush();  
	    }  
	      
	    /* 保存Tokens */  
	    public void saveTokens() throws IOException {  
	        FileWriter writer = new FileWriter("Tokens表.txt");  
	        writer.write("[符号]  \n");  
	        writer.write("\r\n");  
	          
	        for(int i = 0; i < tokens.size(); ++i) {  
	            String tok = (String)tokens.get(i);  
	              
	            /* 写入文件 */  
	            writer.write(tok + "\r\n");  
	        }     
	              
	        writer.flush();  
	    }  
	      
	    void reserve(Word w) {  
	        words.put(w.lexme, w);  
	    }  
	      
	    /* 
	     * 构造函数中将关键字和类型添加到hashtable words中 
	     */  
	    public Lexer() {  
	        /* 初始化读取文件变量 */  
	        try {  
	        	
	        	File file = new File("shuru.txt");
	        	if(!file.exists()){
	        		file.createNewFile();
	        	}
	            reader = new BufferedReader(new FileReader(file));  
	        }  
	        catch(IOException e) {  
	            System.out.print(e);  
	        }  
	          
	          
	        /* 关键字 */  
	        this.reserve(new Word("if", Tag.IF));  
	        this.reserve(new Word("then", Tag.THEN));  
	        this.reserve(new Word("else", Tag.ELSE));  
	        this.reserve(new Word("while", Tag.WHILE));  
	        this.reserve(new Word("do", Tag.DO));
	        this.reserve(new Word("begin",Tag.BEGIN));
	        this.reserve(new Word("end", Tag.END));
	          
	        /* 类型 */  
	        this.reserve(Word.True);  
	        this.reserve(Word.False);  
	        this.reserve(Type.Int);  
	        this.reserve(Type.Char);  
	        this.reserve(Type.Bool);  
	        this.reserve(Type.Float);
	        this.reserve(Type.Null);
	    }  
	      
	    public void readch() throws IOException {  
	        /* 这里应该是使用的是 */  
	        peek = (char)reader.read();  
	        if((int)peek == 0xffff){  
	            this.isEnd = true;  
	        }  
	        // peek = (char)System.in.read();  
	    }  
	      
	    public Boolean readch(char ch) throws IOException {  
	        readch();  
	        if (this.peek != ch) {  
	            return false;  
	        }  
	          
	        this.peek = ' ';  
	        return true;  
	    }  
	      
	    public Token scan() throws IOException {  
	        /* 消除空白 */   
	        for( ; ; readch() ) {  
	            if(peek == ' ' || peek == '\t')  
	                continue;  
	            else if (peek == '\n')   
	                line = line + 1;
	            else if(peek == '\r')
	            	continue;
	            else if(peek == '/') {
	            	if(readch('/')) {
	            		for( ; ; readch() ) { 
	                       if(peek == '\n') {
	                    	   line = line + 1; 
	                    	   break;
	                       }
	                    	   
	            		}
	            	}else{
	            		tokens.add("/");  
		                return new Token('/');
	            	}
	            }
	            else  
	                break;  
	        }  
	          
	        /* 下面开始分割关键字，标识符等信息  */  
	        switch (peek) {  
	        /* 对于 ==, >=, <=, !=的区分使用状态机实现 */  
	        case '=' :  
	            if (readch('=')) {  
	                tokens.add("<==>");  
	                return Word.eq;   
	            }  
	            else {  
	                tokens.add("<=>");  
	                return new Token('=');  
	            }  
	        case '>' :  
	            if (readch('=')) {  
	                tokens.add("<>=>");  
	                return Word.ge;  
	            }  
	            else {  
	                tokens.add("<>>");  
	                return new Token('>');  
	            }  
	        case '<' :  
	            if (readch('=')) {  
	                tokens.add("<<=>");  
	                return Word.le;  
	            }
	            else {  
	                tokens.add("<<>");  
	                return new Token('<');  
	            }  
	        case '!' :  
	            if (readch('=')) {  
	                tokens.add("<!=>");  
	                return Word.ne;  
	            }  
	            else {  
	                tokens.add("<!>");  
	                return new Token('!');  
	            }     
	        }  
	          
	        /* 下面是对数字的识别，根据文法的规定的话，这里的 
	         * 数字只要是能够识别整数就行. 
	         */  
	        if(Character.isDigit(peek)) {
	        		
	        	if(peek == '0') {
	        		int value = 0;
	        		readch();
	        		if(peek == 'x' || peek == 'X'){
	        			readch();
	    	            do {  
	    	                value = 16 * value + Character.digit(peek, 16);  
	    	                readch();  
	    	            } while (Character.isDigit(peek) || (peek >= 'a' && peek <= 'e') || (peek >= 'A' && peek <= 'E'));  
	        		}
	        		
	        		else if(peek == 'y' || peek == 'Y'){
	        			readch();  
	    	            do {  
	    	                value = 24 * value + Character.digit(peek, 24);  
	    	                readch();  
	    	            } while (Character.isDigit(peek) || (peek >= 'A' && peek <= 'P'));  
					}
	        		
	        		 Num n = new Num(value);  
			            tokens.add("<num,"+n.toString()+">");  
			            table.put(n, "Num");
			            
			            //判断是否超出int范围
			            if(value<0){
			            	System.out.println("第 "+line+" 行出错超出数据范围");
			            }
			            return n;  
	        	}
	        	else {
		            int value = 0;  
		            do {  
		                value = 10 * value + Character.digit(peek, 10);  
		                readch();  
		            } while (Character.isDigit(peek));  
		            
		            if(peek != '.') {
			            Num n = new Num(value);  
			            tokens.add("<num,"+n.toString()+">");  
			            table.put(n, "Num");
			            
			            //判断是否超出int范围
			            if(value<0){
			            	System.out.println("第 "+line+" 行出错超出数据范围");
			            }
			            return n;  
			        }
		            
		            float x = value;
		            float d = 10;
		            for( ; ; ) {
		            	readch();
		            	if(!Character.isDigit(peek))break;
		            	x = x + Character.digit(peek, 10) / d;
		            	d = d * 10;
		            }
		            Real real = new Real(x);  
		            tokens.add("<real,"+real.toString()+">");  
		      
		            return real; 
	        	}
	        }  
	          
	        /* 
	         * 关键字或者是标识符的识别 
	         */  
	        if(Character.isLetter(peek)) {  
	            StringBuffer sb = new StringBuffer();  
	              
	            /* 首先得到整个的一个分割 */  
	            do {  
	                sb.append(peek);  
	                readch();  
	            } while (Character.isLetterOrDigit(peek));  
	              
	            /* 判断是关键字还是标识符 */  
	            String s = sb.toString(); 
	            String s1 = s;
	            s = s.toLowerCase();
	            Word w = (Word)words.get(s);  
	              
	            /* 如果是关键字或者是类型的话，w不应该是空的 */  
	            if(w != null) {  
	                table.put(w, "KeyWord or Type");  
	                tokens.add("<"+s1+">");  
	                return w; /* 说明是关键字 或者是类型名 */  
	            }  
	              
	            /* 否则就是一个标识符id */  
	            w = new Word(s, Tag.ID);  
	            tokens.add("<id,"+s1+">");  
	            table.put(w, "id");  
	            words.put(s,  w);  
	              
	            return w;  
	        }  
	          
	        /* peek中的任意字符都被认为是词法单元返回 */  
	        Token tok  = new Token(peek);  
	        // table.put(tok, "Token or Seprator");  
	        if ((int)peek != 0xffff){   
	            tokens.add("<"+tok.toString()+">"); 
	            System.out.println(tok.toString());
	           
	        }
	        	
	        peek = ' ';  
	          
	        return tok;  
	    }  
}
