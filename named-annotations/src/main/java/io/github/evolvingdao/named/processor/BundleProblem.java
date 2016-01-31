package io.github.evolvingdao.named.processor;

import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;
import java.text.MessageFormat;

abstract class BundleProblem extends Exception {

	private static final long serialVersionUID = -2326108573483415742L;

	final Element element;
	final String message;
	final Kind kind;

	BundleProblem(Element element, Kind kind, String message, Object... params) {
		this.element = element;
		this.kind = kind;
		this.message = MessageFormat.format(message, params);
	}

}
