package com.example.slug;

import com.example.slug.internal.Ascii;
import org.jspecify.annotations.Nullable;

/**
 * Turns arbitrary text into URL-friendly slugs.
 *
 * <pre>{@code
 * Slugs.slugify("Hello, World!");     // "hello-world"
 * Slugs.slugify("Crème brûlée", 5);   // "creme"
 * }</pre>
 */
public final class Slugs {
	private Slugs() {
	}

	/**
	 * Returns the slug of {@code input}, or an empty string if {@code input} is {@code null}.
	 */
	public static String slugify(@Nullable String input) {
		return slugify(input, SlugFilter.identity());
	}

	/**
	 * Returns the slug of {@code input}, truncated to at most {@code maxLength} characters.
	 *
	 * @throws SlugException if {@code maxLength} is negative
	 */
	public static String slugify(@Nullable String input, int maxLength) throws SlugException {
		if (maxLength < 0) {
			throw new SlugException("maxLength must be >= 0");
		}
		String slug = slugify(input);
		if (slug.length() <= maxLength) {
			return slug;
		}
		String truncated = slug.substring(0, maxLength);
		return truncated.endsWith("-") ? truncated.substring(0, truncated.length() - 1) : truncated;
	}

	/**
	 * Returns the slug of {@code input}, after applying {@code filter} to each word.
	 */
	public static String slugify(@Nullable String input, SlugFilter filter) {
		if (input == null) {
			return "";
		}
		StringBuilder slug = new StringBuilder();
		for (String word : Ascii.fold(input).toLowerCase().split("[^a-z0-9]+")) {
			String filtered = filter.apply(word);
			if (!filtered.isEmpty()) {
				if (!slug.isEmpty()) {
					slug.append('-');
				}
				slug.append(filtered);
			}
		}
		return slug.toString();
	}

	/**
	 * Returns a slug for {@code input} that keeps emoji as their names. Not stable yet.
	 */
	@Experimental
	public static String slugifyWithEmoji(@Nullable String input) {
		return slugify(input == null ? null : input.replace("❤", " heart "));
	}
}
