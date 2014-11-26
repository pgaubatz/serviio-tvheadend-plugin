Tvheadend Plugin for Serviio
============================

This is a plugin for the [Serviio](http://www.serviio.org) media server.
It makes [Tvheadend](https://tvheadend.org)'s channel list available as live streams in Serviio.

Instructions
------------
- Read the Serviio's [manual](http://www.serviio.org/component/content/article/10-uncategorised/42-online-content-management#plugins) if you don't know how to install plugins.
- Add the URL of Serviio's Web interface (e.g., _http://host:9981_) as a **Web Resource** under **Online sources**.
- The plugin ignores the setting **Max. number of feed items to retrieve**, i.e., it will always retrieve the complete channel list.
- If everything went smoothly, Tvheadend's channels will be available as live streams under the category **Video &rarr; Online**.

Troubleshooting
---------------
- If something goes wrong, make sure that Tvheadend's API can be reached without authentication. You can easily check this by opening, e.g., _http://host:9981/api/serverinfo_ in your favorite Web browser's incognito/private mode. If Tvheadend is configured correctly, it should immediately return a JSON document (e.g, `{"sw_version": "3.9...`), instead of requesting a username/password.
