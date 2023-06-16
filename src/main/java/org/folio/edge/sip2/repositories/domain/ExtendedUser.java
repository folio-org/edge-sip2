package org.folio.edge.sip2.repositories.domain;

public class ExtendedUser {
  private User user;
  private PatronGroup patronGroup;

  public User getUser() {
    return user;
  }

  public PatronGroup getPatronGroup() {
    return patronGroup;
  }

  public void setUser(User user) {
    this.user = user;
  }

  /**
   * Creates a PatronGroup object from the three component strings.
   * @param group The group name
   * @param desc The group description
   * @param id The group id
   */
  public void setPatronGroup(String group, String desc, String id) {
    PatronGroup newPatronGroup = new PatronGroup(group, desc, id);
    this.patronGroup = newPatronGroup;
  }

  public class PatronGroup {
    private String group;
    private String desc;
    private String id;

    /**
     * Constructor.
     * @param group The group name
     * @param desc The group description
     * @param id The group id
     */
    public PatronGroup(String group, String desc, String id) {
      this.group = group;
      this.desc = desc;
      this.id = id;
    }

    public String getGroup() {
      return group;
    }

    public String getDesc() {
      return desc;
    }

    public String getId() {
      return id;
    }
  }

}
