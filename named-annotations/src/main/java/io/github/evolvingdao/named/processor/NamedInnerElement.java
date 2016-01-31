package io.github.evolvingdao.named.processor;

import io.github.evolvingdao.named.annotation.DifferentKey;
import io.github.evolvingdao.named.annotation.NotNamed;

import javax.lang.model.element.Element;

final class NamedInnerElement extends NamedElement {

	private final NamedOwnerElement owner;

	NamedInnerElement(Element element, DifferentKey differentKeyAnnotation, NamedOwnerElement owner)
			throws NotNamedInnerElementException, NamedElementProblem {
		super(element, differentKeyAnnotation == null ? element.getSimpleName().toString() : differentKeyAnnotation
				.value());

		NotNamed notNamedAnnotation = element.getAnnotation(NotNamed.class);

		// If the element has the two annotation, it is an error
		if ((notNamedAnnotation != null) && (differentKeyAnnotation != null)) {
			throw new BothDifferentKeyAndNotNamedAnnotatedElementProblem(element);
		}

		// Check if the element is named
		if (notNamedAnnotation != null) {
			// Skip the current element
			throw new NotNamedInnerElementException();
		}

		this.owner = owner;
	}

	@Override
	String getKey() {
		return this.owner.getKey() + "." + this.key;
	}

}
