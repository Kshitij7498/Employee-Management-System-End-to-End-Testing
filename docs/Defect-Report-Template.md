# Jira-Style Defect Report Template (manual use)

| Field | Value |
|---|---|
| Project | EMS (Employee Management System) |
| Issue Type | Bug |
| Summary | `[Module] Short, specific description of the problem` |
| Priority | Highest / High / Medium / Low |
| Severity | Critical / Major / Moderate / Minor |
| Status | Open |
| Component | Login / Registration / Employee Management / Search / Role Access |
| Affects Version | 1.0.0 |
| Environment | Browser + version / OS / build |
| Reporter | |
| Assignee | |
| Labels | regression, negative, ui, security |
| Linked Test Case | TC_XXX_000 |

## Description
One or two sentences describing the problem and its business impact.

## Steps to Reproduce
1.
2.
3.

## Expected Result

## Actual Result

## Attachments
Screenshot / screen recording / console log.

## Priority vs Severity guide
| Severity | Meaning | Example |
|---|---|---|
| Critical | Blocks a core flow, no workaround | Admin cannot log in |
| Major | Important function broken, workaround exists | HR can delete employees (RBAC breach) |
| Moderate | Function partly wrong | Search is case-sensitive |
| Minor | Cosmetic / low impact | Label typo |
