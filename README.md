# tiny-slug

Turn arbitrary text into URL-friendly slugs.

```java
Slugs.slugify("Hello, World!");                                  // "hello-world"
Slugs.slugify("Crème brûlée", 5);                                // "creme"
Slugs.slugify("The quick fox", SlugFilter.stopWords("the"));      // "quick-fox"
```

## API compatibility

Every pull request is checked for breaking changes with [Roseau](https://github.com/alien-tools/roseau)
through [roseau-action](https://github.com/alien-tools/roseau-action), see [`.github/workflows/api.yml`](.github/workflows/api.yml):

- the supported API is everything public, except `com.example.slug.internal` and `@Experimental` symbols ([`roseau.yaml`](roseau.yaml)),
- intentional breaking changes are listed in [`.roseau/accepted-breaks.csv`](.roseau/accepted-breaks.csv).

See [TESTING.md](TESTING.md) for the scenarios used to exercise the action.
