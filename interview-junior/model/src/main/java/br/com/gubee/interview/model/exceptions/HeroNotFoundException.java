package br.com.gubee.interview.model.exceptions;

import java.util.UUID;

public class HeroNotFoundException extends RuntimeException {
  public HeroNotFoundException(UUID id) {
    super("Hero not found with id: " + id);
  }
}