package io.github.evolvingdao.named.processor;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

import io.github.evolvingdao.named.annotation.DifferentKey;
import io.github.evolvingdao.named.annotation.Named;
import io.github.evolvingdao.named.annotation.NotNamed;
import io.github.evolvingdao.named.annotation.SuppressNamedWarnings;

public final class NamedProcessor extends AbstractProcessor {

	private static final Set<String> SUPPORTED_ANNOTATIONS;
	private static final Predicate<Element> NOT_A_NAMED_ELEMENT;
	private static final Predicate<Element> NOT_A_NAMED_INNER_ELEMENT;

	static {
		SUPPORTED_ANNOTATIONS = new HashSet<>();
		SUPPORTED_ANNOTATIONS.add(Named.class.getName());
		SUPPORTED_ANNOTATIONS.add(NotNamed.class.getName());
		SUPPORTED_ANNOTATIONS.add(DifferentKey.class.getName());
		SUPPORTED_ANNOTATIONS.add(SuppressNamedWarnings.class.getName());

		NOT_A_NAMED_ELEMENT = e -> (e.getAnnotation(Named.class) == null);

		NOT_A_NAMED_INNER_ELEMENT = e -> {
			Element enclosingElement = e.getEnclosingElement();
			return enclosingElement == null ? true : enclosingElement.getAnnotation(Named.class) == null;
		};
	}

	private Elements elements;
	private Messager messager;
	private Types types;

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return SUPPORTED_ANNOTATIONS;
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);

		this.elements = processingEnv.getElementUtils();
		this.messager = processingEnv.getMessager();
		this.types = processingEnv.getTypeUtils();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		// There are some things to check and mark the errors:
		// 1. NotNamed or DifferentKey annotated elements must belong to a Named annotated class.
		// 2. SuppressNamedWarnings annotated elements must have the Named annotation too.
		{
			roundEnv.getElementsAnnotatedWith(NotNamed.class).stream().filter(NOT_A_NAMED_INNER_ELEMENT)
					.forEach(this::errorNotANamedInnerElement);
			roundEnv.getElementsAnnotatedWith(DifferentKey.class).stream().filter(NOT_A_NAMED_INNER_ELEMENT)
					.forEach(this::errorNotANamedInnerElement);
			roundEnv.getElementsAnnotatedWith(SuppressNamedWarnings.class).stream().filter(NOT_A_NAMED_ELEMENT)
					.forEach(this::errorNotANamedElement);
		}

		// After finding all the elements with error, start to process the resource files, creating warnings/errors
		// when problems are found
		roundEnv.getElementsAnnotatedWith(Named.class).stream().forEach(this::processNamedElement);

		return true;
	}

	private void error(Element element, String message) {
		this.messager.printMessage(Kind.ERROR, message, element);
	}

	private void error(Element element, String message, Object... params) {
		this.error(element, MessageFormat.format(message, params));
	}

	private void error(NamedElementProblem problem) {
		this.error(problem.element, problem.message);
	}

	private void errorNotANamedElement(Element element) {
		this.error(element, "It is not an element annotated with @{0}.", Named.class.getSimpleName());
	}

	private void errorNotANamedInnerElement(Element element) {
		this.error(element, "It is not an inner element of a type annotated with @{0}.", Named.class.getSimpleName());
	}

	private void processNamedElement(Element element) {
		try {
			// Obtain the named element
			NamedElement namedElement = new NamedElement(element);

			// Register the problems associated with the inner elements
			namedElement.innerElementsProblems.forEach(this::error);
		} catch (InvalidKeyProblem e) {
			this.error(e);
		}
	}

}
