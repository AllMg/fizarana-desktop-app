package program;

import java.io.File;

import utils.FolderUtil;

public class PageCreator {
	
    protected String page;
    protected String folderComponent;
    protected String folderHistory;

    public PageCreator(String page, String folderComponent, String folderHistory) {
        this.page = page;
        this.folderComponent = folderComponent;
        this.folderHistory = folderHistory;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public void createRootHistory(String fullPath){
        String rootHistory = "";
        if(fullPath != null){
            String history;
            String full = "";
            String[] split = fullPath.replace("\\", "/").split("/");
            for(int i=0; i<split.length; i++){
                full += split[i]+File.separator;
                history = folderHistory.replace("[index]", String.valueOf(i));
                history = history.replace("[pathName]", split[i]);
                history = history.replace("[isFolder]", "Y");
                history = history.replace("[fullPath]", full);
                rootHistory += history;
            }
        }
        page = page.replace("[rootHistory]", rootHistory);
    }

    public void createFolderList(File[] files){
        String folder;
        String folderList = "";
        String iconName;
        String folderName;
        String folderInfo;
        String isFolder;
        String labelTarget;
        int nbElement;
        long size;
        File[] listFiles;

        for(int i=0; i<files.length; i++){
            if(files[i].isDirectory()){
                iconName = "folder.png";
                folderName = files[i].getName();
                if(folderName.length() == 0){
                        folderName = files[i].toString();
                }
                listFiles = files[i].listFiles();
                nbElement = listFiles != null ? listFiles.length : 0;
                labelTarget = nbElement > 0 ? String.valueOf(i) : "Modal";
                folderInfo = nbElement + " &eacute;l&eacute;ment";
                if(nbElement > 1){
                        folderInfo += "s";
                }
                isFolder = "Y";
            }
            else{
                labelTarget = String.valueOf(i);
                iconName = "file.png";
                folderName = files[i].getName();
                size = files[i].length();
                folderInfo = FolderUtil.getFileExt(folderName) + FolderUtil.getFileSize(size);
                isFolder = "N";
            }
            folder = folderComponent.replace("[index]", String.valueOf(i));
            folder = folder.replace("[labelTarget]", labelTarget);
            folder = folder.replace("[iconName]", iconName);
            folder = folder.replace("[folderName]", folderName);
            folder = folder.replace("[folderInfo]", folderInfo);
            folder = folder.replace("[isFolder]", isFolder);
            folder = folder.replace("[fullPath]", files[i].getAbsolutePath());
            folderList += folder;
        }
        page = page.replace("[folderList]", folderList);
    }
	
}
