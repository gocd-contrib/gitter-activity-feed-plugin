/*
 * Copyright 2016 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cd.go.contrib.activityfeed.gitter.executors;

import cd.go.contrib.activityfeed.gitter.GitterNotificationFeedPlugin;
import cd.go.contrib.activityfeed.gitter.PluginRequest;
import cd.go.contrib.activityfeed.gitter.PluginSettings;
import cd.go.contrib.activityfeed.gitter.RequestExecutor;
import cd.go.contrib.activityfeed.gitter.requests.StageStatusRequest;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

public class StageStatusRequestExecutor implements RequestExecutor {
    private static final Gson GSON = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

    private final StageStatusRequest request;
    private final PluginRequest pluginRequest;

    public StageStatusRequestExecutor(StageStatusRequest request, PluginRequest pluginRequest) {
        this.request = request;
        this.pluginRequest = pluginRequest;
    }

    @Override
    public GoPluginApiResponse execute() throws Exception {
        HashMap<String, Object> responseJson = new HashMap<>();
        try {
            sendNotification();
            responseJson.put("status", "success");
        } catch (Exception e) {
            GitterNotificationFeedPlugin.LOG.error("Failed to send notification to gitter:", e);
            responseJson.put("status", "failure");
            responseJson.put("messages", Arrays.asList(e.getMessage()));
        }
        return new DefaultGoPluginApiResponse(200, GSON.toJson(responseJson));
    }

    protected void sendNotification() throws Exception {

        PluginSettings settings = pluginRequest.getPluginSettings();
        HttpsURLConnection connection = getHttpsURLConnection(settings.getGitterWebhookUrl());

        String stageLocator = String.format("%s/%s/%s/%s",
                request.pipeline.name,
                request.pipeline.counter,
                request.pipeline.stage.name,
                request.pipeline.stage.counter);

        String trackbackURL = String.format("%s/go/pipelines/%s", settings.getGoServerUrl(), stageLocator);
        String status = getStatus(request.pipeline.stage.result).toLowerCase();

        JsonObject jsonObject = new JsonObject();
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("[").append(stageLocator).append("]");
        messageBuilder.append("(").append(trackbackURL).append(")");
        messageBuilder.append(" ").append(status).append(".");

        jsonObject.addProperty("message", messageBuilder.toString());
        jsonObject.addProperty("status", status);

        String payloadJSON = jsonObject.toString();
        postContent(connection, payloadJSON);

        int responseCode = connection.getResponseCode();
        String response = getResponse(connection);

        GitterNotificationFeedPlugin.LOG.info("Notification status(" + responseCode + "): " + response.toString());

    }

    private String getResponse(HttpsURLConnection connection) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        reader.close();
        return response.toString();
    }

    private String getStatus(String result) {
        return "Unknown".equals(result) ? "triggered" : result.toString();
    }

    private void postContent(HttpsURLConnection connection, String payloadJSON) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(payloadJSON);
        writer.flush();
        writer.close();
    }

    private HttpsURLConnection getHttpsURLConnection(String url) throws IOException {
        URL urlObj = new URL(url);
        HttpsURLConnection connection = (HttpsURLConnection) urlObj.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        return connection;
    }
}
