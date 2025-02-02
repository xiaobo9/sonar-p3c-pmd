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

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.InputSource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OutHtml {


    public static void main(String[] args) throws DocumentException, IOException {

        String parent = "D:/git/sonar/p3c/p3c-pmd/src/main/resources";

        String messageFile = "messages.xml";


        Map<String, String> map = new HashMap<>();
        File file1 = new File(parent, messageFile);
        Element rootEl = getElement(file1);
        List<Element> entries = rootEl.elements("entry");
        for (Element entry : entries) {
            map.put(attributeValue(entry, "key"), entry.getText());
        }

        File dir = Paths.get(parent, "rulesets", "java").toFile();

        String[] scopes = new String[]{"comment", "constant", "concurrent", "exception", "flowcontrol", "naming", "oop", "orm", "other",
                "set"};
        List<Rule> rules = new LinkedList<>();
        for (String scope : scopes) {
            String fileName = "ali-" + scope + ".xml";
            File file = new File(dir, fileName);
            rootEl = getElement(file);
            List<Element> ruleEls = rootEl.elements("rule");
            for (Element ruleEl : ruleEls) {
                Rule rule = Rule.getRule(scope, ruleEl, map);
                rule.setFile("rulesets/java/" + fileName + "/" + rule.getName());
                rules.add(rule);
            }
        }

        for (Rule rule : rules) {
            System.out.println(rule);

            String dir1 = "D:\\git\\sonar\\sonar-p3c-pmd\\src\\main\\resources\\org\\sonar\\l10n\\pmd\\rules\\pmd-p3c";

            rule.generateHtml(dir1);
        }


        pmdModelXml(rules);

        p3cRulesXml(rules);
    }

    private static void p3cRulesXml(List<Rule> rules) throws IOException {
        Map<String, String> map = new HashMap<>();

        map.put("1", "CRITICAL");
        map.put("2", "MAJOR");
        map.put("3", "MINOR");
        Document document = DocumentHelper.createDocument();
        Element rootEl = document.addElement("rules");

        for (Rule rule : rules) {
            Element ruleEl = rootEl.addElement("rule");
            ruleEl.addAttribute("key", rule.getName());
            ruleEl.addElement("priority").setText(map.get(rule.getPriority()));
            ruleEl.addElement("configKey").addCDATA(rule.getFile());
        }

        String parent = "D:\\git\\sonar\\sonar-p3c-pmd\\src\\main\\resources";

        OutputFormat format = OutputFormat.createPrettyPrint();// 创建文件输出的时候，自动缩进的格式
        format.setEncoding("UTF-8");//设置编码
        XMLWriter writer = new XMLWriter(new FileWriter(parent + "\\org\\sonar\\plugins\\pmd\\rules-p3c.xml"), format);
        writer.write(document);
        writer.close();
    }

    private static void pmdModelXml(List<Rule> rules) throws IOException {
        Document document = DocumentHelper.createDocument();
        Element p3c = document.addElement("sqale").addElement("chc");
        p3c.addElement("key").setText("P3C-PMD");
        p3c.addElement("name").setText("p3c-pmd");

        Element p3cchc = p3c.addElement("chc");

        p3cchc.addElement("key").setText("P3C-PMD");
        p3cchc.addElement("name").setText("p3c-pmd");

        for (Rule rule : rules) {
            Element el = p3cchc.addElement("chc");
            el.addElement("rule-repo").setText("pmd");
            el.addElement("rule-key").setText(rule.getName());
            Element prop = el.addElement("prop");
            prop.addElement("key").setText("remediationFunction");
            prop.addElement("txt").setText("CONSTANT_ISSUE");

            Element prop2 = el.addElement("prop");
            prop2.addElement("key").setText("offset");
            prop2.addElement("val").setText("2");
            prop2.addElement("txt").setText("min");
        }

        OutputFormat format = OutputFormat.createPrettyPrint();// 创建文件输出的时候，自动缩进的格式
        format.setEncoding("UTF-8");//设置编码
        XMLWriter writer = new XMLWriter(new FileWriter("D:\\git\\sonar\\sonar-p3c-pmd\\src\\main\\resources\\com\\sonar\\sqale\\pmd" +
                "-model" +
                ".xml"), format);
        writer.write(document);
        writer.close();
    }


    private static String attributeValue(Element ruleEl, String name) {
        Attribute attribute = ruleEl.attribute(name);
        return attribute == null ? null : attribute.getValue();
    }

    private static Element getElement(File file) throws DocumentException {
        SAXReader reader = new SAXReader();
        reader.setEntityResolver((publicId, systemId) -> new InputSource(
                        new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes())
                )
        );
        Document document = reader.read(file);
        return document.getRootElement();
    }

    public static class Rule {
        private String name;


        private String scope;

        private String language;

        private String message;

        private String description;

        private String file;

        private String priority;

        private String example;

        Rule() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getExample() {
            return example;
        }

        public void setExample(String example) {
            this.example = example;
        }

        @Override
        public String toString() {
            return "Rule{" +
                    "name='" + name + '\'' +
                    ", scope='" + scope + '\'' +
                    ", language='" + language + '\'' +
                    ", message='" + message + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }

        static Rule getRule(String scope, Element ruleEl, Map<String, String> map) {
            Rule rule = new Rule();

            rule.setScope(scope);
            rule.setLanguage("java");
            rule.setName(attributeValue(ruleEl, "name"));
            rule.setMessage(map.get(attributeValue(ruleEl, "message")));
            Element description = ruleEl.element("description");
            if (description != null) {
                rule.setDescription(map.get(description.getText().trim()));
            }
            rule.setPriority(ruleEl.element("priority").getText().trim());
            Element example = ruleEl.element("example");
            if (example != null) {
                rule.setExample(example.getText());
            }
            return rule;
        }

        private void generateHtml(String dir) throws IOException {
            StringBuilder builder = new StringBuilder();

            builder.append(String.format("<p>%s</p>", message.trim()));

            if (StringUtils.isNotEmpty(description)) {
                builder.append("\n<pre>\n");
                builder.append(description);
                builder.append("\n</pre>");
            }

            builder.append("\n").append("<p>Examples:</p>");
            if (StringUtils.isNotEmpty(example)) {
                builder.append("\n<pre>\n");
                builder.append(example);
                builder.append("\n</pre>");
            }

            try (FileOutputStream output = new FileOutputStream(dir + "\\" + name + ".html")) {
                IOUtils.write(builder.toString(), output, StandardCharsets.UTF_8);
            }
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }
    }
}
