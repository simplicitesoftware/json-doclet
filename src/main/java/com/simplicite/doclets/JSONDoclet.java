package com.simplicite.doclets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;

public class JSONDoclet
{
	private static final String DIR = "doc/java-json";
	private static final boolean inherit = true;
	private static final boolean debug = false;	

	private static void write(final String name, final JSONObject json) throws IOException
	{
		File file = new File(DIR + "/" + name + ".json");
		file.delete();
		if (debug) System.out.println(file.getAbsolutePath());
		PrintWriter out = new PrintWriter(file);
		out.println(json.toString(debug ? 2 : 0));
		out.close();
	}

	private static String comment(final String c)
	{
		return c==null ? "" : c.replaceAll("[\n\r]*", "").replaceAll("[\\s]*[<]br[ ]*\\/?[>]", "\n");
	}

	private static JSONArray constructors(final ClassDoc classdoc)
	{
		JSONArray constructors = new JSONArray();
		ConstructorDoc[] constructordocs = classdoc.constructors();
		for (int j = 0; j < constructordocs.length; j++)
		{
			boolean deprecated = false;
			ConstructorDoc constructordoc = constructordocs[j];
			AnnotationDesc[] annotations = constructordoc.annotations();
			for (int k = 0; k < annotations.length; k++)
				if ("Deprecated".equals(annotations[k].annotationType().name())) deprecated = true;
			if (deprecated) continue;
			if (constructordoc.isPublic())
			{
				JSONArray params = new JSONArray();
				Parameter[] paramdocs = constructordoc.parameters();
				Tag[] tags = constructordoc.tags();
				HashMap<String, String> ts = new HashMap<>();
				for (int k = 0; k < tags.length; k++)
				{
					Tag tag = tags[k];
					if ("@deprecated".equals(tag.name())) deprecated = true;
					else if ("@param".equals(tag.name()))
					{
						String[] t = tag.text().split("\\s+", 2);
						if (t.length == 2) ts.put(t[0], comment(t[1]));
					}
				}
				if (deprecated) continue;
				StringBuilder p = new StringBuilder();
				StringBuilder t = new StringBuilder();
				for (int k = 0; k < paramdocs.length; k++)
				{
					Parameter paramdoc = paramdocs[k];
					Type type = paramdoc.type();
					p.append((p.length()==0 ? "" : ", ") + paramdoc.name());
					t.append((t.length()==0 ? "" : ", ") + type.qualifiedTypeName() + (type.dimension() != null ? type.dimension() : ""));
					params.put(new JSONObject()
							.put("name", paramdoc.name())
							.put("comment", comment(ts.get(paramdoc.name())))
							.put("type", type.qualifiedTypeName() + (type.dimension() != null ? type.dimension() : "")));
				}
				constructors.put(new JSONObject()
						.put("signature", classdoc.name() + "(" + t.toString() + ")")
						.put("helper", classdoc.name() + "(" + p.toString() + ")")
						.put("comment", comment(constructordoc.commentText()))
						.put("parameters", params.length()>0 ? params : null));
			}
		}
		return constructors;
	}

	private static JSONArray fields(final ClassDoc classdoc)
	{
		JSONArray fields = new JSONArray();
		FieldDoc[] fielddocs = classdoc.fields();
		for (int j = 0; j < fielddocs.length; j++)
		{
			FieldDoc fielddoc = fielddocs[j];
			boolean deprecated = false;
			AnnotationDesc[] annotations = fielddoc.annotations();
			for (int k = 0; k < annotations.length; k++)
				if ("Deprecated".equals(annotations[k].annotationType().name())) deprecated = true;
			if (deprecated) continue;
			if (fielddoc.isPublic())
			{
				Tag[] tags = fielddoc.tags();
				for (int k = 0; k < tags.length; k++)
					if ("@deprecated".equals(tags[k].name())) deprecated = true;
				if (deprecated) continue;
				Type type = fielddoc.type();
				JSONObject f = new JSONObject()
					.put("name", fielddoc.name())
					.put("comment", comment(fielddoc.commentText()))
					.put("type", type.qualifiedTypeName() + (type.dimension() != null ? type.dimension() : ""));
				if (fielddoc.isStatic()) f.put("static", true);
				fields.put(f);
			}
		}
		return fields;
	}

	private static JSONArray methods(final ClassDoc classdoc)
	{
		JSONArray methods = new JSONArray();
		MethodDoc[] methoddocs = classdoc.methods();
		for (int j = 0; j < methoddocs.length; j++)
		{
			MethodDoc methoddoc = methoddocs[j];
			boolean deprecated = false;
			AnnotationDesc[] annotations = methoddoc.annotations();
			for (int k = 0; k < annotations.length; k++)
				if ("Deprecated".equals(annotations[k].annotationType().name())) deprecated = true;
			if (deprecated) continue;
			if (methoddoc.isPublic())
			{
				JSONArray params = new JSONArray();
				Parameter[] paramdocs = methoddoc.parameters();
				Tag[] tags = methoddoc.tags();
				HashMap<String, String> ts = new HashMap<>();
				String retComment = null;
				for (int k = 0; k < tags.length; k++)
				{
					Tag tag = tags[k];
					if ("@deprecated".equals(tag.name())) deprecated = true;
					else if ("@return".equals(tag.name())) retComment = comment(tag.text());
					else if ("@param".equals(tag.name()))
					{
						String[] t = tag.text().split("\\s+", 2);
						if (t.length == 2) ts.put(t[0], comment(t[1]));
					}
				}
				if (deprecated) continue;
				StringBuilder p = new StringBuilder();
				StringBuilder t = new StringBuilder();
				for (int k = 0; k < paramdocs.length; k++)
				{
					Parameter paramdoc = paramdocs[k];
					Type type = paramdoc.type();
					p.append((p.length()==0 ? "" : ", ") + paramdoc.name());
					t.append((t.length()==0 ? "" : ", ") + type.qualifiedTypeName() + (type.dimension() != null ? type.dimension() : ""));
					params.put(new JSONObject()
							.put("name", paramdoc.name())
							.put("comment", comment(ts.get(paramdoc.name())))
							.put("type", type.qualifiedTypeName() + (type.dimension() != null ? type.dimension() : "")));
				}
				Type retType = methoddoc.returnType();
				String ret = retType.qualifiedTypeName() + (retType.dimension() != null ? retType.dimension() : "");
				JSONObject m = new JSONObject()
					.put("name", methoddoc.name())
					.put("signature", ret + " " + methoddoc.name() + "(" + t.toString() + ")")
					.put("helper", methoddoc.name() + "(" + p.toString() + ")")
					.put("comment", comment(methoddoc.commentText()))
					.put("return", ret)
					.put("returncomment", retComment);
				if (methoddoc.isStatic()) m.put("static", true);
				if (params.length()>0) m.put("parameters", params);
				methods.put(m);
			}
		}
		return methods;
	}

	private static HashMap<String, JSONObject> classes(final String pkg, final ClassDoc[] classdocs) throws IOException
	{
		HashMap<String, JSONObject> classes = new HashMap<>();
		for (int i = 0; i < classdocs.length; i++)
		{
			ClassDoc classdoc = classdocs[i];
			if (!classdoc.isPublic()) continue;

			JSONObject cls = new JSONObject()
				.put("package", pkg)
				.put("name", classdoc.qualifiedName())
				.put("simplename", classdoc.name())
				.put("parent", classdoc.superclass() != null ? classdoc.superclass().qualifiedName() : null)
				.put("comment", comment(classdoc.commentText()));
			JSONArray constructors = constructors(classdoc);
			if (constructors.length() > 0)
				cls.put("constructors", constructors(classdoc));
			JSONArray fields = fields(classdoc);
			if (fields.length() > 0)
				cls.put("fields", fields(classdoc));
			JSONArray methods = methods(classdoc);
			if (methods.length() > 0)
				cls.put("methods", methods(classdoc));

			classes.put(classdoc.qualifiedName(), cls);
		}
		return classes;
	}

	private static JSONArray mergearray(final JSONArray c, final JSONArray p, final String name)
	{
		if (c == null) return p;
		if (p == null) return c;
		JSONArray m = new JSONArray();
		for (int i = 0; i < c.length(); i++)
			m.put(c.getJSONObject(i));
		for (int i = 0; i < p.length(); i++)
		{
			JSONObject pp = new JSONObject(p.getJSONObject(i).toString());
			pp.put("inherited", true);
			if (name!=null)
			{
				pp.put("helper", pp.getString("helper").replaceFirst(".*\\(", name + "("));
				pp.put("signature", pp.getString("signature").replaceFirst(".*\\(", name + "("));
			}
			boolean exists = false;
			if (pp.has("signature"))
			{
				for (int j = 0; j < c.length(); j++)
				{
					JSONObject cc = c.getJSONObject(j);
					if (cc.getString("signature").equals(pp.getString("signature")))
					{
						exists = true;
						break;
					}
				}
			}
			if (exists) continue;
			m.put(pp);
		}
		return m;
	}

	/**
	 * Recursively add parent items to child
	 * @param child Child
	 * @param classes All classes
	 */
	private static JSONObject merge(final JSONObject child, final String p, final HashMap<String, JSONObject> classes)
	{
		if (p == null) return child;
		JSONObject parent = classes.get(p);
		if (parent == null) return child;
		if (debug) System.out.println("- Adding " + p + " to " + child.getString("name"));
		child.put("constructors", mergearray(child.optJSONArray("constructors"), parent.optJSONArray("constructors"), child.getString("simplename")));
		child.put("fields", mergearray(child.optJSONArray("fields"), parent.optJSONArray("fields"), null));
		child.put("methods", mergearray(child.optJSONArray("methods"), parent.optJSONArray("methods"), null));
		return merge(child, parent.optString("parent"), classes);
	}

	public static boolean start(RootDoc root)
	{
		try
		{
			new File(DIR).mkdirs();

			JSONArray packages = new JSONArray();
			PackageDoc[] packagedocs = root.specifiedPackages();
			for (int i = 0; i < packagedocs.length; i++)
			{
				PackageDoc packagedoc = packagedocs[i];

				ClassDoc[] classdocs = packagedoc.allClasses();
				JSONArray cs = new JSONArray();
				for (int j = 0; j < classdocs.length; j++) cs.put(classdocs[j]);
				write("package-" + packagedoc.name(), new JSONObject().put("package", packagedoc.name()).put("classes", cs));

				HashMap<String, JSONObject> classes = classes(packagedoc.name(), classdocs);
				if (inherit)
				{
					HashMap<String, JSONObject> mergedclasses = new HashMap<>();
					for (String name : classes.keySet())
					{
						JSONObject child = classes.get(name);
						if (debug) System.out.println("Merging " + name);
						mergedclasses.put(name, merge(child, child.optString("parent"), classes));
					}
					for (String name : mergedclasses.keySet()) write(name, mergedclasses.get(name));
				}
				else
					for (String name : classes.keySet()) write(name, classes.get(name));

				packages.put(packagedoc.name());
			}
			write("packages", new JSONObject().put("packages", packages));

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
}
