package io.github.evolvingdao.named.processor;

import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;

final class BundleWarning extends BundleProblem {

	private static final long serialVersionUID = 3731588512972466208L;

	BundleWarning(Element element, String message, Object... params) {
		super(element, Kind.WARNING, message, params);
	}

}
