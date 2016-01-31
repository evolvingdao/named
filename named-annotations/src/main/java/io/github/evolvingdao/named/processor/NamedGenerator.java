package io.github.evolvingdao.named.processor;

import javax.lang.model.element.Element;
import java.util.*;

import static io.github.evolvingdao.named.processor.BundleProblemSeverity.ERROR;
import static io.github.evolvingdao.named.processor.BundleProblemSeverity.WARNING;

final class NamedGenerator {

	final Set<BundleProblem> problems;
	private final NamedOwnerElement namedOwnerElement;
	private final Map<NamedElement, Set<String>> parameters;
	private final DefaultValueStack<BundleProblemSeverity> severity = new DefaultValueStack<>(ERROR);

	NamedGenerator(NamedOwnerElement namedOwnerElement) {
		this.namedOwnerElement = namedOwnerElement;
		this.problems = new HashSet<>();
		this.parameters = new HashMap<>();
	}

	void process() {
		// Make sure there is a root resource bundle and that it has all keys declared. This avoid fall back problems,
		// and allows the validation of the other locales
		{
			// The severity of assertions during this part is ERROR
			this.severity.push(ERROR);

			Optional<ResourceBundle> rootBundle = Utils.getResourceBundle(this.namedOwnerElement.bundle, Locale.ROOT);
			if (!rootBundle.isPresent()) {
				// If there is no root bundle, the process is abandoned
				this.addProblem(this.namedOwnerElement.element, "There is no bundle called {0} for the ROOT locale.",
						this.namedOwnerElement.bundle);
				return;
			}

			this.validatePresenceAndCollectParameters(this.namedOwnerElement, rootBundle.get());
			this.namedOwnerElement.innerElements.forEach(element -> this.validatePresenceAndCollectParameters(element,
					rootBundle.get()));

			// Pop the severity used
			this.severity.pop();
		}

		// With the collected parameters, validate the other available locales, checking if the configuration
		// permits it
		if (!this.namedOwnerElement.suppressWarnings) {
			Arrays.asList(Locale.getAvailableLocales()).stream().filter(l -> !l.equals(Locale.ROOT)).forEach
					(this::validateResourceBundleForLocale);
		}
	}

	private void addParameter(NamedElement element, String parameter) {
		if (!this.parameters.containsKey(element)) {
			this.parameters.put(element, new HashSet<>());
		}
		this.parameters.get(element).add(parameter);
	}

	private void addProblem(Element element, String message, Object... params) {
		this.problems.add(this.severity.peek().createBundleProblem(element, message, params));
	}

	private Set<String> getParameters(NamedElement element) {
		if (!this.parameters.containsKey(element)) {
			this.parameters.put(element, new HashSet<>());
		}
		return this.parameters.get(element);
	}

	private void validatePresenceAndCollectParameters(NamedElement element, ResourceBundle rootBundle) {
		Optional<String> string = Utils.getString(rootBundle, element.getKey());

		if (!string.isPresent()) {
			// Add the problem
			this.addProblem(element.element, "There is no string associated with the ''{0}'' key for the ROOT locale " +
					"on the ''{1}'' bundle", element.getKey(), rootBundle.getBaseBundleName());
			return;
		}

		// Collect the parameters
		Utils.getParameters(string.get()).forEach(parameter -> this.addParameter(element, parameter));
	}

	private void validatePresenceAndParameters(NamedElement element, ResourceBundle bundle) {
		// Check the string
		Optional<String> string = Utils.getString(bundle, element.getKey());
		if (!string.isPresent()) {
			// Add the problem
			this.addProblem(element.element, "There is no string associated with the ''{0}'' key for the ''{1}'' " +
					"locale on the ''{2}'' bundle", element.getKey(), bundle.getLocale(), bundle.getBaseBundleName());
			return;
		}

		// Check the parameters
		if (!this.getParameters(element).equals(Utils.getParameters(string.get()))) {
			// Add the problem
			this.addProblem(element.element, "The parameters used in the string of the ''{0}'' key for the ''{1}'' " +
					"locale on the ''{2}'' " + "bundle, is different from the ROOT bundle", element.getKey(), bundle
					.getLocale(), bundle.getBaseBundleName());
		}
	}

	private void validateResourceBundleForLocale(Locale locale) {
		// The severity of assertions during this part is WARNING, because there is the fall back on the default bundle
		this.severity.push(WARNING);

		Optional<ResourceBundle> bundle = Utils.getResourceBundle(this.namedOwnerElement.bundle, locale);
		if (bundle.isPresent()) {
			this.validatePresenceAndParameters(this.namedOwnerElement, bundle.get());
			this.namedOwnerElement.innerElements.forEach(element -> this.validatePresenceAndParameters(element, bundle
					.get()));
		}

		this.severity.pop();
	}

}
