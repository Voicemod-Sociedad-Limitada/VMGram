package org.telegram.vm.sounds.utils;

import android.net.Uri;

import com.google.android.exoplayer2.util.Log;

import org.telegram.messenger.FileLoader;
import org.telegram.vm.utils.ApplicationExecutors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.function.Function;

public class DownloaderHelper {

    private static final String TAG = "DownloaderHelper";
    ApplicationExecutors exec = new ApplicationExecutors();
    private Uri destinationDirectory;
    private boolean enableCache = false;

    public DownloaderHelper(boolean cache, String cacheDirectory){
        enableCache = cache;

        if (!cacheDirectory.startsWith("/")){
            cacheDirectory = "/" + cacheDirectory;
        }

        destinationDirectory = Uri.parse("file://" + FileLoader.getDirectory(FileLoader.MEDIA_DIR_CACHE)+cacheDirectory);

        File directory = new File(destinationDirectory.getPath());

        if (!directory.exists()) directory.mkdir();

        if (!enableCache){
            clearCachedFiles();
        }
    }

    /***
     * Clears all cached files. This will be called automatically in every app start if cache is disabled.
     */
    public void clearCachedFiles(){
        File directory = new File(destinationDirectory.getPath());
            for (File file : Objects.requireNonNull(directory.listFiles()))
                if (!file.isDirectory()) {
                    file.delete();
                }
    }

    /***
     * Downloads a file from a given url and saves it to the destination directory.
     * @param url The url of the file to download (in String format).
     * @param onSuccess The callback to be called when the download finished (or the file already exists and enableCache == true).
     * @param onProgressUpdated The callback to be called when the progress is updated (integer from 0 to 100).
     * @param onError The callback to be called if an error occurs.
     */
    public void downloadToCacheFolder(String url, Function<File,Void> onSuccess, Function<Integer, Void> onProgressUpdated, Function<String,Void> onError){
        try {
            downloadToCacheFolder(new URL(url), onSuccess, onProgressUpdated, onError);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /***
     * Downloads a file from a given url and saves it to the destination directory.
     * @param url The url of the file to download.
     * @param onSuccess The callback to be called when the download finished (or the file already exists and enableCache == true).
     * @param onProgressUpdated The callback to be called when the progress is updated (integer from 0 to 100).
     * @param onError The callback to be called if an error occurs.
     */
    public void downloadToCacheFolder(URL url, Function<File,Void> onSuccess, Function<Integer, Void> onProgressUpdated, Function<String,Void> onError){
        exec.getBackground().execute(() -> {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    String error = "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                    Log.e(TAG,error);
                    onError.apply(error);
                }

                //To show download percentage
                int fileLength = connection.getContentLength();

                input = connection.getInputStream();

                String urlString = url.toString();
                String fileName = urlString.substring( urlString.lastIndexOf('/')+1);
                String outputFilePath = destinationDirectory.getPath() + "/" + fileName;

                File outputFile = new File(outputFilePath);
                if (enableCache && outputFile.exists()) {
                    onProgressUpdated.apply(100);
                    exec.getMainThread().execute(() -> onSuccess.apply(outputFile));
                }else {
                    output = new FileOutputStream(destinationDirectory.getPath() + "/" + fileName);

                    byte[] data = new byte[4096];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        if (fileLength > 0) { // only if total length is known
                            final int progress = (int) (total * 100 / fileLength);
                            exec.getMainThread().execute(() -> onProgressUpdated.apply(progress));
                        }
                        output.write(data, 0, count);
                    }

                    exec.getMainThread().execute(() -> onSuccess.apply(new File(outputFilePath)));
                }
            } catch (Exception e) {
                Log.e(TAG,e.getMessage());
                exec.getMainThread().execute(() -> onError.apply(e.getMessage()));
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
        });
    }

}
