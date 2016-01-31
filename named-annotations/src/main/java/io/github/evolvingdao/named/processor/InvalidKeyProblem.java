package io.github.evolvingdao.named.processor;

import javax.lang.model.element.Element;

final class InvalidKeyProblem extends NamedElementProblem {

	private static final long serialVersionUID = 8720193417633772684L;

	InvalidKeyProblem(Element element, String key) {
		super(element, "''{0}'' is not a valid value for a named element.", key);
	}

}
