package lexer;

public class Type extends Word{
	 public Type(String s, int tag) {  
	        super(s, tag);  
	    }  
	      
	    public static final Type  
	        Integer = new Type("integer", Tag.BASIC), 
	        Real = new Type("real", Tag.BASIC), 
	        Float = new Type("float", Tag.BASIC),
	        String = new Type("string", Tag.BASIC),
	        Char = new Type ("char", Tag.BASIC),  
	        Bool =  new Type("bool", Tag.BASIC),
	        Null = new Type("null", Tag.BASIC);
}
