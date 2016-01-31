package io.github.evolvingdao.named.processor;

import java.util.Stack;

class DefaultValueStack<E> extends Stack<E> {

	private static final long serialVersionUID = 5403461486488222267L;

	private final E defaultValue;

	DefaultValueStack(E defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	public synchronized E peek() {
		return empty() ? this.defaultValue : super.peek();
	}

}
