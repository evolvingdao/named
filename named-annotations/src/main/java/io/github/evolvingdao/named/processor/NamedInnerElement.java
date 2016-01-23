package io.github.evolvingdao.named.processor;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;

import io.github.evolvingdao.named.annotation.DifferentKey;
import io.github.evolvingdao.named.annotation.NotNamed;

final class NamedInnerElement {

	final Element element;
	final String key;

	NamedInnerElement(Element element) throws NotNamedInnerElementException, NamedElementProblem {
		// Obtain the possible annotations
		NotNamed notNamedAnnotation = element.getAnnotation(NotNamed.class);
		DifferentKey differentKeyAnnotation = element.getAnnotation(DifferentKey.class);

		// If the element has the two annotation, it is an error
		if ((notNamedAnnotation != null) && (differentKeyAnnotation != null)) {
			throw new BothDifferentKeyAndNotNamedAnnotatedElementProblem(element);
		}

		// Check if the element is named
		if (notNamedAnnotation != null) {
			// Skip the current element
			throw new NotNamedInnerElementException();
		}

		// Validate the key
		String key = differentKeyAnnotation == null ? element.getSimpleName().toString() : differentKeyAnnotation.key();
		if (!SourceVersion.isName(key)) {
			throw new InvalidKeyProblem(element, key);
		}

		this.element = element;
		this.key = key;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		NamedInnerElement other = (NamedInnerElement) obj;
		if (this.element == null) {
			if (other.element != null) {
				return false;
			}
		} else if (!this.element.equals(other.element)) {
			return false;
		}
		if (this.key == null) {
			if (other.key != null) {
				return false;
			}
		} else if (!this.key.equals(other.key)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.element == null) ? 0 : this.element.hashCode());
		result = (prime * result) + ((this.key == null) ? 0 : this.key.hashCode());
		return result;
	}

}
