/*
 * Copyright (C) 2003-2019 eXo Platform SAS.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.exoplatform.social.core.jpa.storage;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.social.core.binding.model.GroupSpaceBinding;
import org.exoplatform.social.core.binding.model.UserSpaceBinding;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.jpa.test.AbstractCoreTest;
import org.exoplatform.social.core.space.impl.DefaultSpaceApplicationHandler;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.storage.api.GroupSpaceBindingStorage;
import org.exoplatform.social.core.storage.api.IdentityStorage;
import org.exoplatform.social.core.storage.api.SpaceStorage;
import org.exoplatform.social.core.storage.impl.StorageUtils;

/**
 * Unit Tests for
 * {@link org.exoplatform.social.core.storage.api.GroupSpaceBindingStorage}
 */

public class RDBMSGroupSpaceBindingStorageTest extends AbstractCoreTest {

  private List<GroupSpaceBinding>  tearDownGroupbindingList = new ArrayList<>();

  private List<UserSpaceBinding>   tearDownUserbindingList  = new ArrayList<>();

  private SpaceStorage             spaceStorage;

  private IdentityStorage          identityStorage;

  private GroupSpaceBindingStorage groupSpaceBindingStorage;

  private Identity                 demo;

  private Identity                 mary;

  private Identity                 jame;

  private Identity                 root;

  private Identity                 john;

  private String                   spaceId;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    spaceStorage = this.getContainer().getComponentInstanceOfType(SpaceStorage.class);
    identityStorage = this.getContainer().getComponentInstanceOfType(IdentityStorage.class);
    groupSpaceBindingStorage = this.getContainer().getComponentInstanceOfType(GroupSpaceBindingStorage.class);

    root = new Identity("organization", "root");
    john = new Identity("organization", "john");
    demo = new Identity("organization", "demo");
    mary = new Identity("organization", "mary");
    jame = new Identity("organization", "jame");

    identityStorage.saveIdentity(root);
    identityStorage.saveIdentity(john);
    identityStorage.saveIdentity(demo);
    identityStorage.saveIdentity(mary);
    identityStorage.saveIdentity(jame);

    Space space = this.getSpaceInstance(1);
    spaceStorage.saveSpace(space, true);
    StorageUtils.persist();
    spaceId = spaceStorage.getSpaceByPrettyName("myspacetestbinding1").getId();
  }

  /**
   * Cleans up.
   */
  @Override
  protected void tearDown() throws Exception {
    deleteAllBindings();
    super.tearDown();
  }

  protected void deleteAllBindings() {
    for (UserSpaceBinding binding : tearDownUserbindingList) {
      groupSpaceBindingStorage.deleteUserBinding(binding.getId());
      StorageUtils.persist();
    }
    for (GroupSpaceBinding binding : tearDownGroupbindingList) {
      groupSpaceBindingStorage.deleteGroupBinding(binding.getId());
      StorageUtils.persist();
    }
    tearDownGroupbindingList = new ArrayList<>();
    tearDownUserbindingList = new ArrayList<>();
  }

  /**
   * Gets an instance of Space.
   *
   * @param number
   * @return an instance of space
   */
  private Space getSpaceInstance(int number) {
    Space space = new Space();
    space.setApp("app1,app2");
    space.setDisplayName("myspacetestbinding" + number);
    space.setPrettyName(space.getDisplayName());
    space.setRegistration(Space.OPEN);
    space.setDescription("add new space " + number);
    space.setType(DefaultSpaceApplicationHandler.NAME);
    space.setVisibility(Space.PUBLIC);
    space.setPriority(Space.INTERMEDIATE_PRIORITY);
    space.setGroupId("/spaces/space" + number);
    String[] managers = new String[] { "demo" };
    String[] members = new String[] { "john", "root" };
    String[] invitedUsers = new String[] { "mary" };
    String[] pendingUsers = new String[] { "jame" };
    space.setInvitedUsers(invitedUsers);
    space.setPendingUsers(pendingUsers);
    space.setManagers(managers);
    space.setMembers(members);
    space.setUrl(space.getPrettyName());
    return space;
  }

  /**
   * Gets an instance of GroupSpaceBinding.
   *
   * @param id
   * @return an instance of space
   **/
  private GroupSpaceBinding getGroupSpaceBindingInstance(long id,
                                                         String spaceId,
                                                         String spaceRole,
                                                         String group,
                                                         String groupRole) {
    GroupSpaceBinding groupSpaceBinding = new GroupSpaceBinding();
    groupSpaceBinding.setId(id);
    groupSpaceBinding.setSpaceId(spaceId);
    groupSpaceBinding.setSpaceRole(spaceRole);
    groupSpaceBinding.setGroup(group);
    groupSpaceBinding.setGroupRole(groupRole);
    return groupSpaceBinding;
  }

  /**
   * Gets an instance of UserBinding.
   *
   * @param id
   * @return an instance of space
   **/
  private UserSpaceBinding getUserBindingInstance(long id, String userName, String spaceId, GroupSpaceBinding groupSpaceBinding) {
    UserSpaceBinding userSpaceBinding = new UserSpaceBinding();
    userSpaceBinding.setId(id);
    userSpaceBinding.setUser(userName);
    userSpaceBinding.setSpaceId(spaceId);
    userSpaceBinding.setGroupBinding(groupSpaceBinding);
    return userSpaceBinding;
  }

  /**
   * Test
   * {@link org.exoplatform.social.core.storage.api.GroupSpaceBindingStorage#findSpaceBindings(String, String)}
   *
   * @throws Exception
   **/

  public void testFindSpaceBindings() throws Exception {
    int totalBindings = 5;

    for (int i = 1; i <= totalBindings; i++) {
      GroupSpaceBinding groupSpaceBinding = this.getGroupSpaceBindingInstance(i,
                                                                              spaceId,
                                                                              "member",
                                                                              "/platform/administrators",
                                                                              "Any");
      groupSpaceBinding = groupSpaceBindingStorage.saveGroupBinding(groupSpaceBinding, true);
      tearDownGroupbindingList.add(groupSpaceBinding);
      StorageUtils.persist();
    }
    assertEquals("groupSpaceBindingStorage.findSpaceBindings(" + spaceId + ",'member') must return: " + totalBindings,
                 totalBindings,
                 groupSpaceBindingStorage.findSpaceBindings(spaceId, "member").size());
  }

  /**
   * Test
   * {@link org.exoplatform.social.core.storage.api.GroupSpaceBindingStorage#findUserSpaceBindings(String, String)}
   *
   * @throws Exception
   **/

  public void testFindUserBindings() throws Exception {
    int totalBindings = 5;

    GroupSpaceBinding groupSpaceBinding = this.getGroupSpaceBindingInstance(1,
                                                                            spaceId,
                                                                            "member",
                                                                            "/platform/administrators",
                                                                            "Any");
    groupSpaceBinding = groupSpaceBindingStorage.saveGroupBinding(groupSpaceBinding, true);
    StorageUtils.persist();
    tearDownGroupbindingList.add(groupSpaceBinding);
    for (int i = 1; i <= totalBindings; i++) {
      UserSpaceBinding userSpaceBinding = this.getUserBindingInstance(1, "john", spaceId, groupSpaceBinding);
      userSpaceBinding = groupSpaceBindingStorage.saveUserBinding(userSpaceBinding);
      StorageUtils.persist();
      tearDownUserbindingList.add(userSpaceBinding);
    }
    assertEquals("groupSpaceBindingStorage.findUserSpaceBindings(" + spaceId + ",'john') must return: " + totalBindings,
                 totalBindings,
                 groupSpaceBindingStorage.findUserSpaceBindings(spaceId, "john").size());
  }

  /**
   * Test
   * {@link org.exoplatform.social.core.storage.api.GroupSpaceBindingStorage#saveGroupBinding}
   *
   * @throws Exception
   **/

  public void testSaveGroupBinding() throws Exception {
    GroupSpaceBinding groupSpaceBinding = this.getGroupSpaceBindingInstance(1,
                                                                            spaceId,
                                                                            "member",
                                                                            "/platform/administrators",
                                                                            "Any");
    groupSpaceBinding = groupSpaceBindingStorage.saveGroupBinding(groupSpaceBinding, true);
    StorageUtils.persist();
    tearDownGroupbindingList.add(groupSpaceBinding);
    assertEquals("groupSpaceBindingStorage.findSpaceBindings(" + spaceId + ",'member') must return after creation: " + 1,
                 1,
                 groupSpaceBindingStorage.findSpaceBindings(spaceId, "member").size());
  }

  /**
   * Test
   * {@link org.exoplatform.social.core.storage.api.GroupSpaceBindingStorage#saveUserBinding(UserSpaceBinding)}
   *
   * @throws Exception
   **/

  public void testSaveUserBinding() throws Exception {
    GroupSpaceBinding groupSpaceBinding = this.getGroupSpaceBindingInstance(1,
                                                                            spaceId,
                                                                            "member",
                                                                            "/platform/administrators",
                                                                            "Any");
    groupSpaceBinding = groupSpaceBindingStorage.saveGroupBinding(groupSpaceBinding, true);
    StorageUtils.persist();
    tearDownGroupbindingList.add(groupSpaceBinding);
    UserSpaceBinding userSpaceBinding = this.getUserBindingInstance(1, "john", spaceId, groupSpaceBinding);
    userSpaceBinding = groupSpaceBindingStorage.saveUserBinding(userSpaceBinding);
    StorageUtils.persist();
    tearDownUserbindingList.add(userSpaceBinding);
    assertEquals("groupSpaceBindingStorage.findUserBindingsbyMember(" + spaceId + ",'member') must return after creation: " + 1,
                 1,
                 groupSpaceBindingStorage.findUserSpaceBindings(spaceId, "john").size());
    assertEquals("Invalid group binding :" + userSpaceBinding.getGroupBinding().getGroup(),
                 userSpaceBinding.getGroupBinding().getGroup(),
                 "/platform/administrators");
  }

  /**
   * Test
   * {@link org.exoplatform.social.core.storage.api.GroupSpaceBindingStorage#saveGroupBinding(GroupSpaceBinding, boolean)}
   *
   * @throws Exception
   **/

  public void testUpdateGroupBinding() throws Exception {
    GroupSpaceBinding groupSpaceBinding = this.getGroupSpaceBindingInstance(1,
                                                                            spaceId,
                                                                            "member",
                                                                            "/platform/administrators",
                                                                            "Any");
    groupSpaceBinding = groupSpaceBindingStorage.saveGroupBinding(groupSpaceBinding, true);
    StorageUtils.persist();
    tearDownGroupbindingList.add(groupSpaceBinding);
    groupSpaceBinding = this.getGroupSpaceBindingInstance(groupSpaceBinding.getId(), spaceId, "member", "/platform/users", "*");
    groupSpaceBinding = groupSpaceBindingStorage.saveGroupBinding(groupSpaceBinding, false);
    StorageUtils.persist();
    assertEquals("groupSpaceBindingStorage.findSpaceBindings('1','member') must return after update: " + 1,
                 1,
                 groupSpaceBindingStorage.findSpaceBindings(spaceId, "member").size());
    assertEquals("Updated binding group must be: " + 1, "/platform/users", groupSpaceBinding.getGroup());
    assertEquals("Updated binding group role must be: " + 1, "*", groupSpaceBinding.getGroupRole());
  }

  /**
   * Test
   * {@link org.exoplatform.social.core.storage.api.GroupSpaceBindingStorage#deleteGroupBinding(long)}
   *
   * @throws Exception
   **/

  public void testDeleteGroupBinding() throws Exception {
    GroupSpaceBinding groupSpaceBinding = this.getGroupSpaceBindingInstance(1,
                                                                            spaceId,
                                                                            "member",
                                                                            "/platform/administrators",
                                                                            "Any");
    groupSpaceBinding = groupSpaceBindingStorage.saveGroupBinding(groupSpaceBinding, true);
    StorageUtils.persist();
    groupSpaceBindingStorage.deleteGroupBinding(groupSpaceBinding.getId());
    StorageUtils.persist();
    assertEquals("groupSpaceBindingStorage.findSpaceBindings(" + groupSpaceBinding.getId() + ") must return after deletion: " + 0,
                 0,
                 groupSpaceBindingStorage.findSpaceBindings(spaceId, "member").size());
  }

  /**
   * Test
   * {@link org.exoplatform.social.core.storage.api.GroupSpaceBindingStorage#deleteUserBinding(long)}
   *
   * @throws Exception
   **/

  public void testDeleteUserBinding() throws Exception {
    GroupSpaceBinding groupSpaceBinding = this.getGroupSpaceBindingInstance(1,
                                                                            spaceId,
                                                                            "member",
                                                                            "/platform/administrators",
                                                                            "Any");
    groupSpaceBinding = groupSpaceBindingStorage.saveGroupBinding(groupSpaceBinding, true);
    StorageUtils.persist();
    tearDownGroupbindingList.add(groupSpaceBinding);
    UserSpaceBinding userSpaceBinding = this.getUserBindingInstance(1, "john", spaceId, groupSpaceBinding);
    userSpaceBinding = groupSpaceBindingStorage.saveUserBinding(userSpaceBinding);
    StorageUtils.persist();
    groupSpaceBindingStorage.deleteUserBinding(userSpaceBinding.getId());
    StorageUtils.persist();
    assertEquals("groupSpaceBindingStorage.findUserBindingsbyMember(" + spaceId + ",'john') must return after deletion: " + 0,
                 0,
                 groupSpaceBindingStorage.findUserSpaceBindings(spaceId, "john").size());
  }

  /**
   * Test
   * {@link org.exoplatform.social.core.storage.api.GroupSpaceBindingStorage#deleteAllUserBindings(String)}
   *
   * @throws Exception
   **/

  public void testDeleteAllUserBindings() throws Exception {
    GroupSpaceBinding groupSpaceBinding = this.getGroupSpaceBindingInstance(1,
                                                                            spaceId,
                                                                            "member",
                                                                            "/platform/administrators",
                                                                            "Any");
    groupSpaceBinding = groupSpaceBindingStorage.saveGroupBinding(groupSpaceBinding, true);
    StorageUtils.persist();
    tearDownGroupbindingList.add(groupSpaceBinding);
    UserSpaceBinding userSpaceBinding = this.getUserBindingInstance(1, "john", spaceId, groupSpaceBinding);
    userSpaceBinding = groupSpaceBindingStorage.saveUserBinding(userSpaceBinding);
    StorageUtils.persist();
    userSpaceBinding = this.getUserBindingInstance(2, "john", spaceId, groupSpaceBinding);
    userSpaceBinding = groupSpaceBindingStorage.saveUserBinding(userSpaceBinding);
    StorageUtils.persist();
    userSpaceBinding = this.getUserBindingInstance(3, "mary", spaceId, groupSpaceBinding);
    userSpaceBinding = groupSpaceBindingStorage.saveUserBinding(userSpaceBinding);
    tearDownUserbindingList.add(userSpaceBinding);
    StorageUtils.persist();
    groupSpaceBindingStorage.deleteAllUserBindings("john");
    StorageUtils.persist();
    assertEquals("groupSpaceBindingStorage.findUserBindingsbyMember(" + spaceId + ",'john') must return after deletion: " + 0,
                 0,
                 groupSpaceBindingStorage.findUserSpaceBindings(spaceId, "john").size());
    assertEquals("groupSpaceBindingStorage.findUserBindingsbyMember(" + spaceId + ",'mary') must return after deletion: " + 1,
                 1,
                 groupSpaceBindingStorage.findUserSpaceBindings(spaceId, "mary").size());
  }

  /**
   * Test
   * {@link org.exoplatform.social.core.storage.api.GroupSpaceBindingStorage#hasUserBindings(String, String)}
   *
   * @throws Exception
   **/

  public void testHasUserBindings() throws Exception {
    GroupSpaceBinding groupSpaceBinding = this.getGroupSpaceBindingInstance(1,
                                                                            spaceId,
                                                                            "member",
                                                                            "/platform/administrators",
                                                                            "Any");
    groupSpaceBinding = groupSpaceBindingStorage.saveGroupBinding(groupSpaceBinding, true);
    StorageUtils.persist();
    tearDownGroupbindingList.add(groupSpaceBinding);
    UserSpaceBinding userSpaceBinding = this.getUserBindingInstance(1, "john", spaceId, groupSpaceBinding);
    userSpaceBinding = groupSpaceBindingStorage.saveUserBinding(userSpaceBinding);
    StorageUtils.persist();
    tearDownUserbindingList.add(userSpaceBinding);
    StorageUtils.persist();
    assertEquals("groupSpaceBindingStorage.hasUserBindings(" + spaceId + ",'john') must return true ",
                 true,
                 groupSpaceBindingStorage.hasUserBindings(spaceId, "john"));
    assertEquals("groupSpaceBindingStorage.hasUserBindings(" + spaceId + ",'mary') must return false ",
                 false,
                 groupSpaceBindingStorage.hasUserBindings(spaceId, "mary"));
  }

    /**
     * Test
     * {@link org.exoplatform.social.core.storage.api.GroupSpaceBindingStorage#findUserAllBindingsbyGroupMembership(String, String)}
     *
     * @throws Exception
     **/

    public void testfindUserAllBindingsbyGroupMembership() throws Exception {
        int totalBindings = 5;

        GroupSpaceBinding groupSpaceBinding = this.getGroupSpaceBindingInstance(1,
                spaceId,
                "member",
                "/platform/administrators",
                "Any");
        groupSpaceBinding = groupSpaceBindingStorage.saveGroupBinding(groupSpaceBinding, true);
        StorageUtils.persist();
        tearDownGroupbindingList.add(groupSpaceBinding);

        GroupSpaceBinding groupSpaceBinding1 = this.getGroupSpaceBindingInstance(2,
                spaceId,
                "member",
                "/platform/users",
                "Any");
        groupSpaceBinding1 = groupSpaceBindingStorage.saveGroupBinding(groupSpaceBinding1, true);
        StorageUtils.persist();
        tearDownGroupbindingList.add(groupSpaceBinding1);

        for (int i = 1; i <= totalBindings; i++) {
            UserSpaceBinding userSpaceBinding = this.getUserBindingInstance(1, "john", spaceId, groupSpaceBinding);
            userSpaceBinding = groupSpaceBindingStorage.saveUserBinding(userSpaceBinding);
            StorageUtils.persist();
            tearDownUserbindingList.add(userSpaceBinding);
        }
        assertEquals("findUserAllBindingsbyGroupMembership('/platform/administrators','Any') must return: " + totalBindings,
                totalBindings,
                groupSpaceBindingStorage.findUserAllBindingsbyGroupMembership("/platform/administrators","Any").size());
        assertEquals("findUserAllBindingsbyGroupMembership('/platform/administrators','Any') must return: " + 0,
                0,
                groupSpaceBindingStorage.findUserAllBindingsbyGroupMembership("/platform/users","Any").size());
    }

    /**
     * Test
     * {@link org.exoplatform.social.core.storage.api.GroupSpaceBindingStorage#findUserSpaceBindingsByGroup(String, String, String)}
     *
     * @throws Exception
     **/

    public void testfindUserSpaceBindingsByGroup() throws Exception {
        int totalBindings = 5;

        GroupSpaceBinding groupSpaceBinding = this.getGroupSpaceBindingInstance(1,
                spaceId,
                "member",
                "/platform/administrators",
                "Any");
        groupSpaceBinding = groupSpaceBindingStorage.saveGroupBinding(groupSpaceBinding, true);
        StorageUtils.persist();
        tearDownGroupbindingList.add(groupSpaceBinding);

        GroupSpaceBinding groupSpaceBinding1 = this.getGroupSpaceBindingInstance(2,
                spaceId,
                "member",
                "/platform/users",
                "Any");
        groupSpaceBinding1 = groupSpaceBindingStorage.saveGroupBinding(groupSpaceBinding1, true);
        StorageUtils.persist();
        tearDownGroupbindingList.add(groupSpaceBinding1);

        for (int i = 1; i <= totalBindings; i++) {
            UserSpaceBinding userSpaceBinding = this.getUserBindingInstance(1, "john", spaceId, groupSpaceBinding);
            userSpaceBinding = groupSpaceBindingStorage.saveUserBinding(userSpaceBinding);
            StorageUtils.persist();
            tearDownUserbindingList.add(userSpaceBinding);
        }
        assertEquals("findUserAllBindingsbyGroupMembership('/platform/administrators','Any') must return: " + totalBindings,
                totalBindings,
                groupSpaceBindingStorage.findUserSpaceBindingsByGroup("/platform/administrators","Any","john").size());

        assertEquals("findUserAllBindingsbyGroupMembership('/platform/administrators','Any') must return: " + 0,
                0,
                groupSpaceBindingStorage.findUserSpaceBindingsByGroup("/platform/users","Any","john").size());
    }
}