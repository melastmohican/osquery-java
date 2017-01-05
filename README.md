# osquery-java
## Overview
This project contains the Java bindings for creating osquery extensions in Java. The extension can register table, config or logger plugins.
Plugin can quickly enable the integration of data which is not yet available as a part of base osquery. 

## Prerequisites
Osquery must be installed on the computer you are running this sogtware. Osquery should be run as the same user the user which runs the code shown here.

## How to
**Consider the following example:**
```java
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.melastmohican.osquery.BasePlugin;
import net.melastmohican.osquery.PluginManager;
import net.melastmohican.osquery.TablePlugin;
import net.melastmohican.osquery.TablePlugin.TableColumn;
import net.melastmohican.osquery.TablePluginTest.SimpleTablePlugin;

public class MyTablePlugin extends TablePlugin {

	@Override
	public List<TableColumn> columns() {
		return Arrays.asList(new TableColumn("foo", "TEXT"), new TableColumn("baz", "TEXT"));
	}

	@Override
	public String name() {
		return "mytable";
	}

	@Override
	public List<Map<String, String>> generate() {
		List<Map<String, String>> result = Arrays.<Map<String, String>>asList(new HashMap<String, String>() {
			{
				put("foo", "bar");
				put("baz", "baz");
			}
		}, new HashMap<String, String>() {
			{
				put("foo", "bar");
				put("baz", "baz");
			}
		});
		return result;
	}

	public static void main(String[] args) {
		BasePlugin plugin = new MyTablePlugin();
		PluginManager pm = PluginManager.getInstance();
		pm.addPlugin(plugin);
		pm.startExtension("MyTablePlugin","0.0.1","2.2.1","2.2.1");
	}

}
```
**To test this code start an osquery shell:**
```
osqueryi --nodisable_extensions
osquery> select value from osquery_flags where name = 'extensions_socket';
```
|value
|---
|/Users/USERNAME/.osquery/shell.em

**Then start the Java extension:**

javac MyTablePlugin.java
java -Dextension.socket=/Users/USERNAME/.osquery/shell.em MyTablePlugin

This will register a table called "mytable". As you can see, the table will
return two rows:
```
osquery> select * from mytable;
```
| foo | baz |
|---|---|
| bar | baz |
| bar | baz |
```
osquery>
```
## Execute queries in Java
The same Thrift bindings can be used to create a Java client for the osqueryd or
osqueryi's extension socket. 
```java
ClientManager cm =  new ClientManager();
cm.open();	
ExtensionManager.Client client = cm.getClient();
System.out.println("select timestamp from time");
ExtensionResponse res = client.query("select timestamp from time");
System.out.println(res.response);
```
