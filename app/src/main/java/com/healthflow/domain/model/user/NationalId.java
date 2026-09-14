package com.healthflow.domain.model.user;

import com.healthflow.domain.exception.InvalidNationalIdException;

import java.util.Objects;

public class NationalId {
  private final String nationalId;

  // NOT: Burada record kullanmak da oldukça mantıklı valueObjectlerde record her zaman işe yarar
  // bir yöntem olmuştur.
  public NationalId(String nationalId) {

    if(nationalId == null){
        throw new InvalidNationalIdException("National Id can not be null.");
    }

    if (nationalId.isBlank()) {
      throw new InvalidNationalIdException("National ID cannot be empty.");
    }

    if (nationalId.length() != 11) {
      throw new InvalidNationalIdException("National ID must be 11 digits");
    }

    if (!nationalId.chars().allMatch(Character::isDigit)) {
      throw new InvalidNationalIdException("National ID must contain only digits");
    }

    int lastDigit = Character.getNumericValue(nationalId.charAt(nationalId.length() - 1));

    if (lastDigit % 2 != 0) {
      throw new InvalidNationalIdException("Last digit must be even");
    }

    this.nationalId = nationalId;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    NationalId that = (NationalId) o;
    return Objects.equals(nationalId, that.nationalId);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(nationalId);
  }

  public String getValue() {
    return nationalId;
  }
}
