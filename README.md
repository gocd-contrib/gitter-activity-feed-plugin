# GoCD plugin for Gitter Activity Feed
    
    GoCD plugin to send build status notification as a part gitter activity feed.
     
# Installation
    
    Copy gitter-activity-feed-plugin.x.x.x-SNAPSHOT.jar to plugin folder

# GoCD webhook URL
    
   1. Go to your gitter room, where you want to receive activity feed from GoCD server
   2. Click on "Room settings" and select "Integrations"
   3. Select GoCD from integration list
   4. Update your prefrences for activity feed notification
   5. Note the webhook url and click in done

# Configuration

   1. Open plugin setting from **Admin** --> **Plugin**
   2. Specify GoCD server url (e.g. http://<your server name>:8153)
   3. Specify Gitter webhook url for GoCD
    
## License

```plain
Copyright 2016 ThoughtWorks, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
