package org.telegram.vm.utils.download;

import java.io.File;

public class DownloadResult{
    public int id;
    public File file;

    public DownloadResult(int id, File file){
        this.id = id;
        this.file = file;
    }
}
