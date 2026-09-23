package com.example.slug;

/**
 * Transforms each word of a slug; returning an empty string drops the word.
 */
@FunctionalInterface
public interface SlugFilter {
	String apply(String word);

	static SlugFilter identity() {
		return word -> word;
	}

	static SlugFilter stopWords(String... stopWords) {
		java.util.Set<String> words = java.util.Set.of(stopWords);
		return word -> words.contains(word) ? "" : word;
	}
}
