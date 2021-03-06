package org.xbib.elasticsearch.action;

import static org.elasticsearch.common.xcontent.ToXContent.EMPTY_PARAMS;
import static org.elasticsearch.rest.RestRequest.Method.GET;
import static org.elasticsearch.rest.RestStatus.OK;
import static org.elasticsearch.rest.action.support.RestXContentBuilder.restContentBuilder;

import java.io.IOException;
import java.util.List;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.XContentRestResponse;
import org.elasticsearch.rest.XContentThrowableRestResponse;
import org.xbib.elasticsearch.knapsack.Knapsack;
import org.xbib.elasticsearch.knapsack.KnapsackService;

public class RestImportStateAction extends BaseRestHandler {

	private final KnapsackService knapsackService;

	@Inject
	public RestImportStateAction(Settings settings, Client client, RestController controller, KnapsackService stateService) {
		super(settings, client);
		this.knapsackService = stateService;

		controller.registerHandler(GET, "/_import/state", this);
	}

	@Override
	public void handleRequest(RestRequest request, RestChannel channel) {
		try {
			channel.sendResponse(new XContentRestResponse(request, OK, getImportState(request)));
		} catch (IOException ioe) {
			try {
				channel.sendResponse(new XContentThrowableRestResponse(request, ioe));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	private XContentBuilder getImportState(RestRequest request) throws IOException {
		List<Knapsack> imports = knapsackService.getImports();

		XContentBuilder builder = restContentBuilder(request);
		builder.startObject();
		builder.field("timestamp", System.currentTimeMillis());
		builder.field("imports");
		builder.startArray();
		for (Knapsack import_ : imports) {
			import_.toXContent(builder, EMPTY_PARAMS);
		}
		builder.endArray();
		builder.endObject();

		return builder;
	}

}