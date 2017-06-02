package com.example.contactbook;

import android.content.Context;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.People;

import java.io.IOException;

/**
 * Created by me on 31.05.2017.
 */

public class ContactHelper {

    private static final String APPLICATION_NAME = "Contacts_App";

    public static People setUp(Context context, String serverAuthCode) throws IOException {
        HttpTransport httpTransport = new NetHttpTransport();
        JacksonFactory jacksonFactory = new JacksonFactory().getDefaultInstance();

        String redirectUrl = "urn:ietf:wg:oauth:2.0:oob";

        GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                httpTransport,
                jacksonFactory,
                context.getString(R.string.client_ID),
                context.getString(R.string.clientSecret),
                serverAuthCode,
                redirectUrl).execute();

        GoogleCredential credential = new GoogleCredential.Builder()
                .setClientSecrets(context.getString(R.string.client_ID), context.getString(R.string.clientSecret))
                .setTransport(httpTransport)
                .setJsonFactory(jacksonFactory)
                .build();

        credential.setFromTokenResponse(tokenResponse);

        return new People.Builder(httpTransport, jacksonFactory, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}
