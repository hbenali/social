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

package org.exoplatform.social.core.jpa.storage.dao;

import org.exoplatform.commons.api.persistence.GenericDAO;
import org.exoplatform.social.core.jpa.storage.entity.GroupSpaceBindingEntity;
import org.exoplatform.social.core.jpa.storage.entity.GroupSpaceBindingQueueEntity;

import java.util.List;

public interface GroupSpaceBindingQueueDAO extends GenericDAO<GroupSpaceBindingQueueEntity, Long> {

  /**
   * Gets first GroupSpaceBindingQueue in the queue.
   * 
   * @return
   */
  GroupSpaceBindingQueueEntity findFirstGroupSpaceBindingQueue();

  /**
   * Gets GroupSpaceBindings by action from the queue.
   * 
   * @param action
   * @return
   */
  List<GroupSpaceBindingEntity> getGroupSpaceBindingsFromQueueByAction(String action);
}
