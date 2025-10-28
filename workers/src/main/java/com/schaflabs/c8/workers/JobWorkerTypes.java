package com.schaflabs.c8.workers;

public class JobWorkerTypes {
  private static final boolean DEBUG_MODE = false;
  private static final String DEBUG_POSTFIX = "_debug";

  private static final String CREATE_EXAMPLE_OBJECT_STRING = "jobworkertype_create_example_object";
  public static final String CREATE_EXAMPLE_OBJECT =
      DEBUG_MODE ? CREATE_EXAMPLE_OBJECT_STRING + DEBUG_POSTFIX : CREATE_EXAMPLE_OBJECT_STRING;
}
