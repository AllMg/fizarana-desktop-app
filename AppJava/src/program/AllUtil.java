package program;

public class AllUtil {
	
	/*
	 * précise le nombre de chiffre après virgule du nombre donné
	 * selon l'argument 'decimal'
	 * la fonction ne complete pas par des zéro si 'decimal' est supérieur au nombre de chiffre après virgule
	 * */
	public static double toFixed(double d, int decimal){
		double result;
		String str = String.valueOf(d);
		String[] split = str.split("\\.");
		char[] charArray;
		String dec = "";
		result = new Double(split[0]).doubleValue();
		if(split.length == 2 && decimal > 0){
			charArray = split[1].toCharArray();
			for(int i=0; i<charArray.length; i++){
				if(i == decimal){
					break;
				}
				dec += String.valueOf(charArray[i]);
			}
			result = new Double(((int)result)+"."+dec);
		}
		return result;
	}

}
