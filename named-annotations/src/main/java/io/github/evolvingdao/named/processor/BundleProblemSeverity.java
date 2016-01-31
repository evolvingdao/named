package io.github.evolvingdao.named.processor;

import javax.lang.model.element.Element;

enum BundleProblemSeverity {

	ERROR {

		@Override
		BundleError createBundleProblem(Element element, String message, Object... params) {
			return new BundleError(element, message, params);
		}

	},

	WARNING {

		@Override
		BundleWarning createBundleProblem(Element element, String message, Object... params) {
			return new BundleWarning(element, message, params);
		}

	};

	abstract BundleProblem createBundleProblem(Element element, String message, Object... params);

}
