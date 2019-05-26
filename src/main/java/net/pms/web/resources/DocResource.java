package net.pms.web.resources;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.pms.PMS;
import net.pms.configuration.PmsConfiguration;
import net.pms.newgui.DbgPacker;
import net.pms.remote.RemoteUtil.ResourceManager;

@Singleton
@Path("doc")
public class DocResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(DocResource.class);

	@SuppressWarnings("unused")
	private final static String CRLF = "\r\n";

	private PmsConfiguration configuration;

	private ResourceManager resources;

	@Inject
	public DocResource(PmsConfiguration configuration, ResourceManager resources) {
		this.configuration = configuration;
		this.resources = resources;
		// Make sure logs are available right away
		getLogs(false);
	}

	@GET
	@Path("favicon")
	public Response getFavicon() throws IOException {
		LOGGER.debug("root req favicon");
		return ResourceUtil.logoResponse();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Object> handle() throws IOException {
		HashMap<String, Object> vars = new HashMap<>();
		vars.put("logs", getLogs(true));
		if (configuration.getUseCache()) {
			vars.put("cache", "http://" + PMS.get().getServer().getHost() + ":" + PMS.get().getServer().getPort() + "/console/home");
		}

		return vars;
	}

	private ArrayList<HashMap<String, String>> getLogs(boolean asList) {
		Set<File> files = new DbgPacker().getItems();
		if (!asList) {
			return null;
		}
		ArrayList<HashMap<String, String>> logs = new ArrayList<HashMap<String, String>>();
		for (File f : files) {
			if (f.exists()) {
				String id = String.valueOf(resources.add(f));
				if (asList) {
					HashMap<String, String> item = new HashMap<>();
					item.put("filename", f.getName());
					item.put("id", id);
					logs.add(item);
				}
			}
		}
		return logs;
	}
}
