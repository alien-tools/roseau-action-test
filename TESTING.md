# Testing roseau-action with tiny-slug

This repository is a minimal library whose only purpose is to exercise
[roseau-action](https://github.com/alien-tools/roseau-action) on GitHub. Each `demo/*` branch holds one realistic
change. Opening a pull request for each branch covers every feature of the action.

## Workflows

| Workflow | Trigger | What it exercises |
|---|---|---|
| [`api.yml`](.github/workflows/api.yml), job **Breaking changes (sources)** | PRs, pushes to `main` | Source directories, `v1-pom`/`v2-pom`, `config`, `ignored`, extra `reports`, PR comment, inline comments, annotations, job summary, artifact upload, failing on PRs, report-only on `main`, outputs |
| [`api.yml`](.github/workflows/api.yml), job **Binary compatibility (JARs)** | PRs, pushes to `main` | JAR inputs built by Maven, `compatibility: binary`, custom `report-dir`, `upload-reports: false`, `comment: false`, `annotations: false` |
| [`compare-published.yml`](.github/workflows/compare-published.yml) | Manual | Maven coordinates, `roseau-version: latest`, `compatibility` choices, JSON report output |

On pull requests, the baseline is the PR's base commit. On `main`, it is the latest release tag (`v1.0.0`).

## Setup

1. Create an empty GitHub repository, then push `main` and the release tag first:

   ```bash
   git remote add origin git@github.com:<owner>/roseau-action-test.git
   git push -u origin main v1.0.0
   ```

   **Expect:** *API compatibility* runs on `main` and both jobs pass. The summary shows no breaking changes and
   says "the next release can be a minor or patch version".

2. Push the demo branches and open one pull request each:

   ```bash
   git push origin 'refs/heads/demo/*:refs/heads/demo/*'
   for b in add-separator remove-max-length checked-exception internal-change accepted-break annotation-member major-rework; do
     gh pr create --base main --head "demo/$b" --fill
   done
   ```

If Actions are restricted in your organization, allow `alien-tools/roseau-action` first.

## Expected results

| PR | Change | Sources | Binary | PR comment |
|---|---|---|---|---|
| `demo/add-separator` | New overload `slugify(String, char)` | ✅ | ✅ | "no breaking changes detected" |
| `demo/remove-max-length` | Removes `slugify(String, int)` | ❌ 1 | ❌ 1 | Table with `EXECUTABLE_REMOVED` |
| `demo/checked-exception` | `slugify(String, int)` now throws a checked exception | ❌ 1 | ✅ | Table with `EXECUTABLE_NOW_THROWS_CHECKED_EXCEPTION`, binary-compatible |
| `demo/internal-change` | Breaks `internal.Ascii` and an `@Experimental` method | ✅ | ✅ | "no breaking changes detected" (hidden by `roseau.yaml`) |
| `demo/accepted-break` | Renames `SlugFilter.dropping`, listed in `accepted-breaks.csv` | ✅ | ✅ | "no breaking changes detected" (hidden by `ignored`) |
| `demo/annotation-member` | Adds `since()` without default to `@Experimental` | ❌ 1 | ✅ | Table with `ANNOTATION_NEW_METHOD_WITHOUT_DEFAULT`, source-breaking only |
| `demo/major-rework` | Deletes `@Experimental`, removes `SlugFilter.identity()`, changes a return type | ❌ 4 | ❌ 3 | Table with 4 breaking changes |

Each breaking change is also marked once on the lines of the diff, in the "Files changed" tab, with an inline review
comment from `github-actions` (and no annotation, since the comment already marks it):

| PR | Marked line |
|---|---|
| `demo/remove-max-length` | The deleted `slugify(String, int)` declaration (red, old side) |
| `demo/checked-exception` | The `slugify(String, int)` declaration: old side with Roseau v0.7.0, new side once Roseau reports new locations |
| `demo/annotation-member` | The `@interface Experimental` declaration with Roseau v0.7.0; the added `since()` line once Roseau reports new locations |
| `demo/major-rework` | The deleted `Experimental.java`, the deleted `identity()`, and one grouped comment on the `slugify(String, int)` declaration |

On each PR, also check that:

- the **Summary** tab of the sources job shows the Markdown report,
- the run has one `roseau-reports` artifact with `report.json`, `report.md`, `report.html` and `report.csv`,
- each PR has exactly one Roseau comment, which ends with "N of N breaking change(s) are marked on the lines of this diff".

## Follow-up scenarios

**The comment is updated in place.** Accept the break on `demo/remove-max-length` using the CSV report from its artifact:

```bash
git switch demo/remove-max-length
gh run download --name roseau-reports --dir /tmp/roseau-reports \
  $(gh run list --branch demo/remove-max-length --workflow api.yml --limit 1 --json databaseId -q '.[0].databaseId')
tail -n +2 /tmp/roseau-reports/report.csv >> .roseau/accepted-breaks.csv
git commit -am "Accept removal of slugify(String, int)" && git push
```

**Expect:** both jobs turn green, the existing comment is edited to "no breaking changes detected" (no new comment), and
the inline comment on the removed method is deleted. Pushing a commit that does not change the breaking changes keeps
the existing inline comments instead of posting new ones.

**Report-only on `main`.** Merge `demo/add-separator`, `demo/internal-change` and `demo/accepted-break`. Each push to
`main` passes, with the notice "No breaking changes since v1.0.0". Then merge `demo/checked-exception`, even though
its check fails. **Expect:** the push to `main` still passes, but shows the warning "1 breaking change(s) since
v1.0.0: the next release must be a major version".

**Fork pull requests.** Open a PR from a fork, for example from a second account. **Expect:** the check and the job
summary still run; the comment steps only log a warning, because the fork's token cannot write comments. Instead of
inline comments, each breaking change is marked with an annotation from *API compatibility / Breaking changes
(sources)*, on the new side of the diff.

**Maven coordinates.** Run *Compare published versions* from the Actions tab (or
`gh workflow run compare-published.yml`). **Expect:** 8 breaking changes for commons-lang3 3.0 → 3.17.0 with
`all`, 3 with `binary`, and a `roseau-reports` artifact containing an HTML report. The step never fails.
