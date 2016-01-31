package io.github.evolvingdao.named.processor;

import javax.lang.model.element.Element;
import java.util.Objects;

import static io.github.evolvingdao.named.processor.Utils.isAValidKey;

abstract class NamedElement {

	final Element element;
	final String key;

	NamedElement(Element element, String key) throws InvalidKeyProblem {
		if (!isAValidKey(key)) {
			throw new InvalidKeyProblem(element, key);
		}

		this.element = element;
		this.key = key;
	}

	@Override
	public int hashCode() {
		return Objects.hash(element, key);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		NamedElement that = (NamedElement) o;
		return Objects.equals(element, that.element) && Objects.equals(key, that.key);
	}

	abstract String getKey();

}
