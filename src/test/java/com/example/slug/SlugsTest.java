package com.example.slug;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SlugsTest {
	@Test
	void slugify() {
		assertEquals("hello-world", Slugs.slugify("Hello, World!"));
		assertEquals("creme-brulee", Slugs.slugify("Crème brûlée"));
		assertEquals("", Slugs.slugify(null));
	}

	@Test
	void slugifyWithMaxLength() throws SlugException {
		assertEquals("creme", Slugs.slugify("Crème brûlée", 5));
		assertEquals("creme", Slugs.slugify("Crème brûlée", 6));
	}

	@Test
	void slugifyWithFilter() {
		assertEquals("quick-fox", Slugs.slugify("The quick fox", SlugFilter.dropping("the")));
	}
}
