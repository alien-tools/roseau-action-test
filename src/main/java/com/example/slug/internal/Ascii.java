package com.example.slug.internal;

import java.text.Normalizer;

/**
 * Implementation detail; not part of the supported API.
 */
public final class Ascii {
	private Ascii() {
	}

	public static String foldToAscii(String input) {
		return Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
	}
}
