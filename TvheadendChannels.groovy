import groovy.json.JsonSlurper
import org.serviio.library.metadata.*
import org.serviio.library.online.*

/**
 * TvheadendChannels is an URL extractor plugin that extracts Tvheadend's channel list
 *
 * @author Patrick Gaubatz <patrick@gaubatz.at>
 *
 */

class TvheadendChannels extends WebResourceUrlExtractor {
    final static THUMBNAIL_URL_KEY = 'thumbnailUrl'
    final static CONTENT_URL_KEY = 'contentUrl'

    final static RESOURCE_TITLE_PREFIX = 'Tvheadend on '

    final static API_SERVERINFO_PATH = '/api/serverinfo'
    final static API_CHANNEL_GRID_PATH = '/api/channel/grid?start=0&limit=9999'
    final static API_CHANNEL_STREAM_PATH = '/stream/channel/'

    final static JSON = new JsonSlurper()

    final String extractorName = getClass().name

    @Override
    boolean extractorMatches(URL feedUrl) {
        final object = fetchJSON(feedUrl, API_SERVERINFO_PATH)
        return object && object.api_version != null
    }

    @Override
    WebResourceContainer extractItems(URL resourceUrl, int maxItemsToRetrieve) {
        final url = resourceUrl.toString()
        final items = []
        final channels = fetchJSON(resourceUrl, API_CHANNEL_GRID_PATH)

        def maxNumLength = 0
        try {
            final maxNum = channels.entries.max { it.number }
            maxNumLength = maxNum.number.toString().length()
        } catch (Exception e) {
            log('An Exception occurred: ' + e)
        }

        for (Map entry : channels.entries) {
            def title = entry.name
            try {
                title = entry.number.toString().padLeft(maxNumLength, '0') + '. ' + entry.name
            } catch (Exception e) {
                log('An Exception occurred: ' + e)
            }

            final info = [:]
            info[CONTENT_URL_KEY] = url + API_CHANNEL_STREAM_PATH + entry.uuid
            if (entry.icon_public_url) {
                info[THUMBNAIL_URL_KEY] = url + '/' + entry.icon_public_url
            }

            items.add(new WebResourceItem(title: title, additionalInfo: info))
        }

        return new WebResourceContainer(
            title: RESOURCE_TITLE_PREFIX + resourceUrl.host,
            items: items
        )
    }

    @Override
    ContentURLContainer extractUrl(WebResourceItem item, PreferredQuality requestedQuality) {
        final info = item.additionalInfo
        return new ContentURLContainer(
            fileType: MediaFileType.VIDEO,
            contentUrl: info[CONTENT_URL_KEY],
            thumbnailUrl: info[THUMBNAIL_URL_KEY],
            live: true
        )
    }

    private fetchJSON(URL url, String path) {
        final text = openURL(new URL(url.toString() + (path ?: '')), null)
        return text ? JSON.parseText(text) : null
    }

    static void main(args) {
        def extractor = new TvheadendChannels()
        def realTvheadendUrl = new URL("http://10.0.2.5:9981/tvheadend")

        assert extractor.extractorMatches(realTvheadendUrl)
        assert !extractor.extractorMatches(new URL("http://google.com"))

        def resourceContainer = extractor.extractItems(realTvheadendUrl, 1)
        println resourceContainer

        def urlContainer = extractor.extractUrl(resourceContainer.items[0], PreferredQuality.HIGH)
        println urlContainer
    }
}
