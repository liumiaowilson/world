package org.wilson.world.endpoint;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.wilson.world.api.util.APIResultUtils;

public class DemoEndPoint extends EndPoint {
	
	@Override
	public String getName() {
		return "demo";
	}

	@EndPointMark(name = "test", type = EndPointMethodType.GET, scope = EndPointMethodScope.Private)
	public Response doGet(HttpHeaders headers, HttpServletRequest request, UriInfo uriInfo)
			throws Exception {
		return APIResultUtils.buildJSONResponse(APIResultUtils.buildOKAPIResult("test"));
	}

	@EndPointMark(name = "try", type = EndPointMethodType.GET, scope = EndPointMethodScope.Public)
	public Response doGetPublic(HttpHeaders headers, HttpServletRequest request, UriInfo uriInfo)
			throws Exception {
		return APIResultUtils.buildURLResponse(request, "public/public.jsp");
	}

}
