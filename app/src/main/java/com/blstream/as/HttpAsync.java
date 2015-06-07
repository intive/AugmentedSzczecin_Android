package com.blstream.as;



import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class HttpAsync{
    private static final MediaType TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String JSON_EMAIL = "email";
    private static final String JSON_PASSWORD = "password";
    private static final String SERVER_LOGIN_PATH = "whoami";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private OkHttpClient client = new OkHttpClient();

    public Call post(final String url, final String email, final String pass, Callback callback) throws IOException, JSONException {
        Call call;

        if (url.contains(SERVER_LOGIN_PATH)){
            String credentials = Credentials.basic(email, pass);
            Request request = new Request.Builder()
                    .url(url)
                    .header(AUTHORIZATION_HEADER, credentials)
                    .build();

            call = client.newCall(request);
        }
        else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSON_EMAIL, email);
            jsonObject.put(JSON_PASSWORD, pass);

            RequestBody body = RequestBody.create(TYPE_JSON, jsonObject.toString());
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            call = client.newCall(request);
        }
        call.enqueue(callback);
        return call;
    }
}
