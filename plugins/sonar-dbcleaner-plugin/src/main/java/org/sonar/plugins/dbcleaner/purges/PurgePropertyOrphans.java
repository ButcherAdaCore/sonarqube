/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2008-2011 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.dbcleaner.purges;

import org.sonar.api.database.DatabaseSession;
import org.sonar.api.database.configuration.Property;
import org.sonar.api.database.model.ResourceModel;
import org.sonar.api.database.model.User;
import org.sonar.plugins.dbcleaner.api.Purge;
import org.sonar.plugins.dbcleaner.api.PurgeContext;
import org.sonar.plugins.dbcleaner.api.PurgeUtils;

import java.util.List;

import javax.persistence.Query;

/**
 * @since 2.2
 */
public final class PurgePropertyOrphans extends Purge {

  public PurgePropertyOrphans(DatabaseSession session) {
    super(session);
  }

  public void purge(PurgeContext context) {
    purgeResourceOrphans();
    purgeUserOrphans();
  }

  void purgeResourceOrphans() {
    Query query = getSession().createQuery("SELECT p.id FROM " + Property.class.getSimpleName() +
        " p WHERE p.resourceId IS NOT NULL AND NOT EXISTS(FROM " + ResourceModel.class.getSimpleName() + " r WHERE r.id=p.resourceId)");
    List<Integer> idsToDelete = query.getResultList();
    if (!idsToDelete.isEmpty()) {
      PurgeUtils.executeQuery(getSession(), "", idsToDelete, "DELETE FROM " + Property.class.getSimpleName() + " WHERE id in (:ids)");
    }
  }

  void purgeUserOrphans() {
    Query query = getSession().createQuery("SELECT p.id FROM " + Property.class.getSimpleName() +
        " p WHERE p.userId IS NOT NULL AND NOT EXISTS(FROM " + User.class.getSimpleName() + " u WHERE u.id=p.userId)");
    List<Integer> idsToDelete = query.getResultList();
    if (!idsToDelete.isEmpty()) {
      PurgeUtils.executeQuery(getSession(), "", idsToDelete, "DELETE FROM " + Property.class.getSimpleName() + " WHERE id in (:ids)");
    }
  }
}
