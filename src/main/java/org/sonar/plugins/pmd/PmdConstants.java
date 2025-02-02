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

public final class PmdConstants {
    public static final String PLUGIN_NAME = "PMD P3C";
    public static final String PLUGIN_KEY = "pmd-p3c";
    public static final String REPOSITORY_KEY = PLUGIN_KEY;
    public static final String REPOSITORY_NAME = "PMD P3C";
    public static final String XPATH_CLASS = "net.sourceforge.pmd.lang.rule.XPathRule";
    public static final String XPATH_EXPRESSION_PARAM = "xpath";
    public static final String XPATH_MESSAGE_PARAM = "message";

    public static final String JAVA = "java";

    /**
     * Key of the java version used for sources
     */
    public static final String JAVA_SOURCE_VERSION = "sonar.java.source";

    /**
     * Default value for property {@link #JAVA_SOURCE_VERSION}.
     */
    public static final String JAVA_SOURCE_VERSION_DEFAULT_VALUE = "1.6";

    private PmdConstants() {
    }
}
