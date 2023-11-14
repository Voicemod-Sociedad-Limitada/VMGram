package org.telegram.vm.sounds.datasource;

import com.google.android.exoplayer2.util.Log;

import org.telegram.messenger.BuildConfig;
import org.telegram.vm.EncryptionHelper;
import org.telegram.vm.models.tuna.Sound;
import org.telegram.vm.models.tuna.SoundAutocomplete;
import org.telegram.vm.models.tuna.SoundCategories;
import org.telegram.vm.models.tuna.SoundCategory;
import org.telegram.vm.models.tuna.SoundsPage;
import org.telegram.vm.utils.ApplicationExecutors;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class TunaSoundsDataSource implements ISoundsDataSource {

    private static final String TUNA_BASE_URL = BuildConfig.TUNA_BASE_URL;

    private static final String TAG = "TunaSoundsDataSource";

    private static final int RESPONSE_SUCCESS = 200;
    private static final int VM_INTERNAL_APP_ERROR = -1; //Generic error

    ApplicationExecutors exec = new ApplicationExecutors();

    @Override
    public void getCategories(Function<SoundCategory[], Void> onSuccess) {
        buildAndExecuteGetRequest("categories",(response) -> {
                    SoundCategories soundCategories = SoundCategories.fromJson(response);
                    onSuccess.apply(soundCategories != null ? soundCategories.items : null);
                    return null;
                }, (error) -> {
                    Log.e(TAG, "Error getting categories: " + error);
                    return null;
                });
    }

    @Override
    public void getCategoriesWithSoundCount(Function<SoundCategory[], Void> onSuccess) {
        buildAndExecuteGetRequest("categories/sounds/count",(response) -> {
            SoundCategories soundCategories = SoundCategories.fromJson(response);
            onSuccess.apply(soundCategories != null ? soundCategories.items : null);
            return null;
        }, (error) -> {
            Log.e(TAG, "Error getting categories: " + error);
            return null;
        });
    }

    @Override
    public void getSoundById(String id,Function<Sound, Void> onSuccess) {
        buildAndExecuteGetRequest("sounds/" + id, (response) -> {
            Sound sound = Sound.fromJson(response);
            onSuccess.apply(sound);
            return null;
        }, (error) -> {
            Log.e(TAG, "Error getting sound: " + error);
            return null;
        });
    }

    @Override
    public void getSoundAutocomplete(String search, int size, Function<SoundAutocomplete, Void> onSuccess) {
        HashMap<String,String> params = new HashMap<>();
        params.put("search", search);
        params.put("size", String.valueOf(size));

        buildAndExecuteGetRequest("sounds/search/autocomplete", params, (response) -> {
            SoundAutocomplete soundAutocomplete = SoundAutocomplete.fromJson(response);
            onSuccess.apply(soundAutocomplete);
            return null;
        }, (error) -> {
            Log.e(TAG, "Error getting sound autocomplete: " + error);
            return null;
        });
    }

    @Override
    public void searchSounds(int size, int page, String search, String categoryFilter, String tagFilter, String sort, String order, Function<SoundsPage, Void> onSuccess) {
        HashMap<String,String> params = new HashMap<>();
        params.put("size", String.valueOf(size));
        params.put("page", String.valueOf(page));
        if (search != null && !search.isEmpty())  params.put("search", search);
        if (sort != null && !sort.isEmpty())  params.put("sort", sort);
        if (categoryFilter != null && !categoryFilter.isEmpty()) params.put("categoryFilter", categoryFilter);
        if (tagFilter != null && !tagFilter.isEmpty()) params.put("tagFilter", tagFilter);
        if (order != null && !order.isEmpty()) params.put("order", order);

        buildAndExecuteGetRequest(
                "sounds/search",
                params,
                (response) -> {
                    SoundsPage soundsSearch = SoundsPage.fromJson(response);
                    onSuccess.apply(soundsSearch);
                    return null;
                }, (error) -> {
                    Log.e(TAG, "Error getting sound search: " + error);
                    return null;
                });
    }

    private void buildAndExecuteGetRequest(String suffix, Function<String, Void> onSuccess,Function<Integer,Void> onError) {
        buildAndExecuteGetRequest(suffix, null, onSuccess, onError);
    }

    private void buildAndExecuteGetRequest(String suffix, Map<String, String> params, Function<String, Void> onSuccess,Function<Integer,Void> onError){
        exec.getBackground().execute(() -> {
            try {
                URL url;

                if (params != null) {
                    url = applyParameters(new URI(TUNA_BASE_URL + suffix), params).toURL();
                }else{
                    url = new URL(TUNA_BASE_URL + suffix);
                }

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("client-id", EncryptionHelper.getTunaClientID());

                //Request in a background thread.
                int responseCode = con.getResponseCode();

                //We send back the response using the main thread.
                if (responseCode == RESPONSE_SUCCESS){
                    String processedResponse = processResponse(con);
                    exec.getMainThread().execute(() -> onSuccess.apply(processedResponse));
                }else{
                    exec.getMainThread().execute(() -> onError.apply(responseCode));
                }

            } catch (IOException e) {
                onError.apply(VM_INTERNAL_APP_ERROR);
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
    }

    private String processResponse(HttpURLConnection con){
        BufferedReader in = null;
        try {
            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    URI applyParameters(URI baseUri, Map<String,String> urlParameters) {
        StringBuilder query = new StringBuilder();
        final boolean[] first = {true};

        urlParameters.forEach((key, value) -> {
            if (first[0]) {
                first[0] = false;
            } else {
                query.append("&");
            }
            try {
                query.append(URLEncoder.encode(key, "UTF-8"));
                query.append("=");
                query.append(URLEncoder.encode(value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });

       try {
            return new URI(baseUri.getScheme(), baseUri.getAuthority(),
                    baseUri.getPath(), query.toString(), null);
        } catch (URISyntaxException ex) {
            /* As baseUri and query are correct, this exception
             * should never be thrown. */
            throw new RuntimeException(ex);
        }
    }
}
