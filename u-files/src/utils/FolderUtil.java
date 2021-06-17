package utils;

import java.io.File;

public class FolderUtil {

    public static File[] getFolderList(String root){
        //System.out.println("getFolderList");
        File[] result = new File[0];
        if(root == null){
            result = File.listRoots();
        }
        else{
            File fileRoot = new File(root);
            if(fileRoot.isDirectory()){
                result = fileRoot.listFiles();
            }
        }
        return result;
    }

    public static String getFileSize(long size){
        String result = "";
        double ko;
        double mo;
        double go;
        if(size < 1024){ // si la taille du fichier est inferieur a 1024 octets alors en retourne comme unite le octet
            result = size + " octet";
            if(size > 1){
                result += "s";
            }
        }
        else{
            ko = (double) size / 1024;
            if(ko < 1024){ // si la taille du fichier est inferieur a 1024 Ko alors en retourne comme unite le Ko
                result = AllUtil.toFixed(ko, 2) + " Ko";
            }
            else{
                mo = ko / 1024;
                if(mo < 1024){ // si la taille du fichier est inferieur a 1024 Mo alors en retourne comme unite le Mo
                    result = AllUtil.toFixed(mo, 2) + " Mo";
                }
                else{
                    go = mo / 1024;
                    result = AllUtil.toFixed(go, 2) + " Go";
                }
            }
        }
        return result;
    }

    public static String getFileExt(String filename){
        String[] split = filename.split("\\.");
        if(split.length > 1){
            return "["+split[split.length - 1].toUpperCase()+"] ";
        }
        return "";
    }
	
    /*
     * Prend le nom du fichier contenu dans le dossier donné depuis la ligne du requête*/
	public static String getCorrectFile(String folderContainer, String lineFromRequest) {
		File folder = new File(folderContainer);
		String[] filesName = folder.list();
		for(String filename : filesName) {
			//System.out.println(filename);
			if(lineFromRequest.contains(filename)) {
				return filename;
			}
		}
		return null;
	}
	
}
