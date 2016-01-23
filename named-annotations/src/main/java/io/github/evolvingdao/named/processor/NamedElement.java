package io.github.evolvingdao.named.processor;

import static javax.lang.model.element.ElementKind.ENUM_CONSTANT;
import static javax.lang.model.element.ElementKind.FIELD;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;

import io.github.evolvingdao.named.annotation.Named;
import io.github.evolvingdao.named.annotation.SuppressNamedWarnings;

final class NamedElement {

	private static final List<ElementKind> VALID_INNER_KINDS = Arrays.asList(FIELD, ENUM_CONSTANT);

	final Element element;
	final String key;
	final String bundle;
	final boolean suppressWarnings;
	final Set<NamedInnerElement> innerElements;
	final Set<NamedElementProblem> innerElementsProblems;

	NamedElement(Element element) throws InvalidKeyProblem {
		// Obtain the possible annotations
		Named namedAnnotation = element.getAnnotation(Named.class);
		SuppressNamedWarnings suppressWarningsAnnotation = element.getAnnotation(SuppressNamedWarnings.class);

		// Validate the key
		String key = Named.DEFAULT_KEY.equals(namedAnnotation.key()) ? element.getSimpleName().toString()
				: namedAnnotation.key();
		if (!SourceVersion.isName(key)) {
			throw new InvalidKeyProblem(element, key);
		}

		this.key = key;
		this.element = element;
		this.bundle = Named.DEFAULT_BUNDLE.equals(namedAnnotation.bundle())
				? getPackageName(element) + namedAnnotation.bundle() : namedAnnotation.bundle();
		this.suppressWarnings = suppressWarningsAnnotation == null ? false : true;
		this.innerElements = new HashSet<>();
		this.innerElementsProblems = new HashSet<>();

		this.initializeInnerElements();
	}

	private void initializeInnerElements() {
		for (Element innerElement : this.element.getEnclosedElements()) {
			try {
				if (VALID_INNER_KINDS.contains(innerElement.getKind())) {
					this.innerElements.add(new NamedInnerElement(innerElement));
				}
			} catch (NotNamedInnerElementException e) {
				// Simply skip this element since it is not a named one
			} catch (NamedElementProblem e) {
				// Add the found problem to the set of problems
				this.innerElementsProblems.add(e);
			}
		}
	}

	private static final String getPackageName(Element element) {
		Element e = element;
		while ((e != null) && (e.getKind() != ElementKind.PACKAGE)) {
			e = e.getEnclosingElement();
		}
		PackageElement packageElement = (PackageElement) e;
		return (packageElement == null) || (packageElement.getQualifiedName().length() == 0) ? ""
				: packageElement.getQualifiedName().toString() + ".";
	}

}
