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

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;
import net.sourceforge.pmd.*;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class PmdTemplateTest {

    File inputFile = new File(Resources.getResource(PmdTemplateTest.class, "source.txt").getFile());
    RuleSets rulesets = mock(RuleSets.class);
    RuleContext ruleContext = mock(RuleContext.class);
    InputStream inputStream = mock(InputStream.class);
    PMDConfiguration configuration = mock(PMDConfiguration.class);
    SourceCodeProcessor processor = mock(SourceCodeProcessor.class);

    @Test
    public void should_process_input_file() throws Exception {
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                InputStream inputStreamArg = (InputStream) invocation.getArguments()[0];
                List<String> inputStreamLines = CharStreams.readLines(new InputStreamReader(inputStreamArg));
                assertThat(inputStreamLines).containsExactly("Example source");
                return null;
            }
        }).when(processor).processSourceCode(any(InputStream.class), eq(rulesets), eq(ruleContext));

        new PmdTemplate(configuration, processor).process(inputFile, rulesets, ruleContext);

        verify(ruleContext).setSourceCodeFilename(inputFile.getAbsolutePath());
        verify(processor).processSourceCode(any(InputStream.class), eq(rulesets), eq(ruleContext));
    }

    @Test
    public void should_ignore_PMD_error() throws PMDException, FileNotFoundException {
        doThrow(new PMDException("BUG"))
                .when(processor).processSourceCode(any(InputStream.class), any(RuleSets.class), any(RuleContext.class));

        new PmdTemplate(configuration, processor).process(inputFile, rulesets, ruleContext);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_fail_on_invalid_java_version() {
        PmdTemplate.create("12.2", mock(ClassLoader.class), Charsets.UTF_8);
    }

    @Test
    public void shouldnt_fail_on_valid_java_version() {
        PmdTemplate.create("6", mock(ClassLoader.class), Charsets.UTF_8);
    }

    /**
     * SONARPLUGINS-3318
     */
    @Test
    public void should_set_classloader() {
        ClassLoader classloader = mock(ClassLoader.class);
        PmdTemplate pmdTemplate = PmdTemplate.create("6", classloader, Charsets.UTF_8);
        assertThat(pmdTemplate.configuration().getClassLoader()).isEqualTo(classloader);
    }

    @Test
    public void should_set_encoding() {
        PmdTemplate pmdTemplate = PmdTemplate.create("6", mock(ClassLoader.class), Charsets.UTF_16BE);
        assertThat(pmdTemplate.configuration().getSourceEncoding()).isEqualTo("UTF-16BE");
    }

}
