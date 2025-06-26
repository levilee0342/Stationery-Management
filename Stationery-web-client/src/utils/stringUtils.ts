/**
 * Converts Vietnamese characters with diacritics to their non-diacritic equivalents
 * @param str - The string to convert
 * @returns The converted string without diacritics
 */
export function removeVietnameseDiacritics(str: string): string {
  if (!str) return ''

  return str
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .replace(/đ/g, 'd')
    .replace(/Đ/g, 'D')
}

export function convertToSlug(str: string): string {
  if (!str) return ''

  // Remove Vietnamese diacritics first
  const withoutDiacritics = removeVietnameseDiacritics(str)

  return withoutDiacritics
    .toLowerCase()
    .trim()
    .replace(/\s+/g, '-')
    .replace(/[^a-z0-9-]/g, '')
    .replace(/-+/g, '-')
}
