package org.luncert.objectmocker.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.luncert.objectmocker.ObjectMocker;

import java.sql.Date;

@RunWith(JUnit4.class)
public class RealObjectMockContextTest {
  
  @Test
  public void testRegister() {
    ObjectMocker.context()
        .register(ObjectGeneratorBuilder.noFieldScanningGenerator(Date.class,
            () -> new Date(System.currentTimeMillis())))
        .create();
  }
}
