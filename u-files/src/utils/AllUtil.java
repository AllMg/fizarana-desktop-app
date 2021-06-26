package utils;

public class AllUtil {
	
	/*
	 * precise le nombre de chiffre apres virgule du nombre donne
	 * selon l'argument 'decimal'
	 * la fonction ne complete pas par des zero si 'decimal' est superieur au nombre de chiffre apres virgule
	 * */
	public static double toFixed(double d, int decimal){
		double result;
		String str = String.valueOf(d);
		String[] split = str.split("\\.");
		char[] charArray;
		String dec = "";
		result = Double.valueOf(split[0]);
		if(split.length == 2 && decimal > 0){
			charArray = split[1].toCharArray();
			for(int i=0; i<charArray.length; i++){
				if(i == decimal){
					break;
				}
				dec += String.valueOf(charArray[i]);
			}
			result = Double.valueOf(((int)result)+"."+dec);
		}
		return result;
	}

    public static boolean isNumeric(char c){
        char[] numbers = {'1','2','3','4','5','6','7','8','9','0'};
        for(char cc : numbers){
            if(c == cc){
                return true;
            }
        }
        return false;
    }
    
    /*
     * chaine est une chaine de caractere de la forme "95,65,47,12,...,n"
     * la reponse est de la forme [95,65,47,12,...,n]*/
    public static byte[] toByteArray(String chaine) {
    	String[] split = chaine.split(",");
    	byte[] bytes = new byte[split.length];
    	int i = 0;
    	for(String str : split) {
    		bytes[i] = Byte.valueOf(str);
    		i++;
    	}
    	return bytes;
    }

}
