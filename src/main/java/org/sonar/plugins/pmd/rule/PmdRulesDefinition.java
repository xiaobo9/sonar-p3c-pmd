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
package org.sonar.plugins.pmd.rule;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.plugins.pmd.PmdConstants;
import org.sonar.squidbridge.rules.SqaleXmlLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public final class PmdRulesDefinition implements RulesDefinition {
    private static final Logger LOGGER = Loggers.get(PmdRulesDefinition.class);

    public PmdRulesDefinition() {
        // do nothing
    }

    @Override
    public void define(Context context) {
        NewRepository repository = context
                .createRepository(PmdConstants.REPOSITORY_KEY, PmdConstants.JAVA)
                .setName(PmdConstants.REPOSITORY_NAME);

        extractRulesData(repository, "/org/sonar/plugins/pmd/rules-p3c.xml", "/org/sonar/l10n/pmd/rules/pmd-p3c");

        repository.done();
    }

    static void extractRulesData(NewRepository repository, String xmlRulesFilePath, String htmlDescriptionFolder) {
        try (InputStream inputStream = PmdRulesDefinition.class.getResourceAsStream(xmlRulesFilePath)) {
            new RulesDefinitionXmlLoader()
                    .load(
                            repository,
                            inputStream,
                            StandardCharsets.UTF_8
                    );
        } catch (IOException e) {
            LOGGER.error("Failed to load PMD RuleSet.", e);
        }

        ExternalDescriptionLoader.loadHtmlDescriptions(repository, htmlDescriptionFolder);
        loadNames(repository);
        SqaleXmlLoader.load(repository, "/com/sonar/sqale/pmd-model.xml");
    }

    private static void loadNames(NewRepository repository) {

        Properties properties = new Properties();

        try (InputStream stream = PmdRulesDefinition.class.getResourceAsStream("/org/sonar/l10n/pmd.properties")) {
            properties.load(stream);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read names from properties", e);
        }

        for (NewRule rule : repository.rules()) {
            String baseKey = "rule." + repository.key() + "." + rule.key();
            String nameKey = baseKey + ".name";
            String ruleName = properties.getProperty(nameKey);
            if (ruleName != null) {
                rule.setName(ruleName);
            }
            for (NewParam param : rule.params()) {
                String paramDescriptionKey = baseKey + ".param." + param.key();
                String paramDescription = properties.getProperty(paramDescriptionKey);
                if (paramDescription != null) {
                    param.setDescription(paramDescription);
                }
            }
        }
    }
}
