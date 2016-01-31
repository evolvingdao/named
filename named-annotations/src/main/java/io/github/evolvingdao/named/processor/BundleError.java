package io.github.evolvingdao.named.processor;

import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

final class BundleError extends BundleProblem {

	private static final long serialVersionUID = -7889440813777069313L;

	BundleError(Element element, String message, Object... params) {
		super(element, Diagnostic.Kind.ERROR, message, params);
	}

}
