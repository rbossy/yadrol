package org.phatonin.yadrol.app.web;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.phatonin.yadrol.app.YadrolResult;
import org.phatonin.yadrol.app.web.json.WebOptionsConverter;
import org.phatonin.yadrol.app.web.json.YadrolResultConverter;
import org.phatonin.yadrol.core.EvaluationException;
import org.phatonin.yadrol.core.parser.ParseException;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.multipart.FormDataMultiPart;

@Path("/api")
public class YadrolWeb {

	@GET
	@Path("/run")
	public static Response run(
			@Context HttpContext httpContext
			) throws IOException {
		return doRun(httpContext, null);
	}
	
	@POST
	@Path("/run")
	public static Response run(
			@Context HttpContext httpContext,
			@Context FormDataMultiPart formData
			) throws IOException {
		return doRun(httpContext, formData);
	}
	
	private static Response doRun(HttpContext httpContext, FormDataMultiPart formData) throws IOException {
		WebOptions options = new WebOptions();
		try {
			HttpRequestContext requestContext = httpContext.getRequest();
			WebParamDispatcher.setParams(options, formData, requestContext);
			if (!options.hasExpressionString()) {
				throw new RuntimeException();
			}
			YadrolResult result = YadrolResult.createResult(options);
			return successResponse(options, result);
		}
		catch (EvaluationException e) {
			return failureResponse(options, "evaluation", e.getMessage());
		}
		catch (ParseException e) {
			return failureResponse(options, "parse", e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	private static Response successResponse(WebOptions options, YadrolResult result) {
		JSONObject obj = new JSONObject();
		obj.put("status", "success");
		obj.put("options", WebOptionsConverter.INSTANCE.convert(options, options));
		obj.put("result", YadrolResultConverter.INSTANCE.convert(result, options));
		return Response.ok(obj.toString()).build();
	}
	
	@SuppressWarnings("unchecked")
	private static Response failureResponse(WebOptions options, String errType, String msg) {
		JSONObject obj = new JSONObject();
		obj.put("status", "failure");
		obj.put("options", WebOptionsConverter.INSTANCE.convert(options, options));
		obj.put("error-type", errType);
		obj.put("message", msg);
		return Response.status(422).entity(obj.toString()).build();
	}
}
