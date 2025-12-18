export function isExired(expMs: number, thresholdMs: number): boolean {
  return expMs <= Date.now() + thresholdMs;
}
