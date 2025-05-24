package com.chiops.administrator;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.Status;
import java.lang.String;
import java.lang.Throwable;

public class StaticLogbackConfiguration implements Configurator {
  private Context context;

  public Configurator.ExecutionStatus configure(LoggerContext loggerContext) {
    ConsoleAppender stdout = new ConsoleAppender();
    PatternLayoutEncoder encoder = new PatternLayoutEncoder();
    encoder.setPattern("%cyan(%d{HH:mm:ss.SSS}) %gray([%thread]) %highlight(%-5level) %magenta(%logger{36}) - %msg%n");
    encoder.setContext(context);
    encoder.start();
    stdout.setEncoder(encoder);
    stdout.setContext(context);
    stdout.start();
    Logger _rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
    _rootLogger.setLevel(Level.INFO);
    _rootLogger.addAppender(stdout);
    return Configurator.ExecutionStatus.DO_NOT_INVOKE_NEXT_IF_ANY;
  }

  public void setContext(Context context) {
    this.context = context;
  }

  public Context getContext() {
    return context;
  }

  public void addStatus(Status status) {
  }

  public void addInfo(String info) {
  }

  public void addInfo(String info, Throwable ex) {
  }

  public void addWarn(String warn) {
  }

  public void addWarn(String warn, Throwable ex) {
  }

  public void addError(String error) {
  }

  public void addError(String error, Throwable ex) {
  }
}
