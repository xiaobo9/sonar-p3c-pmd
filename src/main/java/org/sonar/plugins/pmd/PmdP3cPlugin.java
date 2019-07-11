/*
 * SonarQube PMD Plugin
 * Copyright (C) 2012 SonarSource
 * sonarqube@googlegroups.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.pmd;

import org.sonar.api.SonarPlugin;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.plugins.pmd.profile.PmdProfileExporter;
import org.sonar.plugins.pmd.profile.PmdProfileImporter;
import org.sonar.plugins.pmd.rule.PmdRulesDefinition;

import java.util.LinkedList;
import java.util.List;

public class PmdP3cPlugin extends SonarPlugin {

    @Override
    public List getExtensions() {
        PropertyDefinition build = PropertyDefinition.builder(PmdConfiguration.PROPERTY_GENERATE_XML)
                .defaultValue("false")
                .name("Generate XML Report")
                .hidden()
                .build();

        List list = new LinkedList();
        list.add(build);
        list.add(PmdSensor.class);
        list.add(PmdConfiguration.class);
        list.add(PmdExecutor.class);
        list.add(PmdRulesDefinition.class);
        list.add(PmdProfileExporter.class);
        list.add(PmdProfileImporter.class);
        list.add(PmdViolationRecorder.class);

        return list;
    }

}
