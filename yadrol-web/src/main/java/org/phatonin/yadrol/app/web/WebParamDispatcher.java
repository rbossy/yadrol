package org.phatonin.yadrol.app.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.phatonin.yadrol.core.OutputMode;
import org.phatonin.yadrol.core.values.ValueType;

import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

public enum WebParamDispatcher {
	EXPRESSION_STRING("expression-string") {
		@Override
		public void setParam(WebOptions options, String value) {
			options.setExpressionString(value);
		}

		@Override
		public String getParam(WebOptions options) {
			return options.getExpressionString();
		}
	},
	
	OUTPUT_MODE("output-mode") {
		@Override
		public void setParam(WebOptions options, String value) {
			OutputMode outputMode = OutputMode.fromString(value);
			options.setOutputMode(outputMode);
		}

		@Override
		public String getParam(WebOptions options) {
			return options.getOutputMode().toString();
		}
	},
	
	EVALUATION_TYPE("evaluation-type") {
		@Override
		public void setParam(WebOptions options, String value) {
			ValueType valueType = ValueType.fromString(value);
			options.setDefaultEvaluationType(valueType);
		}

		@Override
		public String getParam(WebOptions options) {
			return options.getDefaultEvaluationType().toString();
		}
	},
	
	SAMPLE_SIZE("sample-size") {
		@Override
		public void setParam(WebOptions options, String value) {
			long sampleSize = Long.parseLong(value);
			options.setSampleSize(sampleSize);
		}

		@Override
		public String getParam(WebOptions options) {
			return Long.toString(options.getSampleSize());
		}
	},
	
	SEED("seed") {
		@Override
		public void setParam(WebOptions options, String value) {
			long seed = Long.parseLong(value);
			options.setSeed(seed);
		}

		@Override
		public String getParam(WebOptions options) {
			return Long.toString(options.getSeed());
		}
	},
	
	CONFIDENCE_INTERVAL_RISK("confidence-interval-risk") {
		@Override
		public void setParam(WebOptions options, String value) {
			double risk = Double.parseDouble(value);
			options.setConfidenceIntervalRisk(risk);
		}

		@Override
		public String getParam(WebOptions options) {
			return Double.toString(options.getConfidenceIntervalRisk());
		}
	}
	;
	
	private static final String VALUE_UPLOAD = "upload";

	public final String name;
	
	private WebParamDispatcher(String name) {
		this.name = name;
	}

	public abstract void setParam(WebOptions options, String value);
	public abstract String getParam(WebOptions options);

	public static void setParams(WebOptions options, FormDataMultiPart formData, HttpRequestContext requestContext) throws IOException {
		Map<String,WebParamDispatcher> paramMap = WebParamDispatcher.getWebParamDispatcherMap();
		setFormParams(options, paramMap, formData);
		setQueryParams(options, paramMap, requestContext);
	}
	
	private static Map<String,WebParamDispatcher> getWebParamDispatcherMap() {
		Map<String,WebParamDispatcher> result = new HashMap<String,WebParamDispatcher>();
		for (WebParamDispatcher wpd : WebParamDispatcher.values()) {
			result.put(wpd.name, wpd);
		}
		return result;
	}

	private static void setFormParams(WebOptions options, Map<String,WebParamDispatcher> paramMap, FormDataMultiPart formData) throws IOException {
		if (formData == null) {
			return;
		}
		Map<String,List<FormDataBodyPart>> formFields = formData.getFields();
		for (Map.Entry<String,List<FormDataBodyPart>> e : formFields.entrySet()) {
			List<FormDataBodyPart> fields = e.getValue();
			if (fields.isEmpty()) {
				continue;
			}
			FormDataBodyPart field = fields.get(fields.size() - 1);
			String name = e.getKey();
			if (name.startsWith(VALUE_UPLOAD + "-")) {
				name = name.substring(7);
				InputStream is = field.getValueAs(InputStream.class);
				String value = swallowFile(is);
				setParam(options, paramMap, name, value);
			}
			else {
				String value = field.getValue();
				setParam(options, paramMap, name, value);
			}
		}
	}

	private static String swallowFile(InputStream is) throws IOException {
		Reader r = new InputStreamReader(is, "UTF-8");
		BufferedReader br = new BufferedReader(r);
		char[] buf = new char[1024];
		StringBuilder sb = new StringBuilder();
		while (true) {
			int n = br.read(buf);
			if (n == -1) {
				break;
			}
			sb.append(buf, 0, n);
		}
		return sb.toString();
	}

	private static void setParam(WebOptions options, Map<String,WebParamDispatcher> paramMap, String name, String value) {
		if (!paramMap.containsKey(name)) {
			throw new RuntimeException("unknown parameter: " + name);
		}
		WebParamDispatcher wpd = paramMap.get(name);
		wpd.setParam(options, value);
	}

	private static void setQueryParams(WebOptions options, Map<String,WebParamDispatcher> paramMap, HttpRequestContext requestContext) {
		MultivaluedMap<String,String> params = requestContext.getQueryParameters();
		for (Map.Entry<String,List<String>> e : params.entrySet()) {
			List<String> values = e.getValue();
			if (values.isEmpty()) {
				continue;
			}
			String name = e.getKey();
			String value = values.get(values.size() - 1);
			setParam(options, paramMap, name, value);
		}
	}
}
