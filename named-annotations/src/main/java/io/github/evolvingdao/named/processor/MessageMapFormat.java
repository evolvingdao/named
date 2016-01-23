package io.github.evolvingdao.named.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class MessageMapFormat {

	/**
	 * {@link Pattern} used to mark a parameter in the message. Example: ${an_example}.
	 */
	private static final Pattern PARAMETER_PATTERN = Pattern.compile("\\$\\{(.+?)\\}");

	private MessageMapFormat() {
		throw new AssertionError("No MessageMapFormat for you!!");
	}

	static final String format(String message, Map<String, String> parameters) {
		Matcher matcher = PARAMETER_PATTERN.matcher(message);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String var = matcher.group(1);
			String replacement = parameters.get(var);
			if (replacement != null) {
				matcher.appendReplacement(sb, replacement);
			}
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	static final List<String> getParameters(String message) {
		List<String> parameters = new ArrayList<>();
		Matcher matcher = PARAMETER_PATTERN.matcher(message);

		while (matcher.find()) {
			parameters.add(matcher.group(1));
		}

		return parameters;
	}

}
