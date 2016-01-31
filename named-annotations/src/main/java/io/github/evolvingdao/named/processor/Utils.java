package io.github.evolvingdao.named.processor;

import javax.lang.model.SourceVersion;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class Utils {

	/**
	 * {@link Pattern} used to mark a parameter in the message. Example: ${an_example}.
	 */
	private static final Pattern PARAMETER_PATTERN = Pattern.compile("\\$\\{(.+?)\\}");

	private Utils() {

	}

	static String format(String message, Map<String, Object> parameters) {
		Matcher matcher = PARAMETER_PATTERN.matcher(message);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String var = matcher.group(1);
			Object replacement = parameters.get(var);
			if (replacement != null) {
				matcher.appendReplacement(sb, String.valueOf(replacement));
			}
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	static Set<String> getParameters(String message) {
		Set<String> parameters = new HashSet<>();
		Matcher matcher = PARAMETER_PATTERN.matcher(message);

		while (matcher.find()) {
			parameters.add(matcher.group(1));
		}

		return parameters;
	}

	static Optional<ResourceBundle> getResourceBundle(String bundleName, final Locale desiredLocale) {
		ResourceBundle resourceBundle;

		try {
			resourceBundle = ResourceBundle.getBundle(bundleName, desiredLocale, new ResourceBundle.Control() {

				@Override
				public List<Locale> getCandidateLocales(String baseName, Locale locale) {
					return Collections.singletonList(desiredLocale);
				}

				@Override
				public Locale getFallbackLocale(String baseName, Locale locale) {
					return null;
				}

			});
		} catch (Exception e) {
			// If there is a problem, returns null
			resourceBundle = null;
		}

		return resourceBundle == null ? Optional.empty() : Optional.of(resourceBundle);
	}

	static Optional<String> getString(ResourceBundle bundle, String key) {
		String string;

		try {
			string = bundle.getString(key);
		} catch (Exception e) {
			// In case of error, returns null
			string = null;
		}

		return string == null ? Optional.empty() : Optional.of(string);
	}

	static boolean isAValidKey(String key) {
		return (key != null) && SourceVersion.isName(key) && !key.contains(".");
	}

}
