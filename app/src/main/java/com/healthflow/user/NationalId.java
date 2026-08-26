package com.healthflow.user;

public class NationalId {
  private String nationalId;

  // NOT: Burada record kullanmak da oldukça mantıklı valueObjectlerde record her zaman işe yarar
  // bir yöntem olmuştur.
  public NationalId(String nationalId) {
    if (nationalId == null) {
      throw new IllegalArgumentException("National ID cannot be null");
    }

    if (nationalId.length() != 11) {
      throw new IllegalArgumentException("National ID must be 11 digits");
    }

    if (!nationalId.chars().allMatch(Character::isDigit)) {
      throw new IllegalArgumentException("National ID must contain only digits");
    }

    int lastDigit = Character.getNumericValue(nationalId.charAt(nationalId.length() - 1));

    if (lastDigit % 2 != 0) {
      throw new IllegalArgumentException("Last digit must be even");
    }

    this.nationalId = nationalId;
  }

  public String getValue() {
    return nationalId;
  }
}
