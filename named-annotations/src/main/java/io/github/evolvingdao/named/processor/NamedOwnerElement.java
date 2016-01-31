package io.github.evolvingdao.named.processor;

import io.github.evolvingdao.named.annotation.DifferentKey;
import io.github.evolvingdao.named.annotation.Named;
import io.github.evolvingdao.named.annotation.SuppressNamedWarnings;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static javax.lang.model.element.ElementKind.ENUM_CONSTANT;
import static javax.lang.model.element.ElementKind.FIELD;

final class NamedOwnerElement extends NamedElement {

	private static final List<ElementKind> VALID_INNER_KINDS = Arrays.asList(FIELD, ENUM_CONSTANT);

	final String bundle;
	final boolean suppressWarnings;
	final Set<NamedInnerElement> innerElements;
	final Set<NamedElementProblem> innerElementsProblems;

	NamedOwnerElement(Element element, Named namedAnnotation) throws InvalidKeyProblem {
		super(element, Named.DEFAULT_KEY.equals(namedAnnotation.key()) ? element.getSimpleName().toString()
				: namedAnnotation.key());

		this.bundle = Named.DEFAULT_BUNDLE.equals(namedAnnotation.bundle())
				? getPackageName(element) + namedAnnotation.bundle() : namedAnnotation.bundle();
		this.suppressWarnings = element.getAnnotation(SuppressNamedWarnings.class) != null;
		this.innerElements = new HashSet<>();
		this.innerElementsProblems = new HashSet<>();

		this.initializeInnerElements();
	}

	private static String getPackageName(Element element) {
		Element e = element;
		while ((e != null) && (e.getKind() != ElementKind.PACKAGE)) {
			e = e.getEnclosingElement();
		}
		PackageElement packageElement = (PackageElement) e;
		return (packageElement == null) || (packageElement.getQualifiedName().length() == 0) ? ""
				: packageElement.getQualifiedName().toString() + ".";
	}

	private static boolean validInnerKind(Element e) {
		return VALID_INNER_KINDS.contains(e.getKind());
	}

	@Override
	String getKey() {
		return key;
	}

	private void initializeInnerElement(Element innerElement) {
		try {
			this.innerElements
					.add(new NamedInnerElement(innerElement, innerElement.getAnnotation(DifferentKey.class), this));
		} catch (NotNamedInnerElementException e) {
			// Simply skip this element since it is not a named one
		} catch (NamedElementProblem e) {
			// Add the found problem to the set of problems
			this.innerElementsProblems.add(e);
		}
	}

	private void initializeInnerElements() {
		this.element.getEnclosedElements().stream().filter(NamedOwnerElement::validInnerKind)
				.forEach(this::initializeInnerElement);
	}

}
