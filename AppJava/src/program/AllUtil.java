package program;

public class AllUtil {
	
	/*
	 * pr�cise le nombre de chiffre apr�s virgule du nombre donn�
	 * selon l'argument 'decimal'
	 * la fonction ne complete pas par des z�ro si 'decimal' est sup�rieur au nombre de chiffre apr�s virgule
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
