package io.github.evolvingdao.named.processor;

import javax.lang.model.element.Element;

import io.github.evolvingdao.named.annotation.DifferentKey;
import io.github.evolvingdao.named.annotation.NotNamed;

final class BothDifferentKeyAndNotNamedAnnotatedElementProblem extends NamedElementProblem {

	private static final long serialVersionUID = -7536556052931335121L;

	BothDifferentKeyAndNotNamedAnnotatedElementProblem(Element element) {
		super(element, "It is not allowed for an element to be both annotated with @{0} and @{1}.",
				DifferentKey.class.getSimpleName(), NotNamed.class.getSimpleName());
	}

}
