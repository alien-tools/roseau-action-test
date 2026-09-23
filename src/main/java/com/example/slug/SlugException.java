package com.example.slug;

/**
 * Thrown when a slug cannot satisfy the requested constraints.
 */
public class SlugException extends Exception {
	public SlugException(String message) {
		super(message);
	}
}
