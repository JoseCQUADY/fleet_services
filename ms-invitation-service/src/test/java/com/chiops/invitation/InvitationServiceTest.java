package com.chiops.invitation;

import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@MicronautTest
class InvitationServiceTest {

  @Inject EmbeddedApplication<?> application;

  @Test
  void testItWorks() {
    Assertions.assertTrue(true);
  }
}
