# Public Friendly URL Mapping Disabling Pre-Check — Interactive Mockup

Interactive mockup for **[LPD-84598](https://liferay.atlassian.net/browse/LPD-84598)** — *Public Friendly URL Mapping Disabling Pre-Check*, under epic **[LPD-41745](https://liferay.atlassian.net/browse/LPD-41745)** — *Option to remove "/web" fragment from Friendly URLs*.

The pre-check lives on **Control Panel → Server Administration → Resources** as a new section alongside System Actions and Cache Actions, and as a Gogo shell command (`friendlyurl:precheck`) for ops automation. Both surfaces share the same engine and report.

## View the mockup

GitHub doesn't render HTML inline. Open these `raw.githack.com` links to see the live mockup:

- **[Server Administration → Resources (AC2)](https://raw.githack.com/lawrence-lee/liferay-portal/LPD-84598-pre-enablement-health-check-mockup/mockups/LPD-84598-cleanurl-health-check/index.html)** — single page; click *Execute* to run the pre-check, or use the small state toggle at the top of the page to preview the clean / issues result directly.
- **[Gogo Shell — `friendlyurl:precheck` (AC1)](https://raw.githack.com/lawrence-lee/liferay-portal/LPD-84598-pre-enablement-health-check-mockup/mockups/LPD-84598-cleanurl-health-check/gogo-shell.html)** — terminal mockup with toggles for human-readable / JSON and clean / issues scenarios.

## Acceptance criteria coverage

| AC | Requirement | Where it shows up |
| --- | --- | --- |
| **AC1** | Gogo shell command `friendlyurl:precheck` | `gogo-shell.html` |
| **AC2** | Server Administration → Resources section | `index.html` |
| ~~AC3~~ | ~~Site-system reserved path conflicts~~ | Out of scope — already validated at site creation |
| ~~AC4~~ | ~~Site-site duplicate friendly URLs~~ | Out of scope — already validated at site creation |
| **AC5** | Page-site ambiguity | `index.html` → *Issues* state, "Page-Site Ambiguity" |
| **AC6** | Hardcoded `/web/` in templates / ADTs | `index.html` → *Issues* state, "Hardcoded /web/ References" |
| **AC7** | Clean report when no issues | `index.html` → *Clean* state |
| Edge | Progress for very large instances | `index.html` → *Running* state (inline progress card) |
| Tech | JSON output for automation | `gogo-shell.html` → JSON toggle |

## Open questions for the dev team

1. **Virtual hosts (AC5 note).** Sites with virtual host configurations also serve pages at root paths on their hostname and have the same ambiguity. Fold into AC5 or follow-up?
2. **Section naming.** Mockup shows the new section as "PUBLIC FRIENDLY URL MAPPING DISABLING PRE-CHECK". Long for an all-caps section header — happy to shorten if preferred.
3. **Section placement.** Mockup places it as a third section on Resources, alongside *System Actions* and *Cache Actions*. Confirm that's the intended slot.
4. **Pre-flight gate.** Should the pre-check be auto-run (read-only) before the admin flips `layout.friendly.url.public.servlet.mapping.enabled`, with a confirmation dialog?
5. **Acknowledged findings.** For repeated CI/CD runs, a "Mark as acknowledged" toggle would let teams suppress known cases. Out of scope today; worth confirming as a follow-up.
6. **Snippet length.** How many lines of context around each `/web/` hit should appear in the report? Mockup shows ±2 lines.
7. **Exit code.** Should `friendlyurl:precheck` exit non-zero when issues are found, so it can gate CI/CD pre-deploy steps?
8. **Flag syntax.** `--json` vs. `-f json` vs. `--format json` for the machine-readable output.
9. **Command name.** `friendlyurl:precheck` is a strawman per the ticket's "e.g." marker. Open to alternatives.

## File layout

```
mockups/LPD-84598-cleanurl-health-check/
├── README.md           <- you are here
├── styles.css          <- shared stylesheet (Server Administration look)
├── index.html          <- AC2 — single page covering all four states (initial / running / clean / issues), driven by JS
└── gogo-shell.html     <- AC1, with scenario + format toggles
```

## Notes

- Pure static HTML/CSS + a sprinkle of vanilla JS for the running-state progress simulation and the state toggles. No build step, no dependencies.
- Visual style matches the Server Administration → Resources page (tabs, sections with chevrons, action rows with Execute buttons). Final UI components should come from the Clay library when implemented.
- Sample data (1,000 sites, 312 templates, 3 findings) is invented for the demo. The structure of findings — `pageSiteAmbiguity[]` and `hardcodedWebReferences[]` — is a strawman for the JSON schema.
- The directory name still uses `cleanurl-health-check` from the original draft. Renaming the directory would invalidate any links already shared, so left as-is.
