/**
   Copyright 2016, Robert Bossy

   This file is part of Yadrol.

   Yadrol is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   Yadrol is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Yadrol.  If not, see <http://www.gnu.org/licenses/>.
**/

package org.phatonin.yadrol.app.web;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
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

@Path("")
public class YadrolWeb {

	@GET
	@Path("/run")
	@Produces(MediaType.APPLICATION_JSON)
	public static Response run(
			@Context HttpContext httpContext
			) throws IOException {
		return doRun(httpContext, null);
	}
	
	@POST
	@Path("/run")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	public static Response run(
			@Context HttpContext httpContext,
			FormDataMultiPart formData
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
			e.printStackTrace();
			return failureResponse(options, "evaluation", e.getMessage());
		}
		catch (ParseException e) {
			e.printStackTrace();
			return failureResponse(options, "parse", e.getMessage());
		}
	}
	
	@SuppressWarnings("unchecked")
	private static Response successResponse(WebOptions options, YadrolResult result) {
		JSONObject obj = createResponseObject(options, true);
		obj.put("result", YadrolResultConverter.INSTANCE.convert(result, options));
		return Response.ok(obj.toString()).build();
	}
	
	@SuppressWarnings("unchecked")
	private static Response failureResponse(WebOptions options, String errType, String msg) {
		JSONObject obj = createResponseObject(options, false);
		obj.put("error", errType);
		obj.put("message", msg);
		return Response.status(422).entity(obj.toString()).build();
	}
	
	@SuppressWarnings("unchecked")
	private static JSONObject createResponseObject(WebOptions options, boolean success) {
		JSONObject result = new JSONObject();
		result.put("success", success);
		result.put("options", WebOptionsConverter.INSTANCE.convert(options, options));
		return result;
	}
}
