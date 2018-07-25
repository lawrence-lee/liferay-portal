/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.project.templates;

import java.io.File;

import org.junit.Test;

/**
 * @author Lawrence Lee
 * @author Gregory Amerson
 * @author Andrea Di Giorgi
 */
public class FormFieldTest extends ProjectTemplatesTest {

	@Test
	public void testBuildTemplateFormField70() throws Exception {
		File gradleProjectDir = buildTemplateWithGradle(
			"form-field", "foobar", "--liferayVersion", "7.0");

		testContains(
			gradleProjectDir, "bnd.bnd", "Bundle-Name: foobar",
			"Web-ContextPath: /dynamic-data-foobar-form-field");
		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"");
		testContains(
			gradleProjectDir,
			"src/main/java/foobar/form/field/FoobarDDMFormFieldRenderer.java",
			"property = \"ddm.form.field.type.name=foobar\"",
			"public class FoobarDDMFormFieldRenderer extends " +
				"BaseDDMFormFieldRenderer {",
			"ddm.Foobar", "/META-INF/resources/foobar.soy");
		testContains(
			gradleProjectDir,
			"src/main/java/foobar/form/field/FoobarDDMFormFieldType.java",
			"ddm.form.field.type.js.class.name=Liferay.DDM.Field.Foobar",
			"ddm.form.field.type.js.module=foobar-form-field",
			"ddm.form.field.type.label=foobar-label",
			"ddm.form.field.type.name=foobar",
			"public class FoobarDDMFormFieldType extends BaseDDMFormFieldType",
			"return \"foobar\";");
		testContains(
			gradleProjectDir, "src/main/resources/META-INF/resources/config.js",
			"foobar-group", "'foobar-form-field': {",
			"path: 'foobar_field.js',", "'foobar-form-field-template': {");
		testContains(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/foobar.soy",
			"{namespace ddm}", "{template .Foobar autoescape",
			"<div class=\"form-group foobar-form-field\"");
		testContains(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/foobar_field.js",
			"'foobar-form-field',", "var FoobarField",
			"value: 'foobar-form-field'", "NAME: 'foobar-form-field'",
			"Liferay.namespace('DDM.Field').Foobar = FoobarField;");

		File mavenProjectDir = buildTemplateWithMaven(
			"form-field", "foobar", "com.test", "-DclassName=Foobar",
			"-Dpackage=foobar", "-DliferayVersion=7.0");

		buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateFormField71() throws Exception {
		File gradleProjectDir = buildTemplateWithGradle(
			"form-field", "foobar", "--liferayVersion", "7.1");

		testContains(
			gradleProjectDir, "bnd.bnd", "Bundle-Name: foobar",
			"Web-ContextPath: /dynamic-data-foobar-form-field");
		testContains(
			gradleProjectDir, "build.gradle",
			"apply plugin: \"com.liferay.plugin\"",
			"name: \"com.liferay.portal.kernel\", version: \"3.0.0");
		testContains(
			gradleProjectDir, "package.json",
			"\"name\": \"dynamic-data-foobar-form-field\"",
			",foobar_field.js &&");
		testContains(
			gradleProjectDir,
			"src/main/java/foobar/form/field/FoobarDDMFormFieldRenderer.java",
			"property = \"ddm.form.field.type.name=foobar\"",
			"public class FoobarDDMFormFieldRenderer extends " +
				"BaseDDMFormFieldRenderer {",
			"DDMFoobar.render", "/META-INF/resources/foobar.soy");
		testContains(
			gradleProjectDir,
			"src/main/java/foobar/form/field/FoobarDDMFormFieldType.java",
			"ddm.form.field.type.description=foobar-description",
			"ddm.form.field.type.js.class.name=Liferay.DDM.Field.Foobar",
			"ddm.form.field.type.js.module=foobar-form-field",
			"ddm.form.field.type.label=foobar-label",
			"ddm.form.field.type.name=foobar",
			"public class FoobarDDMFormFieldType extends BaseDDMFormFieldType",
			"return \"foobar\";");
		testContains(
			gradleProjectDir, "src/main/resources/META-INF/resources/config.js",
			"field-foobar", "'foobar-form-field': {",
			"path: 'foobar_field.js',");
		testContains(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/foobar.soy",
			"{namespace DDMFoobar}", "variant=\"'foobar'\"",
			"foobar-form-field");
		testContains(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/foobar.es.js",
			"import templates from './foobar.soy';", "* Foobar Component",
			"class Foobar extends Component", "Soy.register(Foobar,",
			"!window.DDMFoobar", "window.DDMFoobar",
			"window.DDMFoobar.render = Foobar;", "export default Foobar;");
		testContains(
			gradleProjectDir,
			"src/main/resources/META-INF/resources/foobar_field.js",
			"'foobar-form-field',", "var FoobarField",
			"value: 'foobar-form-field'", "NAME: 'foobar-form-field'",
			"Liferay.namespace('DDM.Field').Foobar = FoobarField;");

		File mavenProjectDir = buildTemplateWithMaven(
			"form-field", "foobar", "com.test", "-DclassName=Foobar",
			"-Dpackage=foobar", "-DliferayVersion=7.1");

		testContains(
			mavenProjectDir, "bnd.bnd", "-contract: JavaPortlet,JavaServlet");

		buildProjects(gradleProjectDir, mavenProjectDir);
	}

	@Test
	public void testBuildTemplateFormFieldInWorkspace() throws Exception {
		testBuildTemplateWithWorkspace(
			"form-field", "foobar", "build/libs/foobar-1.0.0.jar");
	}

	@Test
	public void testBuildTemplateFormFieldWithBOM() throws Exception {
		File gradleProjectDir = buildTemplateWithGradle(
			"form-field", "field-dependency-management",
			"--dependency-management-enabled");

		testNotContains(
			gradleProjectDir, "build.gradle",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"2.0.0\"");

		testContains(
			gradleProjectDir, "build.gradle", DEPENDENCY_PORTAL_KERNEL);
	}

	@Test
	public void testBuildTemplateFormFieldWithoutBOM() throws Exception {
		File gradleProjectDir = buildTemplateWithGradle(
			"form-field", "field-dependency-management");

		testContains(
			gradleProjectDir, "build.gradle",
			DEPENDENCY_PORTAL_KERNEL + ", version: \"2.0.0\"");
	}

}