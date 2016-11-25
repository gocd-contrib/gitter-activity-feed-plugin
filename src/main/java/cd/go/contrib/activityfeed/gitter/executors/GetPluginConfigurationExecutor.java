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

import cd.go.contrib.activityfeed.gitter.RequestExecutor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thoughtworks.go.plugin.api.response.DefaultGoPluginApiResponse;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;

import java.util.LinkedHashMap;
import java.util.Map;


public class GetPluginConfigurationExecutor implements RequestExecutor {

    private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public static final Field GO_SERVER_URL = new NonBlankField("go_server_url", "Go Server URL", null, true, false, "0");
    public static final Field GITTER_WEBHOOK_URL = new NonBlankField("gitter_webhook_url", "Gitter webhook URL", null, true, false, "1");

    public static final Map<String, Field> FIELDS = new LinkedHashMap<>();

    static {
        FIELDS.put(GO_SERVER_URL.key(), GO_SERVER_URL);
        FIELDS.put(GITTER_WEBHOOK_URL.key(), GITTER_WEBHOOK_URL);
    }

    public GoPluginApiResponse execute() {
        return new DefaultGoPluginApiResponse(200, GSON.toJson(FIELDS));
    }
}
