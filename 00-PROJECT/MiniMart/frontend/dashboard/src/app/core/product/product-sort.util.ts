/**
 * Normalize sort query param into an array form.
 *
 * Router query param `sort` can be:
 * - undefined           → no sorting
 * - string              → single sort (e.g. "name,asc")
 * - string[]            → multiple sorts (e.g. ["name,asc", "price,desc"])
 *
 * This helper always returns a string array for easier processing.
 */
export function parseSortParams(sort: string | string[] | undefined): string[] {
  if (!sort) return [];
  return Array.isArray(sort) ? sort : [sort];
}

/**
 * Append a sort condition using "append strategy" for multi-sort UX.
 *
 * Behavior:
 * - If the column is not sorted yet → append to the end.
 * - If the column already exists   → remove old entry and append to the end
 *   (latest user action has highest priority).
 *
 * This matches common enterprise table behavior.
 *
 * @param sorts Current sort list (e.g. ["name,asc", "price,desc"])
 * @param col   Column key to sort by
 * @param dir   Sort direction ("asc" | "desc")
 * @returns     New sort list with updated priority
 */
export function appendSort(sorts: string[], col: string, dir: string): string[] {
  const filtered = sorts.filter((s) => s.split(',')[0] !== col);
  return [...filtered, `${col},${dir}`];
}

export function removeSort(sorts: string[], col: string): string[] {
  return sorts.filter((s) => s.split(',')[0] !== col);
}
