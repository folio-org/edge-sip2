package org.folio.edge.sip2;

import java.util.Arrays;

public enum Sip2HandlerCommandTypes {
  LOGIN(93),
  CHECKOUT(11),
  NONE(-1);

  private int commandCode;

  Sip2HandlerCommandTypes(int commandCode){
    this.commandCode = commandCode;
  }

  public int getValue(){
    return commandCode;
  }

  public static Sip2HandlerCommandTypes from(int value) {
    return Arrays.stream(values())
      .filter(commandCode ->  commandCode.getValue() == value)
      .findFirst()
      .orElse(NONE);
  }
}
