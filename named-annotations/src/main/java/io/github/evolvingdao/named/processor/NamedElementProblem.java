package io.github.evolvingdao.named.processor;

import javax.lang.model.element.Element;
import java.text.MessageFormat;

abstract class NamedElementProblem extends Exception {

	private static final long serialVersionUID = 409967179035252007L;

	final Element element;
	final String message;

	NamedElementProblem(Element element, String message, Object... params) {
		this.element = element;
		this.message = MessageFormat.format(message, params);
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
		NamedElementProblem other = (NamedElementProblem) obj;
		if (this.element == null) {
			if (other.element != null) {
				return false;
			}
		} else if (!this.element.equals(other.element)) {
			return false;
		}
		if (this.message == null) {
			if (other.message != null) {
				return false;
			}
		} else if (!this.message.equals(other.message)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.element == null) ? 0 : this.element.hashCode());
		result = (prime * result) + ((this.message == null) ? 0 : this.message.hashCode());
		return result;
	}

}
